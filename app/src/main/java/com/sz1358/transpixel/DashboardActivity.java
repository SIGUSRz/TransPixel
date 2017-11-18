package com.sz1358.transpixel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_dashboard);
    }
}
