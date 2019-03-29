package com.easylink.cloud.control.test;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.easylink.cloud.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {

    TitleBar bar;
    ListView lv1;
    ListView lv2;
    ListView lv3;
    ListView lv4;
    ListView lv5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        bar = findViewById(R.id.bar);

        bar.setLeftListener(v -> Snackbar.make(bar, "left", BaseTransientBottomBar.LENGTH_SHORT).show());
        bar.setRightListener(v -> Snackbar.make(bar, "right", BaseTransientBottomBar.LENGTH_SHORT).show());
        bar.setTitle("nihhhhhhhahah");

        lv1 = findViewById(R.id.lv_1);
        lv2 = findViewById(R.id.lv_2);
        lv3 = findViewById(R.id.lv_3);
        lv4 = findViewById(R.id.lv_4);
        lv5 = findViewById(R.id.lv_5);


        String[] str1 = {"1", "1", "1", "1", "1"};
        String[] str2 = {"2", "2", "2", "2", "2"};
        String[] str3 = {"3", "3", "3", "3", "3"};
        String[] str4 = {"4", "4", "4", "4", "4"};
        String[] str5 = {"5", "5", "5", "5", "5"};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, str1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, str2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, str3);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, str4);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, str5);


        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        lv3.setAdapter(adapter3);
        lv4.setAdapter(adapter4);
        lv5.setAdapter(adapter5);
    }

}
