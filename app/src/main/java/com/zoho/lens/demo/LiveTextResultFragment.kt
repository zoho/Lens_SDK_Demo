package com.zoho.lens.demo

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zoho.lens.LensSDK
import kotlinx.android.synthetic.main.fragment_ocr_result.cancelOcrResult
import kotlinx.android.synthetic.main.fragment_ocr_result.link_warning_container
import kotlinx.android.synthetic.main.fragment_ocr_result.ocrEditText
import kotlinx.android.synthetic.main.fragment_ocr_result.ocr_send_button
import kotlinx.android.synthetic.main.fragment_ocr_result.qr_text_view


/**
 * Created by thirumoorthi Nagarajan on 14/09/23.
 * Jambav, Zoho Corporation.
 */


class LiveTextResultFragment(private val scanType: ScanType) : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ocr_result, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val message = args?.getString("chatMessage")

        dialog?.setCanceledOnTouchOutside(false)

        when (scanType) {
            ScanType.OCR -> {
                qr_text_view.visibility = View.GONE
                link_warning_container.visibility = View.GONE
                ocrEditText.visibility = View.VISIBLE
                ocrEditText.isFocusable = true
                ocrEditText.isFocusableInTouchMode = true
                ocrEditText.isClickable = true
                ocrEditText.linksClickable = false

                if (message != null) {
                    setResultText(message)
                }
            }

            ScanType.QR -> {
                ocrEditText.visibility = View.GONE
                qr_text_view.visibility = View.VISIBLE
                qr_text_view.isClickable = true
                qr_text_view.linksClickable = true
                qr_text_view.autoLinkMask = Linkify.ALL

                if (message != null) {
                    setResultText(message)
                }

                qr_text_view.post {
                    // Check if URLs are available after result text has been set
                    if (qr_text_view.urls.isNotEmpty()) {
                        link_warning_container.visibility = View.VISIBLE
                    } else {
                        link_warning_container.visibility = View.GONE
                    }
                }
            }
        }

        cancelOcrResult.setOnClickListener {
            dismiss()
        }

        ocr_send_button.setOnClickListener {
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
                ocrEditText.setText(text)
            }

            ScanType.QR -> {
                qr_text_view.text = text
            }
        }
    }

    private fun getResultText(): String {
        return when (scanType) {
            ScanType.OCR -> {
                ocrEditText.text.toString()
            }

            ScanType.QR -> {
                qr_text_view.text.toString()
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