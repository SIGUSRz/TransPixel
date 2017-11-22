package com.sz1358.transpixel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showRegister(View view) {
        if (SharedPrefManager.getInstance(this).isLogged()) {
            Toast.makeText(getApplicationContext(), "You've Logged In",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }
    }

    public String[] returnInfo() {
        View view = findViewById(R.id.login_layout)
                .findViewById(R.id.login_content);
        loginUsername = view.findViewById(R.id.login_username);
        loginPassword = view.findViewById(R.id.login_password);

        final String username = loginUsername.getText().toString().trim();
        final String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            loginUsername.setError("Please Enter Username");
            loginUsername.requestFocus();
            return null;
        }

        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Please Enter a Password");
            loginPassword.requestFocus();
            return null;
        }

        return new String[]{username, password};
    }

    public void requestLogin(View view) {
        if (SharedPrefManager.getInstance(this).isLogged()) {
            Toast.makeText(getApplicationContext(), "You've Logged In",
                    Toast.LENGTH_SHORT).show();
        } else {
            String[] info = returnInfo();
            if (info != null) {
                final String username = info[0];
                final String password = info[1];
                StringRequest stringReq = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
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
                                                userInfo.getString("email")
                                        );

                                        SharedPrefManager
                                                .getInstance(getApplicationContext())
                                                .userLogin(user);

                                        Intent dashboardIntent = new Intent(LoginActivity.this,
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
                                        "Request Error: " + error.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        return params;
                    }
                };

                VolleySingleton.getInstance(this).addToRequestQueue(stringReq);
            }
        }
    }

}
