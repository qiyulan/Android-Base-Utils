package moe.xing.baseutils.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Hehanbo on 2016/7/14 0014.
 * <p>
 * 基础View
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface BaseView<T extends BasePresenter> {

    void setPresenter(@NonNull T presenter);

    /**
     * 显示消息
     *
     * @param message 消息文字
     */
    void showMessage(String message);

    void showMessage(Throwable e);

    void showMessage(@StringRes int message);

    void showMessage(@StringRes int message, String message2);

    void showProgressDialog();

    void showProgressDialog(String title);

    void dismissProgressDialog();

}
