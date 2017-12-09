package com.sz1358.transpixel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addContentView(R.layout.activity_dictionary, R.id.nav_dictionary);
        generateList();
    }

    public void generateList() {
        LinearLayout scrollView= findViewById(R.id.drawer_layout)
                .findViewById(R.id.dictionary_appbar)
                .findViewById(R.id.dictionary_content)
                .findViewById(R.id.dictionary_scroll)
                .findViewById(R.id.dictionary_list);
        Gson gson = new Gson();
        SharedPrefManager prefManager = SharedPrefManager.getInstance(DictionaryActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Type type = new TypeToken<ArrayList<Tuple>>() {}.getType();
        ArrayList<Tuple> dict = gson.fromJson(prefManager.getDict(), type);
        List<View> views = new ArrayList<>();

        if (dict.size() == 0) {
            return;
        }

        for (int i = 0; i < dict.size(); i++) {
            View view = inflater.inflate(R.layout.dictionary_item, null);
            TextView title = view.findViewById(R.id.item).findViewById(R.id.title);
            title.setText(dict.get(i).getWord());
            TextView language = view.findViewById(R.id.item).findViewById(R.id.language);
            language.setText(dict.get(i).getLangString());
            ImageView image = view.findViewById(R.id.item).findViewById(R.id.image);
            Uri imageURI = Uri.parse(dict.get(i).getUri());
            try {
                InputStream is = getContentResolver().openInputStream(imageURI);
                Bitmap picture = BitmapFactory.decodeStream(is);
                image.setImageBitmap(Bitmap.createScaledBitmap(picture, 140, 90, false));
            } catch (Exception e) {
                e.printStackTrace();
            }

            views.add(view);
        }

        for (int i = 0; i < views.size(); i++) {
            scrollView.addView(views.get(i));
        }
    }
}
