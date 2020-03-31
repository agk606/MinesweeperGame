package com.hfad.minesweeper.views;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hfad.minesweeper.GameEngine;

public class GridAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return GameEngine.getInstance().width * GameEngine.getInstance().height;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return GameEngine.getInstance().getCellAt(position);
    }
}
