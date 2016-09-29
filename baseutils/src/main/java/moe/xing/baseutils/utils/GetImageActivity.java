package moe.xing.baseutils.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import moe.xing.baseutils.Init;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hehanbo on 16-9-29.
 * <p>
 * 获取图像
 */

public class GetImageActivity extends Activity {

    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int CORP_PHOTO = 3;
    private Uri corpedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            doSelect();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        doSelect();
    }

    /**
     * 选择图片
     */
    private void doSelect() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        IntentUtils.startIntentForResult(photoPickerIntent, this, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHOTO:
                    FileUtils.getFileUrlWithAuthority(Init.getApplication(),
                            data.getData())
                            .first()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<File>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    LogHelper.Toast(e.getLocalizedMessage());
                                }

                                @Override
                                public void onNext(File s) {
                                    toCorp(s);
                                }
                            });
                    break;
                case CORP_PHOTO:
                    if (corpedImage != null) {
                        File corped = new File(URI.create(corpedImage.toString()));
                        RxGetImage.getInstance().onAns(corped);
                        finish();
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            RxGetImage.getInstance().onAns(null);
            finish();
        }
    }

    /**
     * 去裁剪图片
     */
    private void toCorp(File image) {
        File ans = null;
        try {
            ans = FileUtils.getCacheFile("corp-" + image.getName());
            Crop.of(Uri.fromFile(image), Uri.fromFile(ans))
                    .withAspect(1, 1).start(this, CORP_PHOTO);
            corpedImage = Uri.fromFile(ans);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        corpedImage = null;
    }
}
