package edu.cmu.xluo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/*
 * This class provides capabilities to return a bitmap given a picture url.
 * Network operations cannot be done from the UI thread, therefore this class makes use of AsyncTask to do do the operations in background
 * onPostExecution runs in the UI thread, and it calls the picReady method to do the update.
 */
public class AsyncLoadImage extends AsyncTask<String, String, Bitmap> {

    // reference used for callback that allow the picture displayed in the corresponding ImageView
    private BookSearch bs;
    private ImageView imageView;

    public AsyncLoadImage(BookSearch bs, ImageView imageView) {
        this.bs = bs;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {

        Bitmap pic = null;
        try {
            String picURL = urls[0];
            URL u = new URL(picURL);
            pic = getRemoteImage(u); // call get Remote Image method to establish connection and return a bitmap
        } catch (Exception e) {
            System.out.println("Error in loading images");
        }
        return pic;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) { // the call back method that can return a bitmap
        bs.picReady(bitmap, imageView);
    }

    /**
     * Establish connection and get bitmap given a picture's url
     * Reference: Interesting Picture Android App
     * @param url
     * @return Bitmap
     */
    private Bitmap getRemoteImage(final URL url) {

        try {
            final URLConnection conn = url.openConnection();
            conn.connect();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            return bm;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}

