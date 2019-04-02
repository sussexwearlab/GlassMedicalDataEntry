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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

import sussex.strc.wear.test.card.CardAdapter;

/**
 * Created by fjordonez on 17/12/15.
 */
public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    SharedPreferences mSharedPreferences;

    static final int INPUT_SETTINGS = 0;
    static final int SENSITIVITY_SETTINGS = 1;
    static final int GRID_DENSITY_SETTINGS = 2;
    static final int COORD_RECOG_SETTINGS = 3;

    private CardScrollAdapter mAdapter;
    private CardScrollView mCardScroller;

    private enum InputModality {
        SPEECH(R.string.text_card_input_speech),
        DISCRETE(R.string.text_card_input_discrete),
        CONTINUOUS(R.string.text_card_input_continuous);
        final int textId;
        InputModality(int textId) {
            this.textId = textId;
        }
    }

    private enum Sensitivity {
        VERY_SLOW(R.string.text_card_sensitivity_speed_1),
        SLOW(R.string.text_card_sensitivity_speed_2),
        NORMAL(R.string.text_card_sensitivity_speed_3),
        FAST(R.string.text_card_sensitivity_speed_4),
        VERY_FAST(R.string.text_card_sensitivity_speed_5);
        final int textId;
        Sensitivity(int textId) {
            this.textId = textId;
        }
    }

    private enum GridDensity {
        LOW(R.string.text_card_grid_size_low),
        MEDIUM(R.string.text_card_grid_size_medium),
        HIGH(R.string.text_card_grid_size_high);
        final int textId;
        GridDensity(int textId) {
            this.textId = textId;
        }
    }

    private enum CoordenatesRecognition {
        MATCH(R.string.text_card_coor_recog_match),
        LETTER(R.string.text_card_coor_recog_letter),
        SIMILARITY(R.string.text_card_coor_recog_similarity);
        final int textId;
        CoordenatesRecognition(int textId) {
            this.textId = textId;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mAdapter = new CardAdapter(createCards(this));
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();
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

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();

        InputModality modality = InputModality.values()[mSharedPreferences.getInt("inputModality", 0)];
        cards.add(INPUT_SETTINGS, new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.text_card_input_settings) + " " + getString(modality.textId))
                .setIcon(R.drawable.ic_input_150));

        Sensitivity sensitivity = Sensitivity.values()[mSharedPreferences.getInt("sensitivity",2)];
        cards.add(SENSITIVITY_SETTINGS, new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.text_card_sensitivity_settings) + " " + getString(sensitivity.textId))
                .setIcon(R.drawable.ic_sensitivity_150));

        GridDensity density = GridDensity.values()[mSharedPreferences.getInt("gridDensity",0)];
        cards.add(GRID_DENSITY_SETTINGS, new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.text_card_grid_size_settings) + " " + getString(density.textId))
                .setIcon(R.drawable.ic_density_150));

        CoordenatesRecognition coord_recog = CoordenatesRecognition.values()[mSharedPreferences.getInt("coordRecognition",0)];
        cards.add(COORD_RECOG_SETTINGS, new CardBuilder(context, CardBuilder.Layout.COLUMNS)
                .setText(getString(R.string.text_card_coor_recog_settings) + " " + getString(coord_recog.textId))
                .setIcon(R.drawable.ic_coordrecog_150));

        return cards;
    }

    private void setCardScrollerListener() {
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                int soundEffect = Sounds.TAP;
                switch (position) {

                    case INPUT_SETTINGS:
                        int nb_modalities = InputModality.values().length;
                        int input = mSharedPreferences.getInt("inputModality",0);
                        if (input == (nb_modalities - 1)) {
                            input = 0;
                        } else {
                            input = input + 1;
                        }
                        InputModality modality = InputModality.values()[input];
                        CardBuilder cardInput = (CardBuilder) mAdapter.getItem(INPUT_SETTINGS);
                        cardInput.setText(getString(R.string.text_card_input_settings) + " " + getString(modality.textId));
                        mCardScroller.getAdapter().notifyDataSetChanged();
                        editor.putInt("inputModality", input);
                        editor.commit();
                        break;

                    case SENSITIVITY_SETTINGS:
                        int nb_sensitivities = Sensitivity.values().length;
                        int s = mSharedPreferences.getInt("sensitivity",2);
                        if (s == (nb_sensitivities - 1)) {
                            s = 0;
                        } else {
                            s = s + 1;
                        }
                        Sensitivity sensitivity = Sensitivity.values()[s];
                        CardBuilder cardSensitivity = (CardBuilder) mAdapter.getItem(SENSITIVITY_SETTINGS);
                        cardSensitivity.setText(getString(R.string.text_card_sensitivity_settings) + " " + getString(sensitivity.textId));
                        mCardScroller.getAdapter().notifyDataSetChanged();
                        editor.putInt("sensitivity", s);
                        editor.commit();
                        break;

                    case GRID_DENSITY_SETTINGS:
                        int nb_densities = GridDensity.values().length;
                        int d = mSharedPreferences.getInt("gridDensity",0);
                        if (d == (nb_densities - 1)) {
                            d = 0;
                        } else {
                            d = d + 1;
                        }
                        GridDensity density = GridDensity.values()[d];
                        CardBuilder cardDensity = (CardBuilder) mAdapter.getItem(GRID_DENSITY_SETTINGS);
                        cardDensity.setText(getString(R.string.text_card_grid_size_settings) + " " + getString(density.textId));
                        mCardScroller.getAdapter().notifyDataSetChanged();
                        editor.putInt("gridDensity", d);
                        editor.commit();
                        break;

                    case COORD_RECOG_SETTINGS:
                        int nb_recogs = CoordenatesRecognition.values().length;
                        int r = mSharedPreferences.getInt("coordRecognition",0);
                        if (r == (nb_recogs - 1)) {
                            r = 0;
                        } else {
                            r = r + 1;
                        }
                        CoordenatesRecognition coord_recog = CoordenatesRecognition.values()[r];
                        CardBuilder cardCoordRecog = (CardBuilder) mAdapter.getItem(COORD_RECOG_SETTINGS);
                        cardCoordRecog.setText(getString(R.string.text_card_coor_recog_settings) + " " + getString(coord_recog.textId));
                        mCardScroller.getAdapter().notifyDataSetChanged();
                        editor.putInt("coordRecognition", r);
                        editor.commit();
                        break;

                    default:
                        soundEffect = Sounds.ERROR;
                }

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }

}
