package com.ninestack.extremous.Utils;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ashleynarcisse on 3/7/15.
 */


public class WebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        view.loadUrl("javascript:console.log('yup')");
    }

    @JavascriptInterface
    private void showToast(String toast){
        //Toast.makeText(context, taost, TOAST.).show();
    }
}