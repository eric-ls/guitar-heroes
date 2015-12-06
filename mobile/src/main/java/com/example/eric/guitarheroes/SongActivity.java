package com.example.eric.guitarheroes;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Node;
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

  private static GoogleApiClient mGoogleApiClient;
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

      mGoogleApiClient.connect();
    ImageView image = (ImageView) findViewById(R.id.songImage);
    image.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Get the song data
        AsyncHttpResponseHandler handler = new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
            try {
              Song song = new Song(response);
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        Log.d(TAG, Integer.toString(getConnectedNodesResult.getNodes().size()));
                        for (Node node : getConnectedNodesResult.getNodes()) {
                            Log.d(TAG, node.toString());
                            sendWearableMessage(node.toString(), "");
                        }
                    }
                });
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        guitarPartyClient.get("songs/2/", new RequestParams(), handler);
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
}
