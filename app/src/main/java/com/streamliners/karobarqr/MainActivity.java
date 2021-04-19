package com.streamliners.karobarqr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.streamliners.karobarqr.databinding.ActivityMainBinding;
import com.streamliners.karobarqr.models.EncodedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String seedNum = "1111111111";
    long seedLong;
    List<Bitmap> bitmapList = new ArrayList<>();
    List<String> encoHash;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        generateQRS();
        //getResult();
    }

    private void initData() {
        seedLong = Long.parseLong(seedNum);
    }

    private void generateQRS() {
        binding.generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(binding.qrNumber.getText().toString());
                if(number%4==0) {
                    dialog = ProgressDialog.show(MainActivity.this, "", "GENERATING", true);
                    for (int i = 1; i <= number; i++) {
                        seedLong++;
                        EncodedData.addToList(seedLong);
                    }
                    EncodedData.encodeIt();
                    getResult();
                }else{
                    Toast.makeText(MainActivity.this, "Only Multiples of 4 allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getResult() {
        encoHash = EncodedData.returnList();
        for (String s : encoHash){
            bitmapList.add(getQRS(s));
        }
        setListView();
        if (dialog != null) {
            dialog.dismiss();
        }
        printPDF();
    }
    public void printPDF() {
        PrintManager printManager = (PrintManager) MainActivity.this.getSystemService(PRINT_SERVICE);
        String jobName ="qrPrint";
        printManager.print(jobName, new ViewPrintAdapter(this,bitmapList,0), null);
    }

    private void setListView() {
        // Setting header
        TextView textView = new TextView(this);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("List of QRS");

        ListView listView=(ListView)findViewById(R.id.qrList);
        listView.addHeaderView(textView);

        // For populating list data
        CustomQRList customQRList = new CustomQRList(this, bitmapList);
        listView.setAdapter(customQRList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(),"You Selected "+ encoHash.get(position - 1) + " as Encoded",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Bitmap getQRS(String s) {
        Bitmap bitmap = null;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(s, BarcodeFormat.QR_CODE, 700, 700);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            bitmap = bmp;

        } catch ( WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}