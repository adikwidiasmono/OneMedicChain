package ndid.omc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by adikwidiasmono on 19/11/17.
 */

public class MedUtils {
    public static final String PREF_NAME = "One Medic Chain Prefenrences";
    public static final String PREF_REGISTERED_STATUS = "Registered Status";

    public static SharedPreferences.Editor setPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }
}
