package com.zoho.lens.demo

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.arcore_assistrtc.enums.TrackingFailureReason
import com.example.arcore_assistrtc.enums.TrackingState
import com.zoho.lens.ArAnnotationObject
import com.zoho.lens.CameraFacing
import com.zoho.lens.ChatModel
import com.zoho.lens.DeviceLockState
import com.zoho.lens.ErrorType
import com.zoho.lens.Feature
import com.zoho.lens.FlashSupportStatus
import com.zoho.lens.FreezeActions
import com.zoho.lens.ISessionCallback
import com.zoho.lens.LensSDK
import com.zoho.lens.LensToast
import com.zoho.lens.LensType
import com.zoho.lens.OCRState
import com.zoho.lens.OCRStateModel
import com.zoho.lens.ParticipantStatus
import com.zoho.lens.StreamingType
import com.zoho.lens.chatLet.ChatLetState
import com.zoho.webrtc.AppRTCAudioManager

class SessionCallbacks(private val activity: LensSample) : ISessionCallback {

    override fun sessionConnectionState(state: LensType, error: ErrorType?, message: Any) {
        when (state) {

            /**
             * Handle the network state.
             */
            LensType.NO_NETWORK -> {
                Toast.makeText(activity, "Network disconnected.", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            /**
             * Handle session close if session failed
             */
            LensType.BELOW_MIN_API_LEVEL -> {
                Toast.makeText(activity, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            LensType.CONTEXT_NOT_AVAILABLE -> {
                Toast.makeText(activity, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            LensType.INVALID_SDK_TOKEN -> {
                Toast.makeText(activity, "Invalid SDK Token", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            LensType.INVALID_SESSION_KEY -> {
                Toast.makeText(activity, "Invalid Session Key", Toast.LENGTH_LONG).show()
                activity.finish()
            }

            /**
             * Handle the error results of validating the session.
             */
            LensType.ERROR -> {
                activity.runOnUiThread {
                    if (error == ErrorType.UPDATE_TO_LATEST_APP) {
                        Toast.makeText(activity, "An upgraded version of Zoho Lens is now available. To continue using Zoho Lens, kindly update the application from the PlayStore.", Toast.LENGTH_LONG).show()
                        activity.finish()
                    } else {
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish()
                    }
                }
            }

            /**
             * Handle the session connection’s state.
             */
            LensType.CLIENT_INVALID, LensType.CLOSED_SESSION, LensType.CUSTOMER_CLOSED_SESSION, LensType.DUPLICATE_CLIENT, LensType.SESSION_EXPIRED, LensType.SESSION_INVALID, LensType.TECHNICIAN_CLOSED_SESSION -> {
                /**
                 * Close the session here.
                 */
                activity.onClosedSession()
            }
            else -> {

            }
        }
    }

    /**
     * Callback used to handle the received messages.
     */
    override fun chatMessage(message: ChatModel) {
    }

    /**
     * Callback used to perform any operation whenever a screenshot is taken.
     */
    override fun onLensScreenShotTaken(displayName: String?) {
        activity.runOnUiThread {
            if (displayName != null && displayName != "") {
                Toast.makeText(activity, "Screenshot taken by $displayName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Screenshot taken", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /**
     * Callback used to handle the audio device change
     */
    override fun onMicroPhoneUsingByOtherApps() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Your microphone is being used by another app. Close any apps that are using the microphone and try again.", Toast.LENGTH_LONG).show()
            activity.finish()
        }
    }

    /**
     * Callback used to handle the logs
     */
    override fun log(tag: String, message: String) {
        Log.d(tag, message)
    }

    /**
     * Returns the activity
     */
    override fun getActivity(): Activity {
        return activity
    }

    /**
     * To update the list of technicians currently connected in the session along with the state.
     */
    override fun updateTechnicianList(technicians: ArrayList<ParticipantStatus>) {
    }

    /**
     * Show toast message to the users, Toast type
     */
    override fun showToast(type: LensToast, displayName: String?) {
        activity.runOnUiThread {
            when (type) {
                LensToast.JOINED_PARTICIPANT -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName joined in the session", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                LensToast.LEFT_PARTICIPANT -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName left in the session", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                LensToast.CAMERA_STREAM_CHANGED -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName is currently streaming", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                LensToast.CAMERA_STREAM_REMOVED -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName is removed streaming", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                LensToast.CAMERA_STREAM_REQUEST_SOMEONE_WAITING_ALREADY -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName is waiting to streaming", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                LensToast.CAMERA_STREAM_REQUEST_APPROVED -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName streaming request approved", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                LensToast.CAMERA_STREAM_REQUEST_DENIED -> {
                    displayName?.let {
                        if (it != "") {
                            Toast.makeText(activity, "$displayName streaming request denied", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun showArTrackingListenerToast(trackingState: TrackingState, trackingFailureReason: TrackingFailureReason) {
        if ((trackingState == TrackingState.PAUSED && trackingFailureReason != TrackingFailureReason.NONE ) || trackingState == TrackingState.STOPPED) {
            activity.runOnUiThread {
                Toast.makeText(activity, "${trackingState.name}:${trackingFailureReason.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Callback used to perform any operation whenever the audio device gets changed.
     */
    override fun audioDeviceChange(selectedAudioDevice: AppRTCAudioManager.AudioDevice, availableAudioDevices: MutableSet<AppRTCAudioManager.AudioDevice>) {

        /*
        * List down the devices and switch the audio mode
        */
        LensSDK.switchAudioMode(selectedAudioDevice)
        activity.viewDataBinding.speaker.text = selectedAudioDevice.name
    }

    /**
     * Callback used to handle when chatlet state is changed
     */
    override fun chatLetState(currentStatus: ChatLetState) {
        activity.chatFragment.chatLetState.postValue(currentStatus)
        activity.chatLetState.postValue(currentStatus)
    }

    /**
     * Callback used to handle the Audio Mute/Un mute states.
     */
    override fun handleAudioMute(isMute: Boolean) {
        activity.runOnUiThread {
            if (isMute) {
                if (activity.changedMuteSelf) {
                    activity.viewDataBinding.muteUnmuteSelf.isChecked = true
                    activity.changedMuteSelf = false
                } else {
                    activity.viewDataBinding.muteUnmuteSelf.isChecked = true
                }
            } else {
                if (activity.changedMuteSelf) {
                    activity.viewDataBinding.muteUnmuteSelf.isChecked = false
                    activity.changedMuteSelf = false
                } else {
                    activity.viewDataBinding.muteUnmuteSelf.isChecked = false
                }
            }
        }
    }

    /**
     * Callback used to handle the annotation count updates
     */
    override fun onAnnotationsCountUpdated(annotationCount: Int?) {
    }

    override fun onArAnnotationDeSelected(arAnnotationObject: ArAnnotationObject) {
    }

    override fun onArAnnotationListUpdate(arAnnotationList: ArrayList<ArAnnotationObject>) {
    }

    override fun onArAnnotationNotesUpdate(arAnnotationObject: ArAnnotationObject) {
        activity.runOnUiThread {
            val arComment = arAnnotationObject.notes?.get(0)?.data
            Toast.makeText(activity, arComment, Toast.LENGTH_LONG).show()
        }
    }

    override fun onArAnnotationPlaced(arAnnotationObject: ArAnnotationObject) {
    }

    override fun onArAnnotationRemoved(arAnnotationList: ArrayList<ArAnnotationObject>) {
    }

    override fun onArAnnotationSelected(arAnnotationObject: ArAnnotationObject) {
    }

    /**
     * Callback used to handle when camera facing is changed
     */
    override fun onCameraFacingChanged(cameraFacing: CameraFacing) {
        Toast.makeText(activity, "Camera facing changed to ${cameraFacing.name}", Toast.LENGTH_SHORT).show()
    }

    override fun customerAlreadyActiveInSession() {

    }

    /**
     * Callback used to perform any operation whenever streaming type is changing.
     */
    override fun showStreamingType(type: StreamingType, displayName: String?) {
        activity.runOnUiThread {
            when (type) {
                StreamingType.VIDEO_UPSTREAM -> {
                    activity.viewDataBinding.swapCamera.visibility = View.VISIBLE
                    activity.viewDataBinding.ocrButton.visibility = View.VISIBLE
                    activity.viewDataBinding.qrButton.visibility = View.VISIBLE

                    if (LensSDK.isARMode()) {
                        activity.viewDataBinding.zoom.visibility = View.VISIBLE
                        activity.viewDataBinding.undoAnnotation.visibility = View.VISIBLE
                        activity.viewDataBinding.clearAllAnnotation.visibility = View.VISIBLE
                        activity.viewDataBinding.resolution.visibility = View.VISIBLE

                        LensSDK.setResolution(false)
                        activity.viewDataBinding.resolution.text = "Non HD"
                    }

                    activity.viewDataBinding.video.visibility = View.VISIBLE

                    activity.viewDataBinding.zoom.isChecked = false
                    activity.viewDataBinding.zoomSeekbar.visibility = View.GONE

                    activity.viewDataBinding.shareCamera.text = "Video Off"
                    activity.viewDataBinding.shareCamera.visibility = View.VISIBLE
                    activity.viewDataBinding.flashLight.visibility = View.GONE

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (LensSDK.getChatLetEnabled()) {
                            activity.viewDataBinding.chatButton.visibility = View.VISIBLE
                        } else {
                            activity.viewDataBinding.chatButton.visibility = View.GONE
                        }
                    }, 5000)

                    activity.isUpstream = true

                    activity.viewDataBinding.annotation.isChecked = false
                    if (LensSDK.isARMode()) {
                        activity.viewDataBinding.annotation.visibility = View.VISIBLE
                    } else {
                        activity.viewDataBinding.annotation.visibility = View.GONE
                    }
                    activity.viewDataBinding.pointCloud.visibility = View.GONE
                    activity.viewDataBinding.planeDetection.visibility = View.GONE
                    activity.viewDataBinding.arrow.visibility = View.GONE
                    activity.viewDataBinding.pencil.visibility = View.GONE
                    activity.viewDataBinding.rectangle.visibility = View.GONE
                    activity.viewDataBinding.ellipse.visibility = View.GONE
                    activity.viewDataBinding.arPointer.visibility = View.GONE
                    activity.viewDataBinding.arMeasure.visibility = View.GONE
                    activity.viewDataBinding.arMeasureIcon.visibility = View.GONE
                    activity.viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
                }
                StreamingType.VIDEO_DOWNSTREAM -> {
                    activity.viewDataBinding.swapCamera.visibility = View.GONE
                    activity.viewDataBinding.ocrButton.visibility = View.VISIBLE
                    activity.viewDataBinding.qrButton.visibility = View.VISIBLE
                    activity.viewDataBinding.zoom.visibility = View.GONE
                    activity.viewDataBinding.video.visibility = View.VISIBLE
                    activity.viewDataBinding.undoAnnotation.visibility = View.GONE
                    activity.viewDataBinding.clearAllAnnotation.visibility = View.GONE
                    activity.viewDataBinding.resolution.visibility = View.GONE

                    activity.viewDataBinding.zoom.isChecked = false
                    activity.viewDataBinding.zoom.visibility = View.GONE
                    activity.viewDataBinding.zoomSeekbar.visibility = View.GONE

                    activity.viewDataBinding.shareCamera.text = "Video On"
                    activity.viewDataBinding.shareCamera.isChecked = false
                    activity.viewDataBinding.shareCamera.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (LensSDK.getChatLetEnabled()) {
                            activity.viewDataBinding.chatButton.visibility = View.VISIBLE
                        } else {
                            activity.viewDataBinding.chatButton.visibility = View.GONE
                        }
                        if (LensSDK.getFlashSupportEnabled()) {
                            activity.viewDataBinding.flashLight.visibility = View.VISIBLE
                        } else {
                            activity.viewDataBinding.flashLight.visibility = View.GONE
                        }
                    }, 5000)

                    activity.isUpstream = false

                    activity.viewDataBinding.annotation.visibility = View.VISIBLE
                    activity.viewDataBinding.annotation.isChecked = false
                    activity.viewDataBinding.pointCloud.visibility = View.GONE
                    activity.viewDataBinding.planeDetection.visibility = View.GONE
                    activity.viewDataBinding.arrow.visibility = View.GONE
                    activity.viewDataBinding.pencil.visibility = View.GONE
                    activity.viewDataBinding.rectangle.visibility = View.GONE
                    activity.viewDataBinding.ellipse.visibility = View.GONE
                    activity.viewDataBinding.arPointer.visibility = View.GONE
                    activity.viewDataBinding.arMeasure.visibility = View.GONE
                    activity.viewDataBinding.arMeasureIcon.visibility = View.GONE
                    activity.viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
                }
                StreamingType.NO_ONE_IS_STREAMING -> {
                    activity.viewDataBinding.swapCamera.visibility = View.GONE
                    activity.viewDataBinding.resolution.visibility = View.GONE
                    activity.viewDataBinding.ocrButton.visibility = View.GONE
                    activity.viewDataBinding.qrButton.visibility = View.GONE
                    activity.viewDataBinding.zoom.visibility = View.GONE
                    activity.viewDataBinding.video.visibility = View.GONE
                    activity.viewDataBinding.zoomSeekbar.visibility = View.GONE
                    activity.viewDataBinding.undoAnnotation.visibility = View.GONE
                    activity.viewDataBinding.clearAllAnnotation.visibility = View.GONE
                    activity.viewDataBinding.flashLight.visibility = View.GONE

                    activity.viewDataBinding.shareCamera.text = "Video On"
                    activity.viewDataBinding.shareCamera.isChecked = false
                    activity.viewDataBinding.shareCamera.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (LensSDK.getChatLetEnabled()) {
                            activity.viewDataBinding.chatButton.visibility = View.VISIBLE
                        } else {
                            activity.viewDataBinding.chatButton.visibility = View.GONE
                        }
                    }, 5000)

                    activity.isUpstream = false

                    activity.viewDataBinding.annotation.visibility = View.GONE
                    activity.viewDataBinding.annotation.isChecked = false
                    activity.viewDataBinding.pointCloud.visibility = View.GONE
                    activity.viewDataBinding.planeDetection.visibility = View.GONE
                    activity.viewDataBinding.arrow.visibility = View.GONE
                    activity.viewDataBinding.pencil.visibility = View.GONE
                    activity.viewDataBinding.rectangle.visibility = View.GONE
                    activity.viewDataBinding.ellipse.visibility = View.GONE
                    activity.viewDataBinding.arPointer.visibility = View.GONE
                    activity.viewDataBinding.arMeasure.visibility = View.GONE
                    activity.viewDataBinding.arMeasureIcon.visibility = View.GONE
                    activity.viewDataBinding.arMeasureCenterAnchor.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Callback used to handle the fpsUpdate
     */
    override fun onFPSUpdate(averageFPS: Int, fifteenthSecondFPS: Int) {

    }

    /**
     * Callback used to handle for features based on the license
     */
    override fun onFeatureNotAvailableWithCurrentLicense(feature: Feature) {
        activity.runOnUiThread {
            when (feature) {
                Feature.FREEZE_CAMERA_STREAM -> {
                    activity.isMuteVideo = false
                    activity.viewDataBinding.video.isChecked = false
                }
                Feature.SHARE_CAMERA -> {
                    activity.viewDataBinding.shareCamera.text = "Video Off"
                    activity.viewDataBinding.shareCamera.isChecked = true
                }
                else -> {

                }
            }
            Toast.makeText(activity, "Feature available only in Standard / Professional edition. Upgrade to use.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Callback used to handle the flash ligh on/off state
     */
    override fun onFlashLightOff(flashSupportStatus: FlashSupportStatus, displayName: String?) {
        when (flashSupportStatus) {
            FlashSupportStatus.SUCCESS -> {
                activity.viewDataBinding.flashLight.isChecked = false
                activity.viewDataBinding.flashLight.text = "Flash On"
                activity.runOnUiThread {
                    Toast.makeText(activity, "$displayName has turned off Flashlight", Toast.LENGTH_LONG).show()
                }
            }
            FlashSupportStatus.FAILED -> {
                activity.viewDataBinding.flashLight.isChecked = true
                activity.viewDataBinding.flashLight.text = "Flash Off"
                activity.runOnUiThread {
                    Toast.makeText(activity, "Unable to turn off Flashlight. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onFlashLightOn(flashSupportStatus: FlashSupportStatus, displayName: String?) {
        when (flashSupportStatus) {
            FlashSupportStatus.SUCCESS -> {
                activity.viewDataBinding.flashLight.isChecked = true
                activity.viewDataBinding.flashLight.text = "Flash Off"
                activity.runOnUiThread {
                    Toast.makeText(activity, "$displayName has turned on Flashlight", Toast.LENGTH_LONG).show()
                }
            }
            FlashSupportStatus.FAILED -> {
                activity.viewDataBinding.flashLight.isChecked = false
                activity.viewDataBinding.flashLight.text = "Flash On"
                activity.runOnUiThread {
                    Toast.makeText(activity, "Unable to turn on Flashlight. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Callback used to handle the freeze state
     */
    override fun onFreezeActions(freezeActions: FreezeActions, displayName: String?) {
        when (freezeActions) {
            FreezeActions.FREEZE_SNAPSHOT, FreezeActions.FREEZE_VIDEO,
            FreezeActions.FREEZE_SNAPSHOT_DOWNLOADED,FreezeActions.FREEZE_SNAPSHOT_SELF -> {
                activity.runOnUiThread {
                    activity.isMuteVideo = true
                    activity.viewDataBinding.video.isChecked = true

                    activity.viewDataBinding.zoom.visibility = View.GONE
                    activity.viewDataBinding.resolution.visibility = View.GONE
                    activity.viewDataBinding.annotation.visibility = View.GONE
                    activity.viewDataBinding.undoAnnotation.visibility = View.GONE
                    activity.viewDataBinding.clearAllAnnotation.visibility = View.GONE
                }
            }
            FreezeActions.UNFREEZE_SNAPSHOT, FreezeActions.UNFREEZE_VIDEO, FreezeActions.UNFREEZE_SNAPSHOT_SELF -> {
                activity.runOnUiThread {
                    activity.isMuteVideo = false
                    activity.viewDataBinding.video.isChecked = false

                    if (LensSDK.isARMode()) {
                        activity.viewDataBinding.zoom.visibility = View.VISIBLE
                        activity.viewDataBinding.resolution.visibility = View.VISIBLE
                        activity.viewDataBinding.annotation.visibility = View.VISIBLE
                        activity.viewDataBinding.undoAnnotation.visibility = View.VISIBLE
                        activity.viewDataBinding.clearAllAnnotation.visibility = View.VISIBLE
                    }
                }
            }
            FreezeActions.FREEZE_SNAPSHOT_DOWNLOAD_FAILED -> {

            }
        }
        displayName?.let {
            if (it != "") {
                Toast.makeText(activity, "$displayName ${freezeActions.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback used to handle swap camera
     */
    override fun onCameraSwapDone(isFrontCamera: Boolean) {
        activity.runOnUiThread {
            activity.viewDataBinding.zoom.isChecked = false
            activity.viewDataBinding.zoomSeekbar.visibility = View.GONE

            if (isFrontCamera) {
                activity.viewDataBinding.zoom.visibility = View.GONE
                activity.viewDataBinding.resolution.visibility = View.GONE
                activity.viewDataBinding.annotation.visibility = View.GONE
                activity.viewDataBinding.undoAnnotation.visibility = View.GONE
                activity.viewDataBinding.clearAllAnnotation.visibility = View.GONE
            } else {
                activity.viewDataBinding.zoom.visibility = View.VISIBLE
                activity.viewDataBinding.resolution.visibility = View.VISIBLE
                activity.viewDataBinding.annotation.visibility = View.VISIBLE
                activity.viewDataBinding.undoAnnotation.visibility = View.VISIBLE
                activity.viewDataBinding.clearAllAnnotation.visibility = View.VISIBLE
            }

            LensSDK.setZoomPercentage(0f)
        }
    }

    /**
     * Callback used to handle if chatlet reverted
     */
    override fun onChatLetReverted(status: Boolean) {
        activity.onChatLetReverted(status)
    }

    /**
     * Callback used to handle the device lock state
     */
    override fun onDeviceLockStateChanged(deviceLockState: DeviceLockState, displayName: String?) {
        displayName?.let {
            if (it != "") {
                Toast.makeText(activity, "$displayName is ${deviceLockState.name} screen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback used to handle Live text scan states
     */
    override fun onOCRStateChanged(ocrStateModel: OCRStateModel) {
        when (ocrStateModel.ocrState) {
            OCRState.STARTED -> {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Scanning started", Toast.LENGTH_SHORT).show()
                }
            }
            OCRState.COMPLETED -> {
                activity.runOnUiThread {
                    activity.onLiveTextScanCompleted(ocrStateModel.ocrText ?: "")
                }
            }
            OCRState.ERROR -> {
                activity.runOnUiThread {
                    Toast.makeText(activity, "${ocrStateModel.errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Callback used to handle QR/Barcode scan success state
     */
    override fun onQRSuccess(qrText: String) {
        activity.runOnUiThread {
            activity.onQRScanCompleted(qrText)
        }
    }

    /**
     * Callback used to handle QR/Barcode failure success state
     */
    override fun onQRFailure(errorMessage: String, willRetry: Boolean) {
        activity.runOnUiThread {
            if (!willRetry) {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onQRScanStarted() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Scanning started", Toast.LENGTH_SHORT).show()
        }
    }
}
