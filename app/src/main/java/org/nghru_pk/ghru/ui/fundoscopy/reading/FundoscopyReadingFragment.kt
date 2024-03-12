package org.nghru_pk.ghru.ui.fundoscopy.reading


import android.content.Context
import android.content.SharedPreferences
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
import org.nghru_pk.ghru.AppExecutors
import org.nghru_pk.ghru.R
import org.nghru_pk.ghru.binding.FragmentDataBindingComponent
import org.nghru_pk.ghru.databinding.FundosReadingBinding
import org.nghru_pk.ghru.db.MemberTypeConverters
import org.nghru_pk.ghru.di.Injectable
import org.nghru_pk.ghru.jobs.SyncFundoscopyJob
import org.nghru_pk.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.nghru_pk.ghru.ui.fundoscopy.reading.reason.ReasonDialogFragment
import org.nghru_pk.ghru.util.autoCleared
import org.nghru_pk.ghru.util.fromJson
import org.nghru_pk.ghru.util.getLocalTimeString
import org.nghru_pk.ghru.util.singleClick
import org.nghru_pk.ghru.vo.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class FundoscopyReadingFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors


    var binding by autoCleared<FundosReadingBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var fundoscopyReadingViewModel: FundoscopyReadingViewModel

    //private var participant: ParticipantRequest? = null

    private var adapter by autoCleared<AssetAdapter>()

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    @Inject
    lateinit var jobManager: JobManager

    private var didDilation: Boolean? = null
    private var cataractObservation : String = ""

    var prefs : SharedPreferences? = null
    private var selectedParticipant: ParticipantListItem? = null
    var user: User? = null
    var meta: Meta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FundosReadingBinding>(
            inflater,
            R.layout.fundos_reading,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = fundoscopyReadingViewModel

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + selectedParticipant!!.participant_id)

        binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
        binding.titleGender.setText(selectedParticipant!!.gender)
        binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

        val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
        val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
        val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
        binding.titleAge.setText(participantAge + "Y")

        //fundoscopyReadingViewModel.setScreeningId(selectedParticipant!!.participant_id)

        fundoscopyReadingViewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                //participant = participantResource.data?.data
                //participant?.meta = meta

                Log.d("BLOOD_PRESSURE_HOME", "PAR_REQ_SUCCESS")

            } else if (participantResource?.status == Status.ERROR) {

                Log.d("BLOOD_PRESSURE_HOME", "PAR_REQ_FAILED")
            }
            binding.executePendingBindings()
        })

        //binding.participant = participant

        fundoscopyReadingViewModel.setUser("user")
        fundoscopyReadingViewModel.user?.observe(this, Observer { userData ->
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

        val adapter = AssetAdapter(dataBindingComponent, appExecutors) { homeemumerationlistItem ->

        }

        this.adapter = adapter
        binding.assetList.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.assetList.setLayoutManager(layoutManager)

        fundoscopyReadingViewModel.asserts?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data?.data)
                if (assertsResource.data != null) {
                    adapter.submitList(assertsResource.data.data)
                    binding.icSync.visibility = View.GONE
                    binding.icText.visibility = View.GONE

                } else {
                    adapter.submitList(emptyList())
                    binding.icSync.visibility = View.VISIBLE
                    binding.icText.visibility = View.VISIBLE
                }
            }
        })
