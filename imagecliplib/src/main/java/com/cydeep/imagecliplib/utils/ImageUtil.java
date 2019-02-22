package com.cydeep.imagecliplib.utils;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    public static String saveClip(Bitmap photo, String path) {
        String fileName = FileUtils.getSaveFile(path, FileUtils.PNG_FILE_SUFFIX);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(fileName, false));
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getSuitBitmap(String filePath) {
        Bitmap bitmap;
        if (filePath.startsWith("file://")) {
            filePath = filePath.substring(7);
        }
        int degree = ImageUtil.readPictureDegree(filePath);
        bitmap = BitmapDecodeUtil.decodeBitmap(filePath);
        if (degree != 0) {
            bitmap = ImageUtil.rotateBitmap(bitmap, degree);
        }
        return bitmap;
    }



    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return degree;
        }
    }


}
