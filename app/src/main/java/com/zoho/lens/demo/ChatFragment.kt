package com.zoho.lens.demo

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zoho.lens.LensSDK
import com.zoho.lens.chatLet.ChatLetState
import kotlinx.android.synthetic.main.fragment_chatlet.chat_layout
import kotlinx.android.synthetic.main.fragment_chatlet.close_chat
import kotlinx.android.synthetic.main.fragment_chatlet.lottieImage
import kotlinx.android.synthetic.main.fragment_chatlet.refresh_chat
import kotlinx.android.synthetic.main.fragment_chatlet.retry_layout


/**
 * Created by thirumoorthi Nagarajan on 13/09/23.
 * Jambav, Zoho Corporation.
 */


class ChatFragment : BottomSheetDialogFragment() {
    var chatLetState = MutableLiveData<ChatLetState>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chatlet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        /**METHODS FOR CHATLET
         * LensSDK.getChatLetView()
         * LensSDK.refreshChatLet()
         * LensSDK.sendMessageThroughChatLet(chatMessage)
         * LensSDK.getChatLetEnabled() -> to check chat is enabled
         * LensSDK.attachFileCallBackForChat(uris.toTypedArray()) or LensSDK.attachFileCallBackForChat(null) **/


        chatLetState.observe(viewLifecycleOwner, Observer {
            when (it) {
                ChatLetState.LOADING -> {
                    lottieImage.visibility = View.VISIBLE
                    chat_layout.visibility = View.GONE
                    retry_layout.visibility = View.GONE
                }

                ChatLetState.SUCCESS -> {
                    chat_layout.visibility = View.VISIBLE
                    lottieImage.visibility = View.GONE
                    retry_layout.visibility = View.GONE
                }

                ChatLetState.ERROR -> {
                    lottieImage.visibility = View.GONE
                    chat_layout.visibility = View.GONE
                    retry_layout.visibility = View.VISIBLE
                }
            }
        })
        /**Remove the Views from chat_webview before adding the new chat view (onDestroyView())*/
        chat_layout.addView(LensSDK.getChatLetView())
        retry_layout.setOnClickListener {
            LensSDK.refreshChatLet()
        }
        refresh_chat.setOnClickListener {
            LensSDK.refreshChatLet()
        }
        close_chat.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        /**Removing the old view before it gets destroyed*/
        chat_layout.removeAllViews()
        super.onDestroyView()

    }

    override fun onStart() {
        super.onStart()
        // Adjust the soft input mode to resize the layout when the keyboard is shown
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
}