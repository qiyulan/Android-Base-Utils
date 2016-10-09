package moe.xing.baseutils.network;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.network.cookies.MyCookiesManager;
import moe.xing.baseutils.utils.BaseBean;
import moe.xing.baseutils.utils.Debug;
import moe.xing.baseutils.utils.FileUtils;
import moe.xing.baseutils.utils.LogHelper;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hehanbo on 2016/7/13 0013.
 * <p>
 * 网络接口
 */
@Keep
@SuppressWarnings({"WeakerAccess", "unused"})
public class RetrofitNetwork {

    //    private static RetrofitNetwork mInstance;
    private static OkHttpClient okHttpClient;
    public Retrofit retrofit;

//    private RetrofitNetwork() {
//        retrofit = new Retrofit.Builder()
//                //.callbackExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//                .client(okHttpClient())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .baseUrl("http://***/")
//                .build();
//    }

    /**
     * 网络结果转换器
     * 调度线程并判断 API 结果是否成功
     *
     * @see #sOperator()
     */
    @NonNull
    public static <T extends BaseBean> Observable.Transformer<T, T> preHandle() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> responseObservable) {
                return responseObservable.lift(RetrofitNetwork.<T>sOperator())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 网络结果转换器
     * 判断 API 结果是否成功
     * 失败的操作调用 {@link Subscriber#onError(Throwable)} 并传递失败原因
     * 成功的操作调用 {@link Subscriber#onNext(Object)} 传递结果
     */
    @NonNull
    public static <T extends BaseBean> Observable.Operator<T, T> sOperator() {
        return new Observable.Operator<T, T>() {
            @Override
            public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
                return new Subscriber<T>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (Init.isDebug()) {
                            subscriber.onError(e);
                        } else {
                            subscriber.onError(new Throwable("网络错误,请检查网络"));
                        }
                        LogHelper.e(e);
                    }

                    @Override
                    public void onNext(T baseBean) {
                        if ("1".equals(baseBean.getRet())) {
                            subscriber.onNext(baseBean);
                        } else {
                            subscriber.onError(new Throwable(baseBean.getErrMsg()));
                        }
                    }
                };
            }
        };
    }

    /**
     * 获取实例(并不线程安全,也不保证绝对单例,因为并不重要)
     */
//    public static RetrofitNetwork getInstance() {
//        if (mInstance == null) {
//            mInstance = new RetrofitNetwork();
//        }
//        return mInstance;
//    }

    /**
     * 设置 okhttp client
     */
    public static OkHttpClient okHttpClient() {

        File httpCacheDirectory = null;
        try {

            httpCacheDirectory = FileUtils.getCacheDir("http_cache");
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.Toast("创建缓存文件夹时出错");
        }

        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .addNetworkInterceptor(new GZIPInterceptor())
                    .addNetworkInterceptor(new CacheInterceptor())
                    .cookieJar(new MyCookiesManager());
            if (httpCacheDirectory != null) {
                builder.cache(new Cache(httpCacheDirectory, 1024 * 1024 * 10));
            }
            if (Init.isDebug()) {
                Debug.addStethoInOkhttp(builder);
                Debug.addLoggerInOkhttp(builder);
            }

            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 获取 UA
     */
    @NonNull
    public static String UA() {
        String BuildVersion = Init.getVersionName();
        String rootBuildVersion = BuildVersion.substring(0, BuildVersion.lastIndexOf("."));
        return Init.getUaName() + rootBuildVersion +
                "(Android;Build 1;Version " + BuildVersion + ";)";
    }

    /**
     * 下载文件
     *
     * @param fileUrl    文件地址
     * @param forceExtra 是否必须储存在外置缓存区
     */
    @NonNull
    public static Observable<File> download(final String fileUrl, final boolean forceExtra) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                File file;
                if (!FileUtils.isExternalStorageWritable() && forceExtra) {
                    subscriber.onError(new Throwable("外置储存卡不可用"));
                    return;
                }
                try {
                    file = FileUtils.getCacheFile(FileUtils.getFileNameFromUrl(fileUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }

                String errorMessage = "";
                URL url = null;
                BufferedInputStream inputStream = null;
                BufferedOutputStream outputStream = null;
                FileOutputStream fileStream = null;
                URLConnection connection = null;
                final int DOWNLOAD_BUFFER_SIZE = 1024;
                try {
                    url = new URL(fileUrl);
                    connection = url.openConnection();
                    connection.setUseCaches(false);

                    inputStream = new BufferedInputStream(connection.getInputStream());
                    fileStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
                    byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(data, 0, data.length)) >= 0) {
                        outputStream.write(data, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(new Exception(e));
                } finally {
                    try {
                        if (outputStream != null)
                            outputStream.close();
                        if (inputStream != null)
                            inputStream.close();
                        if (fileStream != null)
                            fileStream.close();
                    } catch (IOException ignored) {
                    }
                }
                subscriber.onNext(file);
            }
        }).subscribeOn(Schedulers.io()

        );
    }

    /**
     * 插入UA 的 Interceptor
     */
    private static class GZIPInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            String defaultUA = chain.request().headers().get("User-Agent");

            builder.removeHeader("User-Agent");
            builder.addHeader("User-Agent", defaultUA + UA());

            // TODO: 2016/5/17 0017 gzip is close!
            builder.removeHeader("Accept-Encoding");
            builder.addHeader("Accept-Encoding", "identity");

            return chain.proceed(builder.build());
        }
    }

    /**
     * 缓存的 Interceptor
     */
    private static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();//拦截reqeust
            if (!NetworkUtils.isNetworkReachable()) {//判断网络连接状况
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                        .build();
            }
            Response response = chain.proceed(request);
            if (NetworkUtils.isNetworkReachable()) {
                int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
                response.newBuilder()
                        .removeHeader("Pragma")
                        //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .header("Cache-Control", "public, max-age=" + maxAge)//设置缓存超时时间
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        //设置缓存策略，及超时策略
                        .build();
            }
            return response;
        }
    }


}
