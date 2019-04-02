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

package sussex.strc.wear.test;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import sussex.strc.wear.test.card.CardAdapter;
import sussex.strc.wear.test.test.gesture.ContinuousTestActivity;
import sussex.strc.wear.test.test.gesture.DiscreteTestActivity;
import sussex.strc.wear.test.test.speech.SpeechTestActivity;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    SharedPreferences mSharedPreferences;

    static final int NEW_TEST = 0;
    static final int SETTINGS = 1;
    static final int SENSOR_DEMO = 2;

    private final Handler mHandler = new Handler();

    private CardScrollAdapter mAdapter;
    private CardScrollView mCardScroller;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setCardScrollerListener();
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        cards.add(NEW_TEST, new CardBuilder(context, CardBuilder.Layout.CAPTION)
                .addImage(R.drawable.background_us)
                .setText(R.string.text_card_new_procedure)
                .setFootnote(R.string.footnote_card));
        cards.add(SETTINGS, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText(R.string.text_card_settings)
                .showStackIndicator(true)
                .setFootnote(R.string.footnote_card));
//        cards.add(SENSOR_DEMO, new CardBuilder(context, CardBuilder.Layout.MENU)
//                .setText(R.string.sensors_demo)
//                .setFootnote(R.string.footnote_card));
        return cards;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int soundEffect = Sounds.TAP;
                switch (position) {
                    case NEW_TEST:
                        int input = mSharedPreferences.getInt("inputModality",0);
                        switch (input) {
                            case 0:
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startSpeechTest();
                                    }
                                });
                                break;
                            case 1:
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startDiscreteTest();
                                    }
                                });
                                break;
                            case 2:
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startContinuousTest();
                                    }
                                });
                                break;
                        }
                        break;

                    case SETTINGS:
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                startSettings();
                            }
                        });
                        break;

//                    case SENSOR_DEMO:
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                startSensorDemo();
//                            }
//                        });
//                        break;

                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Don't show anything");
                }

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }

    private void startContinuousTest() {
        startActivity(new Intent(this, ContinuousTestActivity.class));
    }


    private void startDiscreteTest() {
        startActivity(new Intent(this, DiscreteTestActivity.class));
    }

    private void startSpeechTest() {
        startActivity(new Intent(this, SpeechTestActivity.class));
    }

    private void startSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

//    private void startSensorDemo() {
////        startActivity(new Intent(this, SensorActivity.class));
//        startActivity(new Intent(this, DemoDriver.class));
//    }



}
