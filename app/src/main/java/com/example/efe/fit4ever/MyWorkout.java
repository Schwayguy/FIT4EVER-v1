package com.example.efe.fit4ever;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.os.AsyncTask;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    Connection conn;
    Statement statement = null;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workout);
        Intent intent = getIntent();
        final String progId = intent.getStringExtra("PROGID");
        final TextView progTitle = (TextView) findViewById(R.id.title);
        TextView progowner = (TextView) findViewById(R.id.progowner2);
        TextView ratingtext = (TextView) findViewById(R.id.ratingText2);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        TextView description = (TextView) findViewById(R.id.description2);


        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        if (inet != null) {
            CONN();
            try {
                statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        File programsFile = new File(getExternalFilesDir(null), "programs.xls");
        FileInputStream myInput = null;
        try {
            myInput = new FileInputStream(programsFile);
            POIFSFileSystem myFileSystem = null;
            myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = null;
            myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            int rowNumber = mySheet.getPhysicalNumberOfRows();
            int i=2;
            for (i = 2; i <= rowNumber; i++) {
                CellReference userRef = new CellReference("L" + i);
                CellReference titleRef = new CellReference("F" + i);
                CellReference idRef = new CellReference("A" + i);
                CellReference creatornameRef = new CellReference("D" + i);
                CellReference rateRef = new CellReference("B" + i);
                CellReference infoRef = new CellReference("G" + i);
                Row row = mySheet.getRow(userRef.getRow());
                Cell cell = row.getCell(idRef.getCol());
                Cell cell2 = row.getCell(titleRef.getCol());
                Cell cell4 = row.getCell(creatornameRef.getCol());
                Cell cell5 = row.getCell(rateRef.getCol());
                Cell cell6 = row.getCell(infoRef.getCol());
                if (cell.toString().equals(progId)) {
                    progTitle.setText(cell2.toString());
                    progowner.setText("Author: " +cell4.toString());
                    ratingtext.setText(cell5.toString());
                    ratingBar.setRating(Float.parseFloat(cell5.toString()));
                    description.setText(cell6.toString());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (inet != null) {
            ResultSet result = null;
            try {
                result = statement.executeQuery("select IsDeleted,IsEditable,IsActive,Rate from Programs where" +
                        "[dbo].[Programs].[ID] = '" + progId + "'");
            } catch (SQLException e) {
                Log.e("ERRORc", e.getMessage());
            }
            try {
                assert result != null;
                while (result.next()) {
                    ratingtext.setText(result.getString("Rate"));
                    ratingBar.setRating(Float.parseFloat(result.getString("Rate")));
                    //aktiflik uyarısını burada yap
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        Button btnPlay = (Button) findViewById(R.id.startButton);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("progId", getIntent().getStringExtra("PROGID"));
                editor.putString("progname", progTitle.getText().toString());
                editor.apply();

                Intent intent = new Intent(getBaseContext(), PlayWorkout.class);
                intent.putExtra("PROGID", getIntent().getStringExtra("PROGID"));
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
        myInput = null;
        if (logFile.exists()) {
            try {
                myInput = new FileInputStream(logFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                int i;
                for (i = 2; i <= rowNumber; i++) {
                    CellReference cellReference = new CellReference("B" + i);
                    Row row = mySheet.getRow(cellReference.getRow());
                    Cell cell = row.getCell(cellReference.getCol());
                    if (!cell.toString().equals("Rest")) {
                        Bitmap workout = drawMiddle(vertexTheight, lineheight, vertexBheight, arrowHeight, vertextop, line, vertexbot, arrow);

                        int workHeight = workout.getHeight();
                        int combHeight = combinedBitmap.getHeight();
                        combinedBitmap = drawTwo(combHeight, workHeight, combinedBitmap, workout);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }  else {
            Toast.makeText(this, "You need internet connection to redownload your program.", Toast.LENGTH_SHORT).show();
        }
        combinedBitmap = drawTwo(combinedBitmap.getHeight(), circleHeight, combinedBitmap, circle);

        Bitmap reDo = Bitmap.createBitmap(3300, combinedBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(reDo);
        // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
        canvas.drawBitmap(combinedBitmap, 300, 0, null);

        Bitmap resized = Bitmap.createScaledBitmap(reDo, (int) (reDo.getWidth() *0.2), (int) (reDo.getHeight() *0.2), true);
        //Bitmap resized = Bitmap.createBitmap(combinedBitmap,0,0,(int)(combinedBitmap.getWidth()*0.5), (int)(combinedBitmap.getHeight()*0.5));

        FrameLayout layout = (FrameLayout) findViewById(R.id.flowFrame);
        if (logFile.exists()) {
            try {
                myInput = new FileInputStream(logFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                int i = 0;
                int j = 2;
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
                    final Cell cell5 = row.getCell(videoRef.getCol());
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

                    if (cell2.toString().equals("Rest")) {
                        canvas2.drawText(cell2.toString() + " for " + String.valueOf((int)Float.parseFloat(cell4.toString())) + " seconds", (float) (newx * 0.4), (float) (((newx * 0.8) + (newx * 0.685) * (i - 3))), paint);
                    } else {
                        if (Float.parseFloat(cell4.toString()) < 30) {
                            canvas2.drawText(cell2.toString() + " x " + String.valueOf((int)Float.parseFloat(cell4.toString())), (float) (newx * 0.55), (float) (((newx * 0.575) + (newx * 0.685) * (j - 2))), paint);
                        } else {
                            if (Float.parseFloat(cell4.toString()) >= 60) {
                                int minute = (int) (Float.parseFloat(cell4.toString()) / 60);
                                int seconds = (int) (Float.parseFloat(cell4.toString()) % 60);

                                canvas2.drawText(cell2.toString() + " for " +minute + ":" + seconds , (float) (newx * 0.55), (float) (((newx * 0.575) + (newx * 0.685) * (j - 2))), paint);
                            } else {
                                canvas2.drawText(cell2.toString() + " for " +"00:" + String.valueOf((int)Float.parseFloat(cell4.toString()))  , (float) (newx * 0.55), (float) (((newx * 0.575) + (newx * 0.685) * (j - 2))), paint);
                            }
                        }
                            if (Float.parseFloat(cell3.toString()) < 30) {
                                canvas2.drawText(String.valueOf((int)Float.parseFloat(cell3.toString())) + " x ", 0, (float) (((newx * 0.575) + (newx * 0.685) * (j - 2))), paint);
                            } else {
                                if (Float.parseFloat(cell3.toString()) >= 60) {
                                    int minute = (int) (Float.parseFloat(cell3.toString()) / 60);
                                    int seconds = (int) (Float.parseFloat(cell3.toString()) % 60);
                                    canvas2.drawText(minute + ":" + seconds , 0, (float) (((newx * 0.575) + (newx * 0.685) * (j - 2))), paint);
                                } else {
                                    canvas2.drawText("00:" + String.valueOf((int)Float.parseFloat(cell3.toString())) , 0, (float) (((newx * 0.575) + (newx * 0.685) * (j - 2))), paint);
                                }
                            }

                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/67.mp4",
                                MediaStore.Video.Thumbnails.MINI_KIND);  //+cell5.toString()
                        thumb = Bitmap.createScaledBitmap(thumb, (int) (thumb.getWidth()), (int) (thumb.getHeight()), true);
                        ImageButton btn = new ImageButton(this);
                        btn.setLayoutParams(new ScrollView.LayoutParams((int) (newx * 0.36), (int) (newx * 0.27)));//sınırları yüzdeli belirle
                        Drawable background = new BitmapDrawable(getResources(), thumb);
                        btn.setBackground(background);
                        btn.bringToFront();
                        btn.setY((float) ((newx * 0.575) + (newx * 0.685) * (j - 2)));
                        btn.setX((float) (newx * 0.4));
                        final String finalI =String.valueOf(i);
                        btn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                Intent intent = new Intent(getBaseContext(), ShowWorkout.class);
                                intent.putExtra("PROGID", progId);
                                intent.putExtra("WORKOUTID",cell5.toString());
                                intent.putExtra("ROWID", finalI);
                                startActivity(intent);

                            }
                        });
                        layout.addView(btn);
                        flowchart.setImageBitmap(resized);
                        j++;
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        }
    }
    public Bitmap drawTwo(int b1Height, int b2Height, Bitmap b1, Bitmap b2) {
        Bitmap drawnBitmap = null;

        try {
            drawnBitmap = Bitmap.createBitmap(3000, b1Height + b2Height, Bitmap.Config.ARGB_8888);

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
            drawnBitmap = Bitmap.createBitmap(3000, b1Height + b2Height + b3Height + b4Height, Bitmap.Config.ARGB_8888);

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
          /*  ConnURL = "jdbc:jtds:sqlserver://fit4ever1.database.windows.net:1433/Workout;user=fit4ever1;password=Bvmguxg2" +
                    ";encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            conn = DriverManager.getConnection(ConnURL, "fit4ever1", "Bvmguxg2");*/
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


    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */

}