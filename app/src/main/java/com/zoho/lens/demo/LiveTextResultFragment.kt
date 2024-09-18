package com.zoho.lens.demo

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zoho.lens.LensSDK
import com.zoho.lens.demo.databinding.FragmentOcrResultBinding


/**
 * Created by thirumoorthi Nagarajan on 14/09/23.
 * Jambav, Zoho Corporation.
 */


class LiveTextResultFragment(private val scanType: ScanType) : BottomSheetDialogFragment() {
    private lateinit var viewDataBinding: FragmentOcrResultBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ocr_result, container, false)
        return viewDataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val message = args?.getString("chatMessage")

        dialog?.setCanceledOnTouchOutside(false)

        when (scanType) {
            ScanType.OCR -> {
                viewDataBinding.qrTextView.visibility = View.GONE
                viewDataBinding.linkWarningContainer.visibility = View.GONE
                viewDataBinding.ocrEditText.visibility = View.VISIBLE
                viewDataBinding.ocrEditText.isFocusable = true
                viewDataBinding.ocrEditText.isFocusableInTouchMode = true
                viewDataBinding.ocrEditText.isClickable = true
                viewDataBinding.ocrEditText.linksClickable = false

                if (message != null) {
                    setResultText(message)
                }
            }

            ScanType.QR -> {
                viewDataBinding.ocrEditText.visibility = View.GONE
                viewDataBinding.qrTextView.visibility = View.VISIBLE
                viewDataBinding.qrTextView.isClickable = true
                viewDataBinding.qrTextView.linksClickable = true
                viewDataBinding.qrTextView.autoLinkMask = Linkify.ALL

                if (message != null) {
                    setResultText(message)
                }

                viewDataBinding.qrTextView.post {
                    // Check if URLs are available after result text has been set
                    if (viewDataBinding.qrTextView.urls.isNotEmpty()) {
                        viewDataBinding.linkWarningContainer.visibility = View.VISIBLE
                    } else {
                        viewDataBinding.linkWarningContainer.visibility = View.GONE
                    }
                }
            }
        }

        viewDataBinding.cancelOcrResult.setOnClickListener {
            dismiss()
        }

        viewDataBinding.ocrSendButton.setOnClickListener {
            val chatMessage = getResultText().trim()
            if (LensSDK.getChatLetEnabled()) {
                LensSDK.sendMessageThroughChatLet(chatMessage)
                if (!(activity as LensSample).chatFragment.isAdded) {
                    (activity as LensSample).chatFragment.show(requireActivity().supportFragmentManager, "Chat")
                }
            }
            dismiss()
        }
    }

    private fun setResultText(text: String) {
        when (scanType) {
            ScanType.OCR -> {
                viewDataBinding.ocrEditText.setText(text)
            }

            ScanType.QR -> {
                viewDataBinding.qrTextView.text = text
            }
        }
    }

    private fun getResultText(): String {
        return when (scanType) {
            ScanType.OCR -> {
                viewDataBinding.ocrEditText.text.toString()
            }

            ScanType.QR -> {
                viewDataBinding.qrTextView.text.toString()
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(scanType: ScanType) = LiveTextResultFragment(scanType)
    }
}

enum class ScanType { OCR, QR }