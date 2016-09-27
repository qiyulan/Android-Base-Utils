package moe.xing.baseutils.utils;


import moe.xing.baseutils.Init;
import moe.xing.baseutils.R;

/**
 * Created by Hehanbo on 2016/7/28 0028.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PXUtils {

    public static int dpToPx(int dp) {
        return dp * Init.getApplication().getResources().getDimensionPixelSize(R.dimen.dp);
    }

}
