package com.example.eric.guitarheroes;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SongActivity extends AppCompatActivity {

  float x1 ,x2;
  final int MIN_DISTANCE = 150;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_song);

    ImageView image = (ImageView) findViewById(R.id.songImage);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch(event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        x1 = event.getX();
        break;
      case MotionEvent.ACTION_UP:
        x2 = event.getX();
        float deltaX = x2 - x1;
        if (Math.abs(deltaX) > MIN_DISTANCE) {
          finish();
        } else {
          // TODO: open up watch app here
        }
        break;
    }
    return super.onTouchEvent(event);
  }
}
