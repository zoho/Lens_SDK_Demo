package com.zoho.lens

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.zoho.assistrtc.*
import com.zoho.lens.databinding.ActivityLensBinding
import com.zoho.webrtc.AppRTCAudioManager
import kotlinx.android.synthetic.main.activity_lens.*


class LensSample : AppCompatActivity() {


    internal var changedMuteSelf: Boolean = false

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LensSDK.onCreate(this)
        val viewDataBinding = DataBindingUtil.setContentView<ActivityLensBinding>(this, R.layout.activity_lens)
        val result = viewDataBinding.setVariable(BR.lensSessionViewModel, ViewModelProviders.of(this).get(LensViewModel::class.java))
        if (!result) {
            throw RuntimeException("ViewModel variable not set. Check the types")
        }
        viewDataBinding.executePendingBindings()
          var isAR: Boolean=false
        var sdkToken: String = ""
        val sessionKey = intent.getStringExtra("sessionKey")
        val baseUrl = intent.getStringExtra("baseUrl")
        if (intent.hasExtra("sdkToken")) {
            sdkToken = intent.getStringExtra("sdkToken") as String
        }
        if (intent.hasExtra("isAR")) {
            isAR = intent.getBooleanExtra("isAR",false) as Boolean
        }

        LensSDK.setCallbackListener(SessionCallbacks(this))
            .setARMode(isAR)
            .setWebRTCRenderView(render_view)
            .startSession(sessionKey = sessionKey, sdkToken = sdkToken, baseUrl = baseUrl)


        clear_annotations.setOnClickListener {
            LensSDK.clearAllAnnotations()
        }


        swap_camera.setOnClickListener {
            LensSDK.onSwapCamera()
        }

        close.setOnClickListener {
            LensSDK.closeSession()
            onClosedSession()
        }


        mute_unmute_self.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changedMuteSelf = true
                LensSDK.muteAudio(true)
            } else {
                changedMuteSelf = true
                LensSDK.muteAudio(false)
            }
        }

        speaker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //speaker on
                LensSDK.switchAudioMode(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE)
            } else {
                LensSDK.switchAudioMode(AppRTCAudioManager.AudioDevice.EARPIECE)
            }
        }

        video.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                LensSDK.unMuteVideoStreaming()
            } else {
                LensSDK.muteVideoStreaming()

            }
        }
    }


    override fun onPause() {
        super.onPause()
        LensSDK.onPause()
    }

    override fun onResume() {
        super.onResume()
        LensSDK.onConfigARMode()
        LensSDK.onResume()
    }

    override fun onStart() {
        super.onStart()
        LensSDK.onStart()
    }

    override fun onStop() {
        super.onStop()
        LensSDK.onStop()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LensSDK.onConfigurationChanged(newConfig)
    }

    internal fun onClosedSession() {
        val intentMain = Intent(this, MainActivity::class.java)
        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentMain)
        finish()
    }


}
