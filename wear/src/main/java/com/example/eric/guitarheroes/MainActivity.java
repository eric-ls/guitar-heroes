package com.example.eric.guitarheroes;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

  private TextView mTextView;
  MainActivity self = this;
  double xVal = 0.0;
  double yVal = 0.0;
  double zVal = 0.0;

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    Log.d("onSensorChanged", "x: " + sensorEvent.values[0] + ", y: " + sensorEvent.values[1] + ", z: " + sensorEvent.values[2]);
    double xReading = sensorEvent.values[2];
    double yReading = sensorEvent.values[1];
    double zReading = sensorEvent.values[2];

    if(xReading < 50 && xVal == 400){
      UpListener ul = new UpListener();
      ul.onClick(new View(self));
    } else if(xReading > -50 && xVal == -400){
      DownListener dl = new DownListener();
      dl.onLongClick(new View(self));
    }

    xVal = xReading;
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  private class DownListener implements View.OnLongClickListener{

    @Override
    public boolean onLongClick(View view) {
      //Toast.makeText(self, "Long Click!", Toast.LENGTH_SHORT).show();
      //ImageView listView = (ImageView)findViewById(R.id.hashtag_list);

      //viewToShow.setVisibility(View.VISIBLE);
      final RelativeLayout pic = (RelativeLayout)findViewById(R.id.pic1);
      int viewHeight = pic.getMeasuredWidth();

      RelativeLayout pic2 = (RelativeLayout)findViewById(R.id.pic2);

      ObjectAnimator animator = ObjectAnimator.ofFloat(pic, "x", pic.getX() + viewHeight);
      ObjectAnimator animator2 = ObjectAnimator.ofFloat(pic2, "x", pic2.getX() + viewHeight);

      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.play(animator2).with(animator);
      animatorSet.setDuration(500).start();
      return false;
    }
  }

  private class UpListener implements View.OnClickListener{

    @Override
    public void onClick(View view) {
      //Toast.makeText(self, "Clicked!", Toast.LENGTH_SHORT).show();
      //ImageView listView = (ImageView)findViewById(R.id.hashtag_list);

      //viewToShow.setVisibility(View.VISIBLE);
      final RelativeLayout pic = (RelativeLayout)findViewById(R.id.pic1);
      int viewHeight = pic.getMeasuredWidth();

      RelativeLayout pic2 = (RelativeLayout)findViewById(R.id.pic2);

      ObjectAnimator animator = ObjectAnimator.ofFloat(pic, "x", pic.getX() - viewHeight);
      ObjectAnimator animator2 = ObjectAnimator.ofFloat(pic2, "x", pic2.getX() - viewHeight);

      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.play(animator2).with(animator);
      animatorSet.setDuration(500).start();
                        /*
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                pic.requestFocus();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        */

      //animator.setDuration(2000).start();
      //animator2.setDuration(2000).start();

      //ObjectAnimator.ofFloat(listView, "y", listView.getY() + viewHeight).setDuration(400).start();

      //ObjectAnimator.ofFloat(listView, "scaleY", 2.0f).setDuration(400).start();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d("MainActivity", "onCreate");

    SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
    stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
      @Override
      public void onLayoutInflated(WatchViewStub stub) {
        mTextView = (TextView) stub.findViewById(R.id.text);

        RelativeLayout pic2 = (RelativeLayout) findViewById(R.id.pic2);
        RelativeLayout pic1 = (RelativeLayout) findViewById(R.id.pic1);
        WatchViewStub stub1 = (WatchViewStub)findViewById(R.id.watch_view_stub);

        Log.d("onLayoutInflated", "width: " + stub1.getMeasuredWidth());
        pic2.setX(stub1.getMeasuredWidth());


        pic1.setOnClickListener(new UpListener());
        pic2.setOnClickListener(new UpListener());

        pic1.setOnLongClickListener(new DownListener());
        pic2.setOnLongClickListener(new DownListener());

      }
    });
  }

  @Override
  protected void onResume(){
    super.onResume();
  }
}