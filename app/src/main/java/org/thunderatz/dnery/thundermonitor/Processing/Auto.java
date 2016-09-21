package org.thunderatz.dnery.thundermonitor.Processing;

import org.thunderatz.dnery.thundermonitor.Constants;
import org.thunderatz.dnery.thundermonitor.MainActivity;

import processing.core.PApplet;

/**
 * Created by dnery on 18/09/2016.
 */
public class Auto extends PApplet {
    private static final String TAG = "Auto";

    private MainActivity m;

    public Auto() {
        this.m = null;
    }

    public Auto(MainActivity m) {
        this.m = m;
    }

    public void settings() {
        size(displayWidth, displayHeight);
    }

    public void setup() {
        if (m == null)
            throw new RuntimeException("MainActivity is null");
    }

    public void draw() {
        background(255);

        textSize(height / 20f);
        fill(Constants.COLOR_BLACK);
        textAlign(PApplet.CENTER, PApplet.CENTER);
        text("Auto " + m.MAC, width/2, height/2);
    }
}
