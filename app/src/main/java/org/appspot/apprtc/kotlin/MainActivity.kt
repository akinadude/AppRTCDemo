package org.appspot.apprtc.kotlin

import android.os.Bundle
import android.util.Log
import org.appspot.apprtc.R
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager?.let { fm ->
            val f = MainFragment.newInstance()
            fm.beginTransaction()
                    .add(R.id.fragment_container, f)
                    .commit()
        }

        Log.d("TAG", "Start")
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
            //todo can also be a call onBackPressed on a fragment instance.
        }
    }
}
