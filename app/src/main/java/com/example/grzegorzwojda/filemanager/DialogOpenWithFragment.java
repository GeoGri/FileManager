package com.example.grzegorzwojda.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-02-10.
 */

public class DialogOpenWithFragment extends DialogFragment {

    private List<Application> applications;
    private File file;

    static DialogOpenWithFragment newInstance (String path,  List<Application> applications){
        DialogOpenWithFragment dialogOpenWithFragment = new DialogOpenWithFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("applications", (Serializable) applications);
        args.putString("path", path);
        dialogOpenWithFragment.setArguments(args);
        return dialogOpenWithFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applications = (List<Application>) getArguments().getSerializable("applications");
        file = new File(getArguments().getString("path"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        CustomAdapterApplicationList adapter
                = new CustomAdapterApplicationList(inflater.getContext(), applications);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {

                    Intent mIntent = new Intent(Intent.ACTION_VIEW);

                    String filePathWithoutWhitespaces = file.getAbsolutePath().replace(" ", "");
                    String type = new FileHelper().getMimeType(filePathWithoutWhitespaces);

                    mIntent.setDataAndType(Uri.fromFile(file), type);
                    mIntent.setPackage(applications.get(which).getPackageName());
                    startActivity(Intent.createChooser(mIntent, "View " + file.getName()));
                } catch (Exception e) {
                    //App not found
                    e.printStackTrace();
                }
            }
        }).setTitle(getString(R.string.rename_to));
        return builder.create();
    }
}
