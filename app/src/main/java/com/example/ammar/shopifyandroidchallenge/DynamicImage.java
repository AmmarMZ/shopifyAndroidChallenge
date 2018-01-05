package com.example.ammar.shopifyandroidchallenge;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DynamicImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dyanmic_image);

        Bundle bundle = getIntent().getExtras();
        String title = (String) bundle.get("title");
        Bitmap bitmap = (Bitmap) bundle.get("bitmap");
        String price = (String) bundle.get("price");
        Long pID =   (Long)bundle.get("pID");
        Double weight = (Double) bundle.get("weight");
        Long stock = (Long) bundle.get("stock");

        ImageView picture = (ImageView) findViewById(R.id.pictureEnlarged);
        TextView textTitle = (TextView) findViewById(R.id.picTitle);
        TextView textPrice = (TextView) findViewById(R.id.price);
        TextView textPID = (TextView) findViewById(R.id.pID);
        TextView textWeight = (TextView) findViewById(R.id.weight);
        TextView textStock = (TextView) findViewById(R.id.stock);

        picture.setImageBitmap(bitmap);
        textTitle.setText(title);
        textPrice.setText("$" + price);
        textPID.setText(String.valueOf(pID));
        textWeight.setText(String.valueOf(weight) + "kg");
        textStock.setText(String.valueOf(stock));

    }
}
