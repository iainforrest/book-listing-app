package com.redefineeverything.booklistingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * {@link AsyncTask} to perform the network request on a background thread, and then
 * update the UI with the first earthquake in the response.
 */
public class BookSearchAsyncTask extends AsyncTask<String , Void, ArrayList<Book>> {
    /** Tag for the log messages */
    public final String LOG_TAG = SearchActivity.class.getSimpleName();

    //Activity used to find listview and set progressDIalog
    private Activity mActivity;
    //progress dialog for asyc tasks and better UX
    private ProgressDialog progDailog;

    //used for debug and initial setup
    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=3";

    public BookSearchAsyncTask (Activity activity){
        mActivity = activity;
    }

    //progress bar from here - http://stackoverflow.com/questions/9170228/android-asynctask-dialog-circle
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog = new ProgressDialog(mActivity);
        progDailog.setMessage("Loading...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();

        ListView listView = (ListView) mActivity.findViewById(R.id.list_view);
        listView.setAdapter(null);
    }

    @Override
    protected ArrayList<Book> doInBackground(String... searchQuery) {
        // Create URL object

        URL url = createUrl(searchQuery[0]);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("BookSearchAsyncTask","Error in connecting to and parsing the JSON : " +e.getMessage());
        }

        // Extract relevant fields from the JSON response and create an {@link Book} object
        ArrayList<Book> books = extractFeaturesFromJson(jsonResponse);

        // Return the {@link Book} object as the result fo the {@link TsunamiAsyncTask}
        return books;
    }

    /**
     * Update the screen with the given earthquake (which was the result of the
     * {@link BookSearchAsyncTask}).
     */
    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        progDailog.dismiss();
        if (books == null || books.size() == 0) {
            // If no books, then create a toast to tell user that no books were found.
            Toast toast = Toast.makeText(mActivity, "Sorry, No Results Found!\n\nPlease Try Searching A Different Topic.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        //if books then update the UI (listview) from the custom adapter
        updateUI(books);
    }

    private void updateUI(ArrayList<Book> books) {
        ListView listView = (ListView) mActivity.findViewById(R.id.list_view);

        BookAdapter bookAdapter = new BookAdapter(mActivity, books);

        listView.setAdapter(bookAdapter);
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String searchQuery) {
        URL url = null;
        try {
            url = new URL("https://www.googleapis.com/books/v1/volumes?q="
                    + URLEncoder.encode(searchQuery, "UTF-8")+"&maxResults=10");
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        } catch (UnsupportedEncodingException e){
            Log.e(LOG_TAG, "Error with creating URL", e);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("MakeHttpRequest", "http response code was : " + responseCode);
            }

        } catch (IOException e) {
            Log.e("MakehttpReuest","IOException thrown : " +e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    private ArrayList<Book> extractFeaturesFromJson(String booksJSON) {
        ArrayList<Book> books = new ArrayList<Book>();
        String description = "";
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }
        try {
            JSONObject root = new JSONObject(booksJSON);
            JSONArray booksArray = root.optJSONArray("items");

            // If there are results in the features array
            if (booksArray.length() > 0) {
                for (int i = 0; i < booksArray.length(); i++) {
                    JSONObject bookObject = booksArray.getJSONObject(i);
                    JSONObject volumeInfoObject = bookObject.getJSONObject("volumeInfo");
                    String title = volumeInfoObject.getString("title");
                    JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                    ArrayList<String> authors = new ArrayList<String>();
                    if (authorsArray != null) {
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors.add(authorsArray.get(j).toString());
                        }
                    }
                    try {
                        description = volumeInfoObject.getString("description");
                    } catch (JSONException e) {
                        description = "";
                    }
                    String infoUrl = volumeInfoObject.getString("infoLink");

                    books.add(new Book(title, authors, infoUrl, description));

                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return books;
    }
}
