package pt.tetrapi.fgf.agroazores.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import pt.tetrapi.fgf.agroazores.databinding.DialogGenericBinding

/**
 * @Author Fernando Nunes
 * @Email fernandonunes@tetrapi.pt
 * @Company Tetrapi
 *
 */

class DialogFragment(
    private val fragmentActivity: FragmentActivity,
    private val title: String,
    private val message: String,
    private val icon: Int
): DialogFragment() {

    private lateinit var xml: DialogGenericBinding

    private var dismissListener: (() -> Unit)? = null

    private var positiveButtonTitle: String? = null
    private var positiveButtonColor: Int? = null
    private var positiveButtonCallback: View.OnClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        xml = DialogGenericBinding.inflate(LayoutInflater.from(context), container, false)
        return xml.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        xml.title.text = this.title
        xml.message.text = this.message
        xml.image.setImageDrawable(ContextCompat.getDrawable(fragmentActivity, icon))
        xml.root.setOnClickListener {
            dismiss()
        }

        if (positiveButtonTitle != null) {
            xml.positiveButton.visibility = View.VISIBLE
            xml.positiveButton.text = positiveButtonTitle
            if (positiveButtonColor != null) {
                xml.positiveButton.setBackgroundColor(ContextCompat.getColor(fragmentActivity, positiveButtonColor!!))
            }
            xml.positiveButton.setOnClickListener(positiveButtonCallback)
        }

        isCancelable = true
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun show() {
        show(fragmentActivity.supportFragmentManager, "GenericDialogFragment")
    }

    override fun dismiss() {
        dismissListener?.invoke()
        super.dismiss()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        this.dismissListener = listener
    }

    fun setPositionButton(title: String, callback: View.OnClickListener) {
        this.positiveButtonTitle = title
        this.positiveButtonCallback = callback
    }

    fun setPositionButton(title: String, color: Int, callback: View.OnClickListener) {
        this.positiveButtonTitle = title
        this.positiveButtonColor = color
        this.positiveButtonCallback = callback
    }
}