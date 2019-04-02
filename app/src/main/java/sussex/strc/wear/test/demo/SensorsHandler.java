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

package sussex.strc.wear.test.demo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

/**
 * Created by fjordonez on 10/12/15.
 */
public class SensorsHandler implements SensorEventListener {

    private static final String TAG = SensorsHandler.class.getSimpleName();

    private SensorManager mSensorManager;
    private float[] gyroscopeValues = new float[3];
    private float[] magneticValues = new float[3];
    private float[] accelerometerValues = new float[3];

    private static SensorsListener mSensorslistener;

    private static final int[] REQUIRED_SENSORS = {Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE};

    private static final int[] SENSOR_RATES = {SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_NORMAL};

    public SensorsHandler(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void start() {
        for (int i = 0; i < REQUIRED_SENSORS.length; i++) {
            int sensor_type = REQUIRED_SENSORS[i];
            Sensor sensor = null;
            List<Sensor> sensors = mSensorManager.getSensorList(sensor_type);
            if (sensors.size() > 1) {
                // Google Glass has two gyroscopes: "MPL Gyroscope" and "Corrected Gyroscope Sensor". Try the later one.
                sensor = sensors.get(1);
            } else {
                sensor = sensors.get(0);
            }
            Log.d(TAG, "registered:" + sensor.getName());
            mSensorManager.registerListener(this, sensor, SENSOR_RATES[i]);
        }
    }

    public void stop() {
        try {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
            }
        } catch (Exception e) {
        }
    }

    public void setSensorsListener(SensorsListener sensorslistener) {
        this.mSensorslistener = sensorslistener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values.clone();
            mSensorslistener.onMagneticChanged(magneticValues);
            return;
        }

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone();
            mSensorslistener.onAccelerationChanged(accelerometerValues);
            return;
        }

        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            gyroscopeValues = event.values.clone();
            mSensorslistener.onGyroscopeChanged(gyroscopeValues);
            return;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}