package com.sz1358.transpixel;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    public static final int ACTIVITY_CALL_CAMERA = 100;
    public static final int ACTIVITY_CALL_GALLERY = 101;
    public static final int PERMISSION_REQUEST = 1;
    public boolean CLEAR_PERMISSION = false;
    public String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getPermission();
    }

    public void addContentView(int layoutId, int currentItem) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView header_name = header.findViewById(R.id.userName);
        TextView header_email = header.findViewById(R.id.userEmail);

        Menu menu = navigationView.getMenu();
        int[] restrictItems = {R.id.nav_profile};
        int[] freeItems = {R.id.nav_login};

        if (SharedPrefManager.getInstance(this).isLogged()) {
            User user = SharedPrefManager.getInstance(this).getLoggedUser();

            header_name.setText(user.getUsername());
            header_email.setText(user.getEmail());

            for (int item : restrictItems) {
                menu.findItem(item).setVisible(true);
            }
            for (int item : freeItems) {
                menu.findItem(item).setVisible(false);
            }
        } else {
            header_name.setText(R.string.nav_header_name);
            header_email.setText("");

            for (int item : freeItems) {
                menu.findItem(item).setVisible(true);
            }
            for (int item : restrictItems) {
                menu.findItem(item).setVisible(false);
            }
        }
        if (currentItem != -1) {
            menu.findItem(currentItem).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(layoutId, null, false);
        drawer.addView(contentView, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_dashboard:
                Intent dashboardIntent = new Intent(BaseActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
                finish();
                break;
            case R.id.nav_login:
                if (SharedPrefManager.getInstance(this).isLogged()) {
                    Toast.makeText(getApplicationContext(),
                            "You've logged in", Toast.LENGTH_SHORT).show();
                } else {
                    Intent loginIntent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
                break;
            case R.id.nav_profile:
                if (!SharedPrefManager.getInstance(this).isLogged()) {
                    Intent loginIntent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Intent profileIntent = new Intent(BaseActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    finish();
                }
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    CLEAR_PERMISSION = false;
                    break;
                } else {
                    CLEAR_PERMISSION = true;
                }
            }
        }

        if (!CLEAR_PERMISSION) {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Map<String, Integer> permResultMap = new HashMap<>();
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        permResultMap.put(permissions[i], grantResults[i]);
                    }
                    for (String permission : PERMISSIONS) {
                        if (permResultMap.get(permission) !=
                                PackageManager.PERMISSION_GRANTED) {
                            showCustomDialog(permission);
                        } else {
                            CLEAR_PERMISSION = true;
                        }
                    }
                }
            }
        }
    }

    private void showCustomDialog(String name) {
        String message = String.format("%s permission is needed for the app to run, " +
                "click CANCEL to exit app.", name);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: {
                        getPermission();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE: {
                        System.exit(0);
                        break;
                    }
                }
            }
        };
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", listener)
                .create()
                .show();
    }
}
