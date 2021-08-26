package com.zoho.lens.demo

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.zoho.lens.*
import com.zoho.webrtc.AppRTCAudioManager
import kotlinx.android.synthetic.main.activity_lens.*
import java.util.ArrayList

class SessionCallbacks(private val activity: LensSample) : ISessionCallback {
    override fun sessionConnectionState(state: LensType, error: ErrorType?, message: String) {
        println("sessionConnectionState====>>>${state.name}")
        when (state) {
            /**
             * Handle the socket connection’s state.
             */
            LensType.SOCKET_CONNECTED -> {
            }
            LensType.SOCKET_DISCONNECTED -> {
            }
            LensType.SOCKET_ERROR -> {
                activity.runOnUiThread {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                }
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
            }
            LensType.INVALID_SESSION_KEY -> {
            }

            /**
             * Handle the results of validating the session.
             */
            LensType.VALID->{
                LensSDK.joinSession()
            }
            LensType.ERROR -> {
                activity.runOnUiThread {
                    log("Error:",message)
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()
                }
            }

            /**
             * Handle the session connection’s state.
             */
            LensType.CLIENT_INVALID, LensType.CLOSED_SESSION, LensType.CUSTOMER_CLOSED_SESSION
                , LensType.DUPLICATE_CLIENT, LensType.SESSION_EXPIRED
                , LensType.SESSION_INVALID, LensType.TECHNICIAN_CLOSED_SESSION -> {
                /**
                 * Close the session here.
                 */
                activity.onClosedSession()
            }
            LensType.SESSION_CONNECTED -> {
                /**
                 * Switch the fragment to and pass the webrtc renderview
                 */
            }
            /**
             * To perform any operation using the peer connection state.
             */

            LensType.PEER_CONNECTED -> {
            }
            LensType.PEER_DISCONNECTED -> {
            }
            LensType.ICE_STATE_CONNECTED -> {
            }
            LensType.ICE_STATE_DISCONNECTED -> {
            }
            LensType.ICE_STATE_FAILED -> {
            }
            LensType.ICE_STATE_RECONNECTED -> {
            }

            /**
             * To perform paricipant join state
             */
            LensType.CONNECTION_INITIATED -> {
            }
            LensType.COMPLETED_PARTICIPANT_JOIN -> {
            }
            LensType.CUSTOMER_LEFT_SESSION -> {
            }
            LensType.TECHNICIAN_LEFT_SESSION -> {
            }
            else -> {
            }
        }

        if(!message.isNullOrEmpty()) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback used to handle the received messages.
     */
    override fun chatMessage(message: ChatModel) {
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
     * Callback used to handle the video freeze state
     */
    override fun freezeLockState(block: Boolean,freezerName:String) {
    }

    /**
     * Session is already active in another location so we can't open.
     */
    override fun openedInDifferentLocation() {
    }

    /**
     * To update the list of techicians currently connected in the session along with the state.
     */
    override fun updateTechnicianList(technicians: ArrayList<ParticipantStatus>) {
    }

    /**
     * Show toast message to the users
     */
    override fun showToast(type: LensToast, message: String?) {
    }

    /**
     * Callback used to perform any operation whenever a screenshot is taken.
     */
    override fun screenshotTaken() {
        activity.runOnUiThread {
            Toast.makeText(activity, "Screenshot taken", Toast.LENGTH_SHORT).show()
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
     * Callback used to handle the Audio Mute/Unmute states.
     */
    override fun handleAudioMute(isMute: Boolean) {
        activity. runOnUiThread {
            if (isMute) {
                if (activity.changedMuteSelf) {
                    activity. mute_unmute_self.isChecked = true
                    activity.  changedMuteSelf = false
                } else {
                    activity.  mute_unmute_self.isChecked = true
                }
            } else {
                if (activity.changedMuteSelf) {
                    activity.   mute_unmute_self.isChecked = false
                    activity. changedMuteSelf = false
                } else {
                    activity.  mute_unmute_self.isChecked = false

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




}