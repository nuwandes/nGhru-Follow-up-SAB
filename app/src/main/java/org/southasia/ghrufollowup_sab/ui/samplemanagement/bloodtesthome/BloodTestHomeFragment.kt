package org.southasia.ghrufollowup_sab.ui.samplemanagement.bloodtesthome


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.otto.Subscribe
import io.reactivex.disposables.CompositeDisposable
import org.southasia.ghrufollowup_sab.R
import org.southasia.ghrufollowup_sab.binding.FragmentDataBindingComponent
import org.southasia.ghrufollowup_sab.databinding.BloodTestHomeFragmentBinding
import org.southasia.ghrufollowup_sab.databinding.SampleMangementHomeFragmentBinding
import org.southasia.ghrufollowup_sab.db.MemberTypeConverters
import org.southasia.ghrufollowup_sab.di.Injectable
import org.southasia.ghrufollowup_sab.event.*
import org.southasia.ghrufollowup_sab.jobs.SyncSampledProcessJob
import org.southasia.ghrufollowup_sab.ui.samplemanagement.bloodtesthome.cancel.CancelDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.util.*
import org.southasia.ghrufollowup_sab.vo.*
import org.southasia.ghrufollowup_sab.vo.request.ParticipantRequest
//import org.southasia.ghru.vo.Date
import org.southasia.ghrufollowup_sab.vo.request.SampleRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class BloodTestHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: BloodTestHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: BloodTestHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager

    private var fastingBloodGlucose: BloodTestData? = null

    private var totalCholesterol: BloodTestData? = null

    private var bloodTestData: BloodTests? = null

    private var participant: ParticipantRequest? = null

    var user: User? = null
    var meta: Meta? = null

//    ----------------------------------------------------------------------------

    var prefs : SharedPreferences? = null

    private var selectedParticipant: ParticipantListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sampleRequest = arguments?.getParcelable("SampleRequestResource")
            //L.d(sampleRequest.toString() + "sampleRequest ${sampleRequest?.storageId}")
        } catch (e: KotlinNullPointerException) {
            Crashlytics.logException(e)
        }

        disposables.add(
            TCHRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    // if (result == null) {
                    Timber.d(result.toString())
                    totalCholesterol = result
                    navController().popBackStack()
                    binding.linearLayoutTotalCholesterol.visibility = View.VISIBLE
                    updateProcessValidUI(binding.TCTextView)
                    binding.executePendingBindings()
                    // }
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            FBGRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    //if (result == null) {
                    Timber.d(result.toString())
                    fastingBloodGlucose = result
                    binding.linearLayoutFastingBloodGlucose.visibility = View.VISIBLE
                    updateProcessValidUI(binding.fbgTextView)
                    binding.executePendingBindings()
                    // }
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BloodTestHomeFragmentBinding>(
            inflater,
            R.layout.blood_test_home_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.sample = sampleRequest

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + selectedParticipant!!.participant_id)

        binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
        binding.titleGender.setText(selectedParticipant!!.gender)
        binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

        viewModel.setScreeningId(selectedParticipant!!.participant_id)

        viewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS)
            {
                participant = participantResource.data?.data
                participant?.meta = meta
                Log.d("FASTED_FRAG", "PAR_REQ_SUCCESS")
            }
            else if (participantResource?.status == Status.ERROR)
            {
                Log.d("FASTED_FRAG", "PAR_REQ_FAILED")
            }
            binding.executePendingBindings()
        })

        val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
        val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
        val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
        binding.titleAge.setText(participantAge + "Y")

        viewModel.setUser("user")
        viewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {
                // setupNavigationDrawer(userData.data)
                user = userData.data

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
                //meta?.registeredBy = user?.id
            }

        })

        if (fastingBloodGlucose != null) {
            binding.fbgCompleteView.visibility = View.VISIBLE
            binding.linearLayoutBlood.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)

        }

        if (totalCholesterol != null) {
            binding.TCCompleteView.visibility = View.VISIBLE
            binding.LinearLayoutTC.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)

        }

        fun isValied(): Boolean {
            return if (totalCholesterol != null && fastingBloodGlucose != null)
                true else {
                false
            }

        }

        binding.buttonSubmit.singleClick {

            val endTime: String = convertTimeTo24Hours()
            val endDate: String = getDate()
            val endDateTime:String = endDate + " " + endTime

            if (isValied())
            {
                bloodTestData = BloodTests(
                    tch = totalCholesterol,
                    fbg = fastingBloodGlucose)

                    participant?.meta?.endTime = endDateTime

                val bloodTestRequest = BloodTestRequest(meta = participant?.meta, body = bloodTestData)
                bloodTestRequest.screeningId = participant?.screeningId!!
                if(isNetworkAvailable()){
                    bloodTestRequest.syncPending =false
                }else{
                    bloodTestRequest.syncPending =true

                }

                viewModel.setBloodRequest(bloodTestRequest, bloodTestRequest.screeningId)

                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }
            else
            {
                binding.sampleValidationError = true

                if (fastingBloodGlucose == null) {
                    updateProcessErrorUI(binding.fbgTextView)
                }

                if (totalCholesterol == null)
                {
                    updateProcessErrorUI(binding.TCTextView)
                }

//                if (!isValied()) {
//                    updateProcessErrorUI(binding.TCTextView)
//                    updateProcessErrorUI(binding.fbgTextView)
//                }
            }

        }

        viewModel.syncBloodRequest?.observe(this, Observer { chkPocess ->

            if (chkPocess?.status == Status.SUCCESS)
            {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
                //Prefs.clear()
            }
            else if(chkPocess?.status == Status.ERROR){
//                Crashlytics.setString(
//                    "BloodTestRequest",
//                    BloodTestRequest(meta = meta, b).toString()
//                )
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + chkPocess.message.toString()))
            }
        })

        binding.linearLayoutBlood.singleClick {
            // viewModel.sampleValidationError.value = false
            binding.sampleValidationError = false
            //updateProcessValidUI(binding.fbgTextView)
            navController().navigate(R.id.action_sampleMangementHomeViewModel_to_FastingBloodGlucoseFragment)
        }

        binding.LinearLayoutTC.singleClick {
            //viewModel.sampleValidationError.value = false
            binding.sampleValidationError = false
            //updateProcessValidUI(binding.hb1AcTextView)
            navController().navigate(R.id.action_sampleMangementHomeViewModel_to_TotalCholesterolFragment)
        }

        binding.buttonCancel.setOnClickListener {
            val cancelDialogFragment = CancelDialogFragment()
            cancelDialogFragment.arguments = bundleOf("participant" to participant)
            cancelDialogFragment.show(fragmentManager!!)
        }
    }


    private fun updateProcessErrorUI(view: TextView) {
        view.setTextColor(Color.parseColor("#FF5E45"))
        view.setDrawbleLeftColor("#FF5E45")
    }

    private fun updateProcessValidUI(view: TextView) {
        view.setTextColor(Color.parseColor("#00548F"))
        view.setDrawbleLeftColor("#00548F")
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    @Subscribe
    fun onFastingBloodGlucoseDto(event: FastingBloodGlucoseDto) {
        Log.d("onFastingBloodo", event.toString())
        binding.linearLayoutFastingBloodGlucose.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
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

    private fun getAge(year: Int, month: Int, date: Int) : String
    {
        val dob : Calendar = Calendar.getInstance()
        val today : Calendar = Calendar.getInstance()

        dob.set(year, month, date)

        var age : Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
        {
            age--
        }

        val ageInt : Int = age
        val ageString : String = ageInt.toString()

        return ageString
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
