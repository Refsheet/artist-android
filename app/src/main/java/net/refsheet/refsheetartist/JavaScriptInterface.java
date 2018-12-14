package net.refsheet.refsheetartist;
import android.content.Context;
import android.widget.Toast;

public class JavaScriptInterface {
    Context mContext;

    JavaScriptInterface(Context c) {
        mContext = c;
    }

    @android.webkit.JavascriptInterface
    public void toast(String toast) {
        Toast t = Toast.makeText(mContext, toast, Toast.LENGTH_SHORT);
        t.show();
    }

    @android.webkit.JavascriptInterface
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }
}