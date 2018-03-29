package com.yasin.yasin_000.rickmortywallpaper;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.eftimoff.viewpagertransformers.RotateUpTransformer;
import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.eftimoff.viewpagertransformers.ZoomOutSlideTransformer;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static final String[] PERMISSIONS_EXTERNAL_STORAGE = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 231;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ViewPager viewPager;
    ArrayList<String> list;
    ViewPagerAdapter viewPagerAdapter;
    int value;
    static View decorView;
    GestureDetector tapGestureDetector;
    boolean mVisible;
    LinearLayout buttons;
    Button setButton, shareButton, downloadButton;
    List<Target> targets;
    ProgressBar progressBar;
    boolean permissionState;
    List<Integer> idsOfImages;
    CoordinatorLayout coordinatorLayout;


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            if (requestCode == REQUEST_EXTERNAL_PERMISSION_CODE) {
//                if (checkExternalStoragePermission(MainActivity.this)) {
//                    // Continue with your action after permission request succeed
//
//                }
//            }
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(MainActivity.this);

        permissionState = false;
        buttons = findViewById(R.id.buttons);
//        fab = findViewById(R.id.fab);
        downloadButton = findViewById(R.id.downloadButton);
        shareButton = findViewById(R.id.shareButton);
        setButton = findViewById(R.id.setButton);
        viewPager = findViewById(R.id.viewPager);
        targets = new ArrayList<Target>();
        progressBar = findViewById(R.id.settingAndSharingProgressBar);
        coordinatorLayout = findViewById(R.id.layout);

        decorView = getWindow().getDecorView();
        mVisible = true;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getInt("EXTRA_PAGE");
            list = extras.getStringArrayList("list");
            idsOfImages = extras.getIntegerArrayList("idsOfImages");
            Log.i("EXTRA_PAGE", String.valueOf(value));
        }


        viewPagerAdapter = new ViewPagerAdapter(MainActivity.this, list, value);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(value);
        viewPager.setPageTransformer(true , new ParallaxPageTransformer(R.id.imageView));
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadWallpaper downloadWallpaper = new DownloadWallpaper(MainActivity.this, viewPager.getCurrentItem());
                downloadWallpaper.execute();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsWallpaper asWallpaper = new AsWallpaper(MainActivity.this, viewPager.getCurrentItem());
                asWallpaper.execute();
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareWallpaper shareWallpaper = new ShareWallpaper(MainActivity.this, viewPager.getCurrentItem());
                shareWallpaper.execute();
            }
        });


        Interpolator sInterpolator = new DecelerateInterpolator();
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(MainActivity.this, sInterpolator);
            // scroller.setFixedDuration(5000);
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visiblity) {
                if ((visiblity & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0 ) {
                    buttons.setVisibility(View.VISIBLE);
//                    buttons.startAnimation(animShow);
                } else {
//                    buttons.startAnimation(animHide);
                    buttons.setVisibility(View.INVISIBLE);
                }
            }
        });

        tapGestureDetector = new GestureDetector(this, new TapGestureListener());

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });

    }

    @Override
    protected void onRestart() {
        Log.i("mVisibleOnRestart", String.valueOf(mVisible));
        super.onRestart();
        if (!mVisible){
            hideStatusBarOnStart();
            buttons.setVisibility(View.INVISIBLE);
        }

    }


    public void hideStatusBarOnStart(){
        mVisible = false;
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void hideStatusBar(){
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mVisible = false;
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//        buttons.startAnimation(animHide);
        buttons.setVisibility(View.INVISIBLE);
    }

    public void showStatusBar(){
        buttons.setVisibility(View.VISIBLE);
//        buttons.startAnimation(animShow);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mVisible = true;
    }

    public void doIt(){
        if (mVisible){
            hideStatusBar();
        }else{
            showStatusBar();
        }
    }
    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            doIt();
            Log.i("mVisibleOnTap", String.valueOf(mVisible));
            return super.onSingleTapUp(e);
        }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {viewPager.setCurrentItem(value);
//
//            }
//        });
//    }


    private class AsWallpaper extends AsyncTask<Object, Void, Void> {

    private Context context;
    private int currentItem;

    public AsWallpaper(Context context, int currentItem) {
        this.context = context;
        this.currentItem = currentItem;
    }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Object... strings) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Bitmap bitmap = null;
            try {

            String appDirectoryName = "RickAndMortyWallpaper";
            File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
            if (!imageRoot.exists()){
                imageRoot.mkdir();
            } else {
                OutputStream fOut = null;
                File file = new File(imageRoot.getAbsoluteFile(), "/RickAndMorty"+ idsOfImages.get(currentItem) +".jpg");
                String bitmapPath = file.getAbsolutePath();
                Uri uriOfBitmap = Uri.fromFile(file);
                if (file.exists()){
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriOfBitmap);
                    wallpaperManager.setBitmap(bitmap);
                } else {
                    bitmap = Glide.with(context)
                            .load(list.get(currentItem))
                            .asBitmap()
                            .centerCrop()
                            .into(720, 1280)
                            .get();
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    wallpaperManager.setBitmap(bitmap);
                }
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, "Wallpaper is set", Snackbar.LENGTH_SHORT).show();
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, "Sorry, an error occured", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, "Sorry, an error occured", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, "Sorry, an error occured", Snackbar.LENGTH_SHORT).show();
                }
            });

        }
        return null;
        }
    }

    private class ShareWallpaper extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        private Context context;
        private int currentItem;

        public ShareWallpaper(Context context, int currentItem) {
            this.context = context;
            this.currentItem = currentItem;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            Bitmap bitmap = null;
            try {
                String appDirectoryName = "RickAndMortyWallpaper";
                File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
                if (!imageRoot.exists()){
                    imageRoot.mkdir();
                }else{
                    //String path = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                    OutputStream fOut = null;

                    File file = new File(imageRoot.getAbsoluteFile(), "/RickAndMorty"+ idsOfImages.get(currentItem) +".jpg");
                    Log.i("filePath", file.getAbsolutePath());
                    String bitmapPath = file.getAbsolutePath();
                    if (file.exists()){
                        Uri uriOfBitmap = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/png");
                        intent.putExtra(Intent.EXTRA_STREAM, uriOfBitmap);
                        startActivity(Intent.createChooser(intent, "Share"));
                    } else {
                        bitmap = Glide.with(context)
                                .load(list.get(currentItem))
                                .asBitmap()
                                .into(720, 1280)
                                .get();
                        fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();

                        Uri bitmapUri = Uri.parse(bitmapPath);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/png");
                        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                        startActivity(Intent.createChooser(intent, "Share"));
                    }
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class DownloadWallpaper extends AsyncTask<Object, Void, Void> {
        private Context context;
        private int currentItem;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        public DownloadWallpaper(Context context, int currentItem) {
            this.context = context;
            this.currentItem = currentItem;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            Bitmap bitmap = null;
            try {
                String appDirectoryName = "RickAndMortyWallpaper";
                File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
                if (!imageRoot.exists()){
                    imageRoot.mkdir();
                }else{
                    //String path = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                    OutputStream fOut = null;

                    File file = new File(imageRoot.getAbsoluteFile(), "/RickAndMorty"+ idsOfImages.get(currentItem) +".jpg");
                    if (file.exists()){
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(coordinatorLayout, "The image already downloaded", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        bitmap = Glide.with(context)
                                .load(list.get(currentItem))
                                .asBitmap()
                                .into(720, 1280)
                                .get();

                        fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(coordinatorLayout, "Downloaded", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public boolean checkExternalStoragePermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }

        int readStoragePermissionState = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int writeStoragePermissionState = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        boolean externalStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        if (!externalStoragePermissionGranted) {
            requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_PERMISSION_CODE);
        }

        return externalStoragePermissionGranted;
    }

    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
