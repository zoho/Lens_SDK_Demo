package com.zoho.lens

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zoho.assistrtc.LensSDK
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    var TAG = MainActivity::class.java.canonicalName as String
    var grid: String = ""
    private lateinit var dialog: AlertDialog

    var API_BASE_URL = "https://lens.zoho.com"
    var DEFAULT_SESSION_KEY = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val edittext = findViewById<EditText>(R.id.key_edittext)
        val baseUrlEdittext = findViewById<EditText>(R.id.server_edittext)
        val sdkTokenText =  findViewById<EditText>(R.id.sdktoken_edittext)

        edittext.setText(DEFAULT_SESSION_KEY)
        sdkTokenText.setText("")

        baseUrlEdittext.setText(API_BASE_URL)

        ok_button.setOnClickListener {
            API_BASE_URL = baseUrlEdittext.text.toString()
            val intent = Intent(this@MainActivity, LensSample::class.java)
            intent.putExtra("sessionKey", key_edittext.text.toString())
            intent.putExtra("baseUrl", API_BASE_URL)
            intent.putExtra("sdkToken", sdkTokenText.text.toString())
            startActivity(intent)
        }
        if(ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            checkPermission(Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE);
        }

    }

    fun checkPermission(permission: String, requestCode: Int) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
    private val CAMERA_PERMISSION_CODE = 100
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super
            .onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                // Showing the toast message
                Toast.makeText(
                    this@MainActivity,
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Camera Permission Denied",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun askForPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    permission
                )
            ) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        } else {
            Toast.makeText(this, "$permission is already granted.", Toast.LENGTH_SHORT)
                .show()
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
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT)
                .show()

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
