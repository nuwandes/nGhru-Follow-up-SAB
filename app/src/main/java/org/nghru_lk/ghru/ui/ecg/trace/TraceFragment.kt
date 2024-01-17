package org.nghru_lk.ghru.ui.ecg.trace


import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_lk.ghru.R
import org.nghru_lk.ghru.binding.FragmentDataBindingComponent
import org.nghru_lk.ghru.databinding.TraceFragmentBinding
import org.nghru_lk.ghru.db.MemberTypeConverters
import org.nghru_lk.ghru.di.Injectable
import org.nghru_lk.ghru.ui.ecg.trace.complete.CompleteDialogFragment
import org.nghru_lk.ghru.ui.ecg.trace.reason.ReasonDialogFragment
import org.nghru_lk.ghru.util.autoCleared
import org.nghru_lk.ghru.util.fromJson
import org.nghru_lk.ghru.util.getLocalTimeString
import org.nghru_lk.ghru.util.singleClick
import org.nghru_lk.ghru.vo.*
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class TraceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TraceFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var verifyIDViewModel: TraceViewModel

    //private var participant: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

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
        val dataBinding = DataBindingUtil.inflate<TraceFragmentBinding>(
            inflater,
            R.layout.trace_fragment,
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

        // binding.viewModel = verifyIDViewModel

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

        //verifyIDViewModel.setScreeningId(selectedParticipant!!.participant_id)

        verifyIDViewModel.participant.observe(this, Observer { participantResource ->

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

        verifyIDViewModel.setUser("user")
        verifyIDViewModel.user?.observe(this, Observer { userData ->
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

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to selectedParticipant?.participant_id)
            reasonDialogFragment.show(fragmentManager!!)
        }


        binding.buttonSubmit.singleClick {

            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else {
                val completeDialogFragment = CompleteDialogFragment()
                completeDialogFragment.arguments = bundleOf("participant" to selectedParticipant, "comment" to binding.comment.text.toString(), "deviceId" to selectedDeviceID
                )
                completeDialogFragment.show(fragmentManager!!)
            }
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        verifyIDViewModel.setStationName(Measurements.ECG)
        verifyIDViewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter.notifyDataSetChanged()
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
