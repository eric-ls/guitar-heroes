package com.example.eric.guitarheroes;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.tajchert.buswear.EventBus;

public class MainActivity extends WearableActivity {

  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = (TextView) findViewById(R.id.text);

    EventBus.getDefault().register(this);
  }

  public void onEvent(String text){
    //Do your stuff with that object
    mTextView.setText(text);
  }
}
