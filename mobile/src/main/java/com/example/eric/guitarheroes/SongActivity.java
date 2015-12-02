package com.example.eric.guitarheroes;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import pl.tajchert.buswear.EventBus;

import org.json.JSONObject;

public class SongActivity extends AppCompatActivity {

  public static GuitarPartyClient guitarPartyClient = new GuitarPartyClient();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_song);

    ImageView image = (ImageView) findViewById(R.id.songImage);
    final Activity that = this;
    image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
              Log.d("HEROES", response.toString());
              EventBus.getDefault().postRemote(response.toString(), that);     //Custom parcelable object
              Song song = new Song(response.getString("body"));
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        guitarPartyClient.get("songs/2/", new RequestParams(), handler);
      }
    });
  }
}
