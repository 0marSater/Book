package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mAuthor;
    private EditText mIsbn;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mTitle = (EditText) findViewById(R.id.etTitle);
        mAuthor = (EditText) findViewById(R.id.etAuthor);
        mIsbn = (EditText) findViewById(R.id.etIsbn);
        mButton = (Button) findViewById(R.id.btnSearch);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advancedSearch();
            }
        });
    }

    public void advancedSearch(){
        String title = mTitle.getText().toString().trim();
        String author = mAuthor.getText().toString().trim();
        String isbn = mIsbn.getText().toString().trim();

        if (title.isEmpty() && author.isEmpty() && isbn.isEmpty()){
            String message = getString(R.string.no_search_data);
            Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        else {
            URL queryURL =  ApiUtil.buildUrl(title, author, isbn);
            Intent intent = new Intent(SearchActivity.this, BookListActivity.class);
            intent.putExtra("Query", queryURL.toString());
            startActivity(intent);

        }
    }
}