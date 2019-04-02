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

package sussex.strc.wear.test.test.gesture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.glass.widget.CardScrollView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import sussex.strc.wear.test.R;
import sussex.strc.wear.test.grid.CellData;
import sussex.strc.wear.test.motion.OnHeadGestureListener;
import sussex.strc.wear.test.motion.HeadGestureDetector;

/**
 * Created by fjordonez on 08/12/15.
 */
public class DiscreteTestActivity extends Activity implements OnHeadGestureListener {


    private static final String TAG = DiscreteTestActivity.class.getSimpleName();

    private boolean mVoiceMenuEnabled = true;
    private int mNumberColumns;
    private int mNumberRows;

    private ArrayList<Integer> lastColumIds;
    private ArrayList<Integer> firstColumIds;
    private int mHoverCellIndex;
    private File logFile = null;
    ArrayList<CellData<Integer,Integer>> mSymbols;

    private HeadGestureDetector mHeadGestureDetector;

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

        lastColumIds = new ArrayList<Integer>();
        firstColumIds = new ArrayList<Integer>();
        mSymbols = new ArrayList<CellData<Integer,Integer>> ();

        mHeadGestureDetector = new HeadGestureDetector(this);
        mHeadGestureDetector.setOnHeadGestureListener(this);

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
                mRelativeLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.grid_gestures_height_low);
                mRelativeLayout.getLayoutParams().width = (int) getResources().getDimension(R.dimen.grid_gestures_width_low);
                params.setMargins((int) getResources().getDimension(R.dimen.grid_cell_low)/2,
                        0,
                        (int) getResources().getDimension(R.dimen.grid_cell_low)/2,
                        (int) getResources().getDimension(R.dimen.grid_cell_low));
                mNumberColumns = getResources().getInteger(R.integer.grid_columns_low) - 1;
                mNumberRows = getResources().getInteger(R.integer.grid_rows_low) - 1;
                cell_size = (int) getResources().getDimension(R.dimen.grid_cell_low);
                mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background_gestures_low));
                appendLog(new Date().getTime() + "," + "START,low,");
                break;
            case 1:
                mRelativeLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.grid_gestures_height_medium);
                mRelativeLayout.getLayoutParams().width = (int) getResources().getDimension(R.dimen.grid_gestures_width_medium);
                params.setMargins((int) getResources().getDimension(R.dimen.grid_cell_medium)/2,
                        0,
                        (int) getResources().getDimension(R.dimen.grid_cell_medium)/2,
                        (int) getResources().getDimension(R.dimen.grid_cell_medium));
                mNumberColumns = getResources().getInteger(R.integer.grid_columns_medium) - 1;
                mNumberRows = getResources().getInteger(R.integer.grid_rows_medium) - 1;
                cell_size = (int) getResources().getDimension(R.dimen.grid_cell_medium);
                mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background_gestures_medium));
                appendLog(new Date().getTime() + "," + "START,medium,");
                break;
            case 2:
                mRelativeLayout.getLayoutParams().height = (int) getResources().getDimension(R.dimen.grid_gestures_height_high);
                mRelativeLayout.getLayoutParams().width = (int) getResources().getDimension(R.dimen.grid_gestures_width_high);
                params.setMargins((int) getResources().getDimension(R.dimen.grid_cell_medium)/2,
                        0,
                        (int) getResources().getDimension(R.dimen.grid_cell_medium)/2,
                        (int) getResources().getDimension(R.dimen.grid_cell_high));
                mNumberColumns = getResources().getInteger(R.integer.grid_columns_high) - 1;
                mNumberRows = getResources().getInteger(R.integer.grid_rows_high) - 1;
                cell_size = (int) getResources().getDimension(R.dimen.grid_cell_high);
                mRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background_gestures_high));
                appendLog(new Date().getTime() + "," + "START,high,");
                break;
        }
        mRelativeLayout.requestLayout();
        mHoverCellIndex = (mNumberColumns * mNumberRows) / 2;
        setupGridLayout(cell_size);
    }

    private void setupGridLayout(int cell_size) {
        GridLayout gl = (GridLayout)findViewById(R.id.grid_layout);
        gl.removeAllViews();
        gl.setColumnCount(mNumberColumns);
        gl.setRowCount(mNumberRows);

        for (int i = 0, c = 0, r = 0; i < mNumberColumns * mNumberRows; i++, c++) {
            if (c == mNumberColumns) {
                c = 0;
                r++;
            }
            TextView oTextView = new TextView(this);
            oTextView.setId(i);
//            oTextView.setText(Integer.toString(i));
            oTextView.setTextColor(Color.BLACK);
            oTextView.setHeight(cell_size);
            oTextView.setWidth(cell_size);
            oTextView.setGravity(Gravity.CENTER);
            oTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getResources().getDimension(R.dimen.grid_cell_text_size));
            gl.addView(oTextView);
        }

        int idx_fc = 0;
        int idx_lc = mNumberColumns - 1;
        for  (int i = 0; i < mNumberRows; i++) {
            lastColumIds.add(new Integer(idx_lc));
            firstColumIds.add(new Integer(idx_fc));
            idx_fc += mNumberColumns;
            idx_lc += mNumberColumns;
        }

        setHover(mHoverCellIndex);
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

    private void updateGridLayout_HoverCell(int hoverIndex) {
        if (hoverIndex != mHoverCellIndex){
            appendLog(new Date().getTime() + "," + "MOVES," + Integer.toString(mHoverCellIndex) + "," +  Integer.toString(hoverIndex));
            removeHover(mHoverCellIndex);
            setHover(hoverIndex);
            mHoverCellIndex = hoverIndex;
        }
        return;
    }

    private void setHover(int index){
        TextView textView = (TextView) findViewById(index);
        textView.setBackgroundResource(R.color.hover);
    }

    private void removeHover(int index){
        TextView textView = (TextView) findViewById(index);
        textView.setBackgroundResource(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHeadGestureDetector.start();
    }

    @Override
    protected void onPause() {
        mHeadGestureDetector.stop();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            switch (item.getItemId()) {
                case R.id.new_cell_item:
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak the symbol");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
                    startActivityForResult(intent, item.getItemId());
                    break;
                case R.id.undo_cell_item:
                    String _log = new Date().getTime() + "," + "REMOV" + "," ;
                    if (!mSymbols.isEmpty()){
                        CellData<Integer,Integer> c = mSymbols.get(mSymbols.size() - 1);
                        appendLog(_log + c.getIndex().toString() + "," + c.getSymbol().toString());
                        updateGridLayout_Symbol(c.getIndex().intValue(), 0);
                        mSymbols.remove(c);
                    }else{
                        appendLog(_log + ",");
                    }
                    break;
                case R.id.exit_test_item:
                    finish();
                    break;
                default: return true;  // No change.
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    protected int getSymbolId(String word){
        String[] symbols = getResources().getStringArray(R.array.symbols_array);
        String[] symbols_numbers = getResources().getStringArray(R.array.symbols_numbers_array);
        //Common speech mistakes control
        if (word.equals("won") || word.equals("ron")){ word = "one";}
        if (word.equals("too") || word.equals("to")){ word = "two";}
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
            int sound = Sounds.ERROR;

            int idx = getSymbolId(spokenText);
            if (idx != -1){
                appendLog(_log + "," + '1');
                sound = Sounds.SUCCESS;
                appendLog(new Date().getTime() + "," + "INPUT" + "," + mHoverCellIndex + "," + (idx + 1));
                mSymbols.add(new CellData<Integer, Integer>(mHoverCellIndex, idx + 1));
                updateGridLayout_Symbol(mHoverCellIndex, idx + 1);
            }

            if (sound == Sounds.ERROR){ appendLog(_log + "," + '0'); }
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(sound);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onHey() {
        Log.d(TAG, "onHey");
        //hover cell not at the top row
        if (mHoverCellIndex > (mNumberColumns-1)){
            updateGridLayout_HoverCell(mHoverCellIndex - mNumberColumns);
        }
    }

    @Override
    public void onNod() {
        Log.d(TAG, "onNod");
        int total = mNumberColumns * mNumberRows;
        //hover cell not at the bottom row
        if (mHoverCellIndex < (total-mNumberColumns)){
            updateGridLayout_HoverCell(mHoverCellIndex + mNumberColumns);
        }
    }

    @Override
    public void onShakeToLeft() {
        Log.d(TAG, "onShakeToLeft");
        //hover cell not at the leftmost column
        if (!firstColumIds.contains(mHoverCellIndex)){
            updateGridLayout_HoverCell(mHoverCellIndex - 1);
        }
    }

    @Override
    public void onShakeToRight() {
        Log.d(TAG, "onShakeToRight");
        //hover cell not at the righmost column
        if (!lastColumIds.contains(mHoverCellIndex)){
            updateGridLayout_HoverCell(mHoverCellIndex + 1);
        }
    }

}

