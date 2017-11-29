package com.sz1358.transpixel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreviewActivity extends BaseActivity {

    String imagePath;
    Uri imageUri;
    Bitmap picture;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_preview, -1);

        View view = findViewById(R.id.drawer_layout)
                .findViewById(R.id.preview_content);

        Spinner spinner = view.findViewById(R.id.locale_spinner);
        if (spinner != null) {
            createSpinner(spinner);
        }

        imagePath = getIntent().getStringExtra("imagePath");
        ImageView preview = view.findViewById(R.id.imagePreview);
        if (imagePath != null) {
            picture = BitmapFactory.decodeFile(imagePath);
            preview.setImageBitmap(picture);
        } else {
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            try {
                picture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                preview.setImageBitmap(picture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createSpinner(final Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locale_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        User user = SharedPrefManager.getInstance(PreviewActivity.this).getLoggedUser();
        spinner.setSelection(user.getLang());
        int temp = user.getLang();
        language = adapter.getItem(user.getLang()) + "";
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = parent.getItemAtPosition(position) + "";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void detectPhoto(View view) {
        StringRequest stringReq = new StringRequest(Request.Method.POST, URLs.URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (!obj.has("error")) {
                                JSONObject res = obj.getJSONObject("response");
                                Intent dashboardIntent = new Intent(PreviewActivity.this,
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
                params.put("image", prepareImage(picture));
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringReq);
    }

    private String prepareImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imgBytes = outputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
}
