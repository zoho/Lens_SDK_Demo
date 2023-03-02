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
import com.zoho.lens.*
import kotlinx.android.synthetic.main.activity_lens.*

class SessionCallbacks(private val activity: LensSample) : ISessionCallback {
    override fun sessionConnectionState(state: LensType, error: ErrorType?, message: String) {
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
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
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

        if (message.isNotEmpty()) {
            Handler(Looper.getMainLooper()).post {
//                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback used to handle the received messages.
     */
    override fun chatMessage(message: ChatModel) {
    }

    override fun clientAlreadyActiveInSession() {
        TODO("Not yet implemented")
    }

    override fun onLensScreenShotTaken() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Screenshot taken", Toast.LENGTH_SHORT).show()
        }
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

    /**
     * Returns the activity
     */
    override fun getActivity(): Activity {
        return activity
    }

    /**
     * Callback used to handle the video freeze state
     */

    /**
     * Session is already active in another location so we can't open.
     */
    override fun openedInDifferentLocation() {
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
            displayName?.let {
                if (type != LensToast.ANCHOR_PLACED &&  it != "") {
                    Toast.makeText(activity, displayName, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun showArTrackingListenerToast(trackingState: TrackingState, trackingFailureReason: TrackingFailureReason) {
        if ((trackingState == TrackingState.PAUSED && trackingFailureReason != TrackingFailureReason.NONE ) || trackingState == TrackingState.STOPPED) {
            activity.runOnUiThread {
                Toast.makeText(
                    activity,
                    "${trackingState.name}:${trackingFailureReason.name}",
                    Toast.LENGTH_SHORT
                ).show()
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
     * Callback used to handle the annotation count updates
     */
    override fun annotationsUpdated(annotationCount: Int?) {
    }

    /**
     * Callback used to handle the selected/ Unselected annotation with annotation Id
     */
    override fun annotationSelection(isSelected: Boolean, id: String?) {
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
                    }

                    activity.video.visibility = View.VISIBLE

                    activity.zoom.isChecked = false
                    activity.zoom_seekbar.visibility = View.GONE

                    activity.share_camera.text = "Video Off"
                    activity.share_camera.visibility = View.VISIBLE
                }
                StreamingType.VIDEO_DOWNSTREAM -> {
                    activity.swap_camera.visibility = View.VISIBLE
                    activity.ocr_button.visibility = View.VISIBLE
                    activity.qr_button.visibility = View.VISIBLE
                    activity.zoom.visibility = View.GONE
                    activity.video.visibility = View.GONE
                    activity.undo_annotation.visibility = View.GONE
                    activity.clear_all_annotation.visibility = View.GONE

                    activity.zoom.isChecked = false
                    activity.zoom.visibility = View.GONE
                    activity.zoom_seekbar.visibility = View.GONE

                    activity.share_camera.text = "Video On"
                    activity.share_camera.visibility = View.VISIBLE
                }
                StreamingType.NO_ONE_IS_STREAMING -> {
                    activity.swap_camera.visibility = View.GONE
                    activity.ocr_button.visibility = View.GONE
                    activity.qr_button.visibility = View.GONE
                    activity.zoom.visibility = View.GONE
                    activity.video.visibility = View.GONE
                    activity.zoom_seekbar.visibility = View.GONE
                    activity.undo_annotation.visibility = View.GONE
                    activity.clear_all_annotation.visibility = View.GONE

                    activity.share_camera.text = "Video On"
                    activity.share_camera.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onFPSUpdate(averageFPS: Int, fifteenthSecondFPS: Int) {

    }

    override fun onCameraSwapDone(isFrontCamera: Boolean) {
        activity.runOnUiThread {
            activity.zoom.isChecked = false
            activity.zoom_seekbar.visibility = View.GONE

            LensSDK.setZoomPercentage(0f)
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
        }
    }

    override fun onQRFailure(errorMessage: String, willRetry: Boolean) {
        activity.runOnUiThread {
            if (!willRetry) {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
