package com.streamliners.karobarqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ViewPrintAdapter extends PrintDocumentAdapter {
    public PdfDocument myPdfDocument;
    public int totalPages;
    Context context;
    private int pageHeight;
    private int pageWidth;
    private final List<Bitmap> bitmapList;
    private int indexOfBitmap;

    public ViewPrintAdapter(Context context, List<Bitmap> bitmapList, int indexOfBitmap) {
        this.context = context;
        this.bitmapList = bitmapList;
        this.indexOfBitmap = indexOfBitmap;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        myPdfDocument = new PrintedPdfDocument(context, newAttributes);
        totalPages = bitmapList.size() / 4;
        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalPages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages);
            indexOfBitmap =0;
            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        for (int i = 0; i < totalPages; i++) {
            if (pageInRange(pages, i)) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page =
                        myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                drawPage(page, i);
                myPdfDocument.finishPage(page);
            }
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pages);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page, int pageNumber) {
        Canvas canvas = page.getCanvas();
        pageNumber++; // Make sure page numbers start at 1

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        Paint paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setFilterBitmap(true);
        paint1.setDither(true);
        //canvas.drawText( "Test Print Document Page " + pageNumber,leftMargin,titleBaseLine,paint);

        paint.setTextSize(14);
        //canvas.drawText("This is some test content to verify that custom document printing works", leftMargin, titleBaseLine + 35, paint);
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.barcode_template);
//        // Define an offset value between canvas and bitmap
//        int offset = 50;
//
//        // Initialize a new Bitmap to hold the source bitmap
//        Bitmap dstBitmap = Bitmap.createBitmap(
//                bitmap.getWidth() + offset * 2, // Width
//                bitmap.getHeight() + offset * 2, // Height
//                Bitmap.Config.ARGB_8888 // Config
//        );
//
//        // Initialize a new Canvas instance
//        Canvas canvas1 = new Canvas(dstBitmap);
//        canvas1.drawBitmap(
//                bitmap, // Bitmap
//                offset, // Left
//                offset, // Top
//                null // Paint
//        );
        if (pageNumber % 2 == 0)
            paint.setColor(Color.RED);
        else
            paint.setColor(Color.GREEN);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.barcode_template);
        Bitmap bitmap1 = bitmapList.get(indexOfBitmap);
        Bitmap bitmap2 = bitmapList.get(indexOfBitmap+1);
        Bitmap bitmap3 = bitmapList.get(indexOfBitmap+2);
        Bitmap bitmap4 = bitmapList.get(indexOfBitmap+3);
        indexOfBitmap+=4;
        PdfDocument.PageInfo pageInfo = page.getInfo();
//        canvas.drawBitmap(getResizedBitmap(bitm,700), (canvas.getWidth() - bitm.getWidth()) / 2, (canvas.getHeight() - bitm.getHeight()) / 2, paint1);
//        canvas.drawBitmap(bitmapList.get(0), 0, 0, paint1);
        canvas.drawBitmap(getResizedBitmap(bitmap,800), 0, 0, null);
        canvas.drawBitmap(getResizedBitmap(bitmap1,135), 77, 144, paint1);
        canvas.drawBitmap(getResizedBitmap(bitmap2,125), 370, 550, paint1);
        canvas.drawBitmap(getResizedBitmap(bitmap3,125), 370, 144, paint1);
        canvas.drawBitmap(getResizedBitmap(bitmap4,135), 77, 550, paint1);
//        indexOfBitmap+=4;
        //canvas.drawCircle(pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() / 2, 150, paint);
    }
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width,height, true);
    }
}
