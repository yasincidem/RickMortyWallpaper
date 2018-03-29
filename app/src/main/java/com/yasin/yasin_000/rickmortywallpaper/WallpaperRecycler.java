package com.yasin.yasin_000.rickmortywallpaper;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class WallpaperRecycler extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference ref;
    List<String> list ;
    List<Integer> idsOfImages ;
    ProgressBar waitForRecyclerViewBar;

    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_recycler);

//        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = manager.getActiveNetworkInfo();
//        AlertDialog.Builder builder;
//        builder = new AlertDialog.Builder(WallpaperRecycler.this);
//        builder.setTitle("No Internet Connection")
//                .setNeutralButton("MOBILE NETWORK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
//                        startActivity(intent);
//                    }
//                })
//                .setPositiveButton("WI-FI", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
//                    }
//                });
//        if (info != null && info.isConnected()){
//            setContentView(R.layout.activity_wallpaper_recycler);
//        } else {
//            builder.show();
//        }
        background = findViewById(R.id.backgroundImage);
        waitForRecyclerViewBar = findViewById(R.id.waitingForRecyclerview);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(WallpaperRecycler.this, 3));

//        Bitmap blurredBitmap = BlurBuilder.blur(WallpaperRecycler.this, BlurBuilder.convertToBitmap(ResourcesCompat.getDrawable(getResources(), R.drawable.default_image, null), 720,1280));
//        Drawable drawable = new BitmapDrawable(getResources(), blurredBitmap);
        background.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher, null));


        ref = FirebaseDatabase.getInstance().getReference("Data");
        FirebaseRecyclerAdapter<Model, DataViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Model, DataViewHolder>(
                        Model.class,
                        R.layout.individual_row,
                        DataViewHolder.class,
                        ref
                ) {
                    @Override
                    protected void populateViewHolder(final DataViewHolder viewHolder, Model model, final int position) {
                        viewHolder.setImage(WallpaperRecycler.this, model.getImage());
                        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(viewHolder.imageView.getContext(), MainActivity.class);
                                intent.putExtra("EXTRA_PAGE", position);
                                Log.i("extrapage-recyclerview",String.valueOf(position));
                                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                                intent.putIntegerArrayListExtra("idsOfImages", (ArrayList<Integer>) idsOfImages);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Data");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                idsOfImages = new ArrayList<>();
                int counter = 0;
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String name = (String) messageSnapshot.child("image").getValue();
                    Log.i("firebasemodeltest", name +  " " + counter + "\n");
                    list.add(name);
                    idsOfImages.add(counter++);
                }
                waitForRecyclerViewBar.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                background.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mRef.addValueEventListener(eventListener);
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public DataViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewRecycler);


        }

        public void setImage(Context context, String url) {
            Glide.with(context)
                    .load(url)
                    .into(imageView);

        }
    }

//    class MyAdapter extends RecyclerView.Adapter<DataViewHolder> {
//
//        @Override
//        public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_row, parent, false);
//            DataViewHolder holder = new DataViewHolder(view);
//            return holder;
//        }
//
//        @Override
//        public void onBindViewHolder(final DataViewHolder holder, final int position) {
//            Glide.with(holder.imageView.getContext())
//                    .load(list.get(position))
//                    .into(holder.imageView);
//
//            holder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(holder.imageView.getContext(), MainActivity.class);
//                    intent.putExtra("EXTRA_PAGE", position);
//                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
//                    startActivity(intent);
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//    }
}
