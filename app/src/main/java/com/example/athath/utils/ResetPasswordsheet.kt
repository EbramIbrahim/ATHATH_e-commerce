package com.example.athath.utils

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.athath.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setUpBottomDialogSheet(
    onSendEmail: (String) -> Unit
){

    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_layout, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()


    val emailEt = view.findViewById<EditText>(R.id.resetEmailEt)
    val sendBtn = view.findViewById<Button>(R.id.sendBtn)
    val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)


    sendBtn.setOnClickListener {
        val email = emailEt.text.toString().trim()
        onSendEmail(email)
        dialog.dismiss()
    }

    cancelBtn.setOnClickListener {
        dialog.dismiss()
    }


}