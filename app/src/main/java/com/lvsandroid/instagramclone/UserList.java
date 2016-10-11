package com.lvsandroid.instagramclone;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userNames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,userNames);

        final ListView usersList = (ListView) findViewById(R.id.usersList);


        ParseUser currentUser = ParseUser.getCurrentUser();

        //=== Get users list
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
}
