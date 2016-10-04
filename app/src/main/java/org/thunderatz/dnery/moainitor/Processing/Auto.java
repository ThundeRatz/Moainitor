package org.thunderatz.dnery.moainitor.processing;

import org.thunderatz.dnery.moainitor.BluetoothService;
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

    private StrategySelector strategySelector;
    private StopButton stopButton;
    private PlayButton playButton;
    private CurrentStrategy currentStrategy;
    private StartAutoButton startAutoButton;
    private StateButton stateButton;
    private PFont font;

    public Auto() {}

    public void settings() {
        size(displayWidth, displayHeight, P2D);
    }

    public void setup() {
        this.m = (MainActivity) getActivity();
        if (m == null)
            throw new RuntimeException(TAG + ": MainActivity is null");

        font = createFont("Roboto-Regular.ttf", 120);
        startAutoButton = new StartAutoButton(0.6f * width, 0.05f * height, 0.38f * width, 0.1f * height);
        stateButton = new StateButton(0.02f * width, 0.05f * height, 0.38f * width, 0.1f * height);
        currentStrategy = new CurrentStrategy(0.5f * width - 0.075f * height, 0.83f * height, 0.15f*height, 0.15f*height);

        stopButton = new StopButton(0.02f * width, 0.83f * height, 0.15f * height, 0.15f * height);
        playButton = new PlayButton(width - 0.17f * height, 0.83f * height, 0.15f * height, 0.15f * height);
        strategySelector = new StrategySelector(0.02f*width, 0.12f*height, 0.96f*width, 0.68f*height);
    }

    public void draw() {
        background(Constants.COLOR_BACKGROUND);
        checkBluetooth();
        textFont(font);

        strategySelector.show();
        playButton.show();
        stopButton.show();
        startAutoButton.show();
        stateButton.show();
        currentStrategy.show();
    }

    private boolean checkBluetooth() {
        if (m.mBtService.getState() == BluetoothService.STATE_CONNECTED)
            return true;

        fill(255, 0, 0);
        noStroke();
        ellipse(0.95f * width, 0.03f * height, 50, 50);
        return false;
    }

    public void mouseReleased() {
        stopButton.mouseReleased();
        playButton.mouseReleased();
        strategySelector.mouseReleased();
        stateButton.mouseReleased();
        startAutoButton.mouseReleased();
        currentStrategy.mouseReleased();
    }

    public void mousePressed() {
        stopButton.mousePressed();
        playButton.mousePressed();
        strategySelector.mousePressed();
        stateButton.mousePressed();
        startAutoButton.mousePressed();
        currentStrategy.mousePressed();
    }

    private class StopButton {
        float xE, xD, yC, yB, w, h, r;
        boolean overBtn;
        PShape btn;

        StopButton(float xE, float yC, float w, float h) {
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

        void show() {
            if (overBtn)
                btn.setFill(0xFFE57373);
            else
                btn.setFill(0xFFF44336);
            shape(btn);
            textSize(0.2f * h);
            textAlign(PApplet.CENTER, PApplet.CENTER);
            fill(Constants.COLOR_BLACK);
            text("STOP", xE + w/2, yC + h/2);
        }

        void mousePressed() {
            float x = mouseX - xE;
            float y = yB - mouseY;

            overBtn = (sqrt(sq(xE + w/2 - mouseX) + sq(yC + h/2 - mouseY)) <= r);
            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        0x04,
                        Constants.CMD_STOP_AUTO,
                        Constants.PACKET_TAIL
                });
            }
        }

        void mouseReleased() {
            overBtn = false;
        }
    }

    private class PlayButton {
        float xE, xD, yC, yB, w, h;
        boolean overBtn;
        PShape btn;

        PlayButton(float xE, float yC, float w, float h) {
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

        void show() {
            if (overBtn)
                btn.setFill(0xFF81C784);
            else
                btn.setFill(0xFF4CAF50);
            shape(btn);
            fill(Constants.COLOR_BLACK);
            textSize(0.2f * h);
            textAlign(PApplet.LEFT, PApplet.CENTER);
            text("START", xE + 0.1f * w, yC + h/2);
        }

        void mousePressed() {
            float x = mouseX - xE;
            float y = yB - mouseY;

            overBtn = (mouseX >= xE && mouseX <= xD && x/2 < y && (y/(yB-yC) + x/(xD+w-xE) < 1));

            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        0x04,
                        Constants.CMD_START_AUTO,
                        Constants.PACKET_TAIL
                });
            }
        }

        void mouseReleased() {
            overBtn = false;
        }
    }

    private class StrategySelector {
        float xE, xD, yC, yB, w, h;
        LeftButton leftButton;
        RightButton rightButton;
        SetButton setButton;
        int strategy;

        StrategySelector(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            strategy = 1;

            rightButton = new RightButton(xE + 0.75f * w, yC + 0.5f * h - 0.1f * w, 0.2f*w, 0.2f*w);
            leftButton = new LeftButton(xE + 0.05f * w, yC + 0.5f * h - 0.1f * w, 0.2f*w, 0.2f*w);
            setButton = new SetButton(xE + 0.3f * w, yC + 0.85f * h, 0.4f*w, 0.1f*h);
        }

        void show() {
            noStroke();
            fill(Constants.COLOR_SECONDARY_TEXT);
            textSize(0.08f * h);
            textAlign(PApplet.CENTER, PApplet.TOP);
            text("Estratégia", xE + w/2, yC + 0.1f*h);
            textSize(0.55f * h);
            textAlign(PApplet.CENTER, PApplet.CENTER);
            text(strategy, xE+w/2, yC+h/2);

            leftButton.show();
            rightButton.show();
            setButton.show();
        }

        void mousePressed() {
            setButton.mousePressed();
            rightButton.mousePressed();
            leftButton.mousePressed();
        }

        void mouseReleased() {
            setButton.mouseReleased();
            rightButton.mouseReleased();
            leftButton.mouseReleased();
        }

        private class LeftButton {
            float xE, xD, yC, yB, w, h;
            PShape btn;
            boolean overBtn;

            LeftButton(float xE, float yC, float w, float h) {
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
                btn.setFill(Constants.COLOR_PRIMARY);
                btn.beginShape();
                btn.vertex(xE, yC + h/2);
                btn.vertex(xD, yC);
                btn.vertex(xD, yB);
                btn.endShape(CLOSE);
            }

            void show() {
                if (overBtn)
                    btn.setFill(Constants.COLOR_PRIMARY_LIGHT);
                else
                    btn.setFill(Constants.COLOR_PRIMARY);
                shape(btn);
            }

            void mousePressed() {
                float x = mouseX - xE;
                float y = yB - mouseY;

                overBtn = (mouseX >= xE && mouseX <= xD && (y/(yB-yC) + x/(-2*w) < 1) && y > -x);

                if (overBtn) {
                    strategy = strategy > 1 ? strategy - 1 : 1;
                }
            }

            void mouseReleased() {
                overBtn = false;
            }
        }

        private class RightButton {
            float xE, xD, yC, yB, w, h;
            PShape btn;
            boolean overBtn;

            RightButton(float xE, float yC, float w, float h) {
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
                btn.setFill(Constants.COLOR_PRIMARY);
                btn.beginShape();
                btn.vertex(xE, yC);
                btn.vertex(xD, yC + h/2);
                btn.vertex(xE, yB);
                btn.endShape(CLOSE);
            }

            void show() {
                if (overBtn)
                    btn.setFill(Constants.COLOR_PRIMARY_LIGHT);
                else
                    btn.setFill(Constants.COLOR_PRIMARY);
                shape(btn);
            }

            void mousePressed() {
                float x = mouseX - xE;
                float y = yB - mouseY;

                overBtn = (mouseX >= xE && mouseX <= xD && x/2 < y && (y/(yB-yC) + x/(xD+w-xE) < 1));

                if (overBtn) {
                    strategy = strategy < 9 ? strategy + 1 : 9;
                }
            }

            void mouseReleased() {
                overBtn = false;
            }
        }

        private class SetButton {
            float xE, xD, yC, yB, w, h;
            PShape btn;
            boolean overBtn;

            SetButton(float xE, float yC, float w, float h) {
                this.xE = xE;
                this.yC = yC;
                this.w = w;
                this.h = h;

                this.xD = xE + w;
                this.yB = yC + h;
                overBtn = false;

                btn = createShape(RECT, xE, yC, w, h);
                btn.setStrokeWeight(10);
                btn.setStroke(Constants.COLOR_DIVIDER);
                btn.setFill(Constants.COLOR_PRIMARY);
            }

            void show() {
                if (overBtn)
                    btn.setFill(Constants.COLOR_PRIMARY_LIGHT);
                else
                    btn.setFill(Constants.COLOR_PRIMARY);
                shape(btn);
                fill(Constants.COLOR_TEXT_ICONS);
                textSize(0.8f * h);
                textAlign(PApplet.CENTER, PApplet.CENTER);
                text("SET", xE + w/2, yC + h/2);
            }

            void mousePressed() {
                float x = mouseX - xE;
                float y = yB - mouseY;

                overBtn = (mouseX >= xE && mouseX <= xD && mouseY >= yC && mouseY <= yB);

                if (overBtn) {
                    m.sendMessage(new int[] {
                            Constants.PACKET_HEADER,
                            0x05,
                            Constants.CMD_SET_STRATEGY,
                            strategy,
                            Constants.PACKET_TAIL
                    });
                }
            }

            void mouseReleased() {
                overBtn = false;
            }
        }
    }

    private class StateButton {
        float xE, xD, yC, yB, w, h;
        boolean overBtn;
        String state;
        PShape btn;

        StateButton(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            overBtn = false;

            btn = createShape(RECT, xE, yC, w, h);
            btn.setStrokeWeight(10);
            btn.setStroke(Constants.COLOR_DIVIDER);
            btn.setFill(Constants.COLOR_PRIMARY);
        }

        void show() {
            if (overBtn)
                btn.setFill(Constants.COLOR_PRIMARY_LIGHT);
            else
                btn.setFill(Constants.COLOR_PRIMARY);
            shape(btn);
            fill(Constants.COLOR_TEXT_ICONS);
            textSize(0.5f * h);
            textAlign(PApplet.CENTER, PApplet.CENTER);

            switch (m.currentState) {
                case Constants.STATE_NONE:
                    state = "NONE"; break;
                case Constants.STATE_AUTO_S:
                    state = "AUTO_S"; break;
                case Constants.STATE_AUTO_P:
                    state = "AUTO_P"; break;
                case Constants.STATE_RC:
                    state = "RC"; break;
                default:
                    state = "?"; break;
            }

            text(state, xE + w/2, yC + h/2);
        }

        void mousePressed() {
            float x = mouseX - xE;
            float y = yB - mouseY;

            overBtn = (mouseX >= xE && mouseX <= xD && mouseY >= yC && mouseY <= yB);

            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        0x04,
                        Constants.CMD_RQST_STATE,
                        Constants.PACKET_TAIL
                });
            }
        }

        void mouseReleased() {
            overBtn = false;
        }
    }

    private class StartAutoButton {
        float xE, xD, yC, yB, w, h;
        boolean overBtn;
        String state;
        PShape btn;

        StartAutoButton(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            overBtn = false;

            btn = createShape(RECT, xE, yC, w, h);
            btn.setStrokeWeight(10);
            btn.setStroke(Constants.COLOR_DIVIDER);
            btn.setFill(Constants.COLOR_PRIMARY);
        }

        void show() {
            if (overBtn)
                btn.setFill(Constants.COLOR_PRIMARY_LIGHT);
            else
                btn.setFill(Constants.COLOR_PRIMARY);
            shape(btn);
            fill(Constants.COLOR_TEXT_ICONS);
            textSize(0.5f * h);
            textAlign(PApplet.CENTER, PApplet.CENTER);

            text("Start Auto", xE + w/2, yC + h/2);
        }

        void mousePressed() {
            overBtn = (mouseX >= xE && mouseX <= xD && mouseY >= yC && mouseY <= yB);

            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        0x05,
                        Constants.CMD_SET_STATE,
                        Constants.STATE_AUTO_S,
                        Constants.PACKET_TAIL
                });
            }
        }

        void mouseReleased() {
            overBtn = false;
        }
    }

    private class CurrentStrategy {
        float xE, xD, yC, yB, w, h;
        boolean overBtn;
        String state;
        PShape btn;

        CurrentStrategy(float xE, float yC, float w, float h) {
            this.xE = xE;
            this.yC = yC;
            this.w = w;
            this.h = h;

            this.xD = xE + w;
            this.yB = yC + h;

            overBtn = false;

            btn = createShape(RECT, xE, yC, w, h);
            btn.setStrokeWeight(10);
            btn.setStroke(Constants.COLOR_DIVIDER);
            btn.setFill(Constants.COLOR_PRIMARY);
        }

        void show() {
            if (overBtn)
                btn.setFill(Constants.COLOR_PRIMARY_LIGHT);
            else
                btn.setFill(Constants.COLOR_PRIMARY);
            shape(btn);
            fill(Constants.COLOR_TEXT_ICONS);
            textSize(0.8f * h);
            textAlign(PApplet.CENTER, PApplet.CENTER);

            text(m.currentStrategy, xE + w/2, yC + h/2);
        }

        void mousePressed() {
            overBtn = (mouseX >= xE && mouseX <= xD && mouseY >= yC && mouseY <= yB);

            if (overBtn) {
                m.sendMessage(new int[] {
                        Constants.PACKET_HEADER,
                        0x04,
                        Constants.CMD_RQST_STRATEGY,
                        Constants.PACKET_TAIL
                });
            }
        }

        void mouseReleased() {
            overBtn = false;
        }
    }
}
