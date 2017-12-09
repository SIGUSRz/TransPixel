package com.sz1358.transpixel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PreviewActivity extends BaseActivity {
    String uriString;
    Bitmap picture;
    String language;
    Integer position;
    Integer method;
    SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_preview, -1);

        View view = findViewById(R.id.drawer_layout)
                .findViewById(R.id.preview_content);

        Spinner lang_spinner = view.findViewById(R.id.locale_spinner);
        Spinner method_spinner = view.findViewById(R.id.method_spinner);
        if (lang_spinner != null) {
            createLangSpinner(lang_spinner);
            createMethodSpinner(method_spinner);
        }

        Uri imageURI;

        if (savedInstanceState == null) {
            uriString = getIntent().getStringExtra("imageURI");
            imageURI = Uri.parse(uriString);
        } else {
            imageURI = Uri.parse(uriString);
        }

        ImageView preview = view.findViewById(R.id.imagePreview);
        try {
            InputStream is = getContentResolver().openInputStream(imageURI);
            picture = BitmapFactory.decodeStream(is);
            preview.setImageBitmap(picture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("imageURI", uriString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uriString = savedInstanceState.getString("imageURI", uriString);
    }

    public void createLangSpinner(final Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locale_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        prefManager = SharedPrefManager.getInstance(PreviewActivity.this);
        User user = prefManager.getLoggedUser();
        position = user.getLang();
        spinner.setSelection(position);
        language = spinner.getItemAtPosition(position) + "";
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = parent.getItemAtPosition(position) + "";
                PreviewActivity.this.position = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void createMethodSpinner(final Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.method_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        method = 0;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                method = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void detectPhoto(View view) {
        String url = method == 0 ? URLs.URL_OCR_UPLOAD : URLs.URL_DETECT_UPLOAD;
        StringRequest stringReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (!obj.has("error")) {
                                JSONObject res = obj.getJSONObject("response");
                                String result = res.getString("result");
                                Intent resultIntent = new Intent(PreviewActivity.this,
                                        ResultActivity.class);
                                resultIntent.putExtra("result", result);
                                resultIntent.putExtra("language", language);
                                resultIntent.putExtra("position", position);
                                resultIntent.putExtra("method", method);
                                resultIntent.putExtra("imageURI", uriString);
                                startActivity(resultIntent);
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
                params.put("image", prepareImage(picture));
                params.put("method", Integer.toString(method));
                return params;
            }
        };

        stringReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        VolleySingleton.getInstance(this).addToRequestQueue(stringReq);
    }

    private String prepareImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imgBytes = outputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}
