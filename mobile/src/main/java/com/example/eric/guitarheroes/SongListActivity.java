package com.example.eric.guitarheroes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Kevin on 12/5/2015.
 */
public class SongListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment topHeader = new FirstListHeader();
        Fragment topList = new FirstListView();
        Fragment bottomHeader = new SecondListHeader();
        Fragment bottomList = new SecondListView();
        fragmentManager.beginTransaction()
                .add(R.id.My_Container_1_ID, topHeader)
                .add(R.id.My_Container_2_ID, topList)
                .add(R.id.My_Container_3_ID, bottomHeader)
                .add(R.id.My_Container_4_ID, bottomList)
                .commit();

    }
}
