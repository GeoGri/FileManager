package com.example.grzegorzwojda.filemanager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Grzegorz Wojda on 2016-01-22.
 */
public class Utilities {

    String PATH = "path";
    String EMPTY_TAG = "EMPTY";
    final Character folder = 'd';
    final String APPLICATION = "application";
    final String AUDIO = "audio";
    final String IMAGE = "image";
    final String TEXT = "text";
    final String VIDEO = "video";

    /*public int selectIcon(Context context, File file, String fileperm) throws PackageManager.NameNotFoundException {

        if (cheekedIsFileOrFolder(fileperm)) {            // sprawdzamy czy plik czy folder
            if (folderFiles(file.getPath())) {
                return R.drawable.empty_folder;
            } else {
                return R.drawable.folder;
            }
        } else {                                         // tutaj pliki
            Uri uri = Uri.fromFile(file);
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            String type = getMimeType(uri.toString());

            if (type == null) {
                return R.drawable.defoult;
            } else {
                String familly = type.substring(0, type.indexOf("/"));
                switch (familly) {
                    case APPLICATION:
                        if (extension.equals("rar") || extension.equals("cab") || extension.equals("arj") ||
                                extension.equals("lzh") || extension.equals("ace") || extension.equals("7z") ||
                                type.equals("application/zip") || type.equals("application/x-compressed-zip") ||
                                type.equals("application/x-tar") || type.equals("java-archive") || type.equals("x-gzip")) {
                            return R.drawable.zip_folder;
                        } else if (type.equals("application/vnd.android.package-archive")) {
                            return 1;
                        } else if (extension.equals("pdf")) {
                            return R.drawable.pdf;
                        } else if (type.equals("application/vnd.ms-powerpoint")) {
                            return R.drawable.default_powerpoint;
                        } else if (type.equals("application/vnd.ms-excel")) {
                            return R.drawable.default_exel;
                        } else if (extension.equals("xml")) {
                            return R.drawable.xml;
                        } else if (type.equals("application/msword")) {
                            return R.drawable.default_word;
                        } else {
                            return R.mipmap.ic_launcher;
                        }
                    case AUDIO:
                        return R.drawable.default_audio;
                    case IMAGE:
                        //return -1;
                        return R.drawable.default_image;
                    case TEXT:
                        return R.drawable.default_txt;
                    case VIDEO:
                        return R.drawable.default_video;
                    default:
                        return R.drawable.defoult;
                }
            }
        }
    }*//*

    public Bitmap getImage(Context context, File file) throws FileNotFoundException {
        Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 120, 120);






        return thumb;
    }

    public void test(Context context, File file){
        String[] projection = {MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA};
///storage/sdcard0/zedge/wallpaper/Street_View-wallpaper-10269951.jpg
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[]{file.getAbsolutePath()},
                null);


        //Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID);
        int count = cursor.getCount();
        int image_column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        int image_path_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

        String[] path = new String[count];
        Bitmap[] bm = new Bitmap[count];
        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int id = cursor.getInt(image_column_index);
            path[i] = cursor.getString(image_path_index);
            bm[i] = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

    }






    public static Bitmap getThumbnail(ContentResolver contentResolver, long id) {
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA}, // Which columns
                // to return
                MediaStore.Images.Media._ID + "=?", // Which rows to return
                new String[]{String.valueOf(id)}, // Selection arguments
                null);// order

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String filePath = cursor.getString(0);
            cursor.close();
            int rotation = 0;
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                int exifRotation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                if (exifRotation != ExifInterface.ORIENTATION_UNDEFINED) {
                    switch (exifRotation) {
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                    }
                }
            } catch (IOException e) {
                Log.e("getThumbnail", e.toString());
            }
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        } else
            return null;
    }

    public String fileRealPath(Context context, File file){
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(Uri.parse("content:/"+file.getPath()));

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            String idFile = cursor.getString(columnIndex);
            cursor.close();
            return idFile;
        }

        cursor.close();
        return null;
    }

    public String RealPath(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public String[] getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID };
        Cursor cursor =context.getContentResolver().query(contentUri,
                proj, null, null, null);
        int path_index = cursor.getColumnIndexOrThrow(proj[0]);
        int id_index = cursor.getColumnIndexOrThrow(proj[1]);
        cursor.moveToFirst();
        return new String[] { cursor.getString(path_index),
                cursor.getLong(id_index) + "" };
    }*/
/*
    public Drawable getIconApk(Context context, File file) {
        if (file.getPath().endsWith(".apk")) {
            String filePath = file.getPath();
            PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                if (Build.VERSION.SDK_INT >= 8) {
                    appInfo.sourceDir = filePath;
                    appInfo.publicSourceDir = filePath;
                }
                return appInfo.loadIcon(context.getPackageManager());
            }
        }
        return null;
    }*/

