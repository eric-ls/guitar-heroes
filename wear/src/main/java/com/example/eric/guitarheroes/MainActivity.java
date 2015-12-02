package com.example.eric.guitarheroes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.eric.guitarheroes.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

  private static final String COUNT_KEY = "com.example.key.count";

  private GoogleApiClient mGoogleApiClient;
  private int count = 0;
  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = (TextView) findViewById(R.id.text);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnected(Bundle bundle) {
    Wearable.DataApi.addListener(mGoogleApiClient, this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    Wearable.DataApi.removeListener(mGoogleApiClient, this);
    mGoogleApiClient.disconnect();
  }

  @Override
  public void onConnectionSuspended(int a) {}
  @Override
  public void onConnectionFailed(ConnectionResult a) {}

  @Override
  public void onDataChanged(DataEventBuffer dataEvents) {
    for (DataEvent event : dataEvents) {
      if (event.getType() == DataEvent.TYPE_CHANGED) {
        // DataItem changed
        DataItem item = event.getDataItem();
        if (item.getUri().getPath().compareTo("/count") == 0) {
          DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
          updateString(dataMap.getString(COUNT_KEY));
        }
      } else if (event.getType() == DataEvent.TYPE_DELETED) {
        // DataItem deleted
      }
    }
  }

  private void updateString(String s) {
    mTextView.setText(s);
  }
}
