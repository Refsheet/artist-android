package net.refsheet.refsheetartist;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

public class Main extends AppCompatActivity {
    private String DEV_URL = "http://localhost:3000";
    private String PROD_URL = "https://extension.refsheet.net";

    private WebView mWebView;
    private ShakeResponder mShakeResponder;

    private boolean webViewInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureWebView();
        configureShaker();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void configureWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Configure Webview:
        mWebView = findViewById(R.id.root);
        mWebView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            JavaScriptInterface jsInterface = new JavaScriptInterface(this);
            mWebView.addJavascriptInterface(jsInterface, "Android");
        }

        if (!webViewInitialized) {
            webViewInitialized = true;
            String connectTo = BuildConfig.DEBUG ? DEV_URL : PROD_URL;
            mWebView.loadUrl(connectTo);
        }
    }

    private void configureShaker() {
        mShakeResponder = new ShakeResponder(this) {
            @Override
            protected void onShake() {
                Toast toast = Toast.makeText(getApplicationContext(), "AAAAAAA Shaking is rude! (refreshing)", Toast.LENGTH_LONG);
                toast.show();

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
}
