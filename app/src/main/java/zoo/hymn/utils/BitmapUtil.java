package zoo.hymn.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    //把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * 描述：获取src中的图片资源.
     *
     * @param src 图片的src路径，如（“image/arrow.png”）
     * @return Bitmap 图片
     */
    public static Bitmap getBitmapFromSrc(String src) {
        Bitmap bit = null;
        try {
            bit = BitmapFactory.decodeStream(BitmapUtil.class.getResourceAsStream(src));
        } catch (Exception e) {
            Log.d("FileUtil.class", "获取图片异常：" + e.getMessage());
        }
        return bit;
    }

    // 以最省内存的方式读取本地资源的图片
    public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
        return bmp;
    }

    // 将Bitmap对象转换为文件对象
    public static File saveBitmapToFile(Context context, Bitmap bmp) {
        if (bmp == null || bmp.isRecycled()) {
            return null;
        }
        File dir = FileUtil.getCacheFile(context);
        File avatarFolder = new File(dir, "selectPhotos");
        boolean exist = avatarFolder.exists();
        if (!exist) {
            exist = avatarFolder.mkdirs();
        }

        if (exist) {
            File avatar = new File(avatarFolder, System.currentTimeMillis() + ".jpg");
            OutputStream os = null;
            try {
                os = new FileOutputStream(avatar);

                do{
                    bmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
                }while (avatar.length()/1024 > 1024);

                return avatar;
            } catch (FileNotFoundException e) {

            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return null;
    }


    /**
     * 根据图片的网络地址url路径获得Bitmap对象
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromUrl(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {


        /**
         *
         Intrinsic 内部的，内在的
         Opacity 不透明性
         OPAQUE opaque 不透明

         在Android的Bitmap.Config中有四个枚举类型：ALPHA_8、ARGB_4444、ARGB_8888和RGB_565

         下面是这四种类型的详细解释：

         ALPHA_8：每个像素都需要1（8位）个字节的内存，只存储位图的透明度，没有颜色信息

         ARGB_4444：A(Alpha)占4位的精度，R(Red)占4位的精度，G(Green)占4位的精度，B（Blue）占4位的精度，加起来一共是16位的精度，折合是2个字节，也就是一个像素占两个字节的内存，同时存储位图的透明度和颜色信息。不过由于该精度的位图质量较差，官方不推荐使用

         ARGB_8888：这个类型的跟ARGB_4444的原理是一样的，只是A,R,G,B各占8个位的精度，所以一个像素占4个字节的内存。由于该类型的位图质量较好，官方特别推荐使用。但是，如果一个480*800的位图设置了此类型，那个它占用的内存空间是：480*800*4/(1024*1024)=1.5M

         RGB_565：同理，R占5位精度，G占6位精度，B占5位精度，一共是16位精度，折合两个字节。这里注意的时，这个类型存储的只是颜色信息，没有透明度信息

         //按指定参数创建一个空的Bitmap对象
         */
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.e(TAG, "calculateInSampleSize: 原图高：" + height);
        Log.e(TAG, "calculateInSampleSize: 原图宽：" + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            Log.e(TAG, "calculateInSampleSize: 高比例：" + heightRatio);
            Log.e(TAG, "calculateInSampleSize: 宽比例：" + widthRatio);
            //取较小的值
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmapByMatrix(Context context,Uri uri) throws FileNotFoundException {
        Bitmap bit = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));

        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        Bitmap bm = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);
        Log.i("getSmallBitmapByMatrix", "压缩后图片的大小" + (bm.getByteCount() / 1024 / 1024)
                + "M宽度为" + bm.getWidth() + "高度为" + bm.getHeight());
        return bm;
    }
    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize 是2的次方。取1,4,8,16。。。
//        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inSampleSize = calculateInSampleSize(options, 720, 1280);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
     *
     * @param filePath
     * @param outFile
     * @return
     * @throws FileNotFoundException
     */
    public static File compressImage(String filePath, String outFile) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filePath);

        int degree = readPictureDegree(filePath);
        Log.e(TAG, "degree：" + degree);

        if (degree != 0) {//旋转照片角度
            bm = rotateBitmap(bm, degree);
        }

//        File imageDir = Environment.getExternalStorageDirectory();

        File outputFile = new File(outFile);

        FileOutputStream out = new FileOutputStream(outputFile);

        if(bm != null) {
            do {
                bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } while (outputFile.length() / 1024 > 1024);
        }
        return outputFile;
    }
    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
     *
     * @param oriUri
     * @param outFile
     * @return
     * @throws FileNotFoundException
     */
    public static File compressImage(Context context,Uri oriUri, String outFile) throws FileNotFoundException {

        Bitmap bm = null;
        int degree = 0 ;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bm = getSmallBitmapByMatrix(context, oriUri);
            degree = readPictureDegree(context, oriUri);
        }else {
            bm = getSmallBitmap(oriUri.getPath());
            degree = readPictureDegree(oriUri.getPath());
        }
        Log.e(TAG, "degree：" + degree);

        if (degree != 0) {//旋转照片角度
            bm = rotateBitmap(bm, degree);
        }

//        File imageDir = Environment.getExternalStorageDirectory();

        File outputFile = new File(outFile);

        FileOutputStream out = new FileOutputStream(outputFile);

        if(bm != null) {
            do {
                bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } while (outputFile.length() / 1024 > 1024);
        }
        return outputFile;
    }
    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
     *
     * @param context
     * @param filePath
     * @param fileName
     * @param q
     * @return
     * @throws FileNotFoundException
     */
    public static File compressImage(Context context, String filePath, String fileName, int q) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filePath);

        int degree = readPictureDegree(filePath);
        Log.e(TAG, "degree：" + degree);

        if (degree != 0) {//旋转照片角度
            bm = rotateBitmap(bm, degree);
        }

        File imageDir = Environment.getExternalStorageDirectory();

        File outputFile = new File(imageDir, fileName);

        FileOutputStream out = new FileOutputStream(outputFile);

        bm.compress(Bitmap.CompressFormat.JPEG, q, out);

        return outputFile;
    }

    /**
     * 判断照片角度
     * @param context
     * @param uri
     * @return
     */

    public static int readPictureDegree(Context context,Uri uri) throws FileNotFoundException {
        int degree = 0;
        try {
            if(Build.VERSION.SDK_INT >= 24) {
                ExifInterface exifInterface = new ExifInterface(context.getContentResolver().openInputStream(uri));
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /**
     * 判断照片角度
     *
     * @param path
     * @return
     */

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
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degress
     * @return
     */


    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 转圆形图片
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }


    public static Bitmap overrideBitmap(Bitmap bitmap,float outWidth,float outHeight){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = outWidth/width;
        float scaleHeight = outHeight/width;

        // 取得想要缩放的matrix參數
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的圖片
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);
        return newbm;
    }

}
