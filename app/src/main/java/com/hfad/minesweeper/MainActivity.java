package com.hfad.minesweeper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.minesweeper.views.GridAdapter;

import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    public static int numberHorizontalPixels;
    public static int numberVerticalPixels;
    public static int numberVerticalCells;
    public static int cellSizeX;
    private static int seconds = 0;
    private static boolean running = false;
    private static boolean wasRunning;
    private Context context;
    public ConstraintLayout mainLayout;
    public GridView minesweeperGridView;
    public TextView bombCount;
    public ImageView smileButton;
    public ImageView settingsButton;
    private SharedPreferences sPref;
    private String APP_PREFERENCES = "settings";
    private String SAVED_FIELD_SIZE = "width";
    private String SAVED_COMPLEXITY = "bombCount";
    private String SAVED_STATE_VIBRO_SWITCH = "stateVibroSwitch";
    private String SAVED_STATE_SOUND_SWITCH = "stateSoundSwitch";
    public static int width;
    public static int bombNumber;
    public static boolean defaultVibroSwitch;
    public static boolean defaultSoundSwitch;
    public static SoundPool mySoundPool;
    public static int shortClickSound;
    public static int longClickSound;
    public static int winSound;
    public static int losingSound;
    public static int idShortClickSound;
    public static int idLongClickSound;
    public static int idWinSound;
    public static int idLosingSound;
    public static Toast winToast;
    public static Toast losingToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            setRunning(savedInstanceState.getBoolean("running"));
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        calcDisplaySize();
        prepGameEngine();
        GameEngine.getInstance().createGrid(this);
        setContentView(R.layout.activity_main);
        runTimer();
        prepReferences();

        minesweeperGridView.setNumColumns(width);
        minesweeperGridView.setAdapter(new GridAdapter());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

        mySoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });
        shortClickSound = mySoundPool.load(this, R.raw.short_click, 1);
        longClickSound = mySoundPool.load(this, R.raw.long_click, 1);
        winSound = mySoundPool.load(this, R.raw.win_melody,1);
        losingSound = mySoundPool.load(this, R.raw.lose_melody, 1);
        sounds();

        makeWinToast();
        makeLosingToast();
    }

    private void calcDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
    }

    private void prepGameEngine() {
        width = loadFieldSize();
        cellSizeX = numberHorizontalPixels / width;
        numberVerticalCells = (numberVerticalPixels - 170) / cellSizeX;
        bombNumber = loadComplexity();
        defaultVibroSwitch = loadStateVibroSwitch();
        defaultSoundSwitch = loadStateSoundSwitch();
    }

    private void prepReferences() {
        mainLayout = findViewById(R.id.main_layout);
        minesweeperGridView = findViewById(R.id.minesweeperGridView);
        bombCount = findViewById(R.id.bombCount);
        smileButton = findViewById(R.id.smile);
        settingsButton = findViewById(R.id.settings);

        smileButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);

        GameEngine.getRef(bombCount, smileButton);

        bombCount.setText(String.format(Locale.getDefault(), "%03d", GameEngine.flagNumber));
    }

    public static void onClickStart() {
        running = true;
    }

    public static boolean getRunning() {
        return running;
    }

    public static void setRunning(boolean run) {
        running = run;
    }

    private void runTimer() {
        final TextView timeView = findViewById(R.id.timer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format("%02d:%02d", minutes, secs);
                timeView.setText(time);
                if (seconds <= 3600) {
                    if (running) {
                        seconds++;
                    }
                } else {
                    running = false;
                    Toast.makeText(context, "Time is over", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smile:
                newGame();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, Settings.class);
                startActivityForResult(intent, 121);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 121 && resultCode == Settings.RESULT_OK) {
            GameEngine.finishEngine();
            newGame();
        } else return;
    }

    protected void newGame() {
        seconds = 0;
        setRunning(false);
        prepGameEngine();
        GameEngine.getInstance().createGrid(this);
        GameEngine.flagNumber = GameEngine.bombNumber;
        GameEngine.gameOver = false;
        bombCount.setText(String.format(Locale.getDefault(), "%03d", GameEngine.flagNumber));
        smileButton.setImageResource(R.mipmap.joy_smile);

        mainLayout.removeView(minesweeperGridView);
        GridView gv = new GridView(this);
        minesweeperGridView.setNumColumns(width);
        minesweeperGridView.setAdapter(new GridAdapter());
        mainLayout.addView(minesweeperGridView);

        soundsOff();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wasRunning = running;
        setRunning(false);
        soundsOff();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!GameEngine.gameOver){
            if(wasRunning) setRunning(true);
        } else setRunning(false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    private void saveParameters(int fieldSize, int complexity) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(SAVED_FIELD_SIZE, fieldSize);
        ed.putInt(SAVED_COMPLEXITY, complexity);
        ed.commit();
    }

    private int loadFieldSize() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int savedFieldSize = sPref.getInt(SAVED_FIELD_SIZE, 7);
        return savedFieldSize;
    }

    private int loadComplexity() {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int savedComplexity = sPref.getInt(SAVED_COMPLEXITY, (int) (width * width * 0.1));
        return savedComplexity;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mySoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        mySoundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
    }

    private void sounds() { //activate sound effects
        if(!defaultSoundSwitch) { //if SoundSwitch is disabled then set soundpool on stop
            soundsOff();
        } else {
            return;
        }
    }

    private void soundsOff() {
        mySoundPool.stop(idLosingSound);
        mySoundPool.stop(idWinSound);
        mySoundPool.stop(idLongClickSound);
        mySoundPool.stop(idShortClickSound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySoundPool.release();
        mySoundPool = null;
    }

    private void makeWinToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.win_toast_layout, (ViewGroup) findViewById(R.id.win_toast_layout));
        winToast = new Toast(getApplicationContext());
        winToast.setView(layout);
        winToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        winToast.setDuration(Toast.LENGTH_SHORT);
    }

    private void makeLosingToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.losing_toast_layout, (ViewGroup) findViewById(R.id.losing_toast_layout));
        losingToast = new Toast(getApplicationContext());
        losingToast.setView(layout);
        losingToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        losingToast.setDuration(Toast.LENGTH_SHORT);
    }
}