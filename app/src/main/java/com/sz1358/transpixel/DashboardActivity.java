package com.sz1358.transpixel;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends BaseActivity {

    Uri imgURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_dashboard);
    }

    // DashboardFragment button handler
    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File imgFile = null;
            ContentValues values = new ContentValues();
            if (CLEAR_PERMISSION) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                        .format(new Date());
                String fileName = "IMAGE_" + timeStamp + ".jpg";
                values.put(MediaStore.Images.Media.TITLE, fileName);
                File output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                imgFile = new File(output, fileName);
            } else {
                Toast.makeText(this, "File Write Permission Denied", Toast.LENGTH_SHORT).show();
            }
            if (imgFile != null && CLEAR_PERMISSION) {
                imgURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI);
                startActivityForResult(takePictureIntent, ACTIVITY_CALL_CAMERA);
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadPhoto(View view) {
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickIntent, "select picture"), ACTIVITY_CALL_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_CALL_CAMERA: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(DashboardActivity.this, PreviewActivity.class);
                    intent.putExtra("imagePath", getAbsolutePath(this, imgURI));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Camera Call Cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case ACTIVITY_CALL_GALLERY: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(DashboardActivity.this, PreviewActivity.class);
//                    String path = getAbsolutePath(this, imgURI);
                    intent.putExtra("imageUri", data.getDataString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Load Gallery Crash", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getAbsolutePath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        } else
            return null;
    }
}
