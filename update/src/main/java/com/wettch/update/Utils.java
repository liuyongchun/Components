package com.wettch.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by liuyongchun on 2017/3/20.
 */

public class Utils {
    public static final String JSON_TYPE = "application/json; charset=utf-8";
    public static final String DOWN_APK_NAME = "temp.apk"; //下载apk名称
    public static final String PATH_SPLIT = "/"; //路径分隔符
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    public static final int SUCCESS = 0;//请求成功
    public static final int FAILURE = 1;//请求失败

    private Utils() {
    }

    /**
     * 获取网络请求类，如果项目中引入okhttp3，则用okhttp3作为网络请求层，否则，用UrlConnection
     *
     * @return
     */
    public static HttpLoader getHttpLoader(Context context) {
        try {
            Class.forName("okhttp3.OkHttpClient");
            return new OkHttpLoader(context);
        } catch (ClassNotFoundException e) {
        }
        return new UrlConnectionHttpLoader(context);
    }

    /**
     * 创建Handler
     *
     * @param updateCallBack 回调
     * @return Handler
     */
    public static Handler createMainHandler(final UpdateCallBack updateCallBack) {
        return new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESS:
                        if (updateCallBack != null)
                            updateCallBack.onSuccess((Response) msg.obj);
                        break;
                    case FAILURE:
                        if (updateCallBack != null)
                            updateCallBack.onFailure((Exception) msg.obj);
                        break;
                }
            }
        };
    }

    /**
     * 创建文件
     *
     * @param context 上下文
     * @return File
     */
    public static File createFile(Context context) {
        File fileApk = new File(getFileInstallPath(context));
        File dir = fileApk.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (fileApk.exists()) {
            fileApk.delete();
        }
        return fileApk;
    }

    /**
     * 获取安装文件绝对路径
     *
     * @param context
     * @return
     */
    public static String getFileInstallPath(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context == null");
        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(Utils.PATH_SPLIT)
                .append(context.getPackageName())
                .append(Utils.PATH_SPLIT)
                .append(Utils.DOWN_APK_NAME);
        return filePathBuilder.toString();
    }

    /**
     * 下载流文件并写入SD卡
     *
     * @param isInstall true：下载并安装，false：只下载不安装
     * @param context 上下文
     * @param mHandler    Handler
     * @param inputStream InputStream
     * @param file        要写入的文件
     * @param fileSize    流文件大小
     */
    public static void downAndWriteFile(boolean isInstall, Context context, Handler mHandler, InputStream inputStream, File file, long fileSize) {
        if (context == null || mHandler == null || inputStream == null || file == null)
            throw new IllegalArgumentException("Context or Handler or InputStream or File is null!");
        OutputStream fileOutputStream = null;

        try {
            byte[] buffer = new byte[2048];
            int readLength = 0;
            int progress = 0;
            fileOutputStream = new FileOutputStream(file);
            com.wettch.update.Response responseCallback = new com.wettch.update.Response()
                    .setFileMax(fileSize);

            while ((readLength = inputStream.read(buffer)) != -1) {
                progress += readLength;
                responseCallback.setProgress(progress);
                mHandler.sendMessage(mHandler.obtainMessage(Utils.SUCCESS, responseCallback));
                fileOutputStream.write(buffer, 0, readLength);
            }
            fileOutputStream.flush();
            if (isInstall) {
                installApk(context, getFileInstallPath(context));
            }

        } catch (Exception e) {
            mHandler.sendMessage(mHandler.obtainMessage(Utils.FAILURE, e));
        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    mHandler.sendMessage(mHandler.obtainMessage(Utils.FAILURE, e));
                }
        }
    }

    public static void installApk(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            Log.e("Utils", "File not exists");
        }
    }
}
