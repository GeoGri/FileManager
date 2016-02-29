package com.example.grzegorzwojda.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Grzegorz Wojda on 2016-01-21.
 */
public class CustomAdapterListFiles extends ArrayAdapter<File> {

    Activity activity;
    Context context;
    ArrayList<File> files;
    //Map<String, Bitmap> loadedFilesThumbnail = new HashMap<>();
    FileHelper fileHelper = new FileHelper();
    //AsyncLoadThumbnail myAsyncTask = new AsyncLoadThumbnail();

    public CustomAdapterListFiles(Context context, ArrayList<File> list) {
        super(context, R.layout.list_fragment_row, list);
        this.context = context;
        this.files = list;
        this.activity = (Activity) context;
        //myAsyncTask.execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_fragment_row, null);
        } else view = convertView;

        TextView fileName = (TextView) view.findViewById(R.id.folder_name);
        TextView fileDate = (TextView) view.findViewById(R.id.folder_date);
        TextView filePerm = (TextView) view.findViewById(R.id.file_permissions);
        ImageView fileIcon = (ImageView) view.findViewById(R.id.folder_icon);

        fileName.setText(files.get(position).getName());
        fileDate.setText(fileHelper.dateToString(new Date(files.get(position).lastModified())));
        filePerm.setText(fileHelper.checkedFilePermissions(files.get(position)));
        try {
            int id = fileHelper.selectIcon(files.get(position), filePerm.getText().toString());
            Drawable icon;
            if (id == 1) {
                icon = fileHelper.getIconApk(context, files.get(position));
                fileIcon.setImageDrawable(icon);
            /*} else if (id == -1) {
                checkedIfNeedLoadThumbnail(fileIcon, files.get(position));*/
            } else {
                icon = context.getDrawable(id);
                fileIcon.setImageDrawable(icon);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return view;
    }


    /*public void checkedIfNeedLoadThumbnail(ImageView imageView, File fileToLoad) {
        boolean exist = loadedFilesThumbnail.containsKey(fileToLoad.getName());
        if (exist) imageView.setImageBitmap(loadedFilesThumbnail.get(fileToLoad.getName()));
        else
            myAsyncTask.addThumbnailToLoad(fileToLoad, imageView);
    }


    class AsyncLoadThumbnail extends AsyncTask<Void, Bitmap, Void> {

        class pare {
            File file;
            ImageView imageView;

            pare(File file, ImageView imageView) {
                this.file = file;
                this.imageView = imageView;
            }
        }

        boolean asyncWork = true;
        Queue<pare> filesToLoad = new LinkedList<>();

        public void cancelAsyc(){
            asyncWork = false;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            filesToLoad.clear();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            filesToLoad.clear();
        }

        public synchronized void addThumbnailToLoad(File file, ImageView imageView) {
            filesToLoad.add(new pare(file, imageView));
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            Bitmap bitmap = values[0];
            filesToLoad.peek().imageView.setImageBitmap(bitmap);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (asyncWork){
                if (!filesToLoad.isEmpty()) {
                    try {
                        Bitmap bitmap = utilities.getImage(context, filesToLoad.peek().file);
                        loadedFilesThumbnail.put(filesToLoad.peek().file.getName(), bitmap);
                        filesToLoad.poll();
                        publishProgress(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }*/
}
