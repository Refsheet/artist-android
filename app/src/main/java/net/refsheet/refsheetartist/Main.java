package net.refsheet.refsheetartist;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    private String DEV_URL = "http://localhost:3000";
    private String PROD_URL = "https://extension.refsheet.net";

    private WebView mWebView;
    private TextView mLoadingText;
    private ProgressBar mLoadingProgress;
    private ShakeResponder mShakeResponder;
    private DrawerLayout mDrawerLayout;

    private boolean webViewInitialized = false;
    private boolean webViewHasError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.root);
        mLoadingText = findViewById(R.id.loading_message);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        configureWebView();
        configureShaker();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void configureWebView() {
        final Main _this = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Configure Webview:
        mWebView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            JavaScriptInterface jsInterface = new JavaScriptInterface(this, mDrawerLayout);
            mWebView.addJavascriptInterface(jsInterface, "Android");
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                String errorMsg = getString(R.string.errors_no_internet);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    errorMsg += "\n\n" + error.getDescription();
                }
                _this.showLoading(errorMsg, true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webViewHasError)
                    _this.hideLoading();
            }
        });

        if (!webViewInitialized) {
            webViewInitialized = true;
            String connectTo = BuildConfig.DEBUG ? DEV_URL : PROD_URL;
            mWebView.loadUrl(connectTo);
        }
    }

    private void configureShaker() {
        final Main _this = this;

        mShakeResponder = new ShakeResponder(this) {
            @Override
            protected void onShake() {
                _this.toast(getString(R.string.loading_refreshing_toast));
                _this.showLoading(getString(R.string.loading_refreshing));
                mWebView.reload();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShakeResponder.register();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        mShakeResponder.unregister();
        mWebView.onPause();
        super.onPause();
    }

    // UI Action Stuff

    private void toast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }

    private void hideLoading() {
        mWebView.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
        mLoadingText.setVisibility(View.GONE);
    }

    private void showLoading(String text) {
        showLoading(text, false);
    }

    private void showLoading(String text, boolean isError) {
        mWebView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mLoadingText.setVisibility(View.VISIBLE);
        mLoadingText.setText(text);
        webViewHasError = isError;
    }
}
