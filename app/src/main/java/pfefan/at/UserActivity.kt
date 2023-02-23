package pfefan.at

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val submitbtn = findViewById<Button>(R.id.submituserbtn)
        val username_input = findViewById<EditText>(R.id.usernameInput)

        submitbtn.setOnClickListener {
            // Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            val username = username_input.text.toString()

            if (username.isNullOrBlank()) {
                val message = "You need to enter a username"
                val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                saveUsername(username) { response ->
                    if (response != null && response != "An exception has occurred") {
                        sendblock() { response ->
                            if (response != null && response != "An exception has occurred") {
                                val responseJson = JSONObject(response)
                                val message = responseJson.getString("result")
                                val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)

                                snackbar.addCallback(object : Snackbar.Callback() {
                                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                        if (event != DISMISS_EVENT_ACTION) {
                                            val intent = Intent(this@UserActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                })

                                snackbar.show()
                            } else {
                                Log.e("UserActivity", "Error sending block: $response")
                                val message = "An error occurred. Please try again later."
                                val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                                snackbar.show()
                            }
                        }
                    } else {
                        Log.e("UserActivity", "Error saving username: $response")
                        val message = "An error occurred. Please try again later."
                        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                        snackbar.show()
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