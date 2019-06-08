package com.example.android.android_me.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.android_me.R;

// After creating the fragment..
// TODO (4) Create a new Activity named MainActivity and a corresponding layout file that displays a MasterListFragment
// Remember, to display a static fragment in a layout file, use the <fragment> tag
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
