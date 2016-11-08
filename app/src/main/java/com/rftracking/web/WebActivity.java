package com.rftracking.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {
    private WebView mWebView;
    private static final int CODE_PICK_PHOTO=1245;
    private ValueCallback<Uri> mUploadMessage;

    private ValueCallback<Uri[]> mValueCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.addJavascriptInterface(new JSObject(), "browser");
        mWebView.loadUrl("file:///android_asset/test.html");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                mValueCallback = filePathCallback;
                startActivityForResult(createDefaultOpenableIntent(), CODE_PICK_PHOTO);
                //   startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), TAKE_PICTURE);
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                startActivityForResult(createDefaultOpenableIntent(), CODE_PICK_PHOTO);
            }
        });

    }

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File externalDataDir = Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        File cameraDataDir = new File(externalDataDir.getAbsolutePath()
//                + File.separator + "rftracking");
//        cameraDataDir.mkdirs();
//        String mCameraFilePath = cameraDataDir.getAbsolutePath()
//                + File.separator + System.currentTimeMillis() + ".jpg";
//        cameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                Uri.fromFile(new File(mCameraFilePath)));
        return cameraIntent;
    }

    private Intent createDefaultOpenableIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        Intent chooser = createChooserIntent(createCameraIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择图片");
        return chooser;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CODE_PICK_PHOTO:
                uploadImg(resultCode, data);
                break;
        }
    }

    private void uploadImg(int resultCode, Intent intent) {

        if (mUploadMessage != null) {

            Uri result = intent == null || resultCode != RESULT_OK ? null

                    : intent.getData();

            mUploadMessage.onReceiveValue(result);

            mUploadMessage = null;

        } else if (mValueCallback != null) {

            Uri[] uris = new Uri[1];
            if (intent.getData() == null) {
                uris[0] = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), (Bitmap) intent.getExtras().get("data"), null, null));
                ;
            } else {
                uris[0] = intent.getData();
            }
            if (uris[0] != null) {
                mValueCallback.onReceiveValue(uris);
            }
            mValueCallback = null;

        }

    }


    public class JSObject {
        @JavascriptInterface
        public void startQRScan() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String qrCode = "http://www.baidu.com";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mWebView.evaluateJavascript("showQrCode('" + qrCode + "')", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.e("webview",value);
                            }
                        });
                    } else {
                        mWebView.loadUrl("javascript:showQrCode('" + qrCode + "')");
                    }

                }
            });
        }
    }

}
