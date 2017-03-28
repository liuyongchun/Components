package com.wettch.update;

/**
 * Created by liuyongchun on 2017/3/20.
 * WettchAndroidUpdate网络请求回调接口
 */

public interface UpdateCallBack {
    /**
     * 失败回调
     * @param e 错误
     */
    void onFailure(Exception e);

    /**
     * 成功回调
     * @param onResponse 成功返回信息
     */
    void onSuccess(Response onResponse);
}
