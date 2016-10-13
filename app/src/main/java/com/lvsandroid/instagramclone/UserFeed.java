package com.lvsandroid.instagramclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserFeed extends AppCompatActivity {
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String currentUserName =  intent.getStringExtra("userName");
        Log.i("DBG",currentUserName);
        setTitle(currentUserName + " Feed");

        ParseQuery<ParseObject> query = new ParseQuery<>("Images");

        ArrayList<MediaStore.Images> images = new ArrayList<>();
        query.whereEqualTo("username",currentUserName)
                 .findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null) {
                        if(objects.size() > 0) {
                            for (ParseObject object : objects) {

                                ParseFile file = (ParseFile) object.get("image");
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if(e == null) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            ImageView imageView = new ImageView(getApplicationContext());
                                            imageView.setImageBitmap(bitmap);
                                            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                            ));

                                            linearLayout.addView(imageView);

                                        } else {
                                            Log.e("ERROR",e.toString());
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        Log.e("ERROR",e.toString());
                    }

                }
        });

    }

}
