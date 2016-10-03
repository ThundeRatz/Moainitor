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

    // Packet masks
    int PACKET_HEADER   = 0xFF;
    int PACKET_TAIL     = 0xFE;

    // Sent Packets
    // Commands
    byte CMD_SET_MOTORS   = (byte)0x10;
    byte CMD_SET_STRATEGY = (byte)0x20;
    byte CMD_SET_MODE     = (byte)0x30;
    byte CMD_START_AUTO   = (byte)0x40;
    byte CMD_STOP         = (byte)0x80;

    // Motor Directions
    byte MOT_FRENTE = (byte)0x00;
    byte MOT_TRAS   = (byte)0x01;

    // Packet Sizes
    byte PACKET_SIZE_SET_MOTORS   = (byte)0x05;
    byte PACKET_SIZE_SET_STRATEGY = (byte)0x05;
    byte PACKET_SIZE_SET_MODE     = (byte)0x05;
    byte PACKET_SIZE_START_AUTO   = (byte)0x04;
    byte PACKET_SIZE_STOP         = (byte)0x04;

    // SET_MOTORS
    byte PACKET_BUTTON_UP    = (byte)0x01;
    byte PACKET_BUTTON_DOWN  = (byte)0x02;
    byte PACKET_BUTTON_LEFT  = (byte)0x04;
    byte PACKET_BUTTON_RIGHT = (byte)0x08;
    byte PACKET_BUTTON_STOP  = (byte)0x10;

    // Received Packets
    // Sensors
    byte SENSOR_DISTANCE = (byte)0x50;
    byte SENSOR_LINE     = (byte)0x60;
    byte BATTERY_LEVEL   = (byte)0x70;

    // Packet Sizes
    byte PACKET_SIZE_SENSOR  = (byte)0x06;
    byte PACKET_SIZE_BATTERY = (byte)0x05;

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

}
