package com.example.eric.guitarheroes;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guitarheroes.song.Song;

import java.util.ArrayList;

/**
 * Created by Kevin on 12/1/2015.
 */
public class BetterMainActivity extends Activity {
    // Image Id's to an entire song. Let's do Hotel California for now or something.
    private ArrayList<Integer> imageId = new ArrayList<>();
    Song song;
    private ImageView chordImage;


    //the index determines the picture that is currently being displayed.
    private int index = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Add image id's to an array list that holds all image id's
        Intent intent = getIntent();
        String songString = intent.getStringExtra("Song");
        if (songString != null && songString != "") {
            song = Song.fromJson(songString);
        }
        for(int i = 1; i < song.getChords().size()/2; i++){
            imageId.add(R.drawable.d_chord);
            imageId.add(R.drawable.e_chord);
        }
        imageId.add(R.drawable.eek_a_bug);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                chordImage = (ImageView) findViewById(R.id.better_pic);
                chordImage.setOnClickListener(new UpListener());
                chordImage.setOnLongClickListener(new DownListener());
            }
        });
        // place your listeners here. when the proper event has been activated, then just call one of the three functions below to update image.
    }

    private class UpListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            moveForward();
        }
    }

    private class DownListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
            moveBackward();
            return false;
        }
    }


    public void moveForward(){
        if (index < imageId.size() - 1){
            index++;
        } else {
            index = 0;
        }
        chordImage.setImageResource(imageId.get(index));
    }

    public void moveBackward(){
        if (index > 0){
            index--;
            chordImage.setImageResource(imageId.get(index));
        }
    }

}
