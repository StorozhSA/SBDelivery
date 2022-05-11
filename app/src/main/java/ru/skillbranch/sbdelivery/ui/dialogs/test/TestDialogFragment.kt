package ru.skillbranch.sbdelivery.ui.dialogs.test

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import ru.skillbranch.sbdelivery.R

class TestDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val linearlayout: View = layoutInflater.inflate(R.layout.fragment_test_dialog, null)
        dialogBuilder.setView(linearlayout)

        return dialogBuilder.create()
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}
