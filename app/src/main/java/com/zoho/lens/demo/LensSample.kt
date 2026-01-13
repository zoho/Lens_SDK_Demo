package com.zoho.lens.demo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.example.arcore_assistrtc.enums.NodeType
import com.example.arcore_assistrtc.enums.TrackingFailureReason
import com.zoho.canvasview.AnnotationType
import com.zoho.lens.ARAnchorPlacedListenerForCustomer
import com.zoho.lens.LensSDK
import com.zoho.lens.UserInfo
import com.zoho.lens.chatLet.ChatLetState
import com.zoho.lens.demo.databinding.ActivityLensBinding
import com.zoho.lens.handler.QRHandler
import com.zoho.webrtc.AppRTCAudioManager


class LensSample : AppCompatActivity() {
    private var sessionKey: String? = null
    private var mailId: String? = "Customer"
    private var userName: String = "Customer"
    var changedMuteSelf: Boolean = false
    val chatFragment = ChatFragment.newInstance()
    val chatLetState = MutableLiveData<ChatLetState>()
    private lateinit var liveTextResultFragment: LiveTextResultFragment
    var isMuteVideo = false
    lateinit var viewDataBinding: ActivityLensBinding
    var isUpstream = true
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LensSDK.onCreate(this)
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_lens)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewDataBinding.let { binding ->
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _: View?, insets: WindowInsetsCompat ->
                val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.navigationBars())
                val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val dp16 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
                val dp08 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 08f, resources.displayMetrics).toInt()
                viewDataBinding.buttonLayout.updatePadding(left = systemBar.left, top = systemBar.top, right = systemBar.right)
                insets
            }
        }

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
            /**
             * Set the current best knowledge of a real-world planar surface and detect the objects.
             */
            // To add the rendering  view to display local and remote views
            .addStreamingView(viewDataBinding.streamView)
            //Optional,  Enable or disable high quality resolution for AR session
            .setPointCloud(false)
            .setPlaneDetection(false)
            //Optional,  Update customer info
            .setUserInfo(UserInfo(userName ,mailId))
            //  session key to start the valid session
            //  Required, Set the sdkToken to be used for validation
            //  Required, Set the authToken to be used for technician validation
            .startSession(sessionKey = sessionKey, baseUrl= baseUrl)

        if (!LensSDK.isARMode()) {
            viewDataBinding.zoom.visibility = View.GONE
            viewDataBinding.undoAnnotation.visibility = View.GONE
            viewDataBinding.clearAllAnnotation.visibility = View.GONE
        }

        viewDataBinding.red.setOnClickListener {
            LensSDK.setAnnotationColor("#FF0000")
        }

        viewDataBinding.green.setOnClickListener {
            LensSDK.setAnnotationColor("#008000")
        }

        viewDataBinding.yellow.setOnClickListener {
            LensSDK.setAnnotationColor("#FFFF00")
        }

        viewDataBinding.orange.setOnClickListener {
            LensSDK.setAnnotationColor("#00FF00")
        }

        viewDataBinding.swapCamera.setOnClickListener {
            LensSDK.onSwapCamera()
        }

        viewDataBinding.close.setOnClickListener {
            LensSDK.closeSession()
            onClosedSession()
        }

        viewDataBinding.muteUnmuteSelf.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changedMuteSelf = true
                LensSDK.muteAudio(true)
            } else {
                changedMuteSelf = true
                LensSDK.muteAudio(false)
            }
        }

        viewDataBinding.speaker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //speaker on
                LensSDK.switchAudioMode(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE)
            } else {
                LensSDK.switchAudioMode(AppRTCAudioManager.AudioDevice.EARPIECE)
            }
        }

        viewDataBinding.video.setOnClickListener {
            isMuteVideo = !isMuteVideo
            LensSDK.onFreezeUnfreezeVideo(isMuteVideo)
        }

        viewDataBinding.ocrButton.setOnClickListener {
            LensSDK.requestOCR()
        }

        viewDataBinding.qrButton.setOnClickListener {
            LensSDK.requestQR(isSilent = false, shouldContinuouslyRetry = QRHandler.RetryMode.RETRY_CONTINOUSLY)
        }

        viewDataBinding.shareCamera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LensSDK.shareCameraRequest()
            } else {
                LensSDK.stopCameraRequest()
            }
        }

        viewDataBinding.zoom.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewDataBinding.zoomSeekbar.visibility = View.VISIBLE
            } else {
                viewDataBinding.zoomSeekbar.visibility = View.GONE
            }
        }

        viewDataBinding.undoAnnotation.setOnClickListener {
            LensSDK.undoAnnotation()
        }

        viewDataBinding.clearAllAnnotation.setOnClickListener {
            LensSDK.clearAllAnnotations()
        }

        viewDataBinding.flashLight.setOnClickListener {
            LensSDK.setFlashOnOff()
        }

        viewDataBinding.resolution.setOnClickListener {
            LensSDK.setResolution(!LensSDK.getResolution())
            if (LensSDK.getResolution()) {
                viewDataBinding.resolution.text = "HD"
            } else {
                viewDataBinding.resolution.text = "Non HD"
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewDataBinding.zoomSeekbar.min = 0
        }

        viewDataBinding.zoomSeekbar.max = 100
        viewDataBinding.zoomSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                LensSDK.setZoomPercentage(progress.toFloat()/100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        viewDataBinding.chatButton.setOnClickListener {
            if (!chatFragment.isAdded) {
                chatFragment.show(supportFragmentManager, "Chat")
            }
        }

        viewDataBinding.annotation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewDataBinding.annotationScrollView.visibility = View.VISIBLE
                if (isUpstream) {
                    if (LensSDK.isARMode()) {
                        viewDataBinding.arrow.visibility = View.VISIBLE
                        viewDataBinding.pencil.visibility = View.VISIBLE
                        viewDataBinding.arPointer.visibility = View.VISIBLE
                        viewDataBinding.arMeasure.visibility = View.VISIBLE
                    } else {
                        viewDataBinding.pointCloud.visibility = View.GONE
                        viewDataBinding.planeDetection.visibility = View.GONE
                        viewDataBinding.arrow.visibility = View.GONE
                        viewDataBinding.pencil.visibility = View.GONE
                        viewDataBinding.arPointer.visibility = View.GONE
                        viewDataBinding.arMeasure.visibility = View.GONE
                        viewDataBinding.arMeasureIcon.visibility = View.GONE
                        viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
                    }
                    viewDataBinding.rectangle.visibility = View.GONE
                    viewDataBinding.ellipse.visibility = View.GONE
                } else {
                    viewDataBinding.pointCloud.visibility = View.GONE
                    viewDataBinding.planeDetection.visibility = View.GONE
                    if (LensSDK.isARMode()) {
                        viewDataBinding.arrow.visibility = View.VISIBLE
                        viewDataBinding.arPointer.visibility = View.VISIBLE
                        viewDataBinding.arMeasure.visibility = View.GONE
                        viewDataBinding.arMeasureIcon.visibility = View.GONE
                        viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
                    }
                    viewDataBinding.pencil.visibility = View.VISIBLE
                    viewDataBinding.rectangle.visibility = View.VISIBLE
                    viewDataBinding.ellipse.visibility = View.VISIBLE
                }
            } else {
                viewDataBinding.annotationScrollView.visibility = View.GONE
            }
        }

        viewDataBinding.arrow.setOnClickListener {
            if (LensSDK.isARMode()) {
                LensSDK.setArDrawingStyle(NodeType.ARROW)
            }
            LensSDK.setAnnotationDrawStyle(AnnotationType.ARROW, "#FF004E", 5f)
            viewDataBinding.arMeasureIcon.visibility = View.GONE
            viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
        }

        viewDataBinding.pencil.setOnClickListener {
            if (LensSDK.isARMode()) {
                LensSDK.setArDrawingStyle(NodeType.PENCIL)
            }
            LensSDK.setAnnotationDrawStyle(AnnotationType.PENCIL, "#FF004E", 5f)
            viewDataBinding.arMeasureIcon.visibility = View.GONE
            viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
        }

        viewDataBinding.rectangle.setOnClickListener {
            if (LensSDK.isARMode()) {
                LensSDK.setArDrawingStyle(NodeType.RECTANGLE)
            }
            LensSDK.setAnnotationDrawStyle(AnnotationType.RECTANGLE, "#FF004E", 5f)
            viewDataBinding.arMeasureIcon.visibility = View.GONE
            viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
        }

        viewDataBinding.ellipse.setOnClickListener {
            if (LensSDK.isARMode()) {
                LensSDK.setArDrawingStyle(NodeType.ELLIPSE)
            }
            LensSDK.setAnnotationDrawStyle(AnnotationType.ELLIPSE, "#FF004E", 5f)
            viewDataBinding.arMeasureIcon.visibility = View.GONE
            viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
        }

        viewDataBinding.arMeasure.setOnClickListener {
            LensSDK.setArDrawingStyle(NodeType.MEASURE)
            viewDataBinding.arMeasureIcon.visibility = View.VISIBLE
            viewDataBinding.arMeasureCenterAnchor.visibility = View.VISIBLE
        }

        viewDataBinding.arPointer.setOnClickListener {
            if (LensSDK.isARMode()) {
                LensSDK.setArDrawingStyle(NodeType.POINTER)
            }
            LensSDK.setAnnotationDrawStyle(AnnotationType.POINTER, "#FF004E", 5f)
            viewDataBinding.arMeasureIcon.visibility = View.GONE
            viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
        }

        viewDataBinding.planeDetection.setOnCheckedChangeListener { _, isChecked ->
            LensSDK.setPlaneDetection(isChecked)
        }

        viewDataBinding.pointCloud.setOnCheckedChangeListener { _, isChecked ->
            LensSDK.setPointCloud(isChecked)
        }

        viewDataBinding.arMeasureIcon.setOnClickListener {
            LensSDK.onTapForArMeasurement()
        }

        chatLetState.observe(this) { state ->
            state?.let {
                when (it) {
                    ChatLetState.SUCCESS -> {
                        viewDataBinding.chatButton.visibility = View.VISIBLE
                    }
                    ChatLetState.ERROR, ChatLetState.LOADING -> {
                        viewDataBinding.chatButton.visibility = View.GONE
                    }
                }
            }
        }

        LensSDK.setArPlacingListenerForCustomer(object : ARAnchorPlacedListenerForCustomer {
            override fun anchorPlaced() {

            }

            override fun arPointerPlaced() {

            }

            override fun anchorPlacingFailed(trackingFailureReason: TrackingFailureReason) {
                if (trackingFailureReason == TrackingFailureReason.ANNOTATION_NOT_AVAILABLE_WITH_CURRENT_LICENSE) {
                    Toast.makeText(this@LensSample, "Annotation available only in Standard / Professional edition. Upgrade to use.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun anchorRemoved() {

            }
        })
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
            viewDataBinding.chatButton.visibility = View.GONE
        } else {
            viewDataBinding.chatButton.visibility = View.VISIBLE
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
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
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

