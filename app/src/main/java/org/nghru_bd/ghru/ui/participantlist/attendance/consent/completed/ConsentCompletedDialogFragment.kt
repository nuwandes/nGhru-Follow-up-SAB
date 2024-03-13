package org.nghru_bd.ghru.ui.participantlist.attendance.consent.completed

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
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import org.nghru_bd.ghru.MeasurementListActivity
import org.nghru_bd.ghru.R
import org.nghru_bd.ghru.binding.FragmentDataBindingComponent
import org.nghru_bd.ghru.databinding.ConsentCompletedDialogFragmentBinding
import org.nghru_bd.ghru.db.MemberTypeConverters.gson
import org.nghru_bd.ghru.util.autoCleared
import org.nghru_bd.ghru.util.singleClick
import org.nghru_bd.ghru.vo.ParticipantListItem
import java.lang.Exception

class ConsentCompletedDialogFragment : DialogFragment() {

    val TAG = ConsentCompletedDialogFragment::class.java.getSimpleName()

    var prefs : SharedPreferences? = null

//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ConsentCompletedDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

//    @Inject
//    lateinit var statusCompletedDialogViewModel: ConsentCompletedDialogViewModel

    private var participant: ParticipantListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantListItem>("single_participant")!!
        } catch (e: Exception) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ConsentCompletedDialogFragmentBinding>(
            inflater,
            R.layout.consent_completed_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        Log.d("CONSENT_COMPLETED_FRAG", "PARTICIPANT: " + participant)

        binding.yesButton.singleClick {

            findNavController().navigate(R.id.action_global_ConsentFragment, bundleOf("single_participant" to participant!!))
            dismiss()
        }

        binding.noButton.singleClick {

            val json1: String = gson.toJson(participant)
            prefs?.edit()?.putString("single_participant", json1)?.apply()
            val intent = Intent(activity, MeasurementListActivity::class.java)
            intent.putExtra("CONSENT_STATUS", false)
            startActivity(intent)
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
