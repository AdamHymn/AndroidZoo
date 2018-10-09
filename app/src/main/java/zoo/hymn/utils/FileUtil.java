package zoo.hymn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class FileUtil {

    public static File getCacheFile(Context context){
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            dir = context.getExternalCacheDir();
        }
        if (dir == null || !dir.exists()) {
            dir = context.getCacheDir();
        }
        if(dir != null){
            if(!dir.exists()){
                dir.mkdirs();
            }
        }
        return dir;
    }
    public static String encodeByBase64(String path) {
        if (!TextUtils.isEmpty(path)) {
            byte[] data = null;
            FileInputStream is = null;
            ByteArrayOutputStream os = null;
            try {
                is = new FileInputStream(path);
                os = new ByteArrayOutputStream();

                int len = 0;
                byte[] b = new byte[64 * 1024];
                while ((len = is.read(b)) != -1) {
                    os.write(b, 0, len);
                }
                os.flush();
                data = os.toByteArray();
            } catch (FileNotFoundException e) {
                Log.e("TAG", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            } finally {
                closeQuietly(is);
                closeQuietly(os);
            }

            if (data != null && data.length > 0) {
                System.out.println(data.length + "===============");
                return Base64.encodeToString(data, Base64.DEFAULT);
            }
        }
        return null;
    }


    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     * 创建文件夹
     */
    public static boolean createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
            return true;
        }
        return false;
    }


    /**
     * 删除一个文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            return false;
        }
        return file.delete();
    }

    /**
     * 删除一个文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     * @param dir
     */
    public static boolean deleteDir(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {// 递归删除文件内文件
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDir(file);
            }
        }
        dir.delete();// 再删除总文件
        return true;
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     * @param path
     */
    public static boolean deleteDir(String path) {
        return deleteDir(new File(path));
    }

    /**
     * 删除一个目录（可以是非空目录）或者文件
     *
     * @param dir
     */
    public static boolean deleteDirOrFile(File dir) {
        if (dir == null || !dir.exists()) {
            return false;
        }
        if (dir.isFile()) {// 文件处理
            dir.delete();
        } else if (dir.isDirectory()) {// 目录
            for (File file : dir.listFiles()) {// 递归删除文件内文件
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    deleteDir(file);
                }
            }
            dir.delete();
        }
        return true;
    }

    /**
     * 删除一个目录（可以是非空目录）或者文件
     *
     * @param path
     */
    public static boolean deleteDirOrFile(String path) {
        return deleteDirOrFile(new File(path));
    }

    /**
     * 检测文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExist(String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return false;
            }
            File file = new File(path);
            return file.exists();
        } catch (Exception ex) {
        }

        return false;
    }


    /**
     * 检测文件是否存在在指定目录
     *
     * @param mDire 指定目录
     * @param name  文件名称
     * @return
     */
    public static boolean isFileExist(String mDire, String name) {
        try {
            if (!TextUtils.isEmpty(mDire) && !TextUtils.isEmpty(name)) {
                File file = new File(mDire, name);
                return file.exists();
            }
        } catch (Exception ex) {
        }
        return false;
    }

    /**
     * 比较两个文件是否相同
     *
     * @param file1
     * @param file2
     * @return true 相同,false 不同
     */
    public static boolean isCompareFiles(File file1, File file2) {
        if (file1.isFile()
                && file2.isFile()
                && file1.getAbsolutePath().equalsIgnoreCase(
                file2.getAbsolutePath())// 文件路径
                && file1.getName().equalsIgnoreCase(file2.getName())// 比文件名
                && file1.length() == file2.length()// 比长度
                && file1.lastModified() == file2.lastModified()) {// 比修改日期.
            return true;
        }
        return false;
    }

    /**
     * 比较两个文件是否相同
     *
     * @param path1
     * @param path2
     * @return true 相同,false 不同
     */
    public static boolean isCompareFiles(String path1, String path2) {
        if (path1 == null || path2 == null) {
            return false;
        }
        if (path1.equalsIgnoreCase(path2)) {
            return true;
        } else {
            return isCompareFiles(new File(path1), new File(path2));
        }
    }

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @param srcFile  文件
     * @param destFile 文件、目录
     * @return
     */
    public static boolean copyFileTo(File srcFile, File destFile) {
        if (!srcFile.exists()) {
            return false;// 判断是否存在
        }
        File inputFile = srcFile;
        File outputFile = destFile;
        String fileName = srcFile.getName();
        if (srcFile.isDirectory()) {
            if (!destFile.isDirectory()) {
                return false;
            }
            String newPath = destFile.getPath() + "/" + fileName;
            createDir(newPath);
            copyFilesTo(srcFile.getPath(), newPath);
        }
        if (destFile.isDirectory() && destFile.exists()) {
            outputFile = new File(destFile.getPath() + "/" + fileName);
        } else if (destFile.getParent() == null) {
            return false;
        }
        try {
            FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            // /data/data/包名/files/目录下会创建名称为fileName文件
            // FileOutputStream fos = mContext.openFileOutput(fileName,
            // Context.MODE_WORLD_WRITEABLE);
            int readLen = 0;
            byte[] buf = new byte[4096];
            while ((readLen = fis.read(buf)) != -1) {
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            fos.close();
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @param srcPath  文件、目录的路径
     * @param destPath 文件、目录的路径
     * @return
     */
    public static boolean copyFileTo(String srcPath, String destPath) {
        return copyFileTo(new File(srcPath), new File(destPath));
    }

    /**
     * 拷贝目录下的所有文件到指定目录
     *
     * @param srcDir  资源目录
     * @param destDir 目的目录
     * @return
     */
    public static boolean copyFilesTo(File srcDir, File destDir) {
        if (!srcDir.exists()) {
            return false;// 不存在
        }
        if (!destDir.exists()) {
            destDir.mkdirs();// 4.3以上创建失败
        }
        if (!srcDir.isDirectory() || !destDir.isDirectory()) {
            return false;// 是文件
        }
        try {
            File[] srcFiles = srcDir.listFiles();
            String destDirPath = destDir.getPath();
            for (int i = 0; i < srcFiles.length; i++) {
                File file = srcFiles[i];
                String[] srcNames = file.getName().split("/");
                String srcName = srcNames[srcNames.length - 1];
                if (file.isFile()) {// 是文件
                    if (!isFileExist(destDirPath, srcName)) {// 目标目录文件不存在
                        File destFile = new File(destDirPath + "/"
                                + srcName);
                        if (isCompareFiles(file, destFile)) {// 没有修改
                        } else {
                            copyFileTo(file, destFile);
                        }
                    }
                } else if (file.isDirectory()) {// 是目录
                    File theDestDir = new File(destDirPath + "/" + srcName);
                    copyFilesTo(file, theDestDir);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param pContext 上下文
     * @param srcPath  源目录的路径
     * @param destPath 目的目录的路径
     * @return
     */
    public static boolean copyFilesTo(Context pContext, String srcPath,
                                      String destPath) {
        return copyFilesTo(new File(srcPath), new File(destPath));
    }

    /**
     * 拷贝目录下的所有文件到指定目录
     *
     * @param srcPath  源目录的路径
     * @param destPath 目的目录的路径
     * @return
     */
    public static boolean copyFilesTo(String srcPath, String destPath) {
        return copyFilesTo(new File(srcPath), new File(destPath));
    }

    /**
     * 移动一个文件、目录到一个新的文件夹
     *
     * @param srcFile  文件、目录
     * @param destFile 完整目录
     */
    public static boolean moveFileTo(File srcFile, File destFile) {
        // File (or directory) to be moved
        if (!srcFile.exists()) {
            return false;
        }
        // Destination directory
        if (!destFile.exists() || !destFile.isDirectory()) {
            return false;
        }
        // Move file to new directory
        boolean success = srcFile.renameTo(new File(destFile, srcFile
                .getName()));

        return success;
    }

    /**
     * 移动一个文件、目录到一个新的文件夹
     *
     * @param srcPath  文件、目录的路径
     * @param destPath 完整目录的路径
     */
    public static boolean moveFileTo(String srcPath, String destPath) {
        return moveFileTo(new File(srcPath), new File(destPath));
    }

    /**
     * 移动目录下的所有文件到指定目录
     *
     * @param srcDir  完整目录
     * @param destDir 完整目录
     * @return
     */
    public static boolean moveFilesTo(File srcDir, File destDir) {
        if (!srcDir.isDirectory() || !destDir.isDirectory()) {
            return false;
        }
        File[] srcDirFiles = srcDir.listFiles();
        for (int i = 0; i < srcDirFiles.length; i++) {
            moveFileTo(srcDirFiles[i], destDir);
        }
        return true;
    }

    /**
     * 移动目录下的所有文件到指定目录
     *
     * @param srcPath  完整目录的路径
     * @param destPath 完整目录的路径
     * @return
     */
    public static boolean moveFilesTo(String srcPath, String destPath) {
        return moveFilesTo(new File(srcPath), new File(destPath));
    }

    /**
     * 给文件或目录重命名
     *
     * @param targetFile 文件、目录
     * @param newName    新的文件名字（单个文件要记得加上后缀.xxx）
     * @return
     */
    public static boolean renameFile(File targetFile, String newName) {
        if (!targetFile.exists() || targetFile.getParentFile() == null) {
            return false;
        }
        boolean success = targetFile.renameTo(new File(targetFile
                .getParentFile(), targetFile.getName()));
        return success;
    }

    /**
     * 给文件或目录重命名
     *
     * @param targetPath 文件、目录的路径
     * @param newName    新的文件名字（单个文件要记得加上后缀.xxx）
     * @return
     */
    public static boolean renameFile(String targetPath, String newName) {
        return renameFile(new File(targetPath), newName);
    }


    /**
     * Description: 从输入流中获取文本
     *
     * @param inputStream 文本输入流
     * @return
     */
    public static String readTextFile(InputStream inputStream) {
        String readedStr = "";
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                readedStr += tmp;
            }
            br.close();
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        return readedStr;
    }


    /**
     * 获取文件的读写权限
     *
     * @param path
     * @return (-1)-不能访问,0-只读,1-读写,2-只写
     */
    public static int getFilePermission(String path) {
        if (!TextUtils.isEmpty(path)) {
            return getFilePermission(new File(path));
        }
        return -1;
    }

    /**
     * 获取文件的读写权限
     *
     * @param f
     * @return (-1)-不能访问,0-只读,1-读写,2-只写
     */
    public static int getFilePermission(File f) {
        // int intPermission = 0;
        // if (!f.canRead() && !f.canWrite()) {
        // intPermission = -1;
        // }
        // if (f.canRead()) {
        // if (f.canWrite()) {
        // intPermission = 1;
        // } else {
        // intPermission = 0;
        // }
        // }
        // return intPermission;

        if (f.canRead()) {
            if (f.canWrite()) {// 可读可写
                return 1;
            } else {// 只读
                return 0;
            }
        } else {// 不可读
            if (f.canWrite()) {// 只写
                return 2;
            } else {// 私有
                return -1;
            }
        }
    }

    /*** 获取文件个数 ***/
    public static int getFileCount(String path) {// 递归求取目录文件个数
        return getFileCount(new File(path));
    }

    /*** 获取文件个数 ***/
    public static int getFileCount(File f) {// 递归求取目录文件个数
        int size = 0;
        if (f.isDirectory()) {
            File flist[] = f.listFiles();
            size = flist.length;
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileCount(flist[i]);
                    size--;
                }
            }
        } else {
            size = 1;
        }
        return size;
    }

    /**
     * 获取文件大小 (单位：kb)
     *
     * @param path
     * @param boolFolderCount 是否统计文件夹大小（文件夹统计比较耗时）
     * @return 文件默认返回 0
     */
    public static long getFileSize(String path, boolean boolFolderCount) {
        return getFileSize(new File(path), boolFolderCount);
    }

    /**
     * 获取文件大小 (单位：kb)
     *
     * @param f
     * @param boolFolderCount 是否统计文件夹大小
     *                        （文件夹统计比较耗时）
     * @return 文件默认返回 0
     */
    public static long getFileSize(File f, boolean boolFolderCount) {
        long size = 0;
        try {
            if (f.isFile()) {// 文件处理
                if (f.exists()) {// 存在
                    size = f.length();
                }
            } else {// 文件夹处理
                if (boolFolderCount) {
                    File flist[] = f.listFiles();
                    if (flist != null && flist.length > 0) {
                        for (File mf : flist) {
                            if (mf.isDirectory()) {
                                size = size
                                        + getFileSize(mf, boolFolderCount);
                            } else {
                                size = size + mf.length();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size / 1024;
    }

    /**
     * 获取文件大小 (单位：b)
     *
     * @param f
     * @param boolFolderCount 是否统计文件夹大小
     *                        （文件夹统计比较耗时）
     * @return 文件默认返回 0
     */
    public static long getFileSize2(File f, boolean boolFolderCount) {
        long size = 0;
        try {
            if (f.isFile()) {// 文件处理
                if (f.exists()) {// 存在
                    size = f.length();
                }
            } else {// 文件夹处理
                if (boolFolderCount) {
                    File flist[] = f.listFiles();
                    if (flist != null && flist.length > 0) {
                        for (File mf : flist) {
                            if (mf.isDirectory()) {
                                size = size
                                        + getFileSize(mf, boolFolderCount);
                            } else {
                                size = size + mf.length();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /*** 转换文件大小单位(B/KB/MB/GB) ***/
    public static String formatFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < (1024 * 1024)) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < (1024 * 1024 * 1024)) {
            fileSizeString = df.format((double) fileS / (1024 * 1024))
                    + "MB";
        } else {
            fileSizeString = df.format((double) fileS
                    / (1024 * 1024 * 1024))
                    + "GB";
        }
        return fileSizeString;
    }

    /**
     * Description: 获得手机内置sd卡总大小
     *
     * @param pContext
     * @param mPath
     * @return
     */
    private static String getTotalSize(Context pContext, String mPath) {
        StatFs stat = new StatFs(mPath);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(pContext, blockSize * totalBlocks);
    }

    /**
     * Description: 获得手机内置sd卡可用大小
     *
     * @param pContext
     * @param mPath
     * @return
     */
    private static String getAvailableSize(Context pContext, String mPath) {
        StatFs stat = new StatFs(mPath);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(pContext, blockSize
                * availableBlocks);
    }

    /**
     * 获得手机内置sd卡总大小(三星 4.0之后的 /storage/sdcard0) 或者获得手机外置sd卡SD卡总大小(如三星 4.0之后的
     * /storage/extSdCard)
     * <p>
     * 注意：4.0之前手机内置sd卡不包含手机内存，只含标准sd容量；4.0之后怎么包含这两个
     *
     * @param pPath 如：/storage/sdcard0，/storage/extSdCard
     * @return
     */
    public static String getInOrOutSDTotalSize(Context pContext,
                                               String pPath) {
        return getTotalSize(pContext, pPath);
    }

    /**
     * 获得手机内置sd卡剩余容量，即可用大小 或者获得手机外置sd卡剩余容量，即可用大小
     * <p>
     * 如：/storage/sdcard0，/storage/extSdCard
     *
     * @return
     */
    public static String getInOrOutSDAvailableSize(Context pContext,
                                                   String path) {
        return getAvailableSize(pContext, path);
    }

    /**
     * 获得机身内存总大小(/data)
     *
     * @return
     */
    public static String getRomTotalSize(Context pContext) {
        File path = Environment.getDataDirectory();
        String mPath = path.getPath();
        return getTotalSize(pContext, mPath);
    }

    /**
     * 获得机身剩余内存
     *
     * @return
     */
    public static String getRomAvailableSize(Context pContext) {
        File path = Environment.getDataDirectory();
        String mPath = path.getPath();
        return getAvailableSize(pContext, mPath);
    }

    /**
     * 比较SD容量是否够需要
     *
     * @param phoneCapacity 盘符的大小
     * @return true 够用；false 不够用
     */
    public static boolean compareSize(String phoneCapacity) {
        String unit = phoneCapacity.substring(phoneCapacity.length() - 2)
                .trim();
        String size = phoneCapacity
                .substring(0, phoneCapacity.length() - 2).trim();
        if ("GB".equalsIgnoreCase(unit)) {
            return true;
        } else if ("MB".equalsIgnoreCase(unit)) {
            int mSize = Math.round(Float.parseFloat(size));
            if (size != null && mSize > 100) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取文件名
     *
     * @param file
     * @return
     */
    public static String getFileName(File file) {
        if (file == null) {
            return "";
        }
        return file.getName();
    }

    /**
     * 获取文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        if (!TextUtils.isEmpty(path)) {
            return getFileName(new File(path));
        } else {
            return null;
        }
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param file
     * @return
     */
    public static String getFileNameNoExtension(File file) {
        if (file == null) {
            return "";
        }
        String filename = file.getName();
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param path
     * @return
     */
    public static String getFileNameNoExtension(String path) {
        return getFileNameNoExtension(new File(path));
    }

    /**
     * 获取文件扩展名(不包含前面那个.)
     *
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }
        String filename = file.getName();
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 获取文件扩展名(不包含前面那个.)
     *
     * @param path
     * @return
     */
    public static String getFileExtension(String path) {
        return getFileExtension(new File(path));
    }

    /**
     * <b>通过文件file对象获取文件标识MimeType</b><br>
     * <br>
     * MIME type的缩写为(Multipurpose Internet Mail
     * Extensions)代表互联网媒体类型(Internet media type)，
     * MIME使用一个简单的字符串组成，最初是为了标识邮件Email附件的类型，在html文件中可以使用content-type属性表示，
     * 描述了文件类型的互联网标准。MIME类型能包含视频、图像、文本、音频、应用程序等数据。
     *
     * @param file
     * @return
     */
    public static String getFileMimeTypeFromFile(File file) {
        String extension = getFileExtension(file);
        extension = extension.replace(".", "");
        if ("docx".equals(extension) || "wps".equals(extension)) {
            extension = "doc";
        } else if ("xlsx".equals(extension)) {
            extension = "xls";
        } else if ("pptx".equals(extension)) {
            extension = "ppt";
        }
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (mimeTypeMap.hasExtension(extension)) {
            // 获得txt文件类型的MimeType
            return mimeTypeMap.getMimeTypeFromExtension(extension);
        } else {
            if ("dwg".equals(extension)) {
                return "application/x-autocad";
            } else if ("dxf".equals(extension)) {
                return "application/x-autocad";
            } else if ("ocf".equals(extension)) {
                return "application/x-autocad";
            } else {
                return "*/*";
            }
        }
    }

    /**
     * <b>通过文件的扩展名Extension获取文件标识MimeType</b><br>
     * <br>
     * MIME type的缩写为(Multipurpose Internet Mail
     * Extensions)代表互联网媒体类型(Internet media type)，
     * MIME使用一个简单的字符串组成，最初是为了标识邮件Email附件的类型，在html文件中可以使用content-type属性表示，
     * 描述了文件类型的互联网标准。MIME类型能包含视频、图像、文本、音频、应用程序等数据。
     *
     * @param extension
     * @return
     */
    public static String getFileMimeTypeFromExtension(String extension) {
        extension = extension.replace(".", "");
        if ("docx".equals(extension) || "wps".equals(extension)) {
            extension = "doc";
        } else if ("xlsx".equals(extension)) {
            extension = "xls";
        } else if ("pptx".equals(extension)) {
            extension = "ppt";
        }
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (mimeTypeMap.hasExtension(extension)) {
            // 获得txt文件类型的MimeType
            return mimeTypeMap.getMimeTypeFromExtension(extension);
        } else {
            if ("dwg".equals(extension)) {
                return "application/x-autocad";
            } else if ("dxf".equals(extension)) {
                return "application/x-autocad";
            } else if ("ocf".equals(extension)) {
                return "application/x-autocad";
            } else {
                return "*/*";
            }
        }
    }

    /**
     * <b>通过文件标识MimeType获取文件的扩展名Extension</b><br>
     * <br>
     * MIME type的缩写为(Multipurpose Internet Mail
     * Extensions)代表互联网媒体类型(Internet media type)，
     * MIME使用一个简单的字符串组成，最初是为了标识邮件Email附件的类型，在html文件中可以使用content-type属性表示，
     * 描述了文件类型的互联网标准。MIME类型能包含视频、图像、文本、音频、应用程序等数据。
     *
     * @return
     */
    public static String getFileExtensionFromMimeType(String mimeType) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (mimeTypeMap.hasMimeType(mimeType)) {
            // 获得"text/html"类型所对应的文件类型如.txt、.jpeg
            return mimeTypeMap.getExtensionFromMimeType(mimeType);
        } else {
            return "";
        }
    }

    /**
     * 获取gmail附件的名称和大小
     *
     * @param context
     * @param documentUri
     * @return
     */
    public static String getAttachmetName(Context context, Uri documentUri) {
        if (null != documentUri) {
            final String uriString = documentUri.toString();
            String documentFilename = null;

            final int mailIndexPos = uriString.lastIndexOf("/attachments");
            if (mailIndexPos != -1) {
                final Uri curi = documentUri;
                final String[] projection = new String[]{OpenableColumns.DISPLAY_NAME};
                final Cursor cursor = context.getContentResolver().query(
                        curi, projection, null, null, null);
                if (cursor != null) {
                    final int attIdx = cursor
                            .getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (attIdx != -1) {
                        cursor.moveToFirst();
                        documentFilename = cursor.getString(attIdx);
                    }
                    cursor.close();
                }
            }
            return documentFilename;
        }
        return null;
    }

    /**
     * 获取Gmail附件的路径
     *
     * @param uri
     * @return
     */
    public static String getGamiFilePath(Context context, Uri uri) {
        String strFileName = getAttachmetName(context, uri);
        String strAbsolutePath = getSystemFilesPath(context) + File.separator + strFileName;
        try {
            InputStream attachment = context.getContentResolver()
                    .openInputStream(uri);
            if (attachment == null) {
                Log.e("onCreate", "cannot access mail attachment");
            } else {
                FileOutputStream tmp = new FileOutputStream(strAbsolutePath);
                byte[] buffer = new byte[1024];
                while (attachment.read(buffer) > 0) {
                    tmp.write(buffer);
                }

                tmp.close();
                attachment.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            strAbsolutePath = "";
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            strAbsolutePath = "";
            e.printStackTrace();
        }
        return strAbsolutePath;
    }

    /**
     * 获取文件的绝对路径，相应地可以改成其他多媒体类型如audio等等
     *
     * @param context 必须是Activity的实例
     * @param uri
     * @return
     */
    public static String getAbsoluteImagePath(Context context, Uri uri) {
        String strAbsolutePath = "";
        String[] proj = {MediaStore.MediaColumns.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = ((Activity) context).managedQuery(uri, proj, null,
                null, null);
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        strAbsolutePath = cursor.getString(column_index);
        cursor.close();
        return strAbsolutePath;
    }

    /**
     * 获得应用系统文件目录
     *
     * @param context
     * @return /data/data/{package name}/files/
     */
    public static String getSystemFilesPath(Context context) {
        return context.getFilesDir().getPath() + File.separator;
    }

    /**
     * Description: 系统文件中的缓存目录路径
     *
     * @return /data/data/{package name}/cache/
     */
    public static String getSystemCachePath(Context context) {
        return context.getCacheDir().getPath() + File.separator;
    }


    public static String readJSONFromAssets(Context mContext, String filePath) {
        StringBuffer sb = new StringBuffer();
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        String buffLine = "";
        try {
            read = new InputStreamReader(mContext.getAssets().open(filePath));
            bufferedReader = new BufferedReader(read);
            while ((buffLine = bufferedReader.readLine()) != null) {
                sb.append(buffLine);
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (read != null) {
                    read.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * Description: 获取Assets中的json文本
     *
     * @param context
     * @param name    文本名称
     * @return
     */
    public static String getJson(Context context, String name) {
        if (name != null) {
            String path = "json/" + name;
            InputStream is = null;
            try {
                is = context.getAssets().open(path);
                return readTextFile(is);
            } catch (IOException e) {
                return null;
            } finally {
                try {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                } catch (IOException e) {

                }
            }
        }
        return null;
    }

    /**
     * 复制Asset目录下的文件到指定文件夹
     * <p>
     * 通过比较Asset和本地文件的最近修改时间或者大小来比较是否需要修改
     *
     * @param ctx          上下文
     * @param fromFileName 文件名
     * @param toFilePath   应用文件保存路径
     * @return true 保存成功；false 保存失败
     */
    public static boolean copyAssetFile(Context ctx, String fromFileName,
                                        String toFilePath) {
        String mFilePath = toFilePath + fromFileName;
        // 本地是不存在直接执行复制
        if (!isFileExist(mFilePath)) {
            return copyAssetFileToLocal(ctx, fromFileName, mFilePath);
        } else {
            try {
                // 通过发布时填写的文件属性，来作为文 对应Assets文件的最近修改时间
                // 这个做法比list获得文件列表效率高
                InputStream inputStream = ctx.getAssets().open(
                        "assetsFilesLog.properties");
                Properties properties = new Properties();
                properties.load(inputStream);
                String mFromModifiedTime = properties
                        .getProperty(fromFileName);
                // Assets相关文件的大小
                InputStream mfrom = ctx.getAssets().open(fromFileName);
                long mFromSize = mfrom.available();

                File toFile = new File(mFilePath);
                // 文件的最近修改时间
                long mLocalModified = toFile.lastModified();
                String mLocalModifiedTime = String.valueOf(mLocalModified);
                // 本地相关文件的大小
                long mLocalSize = toFile.length();
                // 不相同就删除后直接复制
                if ((mFromSize != mLocalSize)
                        || (mFromModifiedTime != mLocalModifiedTime)) {
                    boolean isDele = deleteFile(mFilePath);
                    if (isDele) {
                        return copyAssetFileToLocal(ctx, fromFileName,
                                mFilePath);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 复制Asset目录下的文件到指定文件夹
     *
     * @param ctx          上下文
     * @param fromFileName 文件名
     *                     应用文件保存路径
     * @return true 保存成功；false 保存失败
     */
    private static boolean copyAssetFileToLocal(Context ctx,
                                                String fromFileName, String mFilePath) {
        try {
            InputStream fosfrom = ctx.getAssets().open(fromFileName);
            OutputStream fosto = new FileOutputStream(mFilePath);
            byte bt[] = new byte[4096];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            bt = null;
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    static String mCodePath = null;

    /**
     * 复制Asset目录下的所有文件到指定文件夹
     *
     * @param ctx       上下文
     * @param assetDir  asset下文件目录
     * @param dir       应用文件保存路径
     * @param pCodePath 应用文件保存路径
     *                  SD卡分区路径，也可能是mmcblk1随系统版本定，当前程序路径请用getPackageCodePath();
     * @return true 保存成功；false 保存失败
     */
    public static void copyAssetsDir(Context ctx, String assetDir,
                                     String dir, String pCodePath) {
        mCodePath = pCodePath;
        copyAssetsDir(ctx, assetDir, dir);
    }

    /**
     * 复制Asset目录下的所有文件到指定文件夹
     *
     * @param ctx      上下文
     * @param assetDir asset下文件目录
     * @param dir      应用文件保存路径
     * @return true 保存成功；false 保存失败
     */
    public static void copyAssetsDir(Context ctx, String assetDir,
                                     String dir) {
        String[] files;
        try {
            files = ctx.getResources().getAssets().list(assetDir);// 获得路径下的所有文件和文件夹
        } catch (IOException e1) {
            return;
        }
        File mWorkingPath = new File(dir);
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        } else {
            //如果文件存在就不需要copy
            //return;
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];// 文件或者文件夹名称
                Log.e("fileName", assetDir + "/" + fileName + "=fileName");
                if (new File(fileName).isDirectory()) {// 为目录
                    String mStr = dir + fileName + "/";
                    if (0 == assetDir.length()) {// 空目录
                        copyAssetsDir(ctx, fileName, mStr);
                    } else {
                        copyAssetsDir(ctx, assetDir + "/" + fileName, mStr);
                    }
                    continue;// 结束本次循环
                } else {// 是个文件
                    boolean isExist = isFileExist(dir, fileName);
                    if (isExist) {// 本地存在
                        continue;
                    }
                }
                File outFile = new File(mWorkingPath, fileName);
                // if (outFile.exists())
                // outFile.delete();
                InputStream in = null;
                if (0 != assetDir.length()) {
                    in = ctx.getAssets().open(assetDir + "/" + fileName);
                } else {// 空目录
                    in = ctx.getAssets().open(fileName);
                }
                OutputStream out = new FileOutputStream(outFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 出发扫描 mtp下的文件，在保存文件到 sd卡下后，不能显示，故这里触发一下扫描机制，让手机连上电脑后，就可以读出文件了
     * @param fName，文件的完整路径名
     */
    public static void  fileScan(Context context,String fName){
        Uri data = Uri. parse("file:///" +fName);
        context.sendBroadcast( new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE , data));
    }

    public void saveToSD(Context context,String content, String name,boolean isAppend) {
        File file = new File(FileUtil.getCacheFile(context) + "/" + name);
        try {
            file.createNewFile(); // 创建文件
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file, isAppend);
            fout.write(content.getBytes());
            fout.flush();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<String> readId2Constants(Context context, String path) {
        String content = readFileContent(context,path);
        List<String> list = new ArrayList<>();
        if (content != null) {
            String[] strs = content.split("#kk#");
            for (int j = 0; j < strs.length; j++) {
                list.add(strs[j]);
            }
        }
        return list;
    }

    private String readFileContent(Context context,String filePath) {
        String encoding = "UTF-8";
        StringBuffer sb = new StringBuffer();
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        String line = "";
        try {
            File file = new File(FileUtil.getCacheFile(context) + "/" + filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                bufferedReader = new BufferedReader(read);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (read != null) {
                    read.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
