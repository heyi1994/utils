package com.kaxiu.live.presentation.ui.main.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kaxiu.live.R;
import com.kaxiu.live.data.repository.SourceFactory;
import com.kaxiu.live.presentation.ui.base.BaseActivity;
import com.kaxiu.live.presentation.ui.chatting.ChatActivity;

import java.io.File;

import static com.kaxiu.live.BeautyLiveApplication.CUSTOMSERVICENAME;
import static com.kaxiu.live.BeautyLiveApplication.TARGET_ID;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 我的等级
 */
public class SimpleWebViewActivity extends BaseActivity {

    private static final String EXTRA_URL = "url";

    private String mUrl;

    private TextView tvTitle;
    private WebView mWebView;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private int selectImgMax = 1;//选取图片最大数量
    private int photosType = 0;//图库类型
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private SimpleWebChromeClient webChromeClient;

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, SimpleWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_webview;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mUrl = intent.getStringExtra(EXTRA_URL);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void findViews(Bundle savedInstanceState) {
        mWebView = $(R.id.simple_webview);

        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new Myapp(this), "myapp");

        tvTitle = $(R.id.tv_toolbar_title);
        if (TextUtils.isEmpty(mUrl)) {
            toastShort(getString(R.string.web_posturl_error));
            finish();
        } else {
            webChromeClient = new SimpleWebChromeClient();
            mWebView.setWebChromeClient(webChromeClient);
            mWebView.setWebViewClient(new SimpleWebViewClient());
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());
            mWebView.getSettings().setJavaScriptEnabled(true);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            String absPath = SourceFactory.wrapPath(mUrl);
            mWebView.loadUrl(absPath);

        }
    }


    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        webChromeClient.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            try {
                mWebView.stopLoading();
                mWebView.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SimpleWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(mUrl)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }

    }

    private class SimpleWebChromeClient extends WebChromeClient {
        private String mCameraFilePath;
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (tvTitle != null && !(TextUtils.isEmpty(title))) {
                tvTitle.setText(title);
            }
        }
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }
        @Override
        //扩容
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(requiredStorage*2);
        }

        private void openImageChooserActivity() {
            initDialog();
        }

        private void initDialog(){
            new AlertDialog.Builder(SimpleWebViewActivity.this)
                    .setTitle("個人照片")
                    .setItems(new String[]{"拍照", "圖庫選擇"},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        case 0:
                                            Intent i1=createCameraIntent();
                                            SimpleWebViewActivity.this.startActivityForResult(Intent.createChooser(i1, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
                                            break;
                                        case 1:
                                            Intent i=createFileItent();
                                            SimpleWebViewActivity.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
                                            break;
                                    }

                                }
                            }).setNegativeButton("取消", null).show();
        }

        /**
         * 处理拍照返回函数
         * @param requestCode
         * @param resultCode
         * @param data
         */
        public  void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (null == uploadMessage&& null == uploadMessageAboveL)
                    return;
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                        : data.getData();
                if (uploadMessageAboveL != null) {//5.0以上
                    onActivityResultAboveL(requestCode, resultCode, data);
                }else if(uploadMessage != null) {
                    if (result == null && data == null
                            && resultCode == Activity.RESULT_OK) {
                        File cameraFile = new File(mCameraFilePath);

                        Bitmap bitmap1 = getimage(cameraFile.getPath());

                        result = Uri.parse(MediaStore.Images.Media.insertImage(
                                getBaseContext().getContentResolver(), bitmap1, null, null));
                    }
                    uploadMessage.onReceiveValue(result);
                    uploadMessage = null;
                }


            }
        }
        /**
         * 创建调用照相机的intent
         * @return
         */
        private Intent createCameraIntent() {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File externalDataDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File cameraDataDir = new File(externalDataDir.getAbsolutePath()
                    + File.separator + "browser-photo");
            cameraDataDir.mkdirs();
            mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator
                    + System.currentTimeMillis() + ".jpg";
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(mCameraFilePath)));

            return cameraIntent;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
            if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
                return;
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }else {

                    File cameraFile = new File(mCameraFilePath);

                    Bitmap bitmap1 = getimage(cameraFile.getPath());

                    Uri result = Uri.parse(MediaStore.Images.Media.insertImage(
                            getBaseContext().getContentResolver(), bitmap1, null, null));
                    results=new Uri[]{result};
                }
            }
            uploadMessageAboveL.onReceiveValue(results);
            uploadMessageAboveL = null;
        }


        /**
         * 创建选择图库的intent
         * @return
         */
        private Intent createFileItent(){
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            Intent   intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            return intent;
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Log.i("tag", "url=" + url);
            Log.i("tag", "userAgent=" + userAgent);
            Log.i("tag", "contentDisposition=" + contentDisposition);
            Log.i("tag", "mimetype=" + mimetype);
            Log.i("tag", "contentLength=" + contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void getUrlType(String fName) {
        String type = "";
      /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
        /* android.permission.INSTALL_PACKAGES */
            Uri uri = Uri.parse(fName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public class Myapp {
        //JavaScript调用此方法拨打电话
        Context context;

        public Myapp(Context mContxt) {
            this.context = mContxt;
        }
        @JavascriptInterface
        public void openSystemCustomer() {
            Intent intent = new Intent();
            intent.putExtra(TARGET_ID, CUSTOMSERVICENAME);
            intent.putExtra("t","2");
            intent.putExtra("name", getString(R.string.recharge_custom_servicename));
            intent.setClass(context, ChatActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void copyFn(String value) {
            ClipData clipData = ClipData.newPlainText("text", value);
            ClipboardManager cm = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(clipData);
            toastShort("成功复制链接"+value);
        }
        @JavascriptInterface
        public void jsCopy(String value){
            ClipData clipData = ClipData.newPlainText("text", value);
            ClipboardManager cm = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(clipData);
            toastShort("成功复制链接"+value);
        }
    }
}
