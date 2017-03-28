package com.wettch.update;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liuyongchun on 2017/3/20.
 */

public class OkHttpLoader implements HttpLoader {
    private Context mContext;
    private OkHttpClient okHttpClient;
    private static final MediaType JSON
            = MediaType.parse(Utils.JSON_TYPE);

    public OkHttpLoader(@NonNull Context context) {
        this.mContext = context;
        okHttpClient = new OkHttpClient();
    }

    @Override
    public void getVersion(String url, String jsonString, @NonNull final UpdateCallBack updateCallBack) {
        okHttpClient.newCall(createRequest(url, jsonString))
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        updateCallBack.onFailure(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        com.wettch.update.Response responseCallback = new com.wettch.update.Response();
                        responseCallback.setMessage(response.body().string());
                        updateCallBack.onSuccess(responseCallback);
                    }
                });
    }

    @Override
    public void downFileAndInstall(final boolean isInstall, String url, String jsonString, @NonNull final UpdateCallBack updateCallBack) {

        okHttpClient
                .newCall(createRequest(url, jsonString))
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        updateCallBack.onFailure(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        new Thread(new WriteFileThread(isInstall, response, updateCallBack)).start();
                    }
                });
    }

    /**
     * 创建请求
     *
     * @param url        请求地址
     * @param jsonString 所传参数
     * @return 请求
     */
    private Request createRequest(String url, String jsonString) {
        if (url == null)
            throw new IllegalArgumentException("WettchUpdate:url == null");
        RequestBody requestBody = RequestBody.create(JSON, jsonString);
        return new Request
                .Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    class WriteFileThread implements Runnable {
        Response response = null;
        UpdateCallBack updateCallBack;
        boolean isInstall;

        public WriteFileThread(boolean isInstall, Response response, UpdateCallBack updateCallBack) {
            this.isInstall = isInstall;
            this.response = response;
            this.updateCallBack = updateCallBack;
        }

        @Override
        public void run() {
            if (response == null)
                throw new IllegalArgumentException("WettchUpdate:Response == null");

            Utils.downAndWriteFile(isInstall,
                    mContext,
                    Utils.createMainHandler(updateCallBack),
                    response.body().byteStream(),
                    Utils.createFile(mContext),
                    response.body().contentLength());
        }
    }
}
