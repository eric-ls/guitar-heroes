package com.example.eric.guitarheroes;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kevin on 12/1/2015.
 */
public class Song {
    ArrayList<String> chords = new ArrayList<>();
    ArrayList<String> lyrics = new ArrayList<>();
    String title;
    String artist;
    int difficulty;

    public Song(String jsonString){
        jsonString = jsonString.replace("\r\n", " ");
        int indexOfOpenBracket = jsonString.indexOf('[');
        while(true){
            if (indexOfOpenBracket != 0){
                chords.add(" ");
                lyrics.add(jsonString.substring(0, indexOfOpenBracket));
                jsonString = jsonString.substring(indexOfOpenBracket);
                indexOfOpenBracket = 0;
            } else {
                chords.add(jsonString.substring(1, 2));
                jsonString = jsonString.substring(3);
                indexOfOpenBracket = jsonString.indexOf('[');
                if(indexOfOpenBracket == -1){
                    break;
                }
                lyrics.add(jsonString.substring(0, indexOfOpenBracket));
                jsonString = jsonString.substring(indexOfOpenBracket);
                indexOfOpenBracket = 0;
            }
            Log.d("Song", chords.get(chords.size() - 1) + " " + lyrics.get(lyrics.size() - 1));
        }
    }
}
