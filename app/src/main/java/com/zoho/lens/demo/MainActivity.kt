package com.zoho.lens.demo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zoho.lens.LensSDK
import kotlinx.android.synthetic.main.activity_main.ar_support
import kotlinx.android.synthetic.main.activity_main.baseurl_edittext
import kotlinx.android.synthetic.main.activity_main.key_edittext
import kotlinx.android.synthetic.main.activity_main.ok_button

class MainActivity : AppCompatActivity() {
    private var isAR: Boolean = false
    var TAG = MainActivity::class.java.canonicalName as String
    private lateinit var dialog: AlertDialog
    var DEFAULT_SESSION_KEY = "371007523"

    val CAMERA_PERMISSION = Manifest.permission.CAMERA
    val REC_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    val READ_PHONE_STATE_PERMISSION = Manifest.permission.READ_PHONE_STATE
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS
    @RequiresApi(Build.VERSION_CODES.S)
    val BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT

    var cameraPermission = false
    var micPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val edittext = findViewById<EditText>(R.id.key_edittext)

        edittext.setText(DEFAULT_SESSION_KEY)

        ar_support.setOnCheckedChangeListener { _, isChecked ->
            isAR =  isChecked
        }

        ok_button.setOnClickListener {
            val intent = Intent(this@MainActivity, LensSample::class.java)
            intent.putExtra("sessionKey", key_edittext.text.toString())
            intent.putExtra("baseUrl", baseurl_edittext.text.toString())
            intent.putExtra("isAR", isAR)
            startActivity(intent)
        }

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(CAMERA_PERMISSION, REC_AUDIO_PERMISSION, POST_NOTIFICATIONS, BLUETOOTH_CONNECT)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(CAMERA_PERMISSION, REC_AUDIO_PERMISSION, BLUETOOTH_CONNECT)
        } else {
            arrayOf(CAMERA_PERMISSION, REC_AUDIO_PERMISSION)
        }

        val permissionsToRequest = permissions.filter {
            if (it == CAMERA_PERMISSION && ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED) {
                cameraPermission = true
            }
            if (it == REC_AUDIO_PERMISSION && ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED) {
                micPermission = true
            }
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 0)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            permissions.forEachIndexed { index, s ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    if (s == Manifest.permission.CAMERA) {
                        cameraPermission = true
                    }
                    if (s == Manifest.permission.RECORD_AUDIO) {
                        micPermission = true
                    }
                }
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    if (s == Manifest.permission.CAMERA) {
                        cameraPermission = false
                    }
                    if (s == Manifest.permission.RECORD_AUDIO) {
                        micPermission = false
                    }
                    Toast.makeText(this, "$s Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.show()
        }
        super.onBackPressed()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LensSDK.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        }
    }
}
