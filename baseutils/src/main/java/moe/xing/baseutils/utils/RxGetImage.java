package moe.xing.baseutils.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

import moe.xing.baseutils.Init;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by hehanbo on 16-9-29.
 * <p>
 * 获取图片
 */

@SuppressWarnings("WeakerAccess")
public class RxGetImage {

    private static RxGetImage sSingleton;
    private ArrayList<Subscriber<? super File>> mSubscribers = new ArrayList<>();


    public RxGetImage() {

    }

    /**
     * 获取单例
     */
    public static RxGetImage getInstance() {
        if (sSingleton == null) {
            synchronized (RxGetImage.class) {
                if (sSingleton == null) {
                    sSingleton = new RxGetImage();
                }
            }
        }
        return sSingleton;
    }

    /**
     * 获取图片
     *
     * @return Observable<File>
     */
    @NonNull
    public Observable<File> getImage() {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                Intent intent = new Intent(Init.getApplication(), GetImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Init.getApplication().startActivity(intent);
                mSubscribers.add(subscriber);
            }
        });
    }

    /**
     * 设置返回的图片
     *
     * @param file 返回的图片 可能为空(用户放弃)
     */
    void onAns(@Nullable File file) {
        for (Subscriber<? super File> subscriber : mSubscribers) {
            if (file != null) {
                subscriber.onNext(file);
            }
            subscriber.onCompleted();
        }
        mSubscribers.clear();
    }

    /**
     * 设置返回错误
     *
     * @param message 错误信息
     */
    void onError(String message) {
        for (Subscriber<? super File> subscriber : mSubscribers) {
            subscriber.onError(new Throwable(message));
        }
        mSubscribers.clear();
    }
}
