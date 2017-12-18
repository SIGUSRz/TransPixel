package com.sz1358.transpixel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity implements LoginView {

    EditText loginUsername, loginPassword;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        presenter = new LoginPresenter(this, new LoginService());
    }

    public void showRegister(View view) {
        if (SharedPrefManager.getInstance(this).isLogged()) {
            Toast.makeText(getApplicationContext(), "You've Logged In",
                    Toast.LENGTH_LONG).show();
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

//        if (TextUtils.isEmpty(username)) {
//            loginUsername.setError("Please Enter Username");
//            loginUsername.requestFocus();
//            return null;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            loginPassword.setError("Please Enter a Password");
//            loginPassword.requestFocus();
//            return null;
//        }

        return new String[]{username, password};
    }

    public void requestLogin(View view) {
        final SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        if (prefManager.isLogged()) {
            Toast.makeText(getApplicationContext(), "You've Logged In",
                    Toast.LENGTH_LONG).show();
        } else {
            String[] info = returnInfo();
            if (info != null) {
                presenter.requestLogin();
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
                                                userInfo.getString("email"),
                                                userInfo.getInt("lang")
                                        );

                                        prefManager.userLogin(user);
                                        prefManager.setDict(userInfo.getString("dict"));

                                        Intent dashboardIntent = new Intent(LoginActivity.this,
                                                DashboardActivity.class);
                                        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(dashboardIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                obj.getString("error"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(),
                                            "Response Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),
                                        VolleyErrorLogger.getMessage(error, getApplicationContext()),
                                        Toast.LENGTH_LONG).show();
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

    @Override
    public String getUsername() {
        return loginUsername.getText().toString().trim();
    }

    @Override
    public void showUsernameError(int resId) {
        loginUsername.setError(getString(resId));
    }

    @Override
    public String getPassword() {
        return loginPassword.getText().toString().trim();
    }

    @Override
    public void showPasswordError(int resId) {
        loginPassword.setError(getString(resId));
    }

    @Override
    public void startMainActivity() {
        Intent dashboardIntent = new Intent(LoginActivity.this,
                DashboardActivity.class);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(dashboardIntent);
    }

    @Override
    public void showLoginError(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
    }
}
