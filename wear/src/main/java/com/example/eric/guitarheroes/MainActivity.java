package com.example.eric.guitarheroes;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.guitarheroes.song.Song;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements ShakeDetector.Listener {

  MainActivity self = this;

  int onScreen = 0;
  final int animationSpeed = 500;
  final int majorTextSize = 30;
  final int minorTextSize = 22;

  Song song;

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
    return chordName;
  }

  String getChordFileName(String chordName){
    Log.d("getChordFileName", chordName);
    chordName = chordName.substring(0, 1).toLowerCase() + chordName.substring(1);
    Log.d("getChordFileName", "after conversion: " + chordName);

    return chordName;

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

  public void removeChordImage(){

  }

  public void move(int direction) {
    RelativeLayout rl = (RelativeLayout) findViewById(R.id.root);
    RelativeLayout chordList = (RelativeLayout)findViewById(R.id.chord_list);

    ArrayList<ObjectAnimator> chords = new ArrayList<>();
    AnimatorSet as = new AnimatorSet();
    AnimatorSet.Builder asBuilder = null;


    for (int i = 0; i < rl.getChildCount(); i++) {
      final RelativeLayout thisChord = (RelativeLayout) rl.getChildAt(i);
      ObjectAnimator animator = ObjectAnimator.ofFloat(thisChord, "x", thisChord.getX() + (thisChord.getWidth() * direction));


      TextView tv = (TextView)chordList.getChildAt(i);
      final String chordTitle = (String)tv.getText();
      /*
      if(chordImage != null){
        chordImage.setImageResource(getResources().getIdentifier(getChordFileName((String)tv.getText()), "drawable", getPackageName()));
      }
      */

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

          ImageView chordImage = null;

          for(int j=0; j<thisChord.getChildCount(); j++){
            if(thisChord.getChildAt(j).getClass().equals(ImageView.class)){
              chordImage = (ImageView)thisChord.getChildAt(j);
              break;
            }
          }

          chordImage.setImageResource(getResources().getIdentifier(getChordFileName(chordTitle), "drawable", getPackageName()));
          //chordImage.setImageDrawable(null);
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

          animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
              ImageView chordImage = null;

              for (int j = 0; j < thisChord.getChildCount(); j++) {
                if (thisChord.getChildAt(j).getClass().equals(ImageView.class)) {
                  chordImage = (ImageView) thisChord.getChildAt(j);
                  break;
                }
              }

              //chordImage.setImageResource(getResources().getIdentifier(getChordFileName(chordTitle), "drawable", getPackageName()));
              chordImage.setImageDrawable(null);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
          });
        } else {
          ObjectAnimator chordAnim = ObjectAnimator.ofFloat(tv, "x", thisChord.getWidth()/3 - (getTextPixels((String)tv.getText(), minorTextSize).width()/2));
          ObjectAnimator chordAnim2 = ObjectAnimator.ofFloat(tv, "textSize", majorTextSize, minorTextSize);
          ObjectAnimator chordAnim3 = ObjectAnimator.ofArgb(tv, "textColor", Color.WHITE, Color.LTGRAY);

          asBuilder.with(chordAnim);
          asBuilder.with(chordAnim2);
          asBuilder.with(chordAnim3);

          animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
              ImageView chordImage = null;

              for (int j = 0; j < thisChord.getChildCount(); j++) {
                if (thisChord.getChildAt(j).getClass().equals(ImageView.class)) {
                  chordImage = (ImageView) thisChord.getChildAt(j);
                  break;
                }
              }

              //chordImage.setImageResource(getResources().getIdentifier(getChordFileName(chordTitle), "drawable", getPackageName()));
              chordImage.setImageDrawable(null);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
          });
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

          ImageView chordImage = null;

          for(int j=0; j<thisChord.getChildCount(); j++){
            if(thisChord.getChildAt(j).getClass().equals(ImageView.class)){
              chordImage = (ImageView)thisChord.getChildAt(j);
              break;
            }
          }

          chordImage.setImageResource(getResources().getIdentifier(getChordFileName(chordTitle), "drawable", getPackageName()));
          //chordImage.setImageDrawable(null);

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

    if(root.getChildCount() == 0){
      iv.setImageResource(getResources().getIdentifier(getChordFileName(chordName), "drawable", getPackageName()));
    }

    iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

    newChord.addView(iv);

    TextView lyricsTV = new TextView(self);
    if(lyrics.length() > 50){
      lyrics = lyrics.substring(0, 50);
    }

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
    for(int i=0; i<song.getLyrics().size(); i++){
      addChord(song.getChords().get(i), song.getLyrics().get(i));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d("MainActivity", "onCreatee");

    Intent i = getIntent();

    String songString = i.getStringExtra("Song");

    if(songString != null){
      Log.d("onCreate", "song string: " + songString);
      self.song = Song.fromJson(songString);
    }

    final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
    stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
      @Override
      public void onLayoutInflated(WatchViewStub stub) {
        /*
        self.song = new Song();
        ArrayList<String> chords = new ArrayList<String>();
        chords.add("a");
        chords.add("c");
        chords.add("a");
        chords.add("c");

        ArrayList<String> lyrics = new ArrayList<String>();
        lyrics.add("Hey Jude");
        lyrics.add("don't make it bad");
        lyrics.add("take a sad song");
        lyrics.add("and make it better");

        self.song.setChords(chords);
        self.song.setLyrics(lyrics);
        */

        if(self.song != null){
          buildSong(self.song);
        }


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
    Log.d("hearShakeDown", "shakeDownn");
    moveBackward();
  }
}

