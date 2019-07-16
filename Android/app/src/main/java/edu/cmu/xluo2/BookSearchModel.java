package edu.cmu.xluo2;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 * This class provides capabilities to search for book's information on my web service given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the dataReady method to do the update.
 */
public class BookSearchModel {

    BookSearch bs = null;

    /*
     * search is the public BookSearchModel method.  Its arguments are the search term, and the BookSearch object that called it.  This provides a callback
     * path such that the dataReady method in that object is called when the data is available from the search.
     */
    public void search(String searchTerm, BookSearch bs) {
        this.bs = bs;
        new AsyncBookSearch().execute(searchTerm);
    }

    /*
     * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
     * doInBackground is run in the helper thread.
     * onPostExecute is run in the UI thread, allowing for safe UI updates.
     */
    private class AsyncBookSearch extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return search(urls[0]);
        }

        protected void onPostExecute(String results) {
            bs.dataReady(results);
        }

        /*
         * Search my web service for the searchTerm argument, and return a String containing all information needed
         */
        private String search(String searchTerm) {

            StringBuilder res = new StringBuilder();

            try {
                searchTerm = URLEncoder.encode(searchTerm, "UTF-8"); // encode the search term

                // establish http connection and get the JSON data in response

                // comment the url link for task 1
                // String json = getRemoteJson("https://stormy-hamlet-89103.herokuapp.com/BookServlet/" + searchTerm);

                String json = getRemoteJson("https://arcane-bastion-60747.herokuapp.com/BookServlet/" + searchTerm); // the url link is for task 2

                JsonObject jobj = new Gson().fromJson(json, JsonObject.class);
                int total = jobj.get("size").getAsInt();

                // check if there are results found for the search term
                if (total == 0) {
                    return "No Results Found.";
                }

                // parse the JSONArray and get the information needed for the 5 books and combine them in StringBuilder
                JsonArray bookArray = jobj.get("bookList").getAsJsonArray();
                for (JsonElement book : bookArray) {
                    JsonObject bookObj = book.getAsJsonObject();
                    String title = bookObj.get("title").getAsString();
                    String author = bookObj.get("author").getAsString();
                    String imgURL = bookObj.get("imgURL").getAsString();
                    String infoURL = bookObj.get("infoURL").getAsString();
                    String time = bookObj.get("publishedTime").getAsString();
                    res.append(title).append(";").append(author).append(";").append(imgURL).append(";").append(infoURL).append(";").append(time).append("\n");
                }

                res.deleteCharAt(res.length() - 1); // strip the last "\n"

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return res.toString();

        }

        /*
         * Given a url that will request JSON, return a JSON string, else ""
         * Reference: Lab 7
         */
        private String getRemoteJson(String urlString) {
            String response = "";
            try {
                URL url = new URL(urlString);
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different than the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Read all the text returned by the server

                connection.setRequestMethod("GET");
                // tell the server what format we want back
                connection.setRequestProperty("Content-Type", "application/json");

                connection.connect();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String str;

                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }
                in.close();

            } catch (IOException e) {
                Log.e("Exception in request",e.getMessage());
            }
            return response;
        }

    }
}
