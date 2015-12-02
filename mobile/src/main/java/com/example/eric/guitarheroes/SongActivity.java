package com.example.eric.guitarheroes;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
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

    final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
              @Override
              public void onConnected(Bundle connectionHint) {
                Log.d("HEROES", "onConnected: " + connectionHint);
                // Now you can use the Data Layer API
              }
              @Override
              public void onConnectionSuspended(int cause) {
                Log.d("HEROES", "onConnectionSuspended: " + cause);
              }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
              @Override
              public void onConnectionFailed(ConnectionResult result) {
                Log.d("HEROES", "onConnectionFailed: " + result);
              }
            })
                    // Request access only to the Wearable API
            .addApi(Wearable.API)
            .build();

    ImageView image = (ImageView) findViewById(R.id.songImage);
    image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
            try {
              Log.d("HEROES", response.toString());
              PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
              putDataMapReq.getDataMap().putString("DATA", response.toString());
              PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
              PendingResult<DataApi.DataItemResult> pendingResult =
                      Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
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
