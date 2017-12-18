package com.example.kimyoungbin.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

  private long lastTime;
  private float speed;
  private float lastX;
  private float lastY;
  private float lastZ;
  private float x, y, z;

  private static final int SHAKE_THRESHOLD = 300;
  private static final int DATA_X = SensorManager.DATA_X;
  private static final int DATA_Y = SensorManager.DATA_Y;
  private static final int DATA_Z = SensorManager.DATA_Z;

  //Using the Accelometer & Gyroscoper
  private SensorManager mSensorManager = null;

  //Using the Accelometer
  private Sensor mAccelometerSensor = null;

  //Using the Gyroscoper
  private SensorEventListener mGyroLis;
  private Sensor mGgyroSensor = null;

  private double mStep;
  private boolean isStep;

  TextView textView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Using the Gyroscope & Accelometer
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    //Using the Accelometer
    mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    //Using the Gyroscoper
    mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    mGyroLis = new GyroscopeListener();

    mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

    textView = (TextView) findViewById(R.id.tv_step);
  }
    /* start accelometerSensor listener */
  @Override
  public void onStart() {
    super.onStart();
    if (mAccelometerSensor != null) {
      mSensorManager.registerListener(this, mAccelometerSensor,
          SensorManager.SENSOR_DELAY_GAME);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.e("LOG", "onPause()");
    mSensorManager.unregisterListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e("LOG", "onDestroy()");
    mSensorManager.unregisterListener(this);
  }
        /*
        if accelerometer change, it occurs.
        shake control
         */
  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      long currentTime = System.currentTimeMillis();        //current Time (per milliseconds)
      long gabOfTime = (currentTime - lastTime);            // gap of current and last time
      if (gabOfTime > 100) {
        lastTime = currentTime;
        x = event.values[SensorManager.DATA_X];
        y = event.values[SensorManager.DATA_Y];
        z = event.values[SensorManager.DATA_Z];

        speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;    // abs(sum of values)
            //SHAKE_THRESHOLD = 300
        if (speed > SHAKE_THRESHOLD) {
          //event occured!!
          isStep = false;       // No count
        } else {
          isStep = true;        // count
        }
        lastX = event.values[DATA_X];
        lastY = event.values[DATA_Y];
        lastZ = event.values[DATA_Z];
      }
    }
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

      Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
          + "           [Y]:" + String.format("%.4f", event.values[1])
          + "           [Z]:" + String.format("%.4f", event.values[2])
          + "           [angleXZ]: " + String.format("%.4f", angleXZ)
          + "           [angleYZ]: " + String.format("%.4f", angleYZ));

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

      /* standard value to compare to determining step */
      final double CMP = 0.2;

      /* Compares the X and Z axis values ​​to determine the step. */
      if (!isStep) {
        if (gyroX > CMP && gyroZ < -CMP) {
          mStep++;
          isStep = true;
        }
      }

      if (isStep) {
        if (gyroX < -CMP && gyroZ > CMP) {
          mStep++;
          isStep = false;
        }
      }

      Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
          + "           [Y]:" + String.format("%.4f", event.values[1])
          + "           [Z]:" + String.format("%.4f", event.values[2]));

      textView.setText(String.format("%d", (int) mStep));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  }


}