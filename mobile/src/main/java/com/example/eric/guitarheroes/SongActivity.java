package com.example.eric.guitarheroes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.guitarheroes.song.Song;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import pl.tajchert.buswear.EventBus;
import com.guitarheroes.song.Song;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SongActivity extends AppCompatActivity {
  private static final String TAG = SongActivity.class.getName();
  private static final String SONG_PATH = "/res/songs";
  private static final String JSON_SONG_KEY = "song data";

  private GoogleApiClient mGoogleApiClient;
  public static GuitarPartyClient guitarPartyClient = new GuitarPartyClient();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_song);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
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

    // Which image is this? The whole image? Or just open on song button?
    ImageView image = (ImageView) findViewById(R.id.songImage);
    image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Get the song data
        AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
            try {

              PutDataMapRequest putDataMapReq = PutDataMapRequest.create(SONG_PATH);
              putDataMapReq.getDataMap().putString(JSON_SONG_KEY, response.toString());
              PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
              PendingResult<DataApi.DataItemResult> pendingResult =
                      Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
              Song song = new Song(response);
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
