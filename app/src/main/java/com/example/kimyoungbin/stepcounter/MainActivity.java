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

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x,y,z;

    private static final int SHAKE_THRESHOLD = 300;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

  //Using the Accelometer & Gyroscoper
  private SensorManager mSensorManager = null;

  //Using the Accelometer
  private SensorEventListener mAccLis;
  private Sensor mAccelometerSensor = null;

  //Using the Gyroscoper
  private SensorEventListener mGyroLis;
  private Sensor mGgyroSensor = null;

  private double mStep;
  private boolean isStep;

  TextView textView;

  /** G sensor valuable **/
  /************************************************************/
  //Roll and Pitch
  private double pitch;
  private double roll;
  private double yaw;

  //timestamp and dt
  private double timestamp;
  private double dt;

  // for radian -> dgree
  private double RAD2DGR = 180 / Math.PI;
  private static final float NS2S = 1.0f / 1000000000.0f;

  static int isTouch = 0;
  static int gTouch = 0;

  /************************************************************/


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Using the Gyroscope & Accelometer
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    //Using the Accelometer
    mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mAccLis = new AccelometerListener();

    //Using the Gyroscoper
    mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    mGyroLis = new GyroscopeListener();

    mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

    textView = (TextView) findViewById(R.id.tv_step);

  }

    @Override
  public void onPause() {
    super.onPause();
    Log.e("LOG", "onPause()");
    mSensorManager.unregisterListener(mAccLis);
    mSensorManager.unregisterListener(mGyroLis);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e("LOG", "onDestroy()");
    mSensorManager.unregisterListener(mAccLis);
    mSensorManager.unregisterListener(mGyroLis);
  }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if(gabOfTime > 100){
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x+y+z-lastX-lastY-lastZ)/gabOfTime * 10000;

                if(speed > SHAKE_THRESHOLD){
                    //이벤트발생!!
                    isStep = false;
                    Log.e("LOG", "너무 많이 흔듬");
                }else{
                    isStep = true;
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

            /* 각 축의 각속도 성분을 받는다. */
      double gyroX = event.values[0];
      double gyroY = event.values[1];
      double gyroZ = event.values[2];

      final double NUM = 0.2;

            /* 각속도를 적분하여 회전각을 추출하기 위해 적분 간격(dt)을 구한다.
             * dt : 센서가 현재 상태를 감지하는 시간 간격
             * NS2S : nano second -> second */
            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */

      if (!isStep) {
        if (gyroX > NUM && gyroZ < -NUM ) {
          mStep++;
          isStep = true;
        }
      }

      if (isStep) {
        if (gyroX < -NUM && gyroZ > NUM ) {
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