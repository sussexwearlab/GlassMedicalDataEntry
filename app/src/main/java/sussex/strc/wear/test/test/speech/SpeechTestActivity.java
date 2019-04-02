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

package sussex.strc.wear.test.test.speech;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import sussex.strc.wear.test.R;
import sussex.strc.wear.test.grid.CellData;

/**
 * Created by fjordonez on 08/12/15.
 */
public class SpeechTestActivity extends Activity {

    private static final String TAG = SpeechTestActivity.class.getSimpleName();

    private boolean mVoiceMenuEnabled = true;
    private int mNumberColumns;
    private int mNumberRows;
    List<String> phonetic_row_speech_grid;
    List<String> phonetic_column_speech_grid;
    List<String> letters_row_speech_grid;
    List<String> letters_column_speech_grid;


    private File logFile = null;
    ArrayList<CellData<Integer,Integer>> mSymbols;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (isExternalStorageWritable()) {
            File path = getExternalFilesDir(null);
            if(!path.exists()){ path.mkdirs(); }
            DateFormat df = new SimpleDateFormat("_ddMMyy_HHmmss");
            String fileName = TAG + df.format(Calendar.getInstance().getTime()) + ".csv";
            logFile = new File(getExternalFilesDir(null), fileName);
        }

        mSymbols = new ArrayList<CellData<Integer,Integer>> ();

        setContentView(R.layout.grid_layout);
        setupLayout();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void appendLog(String text)
    {
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void setupLayout() {

        RelativeLayout mRelativeLayout = (RelativeLayout)findViewById(R.id.body_layout);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ((FrameLayout)findViewById(R.id.layout)).getLayoutParams();
        int idx_d = PreferenceManager.getDefaultSharedPreferences(this).getInt("gridDensity", 0);
        int cell_size = (int) getResources().getDimension(R.dimen.grid_cell_low);

        switch (idx_d){

            case 0:
                mNumberColumns = getResources().getInteger(R.integer.grid_columns_low);
                mNumberRows = getResources().getInteger(R.integer.grid_rows_low);
                cell_size = (int) getResources().getDimension(R.dimen.grid_cell_low);
                mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background_speech_low));
//                appendLog(TAG + " " + new Date().getTime() + " Creating low density grid");
                appendLog(new Date().getTime() + "," + "START,low,");
                break;

            case 1:
                mNumberColumns = getResources().getInteger(R.integer.grid_columns_medium);
                mNumberRows = getResources().getInteger(R.integer.grid_rows_medium);
                cell_size = (int) getResources().getDimension(R.dimen.grid_cell_medium);
                mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background_speech_medium));
//                appendLog(TAG + " " + new Date().getTime() + " Creating normal density grid");
                appendLog(new Date().getTime() + "," + "START,medium,");
                break;

            case 2:
                mNumberColumns = getResources().getInteger(R.integer.grid_columns_high);
                mNumberRows = getResources().getInteger(R.integer.grid_rows_high);
                cell_size = (int) getResources().getDimension(R.dimen.grid_cell_high);
                mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background_speech_high));
