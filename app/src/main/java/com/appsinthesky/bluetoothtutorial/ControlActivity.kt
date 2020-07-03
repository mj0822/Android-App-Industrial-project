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
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*

var i: Int= 0

class SharedPreference(val context: Context) : AppCompatActivity(){
    val sharedPref: SharedPreferences = context.getSharedPreferences("Name", Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString(KEY_NAME, value)

        editor.commit()

    }
    fun getValueString(KEY_NAME: String): String? {

        return sharedPref.getString(KEY_NAME, "")

    }
    fun checkKey(KEY_NAME: String): Boolean {
        sharedPref.contains(KEY_NAME)
            return true
    }

}
class ControlActivity: AppCompatActivity() {
    var list: ArrayList<String> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>




    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String


    }

    override fun onCreate(savedInstanceState: Bundle?) {

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()
        val number = findViewById<EditText>(R.id.input)
        val text = number.text



        val sharedPreference:SharedPreference = SharedPreference(this)


        fun isNullOrEmpty(str: String?): Boolean {
            Log.d("TAG",str)
            if (str != null && !str.isEmpty())
                return false
            return true
        }
        i=1

        while(true) {
            if(sharedPreference.checkKey(i.toString())) {
                Log.d("TAG", "found key")
                val item1: String = sharedPreference.getValueString(i.toString())!!
                if (isNullOrEmpty(item1)) {
                    Log.d("TAG", "inside null wala if")
                    break
                }
                Log.d("TAG", "IF MAI NHIIIIII GHUSA")
                val info = findViewById<ListView>(R.id.listView)
                list.add(item1.toString())
//                input.setText("")
//                arrayAdapter.notifyDataSetChanged()
//                listView.adapter = arrayAdapter

            }
            else{
                Log.d("TAG","KEY NOT FOUND")
                Log.d("TAG",i.toString())
                break
            }
            i = i + 1
        }

        show_info.setOnClickListener{
//            Log.d("TAG","i KA Value"+i.toString())

//            if (sharedPreference.getValueString(i.toString())!=null) {
//                val item1: String = sharedPreference.getValueString(i.toString())!!
//                list.add(item1.toString())
                arrayAdapter.notifyDataSetChanged()
                listView.adapter = arrayAdapter

//                i=i+1
//                val info = findViewById<ListView>(R.id.listView)
////              number.hint = sharedPreference.getValueString(count2.toString())!!
//                val item = sharedPreference.getValueString(i.toString())!!
//                list.add(i.toString() + ".    " +item)
//                input.setText("")
//                arrayAdapter.notifyDataSetChanged()
//                listView.adapter = arrayAdapter
                Log.d("TAG","INSIDEIF")



        }
        set_button.setOnClickListener {

            sendCommand(text.toString())
            sharedPreference.save(i.toString(),text.toString())
//            Log.d("TAG", "$i =itext is $text")
            if (sharedPreference.getValueString(i.toString())!=null) {
                val item1: String = sharedPreference.getValueString(i.toString())!!
                list.add(item1)
                Toast.makeText(this,"i is"+i.toString()+"text is"+text.toString(),Toast.LENGTH_SHORT).show()

                i = i + 1

            }

            else {
                number.hint = "NO value found"
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