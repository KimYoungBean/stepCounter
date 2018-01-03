package com.example.kimyoungbin.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

  private boolean isPush;

  /* Avoid counting faster than stepping */
  private boolean pocketFlag;
  private boolean callingFlag;
  private boolean handHeldFlag;
  private boolean handTypingFlag;

  /* Set threshold */
  private final double maxPocketThs = 15.0;
  private final double minPocketThs = 11.5;
  private final double maxCallingThs = 12.7;
  private final double minCallingThs = 11.1;
  private final double maxTypingThs = 13.5;
  private final double minTypingThs = 11.0;
  private final double maxHeldThs = 13.8;
  private final double minHeldThs = 11.0;
  private final double HandHeldXThs = 1.0;
  private final double HandHeldZThs = 1.5;
  private final double HandTypingThs = 0.5;

  private int stepCount;

  //Using the Accelometer & Gyroscoper
  private SensorManager mSensorManager = null;

  //Using the Accelometer
  private SensorEventListener mAccLis;
  private Sensor mAccelometerSensor = null;

  //Using the Gyroscoper
  private SensorEventListener mGyroLis;
  private Sensor mGgyroSensor = null;

  //Using the Closesensor
  private SensorEventListener mClsLis;
  private Sensor mClsSensor = null;

  private TextView mTextView;

  private boolean isPocket;
  private boolean isHandHeld;
  private boolean isHandTyping;

  private float distance;

  @Override
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(this, mClsSensor, SensorManager.SENSOR_DELAY_FASTEST);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = (TextView) findViewById(R.id.tv_count);

    isPush = true;
    pocketFlag = true;
    handHeldFlag = true;
    handTypingFlag = true;
    isPocket = false;
    stepCount = 0;

    //Using the Gyroscope & Accelometer
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    //Using the Accelometer
    mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mAccLis = new AccelometerListener();

    //Using the Gyroscoper
    mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    mGyroLis = new GyroscopeListener();

    //Using the Closesensor
    mClsSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    mClsLis = new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        float[] v = event.values;
        distance = v[0];
        //Log.e("DISTANCE", String.valueOf(distance));

        if (distance < 8.0) {
          Handler mHandler = new Handler();
          mHandler.postDelayed(new Runnable() {
            public void run() {
              isPocket = true;
            }
          }, 1800);
        } else {
          isPocket = false;
        }
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int i) {

      }
    };

    //Touch Listener for Accelometer
    findViewById(R.id.a_start).setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

          case MotionEvent.ACTION_DOWN:
            if (isPush) {
              mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);
              mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
              mSensorManager.registerListener(mClsLis, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_FASTEST);
              isPush = false;
            } else {
              mSensorManager.unregisterListener(mAccLis);
              mSensorManager.unregisterListener(mClsLis);
              isPush = true;
            }

            break;

          /*
          case MotionEvent.ACTION_UP:
            mSensorManager.unregisterListener(mAccLis);
            break;*/

        }
        return false;
      }
    });

  }

  @Override
  public void onPause() {
    super.onPause();
    Log.e("LOG", "onPause()");
    mSensorManager.unregisterListener(mAccLis);
    mSensorManager.unregisterListener(mClsLis);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e("LOG", "onDestroy()");
    mSensorManager.unregisterListener(mAccLis);
    mSensorManager.unregisterListener(mClsLis);
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {

  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }


  private class AccelometerListener implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent event) {

      double accX = event.values[0];
      double accY = event.values[1];
      double accZ = event.values[2];

      double angleXZ = Math.atan2(accX, accZ) * 180 / Math.PI;
      double angleYZ = Math.atan2(accY, accZ) * 180 / Math.PI;

      double tmp = (accX * accX) + (accY * accY) + (accZ * accZ);
      final double E = Math.sqrt(tmp);

      //Log.e("LOG", "ACCELOMETER           [E]:" + String.format("%.4f", E));
        /*  + "           [Y]:" + String.format("%.4f", event.values[1])
          + "           [Z]:" + String.format("%.4f", event.values[2])
          + "           [E]:" + String.format("%.4f", E));
      + "           [angleXZ]: " + String.format("%.4f", angleXZ)
      + "           [angleYZ]: " + String.format("%.4f", angleYZ));*/

      /** In the pocket **/
      if (isPocket) {
        if (E > minPocketThs && E < maxPocketThs && pocketFlag) {
          stepCount++;
          Log.e("LOG", "ACCELOMETER           [E]:" + String.format("%.4f", E));
          Log.e("LOG", String.valueOf(stepCount));
          pocketFlag = false;

          Handler mHandler = new Handler();
          mHandler.postDelayed(new Runnable() {
            public void run() {
              pocketFlag = true;
            }
          }, 400);
        }

        /** Calling **/
        else if (E > minCallingThs && E < maxCallingThs && callingFlag) {
          stepCount++;
          Log.e("LOG", "ACCELOMETER           [E]:" + String.format("%.4f", E));
          Log.e("LOG", String.valueOf(stepCount));
          callingFlag = false;

          Handler mHandler = new Handler();
          mHandler.postDelayed(new Runnable() {
            public void run() {
              callingFlag = true;
            }
          }, 400);
        }
      }

      else {
        /** Walking with typing **/
        if (E > minTypingThs && E < maxTypingThs && isHandTyping && handTypingFlag) {
          stepCount++;
          isHandTyping = false;
          Log.e("LOG", String.valueOf(stepCount));
          Log.e("LOG", "ACCELOMETER           [E]:" + String.format("%.4f", E));
          handTypingFlag = false;

          Handler mHandler = new Handler();
          mHandler.postDelayed(new Runnable() {
            public void run() {
              handTypingFlag = true;
            }
          }, 400);
        }
        /** Hand held working **/
        else if (E > minHeldThs && E < maxHeldThs && isHandHeld && handHeldFlag) {
          stepCount++;
          isHandHeld = false;
          Log.e("LOG", String.valueOf(stepCount));
          Log.e("LOG", "ACCELOMETER           [E]:" + String.format("%.4f", E));
          handHeldFlag = false;

          Handler mHandler = new Handler();
          mHandler.postDelayed(new Runnable() {
            public void run() {
              handHeldFlag = true;
            }
          }, 400);
        }
      }

      mTextView.setText(String.format("%d", (int) stepCount));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  }

  private class GyroscopeListener implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent event) {

      /* receives the angular velocity of each axis. */
      double gyroX = event.values[0];
      double gyroY = event.values[1];
      double gyroZ = event.values[2];

      /* Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
          + "           [Y]:" + String.format("%.4f", event.values[1])
          + "           [Z]:" + String.format("%.4f", event.values[2])); */

      /* detect gyroZ motion when walking with hand */
      if(Math.abs(gyroZ) > HandHeldZThs)
        isHandHeld = true;

      /* if gyroX moves a lot, it is not time to walking with hand */
      if(Math.abs(gyroX) > HandHeldXThs)
        isHandHeld = false;

      /* detect few motion when walking while typing */
      if(Math.abs(gyroX) < HandTypingThs && Math.abs(gyroY) < HandTypingThs && Math.abs(gyroZ) < HandTypingThs)
        isHandTyping = true;
      else
        isHandTyping = false;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  }

}
