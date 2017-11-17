package com.codefactory.devtest.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by kudlaty on 14/10/2016.
 */

public class DroidPermissions {

    public static boolean permissionGranted(Activity acivity, String permission, int requestCode){

        boolean result = true;
        int ok = PackageManager.PERMISSION_GRANTED;
        int permRes=-1;

        permRes = ContextCompat.checkSelfPermission(acivity, permission);
        if(permRes!=ok){
            result = false;
            if(!ActivityCompat.shouldShowRequestPermissionRationale(acivity, permission)){
                ActivityCompat.requestPermissions(acivity, new String[] {permission}, requestCode);
                click(acivity, permission, requestCode);
            }
        }

        return result;
    }

    private static void click(final Activity activity, final String perm, final int code){

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, new String[] {perm}, code);
            }
        };
    }
}
