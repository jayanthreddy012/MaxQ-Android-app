package com.active.maxq;

/**
 * Created by Trung on 1/11/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.AxisBase;
//import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import java.lang.Integer;

import me.anwarshahriar.calligrapher.Calligrapher;


public class GraphActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener{
    private LineChart mChart;
    private static final String TAG = "GRAPH";
    public static String[] sensorData;
    ArrayList<Entry> entries = new ArrayList<>();
    public static ArrayList<Float> stringid = new ArrayList<>();
    public static ArrayList<Float> temperature = new ArrayList<>();
    public static ArrayList<Float> dateTime = new ArrayList<>();
    public static ArrayList<Long> unixTime = new ArrayList<>();
    public static ArrayList<Integer> lidStatus = new ArrayList<>();
    final static String fileName = "/data.csv";
    final static String graphName = "/graph";
    final static String path = Environment.getExternalStorageDirectory().getPath() + "/Documents" ;
    public Button btnpdf;
    public static String avgTemp;
    public static String maxTemp;
    public static String minTemp;
    public static String beginDate;
    public static String endDate;
    public static String timeInterval;

    private TextView average_temperature, maximum_temperature, minimum_temperature, elap_time , device_name;


    public GraphActivity() {
        super();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Log.i(TAG, "onChartGestureStart: X: " +me.getX()+ "Y:" +me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
       // Log.i(TAG, "onChartGestureEnd:" +lastPerformedGesture);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        //Log.i(TAG,"onChartLongPressed");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        //Log.i(TAG,"onChartLongTapped");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        //Log.i(TAG,"onChartSingleTapped");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
       // Log.i(TAG, "onChartFling: veloX:" + velocityX + "veloY" +velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
       // Log.i(TAG,"onChartScale: ScaleX:" + scaleX + "ScaleY:" +scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
       //Log.i(TAG,"onChartTranslate: dX:" + dX + "dY:" + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i(TAG,"onValueSelected:" + e.toString());
        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        //TextView t = (TextView) findViewById(R.id.point);
       // t.setText(e.toString());
        // Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {
        Log.i(TAG,"onChartNothing");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "fonts/GothamLight.ttf", true);
        btnpdf=(Button)findViewById(R.id.btnpdf);
        mChart = (LineChart) findViewById(R.id.line_chart);
        mChart.setOnChartGestureListener(GraphActivity.this);
        mChart.setOnChartValueSelectedListener(GraphActivity.this);
        mChart.setHighlightPerTapEnabled(false);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend = mChart.getLegend();
        legend.setEnabled(true);
        legend.setFormSize(10f); // set the size of the legend forms/shapes
        legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        //legend.setTypeface(...);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);


        //Description description = new Description();
        //description.setText("MaxQ Research LLC");
       // mChart.setDescription(description);
        mChart.setDescription("MaxQ Research LLC");



        mChart.setPinchZoom(false);

        LimitLine upper_limit = new LimitLine(6f, "Danger");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(15f);

        LimitLine lower_limit = new LimitLine(1f, "Too Low");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        lower_limit.setTextSize(15f);

        YAxis leftAxis= mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMinValue(0f);

        //leftAxis.enableGridDashedLine(10f, 10f, 0);
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        //mChart.setDrawGridBackground(false);

        //ArrayList<Entry> yValues = new ArrayList<>();
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        //mChart.animateX(1000);

/*
        yValues.add(new Entry(1, 0f));
        yValues.add(new Entry(2, 0.5f));
        yValues.add(new Entry(3, 1f));
        yValues.add(new Entry(4, 1.7f));
        yValues.add(new Entry(5, 2f));
        yValues.add(new Entry(6, 5f));
        yValues.add(new Entry(7, 4f));
        yValues.add(new Entry(8, 3f));
        yValues.add(new Entry(9, 7f));
        yValues.add(new Entry(10, 5f));
        yValues.add(new Entry(11, 4f));
        //yValues.add(new Entry(12, 2f));

*/      importData();
        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);
        dataSet.setFillAlpha(110);
        //Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(drawable);
        dataSet.setLineWidth(3f);
        dataSet.setDrawValues(false);
        //dataSet.setDrawCircleHole(true);
        dataSet.setDrawCircles(false);
        dataSet.setValueTextSize(10f);
        dataSet.setColor(Color.rgb(93, 177, 198));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //String[] values = new String[] {"Jan" , "Feb" , "Mar" , "Apr", "May"};
        long min = Collections.min(unixTime);


        AxisValueFormatter axisFormartter = new HourAxisValueFormatter(min);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        //xAxis.setAxisMinimum(0f);
        //xAxis.setLabelCount(5);
        //xAxis.setAxisMaximum(400f);
        //xAxis.setValueFormatter(new DateAxisValueFormatter(null));
        xAxis.setValueFormatter(axisFormartter);


        //CustomMarkerView mv = new CustomMarkerView(Contex, R.layout.custom_marker_view);

        // set the marker to the chart
        //mChart.setMarkerView(mv);

        CustomMarkerView myMarkerView= new CustomMarkerView(getApplicationContext(), R.layout.custom_marker_view, min);
        mChart.setMarkerView(myMarkerView);




        //Bitmap test = mChart.getChartBitmap();
        //Log.d(TAG, "Check Bitmap: " + test);

        mChart.post(new Runnable() {
            @Override
            public void run() {
                //String stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath());
                mChart.saveToPath("graph", "/Documents");
                //Log.d(TAG, "Check saved" + stream);
            }
        });



        average_temperature = (TextView) findViewById(R.id.avg_temp);
        maximum_temperature = (TextView) findViewById(R.id.max_temp);
        minimum_temperature = (TextView) findViewById(R.id.min_temp);
        elap_time = (TextView) findViewById(R.id.elap_time);
        device_name = (TextView) findViewById(R.id.deviceId);
        Log.d(TAG, "Check Lid:" + lidStatus);
        //Log.d(TAG, "Check StringID Size:" + stringid.size());

        //long unixSeconds = 1372339860;
        // convert seconds to milliseconds

        //Log.d(TAG, "Check unixTime:" + unixTime);

        long timeinterval = unixTime.get(1)- unixTime.get(0);



        timeInterval = new Long(timeinterval).toString();

        //Log.d(TAG, "Check timeInterval:" + timeinterval);
        long beginTime = unixTime.get(0);

        long endTime = unixTime.get(unixTime.size()-1);


        Date begindate = new Date(beginTime*1000L);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(TimeZone.getTimeZone("CST"));
        beginDate = sdf.format(begindate);

        Date enddate = new Date(endTime*1000L);
        // the format of your date
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(TimeZone.getTimeZone("CST"));
        endDate = sdf.format(enddate);



        float ave = calculateAverage(temperature);
        double ave1 = Math.round(ave*100.0)/100.0;
        //Log.d(TAG, "Check average: " + ave);
        //Log.d(TAG, "Check average1: " + ave1);
        //Log.d(TAG, "Check max: " + Collections.max(temperature));
        //Log.d(TAG, "Check min: " + Collections.min(temperature));
        //String time = MainActivity.timeString;
        //Log.d(TAG, "Check Elapsed Time: " + time);

        maxTemp = Float.toString(Collections.max(temperature));
        minTemp = Float.toString(Collections.min(temperature));
        avgTemp = Double.toString(ave1);


        average_temperature.setText(avgTemp+" °C");
        maximum_temperature.setText(maxTemp+" °C");
        minimum_temperature.setText(minTemp+" °C");
        elap_time.setText(MainActivity.timeString);
        device_name.setText(MainActivity.deviceName);


        btnpdf.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Intent intent=new Intent(GraphActivity.this,PdfActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /*
@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Bitmap test = mChart.getChartBitmap();
        Log.d(TAG, "Check Bitmap: " + test);

}
*/
    /*

    public class HourAxisValueFormatter implements IAxisValueFormatter
    {

        private long referenceTimestamp; // minimum timestamp in your data set

        private DateFormat mDataFormat;
        private Date mDate;

        public HourAxisValueFormatter(long referenceTimestamp) {
            this.referenceTimestamp = referenceTimestamp;
            this.mDataFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            this.mDate = new Date();
        }


        /**
         * Called when a value from an axis is to be formatted
         * before being drawn. For performance reasons, avoid excessive calculations
         * and memory allocations inside this method.
         *
         * @param value the value to be formatted
         * @param axis  the axis the value belongs to
         * @return

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // convertedTimestamp = originalTimestamp - referenceTimestamp
            long convertedTimestamp = (long) value;

            // Retrieve original timestamp
            long originalTimestamp = referenceTimestamp + convertedTimestamp;

            // Convert timestamp to hour:minute
            return getHour(originalTimestamp);
        }


        public int getDecimalDigits() {
            return 0;
        }

        private String getHour(long timestamp){
            try{
                mDate.setTime(timestamp*1000);
                return mDataFormat.format(mDate);
            }
            catch(Exception ex){
                return "xx";
            }
        }
    }

*/

/*
    public class DateAxisValueFormatter implements IAxisValueFormatter{
        private String[] mValues;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");

        public DateAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis)
        {
            return sdf.format(new Date((long) value));
            //return mValues[(int) value];
        }
    }
    */



    private float calculateAverage(List<Float> marks) {
        if (marks == null || marks.isEmpty()) {
            return 0;
        }

        float sum = 0;
        for (Float mark : marks) {
            sum += mark;
        }

        return sum / marks.size();
    }


    public void importData() {



        try {
            FileInputStream fileInputStream = new FileInputStream(new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            reader.readLine();
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                //Log.d(TAG,"Line" + csvLine);
                if (csvLine.length()>5) {

                    sensorData = csvLine.split(",");

                    sensorData[0] = sensorData[0].substring(1);


                    float id = Float.parseFloat(sensorData[0]);

                    float temp = Float.parseFloat(sensorData[1]);
                    //sensorData[4] = sensorData[4].replaceAll("\\D", "");
                    //Log.d(TAG, "Check dateTime: " + sensorData[4]);

                    int lid = Integer.parseInt(sensorData[2]);


                    //float date = Float.parseFloat(sensorData[4]);
                    //float unix = Float.parseFloat(sensorData[4]);
                    long test = Long.parseLong(sensorData[4]);

                    entries.add(new Entry(id, temp));
                    stringid.add(id);
                    temperature.add(temp);
                    //dateTime.add(date);
                    unixTime.add(test);
                    lidStatus.add(lid);

                }
            }


            System.out.println(entries);
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file" + ex);
        }
    }

}