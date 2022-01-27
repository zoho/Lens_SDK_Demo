package com.zoho.lens.demo

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
import com.zoho.lens.LensSDK
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    private var isAR: Boolean = false
    var TAG = MainActivity::class.java.canonicalName as String
    private lateinit var dialog: AlertDialog
    var DEFAULT_SESSION_KEY = "371007523"
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
            intent.putExtra("sessionKey", key_edittext.text.toString())
            intent.putExtra("isAR", isAR)
            startActivity(intent)
        }
        
        checkPermission(arrayListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), CAMERA_PERMISSION_CODE);

    }

    fun checkPermission(permissions: ArrayList<String>, requestCode: Int) {

        // Checking if permission is not granted
        if (permissions.any { permission -> ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED }) {
            ActivityCompat.requestPermissions(this@MainActivity, permissions.toTypedArray(), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permissions already granted", Toast.LENGTH_SHORT).show()
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(
                    this@MainActivity,
                    "Permissions Granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Permissions Denied",
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
