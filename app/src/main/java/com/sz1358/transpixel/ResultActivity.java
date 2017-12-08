package com.sz1358.transpixel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {
    TextView display;
    String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = findViewById(R.id.result_layout).findViewById(R.id.result_content);
        display = view.findViewById(R.id.result_slot);
        String temp = R.string.hint_result + getIntent().getStringExtra("result");
        display.setText(temp);

        uriString = getIntent().getStringExtra("imageURI");
        Uri imageURI = Uri.parse(uriString);
        ImageView preview = view.findViewById(R.id.imagePreview);
        try {
            InputStream is = getContentResolver().openInputStream(imageURI);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            preview.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent recoverIntent = new Intent(ResultActivity.this, PreviewActivity.class);
        recoverIntent.putExtra("imageURI", uriString);
        startActivity(recoverIntent);
        finish();
        return true;
    }
}
