package com.willian.weibo.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.willian.weibo.R;
import com.willian.weibo.constant.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by willian on 2016/4/5.
 */
public class ImageUtil {

    public final static int REQUEST_CODE_FORM_CAMERA = 1;
    public final static int REQUEST_CODE_FORM_ALBUM = 2;

    public static Uri pictureUri;

    public static void choosePictureFromAlbum(Activity mActivity) {
        Intent mIntent = new Intent();
        mIntent.setAction(Intent.ACTION_PICK);
        mIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mActivity.startActivityForResult(mIntent, REQUEST_CODE_FORM_ALBUM);
    }

    public static void choosePictureFromCamera(Activity mActivity) {
        pictureUri = createPictureUri(mActivity);
        Intent mIntent = new Intent();
        mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        mActivity.startActivityForResult(mIntent, REQUEST_CODE_FORM_CAMERA);
    }

    /**
     * 创建一张图片的Uri，用于保存拍照后的照片
     *
     * @param context
     * @return
     */
    private static Uri createPictureUri(Context context) {
        String pictureName = "weibo" + System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, pictureName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, pictureName + ".jpeg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri mUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return mUri;
    }

    /**
     * 从缩略图url中截取末尾的图片id
     */
    public static String getPictureId(String thumbnail_pic) {
        int indexOf = thumbnail_pic.lastIndexOf("/") + 1;
        return thumbnail_pic.substring(indexOf);
    }

    /**
     * 从缩略图url中截取末尾的图片id,用于和拼接成其他质量图片url
     */
    public static String getBmiddlePic(String thumbnail_pic) {
        return Constant.BMIDDLE_URL + getPictureId(thumbnail_pic);
    }

    /**
     * 从缩略图url中截取末尾的图片id,用于和拼接成其他质量图片url
     */
    public static String getOriginalPic(String thumbnail_pic) {
        return Constant.ORIGINAL_URL + getPictureId(thumbnail_pic);
    }

    /**
     * 保存图片到SDCard指定目录
     *
     * @param context
     * @param dirName 目录名
     * @param bmp
     */
    public static void saveImageToAlbum(Context context, String dirName, Bitmap bmp) {
        // 未安装SD卡时不做保存
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showToast(context, context.getResources().getString(R.string.no_sdcard), Toast.LENGTH_SHORT);
            return;
        }

        File appDir = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // 无压缩保存
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(Constant.SD_CARD_PREFIX_URI + file.getAbsoluteFile())));
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);

        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmp;
    }
}
