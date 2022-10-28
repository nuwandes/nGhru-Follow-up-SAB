package org.nghru_hpv.ghru.ui.participantlist.verificationcompleted

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.status_completed_dialog_fragment.*
import org.nghru_hpv.ghru.MeasurementListActivity
import org.nghru_hpv.ghru.R
import org.nghru_hpv.ghru.binding.FragmentDataBindingComponent
import org.nghru_hpv.ghru.databinding.StatusCompletedDialogFragmentBinding
import org.nghru_hpv.ghru.databinding.VerificationCompletedDialogFragmentBinding
import org.nghru_hpv.ghru.db.MemberTypeConverters
import org.nghru_hpv.ghru.ui.participantlist.attendance.consent.completed.ConsentCompletedDialogFragment
import org.nghru_hpv.ghru.util.autoCleared
import org.nghru_hpv.ghru.util.singleClick
import org.nghru_hpv.ghru.vo.ParticipantListItem

class VerificationCompletedDialogFragment : DialogFragment() {

    val TAG = VerificationCompletedDialogFragment::class.java.getSimpleName()

//    var prefs : SharedPreferences? = null

//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<VerificationCompletedDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var participant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    //private var isConsentExist : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantListItem>("single_participant")!!
            //isConsentExist = arguments?.getBoolean("isConsentExist")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<VerificationCompletedDialogFragmentBinding>(
            inflater,
            R.layout.verification_completed_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        btnContinue.singleClick {


            // measurement list displays with nav control, if validation path is ok

//            if (isConsentExist!!)
            if (participant?.isConsent!!)
            {
                val json: String = MemberTypeConverters.gson.toJson(participant)
                prefs?.edit()?.putString("single_participant", json)?.apply()
                val intent = Intent(activity, MeasurementListActivity::class.java)
                intent.putExtra("CONSENT_STATUS", true)
                startActivity(intent)
            }
            else
            {
                val consentCompletedDialogFragment = ConsentCompletedDialogFragment()
                consentCompletedDialogFragment.arguments = bundleOf("single_participant" to participant!!)
                consentCompletedDialogFragment.show(fragmentManager!!)
//                findNavController().navigate(
//                    R.id.action_global_ConsentFragment,
//                    bundleOf("single_participant" to participant!!))
                dismiss()
            }


        }

        btnCancel.singleClick {
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
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

}
