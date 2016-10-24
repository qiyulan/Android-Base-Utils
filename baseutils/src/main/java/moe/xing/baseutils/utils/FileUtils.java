package moe.xing.baseutils.utils;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
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
import rx.functions.Action1;

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
     * 将文件拷贝至外置缓存区的 {@link rx.Observable.Operator}
     *
     * @return Observable.Operator
     */
    @WorkerThread
    @NonNull
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
     * @return Observable<File>
     */
    @WorkerThread
    @NonNull
    public static Observable<File> copyAsset(@NonNull final String fileName) {
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

    /**
     * 将指定的字符串存入缓存
     *
     * @param s        要被储存的字符串
     * @param filename 被储存的文件(原来是 preview.html)
     * @return Observable<File>
     */
    @WorkerThread
    @NonNull
    public static Observable<File> SaveString(@NonNull final String s, @NonNull final String filename) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                File out = null;
                try {
                    out = getCacheFile(filename);
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
     * 替换文件中的字符
     *
     * @param file    要求替换字符的文件
     * @param replace 替换的 map
     * @return Observable<File>
     */
    @NonNull
    @WorkerThread
    public static Observable<File> replaceStringsInfile(@NonNull final File file, @NonNull final Map<String, String> replace) {
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

    /**
     * 从 Uri 获取文件
     *
     * @param context 有权限的 context
     * @param uri     文件的 Uri
     * @return Observable<File>
     */
    @NonNull
    public static Observable<File> getFileUrlWithAuthority(@NonNull final Context context, @NonNull final Uri uri) {

        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(final Subscriber<? super File> subscriber) {
                InputStream is = null;
                OutputStream os = null;
                File file = null;
                if (uri.getAuthority() != null) {
                    try {
                        is = context.getContentResolver().openInputStream(uri);

                        file = FileUtils.getCacheFile(FileUtils.getFileNameFromUrl(uri.toString()));

                        os = new FileOutputStream(file);
                        byte[] buf = new byte[1024 * 8];
                        int len;
                        if (is != null) {
                            while ((len = is.read(buf)) != -1) {
                                os.write(buf, 0, len);
                            }
                        }
                        os.flush();

                    } catch (final IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } catch (SecurityException e) {
                        RxPermissions.getInstance(context).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        subscriber.onError(new Throwable("请再选择一次"));
                                    }
                                });
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (os != null) {
                                os.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (file != null) {
                    subscriber.onNext(file);
                } else {
                    subscriber.onError(new Throwable("获取文件时出错"));
                }
            }
        });
    }

}
