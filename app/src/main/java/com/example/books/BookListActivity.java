package com.example.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        rvBooks = (RecyclerView) findViewById(R.id.rv_books);

        //create layout manager
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvBooks.setLayoutManager(booksLayoutManager);

        executeAdvancedSearch();

    }

    public void executeAdvancedSearch(){
        URL bookUrl;
        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
        try{
            if (query == null || query.isEmpty())
            {
                bookUrl = ApiUtil.buildUrl("cooking");
            }else{
                bookUrl = new URL(query);
            }
            new BooksQueryTask().execute(bookUrl);
        }
        catch (Exception e)
        {
            Log.d("Error", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menue, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookUrl = ApiUtil.buildUrl(query);
            new BooksQueryTask().execute(bookUrl);
        }
        catch (Exception e){
            Log.d("Error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    public class BooksQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL searchURL  = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJason(searchURL);
            }
            catch (IOException e){
                Log.e("Error", e.toString());
            }
            return result;
        }

        public void setData(String result){

            if (result == null){
                enableErrorVisibility();
            }else{
                enableDataVisibility();
                ArrayList<Book> books = ApiUtil.getBooksFromJason(result);
                BooksAdapter adapter = new BooksAdapter(books);
                rvBooks.setAdapter(adapter);
            }

        }

        public void enableErrorVisibility(){
            TextView tvError =  (TextView) findViewById(R.id.tv_error);
            rvBooks.setVisibility(View.INVISIBLE);
            tvError.setVisibility(View.VISIBLE);
        }

        public void enableDataVisibility(){
            mLoadingProgress.setVisibility(View.INVISIBLE);
            TextView tvError =  (TextView) findViewById(R.id.tv_error);
            rvBooks.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            setData(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);

        }
    }






















}