    /*public List<Application> getListOpensApplications(Context context, Uri fileUri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(fileUri);
        String type = getMimeType(fileUri.toString());
        intent.setType(type);

        List<Application> applications = new ArrayList<>();

        final List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        if (matches.size() >= 0) {
            for (ResolveInfo match : matches) {
                Application application = new Application();
                application.setIcon(match.loadIcon(context.getPackageManager()));
                application.setLabel(match.loadLabel(context.getPackageManager()));
                application.setPackageName(match.activityInfo.packageName);
                applications.add(application);
            }
        } else {
            Application application = new Application();
            application.setLabel(context.getString(R.string.no_applications));
        }
        return applications;
    }*/

    // url = file path or whatever suitable URL you want.
    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }

    public String getExtension(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    public String checkedFilePermissions(File file) {
        String permissions;
        if (file.isFile()) permissions = "-";
        else permissions = "d";
        if (file.canRead()) permissions += "r";
        else permissions += "-";
        if (file.canWrite()) permissions += "w";
        else permissions += "-";
        if (file.canExecute()) permissions += "x";
        else permissions += "-";
        return permissions;
    }

    public void renameFile(Context context, String oldFilePath, String newName) {

        File f1 = new File(oldFilePath);
        File f2 = new File(f1.getParent() + "/" + newName);
        if (f1.exists()) {
            if (f1.canWrite()) {
                Log.d("f1", "is writable");
            } else {
                Log.d("f1", "not writable");
                boolean f1w = f1.setWritable(true, false);
                Log.d("f1", String.valueOf(f1w));
            }
            if (f2.canWrite()) {
                Log.d("f2", "is writable");
            } else {
                Log.d("f1", "not writable");
                boolean f2w = f2.setWritable(true, false);
                Log.d("f2", String.valueOf(f2w));
            }

            boolean resoult = f1.renameTo(f2);
            Log.d("resoult", String.valueOf(resoult));
            if (resoult) Toast.makeText(context, context.getString(R.string.renamed),
                    Toast.LENGTH_SHORT).show();
            else {
                boolean root;
                if (f1.isDirectory()) {
                    root = renameFileWithRootAccess(f1.getAbsolutePath(), f2.getAbsolutePath());
                } else {
                    root = renameFileWithRootAccess(f1.getAbsolutePath(), f2.getAbsolutePath() + "." + getExtension(f1.getPath()));
                }//Toast.makeText(context,context.getString(R.string.root_rights), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, String.valueOf(root), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean renameFileWithRootAccess(String oldName, String newName) {
        try {
            String command = "mv " + "\"" + oldName + "\"" + " " + "\"" + newName + "\"";
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(command + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();
            return true;
        } catch (Exception ex) {
            Log.i("ROOT", "Could not reboot", ex);
            return false;
        }
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        boolean resoult = fileOrDirectory.delete();
        Log.d(" onProgressDelete -->", String.valueOf(resoult));
        if (!resoult) {
            resoult = deleteFileWithRootAccess(fileOrDirectory.getAbsolutePath());
            Log.d(" onProgressRoot -->", String.valueOf(resoult));
        }
    }

    public boolean deleteFileWithRootAccess(String path) {
        try {
            String command = "rm -r " + "\"" + path + "\"";
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(command + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            p.waitFor();
            return true;
        } catch (Exception ex) {
            Log.i("ROOT", "Could not reboot", ex);
            return false;
        }
    }

    public boolean copyFile(Context context, File src, String dstPath) {


        File dst = new File(dstPath + "/(copy)" + src.getName());

        if (src.getName().equals(dst.getName())) {
            renameFile(context, src.getAbsolutePath(), src.getName() + "1");
        }

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

    public boolean cheekedIsFileOrFolder(String permissions) {
        return permissions.charAt(0) == folder;
    }

    // czy istnieją pliki w folderze
    public boolean checkListFileIsNul(String dir) {

        File file = new File(dir);
        File[] tab;
        //brak plików
        //Plik pusty ale lista plików nie ?
        return (tab = file.listFiles()) == null || tab.length == 0;
    }

    // Lista pliów

    public boolean folderFiles(String path) {
        path += "/";
        ArrayList<File> list = getsAllFiles(path);
        return list.size() == 0;
    }

    public ArrayList<File> getsAllFiles(String path) {

        File dir = new File(path);                           // obecna sciezka dostepu
        ArrayList<File> list = new ArrayList<>();       // lista plików
        File[] dirList = dir.listFiles();                   // pliki w obecnej sciezce
        if (dirList != null)
            Collections.addAll(list, dirList);    // add all

        // sort -> folders, files
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int result = 0;
                if(!lhs.isDirectory())
                    result +=1;
                if(!rhs.isDirectory())
                    result -=1;

                if(result == 0)
                    result = lhs.getAbsolutePath().compareTo(rhs.getAbsolutePath());
                return result;
            }
        });
        return list;
    }

    public String dateToString(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }

    public void refreshFragment(FragmentManager fragmentManager, String tagFragment, boolean emptyFrag, boolean newFrag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = getFragmentByTag(fragmentManager, tagFragment);

        if (fragment == null) {
            if (emptyFrag) {
                fragmentManager.popBackStack();
                openFile(tagFragment, fragmentManager, false);
            } else {
                openFile("/", fragmentManager, false);
                clearBackStack(fragmentManager);
            }
        } else if (newFrag) {
            fragmentManager.popBackStack();
            openFile(tagFragment, fragmentManager, false);
        } else {
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
        boolean pathIsEmpty = checkListFileIsNul(path);
        if (newFile.mkdirs()) {
            Toast.makeText(context, context.getString(R.string.created)
                    + " "
                    + context.getString(R.string.folder)
                    + " "
                    + name
                    , Toast.LENGTH_SHORT).show();
        } else {
            Log.d(" -folder not created- ", name);
            String resoult = createNewDirectoryWithRootAccess(path + "/" + name);
            if (resoult.equals("ok") && (new File(path + "/" + name).exists())) {
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

    // Strat nowego fragmentu
    public void openFile(String path, FragmentManager fragmentManager, boolean drawerLayout) {

        Fragment fragment;
        if (checkListFileIsNul(path)) fragment = new Folder_EmptyFragment().newInstance(path);
        else fragment = new Folder_ListFragment();

        Bundle args = new Bundle();
        args.putString(PATH, path);

        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (drawerLayout)
            fragmentTransaction.setCustomAnimations(R.anim.custon_anim_in, R.anim.custom_anim_out, R.anim.custon_anim_in, R.anim.custom_anim_out);
        if (checkListFileIsNul(path))
            fragmentTransaction.replace(R.id.containerFragment, fragment, EMPTY_TAG);
        else {
            fragmentTransaction.replace(R.id.containerFragment, fragment, path);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void clearBackStack(FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
        }
    }

    // sprawdzenie stanu karty pamieci
    public boolean getExternalStorageState(Context context) {

        String extState = Environment.getExternalStorageState();
        if (!extState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.no_sd_card, Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    public long TotalMemory(String path) {
        StatFs statFs = new StatFs(path);
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        return Total;
    }

    public long BusyMemory(String path) {
        StatFs statFs = new StatFs(path);
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
        long Busy = Total - Free;
        return Busy;
    }

    public String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public String bytesToHuman(long size) {
        long Kb = 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " Mb";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " Gb";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " Tb";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " Pb";
        if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

        return "???";
    }

}
