package net.refsheet.refsheetartist;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaScriptInterface {
    private Context mContext;
    private DrawerLayout mDrawerLayout;

    JavaScriptInterface(Context c, DrawerLayout drawerLayout) {
        mContext = c;
        mDrawerLayout = drawerLayout;
    }

    @android.webkit.JavascriptInterface
    public String readConfig() {
        String filename = "config.json";
        String encoding = "UTF-8";

        FileInputStream is = null;
        BufferedReader br = null;

        try {
            is = mContext.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(is, encoding);

            br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while(( line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if(is != null)
                try { is.close(); }
                catch (Exception ignored) {}

            if(br != null)
                try { br.close(); }
                catch (Exception ignored) {}
        }
    }

    @android.webkit.JavascriptInterface
    public void writeConfig(String configJson) {
        String filename = "config.json";
        String encoding = "UTF-8";

        try {
            FileOutputStream os = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            os.write(configJson.getBytes(encoding));
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            this.toast("Couldn't save settings :(");
        }
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

    @android.webkit.JavascriptInterface
    public void openMenu() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @android.webkit.JavascriptInterface
    public void closeMenu() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}