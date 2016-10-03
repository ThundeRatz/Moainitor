package org.thunderatz.dnery.moainitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Splash.java
 * Moainitor Splash screen
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robotica
 * 02/10/2016
 */

public class Splash extends Activity {
    private final static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
