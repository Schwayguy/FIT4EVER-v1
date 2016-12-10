package com.example.efe.fit4ever;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class MyWorkout extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workout);

        ImageView flowchart = (ImageView) findViewById(R.id.flowchart);
        Bitmap circle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        int circleHeight = circle.getHeight();
        Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        int arrowHeight = arrow.getHeight();
        Bitmap vertextop = BitmapFactory.decodeResource(getResources(), R.drawable.vertextop);
        int vertexTheight = vertextop.getHeight();
        Bitmap line = BitmapFactory.decodeResource(getResources(), R.drawable.line);
        int lineheight = line.getHeight();
        Bitmap vertexbot = BitmapFactory.decodeResource(getResources(), R.drawable.vertexbot);
        int vertexBheight = vertexbot.getHeight();


        Bitmap combinedBitmap = drawTwo(circleHeight, arrowHeight, circle, arrow);

        //bu fonksiyonu tüm resimler çizildikten sonra çağır


        File logFile = new File(getExternalFilesDir(null), getIntent().getStringExtra("PROGID") + ".xls");
        FileInputStream myInput = null;
        if (logFile.exists()) {
            try {
                myInput = new FileInputStream(logFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                int i;
                for (i = 2; i <= rowNumber; i++) {
                    CellReference cellReference = new CellReference("A" + i);
                    Row row = mySheet.getRow(cellReference.getRow());
                    Cell cell = row.getCell(cellReference.getCol());

                    Bitmap workout = drawMiddle(vertexTheight, lineheight, vertexBheight, arrowHeight, vertextop, line, vertexbot, arrow);

                    int workHeight = workout.getHeight();
                    int combHeight = combinedBitmap.getHeight();
                    combinedBitmap = drawTwo(combHeight, workHeight, combinedBitmap, workout);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        combinedBitmap = drawTwo(combinedBitmap.getHeight(), circleHeight, combinedBitmap, circle);

        Bitmap reDo = Bitmap.createBitmap(2800, combinedBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(reDo);
        // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
        canvas.drawBitmap(combinedBitmap, 300, 0, null);

        Bitmap resized = Bitmap.createScaledBitmap(reDo, (int) (reDo.getWidth() * 0.2), (int) (reDo.getHeight() * 0.2), true);
        //Bitmap resized = Bitmap.createBitmap(combinedBitmap,0,0,(int)(combinedBitmap.getWidth()*0.5), (int)(combinedBitmap.getHeight()*0.5));

        FrameLayout layout = (FrameLayout) findViewById(R.id.flowFrame);
        if (logFile.exists()) {
            try {
                myInput = new FileInputStream(logFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                int i;
                for (i = 2; i <= rowNumber; i++) {
                    CellReference idRef = new CellReference("A" + i);
                    CellReference nameRef = new CellReference("B" + i);
                    CellReference periodRef = new CellReference("G" + i);
                    CellReference repeatRef = new CellReference("H" + i);
                    Row row = mySheet.getRow(idRef.getRow());
                    Cell cell1 = row.getCell(idRef.getCol());
                    Cell cell2 = row.getCell(nameRef.getCol());
                    Cell cell3 = row.getCell(periodRef.getCol());
                    Cell cell4 = row.getCell(repeatRef.getCol());
                    Log.d("try", "cell Value: " + cell1.toString() + " " + cell2.toString() + " " + cell3.toString() + " " + cell4.toString());


                    resized = resized.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas2 = new Canvas(resized);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    // text color - #3D3D3D
                    paint.setColor(Color.rgb(61, 61, 61));
                    // text size in pixels
                    paint.setTextSize((int) 32);
                    // text shadow
                    paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
                    canvas2.drawText(cell2.toString() + " x " + cell4.toString(), 260, (float) ((280 + (564 * 0.6) * (i - 2))), paint);
                    canvas2.drawText(cell3.toString() + " x ", 0, (float) ((280 + (564 * 0.6) * (i - 2))), paint);
                    canvas2.drawText("Rest", 200, (float) ((360 + (564 * 0.6) * (i - 2))), paint);

                    //video adresini değiştir ve buttonlara onclicklistener ekle
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/1479662732648.webm",
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    thumb = Bitmap.createScaledBitmap(thumb, (int) (thumb.getWidth() ), (int) (thumb.getHeight() ), true);
                    ImageButton btn = new ImageButton(this);
                    btn.setLayoutParams(new ScrollView.LayoutParams(144,108));
                    Drawable background = new BitmapDrawable(getResources(), thumb);
                    btn.setBackground(background);
                    btn.bringToFront();
                    btn.setY((float) (290 + (564 * 0.6) * (i - 2)));
                    btn.setX(220);
                    layout.addView(btn);
                    flowchart.setImageBitmap(resized);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public Bitmap drawTwo(int b1Height, int b2Height, Bitmap b1, Bitmap b2) {
        Bitmap drawnBitmap = null;

        try {
            drawnBitmap = Bitmap.createBitmap(2500, b1Height + b2Height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(drawnBitmap);
            // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
            canvas.drawBitmap(b1, 0, 0, null);
            canvas.drawBitmap(b2, 0, b1Height, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawnBitmap;
    }

    public Bitmap drawMiddle(int b1Height, int b2Height, int b3Height, int b4Height, Bitmap b1, Bitmap b2, Bitmap b3, Bitmap b4) {
        Bitmap drawnBitmap = null;

        try {
            drawnBitmap = Bitmap.createBitmap(2500, b1Height + b2Height + b3Height + b4Height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(drawnBitmap);
            // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .


            canvas.drawBitmap(b1, 0, 0, null);
            canvas.drawBitmap(b2, 0, b1Height, null);
            canvas.drawBitmap(b3, 0, b1Height + b2Height, null);
            canvas.drawBitmap(b4, 0, b1Height + b2Height + b3Height, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawnBitmap;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MyWorkout Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}