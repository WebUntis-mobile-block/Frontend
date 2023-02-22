package pfefan.at

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class SchoolActivity : AppCompatActivity() {
    private lateinit var schoolListView: ListView
    private lateinit var schoolAdapter: ArrayAdapter<String>
    private lateinit var schoolList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school)

        schoolListView = findViewById(R.id.schoolListView)
        schoolList = mutableListOf()
        schoolAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, schoolList)
        schoolListView.adapter = schoolAdapter

        val submitbtn = findViewById<Button>(R.id.submitschoolbtn)
        val school_id_input = findViewById<EditText>(R.id.schoolIdInput)

        getSchools()

        schoolListView.setOnItemClickListener { parent, view, position, id ->
            val selectedSchool = schoolList[position]
            saveSchool(selectedSchool) { response ->
                if (response != null) {
                    println(response)
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        submitbtn.setOnClickListener {
            saveSchool(school_id_input.text.toString()) { response ->
                if (response != null || response != "An exeption has accured") {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getSchools() {
        val url = "http://pfefan.ddns.net:5000/api/schools"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val jsonArray = response.getJSONArray("schools")
                for (i in 0 until jsonArray.length()) {
                    val school = jsonArray.getString(i)
                    schoolList.add(school)
                }
                schoolAdapter.notifyDataSetChanged()
            },
            { error ->
                Log.d("SchoolListActivity", error.toString())
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun saveSchool(school: String, callback: (String?) -> Unit) {
        val url = "http://pfefan.ddns.net:5000/api/school"
        val jsonBody = JSONObject()
        jsonBody.put("school_name", school)

        val request = JsonObjectRequest(Request.Method.POST, url, jsonBody,
            { response ->
                callback(response.toString())
            },
            { error ->
                callback(null)
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}

class VolleySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}
