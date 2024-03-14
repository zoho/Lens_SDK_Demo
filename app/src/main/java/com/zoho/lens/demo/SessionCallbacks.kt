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
import com.zoho.lens.AudioDevice
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
import kotlinx.android.synthetic.main.activity_lens.chat_button
import kotlinx.android.synthetic.main.activity_lens.clear_all_annotation
import kotlinx.android.synthetic.main.activity_lens.flash_light
import kotlinx.android.synthetic.main.activity_lens.mute_unmute_self
import kotlinx.android.synthetic.main.activity_lens.ocr_button
import kotlinx.android.synthetic.main.activity_lens.qr_button
import kotlinx.android.synthetic.main.activity_lens.resolution
import kotlinx.android.synthetic.main.activity_lens.share_camera
import kotlinx.android.synthetic.main.activity_lens.swap_camera
import kotlinx.android.synthetic.main.activity_lens.undo_annotation
import kotlinx.android.synthetic.main.activity_lens.video
import kotlinx.android.synthetic.main.activity_lens.zoom
import kotlinx.android.synthetic.main.activity_lens.zoom_seekbar

class SessionCallbacks(private val activity: LensSample) : ISessionCallback {
    override fun sessionConnectionState(state: LensType, error: ErrorType?, message: Any) {
        when (state) {
            /**
             * Handle the socket connection’s state.
             */
            LensType.SOCKET_CONNECTED -> {
            }
            LensType.SOCKET_DISCONNECTED -> {
            }
            LensType.SOCKET_ERROR -> {
            }
            LensType.SOCKET_FAILED -> {
            }

            /**
             * Handle the network state.
             */
            LensType.NETWORK_CONNECTED -> {
            }
            LensType.NETWORK_DISCONNECTED -> {
            }

            /**
             * Handle session close if session failed
             */
            LensType.BELOW_MIN_API_LEVEL -> {
            }
            LensType.CONTEXT_NOT_AVAILABLE -> {
            }
            LensType.INVALID_SDK_TOKEN -> {
                Toast.makeText(activity, "Invalid SDK Token.", Toast.LENGTH_LONG).show()
                activity.finish()
            }
            LensType.INVALID_SESSION_KEY -> {
            }

            /**
             * Handle the results of validating the session.
             */
            LensType.VALID -> {
            }
            LensType.ERROR -> {
                activity.runOnUiThread {
                    if (error == ErrorType.UPDATE_TO_LATEST_APP) {
                        Toast.makeText(activity, "An upgraded version of Zoho Lens is now available. To continue using Zoho Lens, kindly update the application from the PlayStore.", Toast.LENGTH_LONG).show()
                        activity.finish()
                    } else {
                        Toast.makeText(activity, message.toString(), Toast.LENGTH_SHORT).show()
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
            LensType.SESSION_CONNECTED -> {
                /**
                 * Switch the fragment to and pass the webrtc render view
                 */
            }
            /**
             * To perform any operation using the peer connection state.
             */
            // * isFirst - State of the boolean value true is first peer and false is second peer
            // * message- peer connection state info

            LensType.PEER_CONNECTED -> {}
            LensType.PEER_DISCONNECTED -> {}
            LensType.ICE_STATE_CONNECTED -> {}
            LensType.ICE_STATE_DISCONNECTED -> {}
            LensType.ICE_STATE_FAILED -> {}
            LensType.ICE_STATE_RECONNECTED -> {}

            /**
             * To perform participant join state
             */
            LensType.CONNECTION_INITIATED -> {}
            LensType.COMPLETED_PARTICIPANT_JOIN -> {}
            LensType.CUSTOMER_LEFT_SESSION -> {}
            LensType.TECHNICIAN_LEFT_SESSION -> {}
            LensType.INVITE_PARTICIPANT -> {}
            LensType.SECONDARY_TECHNICIAN_LEFT_SESSION -> {}
            LensType.SERVER_ISSUE_LEFT_SESSION -> {}
            LensType.SOCKET_CLOSED -> {}
            LensType.SOCKET_CONNECTING -> {}
            LensType.CLIENT_IS_NOT_COMPATIBLE -> {}
            LensType.ICE_STATE_CLOSED -> {}
            LensType.ICE_STATE_NEW -> {}
            LensType.ICE_CANDIDATES_LOCAL_CONNECTION_FAILURE -> {}
            LensType.ICE_CANDIDATES_REMOTE_CONNECTION_FAILURE -> {}
        }
    }

    /**
     * Callback used to handle the received messages.
     */
    override fun chatMessage(message: ChatModel) {
    }

    override fun customerAlreadyActiveInSession() {

    }

    /**
     * Callback used to perform before starting session microphone is available to use.
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
     * Callback used to handle the annotation count updates
     */
    override fun onAnnotationsCountUpdated(annotationCount: Int?) {

    }

    /**
     * Callback used to perform any operation related to ar comments.
     */
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

    override fun onCameraFacingChanged(cameraFacing: CameraFacing) {
        Toast.makeText(activity, "Camera facing changed to ${cameraFacing.name}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Returns the activity
     */
    override fun getActivity(): Activity {
        return activity
    }



    /**
     * Session is already active in another location so we can't open.
     */

    /**
     * To update the list of technicians currently connected in the session along with the state.
     */
    override fun updateTechnicianList(technicians: ArrayList<ParticipantStatus>) {
    }

    /**
     * Show toast message to the users, Toast type
     */
    override fun showToast(type: LensToast, displayName: String?) {
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

            LensToast.CAMERA_STREAM_REQUEST_RECEIVED -> {}
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
    override fun audioDeviceChange(selectedAudioDevice: AudioDevice, availableAudioDevices: MutableSet<AudioDevice>) {
        /*
        * List down the devices and switch the audio mode
        */
        LensSDK.switchAudioMode(selectedAudioDevice)
    }

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
                    activity.mute_unmute_self.isChecked = true
                    activity.changedMuteSelf = false
                } else {
                    activity.mute_unmute_self.isChecked = true
                }
            } else {
                if (activity.changedMuteSelf) {
                    activity.mute_unmute_self.isChecked = false
                    activity.changedMuteSelf = false
                } else {
                    activity.mute_unmute_self.isChecked = false
                }
            }
        }
    }

    /**
     * Callback used to perform any operation whenever streaming type is changing.
     */
    override fun showStreamingType(type: StreamingType, displayName: String?) {
        activity.runOnUiThread {
            when (type) {
                StreamingType.VIDEO_UPSTREAM -> {
                    activity.swap_camera.visibility = View.VISIBLE
                    activity.ocr_button.visibility = View.VISIBLE
                    activity.qr_button.visibility = View.VISIBLE

                    if (LensSDK.isARMode()) {
                        activity.zoom.visibility = View.VISIBLE
                        activity.undo_annotation.visibility = View.VISIBLE
                        activity.clear_all_annotation.visibility = View.VISIBLE
                        activity.resolution.visibility = View.VISIBLE

                        LensSDK.setResolution(false)
                        activity.resolution.text = "Non HD"
                    }

                    activity.video.visibility = View.VISIBLE

                    activity.zoom.isChecked = false
                    activity.zoom_seekbar.visibility = View.GONE

                    activity.share_camera.text = "Video Off"
//                    activity.share_camera.isChecked = false
                    activity.share_camera.visibility = View.VISIBLE
                    activity.flash_light.visibility = View.GONE

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (LensSDK.getChatLetEnabled()) {
                            activity.chat_button.visibility = View.VISIBLE
                        } else {
                            activity.chat_button.visibility = View.GONE
                        }
                    }, 5000)
                }
                StreamingType.VIDEO_DOWNSTREAM -> {
                    activity.swap_camera.visibility = View.GONE
                    activity.ocr_button.visibility = View.VISIBLE
                    activity.qr_button.visibility = View.VISIBLE
                    activity.zoom.visibility = View.GONE
                    activity.video.visibility = View.VISIBLE
                    activity.undo_annotation.visibility = View.GONE
                    activity.clear_all_annotation.visibility = View.GONE
                    activity.resolution.visibility = View.GONE

                    activity.zoom.isChecked = false
                    activity.zoom.visibility = View.GONE
                    activity.zoom_seekbar.visibility = View.GONE

                    activity.share_camera.text = "Video On"
                    activity.share_camera.isChecked = false
                    activity.share_camera.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (LensSDK.getChatLetEnabled()) {
                            activity.chat_button.visibility = View.VISIBLE
                        } else {
                            activity.chat_button.visibility = View.GONE
                        }
                        if (LensSDK.getFlashSupportEnabled()) {
                            activity.flash_light.visibility = View.VISIBLE
                        } else {
                            activity.flash_light.visibility = View.GONE
                        }
                    }, 5000)

                }
                StreamingType.NO_ONE_IS_STREAMING -> {
                    activity.swap_camera.visibility = View.GONE
                    activity.resolution.visibility = View.GONE
                    activity.ocr_button.visibility = View.GONE
                    activity.qr_button.visibility = View.GONE
                    activity.zoom.visibility = View.GONE
                    activity.video.visibility = View.GONE
                    activity.zoom_seekbar.visibility = View.GONE
                    activity.undo_annotation.visibility = View.GONE
                    activity.clear_all_annotation.visibility = View.GONE
                    activity.flash_light.visibility = View.GONE

                    activity.share_camera.text = "Video On"
                    activity.share_camera.isChecked = false
                    activity.share_camera.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (LensSDK.getChatLetEnabled()) {
                            activity.chat_button.visibility = View.VISIBLE
                        } else {
                            activity.chat_button.visibility = View.GONE
                        }
                    }, 5000)
                }
            }
        }
    }

    override fun onFPSUpdate(averageFPS: Int, fifteenthSecondFPS: Int) {

    }

    override fun onFeatureNotAvailableWithCurrentLicense(feature: Feature) {
        activity.runOnUiThread {
            when (feature) {
                Feature.FREEZE_CAMERA_STREAM -> {
                    activity.isMuteVideo = false
                    activity.video.isChecked = false
                }
                Feature.SHARE_CAMERA -> {
                    activity.share_camera.text = "Video Off"
                    activity.share_camera.isChecked = true
                }
                else -> {

                }
            }
            Toast.makeText(activity, "Feature available only in Standard / Professional edition. Upgrade to use.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFlashLightOff(flashSupportStatus: FlashSupportStatus, displayName: String?) {
        when (flashSupportStatus) {
            FlashSupportStatus.SUCCESS -> {
                activity.flash_light.isChecked = false
                activity.flash_light.text = "Flash On"
                activity.runOnUiThread {
                    Toast.makeText(activity, "$displayName has turned off Flashlight", Toast.LENGTH_LONG).show()
                }
            }
            FlashSupportStatus.FAILED -> {
                activity.flash_light.isChecked = true
                activity.flash_light.text = "Flash Off"
                activity.runOnUiThread {
                    Toast.makeText(activity, "Unable to turn off Flashlight. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onFlashLightOn(flashSupportStatus: FlashSupportStatus, displayName: String?) {
        when (flashSupportStatus) {
            FlashSupportStatus.SUCCESS -> {
                activity.flash_light.isChecked = true
                activity.flash_light.text = "Flash Off"
                activity.runOnUiThread {
                    Toast.makeText(activity, "$displayName has turned on Flashlight", Toast.LENGTH_LONG).show()
                }
            }
            FlashSupportStatus.FAILED -> {
                activity.flash_light.isChecked = false
                activity.flash_light.text = "Flash On"
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
                    activity.video.isChecked = true
                }
            }
            FreezeActions.UNFREEZE_SNAPSHOT, FreezeActions.UNFREEZE_VIDEO, FreezeActions.UNFREEZE_SNAPSHOT_SELF -> {
                activity.runOnUiThread {
                    activity.isMuteVideo = false
                    activity.video.isChecked = false
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

    override fun onLensScreenShotTaken(displayName: String?) {
        if (displayName != null && displayName != "") {
            Toast.makeText(activity, "Screenshot taken by $displayName", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Screenshot taken", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCameraSwapDone(isFrontCamera: Boolean) {
        activity.runOnUiThread {
            activity.zoom.isChecked = false
            activity.zoom_seekbar.visibility = View.GONE

            LensSDK.setZoomPercentage(0f)
        }
    }

    override fun onChatLetReverted(status: Boolean) {
        activity.onChatLetReverted(status)
    }

    override fun onDeviceLockStateChanged(deviceLockState: DeviceLockState, displayName: String?) {
        displayName?.let {
            if (it != "") {
                Toast.makeText(activity, "$displayName is ${deviceLockState.name} screen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOCRStateChanged(ocrStateModel: OCRStateModel) {
        when (ocrStateModel.ocrState) {
            OCRState.STARTED -> {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Scanning started", Toast.LENGTH_SHORT).show()
                }
            }
            OCRState.COMPLETED -> {
                activity.runOnUiThread {
                    Toast.makeText(activity, ocrStateModel.ocrText ?: "", Toast.LENGTH_SHORT).show()
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

    override fun onQRSuccess(qrText: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, qrText, Toast.LENGTH_SHORT).show()
            activity.onQRScanCompleted(qrText)
        }
    }

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