//                appendLog(TAG + " " + new Date().getTime() + " Creating high density grid");
                appendLog(new Date().getTime() + "," + "START,high,");
                break;
        }
        mRelativeLayout.requestLayout();
        setupGridLayout(cell_size);
    }

    private void setupGridLayout(int cell_size) {
        GridLayout gl = (GridLayout)findViewById(R.id.grid_layout);
        gl.removeAllViews();
        gl.setRowCount(mNumberRows);
        gl.setColumnCount(mNumberColumns);

        List<String> indexes_alphabet = Arrays.asList(getResources().getStringArray(R.array.indexes_alphabet));
        letters_row_speech_grid = indexes_alphabet.subList(0, mNumberRows - 1);
        letters_column_speech_grid = indexes_alphabet.subList(indexes_alphabet.size() - mNumberColumns + 1, indexes_alphabet.size());

        List<String> phonetic_alphabet = Arrays.asList(getResources().getStringArray(R.array.phonetic_alphabet));
        phonetic_row_speech_grid = phonetic_alphabet.subList(0, mNumberRows - 1);
        phonetic_column_speech_grid = phonetic_alphabet.subList(indexes_alphabet.size() - mNumberColumns + 1, indexes_alphabet.size());

        for (int i = 0, c = 0, r = 0; i < mNumberColumns * mNumberRows; i++, c++) {
            if (c == mNumberColumns) {
                c = 0;
                r++;
            }
            TextView oTextView = new TextView(this);
            oTextView.setId(i);
            if ((r==0) && (c>0)) { oTextView.setText(letters_column_speech_grid.get(c-1)); }
            if ((c==0) && (r>0)) { oTextView.setText(letters_row_speech_grid.get(r-1)); }
//            oTextView.setText(Integer.toString(i));
            oTextView.setTextColor(Color.BLACK);
            oTextView.setHeight(cell_size);
            oTextView.setWidth(cell_size);
            oTextView.setGravity(Gravity.CENTER);
            oTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getResources().getDimension(R.dimen.grid_cell_text_size));
            if (PreferenceManager.getDefaultSharedPreferences(this).getInt("gridDensity", 0) == 2){
                oTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getResources().getDimension(R.dimen.grid_cell_text_size_high));
            }
            gl.addView(oTextView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.test_menu, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            // Dynamically decides between enabling/disabling voice menu.
            return mVoiceMenuEnabled;
        }
        // Good practice to pass through, for options menu.
        return super.onPreparePanel(featureId, view, menu);
    }

    private void updateGridLayout_Symbol(int index, int symbol) {
        TextView textView = (TextView) findViewById(index);
        if (symbol == 0) {
            textView.setText("");
        }else {
            textView.setText(Integer.toString(symbol));
        }
        return;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.new_cell_item:
//                    appendLog(new Date().getTime() + " Command: new cell");
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak coordinates and symbol");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);
                    startActivityForResult(intent, item.getItemId());
                    break;
                case R.id.undo_cell_item:
                    String _log = new Date().getTime() + "," + "REMOV" + "," ;
//                    appendLog(TAG + " " + new Date().getTime() + " Command: undo cell");
                    if (!mSymbols.isEmpty()){
                        CellData<Integer,Integer> c = mSymbols.get(mSymbols.size() - 1);
                        appendLog(_log + c.getIndex().toString() + "," + c.getSymbol().toString());
//                        appendLog(TAG + " " + new Date().getTime() + "   Removing symbol " + c.getSymbol().toString());
                        updateGridLayout_Symbol(c.getIndex().intValue(), 0);
                        mSymbols.remove(c);
                    }else{
                        appendLog(_log + ",");
                    }

                    break;
                case R.id.exit_test_item:
//                    appendLog(TAG + " " + new Date().getTime() + " Command: exit test");
                    finish();
                    break;
                default: return true;  // No change.
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    protected int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

    protected int getIndexCoordinate(String word, boolean isRow){
        int idx_r = PreferenceManager.getDefaultSharedPreferences(this).getInt("coordRecognition", 0);
        switch (idx_r){

            case 0: //perfect match
                if (isRow) {
                    if (phonetic_row_speech_grid.contains(word.toLowerCase())) {
                        return phonetic_row_speech_grid.indexOf(word.toLowerCase()) + 1;
                    } else {
                        return -1;
                    }
                }else{
                    if (phonetic_column_speech_grid.contains(word.toLowerCase())) {
                        return phonetic_column_speech_grid.indexOf(word.toLowerCase()) + 1;
                    } else {
                        return -1;
                    }
                }

            case 1: //first letter
                String firstLetter = word.substring(0, 1).toUpperCase();
                appendLog(firstLetter);
                if (isRow) {
                    if (letters_row_speech_grid.contains(firstLetter)) {
                        return letters_row_speech_grid.indexOf(firstLetter) + 1;
                    } else {
                        return -1;
                    }
                }else{
                    appendLog("is colum");
                    if (letters_column_speech_grid.contains(firstLetter)) {
                        return letters_column_speech_grid.indexOf(firstLetter) + 1;
                    } else {
                        return -1;
                    }
                }

            case 2: //similarity
                List<String> list;
                if (isRow) list = phonetic_row_speech_grid;
                else list = phonetic_column_speech_grid;
                ListIterator iterator = list.listIterator();
                int min = computeLevenshteinDistance(word, (String) iterator.next());
                int min_idx = 0;
                while (iterator.hasNext()) {
                    int idx = iterator.nextIndex();
                    int dist = computeLevenshteinDistance(word, (String) iterator.next());
                    if (dist < min){
                        min = dist;
                        min_idx = idx;
                    }
                }
                return min_idx + 1;

        }
        return -1;
    }

    protected int getSymbolId(String word){
        String[] symbols = getResources().getStringArray(R.array.symbols_array);
        String[] symbols_numbers = getResources().getStringArray(R.array.symbols_numbers_array);
        //Common speech mistakes control
        if (word.equals("won") || word.equals("ron")){ word = "one";}
        if (word.equals("too") || word.equals("to") || word.equals("II")){ word = "two";}
        if (word.equals("free")){ word = "three";}
        if (word.equals("for")){ word = "four";}
        if (word.equals("fife")){ word = "five";}
        if ((Arrays.asList(symbols).contains(word) || Arrays.asList(symbols_numbers).contains(word))){ //if the symbol is part of the list
            int idx_symbol = -1;
            if (Arrays.asList(symbols).contains(word)) {
                idx_symbol = Arrays.asList(symbols).indexOf(word);
            } else {
                idx_symbol = Arrays.asList(symbols_numbers).indexOf(word);
            }
            return idx_symbol;
        }else{
            return -1;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == R.id.new_cell_item && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            String _log = new Date().getTime() + "," + "SRECO" + "," + spokenText;
            String[] words = spokenText.split(" ");
            appendLog(spokenText);
            int sound = Sounds.ERROR;
            if (words.length == 3) {
                int idx_row = getIndexCoordinate(words[0], true);
                int idx_column = getIndexCoordinate(words[1], false);
                int idx_symbol = getSymbolId(words[2]);
                if ((idx_row != -1) && (idx_column != -1) && (idx_symbol != -1)){
                    appendLog(_log + "," + '1');
                    sound = Sounds.SUCCESS;
                    int cellIndex = (mNumberColumns * idx_row) + idx_column;
                    appendLog(new Date().getTime() + "," + "INPUT" + "," + cellIndex + "," + (idx_symbol + 1));
                    mSymbols.add(new CellData<Integer, Integer>(cellIndex, idx_symbol + 1));
                    updateGridLayout_Symbol(cellIndex, idx_symbol + 1);
                }
//                String[] symbols = getResources().getStringArray(R.array.symbols_array);
//                String[] symbols_numbers = getResources().getStringArray(R.array.symbols_numbers_array);
//                if ((Arrays.asList(symbols).contains(words[2]) || Arrays.asList(symbols_numbers).contains(words[2]))
//                        && (phonetic_row_speech_grid.contains(words[0].toLowerCase()))
//                        && (phonetic_column_speech_grid.contains(words[1].toLowerCase()))) {
//                    int idx_symbol = -1;
//                    if (Arrays.asList(symbols).contains(words[2])) {
//                        idx_symbol = Arrays.asList(symbols).indexOf(words[2]);
//                    } else {
//                        idx_symbol = Arrays.asList(symbols_numbers).indexOf(words[2]);
//                    }
//                    appendLog(_log + "," + '1');
//                    int idx_row = phonetic_row_speech_grid.indexOf(words[0].toLowerCase()) + 1;
//                    int idx_column = phonetic_column_speech_grid.indexOf(words[1].toLowerCase()) + 1;
//                    int cellIndex = (mNumberColumns * idx_row) + idx_column;
//                    sound = Sounds.SUCCESS;
//                    appendLog(new Date().getTime() + "," + "INPUT" + "," + cellIndex + "," + (idx_symbol + 1));
//                    mSymbols.add(new CellData<Integer, Integer>(cellIndex, idx_symbol + 1));
//                    updateGridLayout_Symbol(cellIndex, idx_symbol + 1);
//                }
            }
            if (sound == Sounds.ERROR) { appendLog(_log + "," + '0'); }
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(sound);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
