package com.appsinthesky.bluetoothtutorial


import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*

class SharedPreference(val context: Context) : AppCompatActivity(){
    val sharedPref: SharedPreferences = context.getSharedPreferences("Name", Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString(KEY_NAME, value)

        editor.commit()
    }
    fun getValueString(KEY_NAME: String): String? {

        return sharedPref.getString(KEY_NAME, null)
    }
}
class ControlActivity: AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        val i:Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()
        val number = findViewById<EditText>(R.id.input)
        val text = number.text

        val sharedPreference:SharedPreference = SharedPreference(this)
        set_button.setOnClickListener {

            Toast.makeText(this," entered text is " + text , Toast.LENGTH_LONG).show()
            sendCommand(text.toString())

            val count :Int = i+1


            sharedPreference.save(count.toString(),text.toString())
        }
        //control_led_off.setOnClickListener { sendCommand("b")
        show_info.setOnClickListener {
            if (sharedPreference.getValueString("1")!=null) {
                number.hint = sharedPreference.getValueString("1")!!
                Toast.makeText(this@ControlActivity,"Data Retrieved",Toast.LENGTH_SHORT).show()
            }else{
                number.hint="NO value found"
            }
        }

    }


//    textView.setText("string"). toString()
//    val textViewValue = textView.text

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())


            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }
}