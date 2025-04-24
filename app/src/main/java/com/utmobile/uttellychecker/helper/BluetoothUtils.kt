package com.utmobile.uttellychecker.helper

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class BluetoothPrinterService : Service() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    override fun onCreate() {
        super.onCreate()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            stopSelf()
            return
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        val printerUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        when (action) {
            ACTION_CONNECT -> connectToPrinter()
            ACTION_PRINT -> printData()
        }
        return START_STICKY
    }

    private fun connectToPrinter() {
        // First, ensure that Bluetooth is enabled
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Toast.makeText(applicationContext, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        // Assuming you're getting the Bluetooth device via scanning, replace this with your logic
        val deviceAddress = "00:11:22:33:44:55"  // Replace with actual device address after discovery
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)

        if (device == null) {
            Toast.makeText(applicationContext, "Device not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Standard RFCOMM UUID for Bluetooth serial communication (most printers use this)
        val printerUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        try {
            // Create Bluetooth socket and attempt connection
            bluetoothSocket = device.createRfcommSocketToServiceRecord(printerUUID)

            // Connect to the Bluetooth printer
            bluetoothSocket?.connect()

            // Once connected, you can now send print data
            Toast.makeText(applicationContext, "Connected to printer", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Failed to connect to printer: ${e.message}", Toast.LENGTH_SHORT).show()
            // Optionally, close the socket if there's an error
            try {
                bluetoothSocket?.close()
            } catch (closeException: IOException) {
                closeException.printStackTrace()
            }
        }
    }



    private fun printData() {
        if (bluetoothSocket?.isConnected == true) {
            try {
                val outputStream: OutputStream = bluetoothSocket?.outputStream ?: return
                val printData = "Hello, Bluetooth Printer!"
                outputStream.write(printData.toByteArray())
                outputStream.flush()
                Toast.makeText(applicationContext, "Data sent to printer", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Failed to send data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val ACTION_CONNECT = "com.example.bluetoothprinter.ACTION_CONNECT"
        const val ACTION_PRINT = "com.example.bluetoothprinter.ACTION_PRINT"
    }
}
