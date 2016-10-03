package org.thunderatz.dnery.moainitor.processing;

import org.thunderatz.dnery.moainitor.BluetoothService;
import org.thunderatz.dnery.moainitor.Constants;
import org.thunderatz.dnery.moainitor.MainActivity;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

/**
 * Sensors.java
 * Processing fragment for sensors monitoring
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robotica
 * 02/10/2016
 */

public class Sensors extends PApplet {
    private static final String TAG = "RC";

    private MainActivity m;
    private PFont font;

    private DistanceSensors distanceSensors;
    private LineSensors lineSensors;
    private Battery battery;

    private int lSpeed, rSpeed;

    public Sensors() {}

    public void settings() {
        size(displayWidth, displayHeight, P2D);
    }

    public void setup() {
        this.m = (MainActivity) getActivity();
        if (m == null)
            throw new RuntimeException("M is null");

        font = createFont("Roboto-Regular.ttf", 32);
        distanceSensors = new DistanceSensors(0.01f * width, 0.5f * height, 0.98f * width, 0.49f * height);
        lineSensors = new LineSensors(0.1f * width, 0.25f * height, 0.8f * width, 0.24f * height);
        battery = new Battery(0.55f * width, 0.05f * height, 0.4f * width, 0.13f * height);

        lSpeed = rSpeed = 125;
    }

    public void draw() {
        background(255);
        checkBluetooth();
        distanceSensors.show();
        lineSensors.show();
        battery.show();
        showSpeed();
    }

    public void showSpeed() {
        fill(Constants.COLOR_BLACK);
        textFont(font);
        textAlign(PApplet.LEFT, PApplet.CENTER);
        textSize(0.06f * height);
        text("L: " + nf(m.lSpeed,    3, 0), 0.05f * width, 0.08f * height);
        text("R: " + nf(m.rSpeed,    3, 0), 0.05f * width, 0.15f * height);
    }

    private boolean checkBluetooth() {
        if ((m.mBtService.getState() == BluetoothService.STATE_CONNECTED))
            return true;

        fill(255, 0, 0);
        noStroke();
        ellipse(0.95f * width, 0.03f * height, 50, 50);
        return false;
    }

    private class DistanceSensors {
        float xE, xD, yC, yB, w, h;
        DistanceSensorBar[] bars;

        public DistanceSensors(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            xD = xE + w;
            yB = yC + h;

            bars = new DistanceSensorBar[7];
            for (int i = 0; i < 7; i++)
                bars[i] = new DistanceSensorBar("PC" + i, xE + i*w/7f, yC, w/7f, h, i);
        }

        public void show() {
            for (DistanceSensorBar bar : bars)
                bar.show();
        }

        private class DistanceSensorBar {
            private String port;
            private int value, index;

            float xE, xD, yC, yB, w, h;

            public DistanceSensorBar(String port, float xE, float yC, float w, float h, int index) {
                this.index = index;
                this.port = port;
                this.xE = xE;
                this.yC = yC;
                this.w = w;
                this.h = h;

                xD = xE + w;
                yB = yC + h;

                value = 0;
            }

            public void show() {
                value = m.distances[index];

                fill(Constants.COLOR_BLACK);
                textFont(font);
                textSize(0.07f * h);
                textAlign(PApplet.CENTER, PApplet.CENTER);
                text(port, xE + w/2, yC + 0.05f * h);
                text(nf(value, 3, 0), xE + w/2, yB - 0.05f * h);

                fill(0);
                stroke(0);
                rect(xE + 0.1f * w, yC + 0.1f * h, 0.8f * w, 0.8f * h);

                fill(255);
                float _h = map(value, 0, 255, 0, 0.8f * h);
                rect(xE + 0.1f * w, yC + 0.1f * h, 0.8f * w, _h);
            }
        }
    }

    private class LineSensors {
        float xE, xD, yC, yB, w, h;
        LineSensorCircle[] circles;

        public LineSensors(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            xD = xE + w;
            yB = yC + h;

            circles = new LineSensorCircle[4];
            circles[0] = new LineSensorCircle("PB0", xE, yC, w/7f, h, 0);
            circles[1] = new LineSensorCircle("PB1", xE + 2*w/7f, yC, w/7f, h, 1);
            circles[2] = new LineSensorCircle("PB2", xE + 4*w/7f, yC, w/7f, h, 2);
            circles[3] = new LineSensorCircle("PB3", xE + 6*w/7f, yC, w/7f, h, 3);
        }

        public void show() {
            for (LineSensorCircle circle : circles)
                circle.show();
        }

        private class LineSensorCircle {
            private String port;
            private int value, index;

            float xE, xD, yC, yB, w, h;

            public LineSensorCircle(String port, float xE, float yC, float w, float h, int index) {
                this.index = index;
                this.port = port;
                this.xE = xE;
                this.yC = yC;
                this.w = w;
                this.h = h;

                xD = xE + w;
                yB = yC + h;

                value = 0;
            }

            public void show() {
                value = m.line[index];

                fill(Constants.COLOR_BLACK);
                textFont(font);
                textSize(0.1f * h);
                textAlign(PApplet.CENTER, PApplet.CENTER);
                text(port, xE + w/2, yC + h/2 - 0.35f * w - 0.06f * h);
                text(nf(value, 1, 0), xE + w/2, yB - h/2 + 0.35f * w + 0.06f * h);

                fill(value * 255);
                stroke(0);
                ellipse(xE + w/2f, yC + h/2f, 0.7f * w, 0.7f * w);
            }
        }
    }

    private class Battery {
        float xE, xD, yC, yB, w, h;
        float val;

        PShape bat;

        public Battery(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            xD = xE + w;
            yB = yC + h;

            bat = createShape();
            bat.beginShape();
            bat.fill(200);
            bat.stroke(0);
            bat.strokeWeight(15);
            bat.vertex(xE, yC);
            bat.vertex(xE + 0.94f * w, yC);
            bat.vertex(xE + 0.94f * w, yC + 0.4f * h);
            bat.vertex(xD, yC + 0.4f * h);
            bat.vertex(xD, yC + 0.6f * h);
            bat.vertex(xE + 0.94f * w, yC + 0.6f * h);
            bat.vertex(xE + 0.94f * w, yB);
            bat.vertex(xE, yB);
            bat.vertex(xE, yC);
            bat.endShape();

            val = 3.86f;
        }

        public void show() {
            val = m.battery;

            shape(bat);
            rectangles();
            fill(Constants.COLOR_BLACK);
            textFont(font);
            textSize(h/3);
            textAlign(PApplet.CENTER, PApplet.CENTER);
            text(nf(val, 1, 2), xE + w/2, yB + 0.2f * h);
        }

        private void rectangles() {
            float _w = 0.82f/3 * w, _h = 0.9f * h,_y = yC + 0.05f * h;
            noStroke();
            if (val <= 3.7f) {
                fill(0xFFF44336);
                rect(xE + 0.03f * w, _y, _w, _h);
            } else if (val < 4.0f) {
                fill(0xFFFFEB3B);
                rect(xE + 0.03f * w, _y, _w, _h);
                rect(xE + 0.06f * w + _w, _y, _w, _h);
            } else {
                fill(0xFF4CAF50);
                rect(xE + 0.03f * w, _y, _w, _h);
                rect(xE + 0.06f * w + _w, _y, _w, _h);
                rect(xE + 0.09f * w + 2*_w, _y, _w, _h);
            }
        }
    }
}
