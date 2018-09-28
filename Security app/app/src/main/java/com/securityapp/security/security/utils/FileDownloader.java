package com.securityapp.security.security.utils;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import java.net.CookieManager;
import java.net.HttpCookie;

/**
 * Utility class to download an mp4 video from webservice to the device internal storage
 */

public class FileDownloader {

    private DownloadManager _downloadManager;
    private CookieManager cookieManager;

    public FileDownloader(DownloadManager downloadManager, CookieManager cookieManager){
        _downloadManager = downloadManager;
        this.cookieManager = cookieManager;
    }

    public void downloadFile(String filename, String fileDownloadUrl){

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileDownloadUrl));

        HttpCookie cookie = cookieManager.getCookieStore().getCookies().get(0);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setMimeType("video/mp4")
                .addRequestHeader("Cookie", String.format("%s=%s",cookie.getName(), cookie.getValue()))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Tready/" + filename + ".mp4")
                .allowScanningByMediaScanner();

        _downloadManager.enqueue(request);
    }
}