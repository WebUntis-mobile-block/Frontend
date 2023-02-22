# Frontend

This is the frontend code for WebUntis User block app, implemented using Android Studio and Kotlin.
## MainActivity

This activity displays a start button that, when clicked, launches the SchoolActivity.
## SchoolActivity

This activity displays a list of schools retrieved from the server at http://pfefan.ddns.net:5000/api/schools. The user can select a school from the list or enter a school ID in the input field to save it. The selected school is saved to the server at http://pfefan.ddns.net:5000/api/school. If the save is successful, the app launches the UserActivity.
## UserActivity

This activity displays an input field for the user's name and a submit button. When the user clicks the submit button, their name is saved to the server at http://pfefan.ddns.net:5000/api/user. If the save is successful, the app sends a block request to the server and displays the response message in a toast notification. Finally, the app returns to the MainActivity.
## VolleySingleton

This is a singleton class that handles the network requests using Volley library. It is used by the SchoolActivity and UserActivity to make requests to the server.
