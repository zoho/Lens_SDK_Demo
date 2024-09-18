package com.zoho.lens.demo

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zoho.lens.LensSDK
import com.zoho.lens.chatLet.ChatLetState
import com.zoho.lens.demo.databinding.FragmentChatletBinding


/**
 * Created by thirumoorthi Nagarajan on 13/09/23.
 * Jambav, Zoho Corporation.
 */


class ChatFragment : BottomSheetDialogFragment() {
    var chatLetState = MutableLiveData<ChatLetState>()
    private lateinit var viewDataBinding: FragmentChatletBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatlet, container, true)
        return viewDataBinding.root
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


        chatLetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ChatLetState.LOADING -> {
                    viewDataBinding.lottieImage.visibility = View.VISIBLE
                    viewDataBinding.chatLayout.visibility = View.GONE
                    viewDataBinding.retryLayout.visibility = View.GONE
                }

                ChatLetState.SUCCESS -> {
                    viewDataBinding.chatLayout.visibility = View.VISIBLE
                    viewDataBinding.lottieImage.visibility = View.GONE
                    viewDataBinding.retryLayout.visibility = View.GONE
                }

                ChatLetState.ERROR -> {
                    viewDataBinding.lottieImage.visibility = View.GONE
                    viewDataBinding.chatLayout.visibility = View.GONE
                    viewDataBinding.retryLayout.visibility = View.VISIBLE
                }
                else -> {

                }
            }
        }
        /**Remove the Views from chat_webview before adding the new chat view (onDestroyView())*/
        viewDataBinding.chatLayout.addView(LensSDK.getChatLetView())
        viewDataBinding.retryLayout.setOnClickListener {
            LensSDK.refreshChatLet()
        }
        viewDataBinding.refreshChat.setOnClickListener {
            LensSDK.refreshChatLet()
        }
        viewDataBinding.closeChat.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        /**Removing the old view before it gets destroyed*/
        viewDataBinding.chatLayout.removeAllViews()
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