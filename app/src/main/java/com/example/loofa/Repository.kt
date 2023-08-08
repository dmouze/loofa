package com.example.loofa

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.loofa.util.Event
import com.example.loofa.util.SPP_UUID
import com.example.loofa.util.Util
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class Repository {

    var connected: MutableLiveData<Boolean?> = MutableLiveData(null)
    var progressState: MutableLiveData<String> = MutableLiveData("")
    val putTxt: MutableLiveData<String> = MutableLiveData("")

    val inProgress = MutableLiveData<Event<Boolean>>()
    val connectError = MutableLiveData<Event<Boolean>>()

    var mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var mBluetoothStateReceiver: BroadcastReceiver? = null
    var targetDevice: BluetoothDevice? = null
    var socket: BluetoothSocket? = null
    var mOutputStream: OutputStream? = null
    var mInputStream: InputStream? = null

    var foundDevice: Boolean = false

    private lateinit var sendByte: ByteArray
    var discovery_error = false

    fun isBluetoothSupport(): Boolean {
        return if (mBluetoothAdapter == null) {
            Util.showNotification("Urządzenie nie obsługuje Bluetooth.")
            false
        } else {
            true
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return if (!mBluetoothAdapter!!.isEnabled) {
            // Urządzenie obsługuje Bluetooth, ale jest wyłączone
            // Wymagane jest aktywowanie Bluetooth za zgodą użytkownika
            Util.showNotification("Proszę włączyć Bluetooth.")
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    fun scanDevice() {
        progressState.postValue("Skanowanie urządzeń...")

        registerBluetoothReceiver()

        val bluetoothAdapter = mBluetoothAdapter
        foundDevice = false
        bluetoothAdapter?.startDiscovery() // Rozpoczęcie skanowania urządzeń Bluetooth
    }

    fun registerBluetoothReceiver() {
        // Filtr intencji
        val stateFilter = IntentFilter()
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) // BluetoothAdapter.ACTION_STATE_CHANGED: zmiana stanu Bluetooth
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED) // Połączenie nawiązane
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED) // Połączenie przerwane
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND) // Urządzenie znalezione
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) // Rozpoczęcie skanowania urządzeń
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) // Zakończenie skanowania urządzeń
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        mBluetoothStateReceiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context?, intent: Intent) {
                val action = intent.action // Pobranie akcji
                if (action != null) {
                    Log.d("Bluetooth action", action)
                }
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                var name: String? = null
                if (device != null) {
                    name = device.name // Pobranie nazwy urządzenia z wiadomości broadcast
                }
                when (action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR
                        )
                        when (state) {
                            BluetoothAdapter.STATE_OFF -> {
                            }

                            BluetoothAdapter.STATE_TURNING_OFF -> {
                            }

                            BluetoothAdapter.STATE_ON -> {
                            }

                            BluetoothAdapter.STATE_TURNING_ON -> {
                            }
                        }
                    }

                    BluetoothDevice.ACTION_ACL_CONNECTED -> {

                    }

                    BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    }

                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        connected.postValue(false)
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    }

                    BluetoothDevice.ACTION_FOUND -> {
                        if (!foundDevice) {
                            val device_name = device!!.name
                            val device_Address = device.address

                            // Szukanie urządzeń o nazwie zaczynającej się od "RNM"
                            if (device_name != null && device_name.length > 4) {
                                if (device_name.substring(0, 3) == "ESP") {
                                    // Filtruj urządzenie docelowe i użyj connectToTargetedDevice()
                                    targetDevice = device
                                    foundDevice = true
                                    connectToTargetedDevice(targetDevice)
                                }
                            }
                        }
                    }
                }
            }
        }
        LufaApp.applicationContext().registerReceiver(
            mBluetoothStateReceiver,
            stateFilter
        )
    }

    @SuppressLint("MissingPermission")
    @ExperimentalUnsignedTypes
    private fun connectToTargetedDevice(targetedDevice: BluetoothDevice?) {
        progressState.postValue("Łączenie z ${targetDevice?.name}...")

        val thread = Thread {
            // Obiekt BluetoothDevice dla wybranego urządzenia
            val uuid = UUID.fromString(SPP_UUID)
            try {
                // Utworzenie gniazda BluetoothSocket
                socket = targetedDevice?.createRfcommSocketToServiceRecord(uuid)

                socket?.connect()

                /**
                 * Po nawiązaniu połączenia
                 */
                connected.postValue(true)
                mOutputStream = socket?.outputStream
                mInputStream = socket?.inputStream
                // Nasłuchiwanie na dane
                beginListenForData()

            } catch (e: Exception) {
                // Błąd podczas nawiązywania połączenia Bluetooth
                e.printStackTrace()
                connectError.postValue(Event(true))
                try {
                    socket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        // Rozpoczęcie wątku do nawiązania połączenia
        thread.start()
    }

    fun disconnect() {
        try {
            socket?.close()
            connected.postValue(false)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun unregisterReceiver() {
        if (mBluetoothStateReceiver != null) {
            LufaApp.applicationContext().unregisterReceiver(mBluetoothStateReceiver)
            mBluetoothStateReceiver = null
        }
    }

    /**
     * Wysyłanie danych za pomocą Bluetooth
     */
    fun sendByteData(data: ByteArray) {
        Thread {
            try {
                mOutputStream?.write(data) // Wysłanie danych
            } catch (e: Exception) {
                // Błąd podczas wysyłania danych
                e.printStackTrace()
            }
        }.run()
    }

    /**
     * Konwersja
     * @ByteToUint: byte[] -> uint
     * @byteArrayToHex: byte[] -> hex string
     */
    private val m_ByteBuffer: ByteBuffer = ByteBuffer.allocateDirect(8)
    // byte -> uint
    fun ByteToUint(data: ByteArray?, offset: Int, endian: ByteOrder): Long {
        synchronized(m_ByteBuffer) {
            m_ByteBuffer.clear()
            m_ByteBuffer.order(endian)
            m_ByteBuffer.limit(8)
            if (endian === ByteOrder.LITTLE_ENDIAN) {
                m_ByteBuffer.put(data, offset, 4)
                m_ByteBuffer.putInt(0)
            } else {
                m_ByteBuffer.putInt(0)
                m_ByteBuffer.put(data, offset, 4)
            }
            m_ByteBuffer.position(0)
            return m_ByteBuffer.long
        }
    }

    fun byteArrayToHex(a: ByteArray): String? {
        val sb = StringBuilder()
        for (b in a) sb.append(String.format("%02x ", b /*&0xff*/))
        return sb.toString()
    }

    /**
     * Nasłuchiwanie na dane Bluetooth
     */
    @ExperimentalUnsignedTypes
    fun beginListenForData() {
        val mWorkerThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val bytesAvailable = mInputStream?.available()
                    if (bytesAvailable != null) {
                        if (bytesAvailable > 0) { // Dane odebrane
                            val packetBytes = ByteArray(bytesAvailable)
                            mInputStream?.read(packetBytes)

                            /**
                             * Obsługa bufora
                             */
                            val s = String(packetBytes,Charsets.UTF_8)
                            putTxt.postValue(s)

                            /**
                             * Obsługa pojedynczego bajtu
                             */
                            for (i in 0 until bytesAvailable) {
                                val b = packetBytes[i]
                                Log.d("inputData", String.format("%02x", b))
                            }
                        }
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        // Rozpoczęcie wątku nasłuchującego na dane
        mWorkerThread.start()
    }
}
