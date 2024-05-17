package com.example.phoneauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        var tvName = findViewById<TextView>(R.id.tvName)
        var tvMail = findViewById<TextView>(R.id.tvMail)
        var tvPhone = findViewById<TextView>(R.id.tvPhone)
        var btnLocation = findViewById<Button>(R.id.btnLocation)

        if (user!=null) {
            val mail = user.email
            val name = user.displayName
            val phone = user.phoneNumber

            tvName.text = "Name : $name"
            tvMail.text = "Mail : $mail"
            tvPhone.text = "Phone : $phone"


            val userId = user.uid
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val emailFromFirestore = document.getString("mail")

                        // Display additional user details
                        tvName.text = "Name: $name"
                        tvMail.text = "Email: $emailFromFirestore"
                    } else {
                        Log.d("ProfileActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileActivity", "get failed with ", exception)
                }

        }
         else {
             Toast.makeText(this, "user don't exist for showing details", Toast.LENGTH_SHORT).show()
        }

        btnLocation.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}