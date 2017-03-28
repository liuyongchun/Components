package com.wettch.update;

/**
 * Created by liuyongchun on 2017/3/20.
 */

import android.support.annotation.NonNull;

/**
 * 网络请求接口
 */
public interface HttpLoader {
    /**
     * 获取版本号
     * @param url 服务端URL
     * @param jsonString 所传json字符串
     * @param updateCallBack 请求回调
     */
    void getVersion(String url, String jsonString, @NonNull final UpdateCallBack updateCallBack);

    /**
     * 下载升级文件
     * @param isInstall true下载并安装，false则只下载不安装
     * @param url 服务端URL
     * @param jsonString 所传json字符串
     * @param updateCallBack 请求回调
     */
    void downFileAndInstall(boolean isInstall,String url, String jsonString, @NonNull final UpdateCallBack updateCallBack);
}
