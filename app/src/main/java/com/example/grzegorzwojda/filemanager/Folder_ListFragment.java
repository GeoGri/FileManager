package com.example.grzegorzwojda.filemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-01-20.
 */

public class Folder_ListFragment extends ListFragment {

    public interface NoticeListFragmentManagerListener {
        void onChangeFragmentTag(String tag);
    }

    EditText pathFragment;
    Context context;
    String path = "/";
    String tagFragment;
    CustomAdapterListFiles listFiles;
    ArrayList<File> list;
    List<File> selectedFiles = new ArrayList<>();
    FileHelper fileHelper = new FileHelper();
    FragmentHelper fragmentHelper = new FragmentHelper();
    NoticeListFragmentManagerListener noticeListFragmentManagerListener;
    FloatingActionButton fab;
    MenuItem menuItem;
    long checkedCount;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            noticeListFragmentManagerListener = (NoticeListFragmentManagerListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        fab = (FloatingActionButton) ((Activity) context).findViewById(R.id.fab);
        inflater.inflate(R.menu.file_manager_main_fragment, menu);
        menuItem = menu.findItem(R.id.action_cancel);
        if(fab.isShown() && menuItem != null) menuItem.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel){
            menuItem.setEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        pathFragment = (EditText) view.findViewById(R.id.path);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(fileHelper.PATH); // pobranie ściezki
            tagFragment = path;
        }
        list = fileHelper.getsAllFiles(path);    // zaczytanie plików
        listFiles = new CustomAdapterListFiles(context, list);


        ListView lv = getListView();

        lv.setAdapter(listFiles);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView permissions = (TextView) view.findViewById(R.id.file_permissions);
                path = list.get(position).getPath() + "/";
                openFile(permissions);
            }
        });


        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                actualizeSelectedList(position);
                checkedCount = selectedFiles.size();
                mode.setTitle(checkedCount + " " + getString(R.string.selected));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_option, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.option:

                        DialogListOptionsFragment dialogListOptionsFragment =
                                new DialogListOptionsFragment()
                                        .newInstance(new ArrayList<>(selectedFiles), tagFragment);
                        dialogListOptionsFragment.show(getFragmentManager(), "dialog");

                        onDestroyActionMode(mode);
                        mode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                checkedCount = selectedFiles.size();
                selectedFiles.clear();
            }
        });

        pathFragment.setText(path);
        noticeListFragmentManagerListener.onChangeFragmentTag(tagFragment);

    }


    private int actualizeSelectedList(int position) {
        List<File> temp = new ArrayList<>(selectedFiles);

        for (int i = 0; i < temp.size(); i++)
            if (temp.get(i) == list.get(position))
                return selectFile(position, false);

        return selectFile(position, true);
    }

    private int selectFile(int position, boolean add) {
        if (add) selectedFiles.add(list.get(position));
        else selectedFiles.remove(list.get(position));
        return 0;
    }


    private void openFile(TextView permissions) {

        if (fileHelper.cheekedIsFileOrFolder(permissions.getText().toString())) {
            // open folder
            fragmentHelper.openFile(path, getFragmentManager(), false);
        } else {
            // open file if is recognise
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            String type1 = fileHelper.getMimeType(uri.toString());

            if (type1 == null)
                Toast.makeText(context, getString(R.string.no_app_to_open), Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, type1);
                startActivity(intent);
            }
        }
    }
}