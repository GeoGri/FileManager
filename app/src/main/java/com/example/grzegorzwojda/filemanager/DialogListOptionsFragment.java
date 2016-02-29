package com.example.grzegorzwojda.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-02-08.
 */

public class DialogListOptionsFragment extends DialogFragment {

    public interface NoticeDialogListFragmentListener {
        void copyListener(List<File> listFiles);

        void cutListener(List<File> listFiles);
    }

    private String tagFragment;
    private Context context;
    private List<String> arrayList;
    private List<File> files;

    NoticeDialogListFragmentListener noticeDialogListFragmentListener;
    private FileHelper fileHelper = new FileHelper();

    static DialogListOptionsFragment newInstance(List<File> files, String tagFragment) {
        DialogListOptionsFragment dialogListOptionsFragment = new DialogListOptionsFragment();

        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) files);
        args.putString("tag", tagFragment);

        dialogListOptionsFragment.setArguments(args);
        return dialogListOptionsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        files = (List<File>) getArguments().getSerializable("list");
        tagFragment = getArguments().getString("tag");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            noticeDialogListFragmentListener = (NoticeDialogListFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        context = inflater.getContext();

        arrayList = new ArrayList<>();
        setListItems(arrayList);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayList);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //  menu option
                    setSelectedOption(which);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, (CharSequence) e, Toast.LENGTH_SHORT).show();
                }
            }
        }).setTitle(getString(R.string.rename_to));
        return builder.create();
    }

    private void setSelectedOption(int position) throws PackageManager.NameNotFoundException {

        String command = arrayList.get(position);

        if (command.equals(getString(R.string.open_with)))
            openWithOption();
        else if (command.equals(getString(R.string.share)))
            sendOption();
        else if (command.equals(getString(R.string.shortcut)))
            shortcutOption();
        else if (command.equals(getString(R.string.properties)))
            propertiesOption();
        else if (command.equals(getString(R.string.change_name)))
            changeNameOption();
        else if (command.equals(getString(R.string.delete)))
            deleteOption();
        else if (command.equals(getString(R.string.copy)))
            noticeDialogListFragmentListener.copyListener(files);
        else if (command.equals(getString(R.string.cut)))
            noticeDialogListFragmentListener.cutListener(files);
    }

    private void setListItems(List arrayList) {
        if (files.size() <= 1) {
            if (files.get(0).isFile()
                    && !fileHelper.getExtension(files.get(0).getPath()).equals("apk")) {
                arrayList.add(getString(R.string.open_with));
                arrayList.add(getString(R.string.share));
                arrayList.add(getString(R.string.shortcut));
            }
            arrayList.add(getString(R.string.change_name));
            arrayList.add(getString(R.string.properties));
        }
        arrayList.add(getString(R.string.delete));
        arrayList.add(getString(R.string.copy));
        arrayList.add(getString(R.string.cut));
    }

    private void shortcutOption() throws PackageManager.NameNotFoundException {

        File file = files.get(0);
        Uri uri = Uri.fromFile(file);

        String filePathWithoutWhitespaces = file.getAbsolutePath().replace(" ", "");
        String type = fileHelper.getMimeType(filePathWithoutWhitespaces);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (type == null) intent.setDataAndType(uri, "*/*");
        else intent.setDataAndType(uri, type);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, files.get(0).getName());
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.
                        fromContext(context,
                                fileHelper.selectIcon(file, "-")));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);

        Log.d(" -Shortcut- ", String.valueOf(true));
        Toast.makeText(context, getString(R.string.shortcut_add), Toast.LENGTH_SHORT).show();
    }

    private void openWithOption() {
        Uri uri = Uri.fromFile(files.get(0));
        List<Application> applications = fileHelper.getListOpensApplications(context, uri);
        FragmentManager fragmentManager = getFragmentManager();

        DialogOpenWithFragment dialogOpenWithFragment
                = new DialogOpenWithFragment().newInstance(files.get(0).getPath(), applications);
        dialogOpenWithFragment.show(fragmentManager, "Open with dialog");
    }

    private void sendOption() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(files.get(0).getAbsolutePath());

        String filePathWithoutWhitespaces = file.getAbsolutePath().replace(" ", "");
        String type = fileHelper.getMimeType(filePathWithoutWhitespaces);


        sharingIntent.setType(type);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(sharingIntent, "Share file"));
    }

    private void propertiesOption() {
        DialogPropertiesFragment dialogPropertiesFragment
                = DialogPropertiesFragment.newInstance(files.get(0).getPath());
        dialogPropertiesFragment.show(getFragmentManager(), "properties");
    }

    private void changeNameOption() {
        DialogRenameFileFragment dialogRenameFileFragment
                = new DialogRenameFileFragment().newInstance(files.get(0).getPath(), tagFragment);
        dialogRenameFileFragment.show(getFragmentManager(), "changeOption");
    }

    private void deleteOption() {
        DialogDeleteFragment dialogDeleteFragment
                = DialogDeleteFragment.newInstance(files, tagFragment);
        dialogDeleteFragment.show(getFragmentManager(), "delete");
    }

}
