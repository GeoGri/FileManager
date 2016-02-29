package com.example.grzegorzwojda.filemanager;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-02-18 21:39.
 */

public class DialogDeleteFragment extends DialogFragment {

    Context context;
    List<File> files;
    String tagFragment;

    static DialogDeleteFragment newInstance(List<File> files, String tagFragment){
        DialogDeleteFragment dialogDeleteFragment = new DialogDeleteFragment();

        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) files);
        args.putString("tag", tagFragment);
        dialogDeleteFragment.setArguments(args);

        return dialogDeleteFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        files = (List<File>) getArguments().getSerializable("list");
        tagFragment = getArguments().getString("tag");
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        context = layoutInflater.getContext();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new MyTask().execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void>{

        String parent;
        Activity activity;
        FragmentHelper fragmentHelper = new FragmentHelper();
        FileHelper fileHelper = new FileHelper();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismiss();
            activity = (Activity) context;
            progressDialog = ProgressDialog.show(context,
                    getString(R.string.deleting), getString(R.string.wait), true);
            parent = files.get(0).getParent();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!files.isEmpty()){
                fileHelper.deleteRecursive(files.get(0));
                files.remove(0);
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (fileHelper.checkListFileIsNul(parent))
                fragmentHelper.refreshFragment(activity.getFragmentManager(), parent, true, false);
            else
                fragmentHelper.refreshFragment(activity.getFragmentManager(), tagFragment, false, false);
        }
    }
}