//        binding.syncLayout.singleClick {
//            fundoscopyReadingViewModel.setParticipant(
//                participant!!,
//                binding.comment.text.toString(),
//                selectedDeviceID!!,
//                didDilation!!,
//                cataractObservation
//            )
//
//        }

        fundoscopyReadingViewModel.fundoscopyComplete?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {
                Crashlytics.setString("comment", binding.comment.text.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("fundoscopyComplete " + participant.message.toString()))
                binding.executePendingBindings()
            }
        })
        binding.nextButton.singleClick {
            //print(participant.toString())
            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (validateFundoscopy()) {

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                meta?.endTime =  endDateTime

                val fundoscopyRequest = FundoscopyRequest(
                    comment = binding.comment.text.toString(),
                    device_id = selectedDeviceID!!,
                    pupil_dilation = didDilation!!,
                    meta = meta,
                    cataract_observation = cataractObservation)



                fundoscopyReadingViewModel.setLocalUpdateParticipantFundoStatus(selectedParticipant!!)

                if (isNetworkAvailable())
                {
                    fundoscopyRequest.screeningId = selectedParticipant?.participant_id!!
                    fundoscopyReadingViewModel.setFundoRequest(
                        selectedParticipant?.participant_id,
                        fundoscopyRequest
                    )
                }
                else
                {
                    fundoscopyRequest.syncPending = true
                    fundoscopyRequest.screeningId = selectedParticipant?.participant_id!!
                    fundoscopyReadingViewModel.setFundoLocal(fundoscopyRequest)

                    jobManager.addJobInBackground(
                        SyncFundoscopyJob(
                            selectedParticipant?.participant_id,
                            fundoscopyRequest
                        )
                    )
                    val completedDialogFragment = CompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                    completedDialogFragment.show(fragmentManager!!)
                }
            }
        }

        fundoscopyReadingViewModel.getLocalUpdateParticipantFundoStatus?.observe(this, Observer { bmStatus ->

            if (bmStatus?.status == Status.SUCCESS)
            {
                Toast.makeText(context, "Funduscopy status locally updated", Toast.LENGTH_LONG).show()
            }
            else if(bmStatus?.status == Status.ERROR)
            {
                Toast.makeText(context, "Update Fundoscopy status failed", Toast.LENGTH_LONG).show()
            }
        })

        fundoscopyReadingViewModel.insertFundoLocal?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS)
            {
                Toast.makeText(context, "Fundoscopy locally saved", Toast.LENGTH_LONG).show()
            }
            else if (participant?.status == Status.ERROR) {
                Crashlytics.setString("comment", binding.comment.text.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("fundoscopyComplete " + participant.message.toString()))
                binding.executePendingBindings()
            }
        })

        binding.buttonCancel.singleClick {

            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to selectedParticipant?.participant_id)
            reasonDialogFragment.show(fragmentManager!!)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter_Device_list = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter_Device_list);

        fundoscopyReadingViewModel.setStationName(Measurements.FUNDOSCOPY)
        fundoscopyReadingViewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter_Device_list.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceID = null
                } else {
                    binding.textViewDeviceError.visibility = View.GONE
                    selectedDeviceID = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.radioGroupAbove.setOnCheckedChangeListener({ radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.no) {
                binding.radioGroupAboveValue = false
                didDilation = false

            } else {
                binding.radioGroupAboveValue = false
                didDilation = true

            }
            binding.executePendingBindings()
        })
        binding.radioGroupCataractObserved.setOnCheckedChangeListener({ radioGroup, i ->
             if (radioGroup.checkedRadioButtonId == R.id.cataractLeft)
            {
                cataractObservation = "Left"
                binding.radioGroupCataractValue = false;
            }
            else if (radioGroup.checkedRadioButtonId == R.id.cataractRight)
            {
                cataractObservation = "Right"
                binding.radioGroupCataractValue = false;
            }
            else if(radioGroup.checkedRadioButtonId == R.id.cataractBoth)
            {
                cataractObservation = "Both"
                binding.radioGroupCataractValue = false;
            }
            else
            {
                cataractObservation = "No"
                binding.radioGroupCataractValue = false;
            }
            binding.executePendingBindings()

        })


    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    private fun validateFundoscopy(): Boolean {
        if(didDilation == null)
        {
            binding.radioGroupAboveValue = true
            binding.executePendingBindings()
            return false

        }
        else if(cataractObservation == "")
        {
            binding.radioGroupCataractValue = true
            binding.executePendingBindings()
            return false
        }
        else {
            return true
        }
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
