// Copyright 2010 Square, Inc.
package com.example.eric.guitarheroes;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Detects phone shaking. If more than 75% of the samples taken in the past 0.5s are
 * accelerating, the device is a) shaking, or b) free falling 1.84m (h =
 * 1/2*g*t^2*3/4).
 *
 * @author Bob Lee (bob@squareup.com)
 * @author Eric Burke (eric@squareup.com)
 */
public class ShakeDetector implements SensorEventListener {

  public static final int SENSITIVITY_LIGHT = 11;
  public static final int SENSITIVITY_MEDIUM = 13;
  public static final int SENSITIVITY_HARD = 15;
  private static final int DEFAULT_ACCELERATION_THRESHOLD = SENSITIVITY_MEDIUM;

  private boolean isActive = true;

  ArrayList<Float> restingSamples = new ArrayList<>();

  Float restingValue;
  final private int restingTestCount = 10;
  /**
   * When the magnitude of total acceleration exceeds this
   * value, the phone is accelerating.
   */
  private int accelerationThreshold = DEFAULT_ACCELERATION_THRESHOLD;

  /** Listens for shakes. */
  public interface Listener {
    /** Called on the main thread when the device is shaken. */
    void hearUpShake();
    void hearDownShake();
  }

  private final SampleQueue queue = new SampleQueue();
  private final Listener listener;

  private SensorManager sensorManager;
  private Sensor accelerometer;

  public ShakeDetector(Listener listener) {
    this.listener = listener;
  }

  /**
   * Starts listening for shakes on devices with appropriate hardware.
   *
   * @return true if the device supports shake detection.
   */
  public boolean start(SensorManager sensorManager) {
    // Already started?
    if (accelerometer != null) {
      return true;
    }

    accelerometer = sensorManager.getDefaultSensor(
        Sensor.TYPE_ACCELEROMETER);

    // If this phone has an accelerometer, listen to it.
    if (accelerometer != null) {
      this.sensorManager = sensorManager;
      sensorManager.registerListener(this, accelerometer,
          SensorManager.SENSOR_DELAY_FASTEST);
    }
    return accelerometer != null;
  }

  /**
   * Stops listening.  Safe to call when already stopped.  Ignored on devices
   * without appropriate hardware.
   */
  public void stop() {
    if (accelerometer != null) {
      sensorManager.unregisterListener(this, accelerometer);
      sensorManager = null;
      accelerometer = null;
    }
  }

