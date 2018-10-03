package com.active.maxq;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import com.github.mikephil.charting.data.Entry;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.FontFactory;
import com.itextpdf.awt.PdfGraphics2D;


import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.anwarshahriar.calligrapher.Calligrapher;


public class PdfActivity extends Activity {
    private static final String TAG = "PdfCreatorActivity";
    public File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    public String filetext="";
    public String[] final_file =new String[99999];
    public String[] String_Ids =new String[99999];
    public String[] Temperatures =new String[99999];
    public int line_count=0;
    public static int I=0,J=0,K=0,L=0;
    public int pdf_array_count=0;
    static final int READ_BLOCK_SIZE = 100;
    Font ffont = new Font(Font.FontFamily.UNDEFINED, 9, Font.ITALIC);
    Font tfont = new Font(Font.FontFamily.UNDEFINED, 12, Font.ITALIC);
    public static final Font FONT = new Font();
    public static final Font BOLD = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
    public static String[] sensorData;
    public static final String FONTBASE = "/assets/fonts/GothamLight.ttf";
    public static final String FONTHEAD = "/assets/fonts/GothamBold.ttf";
    Font headFont = FontFactory.getFont(FONTHEAD, 12, BaseColor.BLACK);
    Font baseFont = FontFactory.getFont(FONTBASE, 9, BaseColor.BLACK);
    ArrayList<Float> stringid = new ArrayList<>();
    ArrayList<Float> temperature = new ArrayList<>();
    final static String fileName = "/data.csv";
    final static String graphName = "/graph.png";
    final static String path = Environment.getExternalStorageDirectory().getPath() + "/Documents" ;
    private StringBuilder recDataString = new StringBuilder();
    private BaseFont bfBold;


    class MyFooter extends PdfPageEventHelper {

        PdfTemplate total;

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }


        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();


            //Phrase header1 = new Phrase("MAXQ", baseFont);
            //Phrase header2 = new Phrase("Thermal Control Shipping System", baseFont);
            Phrase footer1 = new Phrase(String.format("Page %d", writer.getPageNumber()), baseFont);
            Phrase footer2 = new Phrase("www.PackMaxQ.com", baseFont);
           // ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
            //        header1,document.leftMargin()+10,document.top()+10,0);
           // ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            //        header2,document.right()-10,document.top()+10,0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer1,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer2,document.right()-40,document.bottom()-10,0);

