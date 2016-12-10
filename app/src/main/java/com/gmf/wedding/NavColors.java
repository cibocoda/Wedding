package com.gmf.wedding;

import android.content.res.ColorStateList;
import android.graphics.Color;

/**
 * Created by zop on 2016/12/9.
 */

public class NavColors {
    // FOR NAVIGATION VIEW ITEM TEXT COLOR
    private int[][] state = new int[][] {
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] {android.R.attr.state_checked}, // checked
            new int[] {android.R.attr.state_pressed}  // pressed

    };

    private int[] color = new int[] {
            Color.BLACK,
            Color.BLACK,
            Color.BLACK,
            Color.RED,
            Color.RED
    };

    ColorStateList csl = new ColorStateList(state, color);


    // FOR NAVIGATION VIEW ITEM ICON COLOR
    private int[][] states = new int[][] {
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] {android.R.attr.state_checked}, // checked
            new int[] {android.R.attr.state_pressed}  // pressed

    };

    private int[] colors = new int[] {
            Color.BLACK,
            Color.BLACK,
            Color.BLACK,
            Color.RED,
            Color.RED
    };

    ColorStateList csl2 = new ColorStateList(states, colors);
}