  public void pauseSelf(){
    isActive = false;
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Log.d("TimerTask", "running task, making active");
        makeActive();
      }
    }, 500);
  }

  public void makeActive(){
    isActive = true;
  }

  @Override public void onSensorChanged(SensorEvent event) {
    int accelerating = isAccelerating(event);
    long timestamp = event.timestamp;
    queue.add(timestamp, accelerating);

    if(!isActive){
      Log.d("onSensorChanged", "NOT ACTIVE");
      return;
    }

    if (queue.isShakingDown()) {

      queue.clear();
      listener.hearDownShake();
      pauseSelf();
    }

    if(queue.isShakingUp()){
      queue.clear();
      listener.hearUpShake();
      pauseSelf();
    }
  }

  /** Returns true if the device is currently accelerating. */
  private int isAccelerating(SensorEvent event) {
    if(!isActive){
      return 0;
    }

    float ax = event.values[0];
    float ay = event.values[1];
    float az = event.values[2];

    float usefulAxis = ax;
    //Log.d("isAccelerating", "x: " + ax + ", y" + ay + ", zz: " + az);
    // Instead of comparing magnitude to ACCELERATION_THRESHOLD,
    // compare their squares. This is equivalent and doesn't need the
    // actual magnitude, which would be computed using (expesive) Math.sqrt().
    //final double magnitudeSquared = ax * ax + ay * ay + az * az;
    restingSamples.add(usefulAxis);
    if(restingSamples.size() > restingTestCount){
      restingSamples.remove(0);
    }

    Float test_rest = isResting();

    if(test_rest != null){
      restingValue = test_rest;
    }


    //restingValue = isResting();

    //Log.d("isAccelerating", "restingValue: " + restingValue + ", x: " + usefulAxis);

    if(restingValue == null){
      return 0;
    }

    final double magnitudeSquared = (usefulAxis - restingValue) * (usefulAxis - restingValue);

    double threshold = (accelerationThreshold * accelerationThreshold)/30;

    Log.d("isAccelerating", "mag: " + magnitudeSquared + ", thres: " + threshold);

    //Log.d("isAccelerating", "mag: " + magnitudeSquared + ", threshold: " + threshold);
    boolean isAccelerating = magnitudeSquared > threshold;
    if(!isAccelerating){
      return 0;
    } else if(usefulAxis > 0){
      return 1;
    } else {
      return -1;
    }

    //return magnitudeSquared > accelerationThreshold * accelerationThreshold;
  }

  public Float isResting(){
    //Log.d("isResting", "restingSamples size: " + restingSamples.size());
    if(restingSamples.size() < restingTestCount){
      return null;
    }

    Float startingPoint = restingSamples.get(0);
    boolean isResting = true;
    float total = startingPoint;
    for(int i=1; i<restingSamples.size(); i++){
      Float thisSample = restingSamples.get(i);

      float diff = Math.abs(thisSample - startingPoint);

      //Log.d("isResting", "starting: " + startingPoint + ", thisSample: " + thisSample);
      if(diff > 0.35){
        isResting = false;
        //Log.d("isResting", "setting false");
        break;
      } else {
        total += thisSample;
      }
    }

    if(isResting){
      float restVal = total/(float)restingTestCount;
      //Log.d("isResting", "returning: " + restVal);
      return restVal;
    } else {
      return null;
    }
  }

  /** Sets the acceleration threshold sensitivity. */
  public void setSensitivity(int accelerationThreshold) {
    this.accelerationThreshold = accelerationThreshold;
  }

  /** Queue of samples. Keeps a running average. */
  static class SampleQueue {

    /** Window size in ns. Used to compute the average. */
    private static final long MAX_WINDOW_SIZE = 500000000; // 0.5s
    private static final long MIN_WINDOW_SIZE = MAX_WINDOW_SIZE >> 1; // 0.25s

    /**
     * Ensure the queue size never falls below this size, even if the device
     * fails to deliver this many events during the time window. The LG Ally
     * is one such device.
     */
    private static final int MIN_QUEUE_SIZE = 4;

    private final SamplePool pool = new SamplePool();

    private Sample oldest;
    private Sample newest;
    private int sampleCount;
    private int acceleratingUpCount;
    private int acceleratingDownCount;

    /**
     * Adds a sample.
     *
     * @param timestamp    in nanoseconds of sample
     * @param accelerating true if > {@link #accelerationThreshold}.
     */
    void add(long timestamp, int accelerating) {
      // Purge samples that proceed window.
      purge(timestamp - MAX_WINDOW_SIZE);

      // Add the sample to the queue.
      Sample added = pool.acquire();
      added.timestamp = timestamp;
      added.acceleratingUp = accelerating == 1 ? true : false;
      added.acceleratingDown = accelerating == -1 ? true : false;
      added.next = null;
      if (newest != null) {
        newest.next = added;
      }
      newest = added;
      if (oldest == null) {
        oldest = added;
      }

      // Update running average.
      sampleCount++;
      if (added.acceleratingUp) {
        acceleratingUpCount++;
      } else if (added.acceleratingDown){
        acceleratingDownCount++;
      }
    }

    /** Removes all samples from this queue. */
    void clear() {
      while (oldest != null) {
        Sample removed = oldest;
        oldest = removed.next;
        pool.release(removed);
      }
      newest = null;
      sampleCount = 0;
      acceleratingUpCount = 0;
      acceleratingDownCount = 0;
    }

    /** Purges samples with timestamps older than cutoff. */
    void purge(long cutoff) {
      while (sampleCount >= MIN_QUEUE_SIZE
          && oldest != null && cutoff - oldest.timestamp > 0) {
        // Remove sample.
        Sample removed = oldest;
        if (removed.acceleratingDown) {
          acceleratingDownCount--;
        } else if (removed.acceleratingUp){
          acceleratingDownCount--;
        }

        sampleCount--;

        oldest = removed.next;
        if (oldest == null) {
          newest = null;
        }
        pool.release(removed);
      }
    }

    /** Copies the samples into a list, with the oldest entry at index 0. */
    List<Sample> asList() {
      List<Sample> list = new ArrayList<Sample>();
      Sample s = oldest;
      while (s != null) {
        list.add(s);
        s = s.next;
      }
      return list;
    }

    /**
     * Returns true if we have enough samples and more than 3/4 of those samples
     * are accelerating.
     */
    boolean isShakingUp() {

      return newest != null
          && oldest != null
          && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE
          && acceleratingUpCount >= (sampleCount >> 1) + (sampleCount >> 2);
    }

    boolean isShakingDown(){

      return newest != null
              && oldest != null
              && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE
              && acceleratingDownCount >= (sampleCount >> 1) + (sampleCount >> 2);
    }
  }

  /** An accelerometer sample. */
  static class Sample {
    /** Time sample was taken. */
    long timestamp;

    /** If acceleration > {@link #accelerationThreshold}. */
    boolean acceleratingUp;

    boolean acceleratingDown;

    /** Next sample in the queue or pool. */
    Sample next;
  }

  /** Pools samples. Avoids garbage collection. */
  static class SamplePool {
    private Sample head;

    /** Acquires a sample from the pool. */
    Sample acquire() {
      Sample acquired = head;
      if (acquired == null) {
        acquired = new Sample();
      } else {
        // Remove instance from pool.
        head = acquired.next;
      }
      return acquired;
    }

    /** Returns a sample to the pool. */
    void release(Sample sample) {
      sample.next = head;
      head = sample;
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }
}
