package com.zoho.lens

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zoho.assist.customer.model.NotesResponse
import com.zoho.assist.customer.model.Status
import com.zoho.assist.customer.model.UpdateSessionNotesResponse
import com.zoho.assist.customer.model.UploadSnapShotResponse
import com.zoho.assistrtc.*
import com.zoho.assistrtc.api.SessionStartFailure
import com.zoho.assistrtc.base.Snapshot
import com.zoho.assistrtc.model.ChatModel
import com.zoho.webrtc.AppRTCAudioManager
import kotlinx.android.synthetic.main.activity_lens.*

class SessionCallbacks(private val activity: LensSample ) :ISessionCallback{


    /**
     * Callback used to handle the network state.
     */
    override fun onNetworkConnectionState(state: NetworkState) {
        when (state) {
            NetworkState.CONNECTED -> {

            }
            NetworkState.DISCONNECTED -> {

            }
        }
    }
    /**
     * Callback used to handle the socket connection’s state.
     */
    override fun onSocketConnectionState(state: SocketConnectionType, message: String) {
        when (state) {
            SocketConnectionType.SOCKET_CONNECTED -> {
            }
            SocketConnectionType.SOCKET_DISCONNECTED -> {
            }
            SocketConnectionType.SOCKET_ERROR -> {
            }
            SocketConnectionType.SOCKET_FAILED -> {
            }
        }
    }
    /**
     * Callback used to handle the session connection’s state.
     */
    override fun onSessionConnectionState(state: SessionConnectionType, message: String) {
        when (state) {
            SessionConnectionType.CLIENT_INVALID, SessionConnectionType.CLOSED_SESSION, SessionConnectionType.CUSTOMER_CLOSED_SESSION
                , SessionConnectionType.DUPLICATE_CLIENT, SessionConnectionType.DUPLICATE_CLIENT, SessionConnectionType.SESSION_EXPIRED
                , SessionConnectionType.SESSION_INVALID, SessionConnectionType.TECHNICIAN_CLOSED_SESSION -> {
                /**
                 * Close the session here.
                 */
                activity.onClosedSession()
            }
            SessionConnectionType.SESSION_CONNECTED -> {
                /**
                 * Switch the fragment to and pass the webrtc renderview
                 */

            }
        }
    }
    /**
     * Callback used to handle the results of validating the session.
     */
    override fun onValidationState(state: ValidateSessionKeyType, message: String) {
        when(state){
            ValidateSessionKeyType.VALID->{
                LensSDK.joinSession()

            }
            ValidateSessionKeyType.ERROR->{
                activity.runOnUiThread {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    activity.startActivity(Intent(activity,MainActivity::class.java))
                    activity.finish()
                }
            }
        }
    }
    /**
     * Callback used to perform any operation using the peer connection state.
    isFirst - State of the boolean value true is first peer and false is second peer
    message- peer connection state info
     */
    override fun onPeerConnectionState(connectionState: PeerConnectionState, isFirst: Boolean, message: String) {
        when (connectionState) {
            PeerConnectionState.PEER_CONNECTED -> {
            }
            PeerConnectionState.PEER_DISCONNECTED -> {
            }
            PeerConnectionState.ICE_STATE_CONNECTED -> {
            }
            PeerConnectionState.ICE_STATE_DISCONNECTED -> {
            }
            PeerConnectionState.ICE_STATE_FAILED -> {
            }
            PeerConnectionState.ICE_STATE_RECONNECTED -> {
            }
            else -> {
            }
        }
    }
    /**
     * Callback used to handle the received messages.
     */
    override fun handleChatMessageReceived(message: ChatModel) {
    }
    /**
     * Callback used to perform any operation when a screenshot is taken.
     */
    override fun onLensScreenShotTaken() {
        activity.runOnUiThread {
            Toast.makeText(activity, "onLensScreenShotTaken", Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * Callback used to perform any operation when the audio device gets changed.
     */
    override fun onAudioDeviceChanges(selectedAudioDevice: AppRTCAudioManager.AudioDevice, availableAudioDevices: MutableSet<AppRTCAudioManager.AudioDevice>) {
        LensSDK.switchAudioMode(selectedAudioDevice)
    }

    /**
     * Callback used to handle the Audio Mute/Unmute.
     */
    override fun handleAudioMute(isMute: Boolean) {
        println("handleAudioMute")
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
     * Callback used to handle to annotation count
     */
    override fun annotationsUpdated(annotationCount: Int?) {

    }

    /**
     * Callback used to perform paricipant join state
     */
    override fun onParticipantJoinState(state: ParticipantJoinState) {
        when (state) {
            ParticipantJoinState.INVITE_CUSTOMER -> {

            }
            ParticipantJoinState.INIT_JOIN_CUSTOMER -> {

            }
            ParticipantJoinState.JOINED_CUSTOMER -> {

            }
            ParticipantJoinState.COMPLETED_CUSTOMER_JOIN -> {

            }
            ParticipantJoinState.CUSTOMER_LEFT_SESSION -> {

            }
            ParticipantJoinState.TECHNICIAN_LEFT_SESSION -> {

            }
        }
    }

    /**
     * Callback used to handle session close if session failed
     */
    override fun onSessionStartFailed(failure: SessionStartFailure) {

    }


}