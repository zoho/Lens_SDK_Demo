package com.zoho.lens.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProviders
import com.zoho.lens.LensSDK
import com.zoho.lens.UserInfo
import com.zoho.lens.demo.databinding.ActivityLensBinding
import com.zoho.webrtc.AppRTCAudioManager
import kotlinx.android.synthetic.main.activity_lens.*


class LensSample : AppCompatActivity() {
    private var sessionKey: String? = null
    private var mailId: String? = ""
    var changedMuteSelf: Boolean = false
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LensSDK.onCreate(this)
        val viewDataBinding = DataBindingUtil.setContentView<ActivityLensBinding>(this, R.layout.activity_lens)
        val result = viewDataBinding.setVariable(BR.viewmodel, ViewModelProviders.of(this).get(LensViewModel::class.java))
        if (!result) {
            throw RuntimeException("ViewModel variable not set. Check the types")
        }
        viewDataBinding.executePendingBindings()

        viewDataBinding.executePendingBindings()
        viewDataBinding.executePendingBindings()
        var isAR: Boolean=false
        var sdkToken: String = ""
        sessionKey = intent.getStringExtra("sessionKey")
        if (intent.hasExtra("sdkToken")) {
            sdkToken = intent.getStringExtra("sdkToken") as String
        }

        if (intent.hasExtra("isAR")) {
            isAR = intent.getBooleanExtra("isAR",false)
        }


        /**
         * Set callback listener for the session
         */
        LensSDK.setCallbackListener(SessionCallbacks(this))
            //Optional, Set whether the AR mode enable or disable for .
            .setARMode(isAR)
            //Optional,  Enable or disable high quality resolution for AR session
            /**
             * Set the current best knowledge of a real-world planar surface and detect the objects.
             */
            // To add the rendering  view to display local and remote views
            .addStreamingView(stream_view)
            //Optional,  Update customer info
            .setUserInfo(UserInfo("Client Name",mailId))
            //  sessionkey to start the valid session
            //  Required, Set the sdkToken to be used for validation
            //  Required, Set the authToken to be used for technician validation
            .startSession(sessionKey = sessionKey )



        red.setOnClickListener {
            LensSDK.setAnnotationColor("#FF0000")
        }
        green.setOnClickListener {
            LensSDK.setAnnotationColor("#008000")

        }
        yellow.setOnClickListener {
            LensSDK.setAnnotationColor("#FFFF00")
        }
        orange.setOnClickListener {
            LensSDK.setAnnotationColor("#00FF00")
        }

        swap_camera.setOnClickListener {
            LensSDK.onSwapCamera()
        }

        close.setOnClickListener {
            LensSDK.closeSession()
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
            LensSDK.muteVideo(!isChecked)
        }

        speaker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LensSDK.switchAudioMode(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE)
            } else {
                LensSDK.switchAudioMode(AppRTCAudioManager.AudioDevice.EARPIECE)
            }
        }

        video.setOnCheckedChangeListener { buttonView, isChecked ->
            LensSDK.muteVideo(isChecked)
            if (isChecked) {
//                LensSDK.unMuteVideoStreaming()
            } else {
//                LensSDK.muteVideoStreaming()
            }
        }



    }

    override fun onPause() {
        super.onPause()
        LensSDK.onPause()
    }

    override fun onResume() {
        super.onResume()
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

    internal fun onClosedSession() {
        val intentMain = Intent(this, MainActivity::class.java)
        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentMain)
        finish()
    }

}

