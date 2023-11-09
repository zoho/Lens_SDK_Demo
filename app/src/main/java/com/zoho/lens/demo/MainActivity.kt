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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isAR: Boolean = false
    var TAG = MainActivity::class.java.canonicalName as String
    private lateinit var dialog: AlertDialog
    private var DEFAULT_SESSION_KEY = "371007523"

    private val cameraPermissions = Manifest.permission.CAMERA
    private val recAudioPermissions = Manifest.permission.RECORD_AUDIO
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val postNotifications = Manifest.permission.POST_NOTIFICATIONS
    @RequiresApi(Build.VERSION_CODES.S)
    val bluetoothConnect = Manifest.permission.BLUETOOTH_CONNECT

    private var cameraPermission = false
    private var micPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val edittext = findViewById<EditText>(R.id.key_edittext)

        edittext.setText(DEFAULT_SESSION_KEY)

        ar_support.setOnCheckedChangeListener { _, isChecked ->
            isAR =  isChecked
        }

        ok_button.setOnClickListener {
            if (cameraPermission && micPermission) {
                val intent = Intent(this@MainActivity, LensSample::class.java)
                intent.putExtra("sessionKey", key_edittext.text.toString())
                intent.putExtra("baseUrl", baseurl_edittext.text.toString())
                intent.putExtra("isAR", isAR)
                startActivity(intent) 
            } else {
                Toast.makeText(this, "Please grant camera and microphone permission", Toast.LENGTH_SHORT).show()
            }
        }
        
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(cameraPermissions, recAudioPermissions, postNotifications, bluetoothConnect)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(cameraPermissions, recAudioPermissions, bluetoothConnect)
        } else {
            arrayOf(cameraPermissions, recAudioPermissions)
        }

        val permissionsToRequest = permissions.filter {
            if (it == cameraPermissions && ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED) {
                cameraPermission = true
            }
            if (it == recAudioPermissions && ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED) {
                micPermission = true
            }

            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
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
