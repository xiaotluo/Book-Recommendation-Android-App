package edu.cmu.xluo2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class serves as the UI thread for this application.
 * It takes a search string from the user and when the search button is clicked, it will call the search method in BookSearchModel.java
 * In the callback method - dataReady, the information of title, author and publication time will be displayed in TextView.
 * The information link will be attached to a button and the button will have an onclick event listener to direct the user to the homepage of the book when the user clicks the button.
 * The thumbnail pictures will be fetched in AsyncLoadImage.java and displayed in imageView in the callback method - picReady.
 *
 * Reference: Interesting Picture Android Application, Stackoverflow, www.concretepage.com (details in writeup)
 */
public class BookSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * The click listener will need a reference to this object, so that upon successfully getting information from the web service, it
         * can callback to this object with the results.
         */
        final BookSearch bs = this;


        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);

        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                if (!searchTerm.isEmpty()) {
                    BookSearchModel model = new BookSearchModel();
                    model.search(searchTerm, bs); // Done asynchronously in another thread.  It calls bs.dataReady() in this thread when complete.
                }
            }
        });

    }

    /**
     * This is called by the BookSearchModel object when the resulting data is ready.
     * @param results
     */
    public void dataReady(String results) {

        TextView resultView = (TextView)findViewById(R.id.results);
        TextView searchView = (EditText)findViewById(R.id.searchTerm);

        // the return string content will be "No Results Found." if no related books are found.
        if (results.equals("No Results Found.")) {

            String response = "No Results Found for " + searchView.getText().toString();
            resultView.setVisibility(View.VISIBLE);
            resultView.setText(response); // show response to user when no results are found

            /**
             * get the id for the UI components for each book (from stackoverflow, details in writeup)
             */
            for (int i = 0; i < 5; i++) {
                String picID = "thumnail" + (i+1);
                String textID = "bookInfo" + (i+1);
                String btnID = "link" + (i+1);

                // get the reference for the imageView
                int picid = getResources().getIdentifier(picID, "id", getPackageName());
                ImageView pic = (ImageView) findViewById(picid);
                // get the reference for the TextView
                int textid = getResources().getIdentifier(textID, "id", getPackageName());
                TextView txt = (TextView) findViewById(textid);
                // get the reference for the info button
                int btnid = getResources().getIdentifier(btnID, "id", getPackageName());
                Button link = (Button) findViewById(btnid); //

                // set the UI components to invisible when no results are found
                pic.setVisibility(View.INVISIBLE);
                txt.setVisibility(View.INVISIBLE);
                link.setVisibility(View.INVISIBLE);
            }

        } else { // when there are related books' information found

            String response = "Here are 5 interesting books for " + searchView.getText().toString();

            // show the user that there are results found for the term searched
            resultView.setVisibility(View.VISIBLE);
            resultView.setText(response);

            // parse the information in the string for each book
            String[] books = results.split("\n");

            for (int i = 0; i < books.length; i++) {

                String book = books[i];
                // get each piece of information for the book
                String[] fields = book.split(";");
                String booktitle = fields[0];
                String bookauthor = fields[1];
                String imgURL = fields[2];
                final String infoURL = fields[3];
                String time = fields[4];

                // get the id for the UI components for each book
                String picID = "thumnail" + (i+1);
                String textID = "bookInfo" + (i+1);
                String btnID = "link" + (i+1);

                // get the reference for each UI component
                int picid = getResources().getIdentifier(picID, "id", getPackageName());
                ImageView pic = (ImageView) findViewById(picid);
                int textid = getResources().getIdentifier(textID, "id", getPackageName());
                TextView txt = (TextView) findViewById(textid);
                int btnid = getResources().getIdentifier(btnID, "id", getPackageName());
                Button link = (Button) findViewById(btnid);

                // create new thread to load image asynchronously
                AsyncLoadImage async = new AsyncLoadImage(this, pic);
                async.execute(imgURL);

                // combine title, author, publication time information and disply in TextView
                StringBuilder info = new StringBuilder();
                info.append("Title: ").append(booktitle).append("\n").append("Author: ").append(bookauthor)
                        .append("\n").append("Publish Time: ").append(time);
                txt.setVisibility(View.VISIBLE);
                txt.setText(info.toString());


                link.setVisibility(View.VISIBLE);
                // attach an event listener for the button to direct user to the homepage for the book when the button is clicked
                link.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String url = infoURL;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }

        }
        searchView.setText(""); // clear the text in the search box
    }


    /**
     * This is called by the AsyncLoadImage object when the Bitmap is ready.
     * @param pic
     * @param pictureView
     */
    public void picReady (Bitmap pic, ImageView pictureView) {
        // display the picture in corresponding ImageView
        pictureView.setImageBitmap(pic);
        pictureView.setVisibility(View.VISIBLE);
    }

}
