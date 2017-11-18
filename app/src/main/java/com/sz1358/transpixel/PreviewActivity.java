package com.sz1358.transpixel;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import java.io.IOException;

public class PreviewActivity extends BaseActivity {

    String imagePath;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_preview);

        imagePath = getIntent().getStringExtra("imagePath");
        ImageView preview = findViewById(R.id.drawer_layout)
                .findViewById(R.id.preview_appbar)
                .findViewById(R.id.preview_content)
                .findViewById(R.id.imagePreview);
        if (imagePath != null) {
            preview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        } else {
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            try {
                preview.setImageBitmap(MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
