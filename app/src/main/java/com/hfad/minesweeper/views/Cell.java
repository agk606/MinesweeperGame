package com.hfad.minesweeper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.hfad.minesweeper.GameEngine;
import com.hfad.minesweeper.MainActivity;
import com.hfad.minesweeper.R;
import com.hfad.minesweeper.Settings;
import static android.content.Context.VIBRATOR_SERVICE;


public class Cell extends BaseCell implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;

    public Cell(Context context, int x, int y) {
        super(context);
        mContext = context;
        setPosition(x, y);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        GameEngine.getInstance().click(getXPos(), getYPos());
        if (!MainActivity.defaultSoundSwitch) { //sound when short click
            return;
        } else {
            MainActivity.idShortClickSound = MainActivity.mySoundPool.play(MainActivity.shortClickSound, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        GameEngine.getInstance().flagged(getXPos(), getYPos());
        vibration(); //vibro when set a flag
        if (!MainActivity.defaultSoundSwitch) { //sound when set a flag
            return true;
        } else {
            MainActivity.idLongClickSound = MainActivity.mySoundPool.play(MainActivity.longClickSound, 1, 1, 0, 0, 1);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawButton(canvas);

        if(!isRevealed() && isFlagged())
            drawFlag(canvas);
        else if(isRevealed() && isBomb() && !isClicked()) {
            drawBomb(canvas);
        }
        else if(isRevealed() && isFlagged() && !isBomb()) {
            drawNoBomb(canvas);
        }
        else if(isRevealed() && !isFlagged() && !isBomb() && !isClicked()) {
            drawNumber(canvas);
        }
        else if(isClicked()){
                if(getValue() == -1)
                    drawBombExploded(canvas);
                else drawNumber(canvas);
        } else drawButton(canvas);
    }

    private void drawButton(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.closed);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    private void drawFlag(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.flagged);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    private void drawBomb(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.bomb);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    private void drawBombExploded(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.bombed);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    private void drawNoBomb(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.nobomb);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    private void drawNumber(Canvas canvas) {
        Drawable drawable = null;
        switch (getValue()) {
            case 0:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.zero);
                break;
            case 1:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num1);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num2);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num3);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num4);
                break;
            case 5:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num5);
                break;
            case 6:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num6);
                break;
            case 7:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num7);
                break;
            case 8:
                drawable = ContextCompat.getDrawable(getContext(), R.mipmap.num8);
                break;
        }
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    private void vibration() { //activate VIBRATOR_SERVICE
        if (!MainActivity.defaultVibroSwitch) { //if VibroSwitch is disabled then don't activate VIBRATOR_SERVICE
            return;
        } else {
            if (Build.VERSION.SDK_INT >= 26) {
                ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,5));
            } else {
                ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
            }
        }
    }
}
