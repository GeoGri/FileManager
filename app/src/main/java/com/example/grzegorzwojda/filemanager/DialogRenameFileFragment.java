package com.example.grzegorzwojda.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.File;

/**
 * Created by Grzegorz Wojda on 2016-02-09.
 */

public class DialogRenameFileFragment extends DialogFragment {

    String oldFilePath;
    String tagFragment;

    static DialogRenameFileFragment newInstance(String path, String tagFragment) {
        DialogRenameFileFragment dialogRenameFileFragment = new DialogRenameFileFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("path", path);
        args.putString("tag", tagFragment);
        dialogRenameFileFragment.setArguments(args);
        return dialogRenameFileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oldFilePath = getArguments().getString("path");
        tagFragment = getArguments().getString("tag");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.rename_file, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        final EditText newName = (EditText) view.findViewById(R.id.rename);
        newName.setText(new File(oldFilePath).getName());
        newName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName.setCursorVisible(true);
            }
        });
        builder// Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        new FileHelper().renameFile(inflater.getContext(),
                                oldFilePath, newName.getText().toString());
                        new FragmentHelper().refreshFragment(getFragmentManager(),
                                tagFragment, false, false);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DialogRenameFileFragment.this.getDialog().cancel();
                    }
                }).setTitle(getString(R.string.rename_to));
        return builder.create();
    }
}
