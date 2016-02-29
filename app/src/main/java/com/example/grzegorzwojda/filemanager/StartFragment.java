package com.example.grzegorzwojda.filemanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Grzegorz Wojda on 2016-02-17.
 */
public class StartFragment extends Fragment implements View.OnClickListener {

    interface NoticeStartFragment{
        void setToolbarTitle(String title);
    }

    private ProgressBar root_directory_progress_bar;
    private ProgressBar internal_storage_progress_bar;
    private ProgressBar external_storage_progress_bar;

    private TextView root_directory_size;
    private TextView internal_storage_size;
    private TextView external_storage_size;

    Context context;
    FileHelper fileHelper = new FileHelper();
    FragmentHelper fragmentHelper = new FragmentHelper();
    NoticeStartFragment noticeStartFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container);
        context = inflater.getContext();
        GridLayout gridLayout = (GridLayout) view.findViewById(R.id.startGridLayout);
        initialViews(view);
        setViewValues();

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(i);
            linearLayout.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            noticeStartFragment = (NoticeStartFragment) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    private void initialViews(View view) {
        root_directory_progress_bar
                = (ProgressBar) view.findViewById(R.id.root_directory_progress_bar);
        internal_storage_progress_bar
                = (ProgressBar) view.findViewById(R.id.internal_storage_progress_bar);
        external_storage_progress_bar
                = (ProgressBar) view.findViewById(R.id.external_storage_progress_bar);

        root_directory_size = (TextView) view.findViewById(R.id.root_directory_size);
        internal_storage_size = (TextView) view.findViewById(R.id.internal_storage_size);
        external_storage_size = (TextView) view.findViewById(R.id.external_storage_size);
    }

    private void setViewValues() {
        setTextSpaces();
        setSpaces();
    }

    private void setTextSpaces() {
        String busyMemoryInternal
                = fileHelper.bytesToHuman(fileHelper.BusyMemory(System.getenv("EXTERNAL_STORAGE")));
        String totalMemoryInternal
                = fileHelper.bytesToHuman(fileHelper.TotalMemory(System.getenv("EXTERNAL_STORAGE")));
        String busyMemoryExternal;
        String totalMemoryExternal;

        root_directory_size.setText(busyMemoryInternal + "/" + totalMemoryInternal);
        internal_storage_size.setText(busyMemoryInternal + "/" + totalMemoryInternal);

        if (fileHelper.getExternalStorageState(context)) {
            busyMemoryExternal = fileHelper.
                    bytesToHuman(fileHelper.BusyMemory(System.getenv("SECONDARY_STORAGE")));
            totalMemoryExternal = fileHelper.
                    bytesToHuman(fileHelper.TotalMemory(System.getenv("SECONDARY_STORAGE")));
        } else {
            busyMemoryExternal = "";
            totalMemoryExternal = getString(R.string.no_sd_card);
        }
        external_storage_size.setText(busyMemoryExternal + "/" + totalMemoryExternal);
    }

    private void setSpaces() {
        rootSpace();
        internalSpace();
        externalSpace();
    }

    private void rootSpace() {
        root_directory_progress_bar.setMax(100);
        long busy = fileHelper.BusyMemory(System.getenv("EXTERNAL_STORAGE"));
        long total = fileHelper.TotalMemory(System.getenv("EXTERNAL_STORAGE"));
        long progress = (busy * 100) / total;
        root_directory_progress_bar.setProgress((int) progress);
    }

    private void internalSpace() {
        internal_storage_progress_bar.setMax(100);
        internal_storage_progress_bar.setProgress(root_directory_progress_bar.getProgress());
    }

    private void externalSpace() {
        if (fileHelper.getExternalStorageState(context)) {
            external_storage_progress_bar.setMax(100);
            long busy = fileHelper.BusyMemory(System.getenv("SECONDARY_STORAGE"));
            long total = fileHelper.TotalMemory(System.getenv("SECONDARY_STORAGE"));
            long progress;
            if (total == 0) progress = 0;
            else progress = (busy * 100) / total;
            external_storage_progress_bar.setProgress((int) progress);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == 2131493020) {   //picture
            fragmentHelper.openFile(System.getenv("EXTERNAL_STORAGE") + "/DCIM",
                    getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.photos));
        } else if (id == 2131493021) {    //music
            fragmentHelper.openFile(System.getenv("EXTERNAL_STORAGE") + "/Music",
                    getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.music));
        } else if (id == 2131493022) {    //movie
            fragmentHelper.openFile(System.getenv("EXTERNAL_STORAGE") + "/Movies",
                    getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.movie));
        } else if (id == 2131493023) {    //download
            fragmentHelper.openFile(System.getenv("EXTERNAL_STORAGE") + "/Download",
                    getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.download));
        } else if (id == 2131493024) {    //root
            fragmentHelper.openFile("/", getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.root_directory));
        } else if (id == 2131493027) {    //internal
            fragmentHelper.openFile(System.getenv("EXTERNAL_STORAGE"),
                    getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.internal_storage));
        } else if (id == 2131493030) {    //external
            fragmentHelper.openFile(System.getenv("SECONDARY_STORAGE"),
                    getFragmentManager(), true);
            noticeStartFragment.setToolbarTitle(getString(R.string.external_storage));
        }
    }
}
