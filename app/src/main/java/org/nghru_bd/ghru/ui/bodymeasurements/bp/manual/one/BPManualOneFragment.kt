package org.nghru_bd.ghru.ui.bodymeasurements.bp.manual.one


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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import org.nghru_bd.ghru.BuildConfig
import org.nghru_bd.ghru.R
import org.nghru_bd.ghru.binding.FragmentDataBindingComponent
import org.nghru_bd.ghru.databinding.BPManualOneFragmentBinding
import org.nghru_bd.ghru.db.MemberTypeConverters
import org.nghru_bd.ghru.di.Injectable
import org.nghru_bd.ghru.event.BPRecordRxBus
import org.nghru_bd.ghru.jobs.SyncBloodPresureRequestJob
import org.nghru_bd.ghru.ui.bodymeasurements.bp.reason.ReasonDialogFragment
import org.nghru_bd.ghru.ui.bodymeasurements.review.completed.CompletedDialogFragment
import org.nghru_bd.ghru.util.*
import org.nghru_bd.ghru.vo.*
//import org.southasia.ghru.vo.Date
import org.nghru_bd.ghru.vo.request.BloodPressureMetaRequest
import org.nghru_bd.ghru.vo.request.BloodPresureItemRequest
import org.nghru_bd.ghru.vo.request.BloodPresureRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList


class BPManualOneFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // private var measurement: BodyMeasurement? = null

    var binding by autoCleared<BPManualOneFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var bPManualOneViewModel: BPManualOneViewModel


    //private var participantRequest: ParticipantRequest? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BPRecordAdapter
    private val disposables = CompositeDisposable()
    private var recordList: ArrayList<BloodPressure> = ArrayList()
    var isCriticalRecordFound: Boolean = false

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null
    var user: User? = null
    var meta: Meta? = null

    @Inject
    lateinit var jobManager: JobManager

    var prefs : SharedPreferences? = null
    private var selectedParticipant: ParticipantListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //   measurement = arguments?.getParcelable<BodyMeasurement>(Constants.ARG_BODY_MEASURMENT)!!
            //participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!

            //   Log.d("measurement", measurement?.height?.value)
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BPManualOneFragmentBinding>(
                inflater,
                R.layout.b_p_manual_one_fragment,
                container,
                false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.expandProcedure = false
        binding.linearLayoutPrepContainer.collapse()
        binding.linearLayoutMessageContainer.collapse()

        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        adapter = BPRecordAdapter(recordList)
        binding.recyclerView.adapter = adapter

        disposables.add(
                BPRecordRxBus.getInstance().toObservable()
                        .subscribe({ result ->

                            Timber.d(result.toString())
                            if (!recordList.contains(result)) {
                                recordList.add(result)
                                adapter.notifyDataSetChanged()

                                if (result.systolic.value?.toInt()!! > 180 || result.diastolic.value?.toInt()!! > 120) {
                                    isCriticalRecordFound = true
                                }

                            }

                        }, { error ->
                            print(error)
                            error.printStackTrace()
                        }))
        disposables.add(
                BPRecordRxBus.getInstance().toObservableReset()
                        .subscribe({ result ->

                            if (result == 1) {
                                recordList.clear()
                                adapter.notifyDataSetChanged()
                                isCriticalRecordFound = false

                            }

                        }, { error ->
                            print(error)
                            error.printStackTrace()
                        }))

        return dataBinding.root
    }

    var mBloodPressureMetaRequest: BloodPressureMetaRequest? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + selectedParticipant!!.participant_id)

        binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
        binding.titleGender.setText(selectedParticipant!!.gender)
        binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

        //bPManualOneViewModel.setScreeningId(selectedParticipant!!.participant_id)

        bPManualOneViewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                //participantRequest = participantResource.data?.data
                //participantRequest?.meta = meta

                Log.d("BLOOD_PRESSURE_HOME", "PAR_REQ_SUCCESS")

            } else if (participantResource?.status == Status.ERROR) {

                Log.d("BLOOD_PRESSURE_HOME", "PAR_REQ_FAILED")
            }
            binding.executePendingBindings()
        })

        val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
        val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
        val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
        binding.titleAge.setText(participantAge + "Y")

        bPManualOneViewModel.setUser("user")
        bPManualOneViewModel.user?.observe(this, Observer { userData ->
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

        //binding.participant = participantRequest
        // bPManualOneViewModel.setBodyMeasurement(measurement!!)
        binding.bloodPressure = bPManualOneViewModel.getBloodPressure().value

        if (recordList.count() > 0) {
            binding.linearLayoutMessageContainer.expand()
        } else {
            binding.linearLayoutMessageContainer.collapse()
        }
        validateNextButton()
        binding.executePendingBindings()
        binding.nextButton.singleClick {

            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else {


                val bloodPresureRequestList: ArrayList<BloodPresureItemRequest> = ArrayList()
                recordList?.forEach {
                    val mBloodPressureItemRequest: BloodPresureItemRequest = BloodPresureItemRequest(
                        systolic = it.systolic.value?.toInt()!!,
                        diastolic = it.diastolic.value?.toInt()!!,
                        pulse = it.pulse.value?.toInt()!!,
                        arm = it.arm.value?.toString()!!
                    )
                    bloodPresureRequestList.add(mBloodPressureItemRequest)
                }
                val mBloodPressureRequest: BloodPresureRequest = BloodPresureRequest(
                    comment = binding.comment.text.toString(),
                    device_id = selectedDeviceID.toString()
                )
                mBloodPressureRequest.syncPending = !isNetworkAvailable()
                mBloodPressureRequest.screeningId = selectedParticipant?.participant_id!!
                mBloodPressureRequest.bloodPresureRequestList = bloodPresureRequestList

                val eTime: String = convertTimeTo24Hours()
                val eDate: String = getDate()
                val eDateTime:String = eDate + " " + eTime

                meta?.endTime = eDateTime

                mBloodPressureMetaRequest = BloodPressureMetaRequest(meta = meta!!, body = mBloodPressureRequest)

                bPManualOneViewModel.setLocalUpdateParticipantBPStatus(selectedParticipant!!)

                if (isNetworkAvailable())
                {
                    mBloodPressureMetaRequest?.syncPending = !isNetworkAvailable()

                    bPManualOneViewModel.setBloodPressureMetaRequestRemote(
                        mBloodPressureMetaRequest!!,
                        selectedParticipant?.participant_id!!
                    )
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textViewError.setText("")
                    binding.textViewError.visibility = View.GONE
                } else {
                    mBloodPressureMetaRequest?.syncPending = true
                    bPManualOneViewModel.setbPMetaLocal(mBloodPressureMetaRequest!!)

                    jobManager.addJobInBackground(
                        SyncBloodPresureRequestJob(
                            selectedParticipant?.participant_id!!,
                            mBloodPressureMetaRequest!!
                        )
                    )
                    val completedDialogFragment = CompletedDialogFragment()
                    completedDialogFragment.show(fragmentManager!!)
                }
            }
        }

        bPManualOneViewModel.getLocalUpdateParticipantBPStatus?.observe(this, Observer { bmStatus ->

            if (bmStatus?.status == Status.SUCCESS)
            {
                Toast.makeText(context, "Blood pressure status locally updated", Toast.LENGTH_LONG).show()
            }
            else if(bmStatus?.status == Status.ERROR)
            {
                Toast.makeText(context, "Update Blood pressure status failed", Toast.LENGTH_LONG).show()
            }
        })

        bPManualOneViewModel.insertbPMetaLocal?.observe(this, Observer {
            binding.progressBar.visibility = View.GONE
            if (it.status.equals(Status.SUCCESS)) {

                Toast.makeText(context, "Blood Pressure locally saved", Toast.LENGTH_LONG).show()
            } else if (it?.status == Status.ERROR) {
                Crashlytics.setString("mBloodPressureMetaRequest", mBloodPressureMetaRequest.toString())
                Crashlytics.setString("participant", selectedParticipant?.participant_id.toString())
                Crashlytics.logException(Exception("bloodPressureRequestRemote " + it.message.toString()))
                binding.textViewError.visibility = View.VISIBLE
                binding.textViewError.setText(it.message?.message)

            }
        })

        bPManualOneViewModel.bloodPressureRequestRemote?.observe(this, Observer {
            binding.progressBar.visibility = View.GONE
            if (it.status.equals(Status.SUCCESS)) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.show(fragmentManager!!)
            } else if (it?.status == Status.ERROR) {
                Crashlytics.setString("mBloodPressureMetaRequest", mBloodPressureMetaRequest.toString())
                Crashlytics.setString("participant", selectedParticipant?.participant_id.toString())
                Crashlytics.logException(Exception("bloodPressureRequestRemote " + it.message.toString()))
                binding.textViewError.visibility = View.VISIBLE
                binding.textViewError.setText(it.message?.message)

            }
        })

        binding.textViewSkip.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to selectedParticipant?.participant_id)
            reasonDialogFragment.show(fragmentManager!!)
        }
        if (BuildConfig.DEBUG) {
//            bPManualOneViewModel.getBloodPressure().value?.systolic?.value = "120"
//            bPManualOneViewModel.getBloodPressure().value?.diastolic?.value = "90"
//            bPManualOneViewModel.getBloodPressure().value?.pulse?.value = "80"
        }

