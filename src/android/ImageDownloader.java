package com.santinowu.cordova.imagedownloader;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.lang.Exception;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.webkit.URLUtil;
import android.content.Intent;
import android.net.Uri;

public class ImageDownloader extends CordovaPlugin {
    public final static String Log_TAG = "com.santinowu.cordova.imagedownloader";
    public final static String ACTION_DOWNLOAD = "download";
    public final static String SUCCESS_MESSAGE = "success";
    public final static String FAILURE_MESSAGE = "failure";

    private CallbackContext _callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        _callbackContext = callbackContext;

        final String url = args.getString(0);

        if (action.equals(ACTION_DOWNLOAD)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (true == download(url)) {
                        _callbackContext.success(SUCCESS_MESSAGE);

                        return;
                    }

                    _callbackContext.error(FAILURE_MESSAGE);
                }
            });

            return true;
        }

        return false;
    }

    private Boolean download(String urlStr) {
        URL url = getURL(urlStr);

        if (null == url) {
            Log.d(Log_TAG, "Fail to get URL");

            _callbackContext.error(FAILURE_MESSAGE);

            return false;
        }

        Log.d(Log_TAG, String.format("Downloading image from URL: %s", url.toString()));

        Bitmap bitmap = downloadImage(url);

        if (null == bitmap) {
            Log.d(Log_TAG, "Failed to download image");

            _callbackContext.error(FAILURE_MESSAGE);

            return false;
        }

        File image = saveImage(bitmap, url);

        if (null == image) {
            Log.d(Log_TAG, "Failed to save image");

            _callbackContext.error(FAILURE_MESSAGE);

            return false;
        }

        scanImage(image);

        return true;
    }

    private URL getURL(String urlStr) {
        URL url = null;

        try {
            url = new URL(urlStr);
        } catch (Exception e) {
            Log.d(Log_TAG, e.getMessage());

            return null;
        }

        return url;
    }

    private File getImagePath() {
        return Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    private File saveImage(Bitmap bitmap, URL url) {
        File imageFile = null;

        try {
            imageFile = new File(getImagePath(), getFileName(url));

            FileOutputStream fos = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) {
            imageFile = null;
        }

        return imageFile;
    }

    private String getFileName(URL url) {
        return URLUtil.guessFileName(url.toString(), null, null);
    }

    private Bitmap downloadImage(URL url) {
        HttpURLConnection conn = null;
        Bitmap bitmap = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                InputStream stream = conn.getInputStream();

                if (null != stream) {
                    bitmap = BitmapFactory.decodeStream(stream);
                }

                stream.close();
            }
        } catch (Exception e) {
            Log.d(Log_TAG, e.getMessage());

            bitmap = null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return bitmap;
    }

    private void scanImage(File image) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(image);

        mediaScanIntent.setData(contentUri);

        cordova.getActivity().sendBroadcast(mediaScanIntent);
    }
}