            try
            {
                //PdfContentByte cb = writer.getDirectContent();

            /*
              Some code to place some text in the header
            */
                InputStream inputStream = getAssets().open("maxq_logo_combine.PNG");
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imgSoc = Image.getInstance(stream.toByteArray());
                //Image imgSoc = Image.getInstance("");
                imgSoc.scalePercent(30);
                imgSoc.setAbsolutePosition(document.leftMargin()+10,document.top()-5);





                cb.addImage(imgSoc);
                //cb.addImage(imgSoc1);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Calligrapher calligrapher = new Calligrapher(this);
        //calligrapher.setFont(this, "fonts/GothamLight.ttf", true);
        try {
            createPdfWrapper();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }



    private void createPdfWrapper() throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);

            }
            return;
        } else {
            createPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }
        pdfFile = new File(docsFolder.getAbsolutePath(), "MaxQ.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        MyFooter event = new MyFooter();



        try {
            PdfWriter docWriter =PdfWriter.getInstance(document, output);
            docWriter.setPageEvent(event);
            //writefile();
            //readfile();

            document.open();
            //PdfPTable table = new PdfPTable(MainActivity.Icount);
            //document.add(new Paragraph("Times:"));

            PdfContentByte cb = docWriter.getDirectContent();
            initializeFonts();

            /*
            InputStream inputStream = getAssets().open("MaxQ_Logo.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = Image.getInstance(stream.toByteArray());
            //companyLogo.setAbsolutePosition(30,700);
            companyLogo.setAlignment(Element.ALIGN_CENTER);
            companyLogo.scalePercent(25);
            document.add(companyLogo);

*/


            //creating a sample invoice

            //createHeadings(cb, 400, 780, "MaxQ Research LLC");
            //createHeadings(cb, 400, 765, "8712 W 6th Ave");
            //createHeadings(cb, 400, 750, "Stillwater, Ok 74075");
            //createHeadings(cb, 400, 735, "United State");




            //list all the products sold to the customer
            float[] columnWidths = {8f, 8f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setTotalWidth(500f);
            //PdfPCell cell1 = new PdfPCell(new Phrase("Product Code"));
            //cell1.setBackgroundColor(BaseColor.GREEN);
            table.addCell("Serial Number");
            //PdfPCell cell2 = new PdfPCell(new Phrase("MaxQ A series"));
            //cell2.setBackgroundColor(BaseColor.CYAN);
            table.addCell(MainActivity.deviceName);
            table.addCell("Start Time");
            table.addCell(GraphActivity.beginDate);
            table.addCell("Stop Time");
            table.addCell(GraphActivity.endDate);
            table.addCell("Time Step");
            table.addCell(GraphActivity.timeInterval + " seconds");
            table.addCell("Elapsed Time");
            table.addCell(MainActivity.timeString);
            table.addCell("Maximum Temperature");
            table.addCell(GraphActivity.maxTemp + " °C");
            table.addCell("Minimum Temperature");
            table.addCell(GraphActivity.minTemp + " °C");
            table.addCell("Average Temperature");
            table.addCell(GraphActivity.avgTemp + " °C");
            table.addCell("Report Generated");
            table.addCell(GraphActivity.endDate);
            table.addCell("No. of Times Lid Opened");
            table.addCell("1");
            table.addCell("Maximum Duration Lid Opened");
            table.addCell("12 " + "seconds");


           // for(PdfPRow r: table.getRows()){
            //    for (PdfPCell c: r.getCells()){
            //        c.setBackgroundColor(BaseColor.CYAN);
            //    }
           // }
/*
            PdfPCell cell = new PdfPCell(new Phrase("Qty"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Number"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Description"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Price"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Ext Price"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            table.setHeaderRows(1);

            DecimalFormat df = new DecimalFormat("0.00");
            for(int i=0; i < 15; i++ ){
                double price = Double.valueOf(df.format(Math.random() * 10));
                double extPrice = price * (i+1) ;
                table.addCell(String.valueOf(i+1));
                table.addCell("ITEM" + String.valueOf(i+1));
                table.addCell("Product Description - SIZE " + String.valueOf(i+1));
                table.addCell(df.format(price));
                table.addCell(df.format(extPrice));
            }
            */

            //absolute location to print the PDF table from
            table.writeSelectedRows(0, -1, document.leftMargin()+10, 750, docWriter.getDirectContent());
            Paragraph specs = new Paragraph("TRIPS DETAILS", BOLD);
            specs.setAlignment(Element.ALIGN_CENTER);


            document.add(specs);

            /*
            //print the signature image along with the persons name
            inputStream = getAssets().open("sign.png");
            bmp = BitmapFactory.decodeStream(inputStream);
            stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image signature = Image.getInstance(stream.toByteArray());
            signature.setAbsolutePosition(400f, 150f);
            signature.scalePercent(25f);
            document.add(signature);

            createHeadings(cb,450,135,"Trung Nguyen");
            */
            //table.setSpacingAfter(20f);
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );
            //document.newPage();
            Paragraph graphText = new Paragraph("PAYLOAD TEMPERATURE HISTORY", BOLD);
            graphText.setAlignment(Element.ALIGN_CENTER);

            document.add(graphText);
            //document.add( Chunk.NEWLINE );
            //document.add( Chunk.NEWLINE );
           // document.add( Chunk.NEWLINE );
            //graphText.setSpacingBefore(20f);
            FileInputStream fileInputStream = new FileInputStream(new File(path + graphName));
            //InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            //inputStream = getAssets().open("sign.png");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bmp = BitmapFactory.decodeStream(fileInputStream);
            stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image graph = Image.getInstance(stream.toByteArray());
            //graph.setAbsolutePosition(400f, 150f);
            graph.scalePercent(50f);
            document.add(graph);

            document.newPage();

            Paragraph rawData = new Paragraph("TEMPERATURE DATA", BOLD);
            rawData.setAlignment(Element.ALIGN_CENTER);

            document.add(rawData);

            document.add(Chunk.NEWLINE);

            Log.d(TAG, "Check ID" + GraphActivity.stringid);
            Log.d(TAG, "Check Temp" + GraphActivity.temperature);
            //Log.d(TAG, "Final file:" + MainActivity.fileInPrint);
            //document.add(new Paragraph(MainActivity.fileInPrint));

            float[] columnWidths2 = {4f, 4f, 4f, 4f , 5f };
            //create PDF table with the given widths
            PdfPTable table2 = new PdfPTable(columnWidths2);
            // set table width a percentage of the page width
            table2.setTotalWidth(500f);

            //FileInputStream fileInput = new FileInputStream(new File(path + fileName));
            //InputStreamReader inputStreamReader = new InputStreamReader(fileInput);
            BufferedReader reader = new BufferedReader(new FileReader(path + fileName));
            //reader.readLine();
            String line = "";

            process(table2, line, baseFont);
            table2.setHeaderRows(1);
            PdfPCell cell1 = new PdfPCell(new Paragraph("Time Step"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Temperature"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Box Status"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("Payload Status"));
            PdfPCell cell5 = new PdfPCell(new Paragraph("Date Time"));

            cell1.setBackgroundColor(BaseColor.YELLOW);
            cell2.setBackgroundColor(BaseColor.YELLOW);
            cell3.setBackgroundColor(BaseColor.YELLOW);
            cell4.setBackgroundColor(BaseColor.YELLOW);
            cell5.setBackgroundColor(BaseColor.YELLOW);

            table2.addCell(cell1);
            table2.addCell(cell2);
            table2.addCell(cell3);
            table2.addCell(cell4);
            table2.addCell(cell5);

           while ((line= reader.readLine())!=null) {
                  process(table2, line, baseFont);
           }
            reader.close();

/*
            for(PdfPRow r: table2.getRows()){
                for (PdfPCell c: r.getCells()){
                    c.setBackgroundColor(BaseColor.YELLOW);
                }
            }
            */
            document.add(table2);

            document.close();

            previewPdf();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void process (PdfPTable table2, String line, Font font){

        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        while (tokenizer.hasMoreTokens()){
            String stringid = tokenizer.nextToken();
            String stringid1 = stringid.substring(1);
            table2.addCell(stringid1);
            String stringtemp = tokenizer.nextToken();
            table2.addCell(stringtemp);
            String stringlid = tokenizer.nextToken();
            String compare = "0";

            if (stringlid.equals(compare)) {
                stringlid = "Lid Close";
            } else
                stringlid = "Lid Open";
            table2.addCell(stringlid);
            String stringstt = tokenizer.nextToken();
            if (stringstt.equals(compare)) {
                stringstt = "Acceptable";
            } else
                stringstt = "Not Acceptable";
            table2.addCell(stringstt);
            String stringtime = tokenizer.nextToken();
            long beginTime = Long.parseLong(stringtime);
            Date begindate = new Date(beginTime*1000L);
            // the format of your date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // give a timezone reference for formatting (see comment at the bottom)
            sdf.setTimeZone(TimeZone.getTimeZone("CST"));
            String beginDate = sdf.format(begindate);
            table2.addCell(beginDate);
            String stringlast = tokenizer.nextToken();
            //table2.addCell(stringlast);
            //table2.addCell(new Phrase(tokenizer.nextToken(), font));
            //Log.d(TAG, "Check Token:" + tokenizer);

        }
    }



    private void createHeadings(PdfContentByte cb, float x, float y, String text){

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }

    private void initializeFonts(){


        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    private void writefile()
    {
        try
        {
            //File file=new File("mytextfile.txt");
            int i=0;
            while(MainActivity.file_array[i]!=null)
            {
                filetext=filetext+MainActivity.file_array[i];
                i++;
            }
            Log.d(TAG, "File text: "+filetext);
            FileOutputStream fileout=openFileOutput("mytextfile.txt",MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(filetext);
            outputWriter.close();
            Toast.makeText(getBaseContext(), "File received successfully!",Toast.LENGTH_SHORT).show();
           // Toast.makeText(this,"File received succesfully",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void readfile()
    {
        try {

            FileInputStream fileIn=openFileInput("mytextfile.txt");

            InputStreamReader InputRead= new InputStreamReader(fileIn);
            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;
            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
                Log.d(TAG, "readfile: "+s);
            }
            int length_of_file=s.length();
            int array_index=0;
            final_file[array_index]="";
            for(int i=0;i<length_of_file;i++)
            {
                final_file[array_index]= final_file[array_index]+Character.toString(s.charAt(i));
                if(Character.toString(s.charAt(i))=="\n")
                {
                      array_index++;
                }
                Log.d(TAG, "Each character in a file :"+final_file[array_index]);
            }
            int index_count=0;
            while(final_file[index_count]!=null)
            {
                Log.d(TAG, "Finalfile: "+final_file[index_count]);
                if(final_file[index_count].contains("$$"))
                {
                    Log.d(TAG, "Start of the File ");
                }
                if(final_file[index_count].contains("{"))
                {
                    Log.d(TAG, " Date ");
                }
                if(final_file[index_count].contains("("))
                {
                    Log.d(TAG, " Time ");
                }
                if(final_file[index_count].contains("&"))
                {
                    Log.d(TAG, " Rate ");
                }
                if(final_file[index_count].contains("~")&&final_file[index_count].contains("@"))
                {
                    StringTokenizer tokens = new StringTokenizer(final_file[index_count], ",");
                    String flag = tokens.nextToken();
                    String_Ids[pdf_array_count] = flag.substring(1);
                    Log.d(TAG, "StringId in pdf: "+String_Ids[pdf_array_count]);
                    String temp=tokens.nextToken();
                    Temperatures[pdf_array_count]=temp;
                    Log.d(TAG, "Temperature in pdf: "+Temperatures[pdf_array_count]);
                    pdf_array_count++;
                    Log.d(TAG, " String ");
                }
               if(final_file[index_count].contains("~"))
                {
                    Log.d(TAG, "Separator ");
                }
                if(final_file[index_count].contains("##"))
                {
                    Log.d(TAG, "End of the file ");
                }
                index_count++;
            }

            InputRead.close();
            Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

*/

    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        } else {
            Toast.makeText(this, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }
}

