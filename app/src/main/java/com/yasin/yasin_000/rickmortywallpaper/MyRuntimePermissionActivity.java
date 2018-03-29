package com.yasin.yasin_000.rickmortywallpaper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yasin_000 on 1.10.2017.
 */

abstract public class MyRuntimePermissionActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void askForPermission(final String[] askedPermissons, final int requestCode) {
        int permissionControl = PackageManager.PERMISSION_GRANTED;
        boolean showExplanation = false;

        for (String permission : askedPermissons) {
            permissionControl += ContextCompat.checkSelfPermission(this, permission);
            showExplanation = showExplanation || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }
        if (permissionControl == PackageManager.PERMISSION_GRANTED){
            if (showExplanation){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Why Do I Need Storage Permission?");
                alertDialog.setMessage("Storage Permisson is neededto save wallpaper in gallery");
                alertDialog.setNegativeButton("GRANT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MyRuntimePermissionActivity.this, askedPermissons, requestCode);
                    }
                });
                alertDialog.show();
            }else {
                ActivityCompat.requestPermissions(MyRuntimePermissionActivity.this, askedPermissons, requestCode);
            }
        }else {
            permissionGranted(requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionControl = PackageManager.PERMISSION_GRANTED;

        for (int permissionState : grantResults) {
            permissionControl += permissionState;
        }
        if ((grantResults.length > 0) && permissionControl == PackageManager.PERMISSION_GRANTED){
            permissionGranted(requestCode);
        }else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Why Do I Need Storage Permission?");
            alertDialog.setMessage("Storage Permisson is neededto save wallpaper in gallery");
            alertDialog.setPositiveButton("GRANT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intent);
                }
            });
            alertDialog.show();

        }
    }
    abstract void permissionGranted(int requestCode);
}
