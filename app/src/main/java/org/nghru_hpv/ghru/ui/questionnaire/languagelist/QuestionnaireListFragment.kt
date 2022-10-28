package org.nghru_hpv.ghru.ui.questionnaire.languagelist

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.nghru_hpv.ghru.AppExecutors
import org.nghru_hpv.ghru.R
import org.nghru_hpv.ghru.binding.FragmentDataBindingComponent
import org.nghru_hpv.ghru.databinding.QuestionnaireListFragmentBinding
import org.nghru_hpv.ghru.db.MemberTypeConverters
import org.nghru_hpv.ghru.di.Injectable
import org.nghru_hpv.ghru.util.autoCleared
import org.nghru_hpv.ghru.util.fromJson
import org.nghru_hpv.ghru.util.getLocalTimeString
import org.nghru_hpv.ghru.vo.Meta
import org.nghru_hpv.ghru.vo.ParticipantListItem
import org.nghru_hpv.ghru.vo.Status
import org.nghru_hpv.ghru.vo.request.ParticipantRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class QuestionnaireListFragment : Fragment(), Injectable, SwipeRefreshLayout.OnRefreshListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<QuestionnaireListFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: QuestionnaireListViewModel

    private var adapter by autoCleared<QuestionnaireListAdapter>()

    var prefs : SharedPreferences? = null
    private var selectedParticipant: ParticipantListItem? = null
    private var participant: ParticipantRequest? = null
    var meta: Meta? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<QuestionnaireListFragmentBinding>(
            inflater,
            R.layout.questionnaire_list_fragment,
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
        binding.viewModel = viewModel

        viewModel.setUser("user")
        viewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                meta = Meta(collectedBy = userData.data?.id, startTime = sDateTime)
                meta?.registeredBy = userData.data?.id
            }

        })

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("QUE_FRAGMENT", " DATA: " + selectedParticipant!!.participant_id)

        val intent1 = activity!!.intent
        val screening_id = intent1?.getStringExtra("ParticipantID")

        if (selectedParticipant != null)
        {
            Log.d("HLQ_LIST_FRAG", " DATA: " + selectedParticipant!!.participant_id)

//            binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
//            binding.titleGender.setText(selectedParticipant!!.gender)
//            binding.titleParticipantId.setText(selectedParticipant!!.participant_id)
//
//            val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
//            val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
//            val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)
//
//            val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
//            binding.titleAge.setText(participantAge + "Y")

            viewModel.setScreeningId(selectedParticipant!!.participant_id)

            viewModel.participant.observe(this, Observer { participantResource ->

                if (participantResource?.status == Status.SUCCESS)
                {
                    participant = participantResource.data?.data
                    participant?.meta = meta

                }
                else if (participantResource?.status == Status.ERROR)
                {
                    Log.d("HLQ_LIST_FRAG", "PAR_REQ_FAILED")
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
                        participant = participantResource.data?.data
                        participant!!.meta = meta

                        Log.d("HLQ_LIST_FRAG", " INSIDE_PARTICIPANT_REQUEST_DATA: " + participant!!.screeningId)

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

//        if (isNetworkAvailable())
//        {
//            viewModel.setScreeningId(selectedParticipant!!.participant_id)
//        }
//        else
//        {
//            viewModel.setScreeningIdOffline(selectedParticipant!!.participant_id)
//        }
//
//        viewModel.participant.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS)
//            {
//                participant = participantResource.data?.data
//                participant?.meta = meta
//                Log.d("QUE_FRAGMENT", "PAR_REQ: SUCCESS" + participant!!.screeningId)
//
//            } else if (participantResource?.status == Status.ERROR)
//            {
//                Log.d("QUE_FRAGMENT", "PAR_REQ: FAILED" + participantResource.status.toString())
//            }
//            binding.executePendingBindings()
//        })
//
//        viewModel.participantOffline?.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participant = participantResource.data
//                participant?.meta = meta
//                Log.d("QUE_FRAGMENT", "PAR_REQ_OFFLINE: SUCCESS" + participant!!.screeningId)
//
//            } else if (participantResource?.status == Status.ERROR)
//            {
//                Log.d("QUE_FRAGMENT", "PAR_REQ_OFFLINE: FAILED" + participantResource.status.toString())
//            }
//            binding.executePendingBindings()
//        })

        val adapter = QuestionnaireListAdapter(dataBindingComponent, appExecutors) { questionnaire ->
            Timber.e(questionnaire.toString())
            if (participant != null)
            {
                findNavController().navigate(R.id.action_QuestionnaireListFragment_to_WebFragment , bundleOf("Questionnaire" to questionnaire, "ParticipantRequest" to participant, "meta" to meta))
            }
            else
            {
                viewModel.setScreeningId(screening_id)

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participant = participantResource.data?.data
                        participant!!.meta = meta

                        findNavController().navigate(R.id.action_QuestionnaireListFragment_to_WebFragment , bundleOf("Questionnaire" to questionnaire, "ParticipantRequest" to participant, "meta" to meta))

                    }
                    else if (participantResource?.status == Status.ERROR)
                    {
                        Toast.makeText(activity!!, "Please try again ", Toast.LENGTH_LONG).show()
                    }
                })
            }

        }

        //findNavController().navigate(R.id.action_QuestionnaireListFragment_to_WebFragment , bundleOf("" to ""))

        this.adapter = adapter
        binding.nghruList.adapter = adapter
        binding.nghruList.setLayoutManager(GridLayoutManager(activity, 1))
        viewModel.getQuestionnaire(language = "en", network =  isNetworkAvailable())
        viewModel.language?.observe(this, Observer { resource ->
            binding.swiperefresh.isRefreshing = false
            if (resource?.data != null) {
                Log.d("QUE_FRAGMENT", "QUE: SUCCESS")
                adapter.submitList(resource.data)
                binding.executePendingBindings()
            } else {
                Log.d("QUE_FRAGMENT", "QUE: SUCCESS")
                adapter.submitList(emptyList())
                binding.executePendingBindings()
            }
        })
        binding.swiperefresh.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        viewModel.retry()
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
