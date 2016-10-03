package org.thunderatz.dnery.moainitor.processing;

import org.thunderatz.dnery.moainitor.Constants;
import org.thunderatz.dnery.moainitor.MainActivity;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

/**
 * Auto.java
 * Processing fragment for Moai's strategy selection
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robotica
 * 02/10/2016
 */

public class Auto extends PApplet {
    private static final String TAG = "Auto";

    private MainActivity m;

    private StopButton stopButton;
    private PlayButton playButton;
    private PFont font;

    int i = 5;

    public Auto() {}

    public void settings() {
        size(displayWidth, displayHeight, P2D);
    }

    public void setup() {
        this.m = (MainActivity) getActivity();
        if (m == null)
            throw new RuntimeException(TAG + ": MainActivity is null");

        font = createFont("Roboto-Regular.ttf", 120);
        stopButton = new StopButton(0.02f * width, 0.83f * height, 0.15f * height, 0.15f * height);
        playButton = new PlayButton(width - 0.17f * height, 0.83f * height, 0.15f * height, 0.15f * height);
    }

    public void draw() {
        background(255);

        playButton.show();
        stopButton.show();

        fill(Constants.COLOR_BLACK);
        textFont(font);
        textSize(0.15f * height);
        textAlign(PApplet.CENTER, PApplet.BOTTOM);
        text(i, width/2, 0.98f * height);
        delay(1000);
        i = (i + 1) % 5;
    }

    private class StopButton {
        float xE, xD, yC, yB, w, h, r;
        boolean overBtn;
        PShape btn;

        public StopButton(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            this.r = 0.5f * w;

            overBtn = false;

            btn = createShape(ELLIPSE, xE + w/2, yC + h/2, w, w);
            btn.setStrokeWeight(10);
            btn.setStroke(Constants.COLOR_DIVIDER);
            btn.setFill(0xFFF44336);
        }

        public void show() {
            if (overBtn)
                btn.setFill(0xFFE57373);
            else
                btn.setFill(0xFFF44336);
            shape(btn);
            textFont(font);
            textSize(0.2f * h);
            textAlign(PApplet.CENTER, PApplet.CENTER);
            fill(Constants.COLOR_BLACK);
            text("STOP", xE + w/2, yC + h/2);
        }

        public void mousePressed() {
            float x = mouseX - xE;
            float y = yB - mouseY;

            overBtn = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= r);
            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        Constants.PACKET_SIZE_STOP,
                        Constants.CMD_STOP,
                        Constants.PACKET_TAIL
                });
            }
        }

        public void mouseReleased() {
            overBtn = false;
        }
    }

    private class PlayButton {
        float xE, xD, yC, yB, w, h;
        boolean overBtn;
        PShape btn;

        public PlayButton(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            overBtn = false;

            btn = createShape();
            btn.setStrokeWeight(10);
            btn.setStroke(Constants.COLOR_DIVIDER);
            btn.setFill(0xFF4CAF50);
            btn.beginShape();
            btn.vertex(xE, yC);
            btn.vertex(xD, yC + h/2);
            btn.vertex(xE, yB);
            btn.endShape(CLOSE);
        }

        public void show() {
            if (overBtn)
                btn.setFill(0xFF81C784);
            else
                btn.setFill(0xFF4CAF50);
            shape(btn);
            fill(Constants.COLOR_BLACK);
            textFont(font);
            textSize(0.2f * h);
            textAlign(PApplet.LEFT, PApplet.CENTER);
            text("START", xE + 0.1f * w, yC + h/2);
        }

        public void mousePressed() {
            float x = mouseX - xE;
            float y = yB - mouseY;

            overBtn = (mouseX >= xE && mouseX <= xD && x/2 < y && (y/(yB-yC) + x/(xD+w-xE) < 1));

            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        Constants.PACKET_SIZE_START_AUTO,
                        Constants.CMD_START_AUTO,
                        Constants.PACKET_TAIL
                });
            }
        }

        public void mouseReleased() {
            overBtn = false;
        }
    }

    public void mouseReleased() {
        stopButton.mouseReleased();
        playButton.mouseReleased();
    }

    public void mousePressed() {
        stopButton.mousePressed();
        playButton.mousePressed();
    }
}
