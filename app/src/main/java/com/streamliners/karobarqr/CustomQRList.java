package com.streamliners.karobarqr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomQRList extends ArrayAdapter{
    private Activity context;
    private List<Bitmap> bitmap;

    public CustomQRList(Activity context, List<Bitmap> bitmap) {
        super(context, R.layout.row_item, bitmap);
        this.context = context;
        this.bitmap = bitmap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.row_item, null, true);
        ImageView imageFlag = (ImageView) row.findViewById(R.id.imageViewFlag);
        imageFlag.setImageBitmap(bitmap.get(position));
        return  row;
    }
}
