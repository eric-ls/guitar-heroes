package com.example.eric.guitarheroes;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import com.guitarheroes.song.Song;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements SensorEventListener, ShakeDetector.Listener {

  MainActivity self = this;
  double xVal = 0.0;
  double yVal = 0.0;
  double zVal = 0.0;

  int onScreen = 0;

  int animationSpeed = 500;

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    Log.d("onSensorChanged", "x: " + sensorEvent.values[0] + ", y: " + sensorEvent.values[1] + ", z: " + sensorEvent.values[2]);
    double xReading = sensorEvent.values[2];
    double yReading = sensorEvent.values[1];
    double zReading = sensorEvent.values[2];

    if(xReading < 50 && xVal == 400){

      self.moveForward();

    } else if(xReading > -50 && xVal == -400){

      self.moveBackward();
    }

    xVal = xReading;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  public boolean canMoveForward(){
    int rootChildCount = ((RelativeLayout)findViewById(R.id.root)).getChildCount();
    Log.d("canMoveForward", "root child count: " + rootChildCount);
    Log.d("canMoveFoward", "onScreen: " + onScreen);

    return onScreen < ((RelativeLayout)findViewById(R.id.root)).getChildCount() -1 ? true : false;
  }

  public boolean canMoveBack(){
    return onScreen > 0;
  }

  public boolean moveForward(){
    Log.d(getClass().toString(), "moveForward");
    if(!canMoveForward()){
      Log.d("moveFoward", "cant move forward");
      return false;
    }

    move(-1);
    onScreen++;

    return true;
  }

  public boolean moveBackward(){
    if(!self.canMoveBack()){
      Log.d("moveBackward", "cant move backward");
      return false;
    }

    move(1);
    onScreen--;

    return true;
  }

  public void move(int direction) {
    RelativeLayout rl = (RelativeLayout) findViewById(R.id.root);
    ArrayList<ObjectAnimator> chords = new ArrayList<>();
    AnimatorSet as = new AnimatorSet();
    AnimatorSet.Builder asBuilder = null;

    for (int i = 0; i < rl.getChildCount(); i++) {
      RelativeLayout thisChord = (RelativeLayout) rl.getChildAt(i);
      ObjectAnimator animator = ObjectAnimator.ofFloat(thisChord, "x", thisChord.getX() + (thisChord.getWidth() * direction));


      if (asBuilder == null) {
        asBuilder = as.play(animator);
      } else {
        asBuilder.with(animator);
      }

    }

    as.setDuration(self.animationSpeed).start();
  }

  public void addChord(String chordName, String lyrics){
    RelativeLayout root = (RelativeLayout)findViewById(R.id.root);

    RelativeLayout newChord = new RelativeLayout(self);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    newChord.setLayoutParams(params);
    newChord.setBackgroundColor(Color.DKGRAY);

    ImageView iv = new ImageView(self);
    RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    iv.setLayoutParams(ivParams);

    iv.setImageResource(getResources().getIdentifier(chordName, "drawable", getPackageName()));
    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

    newChord.addView(iv);

    TextView lyricsTV = new TextView(self);
    lyricsTV.setText(lyrics);
    lyricsTV.setTextSize(14);
    lyricsTV.setTextColor(Color.GREEN);
    lyricsTV.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_CENTER);

    RelativeLayout.LayoutParams lyricsParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lyricsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    lyricsParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
    lyricsParams.setMargins(60, 0, 60, 30);

    lyricsTV.setLayoutParams(lyricsParams);

    newChord.addView(lyricsTV);

    float screenWidth = findViewById(R.id.watch_view_stub).getMeasuredWidth();
    float startX = -screenWidth;

    if(root.getChildCount() > 0){
      startX = root.getChildAt(root.getChildCount() - 1).getX();
    }

    newChord.setX(startX + screenWidth);

    root.addView(newChord);
  }

  public void buildSong(ArrayList<HashMap<String, String>> song){
    for(int i=0; i<song.size(); i++){
      HashMap<String, String> thisChord = song.get(i);
      addChord(thisChord.get("chord"), thisChord.get("lyrics"));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d("MainActivity", "onCreatee");

    SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    ShakeDetector shakeDetector = new ShakeDetector(self);
    shakeDetector.start(sensorManager);

    //Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
    stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
      @Override
      public void onLayoutInflated(WatchViewStub stub) {

        //RelativeLayout pic2 = (RelativeLayout) findViewById(R.id.pic2);
        //WatchViewStub stub1 = (WatchViewStub) findViewById(R.id.watch_view_stub);
        //Log.d("onLayoutInflated", "pic2 id: " + pic2.getId());
        //Log.d("onLayoutInflated", "width: " + stub1.getMeasuredWidth());
        //pic2.setX(stub1.getMeasuredWidth());

        ArrayList<HashMap<String, String>> song = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> c1 = new HashMap<String, String>();
        c1.put("chord", "d_chord");
        c1.put("lyrics", "Slowly walking down the hall");

        HashMap<String, String> c2 = new HashMap<String, String>();
        c2.put("chord", "e_chord");
        c2.put("lyrics", "Faster then a cannonball");

        HashMap<String, String> c3 = new HashMap<String, String>();
        c3.put("chord", "d_chord");
        c3.put("lyrics", "I'm a birthday candle in a circle of black girls");

        song.add(c1);
        song.add(c2);
        song.add(c3);

        buildSong(song);
      }
    });
  }

  @Override
  public void hearUpShake() {
    Log.d("hearShakeUp", "shake up");
    moveForward();
  }

  @Override
  public void hearDownShake() {
    Log.d("hearShakeDown", "shakeDown");
    moveBackward();
  }
}

