package com.hfad.minesweeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.hfad.minesweeper.util.Generator;
import com.hfad.minesweeper.views.Cell;
import java.util.Locale;

public class GameEngine {
    private static GameEngine instance;
    public static int bombNumber;
    public static int width;
    public static int height;
    public static int flagNumber;
    public static TextView tvFlagNumber;
    public static ImageView iButton;
    public static boolean gameOver;
    private Context context;

    private static Cell[][] MinesweeperGrid;
    private static int[][] GeneratedGrid;

    public static GameEngine getInstance() {
        if(instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine(){
        width = MainActivity.width;
        height = MainActivity.numberVerticalCells;
        MinesweeperGrid = new Cell[width][height];
        bombNumber = MainActivity.bombNumber;
        flagNumber = bombNumber;
        gameOver = false;
    }

    public static void getRef (TextView textView, ImageView imageView) {
        tvFlagNumber = textView; //get reference for TextView with number of flags
        iButton = imageView; //get reference for ImageView with smile-button
    }

    public void createGrid(Context context){ //create GeneratedGrid with numbers using generate() method from Generator class
        this.context = context;

        GeneratedGrid = Generator.generate(bombNumber, width, height);
        setGrid(context,GeneratedGrid);
    }

    private void setGrid(final Context context, final int[][] grid ){ //copy data from GeneratedGrid to MinesweeperGrid
        for(int x = 0 ; x < width ; x++){ //every position in the MinesweeperGrid is a Cell-object from Cell class
            for(int y = 0 ; y < height ; y++){
                if(MinesweeperGrid[x][y] == null)
                    MinesweeperGrid[x][y] = new Cell(context, x, y);

                MinesweeperGrid[x][y].setValue(grid[x][y]);
                MinesweeperGrid[x][y].invalidate();
            }
        }
    }

    public Cell getCellAt(int position) {
        int x = position % width;
        int y = position / width;

        return MinesweeperGrid[x][y];
    }

    public Cell getCellAt(int x , int y){
        return MinesweeperGrid[x][y];
    }

    public void click(int x, int y) { //handling short click
        if (gameOver) //if the game is over
            return; //then don't anything

        if(!MainActivity.getRunning()) //if the timer was stopped
            MainActivity.onClickStart(); //then start the timer

        if(x >= 0 && y >= 0 && x < width && y < height && !getCellAt(x, y).isClicked() && !getCellAt(x, y).isFlagged()){
            getCellAt(x,y).setClicked();

            if(getCellAt(x, y).getValue() == 0){
                for(int xt = -1 ; xt <= 1 ; xt++){
                    for(int yt = -1 ; yt <= 1 ; yt++){
                        if(xt != yt){
                            click(x + xt , y + yt);
                        }
                    }
                }
            }

            if(getCellAt(x, y).isBomb()){
                GameLost();
                gameOver = true;
            }
        }
        checkEnd();
    }


    public void flagged(int x, int y) {
        if (gameOver) //check of the end of the game
            return;

        if(!MainActivity.getRunning()) //if the timer was stopped
            MainActivity.onClickStart(); //then start the timer

        if (getCellAt(x, y).isClicked()) //check, was the cell clicked
            return; //if it was clicked, then flag will not set
        else {
            boolean isFlagged = getCellAt(x, y).isFlagged(); //check flag-status of cell
            if(flagNumber == 0) { //if all flags were used
                if(isFlagged) {
                    getCellAt(x, y).setFlagged(!isFlagged);
                    flagNumber++;
                    tvFlagNumber.setText(String.format(Locale.getDefault(), "%03d", flagNumber));
                }
                else return;
            } else { //if there are not used flags there
                if(isFlagged) {
                    getCellAt(x, y).setFlagged(!isFlagged);
                    flagNumber++;
                    tvFlagNumber.setText(String.format(Locale.getDefault(), "%03d", flagNumber));
                } else {
                    getCellAt(x, y).setFlagged(!isFlagged);
                    flagNumber--;
                    tvFlagNumber.setText(String.format(Locale.getDefault(), "%03d", flagNumber));
                }
            }
        }

        checkEnd();
        getCellAt(x, y).invalidate();
    }

    private void checkEnd() {
        int hiddenBomb = bombNumber;
        int revealedCells = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (getCellAt(x, y).isFlagged() && getCellAt(x, y).isBomb())
                    hiddenBomb--;
                if (getCellAt(x, y).isRevealed())
                    revealedCells++;
            }
        }
        if (/*hiddenBomb == 0 && */revealedCells == width * height - bombNumber) {
            MainActivity.setRunning(!MainActivity.getRunning());
            gameOver = true;
            //Toast.makeText(context, "CONGRATULATIONS!", Toast.LENGTH_SHORT).show();
            MainActivity.winToast.show();
            if (!MainActivity.defaultSoundSwitch) { //sound when win
                return;
            } else {
                MainActivity.idWinSound = MainActivity.mySoundPool.play(MainActivity.winSound, 1, 1, 1, 0, 1);
            }
        }
    }

    private void GameLost() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                getCellAt(x, y).setRevealed();
            }
        }
        MainActivity.setRunning(!MainActivity.getRunning());
        iButton.setImageResource(R.mipmap.sad_smile);
        //Toast.makeText(context, "You're lose:(", Toast.LENGTH_SHORT).show();
        MainActivity.losingToast.show();
        if (!MainActivity.defaultSoundSwitch) { //sound when lose
            return;
        } else {
            MainActivity.idLosingSound = MainActivity.mySoundPool.play(MainActivity.losingSound, 1, 1, 1, 0, 1);
        }
    }

    public static void finishEngine() {
        instance = null;
    }

}
