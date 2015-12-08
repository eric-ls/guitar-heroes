package com.example.eric.guitarheroes;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import com.guitarheroes.song.Song;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements ShakeDetector.Listener {

  MainActivity self = this;

  int onScreen = 0;
  final int animationSpeed = 500;
  final int majorTextSize = 30;
  final int minorTextSize = 22;
  final int chordSpacing = 20;

  public boolean canMoveForward(){
    int rootChildCount = ((RelativeLayout)findViewById(R.id.root)).getChildCount();
    Log.d("canMoveForward", "root child count: " + rootChildCount);
    Log.d("canMoveFoward", "onScreen: " + onScreen);

    return onScreen < ((RelativeLayout)findViewById(R.id.root)).getChildCount() -1 ? true : false;
  }

  public boolean canMoveBack(){
    return onScreen > 0;
  }

  String getChordDisplayName(String chordName){
    switch (chordName){
      case "a": return "A";
      case "am": return "Am";
      case "a7": return "A7";
      case "b": return "B";
      case "bm": return "Bm";
      case "b7": return "B7";
      case "c": return "C";
      case "cm": return "Cm";
      case "c7": return "C7";
      case "d": return "D";
      case "dm": return "Dm";
      case "d7": return "D7";
      case "e": return "E";
      case "em": return "Em";
      case "e7": return "E7";
      case "f": return "F";
      case "fm": return "Fm";
      case "f7": return "F7";
      case "g": return "G";
      case "gm": return "Gm";
      case "g7": return "G7";
      default: return "";
    }
  }

  String getChordFileName(String chordName){
    return chordName;
    /*
    switch (chordName){
      case "a": return "a_chord";
      case "am": return "am_chord";
      case "a7": return "a7_chord";
      case "b": return "b_chord";
      case "bm": return "bm_chord";
      case "b7": return "b7_chord";
      case "c": return "c_chord";
      case "cm": return "cm_chord";
      case "c7": return "c7_chord";
      case "d": return "d_chord";
      case "dm": return "dm_chord";
      case "d7": return "d7_chord";
      case "e": return "e_chord";
      case "em": return "em_chord";
      case "e7": return "e7_chord";
      case "f": return "f_chord";
      case "fm": return "fm_chord";
      case "f7": return "f7_chord";
      case "g": return "g_chord";
      case "gm": return "gm_chord";
      case "g7": return "g7_chord";
      default: return "";
    }
    */
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
      Log.d("moveBackward", "cant move backwardd");
      return false;
    }

    move(1);
    onScreen--;

    return true;
  }

  public void move(int direction) {
    RelativeLayout rl = (RelativeLayout) findViewById(R.id.root);
    RelativeLayout chordList = (RelativeLayout)findViewById(R.id.chord_list);

    ArrayList<ObjectAnimator> chords = new ArrayList<>();
    AnimatorSet as = new AnimatorSet();
    AnimatorSet.Builder asBuilder = null;

    for (int i = 0; i < rl.getChildCount(); i++) {
      RelativeLayout thisChord = (RelativeLayout) rl.getChildAt(i);
      ObjectAnimator animator = ObjectAnimator.ofFloat(thisChord, "x", thisChord.getX() + (thisChord.getWidth() * direction));

      TextView tv = (TextView)chordList.getChildAt(i);

      if (asBuilder == null) {
        asBuilder = as.play(animator);
      } else {
        asBuilder.with(animator);
      }

      if(i == onScreen-2){
        if(direction == 1){
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()/3 - (getTextPixels((String)tv.getText(), minorTextSize).width()/2));
          asBuilder.with(chordAnim);
        }
      } else if(i == onScreen-1){
        if(direction == 1){
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()/2 - (getTextPixels((String)tv.getText(), majorTextSize).width()/2));
          ObjectAnimator chordAnim2 = ObjectAnimator.ofFloat(tv, "textSize", minorTextSize, majorTextSize);
          ObjectAnimator chordAnim3 = ObjectAnimator.ofArgb(tv, "textColor", Color.LTGRAY, Color.WHITE);

          asBuilder.with(chordAnim2);
          asBuilder.with(chordAnim3);
          asBuilder.with(chordAnim);
        } else {
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x",  -getTextPixels((String)tv.getText(), majorTextSize).width());
          asBuilder.with(chordAnim);
        }
      } else if(i == onScreen){
        if(direction == 1){
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()*2/3 - (getTextPixels((String)tv.getText(), minorTextSize).width()/2));
          ObjectAnimator chordAnim2 = ObjectAnimator.ofFloat(tv, "textSize", majorTextSize, minorTextSize);
          ObjectAnimator chordAnim3 = ObjectAnimator.ofArgb(tv, "textColor", Color.WHITE, Color.LTGRAY);
          asBuilder.with(chordAnim2);
          asBuilder.with(chordAnim3);
          asBuilder.with(chordAnim);
        } else {
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()/3 - (getTextPixels((String)tv.getText(), minorTextSize).width()/2));
          ObjectAnimator chordAnim2 = ObjectAnimator.ofFloat(tv, "textSize", majorTextSize, minorTextSize);
          ObjectAnimator chordAnim3 = ObjectAnimator.ofArgb(tv, "textColor", Color.WHITE, Color.LTGRAY);

          asBuilder.with(chordAnim);
          asBuilder.with(chordAnim2);
          asBuilder.with(chordAnim3);
        }
      } else if(i == onScreen + 1){
        if(direction == 1){
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth());
          asBuilder.with(chordAnim);
        } else {
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()/2 - (getTextPixels((String)tv.getText(), majorTextSize).width()/2));
          ObjectAnimator chordAnim2 = ObjectAnimator.ofFloat(tv, "textSize", minorTextSize, majorTextSize);
          ObjectAnimator chordAnim3 = ObjectAnimator.ofArgb(tv, "textColor", Color.LTGRAY, Color.WHITE);

          asBuilder.with(chordAnim);
          asBuilder.with(chordAnim2);
          asBuilder.with(chordAnim3);
        }
      } else if(i == onScreen + 2){
        if(direction == -1){
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()*2/3 - (getTextPixels((String)tv.getText(), minorTextSize).width()/2));
          asBuilder.with(chordAnim);
        }
      }
    }

    as.setDuration(self.animationSpeed).start();
  }

  public void addChord(String chordName, String lyrics){
    RelativeLayout root = (RelativeLayout)findViewById(R.id.root);
    float screenWidth = findViewById(R.id.watch_view_stub).getMeasuredWidth();
    float startX = -screenWidth;

    TextView chordTitle = new TextView(self);

    chordTitle.setText(getChordDisplayName(chordName));

    chordTitle.setGravity(Gravity.CENTER_VERTICAL);
    //chordTitle.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_CENTER);

    //float text_width = getTextPixels("C", 20);

    //Log.d("addChord", "chord measured width: " + chordTitle.getMeasuredWidth() + ", width: " + chordTitle.getWidth() + ", paint width: " + text_width + ", paint height: " + text_height);

    RelativeLayout.LayoutParams ctParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    //ctParams.setMargins(10, 10, 10, 0);
    //ctParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

    if(root.getChildCount() == 0){
      chordTitle.setTextColor(Color.WHITE);
      chordTitle.setTextSize(majorTextSize);
      chordTitle.setX(screenWidth / 2 - getTextPixels(getChordDisplayName(chordName), majorTextSize).width() / 2);

      int textHeight = getTextPixels("C", majorTextSize).height();
      Log.d("addChord", "0, height: " + textHeight);
      double yVal = 30 - (textHeight/2.0);
      Log.d("addChord", "yVal: " + yVal);

    } else if(root.getChildCount() == 1){
      chordTitle.setTextColor(Color.LTGRAY);

      chordTitle.setTextSize(minorTextSize);
      chordTitle.setX(screenWidth*2/3 - (getTextPixels(getChordDisplayName(chordName), minorTextSize).width()/2));

    } else {

      chordTitle.setTextColor(Color.LTGRAY);
      chordTitle.setTextSize(minorTextSize);
      chordTitle.setX(screenWidth + getTextPixels(getChordDisplayName(chordName), minorTextSize).width());
    }

    chordTitle.setLayoutParams(ctParams);

    RelativeLayout chordTitles = (RelativeLayout)findViewById(R.id.chord_list);
    chordTitles.addView(chordTitle);



    RelativeLayout newChord = new RelativeLayout(self);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    newChord.setLayoutParams(params);
    newChord.setBackgroundColor(Color.TRANSPARENT);

    ImageView iv = new ImageView(self);
    RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    iv.setLayoutParams(ivParams);

    iv.setImageResource(getResources().getIdentifier(getChordFileName(chordName), "drawable", getPackageName()));
    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

    newChord.addView(iv);

    TextView lyricsTV = new TextView(self);
    lyricsTV.setText(lyrics);
    lyricsTV.setTextSize(14);
    lyricsTV.setTextColor(Color.WHITE);
    lyricsTV.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_CENTER);
    //lyricsTV.setGravity(Gravity.CENTER);

    RelativeLayout.LayoutParams lyricsParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lyricsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    lyricsParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);

    lyricsParams.setMargins(60, 0, 60, 25);

    lyricsTV.setLayoutParams(lyricsParams);

    newChord.addView(lyricsTV);

    if(root.getChildCount() > 0){
      startX = root.getChildAt(root.getChildCount() - 1).getX();
    }

    newChord.setX(startX + screenWidth);

    root.addView(newChord);

  }

  public Rect getTextPixels(String text, int fontSize){
    Paint paint = new Paint();
    Rect bounds = new Rect();

    int text_height = 0;
    int text_width = 0;

    paint.setTypeface(Typeface.DEFAULT);
    paint.setTextSize(fontSize);

    paint.getTextBounds(text, 0, text.length(), bounds);

    //text_height = bounds.height();
    //text_width = bounds.width();

    return bounds;
  }

  public void buildSong(ArrayList<HashMap<String, String>> song){
    for(int i=0; i<song.size(); i++){
      HashMap<String, String> thisChord = song.get(i);
      addChord(thisChord.get("chord"), thisChord.get("lyrics"));
    }
  }

  public void buildSong(Song song){
    for(int i=0; i<song.lyrics.size(); i++){
      addChord(song.chords.get(i), song.chords.get(i));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d("MainActivity", "onCreatee");

    final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
    stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
      @Override
      public void onLayoutInflated(WatchViewStub stub) {

        ArrayList<HashMap<String, String>> song = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> c1 = new HashMap<String, String>();
        c1.put("chord", "a");
        c1.put("lyrics", "Slowly walking down the hall");

        HashMap<String, String> c2 = new HashMap<String, String>();
        c2.put("chord", "a7");
        c2.put("lyrics", "Faster then a cannonball");

        HashMap<String, String> c3 = new HashMap<String, String>();
        c3.put("chord", "a");
        c3.put("lyrics", "I'm a birthday candle in a circle of black girls");

        HashMap<String, String> c4 = new HashMap<String, String>();
        c4.put("chord", "a7");
        c4.put("lyrics", "2 Loy dest de 2");

        song.add(c1);
        song.add(c2);
        song.add(c3);
        song.add(c4);

        buildSong(song);

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(self);
        shakeDetector.start(sensorManager);

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

