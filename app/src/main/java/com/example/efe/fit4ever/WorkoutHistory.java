package com.example.efe.fit4ever;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jxl.format.BoldStyle;

import static java.lang.Math.abs;

public class WorkoutHistory extends AppCompatActivity {
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        FileInputStream myInput = null;
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        File weightFile = new File(getExternalFilesDir(null), sharedPref.getString("userId", "") + ".xls");
        LinearLayout layout = (LinearLayout) findViewById(R.id.historyvertical);
        if (weightFile.exists()) {
            try {
                myInput = new FileInputStream(weightFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                int i;

                TextView userweightinfo1 = new TextView(this);
                userweightinfo1.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                userweightinfo1.setText("| Program Name | Date | Weight Change | BMI | FatRatio |  MuscleRatio | ");
                userweightinfo1.setTypeface(null, Typeface.BOLD);
               // userweightinfo1.setGravity(Gravity.LEFT);
                userweightinfo1.setPadding(0,5,0,0);
                layout.addView(userweightinfo1);
                View view2 = new View(this);
                view2.setBackgroundColor(0xFFC2BEBF);
                layout.addView(view2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));


                CellReference cellReference = new CellReference("E" + 2);
                Row row = mySheet.getRow(cellReference.getRow());
                if(row!=null) {
                    Cell cell = row.getCell(cellReference.getCol());
                    if (!cell.toString().equals("0.0")) {
                        CellReference weightRef = new CellReference("A" + 2);
                        CellReference fatRef = new CellReference("B" + 2);
                        CellReference muscleRef = new CellReference("C" + 2);
                        CellReference recordRef = new CellReference("D" + 2);
                        CellReference progidRef = new CellReference("E" + 2);
                        CellReference prognameRef = new CellReference("F" + 2);

                        Cell cell1 = row.getCell(weightRef.getCol());
                        Cell cell2 = row.getCell(recordRef.getCol());
                        Cell cell3 = row.getCell(progidRef.getCol());
                        Cell cell4 = row.getCell(prognameRef.getCol());
                        Cell cell5 = row.getCell(fatRef.getCol());
                        Cell cell6 = row.getCell(muscleRef.getCol());

                        if (!cell3.toString().isEmpty()) {
                            TextView userweightinfo = new TextView(this);
                            userweightinfo.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                            String progname = cell4.toString();
                            String date = cell2.toString();
                            String weightloss = cell1.toString();
                            String fatratio = cell5.toString();
                            String muscleratio = cell6.toString();
                            String height = sharedPref.getString("height", "");
                            float bmi = 0;
                            if (!height.isEmpty()) {
                                bmi = Float.parseFloat(weightloss) * 10000 / (Float.parseFloat(height) * Float.parseFloat(height));
                            }

                            userweightinfo.setText("| " + progname + " | " + date + " | " + weightloss + " | " + bmi + " | " + fatratio + " | " + muscleratio + " | ");
                            userweightinfo.setGravity(Gravity.LEFT);
                            userweightinfo.setPadding(0, 5, 0, 0);
                            layout.addView(userweightinfo);
                            view2 = new View(this);
                            view2.setBackgroundColor(0xFFC2BEBF);
                            layout.addView(view2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        }
                    }


                    int j;
                    for (i = 3; i <= rowNumber; i++) {


                        cellReference = new CellReference("E" + i);
                        row = mySheet.getRow(cellReference.getRow());
                        cell = row.getCell(cellReference.getCol());
                        if (!cell.toString().equals("0.0")) {

                            CellReference weightRef = new CellReference("A" + i);
                            CellReference fatRef = new CellReference("B" + i);
                            CellReference muscleRef = new CellReference("C" + i);
                            CellReference recordRef = new CellReference("D" + i);
                            CellReference progidRef = new CellReference("E" + i);
                            CellReference prognameRef = new CellReference("F" + i);

                            Cell cell1 = row.getCell(weightRef.getCol());
                            Cell cell2 = row.getCell(recordRef.getCol());
                            Cell cell3 = row.getCell(progidRef.getCol());
                            Cell cell4 = row.getCell(prognameRef.getCol());
                            Cell cell5 = row.getCell(fatRef.getCol());
                            Cell cell6 = row.getCell(muscleRef.getCol());
                            j = i - 1;
                            CellReference weightRefBefore = new CellReference("A" + j);
                            CellReference fatRefBefore = new CellReference("B" + j);
                            CellReference muscleRefBefore = new CellReference("C" + j);
                            CellReference progBefore = new CellReference("E" + j);
                            Row rowBefore = mySheet.getRow(weightRefBefore.getRow());
                            Cell cellw0 = rowBefore.getCell(weightRefBefore.getCol());
                            Cell cellf0 = rowBefore.getCell(fatRefBefore.getCol());
                            Cell cellm0 = rowBefore.getCell(muscleRefBefore.getCol());
                            Cell cellp0 = rowBefore.getCell(progBefore.getCol());
                            while (cellp0.toString().isEmpty()) {
                                j--;
                                weightRefBefore = new CellReference("A" + j);
                                rowBefore = mySheet.getRow(weightRefBefore.getRow());
                                cellp0 = rowBefore.getCell(progBefore.getCol());
                                cellw0 = rowBefore.getCell(weightRefBefore.getCol());
                                cellf0 = rowBefore.getCell(fatRefBefore.getCol());
                                cellm0 = rowBefore.getCell(muscleRefBefore.getCol());
                            }


                            if (!cell3.toString().isEmpty()) {
                                TextView userweightinfo = new TextView(this);
                                userweightinfo.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));

                                String progname = cell4.toString();
                                String date = cell2.toString();
                                String weightloss = cell1.toString();
                                String fatratio = cell5.toString();
                                String muscleratio = cell6.toString();
                                float delta = abs(Float.parseFloat(cellw0.toString()) - Float.parseFloat(cell1.toString()));
                                String height = sharedPref.getString("height", "");
                                float bmi = 0;
                                if (!height.isEmpty()) {
                                    bmi = Float.parseFloat(weightloss) * 10000 / (Float.parseFloat(height) * Float.parseFloat(height));
                                }
                                Log.d("weights", cellw0.toString() + " " + cell1.toString() + " " + cellf0.toString() + " " + cell5.toString() + " " + cellm0.toString() + " " + cell6.toString());
                                if ((Float.parseFloat(cellw0.toString()) < Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) < Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) < Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=green>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=red>" + fatratio + "</font>" + " | " + "<font color=green>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) < Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) > Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) < Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=green>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=green>" + fatratio + "</font>" + " | " + "<font color=green>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) < Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) < Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) > Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=red>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=red>" + fatratio + "</font>" + " | " + "<font color=red>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) > Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) > Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) > Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=red>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=green>" + fatratio + "</font>" + " | " + "<font color=red>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) > Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) > Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) < Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=green>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=green>" + fatratio + "</font>" + " | " + "<font color=green>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) > Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) < Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) > Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=red>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=red>" + fatratio + "</font>" + " | " + "<font color=red>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) < Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) > Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) < Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=green>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=green>" + fatratio + "</font>" + " | " + "<font color=green>" + muscleratio + "</font>" + " | "));
                                } else if ((Float.parseFloat(cellw0.toString()) < Float.parseFloat(cell1.toString())) && (Float.parseFloat(cellf0.toString()) > Float.parseFloat(cell5.toString())) && (Float.parseFloat(cellm0.toString()) < Float.parseFloat(cell6.toString())) && (delta >= 2)) {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + "<font color=green>" + weightloss + "</font>" + " | " + bmi + " | " + "<font color=green>" + fatratio + "</font>" + " | " + "<font color=green>" + muscleratio + "</font>" + " | "));
                                } else {
                                    userweightinfo.setText(Html.fromHtml("| " + progname + " | " + date + " | " + weightloss + " | " + bmi + " | " + fatratio + " | " + muscleratio + " | "));
                                }
                                userweightinfo.setGravity(Gravity.LEFT);
                                userweightinfo.setPadding(0, 5, 0, 0);
                                layout.addView(userweightinfo);
                                view2 = new View(this);
                                view2.setBackgroundColor(0xFFC2BEBF);
                                layout.addView(view2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));


                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
