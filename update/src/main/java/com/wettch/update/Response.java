package com.wettch.update;

/**
 * Created by liuyongchun on 2017/3/27.
 * 服务器响应实体
 */

public class Response {
    /**
     * 响应内容
     */
    private String message;

    /**
     * 文件大小
     */
    private long fileMax;

    /**
     * 下载进度
     */
    private int progress;

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public Response setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public long getFileMax() {
        return fileMax;
    }

    public Response setFileMax(long fileMax) {
        this.fileMax = fileMax;
        return this;
    }
}
