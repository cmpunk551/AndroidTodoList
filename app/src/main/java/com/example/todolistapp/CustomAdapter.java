package com.example.todolistapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.TreeSet;

class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<String>();
    private ArrayList<Todo> mTodo = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    public int todoIndex = 0;

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        todoIndex = 0;
    }

    public void addItem(final Todo item) {
        mData.add(item.text);
        mTodo.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Project item) {
        mData.add(item.title);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        if(position == 0){
            todoIndex = 0;
        }

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.list_header, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    holder.checkBox = convertView.findViewById(R.id.cb);

                    Todo todo = mTodo.get(todoIndex);
                    if(todo.isCompleted)
                    {
                        holder.checkBox.setChecked(true);
                    }
                    holder.checkBox.setTag(todo);


                    holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            Todo todoInFocus = (Todo)buttonView.getTag();
                            todoInFocus.isCompleted = !(todoInFocus.isCompleted);
                            if (todoInFocus.isCompleted == isChecked) {
                                JsonObject params = new JsonObject();

                                params.addProperty("todo_id", todoInFocus.id);

                                Ion.with(buttonView.getContext())
                                        .load("PUT","https://obscure-harbor-43101.herokuapp.com/todos/" + todoInFocus.id)
                                        .setJsonObjectBody(params)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                // do stuff with the result or error
                                            }
                                        });
                            }return;

                        }
                    });

                    todoIndex++;
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.list_row, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);

                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position));

        return convertView;
    }


    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }

}