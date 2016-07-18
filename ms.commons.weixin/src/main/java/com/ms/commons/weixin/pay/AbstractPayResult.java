package com.ms.commons.weixin.pay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

public abstract class AbstractPayResult {

    private String  return_code;        // 返回状态码
    private String  return_msg;         // 返回信息
    private boolean signCorrect = false;

    private String  appid;              // 调用接口提交的公众账号ID
    private String  mch_id;             // 商户号
    private String  nonce_str;          // 随机字符串
    private String  sign;               // 签名
    private String  result_code;        // 业务结果SUCCESS/FAIL
    private String  err_code;           // 错误代码
    private String  err_code_des;       // 错误代码描述

    public AbstractPayResult(Map<String, String> datas, boolean signCorrect) {
        if (datas == null || datas.isEmpty()) {
            return_msg = "请求无响应";
            return;
        }
        this.signCorrect = signCorrect;
        this.return_code = datas.get("return_code");
        this.return_msg = datas.get("return_msg");
        this.nonce_str = datas.get("nonce_str");
        this.sign = datas.get("sign");
        this.result_code = datas.get("result_code");
        this.err_code = datas.get("err_code");
        this.err_code_des = datas.get("err_code_des");
    }

    /**
     * 设置数据
     * 
     * @param datas
     */
    public void setData(Map<String, String> datas) {

    }

    /**
     * @return the return_code
     */
    public String getReturnCode() {
        return return_code;
    }

    /**
     * @return the return_msg
     */
    public String getReturnMsg() {
        return return_msg;
    }

    /**
     * @return the result_code
     */
    public String getResult_code() {
        return result_code;
    }

    /**
     * @return the err_code
     */
    public String getErr_code() {
        return err_code;
    }

    /**
     * @return the err_code_des
     */
    public String getErr_code_des() {
        return err_code_des;
    }

    /**
     * @return the nonce_str
     */
    public String getNonce_str() {
        return nonce_str;
    }

    /**
     * @return the sign
     */
    public String getSign() {
        return sign;
    }

    /**
     * @return the appid
     */
    public String getAppid() {
        return appid;
    }

    /**
     * @return the mch_id
     */
    public String getMch_id() {
        return mch_id;
    }

    /**
     * -------------------
     */

    public int toInt(String value) {
        return NumberUtils.toInt(value, 0);
    }

    public float toFloat(String value) {
        return NumberUtils.toFloat(value, 0);
    }

    public Date toDate(String value) {
        // 20141030133525
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date parse = sdf.parse(value);
            return parse;
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * @return
     */
    public boolean isSignCorrect() {
        return signCorrect;
    }

    public boolean isReturnSuccess() {
        return "SUCCESS".equals(return_code);
    }

    public boolean isResultSuccess() {
        return "SUCCESS".equals(result_code);
    }

    /**
     * 是否成功
     * 
     * @return
     */
    public boolean isSuccess() {
        return isReturnSuccess() && isResultSuccess() && isSignCorrect();
    }

    /**
     * 错误信息
     * 
     * @return
     */
    public String getErrorMessage() {
        if (!isReturnSuccess()) {
            return return_msg;
        }
        if (!isResultSuccess()) {
            return err_code_des;
        }
        if (!isSignCorrect()) {
            return "签名失败";
        }
        return "ok";
    }
}
