package com.example.camerasample.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by admin on 2018/1/11.
 */

public class FileProviderUtils {

    public static Uri getUriFromFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = getUri(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private static Uri getUri(Context context, File file) {
        try {
            String packageName = context.getPackageName();
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName()+".file.provider", file);
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
