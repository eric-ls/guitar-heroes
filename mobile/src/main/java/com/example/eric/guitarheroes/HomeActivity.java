package com.example.eric.guitarheroes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import android.widget.SearchView;

import cz.msebera.android.httpclient.Header;
import com.guitarheroes.song.Song;

public class HomeActivity extends AppCompatActivity {

  GuitarPartyClient guitarParty = new GuitarPartyClient();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_home);
    FragmentManager fragmentManager = getFragmentManager();
    final Fragment topList = new SongListView();
    fragmentManager.beginTransaction()
            .add(R.id.My_Container_1_ID, topList)
            .commit();

    String defaultQuery = "love";
    SearchView search = (SearchView) findViewById(R.id.song_search);
    search.onActionViewExpanded();
    RequestParams params = new RequestParams();
    params.add("query", defaultQuery);
    AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
          ArrayList<Song> songs = new ArrayList<>();
          JSONArray objects = response.getJSONArray("objects");
          for (int i = 0; i < objects.length(); i++) {
            songs.add(new Song(objects.getJSONObject(i)));
          }
          ((SongListView) topList).setSongList(songs);
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
