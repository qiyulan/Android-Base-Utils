package moe.xing.baseutils.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hehanbo on 2016/7/13 0013.
 * <p>
 * <a href="http://crm.yunyuer.com/apidoc/zsb/#api-groupDesc-DescDescReturn">基础bean</a>
 */

public class BaseBean implements Serializable {

    /**
     * ret : -1
     * err_type : mobile
     * err_msg : 手机号不正确
     */

    @SerializedName("ret")
    private String ret;
    @SerializedName("err_type")
    private String errType;
    @SerializedName("err_msg")
    private String errMsg;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getErrType() {
        return errType;
    }

    public void setErrType(String errType) {
        this.errType = errType;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
