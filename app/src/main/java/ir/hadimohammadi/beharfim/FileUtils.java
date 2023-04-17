package ir.hadimohammadi.beharfim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static final String CORR_FOLDER = "correspondence";
    public static final String CUSTOMERS_FOLDER = "customers";
    public static final String ORDER_FOLDER = "orders";

    public int upload = 0;
    public String uriStr = "";
    public String id = "";
    public String path = "";
    public String name = "";
    public String mimeType = "";
    public Long size = 0L;
    public int width = 0;
    public int height = 0;


    @Override
    public String toString() {
        return "FileUtils{" +
                "uriStr='" + uriStr + '\'' +
                ", id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                '}';
    }



    public static void resizeIfMedia(Context context, FileUtils metaData) {

        if (FileUtils.isImage(metaData.mimeType) && !metaData.path.isEmpty()) {

            try {
                File file = new File(metaData.path);
                ExifInterface oldExif = new ExifInterface(file.getAbsolutePath());
                String exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);


                File storageDir = new File(context.getExternalFilesDir(null).getAbsolutePath());
                if (!storageDir.exists()) storageDir.mkdir();
                File tempImg = new File(storageDir, file.getName());

                FileOutputStream stream = new FileOutputStream(tempImg);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream);

                if (exifOrientation != null) {
                    ExifInterface newExif = new ExifInterface(tempImg.getAbsolutePath());
                    newExif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation);
                    newExif.saveAttributes();
                }

                metaData.uriStr = Uri.fromFile(tempImg).toString();
                metaData.path = tempImg.getAbsolutePath();
                metaData.size = tempImg.length();

            } catch (IOException e) {

                e.printStackTrace();
            }

        } else if (FileUtils.isVideo(metaData.mimeType) && !metaData.path.isEmpty()) {
          /*  //https://github.com/AbedElazizShe/LightCompressor
            String filePath = "";
            try {
                filePath = SiliCompressor.with(context).compressVideo(metaData.path,
                        context.getExternalFilesDir(null).getAbsolutePath(),metaData.width,metaData.height,10000);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            metaData.path = filePath ;*/
        }

    }

    public static FileUtils getMetaData(final Context context, final Uri uri) {

        Log.d("uri", uri.toString());

        FileUtils fileMetaData = new FileUtils();
        fileMetaData.uriStr = uri.toString();

        //find path form uri...
        if (DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {

                String filePath = "";
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Log.d("uriType", "externalStorage");

                if ("primary".equalsIgnoreCase(type)) {
                    filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    //non-primary e.g sd card
                    File[] external = context.getExternalMediaDirs();
                    for (File f : external) {
                        filePath = f.getAbsolutePath();
                        if (filePath.contains(type)) {
                            int endIndex = filePath.indexOf("Android");
                            filePath = filePath.substring(0, endIndex) + split[1];
                        }
                    }
                }

                getInfoColumns(context, fileMetaData, uri, null, null);
                fileMetaData.path = filePath;

            } else if (isDownloadsDocument(uri)) {

                Log.d("uriType", "downloadDocument");
                getInfoColumns(context, fileMetaData, uri, null, null);
                if (!fileMetaData.name.isEmpty()) {
                    Uri newUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                    fileMetaData.path = Uri.withAppendedPath(newUri, fileMetaData.name).toString();
                }

            } else if (isMediaDocument(uri)) {

                Log.d("uriType", "mediaDocument");

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri ;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentUri = MediaStore.getMediaUri(context,uri);
                }else{

                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    } else {
                        contentUri = uri;
                    }
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                getInfoColumns(context, fileMetaData, contentUri, selection, selectionArgs);

            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            Log.d("uriType", "content");
            getInfoColumns(context, fileMetaData, uri, null, null);
            if (isGooglePhotosUri(uri)) { fileMetaData.path = uri.getLastPathSegment(); }

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {

            Log.d("uriType", "file");
            File f = new File(uri.getPath());
            fileMetaData.path = f.getPath();
            fileMetaData.name = f.getName();
            fileMetaData.size = f.length();
            // fileMetaData.mimeType  should have set manually bcuz File object don't have mimeType

        }

        Log.d("metaData", fileMetaData.toString());
        return fileMetaData;
    }

    @SuppressLint("Range")
    private static void getInfoColumns(Context context, FileUtils fileUtils , Uri uri, String selection, String[] selectionArgs) {

        //we set uriStr here bcuz uri changes on getMetaData() method ...;
        fileUtils.uriStr = uri.toString();

        final String[] projection = new String[]{
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
        };

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {


            if (cursor != null && cursor.moveToFirst()) {

               if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns._ID)) ) {
                    fileUtils.id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                }

                if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))) {
                    fileUtils.path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                }

                if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)) ) {
                    fileUtils.name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                }

                if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))) {
                    fileUtils.mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                }

                if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE) )) {
                    fileUtils.size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
                }

                if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH) )) {
                    fileUtils.width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH));
                }

                if (!cursor.isNull(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT) )) {
                    fileUtils.height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile.equals(destFile)) {
            return true;
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        try (FileInputStream source = new FileInputStream(sourceFile); FileOutputStream destination = new FileOutputStream(destFile)) {
            destination.getChannel().transferFrom(source.getChannel(), 0, source.getChannel().size());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String formatFileSize(long size, boolean removeZero) {
        if (size < 1024) {
            return String.format("%d B", size);
        } else if (size < 1024 * 1024) {
            float value = size / 1024.0f;
            if (removeZero || (value - (int) value) * 10 == 0) {
                return String.format("%d KB", (int) value);
            } else {
                return String.format("%.1f KB", value);
            }
        } else if (size < 1024 * 1024 * 1024) {
            float value = size / 1024.0f / 1024.0f;
            if (removeZero || (value - (int) value) * 10 == 0) {
                return String.format("%d MB", (int) value);
            } else {
                return String.format("%.1f MB", value);
            }
        } else {
            float value = (int) (size / 1024L / 1024L) / 1000.0f;
            if (removeZero || (value - (int) value) * 10 == 0) {
                return String.format("%d GB", (int) value);
            } else {
                return String.format("%.2f GB", value);
            }
        }
    }

    public static void deleteAllFiles(File dir) {

        if (dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory())
                    file.delete();
            }
        }

    }

    public static boolean isImage(String fileMimeType) {

       /*
       String[] allowedExtensions = new String[]{"jpeg", "jpg", "png", "tiff", "gif"};
        for (String extension : allowedExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
        */
        return fileMimeType.contains("image");

    }

    public static boolean isVideo(String fileMimeType) {
        return fileMimeType.startsWith("video");
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
