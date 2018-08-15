package com.br.android.storycard.view.sga.util;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

/**
 * PrinterBluetooth
 * 1.0
 * 07/08/2018
 * Classe responsável por conectar e imprimir via impressora bluetooth (innerprinter)
 */
public class PrinterBluetooth implements Runnable {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    protected static final String TAG = "TAG";
    private BluetoothSocket mBluetoothSocket;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private static final String Innerprinter_Address = "00:11:22:33:44:55";

    public void sendToPrint() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                if (mDevice.getAddress().equals(Innerprinter_Address)) {
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDevice.getAddress());
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
            imprimir();
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //mBluetoothConnectProgressDialog.dismiss();
            //Toast.makeText(MainActivity_New.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    public void imprimir() {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket.getOutputStream();
                    String BILL = montaString();

                    //BILL = BILL + "\n\n ";
                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    private String montaString() {
        String BILL = "";

        BILL =  "-------------------------\n"
                + "       BICHO BOM      \n " +
                "-------------------------\n";
        BILL = BILL + String.format("%1$-1s %2$5s ", "P: ", "27423");
        BILL = BILL + "\n";
        BILL = BILL + String.format("%1$-1s %2$5s ", "D: ", "02/08/2018");
        BILL = BILL + "\n";
        BILL = BILL + String.format("%1$-1s %2$5s ", "H: ", "12:39:04");
        BILL = BILL + "\n";
        BILL = BILL + String.format("%1$-1s %2$5s ", "V: ", "1413/1413");
        BILL = BILL
                + "\n-------------------------";
        BILL = BILL + "\n";
        BILL = BILL + String.format("%1$-10s %2$5s ", "CORRE EM ", "02/08/2018");
        BILL = BILL + "\n-------------------------";

        BILL = BILL + "\n\n ";

        return BILL;

    }

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }
}
