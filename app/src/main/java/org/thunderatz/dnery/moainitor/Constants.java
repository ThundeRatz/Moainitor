package org.thunderatz.dnery.moainitor;

/**
 * Constants.java
 * Set project-wide constants
 *
 * Autor: Daniel Nery Silva de Oliveira
 *
 * Equipe ThundeRatz de Robótica
 * 02/10/2016
 */

public interface Constants {
    // Message types sent from the BluetoothService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

    /// Packet masks
    // Header and Tail
    int PACKET_HEADER = 0xFF;
    int PACKET_TAIL   = 0xFA;

    // Sent Packets
    // SET Commands
    int CMD_SET_MOTORS   = 0x02;
    int CMD_SET_STRATEGY = 0x04;
    int CMD_SET_STATE    = 0x06;
    
    // Request Commands
    int CMD_RQST_STATE    = 0x08;
    int CMD_RQST_STRATEGY = 0x0A;

    // Start/Stop Commands
    int CMD_START_AUTO = 0x0C;
    int CMD_STOP_AUTO  = 0x0E;

    // SET_MOTORS buttons
    int BUTTON_UP    = 0x01;
    int BUTTON_DOWN  = 0x02;
    int BUTTON_LEFT  = 0x04;
    int BUTTON_RIGHT = 0x08;
    int BUTTON_STOP  = 0x10;

    // Received Packets
    // Sensors
    int ANS_SENSOR_LINE     = 0x12;
    int ANS_SENSOR_DISTANCE = 0x14;

    int ANS_BATTERY   = 0x16;
    int ANS_STATE     = 0x18;
    int ANS_MOTORS    = 0x1A;
    int ANS_STRATEGY  = 0x1C;

    // ANS_MOTORS motor directions
    int MOT_FRENTE = 0x0F;
    int MOT_TRAS   = 0xF0;

    // ANS_STATE states
    int STATE_NONE   = 0x10;
    int STATE_AUTO_S = 0x20;
    int STATE_AUTO_P = 0x30;
    int STATE_RC     = 0x40;


    // Colors for Processing 0xAARRGGBB
    int COLOR_PRIMARY        = 0xFF303F9F;
    int COLOR_PRIMARY_DARK   = 0xFF1A237E;
    int COLOR_PRIMARY_LIGHT  = 0xFF3F51B5;
    int COLOR_ACCENT         = 0xFFFFFF00;
    int COLOR_PRIMARY_TEXT   = 0xFFF5F5F5;
    int COLOR_SECONDARY_TEXT = 0xFF757575;
    int COLOR_TEXT_ICONS     = 0xFFFFFFFF;
    int COLOR_DIVIDER        = 0xFFBDBDBD;
    int COLOR_BLACK          = 0xFF000000;
    int COLOR_BACKGROUND     = 0xFFEFEFEF;
}
