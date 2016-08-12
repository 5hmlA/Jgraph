package com.jonas.schart;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class EntActivity extends ListActivity {

    String[] items = new String[]{"NChart", "ChartActivity"};
    Class[] clazz = new Class[]{MainActivity.class, ChartActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(position);

            }
        });
    }

    private void startActivity(int position) {
        Intent intent = new Intent(this, clazz[position]);
        startActivity(intent);
    }
}
