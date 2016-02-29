package com.example.grzegorzwojda.filemanager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Grzegorz Wojda on 2016-01-21.
 */

public class Folder_EmptyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public interface NoticeEmptyFragmentListener {
        void onChangeFragmentTagFromEmptyFrag(String tag);
    }

    View view;
    NoticeEmptyFragmentListener noticeEmptyFragmentListener;

    Folder_EmptyFragment newInstance(String path) {
        Folder_EmptyFragment folderEmptyFragment = new Folder_EmptyFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        folderEmptyFragment.setArguments(args);
        return folderEmptyFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            noticeEmptyFragmentListener = (NoticeEmptyFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.file_manager_main_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.empty_row, container, false);

        EditText pathFragment = (EditText) view.findViewById(R.id.path);
        TextView emptyFile = (TextView) view.findViewById(R.id.textView2);

        String path = getArguments().getString("path");

        emptyFile.setText(inflater.getContext().getString(R.string.empty_row));
        pathFragment.setText(path);

        noticeEmptyFragmentListener.onChangeFragmentTagFromEmptyFrag(path);

        return view;
    }
}
