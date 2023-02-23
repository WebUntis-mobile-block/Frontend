package pfefan.at

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class SchoolActivity : AppCompatActivity() {

    private lateinit var schoolListView: ListView
    private lateinit var schoolAdapter: ArrayAdapter<String>
    private lateinit var schoolList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school)

        schoolListView = findViewById(R.id.school_listview)
        schoolList = mutableListOf()
        schoolAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, schoolList)
        schoolListView.adapter = schoolAdapter

        val submitBtn = findViewById<Button>(R.id.submit_school_btn)
        val schoolIdInput = findViewById<EditText>(R.id.school_id_input)

        getSchools()

        schoolListView.setOnItemClickListener { parent, view, position, id ->
            val selectedSchool = schoolList[position]
            saveSchoolHandler(selectedSchool)
        }

        submitBtn.setOnClickListener {
            val school = schoolIdInput.text.toString()
            saveSchoolHandler(school)
        }
    }

    private fun saveSchoolHandler(school: String) {
        if (school.isNotEmpty()) {
            saveSchool(school) { response ->
                if (response != null && response != "An exeption has accured") {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            val message = "You need to enter a school ID"
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
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

class VolleySingleton private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also { INSTANCE = it }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}