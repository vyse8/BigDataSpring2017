package com.example.mattv.cognitivevisualrecognitiontracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static com.example.mattv.cognitivevisualrecognitiontracker.R.id.webview;

@SuppressLint("SetJavaScriptEnabled")
public class MetricsActivity extends AppCompatActivity {

    public ArrayList<String> question = new ArrayList<String>();
    public ArrayList<String> answer = new ArrayList<String>();
    public ArrayList<String> imgName = new ArrayList<String>();
    public ArrayList<String> date = new ArrayList<String>();
    int num1, num2, num3, num4, num5;
    String x1, x2, x3, x4, x5;
    WebView metricsView;
    Spinner spCharts;
    List<String> listCharts;
    List<String> listHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);

        //Intent intent = getIntent();
        //num1 = intent.getIntExtra("NUM1", 0);
        //num2 = intent.getIntExtra("NUM2", 0);
        //num3 = intent.getIntExtra("NUM3", 0);
        //num4 = intent.getIntExtra("NUM4", 0);
        //num5 = intent.getIntExtra("NUM5", 0);
        //getMetrics();

        spCharts = (Spinner) findViewById(R.id.spcharts);
        listCharts = new ArrayList<String>();
        listCharts.add("Questions Asked Per Date");
        listCharts.add("Pie Chart");
        listCharts.add("Pie Chart 3D");
        listCharts.add("Scatter Chart");
        listCharts.add("Column Chart");
        listCharts.add("Bar Chart");
        listCharts.add("Histogram");
        listCharts.add("Line Chart");
        listCharts.add("Area Chart");

        listHtml = new ArrayList<String>();
        listHtml.add("file:///android_asset/questions_asked_per_date.html");
        listHtml.add("file:///android_asset/pie_chart.html");
        listHtml.add("file:///android_asset/pie_chart_3d.html");
        listHtml.add("file:///android_asset/scatter_chart.html");
        listHtml.add("file:///android_asset/column_chart.html");
        listHtml.add("file:///android_asset/bar_chart.html");
        listHtml.add("file:///android_asset/histogram.html");
        listHtml.add("file:///android_asset/line_chart.html");
        listHtml.add("file:///android_asset/area_chart.html");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listCharts);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCharts.setAdapter(dataAdapter);
        spCharts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String chartHtml = listHtml.get(parent.getSelectedItemPosition());
                if(chartHtml == "file:///android_asset/questions_asked_per_date.html")
                {
                    date.clear();
                    question.clear();
                    getMetrics(0);
                    if(question.size() == 1)
                    {
                        num1 = Integer.valueOf(question.get(0));
                        x1 = date.get(0);
                    }
                    if(question.size() == 2)
                    {
                        num1 = Integer.valueOf(question.get(0));
                        x1 = date.get(0);
                        num2 = Integer.valueOf(question.get(1));
                        x2 = date.get(1);
                    }
                }
                metricsView.loadUrl(chartHtml);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }});
        metricsView = (WebView) findViewById(webview);
        metricsView.addJavascriptInterface(new WebAppInterface(), "Android");
        WebSettings webSettings = metricsView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //metricsView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        metricsView.loadUrl("file:///android_asset/chart.html");
        //String summary = "<html><body>You scored <b>192</b> points.</body></html>";
        //metricsView.loadData(summary, "text/html", null);
        //String CHART_URL = "http://chart.apis.google.com/chart?cht=p3&chs=300x300&chd=e:TNTNTNGa&chts=000000,16&chtt=A+Better+Web&chl=Hello|Hi|anas|Explorer&chco=FF5533,237745,9011D3,335423&chdl=Apple|Mozilla|Google|Microsoft";
        //metricsView.loadUrl(CHART_URL);
    }
    public class WebAppInterface {

        @JavascriptInterface
        public int getNum1() {
            return num1;
        }

        @JavascriptInterface
        public int getNum2() {
            return num2;
        }

        @JavascriptInterface
        public int getNum3() {
            return num3;
        }

        @JavascriptInterface
        public int getNum4() {
            return num4;
        }

        @JavascriptInterface
        public int getNum5() {
            return num5;
        }
        @JavascriptInterface
        public String getx1() {
            return x1;
        }

        @JavascriptInterface
        public String getx2() {
            return x2;
        }

        @JavascriptInterface
        public String getx3() {
            return x3;
        }

        @JavascriptInterface
        public String getx4() {
            return x4;
        }

        @JavascriptInterface
        public String getx5() {
            return x5;
        }

    }

    public void getMetrics(int chartID)
    {
        SQLiteDatabase database = openOrCreateDatabase("metrics.db", MODE_PRIVATE, null);
        Cursor cursor = null;
        if(chartID == 0)
        {
            cursor = database.rawQuery("select Date, Count(Question) AS 'Question' from tblHistoryLog group by Date order by Date", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                date.add(cursor.getString(0));
                question.add(cursor.getString(1));
                System.out.println("Date: " + cursor.getString(0));
                System.out.println("Question: " + cursor.getString(1));
                cursor.moveToNext();
            }
        }
        else
        {
            cursor = database.rawQuery("select * from tblHistoryLog order by Date", null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                question.add(cursor.getString(0));
                answer.add(cursor.getString(1));
                imgName.add(cursor.getString(2));
                date.add(cursor.getString(3));
                System.out.println("Question: " + cursor.getString(0));
                System.out.println("Answer: " + cursor.getString(1));
                System.out.println("Image Name: " + cursor.getString(2));
                System.out.println("Date: " + cursor.getString(3));
                cursor.moveToNext();
            }
        }

        cursor.close();
        database.close();
    }
}
