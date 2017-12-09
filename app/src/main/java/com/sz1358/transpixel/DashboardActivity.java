package com.sz1358.transpixel;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends BaseActivity {

    Uri imgURI;
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_dashboard, R.id.nav_dashboard);
    }

    // DashboardFragment button handler
    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imgFile = null;
            ContentValues values = new ContentValues();
            if (CLEAR_PERMISSION) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                        .format(new Date());
                String fileName = "IMAGE_" + timeStamp + ".jpg";
                values.put(MediaStore.Images.Media.TITLE, fileName);
                File output = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                imgFile = new File(output, fileName);
            } else {
                Toast.makeText(this, "File Write Permission Denied", Toast.LENGTH_LONG).show();
            }
            if (imgFile != null && CLEAR_PERMISSION) {
                imgURI = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI);
                startActivityForResult(takePictureIntent, ACTIVITY_CALL_CAMERA);
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void loadPhoto(View view) {
        if (Build.VERSION.SDK_INT < 19) {
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(pickIntent, "select picture"),
                    ACTIVITY_CALL_GALLERY);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, ACTIVITY_CALL_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_CALL_CAMERA: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(DashboardActivity.this, PreviewActivity.class);
                    intent.putExtra("tag", "from_dashboard");
                    intent.putExtra("imageURI", imgURI.toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Camera Call Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case ACTIVITY_CALL_GALLERY: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(DashboardActivity.this, PreviewActivity.class);
                    intent.putExtra("imageURI", data.getDataString());
                    intent.putExtra("tag", "from_dashboard");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Load Gallery Crash", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