//        binding.previousButton.singleClick {
//            navController().popBackStack()
//        }
//
//
//        binding.textViewSkip.singleClick {
//
//            val skipDialogFragment = SkipDialogFragment()
//            val bundle = bundleOf("ParticipantRequest" to participantRequest, Constants.ARG_BODY_MEASURMENT to bPManualOneViewModel.getBodyMeasurement().value)
//            skipDialogFragment.arguments = bundle
//            skipDialogFragment.show(fragmentManager!!)
//        }

        binding.prepEC.setOnClickListener {
            if (binding.expandProcedure!!) {

                binding.linearLayoutPrepContainer.collapse()
                binding.expandProcedure = false

            } else {

                binding.linearLayoutPrepContainer.expand()
                binding.expandProcedure = true
            }
            binding.executePendingBindings()

        }

        binding.buttonAddTest.singleClick {

            //val bundle = bundleOf("ParticipantRequest" to participantRequest, Constants.ARG_BODY_MEASURMENT to bPManualOneViewModel.getBodyMeasurement().value)
            val bundle = bundleOf(Constants.ARG_BODY_MEASURMENT to bPManualOneViewModel.getBodyMeasurement().value)
            navController().navigate(R.id.action_pPManualOneFragment_to_bPManualTwoFragment, bundle)

        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
//        deviceListName.add("device 1- blood pressure")
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter)

//        bPManualOneViewModel.setStationName(Measurements.BLOOD_PRESSURE)
        bPManualOneViewModel.setStationName(Measurements.BLOOD_PRESSURE)
        bPManualOneViewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedDeviceID = null
                } else {
                    binding.textViewDeviceError.visibility = View.GONE
                    selectedDeviceID = deviceListObject[position - 1].device_id
//                    selectedDeviceID = "635e4430-81d6-11e9-8f12-01dcd2e55ca0"
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

    }

    private fun validateNextButton() {

        if (recordList.count() > 2) {
            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.nextButton.setDrawableRightColor("#0A1D53")
            binding.nextButton.isEnabled = true
        } else {
            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"));
            binding.nextButton.setDrawableRightColor("#AED6F1")
            binding.nextButton.isEnabled = false
        }

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
