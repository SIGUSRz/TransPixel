package com.sz1358.transpixel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends BaseActivity {

    TextView idView, usernameView, emailView;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_profile, R.id.nav_profile);
        View view = findViewById(R.id.drawer_layout)
                .findViewById(R.id.register_content);

        Spinner spinner = view.findViewById(R.id.locale_spinner);
        if (spinner != null) {
            createSpinner(spinner);
        }

        idView = view.findViewById(R.id.profile_id);
        usernameView = view.findViewById(R.id.profile_username);
        emailView = view.findViewById(R.id.profile_email);

        User user = SharedPrefManager.getInstance(this).getLoggedUser();
        idView.setText(String.valueOf(user.getId()));
        usernameView.setText(user.getUsername());
        emailView.setText(user.getEmail());
    }

    public void createSpinner(final Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locale_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        User user = SharedPrefManager.getInstance(ProfileActivity.this).getLoggedUser();
        spinner.setSelection(user.getLang());
        language = adapter.getItem(user.getLang()) + "";
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = parent.getItemAtPosition(position) + "";
                SharedPrefManager.getInstance(ProfileActivity.this).changeLang(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void requestLogout(View view) {
        if (SharedPrefManager.getInstance(this).isLogged()) {
            SharedPrefManager.getInstance(this).logout();
            Intent dashboardIntent = new Intent(ProfileActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        }
    }
}
