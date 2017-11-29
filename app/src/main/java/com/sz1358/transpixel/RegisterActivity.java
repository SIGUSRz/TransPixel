package com.sz1358.transpixel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RegisterActivity extends AppCompatActivity {

    EditText registUsername, registEmail, registPassword;
    String language;
    int langIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View view = findViewById(R.id.register_layout)
                .findViewById(R.id.register_content);

        Spinner spinner = view.findViewById(R.id.locale_spinner);
        if (spinner != null) {
            createSpinner(spinner);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void createSpinner(final Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locale_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = parent.getItemAtPosition(position) + "";
                langIdx = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
                language = parent.getItemAtPosition(0) + "";
                langIdx = 0;
            }
        });
    }

    public String[] returnInfo() {
        View view = findViewById(R.id.register_layout)
                .findViewById(R.id.register_content);
        registUsername = view.findViewById(R.id.regist_username);
        registEmail = view.findViewById(R.id.regist_email);
        registPassword = view.findViewById(R.id.regist_password);
        final String username = registUsername.getText().toString().trim();
        final String email = registEmail.getText().toString().trim();
        final String password = registPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            registUsername.setError("Please Enter Username");
            registUsername.requestFocus();
            return null;
        }

        if (TextUtils.isEmpty(email)) {
            registEmail.setError("Please Enter Email");
            registEmail.requestFocus();
            return null;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registEmail.setError("Please Enter a Valid Email");
            registEmail.requestFocus();
            return null;
        }

        if (TextUtils.isEmpty(password)) {
            registPassword.setError("Please Enter a Password");
            registPassword.requestFocus();
            return null;
        }

        return new String[]{username, email, password, Integer.toString(langIdx)};
    }

    public void requestRegister(View view) {
        if (SharedPrefManager.getInstance(this).isLogged()) {
            Toast.makeText(getApplicationContext(), "You've Logged In", Toast.LENGTH_SHORT).show();
        } else {
            String[] info = returnInfo();
            if (info != null) {
                final String username = info[0];
                final String email = info[1];
                final String password = info[2];
                final String lang = info[3];
                StringRequest stringReq = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);

                                    if (!obj.has("error")) {
                                        JSONObject userInfo = obj.getJSONObject("user");

                                        User user = new User(
                                                userInfo.getInt("id"),
                                                userInfo.getString("username"),
                                                userInfo.getString("email"),
                                                userInfo.getInt("lang")
                                        );

                                        SharedPrefManager.
                                                getInstance(getApplicationContext())
                                                .userLogin(user);

                                        Intent dashboardIntent = new Intent(RegisterActivity.this,
                                                DashboardActivity.class);
                                        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(dashboardIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                obj.getString("error"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(),
                                            "Response Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        },
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
                        params.put("username", username);
                        params.put("email", email);
                        params.put("password", password);
                        params.put("lang", lang);
                        return params;
                    }
                };

                VolleySingleton.getInstance(this).addToRequestQueue(stringReq);
            }
        }
    }

    public void requestLogout(View view) {
        if (SharedPrefManager.getInstance(this).isLogged()) {
            SharedPrefManager.getInstance(this).logout();
            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}
