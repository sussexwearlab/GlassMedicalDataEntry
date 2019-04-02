/*
 * Copyright (c) 2019. Francisco Javier Ordoñez Morales, Mathias Ciliberto, Daniel Roggen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package sussex.strc.wear.test.motion;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

/**
 * Created by fjordonez on 17/12/15.
 */
public class HeadCursorMotionHandler implements SensorEventListener {

    private static final String TAG = HeadCursorMotionHandler.class.getSimpleName();

    private SensorManager mSensorManager;
    private static float[] mSensitivity;
    private boolean isStarted;
    private float[] mCurrent = new float[2];
    private float[] mInit = new float[2];

    private static HeadCursorMotionListener mHeadCursorMotionListener;

    public HeadCursorMotionHandler(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void start(float[] sensitivity, float[] init) {
        Sensor sensor = null;
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if (sensors.size() > 1) {
            // Google Glass has two gyroscopes: "MPL Gyroscope" and "Corrected Gyroscope Sensor". Try the later one.
            sensor = sensors.get(1);
        } else {
            sensor = sensors.get(0);
        }
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensitivity = sensitivity;
        isStarted = false;
        mInit[0] = init[0] * mSensitivity[0];
        mInit[1] = init[1] * mSensitivity[1];
    }


    public void start(float[] sensitivity) {
        start(sensitivity, new float[]{0,0});
    }

    public void stop() {
        try {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
            }
        } catch (Exception e) {
        }
    }

    public void setCursorMotionListener(HeadCursorMotionListener headCursorMotionListener) {
        this.mHeadCursorMotionListener = headCursorMotionListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            float[]gyroscopeValues = event.values.clone();
            if (!isStarted) {
                isStarted = true;
                mCurrent[0] = gyroscopeValues[1] + mInit[0];
                mCurrent[1] = gyroscopeValues[0] + mInit[1];
            }else{
                mCurrent[0] += gyroscopeValues[1];
                mCurrent[1] += gyroscopeValues[0];
                if (Math.abs(mCurrent[0]) > mSensitivity[0]){
                    mCurrent[0] = (mCurrent[0]/Math.abs(mCurrent[0])) * mSensitivity[0];
                }
                if (Math.abs(mCurrent[1]) > mSensitivity[1]){
                    mCurrent[1] = (mCurrent[1]/Math.abs(mCurrent[1])) * mSensitivity[1];
                }
                mHeadCursorMotionListener.onPositionChanged(new float[]{-1 * mCurrent[0]/mSensitivity[0],mCurrent[1]/mSensitivity[1]});
            }
            return;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
