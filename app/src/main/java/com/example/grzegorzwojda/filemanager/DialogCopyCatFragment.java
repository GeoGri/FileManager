package com.example.grzegorzwojda.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-02-15.
 */

public class DialogCopyCatFragment extends DialogFragment {

    private SeekBar seekBarFile;
    private SeekBar seekBarFiles;

    private TextView from;
    private TextView to;
    private TextView seekBarFileProgress;
    private TextView seekBarFilesProgress;

    private String dst;
    private String order;
    private String FRAGMENT_TAG;

    LayoutInflater inflater;
    private MyTask task;
    private List<File> fileList;
    FileHelper fileHelper = new FileHelper();
    FragmentHelper fragmentHelper = new FragmentHelper();

    static DialogCopyCatFragment newInstance(List<File> fileList, String dst, String FRAGMENT_TAG,
                                             String order) {
        DialogCopyCatFragment dialogCopyCatFragment = new DialogCopyCatFragment();

        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) fileList);
        args.putString("dst", dst);
        args.putString("order", order);
        args.putString("tag", FRAGMENT_TAG);

        dialogCopyCatFragment.setArguments(args);
        return dialogCopyCatFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileList = (List<File>) getArguments().getSerializable("list");
        dst = getArguments().getString("dst");
        order = getArguments().getString("order");
        FRAGMENT_TAG = getArguments().getString("tag");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_copy_cut_fragment, null);
        builder.setView(view);
        initializeViews(view);

        task = new MyTask(inflater.getContext(), FRAGMENT_TAG);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.onCancelled(getString(R.string.cancel));
            }
        }).setCustomTitle(setTitle(inflater.getContext()));
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        task.execute();
    }

    private TextView setTitle(Context context) {
        TextView title = new TextView(context);

        if (order.equals("cut")) title.setText(getString(R.string.cutting));
        else title.setText(getString(R.string.copying));
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        return title;
    }

    private void initializeViews(View view) {
        seekBarFile = (SeekBar) view.findViewById(R.id.seekBarfile);
        seekBarFiles = (SeekBar) view.findViewById(R.id.seekBarFiles);

        from = (TextView) view.findViewById(R.id.from);
        to = (TextView) view.findViewById(R.id.to);
        seekBarFileProgress = (TextView) view.findViewById(R.id.seekBarfileProgres);
        seekBarFilesProgress = (TextView) view.findViewById(R.id.seekBarfilesProgres);
    }

    class MyTask extends AsyncTask<Void, Integer, String> {

        private InputStream in;
        private OutputStream out;
        final Activity activity;

        private int fileLength;
        private int maxLength = 0;
        private long maxProgress = 0;

        private String F_TAG;
        private boolean emptyFile;
        private boolean ROOT = false;

        private ProgressDialog progressDialog;


        MyTask(Context context, String F_TAG) {
            this.F_TAG = F_TAG;

            this.activity = (Activity) context;

            for (int i = 0; i < fileList.size(); i++)
                maxLength += (int) fileHelper.getFolderSize(fileList.get(i));
            seekBarFiles.setMax(maxLength);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // check if need flag with empty_Frag
            emptyFile = fileHelper.checkListFileIsNul(F_TAG);

            from.setText(fileList.get(0).getParentFile().getAbsolutePath());
            to.setText(dst);

            // sort -> files first, second folders
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    int result = 0;
                    if (!lhs.isDirectory())
                        result -= 1;
                    if (!rhs.isDirectory())
                        result += 1;

                    if (result == 0)
                        result = lhs.getAbsolutePath().compareTo(rhs.getAbsolutePath());
                    return result;
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // copy file
                for (int i = 0; i < fileList.size(); i++)
                    copyDirectory(fileList.get(i),
                            new File(FRAGMENT_TAG, fileList.get(i).getName()));
                // delete if need cut
                deleteFileIfNeed();
            } catch (IOException e) {
                Log.d(" IOException -->", e.getMessage());
                ROOT = true;
                publishProgress(0);
                copyWithRoot();
                deleteFileIfNeed();
                publishProgress(1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // fill fields
            if (!ROOT) {
                Log.d(" onProgressUpdate -->", String.valueOf(values[0]));

                seekBarFile.setProgress(values[0]);
                seekBarFiles.setProgress((int) maxProgress);

                long val = values[0].longValue();
                long val100 = val * 100;

                long fileProgress;
                if (fileLength == 0) fileProgress = 0;
                else fileProgress = val100 / fileLength;
                if (fileProgress > 100) fileProgress = 100;
                seekBarFileProgress.setText(fileProgress + " %");

                long maxVal100 = maxProgress * 100;
                long filesProgress = maxVal100 / maxLength;
                if (filesProgress > 100) filesProgress = 100;
                seekBarFilesProgress.setText(filesProgress + " %");
            } else if (ROOT && values[0] == 0) {
                progressDialog = ProgressDialog.
                        show(inflater.getContext(), "Moving", "Wait...", true);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (ROOT)
                progressDialog.dismiss();
            dismiss();
            if (emptyFile)
                fragmentHelper.refreshFragment(activity.getFragmentManager(), F_TAG, true, false);
            else fragmentHelper.refreshFragment(activity.getFragmentManager(), F_TAG, false, false);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            try {
                in.close();
                out.close();

                // if exist delete
                for (int i = 0; i < fileList.size(); i++)
                    if (new File(FRAGMENT_TAG, fileList.get(i).getName()).exists())
                        fileHelper.deleteRecursive(fileList.get(i));

                // reopen Fragment Folder_list or Folder_Empty
                if (emptyFile)
                    fragmentHelper.refreshFragment(activity.getFragmentManager(), F_TAG, true, false);
                else
                    fragmentHelper.refreshFragment(activity.getFragmentManager(), F_TAG, false, false);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(" IOException -->", e.getMessage());
            }
        }

        public void deleteFileIfNeed() {
            if (order.equals("cut"))
                for (int i = 0; i < fileList.size(); i++)
                    fileHelper.deleteRecursive(fileList.get(i));
        }

        public void copyWithRoot() {
            for (int i = 0; i < fileList.size(); i++)
                copyWithRoot(fileList.get(i),
                        new File(FRAGMENT_TAG, fileList.get(i).getName()));
        }


        private int copyDirectory(File sourceLocation, File targetLocation)
                throws IOException {

            // open every file and copy
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                    return 1;
                }

                for (String aChildren : sourceLocation.list()) {
                    copyDirectory(new File(sourceLocation, aChildren),
                            new File(targetLocation, aChildren));
                }
            } else {

                // make sure the directory we plan to store the recording in exists
                File directory = targetLocation.getParentFile();
                if (directory != null && !directory.exists() && !directory.mkdirs()) {
                    return 1;
                }

                fileLength = (int) fileHelper.getFolderSize(sourceLocation);
                seekBarFile.setProgress(0);
                seekBarFile.setMax(fileLength);


                in = new FileInputStream(sourceLocation);
                out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                int progress = 0;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                    progress += len;
                    maxProgress += len;
                    publishProgress(progress);
                }
                in.close();
                out.close();
                return 1;
            }
            return 1;
        }

        private void copyWithRoot(File sourceLocation, File targetLocation) {
            dismiss();
            try {
                String command = "cp -r " + "\"" + sourceLocation.getAbsolutePath() + "\" \"" +
                        targetLocation.getAbsolutePath() + "\"";
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                dos.writeBytes(command + "\n");
                dos.writeBytes("exit\n");
                dos.flush();
                dos.close();
                p.waitFor();

            } catch (Exception ex) {
                Log.i("ROOT", "Could not execute", ex);
                onCancelled(getString(R.string.cancel));
            }
        }

    }
}
