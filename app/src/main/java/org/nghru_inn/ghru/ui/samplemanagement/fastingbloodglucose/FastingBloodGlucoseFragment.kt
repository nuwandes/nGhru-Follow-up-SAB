package org.nghru_inn.ghru.ui.samplemanagement.fastingbloodglucose


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import br.com.ilhasoft.support.validation.Validator
import io.reactivex.disposables.CompositeDisposable
import org.nghru_inn.ghru.R
import org.nghru_inn.ghru.binding.FragmentDataBindingComponent
import org.nghru_inn.ghru.databinding.FastingBloodGlucoseFragmentBinding
import org.nghru_inn.ghru.db.MemberTypeConverters
import org.nghru_inn.ghru.di.Injectable
import org.nghru_inn.ghru.event.BusProvider
import org.nghru_inn.ghru.event.FBGRxBus
import org.nghru_inn.ghru.sync.CholesterolcomEventType
import org.nghru_inn.ghru.sync.JanaCareGlucoseRxBus
import org.nghru_inn.ghru.ui.samplemanagement.fastingbloodglucose.cancel.CancelDialogFragment
import org.nghru_inn.ghru.util.*
import org.nghru_inn.ghru.util.Constants.Companion.FBG_MAX_VAL
import org.nghru_inn.ghru.util.Constants.Companion.FBG_MIN_VAL
import org.nghru_inn.ghru.vo.*
import org.nghru_inn.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class FastingBloodGlucoseFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<FastingBloodGlucoseFragmentBinding>()


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var samplemangementfastingbloodglucoseViewModel: FastingBloodGlucoseViewModel

    private val disposables = CompositeDisposable()

    private lateinit var fastingBloodGlucose: FastingBloodGlucose

    private lateinit var validator: Validator

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    var prefs : SharedPreferences? = null
    private var selectedParticipant: ParticipantListItem? = null

    private var participant: ParticipantRequest? = null
    var meta: Meta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        disposables.add(
//            JanaCareGlucoseRxBus.getInstance().toObservable()
//                .subscribe({ result ->
//                    if (result != null) {
//                        binding.textInputEditTextFBG.setText(result.result.result)
//                        binding.textviewFbgAinaValue.setText(result.result.result.toString())
//                        samplemangementfastingbloodglucoseViewModel.fastingBloodGlucose.value = result.result.result.toString()
//                    }
//
//                }, { error ->
//                    error.printStackTrace()
//                })
//        )
        disposables.add(
            JanaCareGlucoseRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "household SyncCommentLifecycleObserver ${result}")
                    //handleSyncResponse(result)
                    when (result.eventType) {
                        CholesterolcomEventType.FASTING_BLOOD_GLUCOSE -> {
                            if (result != null) {
                                binding.textInputEditTextFBG.setText(result.result.result.toString())
                                binding.textviewFbgAinaValue.setText(result.result.result.toString())
                                binding.fastingBloodGlucose!!.value = result.result.result.toString()
                                binding.fastingBloodGlucose!!.probeId = result.result.lotNumber.toString()

                            }

                        }
                    }

                }, { error ->
                    error.printStackTrace()
                })
        )
        fastingBloodGlucose = FastingBloodGlucose.build()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FastingBloodGlucoseFragmentBinding>(
            inflater,
            R.layout.fasting_blood_glucose_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        validator = Validator(binding)
        binding.root.hideKeyboard()
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.fastingBloodGlucose = fastingBloodGlucose

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("FBG_HOME", " DATA: " + selectedParticipant!!.participant_id)

        binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
        binding.titleGender.setText(selectedParticipant!!.gender)
        binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

        val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
        val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
        val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
        binding.titleAge.setText(participantAge + "Y")

        samplemangementfastingbloodglucoseViewModel.setUser("user")
        samplemangementfastingbloodglucoseViewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                meta = Meta(collectedBy = userData.data?.id, startTime = sDateTime)
                //  meta?.registeredBy = userData.data?.id
            }

        })

        binding.buttonSubmit.singleClick {
            binding.root.hideKeyboard()
            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (isValidFBGRange()) {

                binding.fastingBloodGlucose?.deviceId = selectedDeviceID!!
                val mFastingBloodGlucoseDto = BloodTestData(
                    value = binding.fastingBloodGlucose!!.value + " mg/dL",
                    device_id = binding.fastingBloodGlucose?.deviceId,
                    lot_id = binding.fastingBloodGlucose!!.lotId,
                    comment = binding.fastingBloodGlucose!!.comment
                )

                FBGRxBus.getInstance().post(mFastingBloodGlucoseDto)
                navController().popBackStack()
                Log.d("FBG_FRAG", "BLOOD_GLUCOSE: Clikced, " + mFastingBloodGlucoseDto )
            }
        }

        binding.buttonJanacare.singleClick {
            // navController().navigate(R.id.action_samplemangementhb1AcFragment_to_bagScanBarcodeFragment)
            startAina("com.janacare.ainamini.openAinaMini", AINA_REQUEST_CODE_GLUCOSE)
            //startAina("com.janacare.aina.openAinaMini", AINA_REQUEST_CODE_GLUCOSE)

        }
        binding.buttonConnect.singleClick {
            if (isAinaPackageAvailable(activity!!.getApplicationContext())) {
                binding.ainaViewConnected.visibility = View.VISIBLE
                binding.ainaViewNotConnected.visibility = View.GONE

                binding.layoutFbgTextInput.visibility = View.GONE
                binding.layoutFbgAinaInput.visibility = View.VISIBLE
            } else {
                Toast.makeText(activity, "Aina app not installed", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonManualEntry.singleClick {

            binding.textInputEditTextFBG.setText("")

            binding.ainaViewConnected.visibility = View.GONE
            binding.ainaViewNotConnected.visibility = View.VISIBLE

            binding.layoutFbgTextInput.visibility = View.VISIBLE
            binding.layoutFbgAinaInput.visibility = View.GONE
        }
        if (isAinaPackageAvailable(activity!!.getApplicationContext())) {
            binding.ainaViewNotConnected.visibility = View.GONE
            binding.ainaViewConnected.visibility = View.VISIBLE

            binding.layoutFbgTextInput.visibility = View.GONE
            binding.layoutFbgAinaInput.visibility = View.VISIBLE
        } else {
            binding.ainaViewNotConnected.visibility = View.VISIBLE
            binding.ainaViewConnected.visibility = View.GONE

            binding.layoutFbgTextInput.visibility = View.VISIBLE
            binding.layoutFbgAinaInput.visibility = View.GONE
        }

        binding.buttonRunTest.singleClick {

            startAina("com.janacare.ainamini.openAinaMini", AINA_REQUEST_CODE_GLUCOSE)
            //startAina("com.janacare.aina.openAina", AINA_REQUEST_CODE_GLUCOSE)

        }
        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);
        samplemangementfastingbloodglucoseViewModel.setStationName(Measurements.BLOOD_GLUCOSE)
        samplemangementfastingbloodglucoseViewModel.stationDeviceList?.observe(this, Observer {
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

        binding.buttonCancel.setOnClickListener {
            Log.d("FBG_HOME", " PARTICIPANT: " + participant)
            val cancelDialogFragment = CancelDialogFragment()
            cancelDialogFragment.arguments = bundleOf("participant" to participant)
            cancelDialogFragment.show(fragmentManager!!)
        }

    }


    val AINA_REQUEST_CODE_GLUCOSE = 10

    fun isAinaPackageAvailable(context: Context): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager
        pm = context.getPackageManager()
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName.contains("com.janacare.ainamini")) return true
            //if (packageInfo.packageName.contains("com.janacare.aina")) return true
        }
        return false
    }

    private fun startAina(action: String, requestCode: Int) {
        var ainaIntent: Intent? = null
        if (isAinaPackageAvailable(activity!!.getApplicationContext())) {
            ainaIntent = Intent(action)
            activity!!.startActivityForResult(ainaIntent, requestCode)
//            binding.ainaViewConnected.visibility = View.VISIBLE
//            binding.ainaViewNotConnected.visibility = View.GONE
        } else {
            Toast.makeText(activity, "Aina app not started", Toast.LENGTH_SHORT).show()
//            binding.ainaViewConnected.visibility = View.GONE
//            binding.ainaViewNotConnected.visibility = View.VISIBLE
        }
    }// Lines of code to invoke Aina launch for a specific test

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    fun isValidFBGRange(): Boolean {
        if(binding.textInputEditTextFBG.text !=null && !binding.textInputEditTextFBG.text.toString().equals("")) {
            val value = binding.textInputEditTextFBG.text.toString().toFloat()
            if (value >= FBG_MIN_VAL && value <= FBG_MAX_VAL) {
                binding.textInputLayoutFBG.error = ""
                return true
            } else {
                binding.textInputLayoutFBG.error = getString(R.string.error_invalid_input)

                Toast.makeText(activity, getString(R.string.error_blood_glucose_message), Toast.LENGTH_SHORT).show()
                return false

            }
        }else{
            binding.textInputLayoutFBG.error = getString(R.string.error_invalid_input)

            Toast.makeText(activity, getString(R.string.error_blood_glucose_message), Toast.LENGTH_SHORT).show()
            return false
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

}
