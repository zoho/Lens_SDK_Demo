package com.zoho.lens.demo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.zoho.lens.AudioDevice
import com.zoho.lens.LensSDK
import com.zoho.lens.UserInfo
import com.zoho.lens.chatLet.ChatLetState
import com.zoho.lens.demo.databinding.ActivityLensBinding
import com.zoho.lens.handler.QRHandler
import kotlinx.android.synthetic.main.activity_lens.chat_button
import kotlinx.android.synthetic.main.activity_lens.clear_all_annotation
import kotlinx.android.synthetic.main.activity_lens.close
import kotlinx.android.synthetic.main.activity_lens.green
import kotlinx.android.synthetic.main.activity_lens.mute_unmute_self
import kotlinx.android.synthetic.main.activity_lens.ocr_button
import kotlinx.android.synthetic.main.activity_lens.orange
import kotlinx.android.synthetic.main.activity_lens.qr_button
import kotlinx.android.synthetic.main.activity_lens.red
import kotlinx.android.synthetic.main.activity_lens.share_camera
import kotlinx.android.synthetic.main.activity_lens.speaker
import kotlinx.android.synthetic.main.activity_lens.stream_view
import kotlinx.android.synthetic.main.activity_lens.swap_camera
import kotlinx.android.synthetic.main.activity_lens.undo_annotation
import kotlinx.android.synthetic.main.activity_lens.video
import kotlinx.android.synthetic.main.activity_lens.yellow
import kotlinx.android.synthetic.main.activity_lens.zoom
import kotlinx.android.synthetic.main.activity_lens.zoom_seekbar


class LensSample : AppCompatActivity() {
    private var sessionKey: String? = null
    private var mailId: String? = ""
    var changedMuteSelf: Boolean = false
    val chatFragment = ChatFragment.newInstance()
    val chatLetState = MutableLiveData<ChatLetState>()
    private lateinit var liveTextResultFragment: LiveTextResultFragment
    var isFreezeVideo = false

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LensSDK.onCreate(this)
        val viewDataBinding = DataBindingUtil.setContentView<ActivityLensBinding>(this, R.layout.activity_lens)
        val result = viewDataBinding.setVariable(BR.viewmodel, ViewModelProviders.of(this)[LensViewModel::class.java])
        if (!result) {
            throw RuntimeException("ViewModel variable not set. Check the types")
        }
        viewDataBinding.executePendingBindings()

        var isAR = false
        sessionKey = intent.getStringExtra("sessionKey")
        val baseUrl = intent.getStringExtra("baseUrl")

        if (intent.hasExtra("isAR")) {
            isAR = intent.getBooleanExtra("isAR",false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * Set callback listener for the session
         */
        LensSDK.setCallbackListener(SessionCallbacks(this))
            //Optional, Set whether the AR mode enable or disable for .
            .setARMode(isAR)
            //Optional,  Enable or disable high quality resolution for AR session
            .setResolution(true)
            /**
             * Set the current best knowledge of a real-world planar surface and detect the objects.
             */
            // To add the rendering  view to display local and remote views
            .addStreamingView(stream_view)
            //Optional,  Update customer info
            .setUserInfo(UserInfo("Client Name",mailId))
            //  session key to start the valid session
            //  Required, Set the sdkToken to be used for validation
            //  Required, Set the authToken to be used for technician validation
            .startSession(sessionKey = sessionKey, baseUrl= baseUrl)


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
                LensSDK.switchAudioMode(AudioDevice.SPEAKER_PHONE)
            } else {
                LensSDK.switchAudioMode(AudioDevice.EARPIECE)
            }
        }

        video.setOnCheckedChangeListener { _, isChecked ->
            LensSDK.muteVideo(isChecked)
        }

        ocr_button.setOnClickListener {
            LensSDK.requestOCR()
        }

        qr_button.setOnClickListener {
            LensSDK.requestQR(isSilent = false, shouldContinuouslyRetry = QRHandler.RetryMode.RETRY_CONTINOUSLY)
        }

        share_camera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LensSDK.shareCameraRequest()
            } else {
                LensSDK.stopCameraRequest()
            }
        }

        zoom.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                zoom_seekbar.visibility = View.VISIBLE
            } else {
                zoom_seekbar.visibility = View.GONE
            }
        }

        undo_annotation.setOnClickListener {
            LensSDK.undoAnnotation()
        }

        clear_all_annotation.setOnClickListener {
            LensSDK.clearAllAnnotations()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            zoom_seekbar.min = 0
        }

        zoom_seekbar.max = 100
        zoom_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                LensSDK.setZoomPercentage(progress.toFloat()/100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        chat_button.setOnClickListener {
            if (!chatFragment.isAdded) {
                chatFragment.show(supportFragmentManager, "Chat")
            }
        }

        chatLetState.observe(this) { state ->
            state?.let {
                when (it) {
                    ChatLetState.SUCCESS -> {
                        chat_button.visibility = View.VISIBLE
                    }
                    ChatLetState.ERROR, ChatLetState.LOADING -> {
                        chat_button.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun onLiveTextScanCompleted(resultText: String) {
        val chatMessage = resultText.trim()
        val args = Bundle().apply { putString("chatMessage", chatMessage) }
        liveTextResultFragment = LiveTextResultFragment.newInstance(ScanType.OCR)
        liveTextResultFragment.arguments = args
        if (!liveTextResultFragment.isAdded) {
            liveTextResultFragment.show(this.supportFragmentManager, "OCR Result")
        }
    }

    fun onQRScanCompleted(resultText: String) {
        val chatMessage = resultText.trim()
        val args = Bundle().apply { putString("chatMessage", chatMessage) }
        liveTextResultFragment = LiveTextResultFragment.newInstance(ScanType.QR)
        liveTextResultFragment.arguments = args
        if (!liveTextResultFragment.isAdded) {
            liveTextResultFragment.show(this.supportFragmentManager, "QR Result")
        }
    }

    fun onChatLetReverted(status:Boolean) {
        if (status) {
            if (chatFragment.isAdded) {
                chatFragment.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
            chat_button.visibility = View.GONE
        } else {
            chat_button.visibility = View.VISIBLE
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

    override fun onBackPressed() {
        super.onBackPressed()
        LensSDK.closeSession()
    }

    internal fun onClosedSession() {
        val intentMain = Intent(this, MainActivity::class.java)
        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentMain)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uris = mutableListOf<Uri>()
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val item = clipData.getItemAt(i)
                    uris.add(item.uri)
                }
            } else {
                val uri = data?.data
                if (uri != null) {
                    uris.add(uri)
                }
            }
            LensSDK.attachFileCallBackForChat(uris.toTypedArray())
        } else {
            LensSDK.attachFileCallBackForChat(null)
        }
    }
}

