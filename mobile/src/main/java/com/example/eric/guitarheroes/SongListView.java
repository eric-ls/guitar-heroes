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

import com.guitarheroes.song.Song;

/**
 * Created by Kevin on 12/5/2015.
 */
public class SongListView extends ListFragment{
    public SongAdapter adapter;
    public ArrayList<Song> songList = new ArrayList<>();

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        adapter = new SongAdapter(songList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
    }

    public void setSongList(ArrayList<Song> songs) {
        adapter.clear();
        adapter.addAll(songs);
        adapter.notifyDataSetChanged();
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
