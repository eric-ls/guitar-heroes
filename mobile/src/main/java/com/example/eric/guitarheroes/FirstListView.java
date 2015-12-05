package com.example.eric.guitarheroes;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kevin on 12/5/2015.
 */
public class FirstListView extends ListFragment{
    ArrayList<Song> listOfRecentSongs = new ArrayList<>();

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        SongAdapter adapter = new SongAdapter(listOfRecentSongs);
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

    }

    private class SongAdapter extends ArrayAdapter<Song> {
        public SongAdapter(ArrayList<Song> songs){
            super(getActivity(), 0, songs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.song_item, null);
            }
            // Populate layout with stuff
            Song song = getItem(position);
            TextView songTitle = (TextView) convertView.findViewById(R.id.song_title);
            TextView songArtist = (TextView) convertView.findViewById(R.id.song_artist);
            ImageView albumCover = (ImageView) convertView.findViewById(R.id.song_image);
            songTitle.setText(song.title);
            songArtist.setText(song.artist);

            return convertView;
        }
    }
}
