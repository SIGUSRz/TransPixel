package com.sz1358.transpixel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                SharedPrefManager.getInstance(ProfileActivity.this).setLang(position, language);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void requestLogout(View view) {
        final SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        if (prefManager.isLogged()) {
            StringRequest stringReq = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                    null,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),
                                    VolleyErrorLogger.getMessage(error, getApplicationContext()),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    User user = prefManager.getLoggedUser();
                    params.put("username", user.getUsername());
                    params.put("id", Integer.toString(user.getId()));
                    params.put("dict", prefManager.getDict());
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringReq);
            SharedPrefManager.getInstance(this).logout();
            Intent dashboardIntent = new Intent(ProfileActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        }
    }
}
