package moe.xing.baseutils.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.R;
/**
 * Created by Hehanbo on 2016/7/26 0026.
 * <p>
 * 文件相关帮助类
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FileUtils {

    /**
     * 获取新的缓存文件(优先外部)
     *
     * @param name 文件名
     * @return 文件
     * @throws IOException 文件无法创建或者名称对应的不是文件
     */
    @NonNull
    public static File getCacheFile(@NonNull String name) throws IOException {
        File cacheFile = new File(getCacheDir(), name);
        if (!cacheFile.getParentFile().exists()) {
            cacheFile.getParentFile().mkdirs();
        }

        cacheFile.createNewFile();
        if (!cacheFile.exists() || !cacheFile.isFile()) {
            throw new IOException(Init.getApplication().getString(R.string.error_in_make_file));
        }
        return cacheFile;
    }

    /**
     * 获取缓存文件夹下的子文件夹
     *
     * @param name 文件夹名
     * @return 文件夹
     * @throws IOException 文件夹无法创建
     */
    public static File getCacheDir(@NonNull String name) throws IOException {
        File cacheFile = new File(getCacheDir(), name);
        if (!cacheFile.exists()) {
            if (!cacheFile.mkdirs()) {
                throw new IOException(Init.getApplication().getString(R.string.error_in_make_dir));
            }
        }
        return cacheFile;
    }

    /**
     * 外置储存区是否存在
     *
     * @return <code>true</code>存在
     * <code>false</code>不存在
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 获取缓存文件夹(总文件夹)
     *
     * @return 缓存文件夹(优先外置)
     */
    @NonNull
    public static File getCacheDir() {
        if (isExternalStorageWritable()) {
            //noinspection ConstantConditions
            return Init.getApplication().getExternalCacheDir();
        } else {
            return Init.getApplication().getCacheDir();
        }
    }

    /**
     * 复制文件
     *
     * @param src 源文夹
     * @param dst 复制到的文件
     * @throws IOException
     */
    @WorkerThread
    public static void CopyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        CopyFile(inStream, dst);
    }

    /**
     * 复制文件
     *
     * @param src 源文夹
     * @param dst 复制到的文件
     * @throws IOException
     */
    @WorkerThread
    public static void CopyFile(FileDescriptor src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        CopyFile(inStream, dst);
    }

    /**
     * 复制文件
     *
     * @param inStream 源文夹流
     * @param dst      复制到的文件
     * @throws IOException
     */
    @WorkerThread
    public static void CopyFile(FileInputStream inStream, File dst) throws IOException {
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    /**
     * 将 Asset 文件拷贝到缓存
     *
     * @param filename 文件名
     * @return 被储存的文件
     * @throws IOException 文件无法创建或者名称对应的不是文件,assert 无法打开,写入无法终止等
     */
    @WorkerThread
    @NonNull
    protected static File copyAssetFile(@NonNull String filename) throws IOException {
        AssetManager assetManager = Init.getApplication().getAssets();

        File dst = getCacheFile(filename);


        InputStream in = assetManager.open(filename);
        OutputStream out = new FileOutputStream(dst);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();

        return dst;
    }

    /**
     * 将 stream 转换为 string
     */
    protected static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     * 将 string 写入 FOS
     */
    protected static void writeToFile(String data, FileOutputStream fos) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }

    /**
     * 从 Url 获取文件名
     * 移除 ? 后的所有内容
     * 将 % 转换为 _
     *
     * @param url 文件的 Url
     * @return 文件名
     */
    @NonNull
    public static String getFileNameFromUrl(@NonNull String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        int lastBackslash = url.lastIndexOf("/");
        if (lastBackslash == -1 || lastBackslash >= url.length() + 1) {
            return "noName";
        }
        url = url.substring(url.lastIndexOf("/") + 1).replace("%", "_");

        return url;
    }

}
