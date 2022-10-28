package org.nghru_hpv.ghru.ui.covid.confirmation

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import com.crashlytics.android.Crashlytics
import org.nghru_hpv.ghru.R
import org.nghru_hpv.ghru.binding.FragmentDataBindingComponent
import org.nghru_hpv.ghru.databinding.CovidConfirmationFragmentBinding
import org.nghru_hpv.ghru.db.MemberTypeConverters
import org.nghru_hpv.ghru.di.Injectable
import org.nghru_hpv.ghru.event.BusProvider
import org.nghru_hpv.ghru.ui.covid.reason.ReasonDialogFragment
import org.nghru_hpv.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.nghru_hpv.ghru.util.*
import org.nghru_hpv.ghru.vo.*
import org.nghru_hpv.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class CovidConfirmationFragment : Fragment() , Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<CovidConfirmationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: CovidConfirmationViewModel

    private var participantRequest: ParticipantRequest? = null

    private var cogData: CovidDataNew? = null
    private var cognitionCompleted: Boolean? = false

    private var selectedParticipant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    var meta: Meta? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CovidConfirmationFragmentBinding>(
            inflater,
            R.layout.covid_confirmation_fragment,
            container,
            false
        )
        binding = dataBinding

        //setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participantRequest
        binding.viewModel = viewModel

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())

        val intent1 = activity!!.intent
        val screening_id = intent1?.getStringExtra("ParticipantID")

        if (selectedParticipant != null)
        {
            Log.d("CONFIRMATION_FRAG", " DATA: " + selectedParticipant!!.participant_id)

            binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
            binding.titleGender.setText(selectedParticipant!!.gender)
            binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

            val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
            val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
            val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)

            val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
            binding.titleAge.setText(participantAge + "Y")

            viewModel.setScreeningId(selectedParticipant!!.participant_id)

            viewModel.participant.observe(this, Observer { participantResource ->

                if (participantResource?.status == Status.SUCCESS)
                {
                    participantRequest = participantResource.data?.data
                    participantRequest?.meta = meta

                }
                else if (participantResource?.status == Status.ERROR)
                {
                    Log.d("CONFIRMATION_FRAG", "PAR_REQ_FAILED")
                }
                binding.executePendingBindings()
            })
        }
        else
        {
            if (screening_id != null)
            {
                viewModel.setScreeningId(screening_id)

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participantRequest = participantResource.data?.data
                        participantRequest!!.meta = meta

                        Log.d("CONFIRMATION_FRAG", " INSIDE_PARTICIPANT_REQUEST_DATA: " + participantRequest!!.screeningId)

                        binding.titleName.setText(participantRequest!!.firstName + " " + participantRequest!!.lastName)
                        binding.titleGender.setText(participantRequest!!.gender)
                        binding.titleParticipantId.setText(participantRequest!!.screeningId)
                        binding.titleAge.setText(participantRequest?.age?.ageInYears.toString() + "Y")

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("CONFIRMATION_FRAG","Please try again")
                        //Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

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

        Log.d("CONFIRMATION_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        viewModel.cogUpdateComplete?.observe(this, Observer { cognitionProcess ->

            if(cognitionProcess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
            }

            if (cognitionProcess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(cognitionProcess?.status == Status.ERROR){
                Crashlytics.setString(
                    "CognitionMeasurementMeta",
                    CovidRequestNew(meta = participantRequest?.meta, body = cogData, status = "10").toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + cognitionProcess.message.toString()))
            }
        })

        binding.buttonSubmit.singleClick {

            if (participantRequest != null)
            {
                if (validateNextButton()) {

                    cogData = CovidDataNew(
                        questionnaire_completed = cognitionCompleted!!
                    )

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    val cogRequest = CovidRequestNew(meta = participantRequest?.meta, body = cogData, status = "100")
                    cogRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        cogRequest.syncPending =false
                    }

                    viewModel.setUpdateCog(cogRequest, participantRequest?.screeningId)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                }
                else
                {
                    binding.executePendingBindings()
                }
            }
            else
            {
                Log.d("CONFIRMATION_FRAG", "INSIDE_NEXT_PARTICIPANT_NULL: ")
                viewModel.setScreeningId("AAA")

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participantRequest = participantResource.data?.data
                        participantRequest!!.meta = meta

                        if (validateNextButton()) {

                            cogData = CovidDataNew(
                                questionnaire_completed = cognitionCompleted!!
                            )

                            val endTime: String = convertTimeTo24Hours()
                            val endDate: String = getDate()
                            val endDateTime:String = endDate + " " + endTime

                            participantRequest?.meta?.endTime = endDateTime

                            val cogRequest = CovidRequestNew(meta = participantRequest?.meta, body = cogData, status = "100")
                            cogRequest.screeningId = participantRequest?.screeningId!!

                            if(isNetworkAvailable()){
                                cogRequest.syncPending =false
                            }

                            viewModel.setUpdateCog(cogRequest, participantRequest?.screeningId)

                            binding.progressBar.visibility = View.VISIBLE
                            binding.buttonSubmit.visibility = View.GONE

                        }
                        else
                        {
                            binding.executePendingBindings()
                        }

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("CONFIRMATION_FRAG","Please try again")
                        //Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })

                viewModel.setScreeningId(screening_id)
            }

        }

        binding.buttonCancel.singleClick{

            if (participantRequest != null)
            {
                val reasonDialogFragment = ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else
            {
                Log.d("CONFIRMATION_FRAG", "INSIDE_CANCEL_PARTICIPANT_NULL: ")
                viewModel.setScreeningId("AAA")

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participantRequest = participantResource.data?.data
                        participantRequest!!.meta = meta

                        val reasonDialogFragment = ReasonDialogFragment()
                        reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
                        reasonDialogFragment.show(fragmentManager!!)
                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("CONFIRMATION_FRAG","Please try again")
                        //Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })

                viewModel.setScreeningId(screening_id)
            }

        }

        binding.radioGroupStaff.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noStaff) {
                binding.radioGroupCovidValue = false
                viewModel.setHaveStaff(false)
                cognitionCompleted = false

            }
            else {
                binding.radioGroupCovidValue = false
                viewModel.setHaveStaff(true)
                cognitionCompleted = true
            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {

        if (viewModel.haveStaff.value == null)
        {
            binding.radioGroupCovidValue = true
            binding.executePendingBindings()
            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.hideKeyboard()
                navController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
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

}
