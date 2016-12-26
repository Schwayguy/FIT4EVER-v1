package com.example.efe.fit4ever;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class MyWorkout extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    SharedPreferences sharedPref;

    Connection conn;
    Statement statement = null;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workout);
        Intent intent = getIntent();
        String progId = intent.getStringExtra("PROGID");
        TextView progTitle = (TextView) findViewById(R.id.title);
        TextView progowner = (TextView) findViewById(R.id.progowner2);
        TextView ratingtext = (TextView) findViewById(R.id.ratingText2);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        TextView description = (TextView) findViewById(R.id.description2);


        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        if (inet != null) {
            CONN();
            try {
                statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();            }
        }

        ResultSet result = null;
        try {
            result = statement.executeQuery("select Title, Rate, Information, Name, Surname from Programs INNER JOIN Users on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] AND" +
                    "[dbo].[Programs].[ID] = '" + progId + "'");
        } catch (SQLException e) {
            Log.e("ERRORc", e.getMessage());
        }
        try {
            assert result != null;
            while (result.next()) {
                progTitle.setText(result.getString("Title"));
                progowner.setText("Author: "+result.getString("Name")+" "+result.getString("Surname"));
                ratingtext.setText(result.getString("Rate"));
                ratingBar.setRating(Float.parseFloat(result.getString("Rate")));
                description.setText(result.getString("Information"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button btnPlay = (Button) findViewById(R.id.startButton);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("progId",getIntent().getStringExtra("PROGID") );
                editor.apply();

                Intent intent = new Intent(getBaseContext(), PlayWorkout.class);
                intent.putExtra("PROGID",  getIntent().getStringExtra("PROGID"));
                startActivity(intent);

            }
        });

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
        }else if(inet != null) {
            Download();
        }else{
            Toast.makeText(this,"You need internet connection to redownload your program.",Toast.LENGTH_SHORT).show();
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
                    CellReference periodRef = new CellReference("H" + i);
                    CellReference repeatRef = new CellReference("I" + i);
                    CellReference videoRef = new CellReference("C" + i);
                    Row row = mySheet.getRow(idRef.getRow());
                    Cell cell1 = row.getCell(idRef.getCol());
                    Cell cell2 = row.getCell(nameRef.getCol());
                    Cell cell3 = row.getCell(periodRef.getCol());
                    Cell cell4 = row.getCell(repeatRef.getCol());
                    Cell cell5 = row.getCell(videoRef.getCol());
                    Log.d("try", "cell Value: " + cell1.toString() + " " + cell2.toString() + " " + cell3.toString() + " " + cell4.toString());

                    float newx = resized.getWidth();

                    resized = resized.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas2 = new Canvas(resized);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    // text color - #3D3D3D
                    paint.setColor(Color.rgb(61, 61, 61));
                    // text size in pixels
                    paint.setTextSize((int) 32);
                    // text shadow
                    paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
                    canvas2.drawText(cell2.toString() + " x " + cell4.toString(), (float) (newx*0.65), (float) (((newx*0.65) + (newx * 0.8) * (i - 2))), paint);
                    canvas2.drawText(cell3.toString() + " x ", 0, (float) (((newx*0.65) + (newx * 0.8) * (i - 2))), paint);
                    canvas2.drawText("Rest", (float) (newx*0.45), (float) (((newx*0.9) + (newx * 0.8) * (i - 2))), paint);

                    //video adresini değiştir ve buttonlara onclicklistener ekle


                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/67.mp4",
                            MediaStore.Video.Thumbnails.MINI_KIND);  //+cell5.toString()
                    thumb = Bitmap.createScaledBitmap(thumb, (int) (thumb.getWidth() ), (int) (thumb.getHeight() ), true);
                    ImageButton btn = new ImageButton(this);
                    btn.setLayoutParams(new ScrollView.LayoutParams((int) (newx*0.36), (int) (newx*0.27)));//sınırları yüzdeli belirle
                    Drawable background = new BitmapDrawable(getResources(), thumb);
                    btn.setBackground(background);
                    btn.bringToFront();
                    btn.setY((float) ((newx*0.7) + (newx * 0.8) * (i - 2)));
                    btn.setX((float) (newx*0.5));
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

    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        conn = null;
        String ConnURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://192.168.1.23:1433/Workout";
            conn = DriverManager.getConnection(ConnURL, "efe", "e1234567");
            System.out.println("connected");
        } catch (SQLException se) {
            Log.e("ERRO1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO2", e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong while trying to connect to the database, please check your internet connection.", Toast.LENGTH_LONG).show();
        }
        return conn;
    }

    public void Download() {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        File logFile = new File(getExternalFilesDir(null),getIntent().getStringExtra("PROGID") + ".xls");

        // şuan her zaman yeni dosya indiriyo sonra düzelt
        if(logFile.exists()){
            Toast.makeText(this,"It already exists.",Toast.LENGTH_SHORT).show();
        }else {
            try {
                ResultSet workoutDownloadInfo = statement.executeQuery("USE [Workout] SELECT  WorkoutID ,Name, Video, Information ,Rate, Title ,Subtitle , Period ,Repeat, Queue" +
                        " FROM [dbo].[Workouts] INNER JOIN [dbo].[PWRelation] on [dbo].[Workouts].[ID]=[dbo].[PWRelation].[WorkoutID] AND" +
                        " [dbo].[PWRelation].[IsActive]=1 AND [ProgramID]='" + getIntent().getStringExtra("PROGID") + "' order by Queue");

                Workbook wb = new HSSFWorkbook();
                Cell c = null;
                CellStyle cs = wb.createCellStyle();
                cs.setFillForegroundColor(HSSFColor.LIME.index);
                cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                Sheet sheet1 = null;
                sheet1 = wb.createSheet("Workout Sheet");

                Row headerRow = sheet1.createRow(0);

                c = headerRow.createCell(0);
                c.setCellValue("WorkoutID");
                c.setCellStyle(cs);

                c = headerRow.createCell(1);
                c.setCellValue("Name");
                c.setCellStyle(cs);

                c = headerRow.createCell(2);
                c.setCellValue("Video");
                c.setCellStyle(cs);

                c = headerRow.createCell(3);
                c.setCellValue("Information");
                c.setCellStyle(cs);

                c = headerRow.createCell(4);
                c.setCellValue("Rate");
                c.setCellStyle(cs);

                c = headerRow.createCell(5);
                c.setCellValue("Title");
                c.setCellStyle(cs);

                c = headerRow.createCell(6);
                c.setCellValue("Subtitle");
                c.setCellStyle(cs);

                c = headerRow.createCell(7);
                c.setCellValue("Period");
                c.setCellStyle(cs);

                c = headerRow.createCell(8);
                c.setCellValue("Repeat");
                c.setCellStyle(cs);

                c = headerRow.createCell(9);
                c.setCellValue("Queue");
                c.setCellStyle(cs);

                sheet1.setColumnWidth(0, (15 * 700));
                sheet1.setColumnWidth(1, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(3, (15 * 500));
                sheet1.setColumnWidth(4, (15 * 500));
                sheet1.setColumnWidth(5, (15 * 500));
                sheet1.setColumnWidth(6, (15 * 200));
                sheet1.setColumnWidth(7, (15 * 200));
                sheet1.setColumnWidth(8, (15 * 200));
                sheet1.setColumnWidth(9, (15 * 200));

                FileOutputStream os = null;
                int row = 1;
                while (workoutDownloadInfo.next()) {
                    Row dataRow = sheet1.createRow(row);
                    c = dataRow.createCell(0);
                    c.setCellValue(workoutDownloadInfo.getString("WorkoutID"));
                    c = dataRow.createCell(1);
                    c.setCellValue(workoutDownloadInfo.getString("Name"));
                    c = dataRow.createCell(2);
                    c.setCellValue(workoutDownloadInfo.getString("Video"));
                    c = dataRow.createCell(3);
                    c.setCellValue(workoutDownloadInfo.getString("Information"));
                    c = dataRow.createCell(4);
                    c.setCellValue(Float.toString(workoutDownloadInfo.getFloat("Rate")));
                    c = dataRow.createCell(5);
                    c.setCellValue(workoutDownloadInfo.getString("Title"));
                    c = dataRow.createCell(6);
                    c.setCellValue(workoutDownloadInfo.getString("Subtitle"));
                    c = dataRow.createCell(7);
                    c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Period")));
                    c = dataRow.createCell(8);
                    c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Repeat")));
                    c = dataRow.createCell(9);
                    c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Queue")));

                    Log.d("video", workoutDownloadInfo.getString("Video"));
/*
                URL url = new URL("file://212.252.141.117/Users/efe/Desktop/bitirmeVers_4/Workout.Web/Assets/Videos/"+workoutDownloadInfo.getString("Video"));

                //Open a connection to that URL.
                URLConnection ucon = url.openConnection();

                File file = new File("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/"+workoutDownloadInfo.getString("Video"));
                InputStream is = ucon.getInputStream();
                BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buff = new byte[5 * 1024];

                //Read bytes (and store them) until there is nothing more to read(-1)
                int len;
                while ((len = inStream.read(buff)) != -1)
                {
                    outStream.write(buff,0,len);
                }

                //clean up
                outStream.flush();
                outStream.close();
                inStream.close();*/

                    row++;
                }
                os = new FileOutputStream(logFile);
                wb.write(os);

                Toast.makeText(this, "Download successful.", Toast.LENGTH_SHORT).show();
                os.close();
            } catch (SQLException e) {
                Log.e("error download", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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