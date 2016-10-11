package com.lvsandroid.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by Laurent on 10/10/2016.
 */

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        String appId = getResources().getString(R.string.parse_app_id);
        String clientKey = getResources().getString(R.string.parse_client_key);
        String server = getResources().getString(R.string.parse_server);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(appId)
                .clientKey(clientKey)
                .server(server)
                .build()
        );

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
