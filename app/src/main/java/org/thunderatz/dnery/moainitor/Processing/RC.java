package org.thunderatz.dnery.moainitor.processing;

import android.util.Log;

import org.thunderatz.dnery.moainitor.BluetoothService;
import org.thunderatz.dnery.moainitor.Constants;
import org.thunderatz.dnery.moainitor.MainActivity;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;

/**
 * RC.java
 * Processing fragment for Moai's 'Radio' Control mode
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robotica
 * 02/10/2016
 */

public class RC extends PApplet {
    private static final String TAG = "RC";
    private static final int MINX = -250;
    private static final int MAXX =  250;
    private static final int MINY = -250;
    private static final int MAXY =  250;
    private static final int MINS = -250;
    private static final int MAXS =  250;

    private MainActivity m;
    private float xPosition, yPosition;
    private int lSpeed, rSpeed;

    private PFont infoFont;
    private PImage mouse;
    private ElevonMode elevonMode;
    private ButtonMode buttonMode;

    public boolean overE, overC, overD, overB, overS;

    public RC() {}

    public void settings() {
        size(displayWidth, displayHeight, P2D);
    }

    public void setup() {
        this.m = (MainActivity) getActivity();
        if (m == null)
            throw new RuntimeException("M is null");

        infoFont = createFont("Roboto-Regular.ttf", 120);
        mouse = loadImage("brasao.png");
        mouse.resize((int)(0.06f * width), 0);
//        elevonMode = new ElevonMode(0.05f * width, 0.3f * height, 0.9f * width, 0.9f * width);
        buttonMode = new ButtonMode(0.05f * width, 0.3f * height, 0.9f * width, 0.9f * width);
    }

    public void draw() {
        background(255);
        checkBluetooth();
        buttonMode.show();
    }

    private boolean checkBluetooth() {
        if (m.mBtService.getState() == BluetoothService.STATE_CONNECTED)
            return true;

        fill(255, 0, 0);
        noStroke();
        ellipse(0.95f * width, 0.03f * height, 50, 50);
        return false;
    }

    private class ElevonMode {
        float xE, xD, yC, yB, w, h;

        public ElevonMode(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;
        }

        public void show() {
            stroke(127);
            strokeWeight(10);
            line((xE+xD)/2, yC, (xE+xD)/2, yB);
            line(xE, (yC+yB)/2, xD, (yC+yB)/2);

            noFill();
            stroke(0);
            strokeWeight(20);

            rect(xE, yC, w, h);

            fill(Constants.COLOR_PRIMARY_LIGHT);
            noStroke();

            float x, y;
            if (mousePressed) {
                x = mouseX < xE ? xE : mouseX;
                x = mouseX > xD ? xD : x;
                y = mouseY < yC ? yC : mouseY;
                y = mouseY > yB ? yB : y;
            } else {
                x = (xE + xD) / 2;
                y = (yC + yB) / 2;
            }

            imageMode(PApplet.CENTER);
            image(mouse, x, y);
            xPosition = map(x, xE, xD, MINX, MAXX);
            yPosition = map(y, yC, yB, MAXY, MINY);

            updateSpeeds();
            showInfo();
        }

        private void showInfo() {
            fill(Constants.COLOR_BLACK);
            textFont(infoFont);
            textAlign(PApplet.LEFT, PApplet.CENTER);
            textSize(0.06f * height);
            text("X: " + nf((int)xPosition, 3, 0), 0.05f * width, 0.1f * height);
            text("Y: " + nf((int)yPosition, 3, 0), 0.05f * width, 0.2f * height);
            text("L: " + nf((int)lSpeed,    3, 0), 0.55f * width, 0.1f * height);
            text("R: " + nf((int)rSpeed,    3, 0), 0.55f * width, 0.2f * height);
        }

        private void updateSpeeds() {
            lSpeed = (int)(yPosition + xPosition);
            if (lSpeed > MAXS) lSpeed = MAXS;
            else if (lSpeed < MINS) lSpeed = MINS;

            rSpeed = (int)(yPosition - xPosition);
            if (rSpeed > MAXS) rSpeed = MAXS;
            else if (rSpeed < MINS) rSpeed = MINS;

            if (!checkBluetooth())
                return;

            int lDir, rDir, lS, rS;
            lDir = lSpeed < 0 ? Constants.MOT_TRAS : Constants.MOT_FRENTE;
            rDir = rSpeed < 0 ? Constants.MOT_TRAS : Constants.MOT_FRENTE;

            lS = (abs(lSpeed));
            rS = (abs(rSpeed));

            m.sendMessage(new int[] {
                    Constants.PACKET_HEADER,
                    Constants.PACKET_SIZE_SET_MOTORS,
                    Constants.CMD_SET_MOTORS,
                    lDir,
                    lS,
                    rDir,
                    rS,
                    Constants.PACKET_TAIL
            });

//        try {
//            Thread.sleep(5);
//        } catch (InterruptedException e) {
//            Log.e(TAG, "Sleep error");
//        }
        }
    }

    private class ButtonMode {
        float xE, xD, yC, yB, w, h, r;
        PShape E, D, C, B, S;


