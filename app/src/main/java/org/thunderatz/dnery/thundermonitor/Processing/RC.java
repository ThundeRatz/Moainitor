package org.thunderatz.dnery.thundermonitor.Processing;

import org.thunderatz.dnery.thundermonitor.BluetoothService;
import org.thunderatz.dnery.thundermonitor.Constants;
import org.thunderatz.dnery.thundermonitor.MainActivity;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

/**
 * Created by dnery on 18/09/2016.
 */
public class RC extends PApplet {
    private static final String TAG = "RC";
    private static final int MINX = -125;
    private static final int MAXX =  125;
    private static final int MINY = -125;
    private static final int MAXY =  125;

    private MainActivity m;
    private float xPosition, yPosition;
    private int lSpeed, rSpeed;
    private boolean connected;

    private PFont infoFont;
    private PImage mouse;
    private ElevonMode elevonMode;

    public RC() {
        this.m = null;
    }

    public RC(MainActivity m) {
        this.m = m;
    }

    public void settings() {
        size(displayWidth, displayHeight);
    }

    public void setup() {
        if (m == null)
            throw new RuntimeException("M is null");

        infoFont = createFont("Roboto-Regular.ttf", 120);
        mouse = loadImage("brasao.png");
        mouse.resize((int)(0.05f * width), 0);
        elevonMode = new ElevonMode(0.05f * width, 0.3f * height, 0.9f * width, 0.9f * width);

        connected = (m.mBtService.getState() == BluetoothService.STATE_CONNECTED);
    }

    public void draw() {
        background(255);
        checkBluetooth();
        elevonMode.show();
        updateSpeeds();
        showInfo();
    }

    private boolean checkBluetooth() {
        if (connected)
            return true;

        fill(255, 0, 0);
        noStroke();
        ellipse(0.95f * width, 0.03f * height, 50, 50);
        return false;
    }

    private void updateSpeeds() {
        lSpeed = (int)(yPosition + xPosition);
        rSpeed = (int)(yPosition - xPosition);

        if (!connected)
            return;

        byte lDir, rDir, lS, rS;
        lDir = lSpeed < 0 ? Constants.MOT_TRAS : Constants.MOT_FRENTE;
        rDir = rSpeed < 0 ? Constants.MOT_TRAS : Constants.MOT_FRENTE;

        lS = (byte)(abs(lSpeed));
        rS = (byte)(abs(rSpeed));

        m.sendMessage(new byte[]{
                Constants.PACKET_HEADER,
                Constants.PACKET_SIZE_SET_MOTORS,
                Constants.CMD_SET_MOTORS,
                lDir,
                lS,
                rDir,
                rS,
                Constants.PACKET_TAIL
        });

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
        }
    }
}
