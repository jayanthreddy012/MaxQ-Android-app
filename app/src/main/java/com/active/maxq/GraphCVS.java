package com.active.maxq;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;


/**
 * Created by Trung on 1/24/2018.
 */

public class GraphCVS extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private GraphView mGraph;
    private static final String PATH_TO_SERVER = "https://raw.githubusercontent.com/trungdn/Challenge/master/CVSTest/data.csv";
    final static String fileName = "/data1.csv";
    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents" ;
    LineGraphSeries<DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        mGraph = (GraphView) findViewById(R.id.graph);


        //readRawData();


        DownloadFilesTask downloadFilesTask = new DownloadFilesTask();
        downloadFilesTask.execute();
        //graphplot();

    }
/*
    private void graphplot() {
        {
            //int i=0;
            for (int i = 0; i < result.size(); i++)
            //for(int j=0;j<MainActivity.Icount;j++)
            {

                RawDataSample sample = new RawDataSample();
                double x=sample.getId();
                //need to write down different increments for the j value depending on the rate of transmission
                double y = sample.getTemp();
                series.appendData(new DataPoint(x, y), true, 10);

            }
            series.setThickness(5);
            series.setDrawDataPoints(true);
        }
    }
    */

    private List<RawDataSample> dataSamples = new ArrayList<>();
    private void readRawData() {
        //List<RawDataSample> dataSamples = new ArrayList<>();
        //List<String[]> csvLine = new ArrayList<>();
        //String[] content = null;
        //DataPoint[] dataPoints = new DataPoint[result.size()];
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = "";
            reader.readLine();

            while((line = reader.readLine()) != null){
                Log.d(TAG, "Line: " + line);

                String[] tokens = line.split(",");

                RawDataSample sample = new RawDataSample();
                tokens[0] = tokens[0].substring(1);
                sample.setId(Integer.parseInt(tokens[0]));
                sample.setTemp(Double.parseDouble(tokens[1]));
                sample.setBoxStt(Integer.parseInt(tokens[2]));
                sample.setPayloadStt(Integer.parseInt(tokens[3]));

                dataSamples.add(sample);
                //csvLine.add(tokens);

                Log.d(TAG, "Just created: " + tokens[0]);
                //Log.d(TAG, "Just created: " + sample);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<String[]> readCVSFromAssetFolder(){
        List<String[]> csvLine = new ArrayList<>();
        String[] content = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = "";
            br.readLine();
            while((line = br.readLine()) != null){

                content = line.split(",");

                csvLine.add(content);

                Log.d(TAG, "Line:" + line);


            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvLine;
    }
    private class DownloadFilesTask extends AsyncTask<URL, Void, List<String[]>> {
        protected List<String[]> doInBackground(URL... urls) {
            return readCVSFromAssetFolder();
        }
        protected void onPostExecute(List<String[]> result) {
            if(result != null){
                readRawData();
            }
        }
    }
    private void createLineGraph(List<String[]> result){
        DataPoint[] dataPoints = new DataPoint[result.size()];
        for (int i = 0; i < result.size(); i++){
            String [] rows = result.get(i);


            //Log.d(TAG, "Check size " + check);
            Log.d(TAG, "Output " + Integer.parseInt(rows[0]) + " " + Integer.parseInt(rows[1]));


            dataPoints[i] = new DataPoint(Integer.parseInt(rows[0]), Integer.parseInt(rows[1]));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        mGraph.addSeries(series);
    }

}
