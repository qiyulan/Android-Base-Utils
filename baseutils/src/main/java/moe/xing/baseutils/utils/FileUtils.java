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
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Hehanbo on 2016/7/26 0026.
 */

public class FileUtils {

    public static File getCacheFile(String name) throws IOException {
        File cacheFile = new File(getCacheDir(), name);
        if (!cacheFile.getParentFile().exists()) {
            cacheFile.getParentFile().mkdirs();
        }

        cacheFile.createNewFile();
        if (!cacheFile.exists() || !cacheFile.isFile()) {
            throw new IOException("建立文件出错");
        }
        return cacheFile;
    }

    public static File getCacheDir(String name) throws IOException {
        File cacheFile = new File(getCacheDir(), name);
        if (!cacheFile.exists()) {
            if (!cacheFile.mkdirs()) {
                throw new IOException("error in make dir");
            }
        }
        return cacheFile;
    }


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getCacheDir() {
        if (isExternalStorageWritable()) {
            return Init.getApplication().getExternalCacheDir();
        } else {
            return Init.getApplication().getCacheDir();
        }
    }

    @WorkerThread
    public static void CopyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        CopyFile(inStream, dst);
    }

    @WorkerThread
    public static void CopyFile(FileDescriptor src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        CopyFile(inStream, dst);
    }

    @WorkerThread
    public static void CopyFile(FileInputStream inStream, File dst) throws IOException {
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

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
                            subscriber.onError(new Throwable("外置储存区不可用,无法分享图片"));
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
