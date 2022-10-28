package org.nghru_ins.ghru.ui.covid.reason

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import org.nghru_ins.ghru.R
import org.nghru_ins.ghru.binding.FragmentDataBindingComponent
import org.nghru_ins.ghru.databinding.CovidReasonDialogFragmentBinding
import org.nghru_ins.ghru.databinding.HeightReasonDialogFragmentBinding
import org.nghru_ins.ghru.di.Injectable
import org.nghru_ins.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.nghru_ins.ghru.util.autoCleared
import org.nghru_ins.ghru.util.hideKeyboard
import org.nghru_ins.ghru.util.singleClick
import org.nghru_ins.ghru.vo.Status
import org.nghru_ins.ghru.vo.request.BodyMeasurementData
import org.nghru_ins.ghru.vo.request.CancelRequest
import org.nghru_ins.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ReasonDialogFragment : DialogFragment(), Injectable {

    val TAG = ReasonDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<CovidReasonDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ReasonDialogViewModel


    lateinit var cancelRequest: CancelRequest

    private var participant: ParticipantRequest? = null

    @Inject
    lateinit var jobManager: JobManager

    private lateinit var bodyMeasurementData: BodyMeasurementData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CovidReasonDialogFragmentBinding>(
            inflater,
            R.layout.covid_reason_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cancelRequest = CancelRequest()

        bodyMeasurementData = BodyMeasurementData()

        cancelRequest = CancelRequest(stationType = "covid-questionnaire")

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            // println("i $i" + radioGroup.checkedRadioButtonId)
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonParticipantFactor -> cancelRequest.reason =
                    getString(R.string.participant_factor)
                R.id.radioButtonTechnicalProblem -> cancelRequest.reason =
                    getString(R.string.technical_problem)
                R.id.radioButtonParticipantRefuse -> cancelRequest.reason =
                    getString(R.string.participant_refeuse)
                else -> {
                    // binding.textViewError.text = getString(R.string.app_error_please_select_one)
                    binding.textViewError.visibility = View.GONE
                }
            }
//            if (radioGroup.checkedRadioButtonId == R.id.radioButtonOther) {
//                binding.textInputEditTextOther.visibility = View.VISIBLE
//                binding.textInputEditTextOther.shoKeyboard()
//            } else {
//                binding.textInputEditTextOther.visibility = View.GONE
//            }
            binding.executePendingBindings()
        }

//        if (contraindications != null) {
//            binding.radioGroup.check(R.id.radioButtonContra)
//        }

        binding.buttonAcceptAndContinue.singleClick {
            binding.root.hideKeyboard()
            if (binding.radioGroup.checkedRadioButtonId == -1) {
                binding.textViewError.text = getString(R.string.app_error_please_select_one)
                binding.textViewError.visibility = View.VISIBLE

            } else
//                if (binding.radioGroup.checkedRadioButtonId == R.id.radioButtonOther) {
//                cancelRequest.reason = binding.textInputEditTextOther.text.toString()
//            }
            if (binding.radioGroup.checkedRadioButtonId != -1) {

                var comment = binding.comment.text.toString()
                cancelRequest.comment = comment

                cancelRequest.syncPending = !isNetworkAvailable()
                cancelRequest.screeningId = participant?.screeningId!!

                viewModel.setLogin(participant, cancelRequest)
            }


        }

        viewModel.cancelId?.observe(this, Observer { cancelObserver ->
            if (cancelObserver?.status == Status.SUCCESS) {
                dismiss()
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to true)
                completedDialogFragment.show(fragmentManager!!)

            } else if (cancelObserver?.status == Status.ERROR) {
                binding.textViewError.text = cancelObserver.message?.message.toString()
            }
        })
        binding.buttonCancel.singleClick {
            binding.root.hideKeyboard()
            dismiss()
        }
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // creating the fullscreen dialog
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
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
