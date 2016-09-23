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
import java.util.Map;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.R;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Hehanbo on 2016/7/26 0026.
 * <p>
 * 文件相关帮助类
 */

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
     * 将文件拷贝至外置缓存区的 {@link rx.Observable.Operator}
     */
    @WorkerThread
    public static Observable.Operator<File, File> copyFileToExCache() {
        return new Observable.Operator<File, File>() {
            @Override
            public Subscriber<? super File> call(final Subscriber<? super File> subscriber) {
                return new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onNext(File file) {
                        if (!FileUtils.isExternalStorageWritable()) {
                            subscriber.onError(new Throwable(Init.getApplication()
                                    .getString(R.string.external_disk_not_exits)));
                        }
                        try {
                            File dst = getCacheFile(file.getName());
                            CopyFile(file, dst);
                            subscriber.onNext(dst);
                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                };
            }
        };
    }

    /**
     * 将 Asset 文件拷贝到缓存
     *
     * @param fileName 文件名
     */
    @WorkerThread
    public static Observable<File> copyAsset(final String fileName) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    subscriber.onNext(copyAssetFile(fileName));
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @WorkerThread
    public static Observable<File> SaveString(final String s) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                File out = null;
                try {
                    out = getCacheFile("preview.html");
                    FileOutputStream fos = new FileOutputStream(out);
                    writeToFile(s, fos);
                    fos.close();
                    subscriber.onNext(out);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    @WorkerThread
    private static File copyAssetFile(String filename) throws IOException {
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

    @WorkerThread
    public static Observable<File> replaceStringsInfile(final File file, final Map<String, String> replace) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    String content = convertStreamToString(fis);

                    for (Map.Entry<String, String> entry : replace.entrySet()) {
                        content = content.replaceAll(entry.getKey(), entry.getValue());
                    }
                    File out = getCacheFile("preview-" + file.getName());
                    FileOutputStream fos = new FileOutputStream(out);
                    writeToFile(content, fos);
                    fis.close();
                    fos.close();
                    subscriber.onNext(out);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static void writeToFile(String data, FileOutputStream fos) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
        outputStreamWriter.write(data);
        outputStreamWriter.close();
    }

    /**
     * 从 Url 获取文件名
     *
     * @param url 文件的 Url
     * @return 文件名
     */
    @NonNull
    public static String getFileNameFromUrl(@NonNull String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
