package moe.xing.baseutils.utils;

import android.Manifest;
import android.content.Context;
import android.net.Uri;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Hehanbo on 2016/8/3 0003.
 */


@SuppressWarnings({"WeakerAccess", "unused"})
public class ImageUtils {

    /*get Image Path*/
    public static Observable<String> getImageUrlWithAuthority(final Context context, final Uri uri) {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                InputStream is = null;
                OutputStream os = null;
                File file = null;
                if (uri.getAuthority() != null) {
                    try {
                        is = context.getContentResolver().openInputStream(uri);

                        file = FileUtils.getCacheFile(getImageFileName(uri));

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
                    subscriber.onNext(file.getAbsolutePath());
                } else {
                    subscriber.onError(new Throwable("获取文件时出错"));
                }
            }
        });
    }


    /**
     * 获取url对应文件的文件名
     */
    private static String getImageFileName(Uri uri) {
        String fileName = getFileName(uri.toString());
        if (!fileName.contains(".")) {
            fileName += ".jpg";
        }
        fileName = fileName.replace("%", "_");
        return fileName;
    }

    /**
     * 从地址获取文件名
     *
     * @param filePath 文件地址
     * @return 文件名
     */
    public static String getFileName(final String filePath) {
        String file;
        if (filePath.contains("?")) {
            file = filePath.substring(0, filePath.indexOf("?"));
        } else {
            file = filePath;
        }
        int last = file.lastIndexOf("/");
        file = file.substring(last + 1).replace("%", "_");
        final String typeStart = "format/";
        if (filePath.contains(typeStart)) {
            int start = filePath.indexOf(typeStart) + typeStart.length();
            String type = filePath.substring(start);
            if (type.contains("/")) {
                type = type.substring(0, type.indexOf("/"));
            }
            file = file.substring(0, file.lastIndexOf(".") + 1) + type;
        }
        return file;
    }
}
