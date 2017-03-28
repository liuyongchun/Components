package com.wettch.update;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by liuyongchun on 2017/3/20.
 * 皖通Android升级组件
 */

public class WettchAndroidUpdate {
    private Context mContext;
    private String url = "";
    private String parameterJsonString = "";
    private UpdateCallBack updateCallBack;

    private WettchAndroidUpdate(Builder builder){
        this.mContext = builder.mContext;
        this.url = builder.url;
        this.parameterJsonString = builder.parameterJsonString;
        this.updateCallBack = builder.updateCallBack;
    }

    public static class Builder{
        private Context mContext;
        private String url = "";
        private String parameterJsonString = "";
        private UpdateCallBack updateCallBack;

        public Builder context(@NonNull Context mContext) {
            this.mContext = mContext;
            return this;
        }

        public Builder url(@NonNull String url) {
            this.url = url;
            return this;
        }

        public Builder parameterJsonString(String parameterJsonString) {
            if (parameterJsonString == null) {
                this.parameterJsonString = "";
            } else {
                this.parameterJsonString = parameterJsonString;
            }
            return this;
        }

        public Builder updateCallBack(@NonNull UpdateCallBack updateCallBack) {
            this.updateCallBack = updateCallBack;
            return this;
        }

        public WettchAndroidUpdate build() {
            return new WettchAndroidUpdate(this);
        }

    }

    /**
     * 获取版本号
     */
    public void getVersion() {
        validateMustParameter();
        HttpLoader httpLoader = Utils.getHttpLoader(mContext);
        httpLoader.getVersion(url, parameterJsonString, updateCallBack);
    }

    /**
     * 下载文件
     * @param isInstall true：下载并安装；false:只下载不安装
     */
    public void downFileAndInstall(boolean isInstall) {
        validateMustParameter();
        HttpLoader httpLoader = Utils.getHttpLoader(mContext);
        httpLoader.downFileAndInstall(isInstall, url, parameterJsonString, updateCallBack);
    }

    /**
     * 获取下载文件绝对路径
     * @param context 上下文
     * @return 路径
     */
    public String getFileInstallPath(@NonNull Context context) {
        return Utils.getFileInstallPath(context);
    }

    /**
     * 验证必要参数是否为空，为空则抛出异常
     */
    private void validateMustParameter() {
        if (mContext == null)
            throw new IllegalArgumentException("context is null!");
        if (TextUtils.isEmpty(url))
            throw new IllegalArgumentException("url is null!");
        if (updateCallBack == null)
            throw new IllegalArgumentException("updateCallBack is null!");
    }
}
