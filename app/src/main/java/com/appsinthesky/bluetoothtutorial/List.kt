import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.appsinthesky.bluetoothtutorial.R
import kotlinx.android.synthetic.main.activity_list.*

class List : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var button: Button
    lateinit var listView: ListView
    var list: ArrayList<String> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        title = "Kotlin"
        listView = findViewById(R.id.listView)
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.btnAdd)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        button.setOnClickListener {
            list.add(editText.text.toString())
            editText.setText("")
            arrayAdapter.notifyDataSetChanged()
            listView.adapter = arrayAdapter
        }

//        val count = arrayOf("japan","india")
//        val listview = findViewById<ListView>(R.id.list_view)
//
//        listview.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, count)

    }
}