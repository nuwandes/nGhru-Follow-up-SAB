package org.nghru_ins.ghru.ui.samplemanagement.tubescanbarcode.errordialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_ins.ghru.R
import org.nghru_ins.ghru.binding.FragmentDataBindingComponent
import org.nghru_ins.ghru.databinding.ErrorTubeDialogFragmentBinding
import org.nghru_ins.ghru.di.Injectable
import org.nghru_ins.ghru.util.autoCleared
import org.nghru_ins.ghru.util.singleClick
import javax.inject.Inject


class ErrorDialogFragment : DialogFragment(), Injectable {

    val TAG = ErrorDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<ErrorTubeDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ErrorDialogViewModel

    lateinit var errorMsg: String;


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ErrorTubeDialogFragmentBinding>(
                inflater,
                R.layout.error_tube_dialog_fragment,
                container,
                false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        println(viewModel.errorMsg.value)
        viewModel.errorMsg.postValue(errorMsg)
        binding.buttonCancel.singleClick {
            //  ScanRefreshRxBus.getInstance().post(true)
            dismiss()
        }
    }

    fun setErrorMessage(error: String) {
        this.errorMsg = error
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        // creating the fullscreen dialog
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

}