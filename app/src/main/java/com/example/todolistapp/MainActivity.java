package com.example.todolistapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainActivity extends AppCompatActivity {

    public ListView mainListView;
    List<Project> mProjects = new ArrayList<Project>();
    private CustomAdapter mAdapter;
    public Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3AAFDA")));

        Ion.with(this)

                .load("https://obscure-harbor-43101.herokuapp.com/todos.json")

                .asJsonObject()

                .setCallback(new FutureCallback<JsonObject>() {

                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result != null) {

                            List<Project> projects = new ArrayList<Project>();
                            JsonArray jsonProjects = result.getAsJsonArray("projects");

                            for (final JsonElement projectJsonElement : jsonProjects) {

                                projects.add(new Gson().fromJson(projectJsonElement, Project.class));

                            }
                            List<Todo> todos = new ArrayList<Todo>();

                            JsonArray jsonTodos = result.getAsJsonArray("todos");

                            for (final JsonElement todoJsonElement : jsonTodos) {

                                todos.add(new Gson().fromJson(todoJsonElement, Todo.class));

                            }


                            ArrayList<String> projectsTitles = new ArrayList<>();
                            for (Project project:projects
                            ) {
                                projectsTitles.add(project.title);
                            }
                            Context context = getApplicationContext();
                            Bundle b=new Bundle();
                            b.putStringArrayList("Projects", projectsTitles);
                            myIntent = new Intent(getBaseContext(), AddTodoActivity.class);
                            myIntent.putExtras(b);

                            mAdapter = new CustomAdapter(getBaseContext());
                            for (int indexOfProject = 0; indexOfProject < projects.size(); indexOfProject++) {

                                mAdapter.addSectionHeaderItem(projects.get(indexOfProject));
                                for(Todo currentTodo :todos){
                                    if(currentTodo.project_id == indexOfProject+1)
                                        mAdapter.addItem(currentTodo);
                                }

                            }
                            mainListView = (ListView) findViewById(R.id.mainListView);
                            mainListView.setAdapter(mAdapter);


                        }

                    }

                });






        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("@font/open_sans_light")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
    public void onButtonClick(View v){
        finish();
        startActivity(myIntent);
    }


}



