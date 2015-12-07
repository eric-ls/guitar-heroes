package com.example.eric.guitarheroes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Kevin on 12/1/2015.
 */
public class BetterMainActivity extends Activity {
    // Image Id's to an entire song. Let's do Hotel California for now or something.
    private ArrayList<Integer> imageId = new ArrayList<>();

    private ImageView chordImage;


    //the index determines the picture that is currently being displayed.
    private int index = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Add image id's to an array list that holds all image id's
        imageId.add(R.drawable.d_chord);
        imageId.add(R.drawable.e_chord);
        imageId.add(R.drawable.base10z);
        imageId.add(R.drawable.eek_a_bug);
        chordImage = (ImageView) findViewById(R.id.better_pic);

        // place your listeners here. when the proper event has been activated, then just call one of the three functions below to update image.
    }

    public void moveForward(){
        if (index < imageId.size() - 1){
            index++;
            chordImage.setBackgroundResource(imageId.get(index));
        }
    }

    public void moveBackward(){
        if (index > 0){
            index--;
            chordImage.setBackgroundResource(imageId.get(index));
        }
    }

    public void repeatSong(){
        if(index == imageId.size() - 1){
            index = 0;
            chordImage.setBackgroundResource(imageId.get(index));
        }
    }


}
