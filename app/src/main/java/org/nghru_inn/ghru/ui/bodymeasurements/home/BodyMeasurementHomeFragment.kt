package org.nghru_inn.ghru.ui.bodymeasurements.home


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
import io.reactivex.disposables.CompositeDisposable
import org.nghru_inn.ghru.R
import org.nghru_inn.ghru.binding.FragmentDataBindingComponent
import org.nghru_inn.ghru.databinding.BodyMeasurementsHomeFragmentBinding
import org.nghru_inn.ghru.db.MemberTypeConverters
import org.nghru_inn.ghru.di.Injectable
import org.nghru_inn.ghru.event.BodyMeasurementDataEventType
import org.nghru_inn.ghru.event.BodyMeasurementDataRxBus
import org.nghru_inn.ghru.event.BusProvider
import org.nghru_inn.ghru.ui.bodymeasurements.home.completed.CompletedDialogFragment
import org.nghru_inn.ghru.ui.bodymeasurements.home.reason.ReasonDialogFragment
import org.nghru_inn.ghru.util.*
import org.nghru_inn.ghru.vo.Meta
import org.nghru_inn.ghru.vo.ParticipantListItem
import org.nghru_inn.ghru.vo.Status
import org.nghru_inn.ghru.vo.User
import org.nghru_inn.ghru.vo.request.*
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BodyMeasurementHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: BodyMeasurementsHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: BodyMeasurementHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager


    private var height: BodyMeasurementData? = null
    private var hipWaist: BodyMeasurementData? = null
    private var bodyComposition: BodyMeasurementData? = null

    private var bodyMeasurement: BodyMeasurement? = null

    private var participantRequest: ParticipantRequest? = null

    var user: User? = null
    var meta: Meta? = null

//    ----------------------------------------------------------------------------

    var prefs : SharedPreferences? = null

    private var selectedParticipant: ParticipantListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!

//            prefs = PreferenceManager.getDefaultSharedPreferences(context)
//
//            val json : String? = prefs?.getString("single_participant","")
//            selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
//
//            Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + selectedParticipant!!.participant_id)

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            BodyMeasurementDataRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    // if (result == null) {
                    Timber.d(result.bodyMeasurementData.toString())
                    when (result.eventType) {
                        BodyMeasurementDataEventType.HEIGHT -> {
                            height = result.bodyMeasurementData
//                            height!!.data!!.height!!.value = selectedParticipant!!.height!!.toDouble()
//                            height!!.data!!.height!!.unit = "cm"
                            navController().popBackStack()
                            binding.linearLayoutHeightx.visibility = View.VISIBLE
                        }
                        BodyMeasurementDataEventType.HIP_WAIST -> {
                            hipWaist = result.bodyMeasurementData
                            navController().popBackStack()
                            binding.linearLayoutHipWaistX.visibility = View.VISIBLE
                        }
                        BodyMeasurementDataEventType.BODY_COMOSITION -> {
                            bodyComposition = result.bodyMeasurementData
                            navController().popBackStack()
                            binding.linearLayoutBodyComposition.visibility = View.VISIBLE
                        }
                        else -> {
                        }
                    }
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
        val dataBinding = DataBindingUtil.inflate<BodyMeasurementsHomeFragmentBinding>(
            inflater,
            R.layout.body_measurements_home_fragment,
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
        binding.participant = participantRequest

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + selectedParticipant!!.participant_id)

        binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
        binding.titleGender.setText(selectedParticipant!!.gender)
        binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

//        height = BodyMeasurementData()

        viewModel.setScreeningId(selectedParticipant!!.participant_id)

        viewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta

                Log.d("BODY_MEASUREMENT_HOME", "PAR_REQ_SUCCESS")
//                if (!participantResource.data?.stationStatus!!) {
//                    val bundle = bundleOf("ParticipantRequest" to participantResource.data?.data)
//                    findNavController().navigate(R.id.action_manualScanFragment_to_measurementsFragment, bundle)
//                } else {
//                    val stationCheckDialogFragment = StationCheckDialogFragment()
//                    stationCheckDialogFragment.show(fragmentManager!!)
//                }
            } else if (participantResource?.status == Status.ERROR) {
                Log.d("BODY_MEASUREMENT_HOME", "PAR_REQ_FAILED")
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
//                errorDialogFragment.show(fragmentManager!!)
                //Crashlytics.logException(Exception(participantResource.toString()))
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

        if (height != null) {
            if (height?.skip == null) {

                binding.heightCompleteView.visibility = View.VISIBLE
                binding.linearLayoutHeight.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)
            } else {

                binding.heightSkippedView.visibility = View.VISIBLE
//                binding.linearLayoutHeight.background =
//                    resources.getDrawable(R.drawable.ic_process_complete_bg, null)
            }
        }

        if (hipWaist != null) {
            if (hipWaist?.skip == null) {
                binding.hipWaistCompleteView.visibility = View.VISIBLE
                binding.linearLayoutHipWaist.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)
            } else {
                binding.hipWaistSkippedView.visibility = View.VISIBLE
                // binding.linearLayoutHipWaist.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)
            }
        }

        if (bodyComposition != null) {

            if (bodyComposition?.skip == null) {
                binding.bodyCompositionCompleteView.visibility = View.VISIBLE
                binding.linearLayoutBodyComposition.background =
                    resources.getDrawable(R.drawable.ic_process_complete_bg, null)
            } else {
                binding.bodyCompositionSkippedView.visibility = View.VISIBLE
//                binding.linearLayoutBodyComposition.background =
//                    resources.getDrawable(R.drawable.ic_process_complete_bg, null)
            }
        }
        binding.errorView.collapse()

        viewModel.sampleMangementPocess?.observe(this, Observer { sampleMangementPocess ->
            // Timber.d(sampleMangementPocess.toString())
            if (sampleMangementPocess?.status == Status.SUCCESS) {
                activity!!.finish()
            } else if (sampleMangementPocess?.status == Status.ERROR) {
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
                //Crashlytics.logException(Exception(sampleMangementPocess.message?.message))
                //var error = accessToken.dat
            }
        })



