package com.programmalar.pdpgramm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.programmalar.pdpgramm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding
    private val referenceOnline = FirebaseDatabase.getInstance().getReference("isOnline")
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
    }

    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }


    override fun onStop() {
        if (currentUser != null) {
            referenceOnline.child(currentUser.uid).setValue(0)
        }
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if (currentUser != null) {
            referenceOnline.child(currentUser.uid).setValue(1)
        }
    }
}