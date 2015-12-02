package com.example.eric.guitarheroes;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kevin on 12/1/2015.
 *
 * Create a Song by passing in the string in the "body" portion of the json in the guitarparty response.
 * For every chord and lyric verse, the chords and lyric verses should correspond to each other if they have the same index.
 * For some songs, they may not start on a chord which means the first chord will be empty
 *
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
        int indexOfClosedBracket = jsonString.indexOf(']');
        while(true){
            if (indexOfOpenBracket != 0){
                chords.add(" ");
                lyrics.add(jsonString.substring(0, indexOfOpenBracket));
                jsonString = jsonString.substring(indexOfOpenBracket);
                indexOfOpenBracket = 0;
                indexOfClosedBracket = jsonString.indexOf(']');
            } else {
                chords.add(jsonString.substring(1, indexOfClosedBracket));
                jsonString = jsonString.substring(indexOfClosedBracket + 1);
                indexOfOpenBracket = jsonString.indexOf('[');
                if(indexOfOpenBracket == -1){
                    break;
                }
                lyrics.add(jsonString.substring(0, indexOfOpenBracket));
                jsonString = jsonString.substring(indexOfOpenBracket);
                indexOfOpenBracket = 0;
                indexOfClosedBracket = jsonString.indexOf(']');
            }
            Log.d("Song", chords.get(chords.size() - 1) + " " + lyrics.get(lyrics.size() - 1));
        }
    }

    public void setExtraInfo(int difficulty, String artist, String title){
        this.difficulty = difficulty;
        this.artist = artist;
        this.title = title;
    }
}