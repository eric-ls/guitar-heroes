package com.guitarheroes.song;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

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
    private ArrayList<String> chords = new ArrayList<>();
    private ArrayList<String> lyrics = new ArrayList<>();
    private String title;
    private String artist;
    private String uri;
    private String artUrl = "https://d30j0ipo6imng1.cloudfront.net/static/images/features/listen/album-placeholder.f97c23852f00.png";

    public Song(JSONObject songObj) {
        try {
            String jsonString = songObj.getString("body");
            jsonString = jsonString.replace("\r\n", " ");
            int indexOfOpenBracket = jsonString.indexOf('[');
            int indexOfClosedBracket = jsonString.indexOf(']');
            Log.d("Song", "Parsing song");
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
                //Log.d("Song", chords.get(chords.size() - 1) + " " + lyrics.get(lyrics.size() - 1));

                title = songObj.getString("title");
                artist = songObj.getJSONArray("authors").getJSONObject(0).getString("name");
                uri = songObj.getString("uri");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }
    public void setChords(ArrayList<String> chords) {
        this.chords = chords;
    }
    public void setLyrics(ArrayList<String> lyrics) {
        this.lyrics = lyrics;
    }
    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }
    public String getUri() {
        return uri;
    }
    public String getArtUrl() {
        return artUrl;
    }
    public ArrayList<String> getChords() {
        return chords;
    }
    public ArrayList<String> getLyrics() {
        return lyrics;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writeValueAsString(this);
            Log.d("guitar", jsonInString);
            return jsonInString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public static Song fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Song obj = mapper.readValue(s, Song.class);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Song(new JSONObject());
    }

}