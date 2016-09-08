package org.thunderatz.dnery.thundermonitor;

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
    byte PACKET_HEADER   = (byte)0xFF;
    byte PACKET_TAIL     = (byte)0xFE;

    // Sent Packets
    // Commands
    byte CMD_SET_MOTORS   = (byte)0x02;
    byte CMD_SET_STRATEGY = (byte)0x03;
    byte CMD_SET_MODE     = (byte)0x04;
    byte CMD_START_AUTO   = (byte)0x05;

    // Packet Sizes
    byte PACKET_SIZE_SET_MOTORS   = (byte)0x06;
    byte PACKET_SIZE_SET_STRATEGY = (byte)0x05;
    byte PACKET_SIZE_SET_MODE     = (byte)0x05;
    byte PACKET_SIZE_START_AUTO   = (byte)0x04;

    // Received Packets
    // Sensors
    byte SENSOR_DISTANCE = (byte)0x02;
    byte SENSOR_LINE     = (byte)0x03;
//    byte BATTERY_LEVEL   = (byte)0x04;

    // Packet Sizes
    byte PACKET_SIZE_SENSOR  = (byte)0x06;
//    byte PACKET_SIZE_BATTERY = (byte)0x05;
}
