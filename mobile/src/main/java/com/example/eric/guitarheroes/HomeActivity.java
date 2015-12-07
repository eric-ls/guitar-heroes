package com.example.eric.guitarheroes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;
import com.guitarheroes.song.Song;

public class HomeActivity extends AppCompatActivity {

  GuitarPartyClient guitarParty = new GuitarPartyClient();
  ITunesClient itunes = new ITunesClient();
  private static final String TAG = HomeActivity.class.getName();
  final Fragment topList = new SongListView();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_home);
    FragmentManager fragmentManager = getFragmentManager();
    fragmentManager.beginTransaction()
            .add(R.id.My_Container_1_ID, topList)
            .commit();
    loadResults("love");
    final SearchView search = (SearchView) findViewById(R.id.song_search);
    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "searching!");
        loadResults(((SearchView) search).getQuery().toString());
        return false;
      }
      @Override
      public boolean onQueryTextChange(String query) {
        return false;
      }
    });
  }

  protected void loadResults(String query) {
    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    RequestParams params = new RequestParams();
    params.add("query", query);
    final Context baseContext = this;
    AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
          final ArrayList<Song> songs = new ArrayList<>();
          final JSONArray objects = response.getJSONArray("objects");
          if (objects.length() == 0) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            Toast.makeText(baseContext, "No songs found!", Toast.LENGTH_LONG);
          }
          for (int i = 0; i < objects.length(); i++) {
            final int count = i;
            songs.add(new Song(objects.getJSONObject(i)));
            final Song song = songs.get(i);
            RequestParams params = new RequestParams();
            params.add("term", songs.get(i).getTitle());
            params.add("limit", "1");
            AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                  song.setArtUrl(response.getJSONArray("results").getJSONObject(0).getString("artworkUrl100"));
                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                  if (count == objects.length() - 1) {
                    ((SongListView) topList).setSongList(songs);
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                  }
                }
              }
            };
            itunes.get("search", params, handler);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    guitarParty.get("songs/", params, handler);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_home, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
