package com.example.todolistapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;



public class AddTodoActivity extends AppCompatActivity {
    public ListView projectsList;
    public int mSelectedItem = -1;
    public boolean isChecked;
    public String todoName;
    public Intent myIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.add_todo_action_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3AAFDA")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayList<String> projectsArray = new ArrayList<>();
        projectsArray = getIntent().getStringArrayListExtra("Projects");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.projectlist_cell);

        adapter.addAll(projectsArray);

        projectsList = (ListView) findViewById(R.id.projectsListView);

        projectsList.setAdapter(adapter);

        projectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Drawable doneImg = getApplicationContext().getResources().getDrawable(R.drawable.tick);
                Bitmap bitmap = ((BitmapDrawable) doneImg).getBitmap();
                Drawable croppedDoneImg = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap,40,40,true));

                if(!isChecked){
                TextView projectCell = (TextView)view.findViewById(R.id.project_element);
                doneImg.setBounds(20,20,20,20);

                projectCell.setCompoundDrawablesWithIntrinsicBounds(null,null,croppedDoneImg,null);
                isChecked = true;
                mSelectedItem = position;
                }
                else{
                    if(position == mSelectedItem){

                        TextView projectCell = (TextView)view.findViewById(R.id.project_element);
                        projectCell.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                        isChecked = false;
                    }
                }



            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_todo, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(isChecked)
        {
            EditText EditTextView = (EditText) findViewById(R.id.todo_text);
            todoName = EditTextView.getText().toString();

            Todo todo = new Todo();
            JsonObject params = new JsonObject();


            params.addProperty("text", todoName);

            params.addProperty("project_id", (mSelectedItem + 1));

            Ion.with(this)
                    .load("https://obscure-harbor-43101.herokuapp.com/todos")
                    .setJsonObjectBody(params)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                        }
                    });
            myIntent = new Intent(getBaseContext(), MainActivity.class);
            finish();
            startActivity(myIntent);
        return true;
        }
        else{
            return false;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        myIntent = new Intent(getBaseContext(), MainActivity.class);
        finish();
        startActivity(myIntent);
        return true;
    }
}
