package org.nghru_bd.ghru.ui.ecg.trace.complete

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
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_bd.ghru.R
import org.nghru_bd.ghru.binding.FragmentDataBindingComponent
import org.nghru_bd.ghru.databinding.EcgCompleteDialogFragmentBinding
import org.nghru_bd.ghru.di.Injectable
import org.nghru_bd.ghru.jobs.SyncECGJob
import org.nghru_bd.ghru.ui.ecg.trace.completed.CompletedDialogFragment
import org.nghru_bd.ghru.util.autoCleared
import org.nghru_bd.ghru.util.getLocalTimeString
import org.nghru_bd.ghru.util.singleClick
import org.nghru_bd.ghru.vo.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class CompleteDialogFragment : DialogFragment(), Injectable {

    val TAG = CompleteDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<EcgCompleteDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var confirmationdialogViewModel: CompleteDialogViewModel

    @Inject
    lateinit var jobManager: JobManager
    private var participant: ParticipantListItem? = null
    private var comment: String? = null
    private var device_id: String? = null
    var user: User? = null
    var meta: Meta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantListItem>("participant")!!
            comment = arguments?.getString("comment")
            device_id = arguments?.getString("deviceId")
        } catch (e: KotlinNullPointerException) {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<EcgCompleteDialogFragmentBinding>(
            inflater,
            R.layout.ecg_complete_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        confirmationdialogViewModel.setUser("user")

        confirmationdialogViewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {
                // setupNavigationDrawer(userData.data)

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                user = userData.data
                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
                //meta?.registeredBy = user?.id
            }

        })

        confirmationdialogViewModel.eCGSaveRemote?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                dismiss()
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {

                Crashlytics.setString("comment", comment.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("eCGSaveRemote " + participant.message.toString()))
                binding.progressBar.visibility = View.GONE
                binding.textViewError.setText(participant.message?.message)
                binding.textViewError.visibility = View.VISIBLE
                binding.executePendingBindings()
            }
        })
        binding.buttonAcceptAndContinue.singleClick {
            // if(binding,)
            val status = if (binding.radioGroup.checkedRadioButtonId == R.id.normal) {
                getString(R.string.ecg_check_normal)
            } else {
                getString(R.string.ecg_check_abnormal)
            }

            val endTime: String = convertTimeTo24Hours()
            val endDate: String = getDate()
            val endDateTime:String = endDate + " " + endTime

            meta?.endTime = endDateTime
            val mECGStatus = ECGStatus(status, comment, device_id, meta= meta)

            confirmationdialogViewModel.setLocalUpdateParticipantEcgStatus(participant!!)

            if (isNetworkAvailable())
            {
                confirmationdialogViewModel.setECGRemote(participant?.participant_id,mECGStatus, isNetworkAvailable())
            }
            else
            {
                mECGStatus.syncPending = true
                mECGStatus.screeningId = participant?.participant_id!!
                confirmationdialogViewModel.setEcgLocal(mECGStatus)

                dismiss()
                jobManager.addJobInBackground(SyncECGJob(participant?.participant_id, mECGStatus))
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)

            }
        }

        confirmationdialogViewModel.getLocalUpdateParticipantEcgStatus?.observe(this, Observer { bmStatus ->

            if (bmStatus?.status == Status.SUCCESS)
            {
                Toast.makeText(context, "Ecg status locally updated", Toast.LENGTH_LONG).show()
            }
            else if(bmStatus?.status == Status.ERROR)
            {
                Toast.makeText(context, "Update Ecg status failed", Toast.LENGTH_LONG).show()
            }
        })

        confirmationdialogViewModel.insertEcgLocal?.observe(this, Observer { ecgRes ->

            if (ecgRes?.status == Status.SUCCESS)
            {
                Toast.makeText(activity, "ECG locally saved", Toast.LENGTH_LONG).show()
            }
            else if (ecgRes?.status == Status.ERROR)
            {
                Crashlytics.setString("comment", comment.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("eCGSaveRemote " + ecgRes.message.toString()))
                binding.progressBar.visibility = View.GONE
                binding.textViewError.setText(ecgRes.message?.message)
                binding.textViewError.visibility = View.VISIBLE
                binding.executePendingBindings()
            }
        })

        binding.buttonCancel.singleClick {
            dismiss()
        }
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

        return dialog
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(now.time.toLocaleString())
            output = outputformat.format(date)
            return output
        }catch(p: ParseException){
            return ""
        }
    }

    private fun getDate(): String
    {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputformat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }

}