        public ButtonMode(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            this.r = 0.48f * w;

            E = createShape(ARC, xE + w/2,yC + h/2, 2*r, 2*r, PI - QUARTER_PI, PI + QUARTER_PI, PIE);
            E.setStrokeWeight(10);
            E.setStroke(Constants.COLOR_DIVIDER);
            E.setFill(Constants.COLOR_PRIMARY);

            C = createShape(ARC, xE + w/2,yC + h/2, 2*r, 2*r, PI + QUARTER_PI, 3*HALF_PI + QUARTER_PI, PIE);
            C.setStrokeWeight(10);
            C.setStroke(Constants.COLOR_DIVIDER);
            C.setFill(Constants.COLOR_PRIMARY);

            D = createShape(ARC, xE + w/2,yC + h/2, 2*r, 2*r, -QUARTER_PI, QUARTER_PI, PIE);
            D.setStrokeWeight(10);
            D.setStroke(Constants.COLOR_DIVIDER);
            D.setFill(Constants.COLOR_PRIMARY);

            B = createShape(ARC, xE + w/2,yC + h/2, 2*r, 2*r, QUARTER_PI, PI - QUARTER_PI, PIE);
            B.setStrokeWeight(10);
            B.setStroke(Constants.COLOR_DIVIDER);
            B.setFill(Constants.COLOR_PRIMARY);

            S = createShape(ELLIPSE, xE + w/2, yC + h/2, 0.3f * w, 0.3f * w);
            S.setStrokeWeight(10);
            S.setStroke(Constants.COLOR_DIVIDER);
            S.setFill(0xFFF44336);
//            S.setFill(Constants.COLOR_PRIMARY);
        }

        public void show() {
            updateColors();
            shape(E);
            shape(C);
            shape(D);
            shape(B);
            shape(S);
        }

        private void updateColors() {
            fill(Constants.COLOR_BLACK);
            textFont(infoFont);
            textAlign(PApplet.LEFT, PApplet.CENTER);
            textSize(0.06f * height);
            if (overS) {
                S.setFill(0xFFE57373);
                E.setFill(Constants.COLOR_PRIMARY);
                C.setFill(Constants.COLOR_PRIMARY);
                D.setFill(Constants.COLOR_PRIMARY);
                B.setFill(Constants.COLOR_PRIMARY);
                text("STOP", 0.55f * width, 0.1f * height);
            } else if (overE) {
                S.setFill(0xFFF44336);
                E.setFill(Constants.COLOR_PRIMARY_LIGHT);
                C.setFill(Constants.COLOR_PRIMARY);
                D.setFill(Constants.COLOR_PRIMARY);
                B.setFill(Constants.COLOR_PRIMARY);
                text("ESQ", 0.55f * width, 0.1f * height);
            } else if (overC) {
                S.setFill(0xFFF44336);
                E.setFill(Constants.COLOR_PRIMARY);
                C.setFill(Constants.COLOR_PRIMARY_LIGHT);
                D.setFill(Constants.COLOR_PRIMARY);
                B.setFill(Constants.COLOR_PRIMARY);
                text("CIMA", 0.55f * width, 0.1f * height);
            } else if (overD) {
                S.setFill(0xFFF44336);
                E.setFill(Constants.COLOR_PRIMARY);
                C.setFill(Constants.COLOR_PRIMARY);
                D.setFill(Constants.COLOR_PRIMARY_LIGHT);
                B.setFill(Constants.COLOR_PRIMARY);
                text("DIR", 0.55f * width, 0.1f * height);
            } else if (overB) {
                S.setFill(0xFFF44336);
                E.setFill(Constants.COLOR_PRIMARY);
                C.setFill(Constants.COLOR_PRIMARY);
                D.setFill(Constants.COLOR_PRIMARY);
                B.setFill(Constants.COLOR_PRIMARY_LIGHT);
                text("BAIXO", 0.55f * width, 0.1f * height);
            } else {
                S.setFill(0xFFF44336);
                E.setFill(Constants.COLOR_PRIMARY);
                C.setFill(Constants.COLOR_PRIMARY);
                D.setFill(Constants.COLOR_PRIMARY);
                B.setFill(Constants.COLOR_PRIMARY);
                text("NADA", 0.55f * width, 0.1f * height);
            }
        }

        public void mouseReleased() {
            overE = overC = overD = overB = overS = false;
        }

        public void mousePressed() {
            Log.i(TAG, mouseX + " " + mouseY + " - " + (xE + w/2) + " " + (yC + h/2) + " (" + r + ")");
            float x = mouseX - xE;
            float y = yB - mouseY;

            overS = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= 0.15f * w);
            overE = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= r) &&
                    (x < y) && (y/(yB-yC) + x/(xD-xE)) < 1 && !overS;
            overC = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= r) &&
                    (x < y) && (y/(yB-yC) + x/(xD-xE)) > 1 && !overS;
            overD = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= r) &&
                    (x > y) && (y/(yB-yC) + x/(xD-xE)) > 1 && !overS;
            overB = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= r) &&
                    (x > y) && (y/(yB-yC) + x/(xD-xE)) < 1 && !overS;

            if (overS) {
                Log.i(TAG, "=== Over S");
                sendButton(Constants.PACKET_BUTTON_STOP);
            } else if (overE) {
                sendButton(Constants.PACKET_BUTTON_LEFT);
            } else if (overC) {
                sendButton(Constants.PACKET_BUTTON_UP);
            } else if (overD) {
                sendButton(Constants.PACKET_BUTTON_RIGHT);
            } else if (overB) {
                sendButton(Constants.PACKET_BUTTON_DOWN);
            }
        }

        private void sendButton(byte button) {
            m.sendMessage(new int[] {
                    Constants.PACKET_HEADER,
                    Constants.PACKET_SIZE_SET_MOTORS,
                    Constants.CMD_SET_MOTORS,
                    button,
                    Constants.PACKET_TAIL
            });
        }
    }

    public void mouseReleased() {
        buttonMode.mouseReleased();
    }

    public void mousePressed() {
        buttonMode.mousePressed();
    }
}
