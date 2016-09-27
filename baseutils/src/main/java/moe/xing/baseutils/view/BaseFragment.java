package moe.xing.baseutils.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.yokeyword.fragmentation.SupportFragment;
import moe.xing.baseutils.utils.LogHelper;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Hehanbo on 2016/7/14 0014.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseFragment extends SupportFragment {

    protected View mView;
    protected Context mContext;
    protected Subscription mSubscription;
    protected android.support.v7.app.ActionBar mActionBar;
    protected String title;
    protected Fragment mFragment = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = CreateView(inflater, container, savedInstanceState);
        assert mView != null;
        ViewFound(mView);
        return mView;
    }


    protected abstract View CreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void ViewFound(View view);


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    protected abstract String getTitle();

    @Override
    public void onResume() {
        super.onResume();
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        title = getTitle();
        if (mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mSubscription.unsubscribe();
        } catch (Exception ignore) {
        }
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showMessage(String message) {
        LogHelper.Snackbar(mView, message);
    }

    public void showMessage(Throwable e) {
        showMessage(e.getLocalizedMessage());
    }

    public void showMessage(@StringRes int message) {
        showMessage(getString(message));
    }

    public void showMessage(@StringRes int message, String message2) {
        showMessage(getString(message) + " " + message2);
    }

    public void showProgressDialog() {
        showProgressDialog("");
    }

    public void showProgressDialog(String title) {
        ((BaseActivity) _mActivity).showProgressDialog(title);
    }

    public void dismissProgressDialog() {
        ((BaseActivity) _mActivity).dismissProgressDialog();
    }

    @Override
    public boolean onBackPressedSupport() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return true;
        } else {
            return super.onBackPressedSupport();
        }
    }

    /**
     * fragment 生命周期与 Rx 协调
     * 判断 能否安全进行UI操作
     * 失败的操作调用 {@link Subscriber#onError(Throwable)} 并传递失败原因
     * 成功的操作调用 {@link Subscriber#onNext(Object)} 传递结果
     */
    @NonNull
    public <T> Observable.Operator<T, T> fragmentLifeTime() {
        return new Observable.Operator<T, T>() {
            @Override
            public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
                return new Subscriber<T>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isAdded()) {
                            subscriber.onError(e);
                            LogHelper.e(e);
                        }
                    }

                    @Override
                    public void onNext(T t) {
                        if (isAdded()) {
                            subscriber.onNext(t);
                        }
                    }
                };
            }
        };
    }
}
