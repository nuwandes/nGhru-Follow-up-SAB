package org.nghru_pk.ghru.ui.covid

import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics

import org.nghru_pk.ghru.R
import org.nghru_pk.ghru.binding.FragmentDataBindingComponent
import org.nghru_pk.ghru.databinding.CovidGuideFragmentBinding
import org.nghru_pk.ghru.db.MemberTypeConverters
import org.nghru_pk.ghru.di.Injectable
import org.nghru_pk.ghru.event.BusProvider
import org.nghru_pk.ghru.ui.covid.completed.StartedDialogFragment
import org.nghru_pk.ghru.ui.covid.stageonereason.ReasonDialogFragmentNew
import org.nghru_pk.ghru.util.*
import org.nghru_pk.ghru.vo.*
import org.nghru_pk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class CovidGuideFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<CovidGuideFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: CovidGuideViewModel

    private var participantRequest: ParticipantRequest? = null

    private var cogData: CovidData? = null

    private var selectedParticipant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    var meta: Meta? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CovidGuideFragmentBinding>(
            inflater,
            R.layout.covid_guide_fragment,
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

        binding.participant = participantRequest
        binding.setLifecycleOwner(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())

        val intent1 = activity!!.intent
        val screening_id = intent1?.getStringExtra("ParticipantID")

        if (selectedParticipant != null)
        {
            Log.d("COVID_GUIDE_FRAG", " DATA: " + selectedParticipant!!.participant_id)

            binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
            binding.titleGender.setText(selectedParticipant!!.gender)
            binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

            var id  = selectedParticipant!!.participant_id!!
            val re = Regex("[^A-Za-z0-9 ]")
            id = re.replace(id, "")
            val num_id = id.filter { it.isDigit() }
            binding.textViewPleaseMsgID.setText(num_id)

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
                    Log.d("COVID_GUIDE_FRAG", "PAR_REQ_FAILED")
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

                        Log.d("COVID_GUIDE_FRAG", " INSIDE_PARTICIPANT_REQUEST_DATA: " + participantRequest!!.screeningId)

                        binding.titleName.setText(participantRequest!!.firstName + " " + participantRequest!!.lastName)
                        binding.titleGender.setText(participantRequest!!.gender)
                        binding.titleParticipantId.setText(participantRequest!!.screeningId)

                        var id1  = participantRequest!!.screeningId
                        val re1 = Regex("[^A-Za-z0-9 ]")
                        id1 = re1.replace(id1, "")
                        val num_id1 = id1.filter { it.isDigit() }
                        binding.textViewPleaseMsgID.setText(num_id1)
                        binding.titleAge.setText(participantRequest?.age?.ageInYears.toString() + "Y")

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("GUIDE_FRAG_LOADING","Please try again")
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

        binding.nextButton.singleClick {

            if (participantRequest != null)
            {
                cogData = CovidData(
                    status = "1"
                )

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                val cogRequest = CovidRequest(meta = participantRequest?.meta, body = cogData)
                cogRequest.screeningId = participantRequest?.screeningId!!

                if(isNetworkAvailable())
                {
                    cogRequest.syncPending =true
                }

                viewModel.setPostCog(cogRequest, participantRequest!!.screeningId)

                binding.progressBar.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE
            }
            else
            {
                Log.d("GUIDE_FRAG", "INSIDE_NEXT_PARTICIPANT_NULL: ")
                viewModel.setScreeningId("AAA")

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participantRequest = participantResource.data?.data
                        participantRequest!!.meta = meta

                        cogData = CovidData(
                            status = "1"
                        )

                        //Log.d("GUIDE_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime:String = endDate + " " + endTime

                        participantRequest?.meta?.endTime = endDateTime

                        val cogRequest = CovidRequest(meta = participantRequest?.meta, body = cogData)
                        cogRequest.screeningId = participantRequest?.screeningId!!

                        if(isNetworkAvailable())
                        {
                            cogRequest.syncPending =true
                        }

                        viewModel.setPostCog(cogRequest, participantRequest!!.screeningId)

                        binding.progressBar.visibility = View.VISIBLE
                        binding.nextButton.visibility = View.GONE

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("GUIDE_FRAG_NEXT","Please try again")
                        //Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })

                viewModel.setScreeningId(screening_id)
            }
        }

        viewModel.cogPostComplete?.observe(this, Observer { ffqPocess ->

            if(ffqPocess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.nextButton.visibility = View.VISIBLE
            }

            if (ffqPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = StartedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(ffqPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "FFQMeasurementMeta",
                    CovidRequest(meta = participantRequest?.meta, body = cogData).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + ffqPocess.message.toString()))
            }
        })

        binding.skipButton.singleClick {

            if (participantRequest != null)
            {
                val reasonDialogFragment = ReasonDialogFragmentNew()
                reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else
            {
                Log.d("GUIDE_FRAG", "INSIDE_CANCEL_PARTICIPANT_NULL: ")
                viewModel.setScreeningId("AAA")

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participantRequest = participantResource.data?.data
                        participantRequest!!.meta = meta

                        val reasonDialogFragment = ReasonDialogFragmentNew()
                        reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
                        reasonDialogFragment.show(fragmentManager!!)
                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("GUIDE_FRAG_CANCEL","Please try again")
                        //Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })

                viewModel.setScreeningId(screening_id)
            }
        }
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
