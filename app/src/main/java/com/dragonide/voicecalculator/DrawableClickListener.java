package com.dragonide.voicecalculator;

/**
 * Created by Ankit on 12/5/2016.
 */

public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}