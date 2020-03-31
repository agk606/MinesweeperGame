package com.hfad.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity /*implements AdapterView.OnItemSelectedListener*/ {

    private Switch vibroSwitch;
    private Switch soundSwitch;
    private int testBombNumber; //count of bombs
    private int testWidth; //count of horizontal blocks
    private int positionSizeArr; //chosen position in the FieldSize array
    private int positionComplArr; //chosen position in the Complexity array
    public static boolean stateVibroSwitch; //state of VibroSwitch
    public static boolean stateSoundSwitch; //state of SoundSwitch
    private SharedPreferences sPref;
    private String APP_PREFERENCES = "settings";
    private String SAVED_FIELD_SIZE = "width";
    private String SAVED_COMPLEXITY = "bombCount";
    private String SAVED_POS_SIZE_ARR = "positionSizeArr";
    private String SAVED_POS_COMPL_ARR = "positionComplArr";
    private String SAVED_STATE_VIBRO_SWITCH = "stateVibroSwitch";
    private String SAVED_STATE_SOUND_SWITCH = "stateSoundSwitch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.settings);

        Spinner sizeSpinner = (Spinner) findViewById(R.id.size_spinner);
        setSpinner(sizeSpinner, R.array.field_size, loadPosSize());
        sizeSpinner.setSelection(loadPosSize());

        Spinner complexSpinner = (Spinner) findViewById(R.id.complexity_spinner);
        setSpinner(complexSpinner, R.array.level_of_complexity, loadPosCompl());
        complexSpinner.setSelection(loadPosCompl());

        vibroSwitch = findViewById(R.id.vibro_switch);
        soundSwitch = findViewById(R.id.sound_switch);

        vibroSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stateVibroSwitch = true;
                } else {
                    stateVibroSwitch = false;
                }
                saveStateVibroSwitch(stateVibroSwitch);
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stateSoundSwitch = true;
                } else {
                    stateSoundSwitch = false;
                }
                saveStateSoundSwitch(stateSoundSwitch);
            }
        });

        vibroSwitch.setChecked(loadStateVibroSwitch());
        soundSwitch.setChecked(loadStateSoundSwitch());

        FloatingActionButton okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveParameters(testWidth, testBombNumber, positionSizeArr, positionComplArr);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setSpinner(Spinner spinner, final int resource, final int pos) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.LTGRAY);
                ((TextView) parent.getChildAt(0)).setTextSize(15);

                if (resource == R.array.field_size) {
                    if (position == 0) {
                        testWidth = 7;
                        positionSizeArr = 0;
                    } else if (position == 1) {
                        testWidth = 10;
                        positionSizeArr = 1;
                    } else if (position == 2) {
                        testWidth = 12;
                        positionSizeArr = 2;
                    } else if (position == 3) {
                        testWidth = 14;
                        positionSizeArr = 3;
                    } else if (position == 4) {
                        testWidth = 16;
                        positionSizeArr = 4;
                    }
                } else if (resource == R.array.level_of_complexity) {
                    if (position == 0) {
                        testBombNumber = (int) (testWidth * testWidth * 0.1);
                        positionComplArr = 0;
                    } else if (position == 1) {
                        testBombNumber = (int) (testWidth * testWidth * 0.2);
                        positionComplArr = 1;
                    } else if (position == 2) {
                        testBombNumber = (int) (testWidth * testWidth * 0.3);
                        positionComplArr = 2;
                    } else if (position == 3) {
                        testBombNumber = (int) (testWidth * testWidth * 0.4);
                        positionComplArr = 3;
                    } else if (position == 4) {
                        testBombNumber = (int) (testWidth * testWidth * 0.5);
                        positionComplArr = 4;
                    }
                }

                switch (positionComplArr) {
                    case 0:
                        if (testBombNumber != (int) (testWidth * testWidth * 0.1))
                            testBombNumber = (int) (testWidth * testWidth * 0.1);
                        break;
                    case 1:
                        if (testBombNumber != (int) (testWidth * testWidth * 0.2))
                            testBombNumber = (int) (testWidth * testWidth * 0.2);
                        break;
                    case 2:
                        if (testBombNumber != (int) (testWidth * testWidth * 0.3))
                            testBombNumber = (int) (testWidth * testWidth * 0.3);
                        break;
                    case 3:
                        if (testBombNumber != (int) (testWidth * testWidth * 0.4))
                            testBombNumber = (int) (testWidth * testWidth * 0.4);
                        break;
                    case 4:
                        if (testBombNumber != (int) (testWidth * testWidth * 0.5))
                            testBombNumber = (int) (testWidth * testWidth * 0.5);
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveParameters(int fieldSize, int complexity, int positionSizeArr, int positionComplArr) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(SAVED_FIELD_SIZE, fieldSize);
        ed.putInt(SAVED_COMPLEXITY, complexity);
        ed.putInt(SAVED_POS_SIZE_ARR, positionSizeArr);
        ed.putInt(SAVED_POS_COMPL_ARR, positionComplArr);
        ed.commit();
    }

    private void saveStateVibroSwitch(boolean stateVibroSwitch) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(SAVED_STATE_VIBRO_SWITCH, stateVibroSwitch);
        ed.commit();
    }

    private void saveStateSoundSwitch(boolean stateSoundSwitch) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(SAVED_STATE_SOUND_SWITCH, stateSoundSwitch);
        ed.commit();
    }

    private int loadFieldSize() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int savedFieldSize = sPref.getInt(SAVED_FIELD_SIZE, 5);
        return savedFieldSize;
    }

    private int loadComplexity() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int savedComplexity = sPref.getInt(SAVED_COMPLEXITY, 10);
        return savedComplexity;
    }

    private int loadPosSize() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int savedPosSize = sPref.getInt(SAVED_POS_SIZE_ARR, 0);
        return savedPosSize;
    }

    private int loadPosCompl() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int savedPosCompl = sPref.getInt(SAVED_POS_COMPL_ARR, 0);
        return savedPosCompl;
    }

    private boolean loadStateVibroSwitch() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        boolean savedStateVibroSwitch = sPref.getBoolean(SAVED_STATE_VIBRO_SWITCH, false);
        return savedStateVibroSwitch;
    }

    private boolean loadStateSoundSwitch() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        boolean savedStateSoundSwitch = sPref.getBoolean(SAVED_STATE_SOUND_SWITCH, false);
        return savedStateSoundSwitch;
    }

}
