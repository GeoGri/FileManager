package com.example.grzegorzwojda.filemanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Grzegorz Wojda on 2016-01-22.
 */

public class FileHelper {
    String PATH = "path";
    final Character folder = 'd';
    final String APPLICATION = "application";
    final String AUDIO = "audio";
    final String IMAGE = "image";
    final String TEXT = "text";
    final String VIDEO = "video";

    public boolean cheekedIsFileOrFolder(String permissions) {
        return permissions.charAt(0) == folder;
    }

    public String dateToString(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }

    // if there are files in folder
    public boolean checkListFileIsNul(String dir) {

        File file = new File(dir);
        File[] tab;
        //  no files or
        //  list size is empty ?
        return (tab = file.listFiles()) == null || tab.length == 0;
    }

    public boolean folderFiles(String path) {
        path += "/";
        ArrayList<File> list = getsAllFiles(path);
        return list.size() == 0;
    }

    public ArrayList<File> getsAllFiles(String path) {

        File dir = new File(path);                           // file path
        ArrayList<File> list = new ArrayList<>();            // list
        File[] dirList = dir.listFiles();                    // files in tem array
        if (dirList != null)
            Collections.addAll(list, dirList);    // add all

        // sort -> folders, files
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int result = 0;
                if (!lhs.isDirectory())
                    result += 1;
                if (!rhs.isDirectory())
                    result -= 1;

                if (result == 0)
                    result = lhs.getAbsolutePath().compareTo(rhs.getAbsolutePath());
                return result;
            }
        });
        return list;
    }

    public String getExtension(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null)
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        return type;
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

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        boolean result = fileOrDirectory.delete();
        Log.d(" onProgressDelete -->", String.valueOf(result));
        if (!result) {
            result = deleteFileWithRootAccess(fileOrDirectory.getAbsolutePath());
            Log.d(" onProgressRoot -->", String.valueOf(result));
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
    }

    public List<Application> getListOpensApplications(Context context, Uri fileUri) {
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
    }

    // checked external storage sate
    public boolean getExternalStorageState(Context context) {

        String extState = Environment.getExternalStorageState();
        if (!extState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.no_sd_card, Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    public long TotalMemory(String path) {
        StatFs statFs = new StatFs(path);
        return ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
    }

    public long BusyMemory(String path) {
        StatFs statFs = new StatFs(path);
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
        return Total - Free;
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

    public int selectIcon(File file, String filePerm) throws PackageManager.NameNotFoundException {

        if (cheekedIsFileOrFolder(filePerm)) {            // is File or folder
            if (folderFiles(file.getPath())) {  //  folder
                return R.drawable.empty_folder;
            } else {
                return R.drawable.folder;
            }
        } else {                                         // file
            Uri uri = Uri.fromFile(file);
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            String type = getMimeType(uri.toString());

            if (type == null) {
                return R.drawable.defoult;
            } else {
                String family = type.substring(0, type.indexOf("/"));
                switch (family) {
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
    }

    /*public Bitmap getImage(File file) throws FileNotFoundException {
        Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getAbsolutePath()), 120, 120);
        return thumb;
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
}
