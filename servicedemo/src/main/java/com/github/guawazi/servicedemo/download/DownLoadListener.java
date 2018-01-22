package com.github.guawazi.servicedemo.download;

/**
 * Created by wangliang on 2018/1/22.
 * 下载监听
 */

public interface DownLoadListener {
    // 获取下载进度
    void onProgress(int progress);

    // 成功
    void onSuccess();

    // 失败
    void onFailed();

    // 暂停
    void onPaused();

    // 取消
    void onCanceled();
}
