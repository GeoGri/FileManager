package com.example.grzegorzwojda.filemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;

/**
 * Created by Grzegorz Wojda on 2016-02-24 14:10.
 */

public class FragmentHelper {


    String PATH = "path";
    String EMPTY_TAG = "EMPTY";

    FileHelper fileHelper = new FileHelper();

    public void refreshFragment(FragmentManager fragmentManager, String tagFragment, boolean emptyFrag, boolean newFrag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = getFragmentByTag(fragmentManager, tagFragment);

        if (fragment == null) {
            if (emptyFrag) {    // change File_Empty -> File_List
                fragmentManager.popBackStack();
                openFile(tagFragment, fragmentManager, false);
            } else {
                openFile("/", fragmentManager, false);
                clearBackStack(fragmentManager);
            }
        } else if (newFrag) {   //  refresh background list
            fragmentManager.popBackStack();
            openFile(tagFragment, fragmentManager, false);
        } else {    //  refresh
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
            fragmentTransaction.commit();
        }
    }

    public Fragment getFragmentByTag(FragmentManager fragmentManager, String tagFragment) {
        return fragmentManager.findFragmentByTag(tagFragment);
    }

    public void createNewDirectory(Context context, FragmentManager fragmentManager, String path, String name) {
        File newFile = new File(path, name);
        boolean pathIsEmpty = fileHelper.checkListFileIsNul(path);
        if (newFile.mkdirs()) {
            Toast.makeText(context, context.getString(R.string.created)
                    + " "
                    + context.getString(R.string.folder)
                    + " "
                    + name
                    , Toast.LENGTH_SHORT).show();
        } else {
            Log.d(" -folder not created- ", name);
            String result = createNewDirectoryWithRootAccess(path + "/" + name);
            if (result.equals("ok") && (new File(path + "/" + name).exists())) {
                Log.d(" -ROOT- ", "Created folder " + name);
                Toast.makeText(context, context.getString(R.string.created)
                        + " "
                        + context.getString(R.string.folder)
                        + " "
                        + name
                        , Toast.LENGTH_SHORT).show();
            } else {
                Log.d(" -ROOT- ", "Folder not created " + name);
                Toast.makeText(context, context.getString(R.string.no_root_or_this_is_a_system_folder)
                        , Toast.LENGTH_LONG).show();
            }
        }
        if (pathIsEmpty)
            refreshFragment(fragmentManager, path, true, false);
        else
            refreshFragment(fragmentManager, path, false, false);
        Log.d(" -created folder- ", name);
    }

    // start new fragment
    public void openFile(String path, FragmentManager fragmentManager, boolean drawerLayout) {

        Fragment fragment;

        // choice fragment: File_Empty  /   File_list
        if (fileHelper.checkListFileIsNul(path))
            fragment = new Folder_EmptyFragment().newInstance(path);
        else fragment = new Folder_ListFragment();

        Bundle args = new Bundle();
        args.putString(PATH, path);

        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (drawerLayout)
            fragmentTransaction.setCustomAnimations(R.anim.custon_anim_in, R.anim.custom_anim_out, R.anim.custon_anim_in, R.anim.custom_anim_out);

        //  if Folder_Empty_Fragment tag empty
        if (fileHelper.checkListFileIsNul(path))
            fragmentTransaction.replace(R.id.containerFragment, fragment, EMPTY_TAG);
        else {
            fragmentTransaction.replace(R.id.containerFragment, fragment, path);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public String createNewDirectoryWithRootAccess(String path) {
        try {

            String command = "mkdir " + "\"" + path + "\"";
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(command + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();

            return "ok";
        } catch (Exception ex) {
            Log.i("ROOT", "Could not reboot", ex);
            return "fail";
        }
    }

    public void clearBackStack(FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
        }
    }
}
