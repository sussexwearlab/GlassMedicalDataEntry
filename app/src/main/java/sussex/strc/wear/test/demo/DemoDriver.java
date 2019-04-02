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
import sussex.strc.wear.test.motion.HeadCursorMotionHandler;
import sussex.strc.wear.test.motion.HeadCursorMotionListener;

/**
 * Created by fjordonez on 17/12/15.
 */
public class DemoDriver extends Activity implements HeadCursorMotionListener {

    private HeadCursorMotionHandler mHeadCursorMotionHandler;

    private TextView mAccX;
    private TextView mAccY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mHeadCursorMotionHandler = new HeadCursorMotionHandler(this);
        mHeadCursorMotionHandler.setCursorMotionListener(this);

        setContentView(R.layout.sensors_activity_layout);
        mAccX = (TextView) findViewById(R.id.acc_X_value);
        mAccY= (TextView) findViewById(R.id.acc_Y_value);

    }

    @Override
    public void onPositionChanged(float[] values) {
        mAccX.setText(Integer.toString((int) (values[0]*100)));
        mAccY.setText(Integer.toString((int) (values[1]*100)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeadCursorMotionHandler.start(new float[] {20,10});
    }

    @Override
    protected void onPause() {
        mHeadCursorMotionHandler.stop();
        super.onPause();
    }
}
