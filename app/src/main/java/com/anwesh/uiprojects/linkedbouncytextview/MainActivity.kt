package com.anwesh.uiprojects.linkedbouncytextview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.bouncytextview.BouncyTextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BouncyTextView.create(this)
    }
}
