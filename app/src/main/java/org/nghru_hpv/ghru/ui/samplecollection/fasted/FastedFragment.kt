package org.nghru_hpv.ghru.ui.samplecollection.fasted


import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_hpv.ghru.R
import org.nghru_hpv.ghru.binding.FragmentDataBindingComponent
import org.nghru_hpv.ghru.databinding.FastedFragmentBinding
import org.nghru_hpv.ghru.db.MemberTypeConverters
import org.nghru_hpv.ghru.di.Injectable
import org.nghru_hpv.ghru.util.*
import org.nghru_hpv.ghru.vo.Meta
import org.nghru_hpv.ghru.vo.ParticipantListItem
import org.nghru_hpv.ghru.vo.Status
import org.nghru_hpv.ghru.vo.User
import org.nghru_hpv.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.Observer

class FastedFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FastedFragmentBinding>()


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: FastedViewModel

    private var participant: ParticipantRequest? = null

    var user: User? = null
    var meta: Meta? = null

//    ----------------------------------------------------------------------------

    var prefs : SharedPreferences? = null

    private var selectedParticipant: ParticipantListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FastedFragmentBinding>(
            inflater,
            R.layout.fasted_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()

        binding.participant = participant
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)

        binding.buttonSubmit.singleClick {
            if (participant != null)
            {
                Log.d("FASTED_FRAG", "BUTTON_CLICK_PARTICIPANT_NOT_NULL")
                val bundle = bundleOf("participant" to participant)
                navController().navigate(R.id.action_fastedFragment_to_bagScanBarcodeFragment, bundle)
            }
            else
            {
                Log.d("FASTED_FRAG", "BUTTON_CLICK_PARTICIPANT_NULL")
                viewModel.setScreeningId("AAA")

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participant = participantResource.data?.data
                        participant?.meta = meta

                        val bundle = bundleOf("participant" to participant)
                        navController().navigate(R.id.action_fastedFragment_to_bagScanBarcodeFragment, bundle)
                        Log.d("FASTED_FRAG", "PARTICIPANT_NULL")

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Log.d("FASTED_FRAG", "PAR_REQ_FAILED")
                    }
                    binding.executePendingBindings()
                })

                viewModel.setScreeningId(selectedParticipant!!.participant_id)
            }
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())

        if (selectedParticipant != null)
        {
            Log.d("COVID_GUIDE_FRAG", " DATA: " + selectedParticipant!!.participant_id)

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
                    participant = participantResource.data?.data
                    participant?.meta = meta

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
            val intent1 = activity!!.intent
            val screening_id = intent1?.getStringExtra("ParticipantID")

            if (screening_id != null)
            {
                viewModel.setScreeningId(screening_id)

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participant = participantResource.data?.data
                        participant!!.meta = meta

                        Log.d("COVID_GUIDE_FRAG", " INSIDE_PARTICIPANT_REQUEST_DATA: " + participant!!.screeningId)

                        binding.titleName.setText(participant!!.firstName + " " + participant!!.lastName)
                        binding.titleGender.setText(participant!!.gender)
                        binding.titleParticipantId.setText(participant!!.screeningId)
                        binding.titleAge.setText(participant?.age?.ageInYears.toString() + "Y")

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
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
