package pfefan.at

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val submitbtn = findViewById<Button>(R.id.submituserbtn)
        val username_input = findViewById<EditText>(R.id.usernameInput)

        submitbtn.setOnClickListener {
            saveUsername(username_input.text.toString()) { response ->
                if (response != null && response != "An exeption has accured") {
                    sendblock() { response ->
                        if (response != null && response != "An exeption has accured") {
                            val responseJson = JSONObject(response)
                            val message = responseJson.getString("result")
                            val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun saveUsername(username: String, callback: (String?) -> Unit) {
        val url = "http://pfefan.ddns.net:5000/api/user"
        val jsonBody = JSONObject()
        jsonBody.put("username", username)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                callback(response.toString())
            },
            { error ->
                callback(null)
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun sendblock(callback: (String?) -> Unit) {
        val url = "http://pfefan.ddns.net:5000/api/block"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                callback(response.toString())
            },
            { error ->
                callback(null)
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}