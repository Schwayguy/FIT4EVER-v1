package com.example.efe.fit4ever;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MyWorkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workout);

        ImageView flowchart = (ImageView) findViewById(R.id.flowchart);
        Bitmap pic1 = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        int pic1Height = pic1.getHeight();
        Bitmap pic2 = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        int pic2Height = pic2.getHeight();
        Bitmap combinedBitmap = getCombinedBitmap(pic1Height,pic2Height,pic1, pic2);

        //bu fonksiyonu tüm resimler çizildikten sonra çağır
        Bitmap resized = Bitmap.createScaledBitmap(combinedBitmap,(int)(combinedBitmap.getWidth()*0.2), (int)(combinedBitmap.getHeight()*0.2), true);

        flowchart.setImageBitmap(resized);
    }

    public Bitmap getCombinedBitmap(int b1Height, int b2Height, Bitmap b1, Bitmap b2) {
        Bitmap drawnBitmap = null;

        try {
            drawnBitmap = Bitmap.createBitmap(300, b1Height+b2Height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(drawnBitmap);
            // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
            canvas.drawBitmap(b1, 0, 0, null);
            canvas.drawBitmap(b2, 0, b1Height, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawnBitmap;
    }
}