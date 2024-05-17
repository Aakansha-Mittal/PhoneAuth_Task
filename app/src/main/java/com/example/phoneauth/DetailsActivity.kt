package com.example.phoneauth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class DetailsActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_details)

        var etName = findViewById<EditText>(R.id.etName)
        var etMail = findViewById<EditText>(R.id.etMail)
        var btnNext = findViewById<Button>(R.id.btnNext)

        auth = FirebaseAuth.getInstance()

        var user = Firebase.auth.currentUser

        val id = user?.uid

        btnNext.setOnClickListener {
            var name = etName.text.toString()
            var mail = etMail.text.toString().trim()

            if(user!=null ) {

                user.updateEmail(mail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User email address updated")
                        } else {
                            Log.d(TAG, "Usser email address not updated")
                        }
                    }

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User name updated")
                            val intent = Intent(this@DetailsActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.w(TAG, "Failed to update name", task.exception)
                        }
                    }

                val firestore = FirebaseFirestore.getInstance()
                val userMap = hashMapOf(
                    "name" to name ,
                    "mail" to mail
                )

                firestore.collection("users").document(user.uid).set(userMap)
                    .addOnSuccessListener {
                        Log.d("DetailsActivity", "User details successfully written to Firestore")
                        val intent = Intent(this@DetailsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Log.w("DetailsActivity", "Error writing user details")
                    }
            }
            else {
                Toast.makeText(this, "User does not exist ", Toast.LENGTH_SHORT).show()
            }

        }
    }
}