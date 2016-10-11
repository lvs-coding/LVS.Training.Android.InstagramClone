package com.lvsandroid.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText usernameTxt;
    private EditText passwordTxt;
    private String signUp;
    private String logIn;
    private Button button;
    private boolean signUpMode = false;

    // Button clicked
    public void signUpOrLogin(View view) {
        String username = usernameTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if(isUserInputValid(username, password)) {
            if (signUpMode) {
                signUpUser(username, password);
            } else {
                logUser(username, password);
            }
        }

        // if user logged
        if(ParseUser.getCurrentUser() != null) {
            Log.i("currentUser","User logged in");
            showUsersList();
        }
    }

    private void showUsersList() {
        Intent intent = new Intent(getApplicationContext(),UserList.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if user was already logged in we show directly the users list
        if(ParseUser.getCurrentUser() != null) {
            Log.i("currentUser","User already logged in");
            showUsersList();
        }

        button = (Button)findViewById(R.id.button);
        signUp = getResources().getString(R.string.sign_up);
        logIn = getResources().getString(R.string.log_in);
        usernameTxt = (EditText) findViewById(R.id.eTxtUsername);
        passwordTxt = (EditText) findViewById(R.id.eTxtPassword);

        //=== Try to login or signup when DONE key pressed on soft keyboard
        passwordTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                  @Override
                                                  public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                                                      if (keyCode == KeyEvent.KEYCODE_ENDCALL) {
                                                          Log.i("DBG", "Done pressed");
                                                          signUpOrLogin(textView);
                                                      }

                                                      return false;
                                                  }
                                              });

        //=== Hide soft keyboard when clicking anywhere
        RelativeLayout logo = (RelativeLayout)findViewById(R.id.content_main);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        //=== Set link text under button
        final TextView textView = (TextView)findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textView.getText().equals(signUp)) {
                    signUpMode = true;
                    textView.setText(logIn);
                    button.setText(signUp);
                } else {
                    signUpMode = false;
                    textView.setText(signUp);
                    button.setText(logIn);
                }
            }
        });
    }

    private boolean userExists(String username) {
        final boolean[] userExists = {false};

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username",username);

        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size() > 0) {
                    userExists[0] = true;
                }
            }
        });

        return userExists[0];
    }

    // Validate user info
    private boolean isUserInputValid(String username, String password) {
        if(username.equals("") || password.equals("")) {
            if(username.equals("")) {
                String userNameBlank = getResources().getString(R.string.msg_username_blank);
                showToast(userNameBlank, Toast.LENGTH_LONG);
            } else {
                String passwordBlank = getResources().getString(R.string.msg_password_blank);
                showToast(passwordBlank,Toast.LENGTH_LONG);

            }
            return false;
        }
        return true;
    }

    // Show Toast message
    private void showToast(String message, int duration) {
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }

    // Sign up user
    private void signUpUser(String username, String password) {
        ParseUser user = new ParseUser();

        if(userExists(username)) {
            showToast((getResources().getString(R.string.msg_user_exists)).replace("{0}",username),Toast.LENGTH_LONG);
            return;
        }

        user.setUsername(username);
        user.setPassword(password);

        // The username is created in the remote database but localy too, and he is automatically logged in
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("signUpInBackground", "Successful");
                } else {
                    String message = getResources().getString(R.string.msg_sign_up_failed);
                    showToast(message,Toast.LENGTH_SHORT);
                    Log.e("signUpInBackground", "Failed");
                    Log.e("signUpInBackground",e.toString());
                }
            }
        });
    }

    // Log user
    private void logUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.i("LogInCallback", "Successful");
                } else {
                    String message = getResources().getString(R.string.msg_login_failed);
                    showToast(message,Toast.LENGTH_SHORT);
                    Log.e("LogInCallback", "Failed");
                    Log.e("LogInCallback",e.toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
