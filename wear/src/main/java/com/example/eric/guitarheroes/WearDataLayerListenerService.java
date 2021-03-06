package com.example.eric.guitarheroes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.guitarheroes.song.Song;

/**
 * Created by Yangzi on 12/5/15.
 */
public class WearDataLayerListenerService extends WearableListenerService implements MessageApi.MessageListener {
  private static final String SONG_PATH = "/res/songs";
  private static final String JSON_SONG_KEY = "song data";

  @Override
  public void onMessageReceived (MessageEvent messageEvent) {
    Log.d("GUITARS", "EVENT RECEIVED");
    String text = "";
    try {
      text = new String(messageEvent.getData(), "UTF-8");
      Log.d("Song", text);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra("Song", text);
    startActivity(intent);
  }
}
