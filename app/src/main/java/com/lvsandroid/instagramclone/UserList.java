package com.lvsandroid.instagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class UserList extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //=== List of users
        userNames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,userNames);
        final ListView usersList = (ListView) findViewById(R.id.usersList);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Intent intent = new Intent(getApplicationContext(),UserFeed.class);
                String userName = userNames.get(index);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });


        //=== Get users list
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUser.getUsername())
                .addAscendingOrder("username")
                .findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                for (ParseUser user : objects) {
                                    userNames.add(user.getUsername());
                                    Log.i("users", user.getUsername());
                                }
                                usersList.setAdapter(arrayAdapter);
                            }
                        } else {
                            Log.e("[ERROR]", getResources().getString(R.string.log_can_t_get_users) + " : " + e.toString());
                        }
                    }
                });
        for(String usr : userNames) {
            Log.i("users list",usr);
        }
    }

    //=== Menu ===
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,1);
            return true;
        }
        if (id == R.id.logout) {
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    //=== Photo import ===
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            try {
                // Check permission access to storage
                if (ActivityCompat.checkSelfPermission(UserList.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    UserListPermissionsDispatcher.onActivityResultWithCheck(this,requestCode,resultCode,data);
                } else {

                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    ParseFile file = new ParseFile("image.png",byteArray);

                    ParseObject object = new ParseObject("Images");
                    object.put("username",ParseUser.getCurrentUser().getUsername());
                    object.put("image",file);

                    // Set image public
                    ParseACL acl=new ParseACL();
                    acl.setPublicReadAccess(true);
                    object.setACL(acl);

                    // Save image in DB
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Toast.makeText(getApplication().getBaseContext(),"Your image has been posted",Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplication().getBaseContext(),"There was an error, please try again",Toast.LENGTH_LONG).show();
                                Log.e("ERROR",e.toString());
                            }
                        }
                    });
                }

            } catch (Exception e) {
                Toast.makeText(getApplication().getBaseContext(),"There was an error, please try again",Toast.LENGTH_LONG).show();
                Log.e("ERROR",e.toString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        UserListPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
