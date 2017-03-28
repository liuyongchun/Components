package com.wettch.update;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liuyongchun on 2017/3/20.
 */

public class UrlConnectionHttpLoader implements HttpLoader {

    private Context mContext;
    private HttpURLConnection urlConnection;

    public UrlConnectionHttpLoader(Context context) {
        mContext = context;
    }

    @Override
    public void getVersion(String url, String jsonString, @NonNull UpdateCallBack updateCallBack) {
        openConnection(url, updateCallBack);
        new Thread(new GetVersionRunnable(jsonString, Utils.createMainHandler(updateCallBack))).start();
    }

    @Override
    public void downFileAndInstall(boolean isInstall, String url, String jsonString, @NonNull UpdateCallBack updateCallBack) {
        openConnection(url, updateCallBack);
        new Thread(new DownAndWriteFile(isInstall, jsonString, Utils.createMainHandler(updateCallBack))).start();
    }

    /**
     * 初始化网络连接
     *
     * @param url
     * @param updateCallBack
     */
    private void openConnection(String url, UpdateCallBack updateCallBack) {
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");// 提交模式
            urlConnection.setConnectTimeout(Utils.DEFAULT_CONNECT_TIMEOUT_MILLIS);
            urlConnection.setReadTimeout(Utils.DEFAULT_READ_TIMEOUT_MILLIS);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
        } catch (IOException e) {
            updateCallBack.onFailure(e);
        }
    }

    class GetVersionRunnable implements Runnable {
        String jsonString;
        Handler mainHandler;

        public GetVersionRunnable(String jsonString, @NonNull Handler mainHandler) {
            this.jsonString = jsonString;
            this.mainHandler = mainHandler;
        }

        @Override
        public void run() {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                dataOutputStream.writeBytes(jsonString);
                dataOutputStream.flush();
                dataOutputStream.close();
                //开始获取数据
                BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while ((len = bis.read(arr)) != -1) {
                    bos.write(arr, 0, len);
                    bos.flush();
                }
                bos.close();
                mainHandler.sendMessage(mainHandler
                        .obtainMessage(
                                Utils.SUCCESS,
                                new Response()
                                        .setMessage(bos.toString("utf-8"))));

            } catch (IOException e) {
                mainHandler.sendMessage(mainHandler.obtainMessage(Utils.FAILURE, e));
            }
        }
    }

    class DownAndWriteFile implements Runnable {
        boolean isInstall;
        private String jsonString;
        private Handler mainHandler;

        public DownAndWriteFile(boolean isInstall, String jsonString, @NonNull Handler mainHandler) {
            this.isInstall = isInstall;
            this.jsonString = jsonString;
            this.mainHandler = mainHandler;
        }

        @Override
        public void run() {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                dataOutputStream.writeBytes(jsonString);
                dataOutputStream.flush();
                dataOutputStream.close();
                //开始获取数据
                urlConnection.connect();
                Utils.downAndWriteFile(isInstall,
                        mContext,
                        mainHandler,
                        urlConnection.getInputStream(),
                        Utils.createFile(mContext),
                        urlConnection.getContentLength());
            } catch (IOException e) {
               mainHandler.sendMessage(mainHandler.obtainMessage(Utils.FAILURE, e));
            }
        }
    }
}
