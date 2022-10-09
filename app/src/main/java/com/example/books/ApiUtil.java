package com.example.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {


    private ApiUtil() {}

    public static final String BASE_API_URI =
            "https://www.googleapis.com/books/v1/volumes/";
    private static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyA2_hPN_hCXwbt0skGTnLur7q7yRZCWIAQ";
    public static final String TITLE = "intitle:";
    public static final String AUTHOR = "inauthor:";
    public static final String PUBLISHER = "inpublisher:";
    public static final String ISBN = "isbn";



    public static URL buildUrl(String title){
        URL url = null;
        Uri uri = Uri.parse(BASE_API_URI).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                .appendQueryParameter(KEY,API_KEY)
                .build();
        try {
                url = new URL(uri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrl(String title, String author, String isbn){
        URL url = null;
        StringBuilder sb =new StringBuilder();
        if(!title.isEmpty()) sb.append(TITLE + title + "+");
        if(!author.isEmpty()) sb.append(AUTHOR + author + "+");
        if(!isbn.isEmpty()) sb.append(ISBN + isbn + "+");
        sb.setLength(sb.length()-1);

        String query = sb.toString();
        Uri uri = Uri.parse(BASE_API_URI).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,query)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String getJason (URL url) throws IOException{

        HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch(Exception e){
            Log.d("Error", e.toString());
            return null;
        }

        finally {
            connection.disconnect();
        }

    }



    public static ArrayList<Book> getBooksFromJason(String json){
        final String ID = "id";
        final String TITLE ="title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER ="publisher";
        final String PUBLISHED_DATA = "publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";


        ArrayList<Book> books = new ArrayList<Book>();
        try {
            // get all info of what we need
            JSONObject jsonBook = new JSONObject(json);
            JSONArray arrayBooks = jsonBook.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for (int i =0; i <numberOfBooks; i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJSON.getJSONObject(VOLUMEINFO);
                JSONObject imageLinksJSON = null;
                if (volumeInfoJSON.has (IMAGELINKS)) {
                   imageLinksJSON =  volumeInfoJSON.getJSONObject(IMAGELINKS);
                }
                int authorsNum;
                try {
                    authorsNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                } catch (Exception e)
                {
                    authorsNum = 0;
                }
                String [] authors = new String[authorsNum];

                for (int j = 0; j < authorsNum; j++){
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }

                // fill the constructor of model class.
                Book book = new Book (
                        bookJSON.getString(ID), volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE)?"No subtitle found.": volumeInfoJSON.getString(SUBTITLE)),
                        authors,
                        (volumeInfoJSON.isNull(PUBLISHER)?"No publisher found.": volumeInfoJSON.getString(PUBLISHER)),
                        (volumeInfoJSON.isNull(PUBLISHED_DATA)?"No publish date found.": volumeInfoJSON.getString(PUBLISHED_DATA)),
                        (volumeInfoJSON.isNull(PUBLISHED_DATA)?"No description found.":volumeInfoJSON.getString(DESCRIPTION)),
                        (imageLinksJSON == null?"": imageLinksJSON.getString(THUMBNAIL))
                        );
                // add object of model to list of books
                books.add(book);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return books;
    }






}
