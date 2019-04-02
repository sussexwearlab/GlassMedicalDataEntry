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

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import sussex.strc.wear.test.R;

/**
 * Created by fjordonez on 10/12/15.
 */
public class SensorActivity extends Activity implements SensorsListener {

    private static final String TAG = SensorActivity.class.getSimpleName();

    private TextView mAccX;
    private TextView mAccY;
    private TextView mAccZ;
    private TextView mGyroX;
    private TextView mGyroY;
    private TextView mGyroZ;
    private TextView mMagX;
    private TextView mMagY;
    private TextView mMagZ;

    private SensorsHandler mSensorHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorHandler = new SensorsHandler(this);
        mSensorHandler.setSensorsListener(this);

        setContentView(R.layout.sensors_activity_layout);
        mAccX = (TextView) findViewById(R.id.acc_X_value);
        mAccY= (TextView) findViewById(R.id.acc_Y_value);
        mAccZ = (TextView) findViewById(R.id.acc_Z_value);
        mGyroX = (TextView) findViewById(R.id.gyro_X_value);
        mGyroY= (TextView) findViewById(R.id.gyro_Y_value);
        mGyroZ = (TextView) findViewById(R.id.gyro_Z_value);
        mMagX = (TextView) findViewById(R.id.mag_X_value);
        mMagY= (TextView) findViewById(R.id.mag_Y_value);
        mMagZ = (TextView) findViewById(R.id.mag_Z_value);
    }

    @Override
    public void onAccelerationChanged(float[] values) {
        mAccX.setText(Float.toString(values[0]));
        mAccY.setText(Float.toString(values[1]));
        mAccZ.setText(Float.toString(values[2]));
    }

    @Override
    public void onGyroscopeChanged(float[] values) {
        mGyroX.setText(Float.toString(values[0]));
        mGyroY.setText(Float.toString(values[1]));
        mGyroZ.setText(Float.toString(values[2]));
    }

    @Override
    public void onMagneticChanged(float[] values) {
        mMagX.setText(Float.toString(values[0]));
        mMagY.setText(Float.toString(values[1]));
        mMagZ.setText(Float.toString(values[2]));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorHandler.start();
    }

    @Override
    protected void onPause() {
        mSensorHandler.stop();
        super.onPause();
    }
}
