package de.bofloos.totpandroid.util;

import android.content.Context;
import android.widget.Toast;

public class Util {

    public static void showMsg(String msg, Context ctx) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
