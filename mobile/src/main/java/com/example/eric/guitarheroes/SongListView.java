package com.example.eric.guitarheroes;

import android.app.ListFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.guitarheroes.song.Song;

/**
 * Created by Kevin on 12/5/2015.
 */
public class SongListView extends ListFragment {
    public SongAdapter adapter;
    public ArrayList<Song> songList = new ArrayList<>();
    private HashMap<String, Bitmap> images = new HashMap<>();
    private static GoogleApiClient mGoogleApiClient;
    private static final String TAG = SongActivity.class.getName();
    private static final String SONG_PATH = "/res/songs";

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        adapter = new SongAdapter(songList);

        setListAdapter(adapter);
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, String.format("Failed. Error msg: %s. Error Code: %s.",
                                result.getErrorMessage(),
                                result.getErrorCode()));
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id){
        Log.d("guitar", songList.get(position).uri);
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                Log.d(TAG, Integer.toString(getConnectedNodesResult.getNodes().size()));
                for (Node node : getConnectedNodesResult.getNodes()) {
                    Log.d(TAG, node.toString());
                    sendWearableMessage(node.toString(), songList.get(position).title);
                }
            }
        });
    }

    private static void sendWearableMessage(String node, String data) {
        try {
            Wearable.MessageApi.sendMessage(mGoogleApiClient , node , SONG_PATH , data.getBytes("UTF-8")).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    if (!sendMessageResult.getStatus().isSuccess()) {
                        Log.e("GoogleApi", "Failed to send message with status code: "
                                + sendMessageResult.getStatus().getStatusCode());
                    }
                }
            });
        } catch (Exception e) {
        }
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
            if (images.containsKey(song.artUrl)) {
                albumCover.setImageBitmap(images.get(song.artUrl));
            } else {
                new DownloadImageTask(albumCover).execute(song.artUrl);
            }
            songTitle.setText(song.title);
            songArtist.setText(song.artist);

            return convertView;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String urldisplay;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            images.put(urldisplay, result);
        }
    }
}
