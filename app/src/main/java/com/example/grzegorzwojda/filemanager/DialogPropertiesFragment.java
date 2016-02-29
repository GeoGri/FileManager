package com.example.grzegorzwojda.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

/**
 * Created by Grzegorz Wojda on 2016-02-11 21:36.
 */
public class DialogPropertiesFragment extends DialogFragment {

    private File file;

    int folders = -1;   // -1 because it count folder from is counting
    int files = 0;

    TextView name;
    TextView localization;
    TextView permissions;
    TextView modification_time;
    TextView size;
    TextView type_or_elementsTitle;
    TextView type_or_elements;

    MyTask task;
    FileHelper fileHelper = new FileHelper();

    static DialogPropertiesFragment newInstance(String path) {
        DialogPropertiesFragment dialogPropertiesFragment = new DialogPropertiesFragment();

        Bundle args = new Bundle();
        args.putString("path", path);
        dialogPropertiesFragment.setArguments(args);
        return dialogPropertiesFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        task = new MyTask();
        task.execute(file.getAbsolutePath());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        file = new File(getArguments().getString("path"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.file_properties, null);

        initializeTextsViews(view);
        fillFields();
        TextView title = setTitle(inflater.getContext());
        builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if
                task.isCancelled = true;
            }
        }).setCustomTitle(title);

        return builder.create();
    }

    private void initializeTextsViews(View view) {
        name = (TextView) view.findViewById(R.id.file_name);
        localization = (TextView) view.findViewById(R.id.file_localization);
        permissions = (TextView) view.findViewById(R.id.permissions);
        modification_time = (TextView) view.findViewById(R.id.modification_time);
        size = (TextView) view.findViewById(R.id.size);
        type_or_elementsTitle = (TextView) view.findViewById(R.id.type_or_elementsTitle);
        type_or_elements = (TextView) view.findViewById(R.id.type_or_elements);
    }

    private void fillFields() {
        name.setText(getName());
        localization.setText(getLocalization());
        permissions.setText(getPermissions());
        modification_time.setText(getModificationTime());
    }

    private TextView setTitle(Context context) {
        TextView title = new TextView(context);
        title.setText(file.getName() + " (" + getString(R.string.properties) + ")");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        return title;
    }

    private String getName() {
        return file.getName();
    }

    private String getLocalization() {
        return file.getParent();
    }

    private String getPermissions() {
        return fileHelper.checkedFilePermissions(file);
    }

    private String getModificationTime() {
        return fileHelper.dateToString(new Date(file.lastModified()));
    }


    class MyTask extends AsyncTask<String, Integer, String> {

        boolean isCancelled = false;
        long length;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            getTypeOrElements();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            size.setText(fileHelper.bytesToHuman(length));
            if (values[0] == 0)
                type_or_elements.setText(getString(R.string.folders) + ": " + folders + ", " +
                        getString(R.string.files) + ": " + files);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        private String getType() {
            return fileHelper.getExtension(file.getPath());
        }

        private void getTypeOrElements() {
            if (file.isDirectory()) {
                type_or_elementsTitle.setText(R.string.elements);
                getElements(file);
            } else {
                type_or_elementsTitle.setText(R.string.type);
                type_or_elements.setText(getType());
                length = file.length();
                publishProgress(1);
            }
        }

        private void getElements(File file) {
            if (file.isDirectory()) {
                ++folders;
                Log.d("folders ", String.valueOf(folders));
                publishProgress(0);
                if (file.list() != null) {
                    for (String children : file.list()) {
                        if (isCancelled) break;
                        else getElements(new File(file, children));
                    }
                }
            } else {
                ++files;
                Log.d("folders ", String.valueOf(files));
                length += file.length();
                publishProgress(0);
            }
        }
    }
}