//        viewModel.participantMetas?.observe(this, Observer { sampleMangementPocess ->
//
//            if (sampleMangementPocess?.status == Status.SUCCESS) {
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                completedDialogFragment.show(fragmentManager!!)
//            } else if (sampleMangementPocess?.status == Status.ERROR) {
//                Crashlytics.setString(
//                    "BodyMeasurementMeta",
//                    BodyMeasurementMeta(meta = meta, body = bodyMeasurement).toString()
//                )
//                Crashlytics.setString("participant", participantRequest.toString())
//                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
//            }
//        })


        viewModel.bodyMeasurementMetaOffline?.observe(this, Observer { sampleMangementPocess ->

            if(sampleMangementPocess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
            }

            if (sampleMangementPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(sampleMangementPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "BodyMeasurementMeta",
                    BodyMeasurementMeta(meta = meta, body = bodyMeasurement).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
            }
        })




        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.buttonSubmit.singleClick {

            val bodyValue = BodyMeasurementValueData(BodyMeasurementValueDto("cm", selectedParticipant!!.height!!.toDouble()))
            height = BodyMeasurementData("","", bodyValue, CancelRequest("", "", ""))

            if (hipWaist != null && bodyComposition != null) {
                bodyMeasurement =
                    BodyMeasurement(height = height, hipWaist = hipWaist, bodyComposition = bodyComposition)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                meta?.endTime = endDateTime

                val bodyMeasurementMeta = BodyMeasurementMeta(meta = meta, body = bodyMeasurement)
                bodyMeasurementMeta.screeningId = selectedParticipant!!.participant_id!!
                //bodyMeasurementMeta.syncPending = !isNetworkAvailable()
                if(isNetworkAvailable()){
                    bodyMeasurementMeta.syncPending =false
                }else{
                    bodyMeasurementMeta.syncPending =true

                }
                viewModel.setBodyMeasurementMeta(bodyMeasurementMeta)

                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
//                if (isNetworkAvailable()) {
//
//
////                    viewModel.setBodyMeasurementMeta(
////                        BodyMeasurementMeta(meta = meta, body = bodyMeasurement),
////                        participantRequest!!
////                    )
//
//                    val bodyMeasurementMeta = BodyMeasurementMeta(meta = meta, body = bodyMeasurement)
//                    bodyMeasurementMeta.screeningId = participantRequest?.screeningId!!
//                    bodyMeasurementMeta.syncPending = !isNetworkAvailable()
//                    viewModel.setBodyMeasurementMeta(bodyMeasurementMeta)
//
//                    binding.progressBar.visibility = View.VISIBLE
//                    binding.buttonSubmit.visibility = View.GONE
//                } else {
//
//                    jobManager.addJobInBackground(
//                        SyncBodyMeasurementMetaJob(
//                            bodyMeasurementRequest = BodyMeasurementMeta(meta = meta, body = bodyMeasurement),
//                            screeningId = participantRequest?.screeningId!!
//                        )
//                    )
//                    val completedDialogFragment = CompletedDialogFragment()
//                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                    completedDialogFragment.show(fragmentManager!!)
//                }


            } else {
                binding.errorView.expand()

                ///Change  viewModel.sampleValidationError.value = true
                binding.sampleValidationError = true
                if (height == null) {
                    updateProcessErrorUI(binding.heightTextView)
                }

                if (hipWaist == null) {
                    updateProcessErrorUI(binding.hipWaistextView)
                }

                if (bodyComposition == null) {
                    updateProcessErrorUI(binding.bodyCompositionTextView)

                }
                binding.executePendingBindings()
            }
        }

//        binding.linearLayoutHeight.singleClick {
//            // viewModel.sampleValidationError.value = false
//
//            binding.sampleValidationError = false
//            updateProcessValidUI(binding.heightTextView)
//            updateProcessValidUI(binding.hipWaistextView)
//            updateProcessValidUI(binding.bodyCompositionTextView)
//            //updateProcessValidUI(binding.fbgTextView)
//            navController().navigate(R.id.action_BodyMeasurementHomeFragment_to_HeightFragment)
//        }



        binding.linearLayoutBodyComposition.singleClick {
            //  viewModel.sampleValidationError.value = false
            binding.sampleValidationError = false
            updateProcessValidUI(binding.heightTextView)
            updateProcessValidUI(binding.hipWaistextView)
            updateProcessValidUI(binding.bodyCompositionTextView)
            //updateProcessValidUI(binding.hb1AcTextView)
            navController().navigate(R.id.action_BodyMeasurementHomeFragment_to_BodyCompositionFragment)
        }


        binding.linearLayoutHipWaist.singleClick {
            //  viewModel.sampleValidationError.value = false
            binding.sampleValidationError = false
            updateProcessValidUI(binding.heightTextView)
            updateProcessValidUI(binding.hipWaistextView)
            updateProcessValidUI(binding.bodyCompositionTextView)
            //updateProcessValidUI(binding.hb1AcTextView)
            navController().navigate(R.id.action_BodyMeasurementHomeFragment_to_HipWaistFragment)
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
