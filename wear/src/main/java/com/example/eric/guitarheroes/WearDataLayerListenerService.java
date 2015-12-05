package com.example.eric.guitarheroes;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Yangzi on 12/5/15.
 */
public class WearDataLayerListenerService extends WearableListenerService {
  private static final String SONG_PATH = "/res/songs";
  private static final String JSON_SONG_KEY = "song data";

  @Override
  public void onDataChanged(DataEventBuffer dataEvents) {
    for (DataEvent event : dataEvents) {
      if (event.getType() == DataEvent.TYPE_CHANGED) {
        // DataItem changed
        DataItem item = event.getDataItem();
        if (item.getUri().getPath().equals(SONG_PATH)) {
          // Do we need the dataMap item?
          DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
          Intent intent = new Intent(this, MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        }
      }
    }
  }
}

