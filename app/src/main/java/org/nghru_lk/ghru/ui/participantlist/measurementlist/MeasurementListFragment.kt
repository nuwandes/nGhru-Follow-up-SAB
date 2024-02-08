package org.nghru_lk.ghru.ui.participantlist.measurementlist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_lk.ghru.*
import org.nghru_lk.ghru.binding.FragmentDataBindingComponent
import org.nghru_lk.ghru.databinding.MeasurementListFragmentBinding
import org.nghru_lk.ghru.db.MemberTypeConverters
import org.nghru_lk.ghru.di.Injectable
import org.nghru_lk.ghru.jobs.SyncParticipantListItemJob
import org.nghru_lk.ghru.ui.participantlist.measurementlist.completevisitcompleted.VisitCompletedDialogFragment
import org.nghru_lk.ghru.ui.participantlist.measurementlist.completevisitwarning.VisitWarningDialogFragment
import org.nghru_lk.ghru.util.autoCleared
import org.nghru_lk.ghru.util.fromJson
import org.nghru_lk.ghru.util.singleClick
import org.nghru_lk.ghru.vo.MeasurementListItem
import org.nghru_lk.ghru.vo.ParticipantListItem
import org.nghru_lk.ghru.vo.ParticipantStation
import org.nghru_lk.ghru.vo.Status
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MeasurementListFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<MeasurementListFragmentBinding>()
//    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var measurementListViewModel: MeasurementListViewModel

    private var adapter by autoCleared<MeasurementListAdapter>()

    private var measurementListObject: List<MeasurementListItem> = arrayListOf()

    private var stationListObject: List<ParticipantStation> = arrayListOf()
    private var isInProgress: Boolean? = false

    private var participantId: String? = ""

    private var BM_Status: String? = "Not started"
    private var BP_Status: String? = "Not started"
    //private var SP_Status: String? = "Not started"
    private var FBG_Status: String? = "Not started"
    private var QU_Status: String? = "Not started"
    private var BT_Status: String? = "Not started"
    private var INT_Status: String? = "Not started"
    private var COV_STATUS: String? = "Not started"
    private var ECG_Status: String? = "Not started"
    private var FUN_Status: String? = "Not started"
    private var ACT_Status: String? = "Not started"
    private var Folloup_Status: String? = ""

    private var participant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //var isConsent : Boolean? = null
    @Inject
    lateinit var jobManager: JobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
//            participant = arguments?.getParcelable<ParticipantListItem>("participant")!!

        } catch (e: KotlinNullPointerException) {
            print(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<MeasurementListFragmentBinding>(
            inflater,
            R.layout.measurement_list_fragment,
            container,
            false
        )

        binding = dataBinding
        setHasOptionsMenu(true)
        return dataBinding.root
    }

    override fun onStart() {

        super.onStart()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
//                val intent = Intent(activity, SettingActivity::class.java)
//                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)


        val json : String? = prefs?.getString("single_participant","")
        participant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + participant!!.participant_id)



        val intent1 = activity!!.intent
//        val con = intent1?.getBooleanExtra("CONSENT_STATUS", false)
//
//        if (con != null)
//        {
//            Log.d("MEASUREMENT_LIST", " IS_CONSENT: " + con)
//
//            isConsent = con
//        }

        binding.measurementProgressBar.visibility = View.VISIBLE

        binding.homeViewModel = measurementListViewModel

        val measurementAdapter = MeasurementListAdapter(dataBindingComponent, appExecutors) { measurementListItem ->

            Timber.d(measurementListItem.toString())

//            if (measurementListItem.id == 0)
//            {
//                val intent = Intent(activity, BodyMeasurementsActivity::class.java)
//                startActivity(intent)
//            }

            if (measurementListItem.id == 1)
            {
                val intent = Intent(activity, BodyMeasurementsActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 2)
            {
                val intent = Intent(activity, BloodPressureActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 3)
            {
                val intent = Intent(activity, SpirometryActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 4) {
                val intent = Intent(activity, BloodTestActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 5) {
                val intent = Intent(activity, WebViewActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 6) {
                val intent = Intent(activity, SampleCollectionActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 7) {
                val intent = Intent(activity, IntakeActivity::class.java)
                startActivity(intent)
            }

//            if (measurementListItem.id == 8) {
//                if (isInProgress!!)
//                {
//                    val intent = Intent(activity, CovidQuestionnaireNewActivity::class.java)
//                    startActivity(intent)
//                }
//                else
//                {
//                    val intent = Intent(activity, CovidQuestionnaireActivity::class.java)
//                    startActivity(intent)
//                }
//            }

            if (measurementListItem.id == 9) {
                val intent = Intent(activity, ECGActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 10) {
                val intent = Intent(activity, FundoscopyActivity::class.java)
                startActivity(intent)
            }

            if (measurementListItem.id == 11) {
                val intent = Intent(activity, ActivityTrackerActivity::class.java)
                startActivity(intent)
            }
        }

        val dob_year: String = participant!!.dob!!.substring(0,4)
        val dob_month: String = participant!!.dob!!.substring(5,7)
        val dob_date : String = participant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())

        Log.d("MEASUREMENT_FRAGMENT", "year: " + dob_year + ", month: " + dob_month + ", date: " + dob_date + "AGE: " + participantAge)


        binding.participantDetails.setText(participant!!.firstname + " " + participant!!.last_name+ ", " + participant!!.gender + ", " + participantAge + " Years, "  + participant!!.participant_id)
//        binding.participantDetails.setText(participant!!.firstname + " " + participant!!.last_name+  ", " + participant!!.gender + ", " + participant!!.participant_id)

        this.adapter = measurementAdapter
        binding.nghruList.adapter = measurementAdapter
        binding.nghruList.setLayoutManager(GridLayoutManager(activity, 1))

//        measurementListViewModel.setMeasurementId("get")
//        measurementListViewModel.measurementListItem.observe(this, Observer {
//
//            if (it?.data != null) {
//                adapter.submitList(it.data)
//            } else {
//                adapter.submitList(emptyList())
//            }
//        })

        if (isNetworkAvailable())
        {
            measurementListViewModel.setParticipantId(participant = participant!!.participant_id!!)

            measurementListViewModel.getSingleParticipantStations?.observe(activity!!, Observer {

                if (isNetworkAvailable())
                {
                    if (it.status.equals(Status.SUCCESS))
                    {
                        val stationList: ArrayList<ParticipantStation> = ArrayList<ParticipantStation>()

                        it.data!!.stations?.forEach { stations ->

                            stationList.add(stations)
                        }

                        Folloup_Status = findFollowUpStatus(stationList)

                        Log.d("MEASUREMENT_FRAGMENT", "FOLLOW_UP_STATUS: " + Folloup_Status)

                        measurementListViewModel.setStationList(stationList)

                        measurementListViewModel.measurementListItem.observe(this, Observer {

                            if (it?.data != null)
                            {
                                adapter.submitList(it.data)
                                binding.measurementProgressBar.visibility = View.GONE
                            }
                            else
                            {
                                adapter.submitList(emptyList())
                                binding.measurementProgressBar.visibility = View.GONE
                            }
                        })

                        Log.d("MEASUREMENT_FRAGMENT", "SUCCESS: " + it.status + " DATA: " + stationList.size)
                    }
                    else if (it.status == Status.ERROR)
                    {
                        Log.d("MEASUREMENT_FRAGMENT", "ERROR: " + it.status)
                    }
                }
                else
                {
                    Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
                }
            })
        }
        else
        {
            // load offine

            measurementListViewModel.setStationList(setParticipantStationOffline(participant!!))

            measurementListViewModel.measurementListItem.observe(this, Observer {

                if (it?.data != null)
                {
                    adapter.submitList(it.data)
                    binding.measurementProgressBar.visibility = View.GONE
                }
                else
                {
                    adapter.submitList(emptyList())
                    binding.measurementProgressBar.visibility = View.GONE
                }
            })

        }

        measurementListViewModel.StationList?.observe(this, Observer { resource ->

            if (resource?.data != null) {
                //L.d("data saved")
            }

        })

        binding.buttonRefresh.singleClick {

            if (isNetworkAvailable())
            {
                Log.d("MEASUREMENT_FRAGMENT", "button clicked: ")

                // to recall the single participant api call using live data

                binding.measurementProgressBar.visibility = View.VISIBLE

                measurementListViewModel.setParticipantId("new party")
                adapter.submitList(emptyList())

                measurementListViewModel.setParticipantId(participant = participant!!.participant_id!!)

                measurementListViewModel.getSingleParticipantStations?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        val stationList: ArrayList<ParticipantStation> = ArrayList<ParticipantStation>()

                        it.data!!.stations?.forEach { stations ->

                            stationList.add(stations)
                        }

                        Folloup_Status = findFollowUpStatus(stationList)

                        Log.d("MEASUREMENT_FRAGMENT", "FOLLOW_UP_STATUS: " + Folloup_Status)

                        //Log.d("MEASUREMENT_FRAGMENT", "SINGLE_PARTICIPANT_CALL_OK")

                        measurementListViewModel.setStationList(sList = null)

                        measurementListViewModel.setStationList(stationList)

                        measurementListViewModel.measurementListItem.observe(this, Observer {

                            if (it?.data != null)
                            {
                                Log.d("MEASUREMENT_FRAGMENT", "STATION_LIST_SUCCESS: " + it.status + " DATA: " + stationList.size)

                                //adapter.notifyDataSetChanged()
                                adapter.submitList(it.data)
                                binding.measurementProgressBar.visibility = View.GONE
                            } else
                            {
                                Log.d("MEASUREMENT_FRAGMENT", "STATION_LIST_FAILED: " + it.status)
                                adapter.submitList(emptyList())
                                binding.measurementProgressBar.visibility = View.GONE
                            }
                        })
                    }
                    else if (it.status == Status.ERROR)
                    {
                        Log.d("MEASUREMENT_FRAGMENT", "SINGLE_PARTICIPANT_CALL_NOT_OK")
                    }
                })
            }
            else
            {
                // load offline measurement list
                measurementListViewModel.setStationList(sList = null)

                measurementListViewModel.setScreeningIdParticipantListItemsFromDb(participant!!.participant_id!!)

                measurementListViewModel.getScreeningIdParticipantListItemsFromDb?.observe(this, Observer { participant ->
                    if (participant?.status == Status.SUCCESS)
                    {
                        if (participant.data != null) {

                            measurementListViewModel.setStationList(setParticipantStationOffline(participant.data))

                            measurementListViewModel.measurementListItem.observe(this, Observer {

                                if (it?.data != null)
                                {
                                    adapter.submitList(it.data)
                                    binding.measurementProgressBar.visibility = View.GONE
                                }
                                else
                                {
                                    adapter.submitList(emptyList())
                                    binding.measurementProgressBar.visibility = View.GONE
                                }
                            })

                        } else {

                        }
                    }
                })
            }

        }

        measurementListViewModel.setFollowUpParticipant(participant!!, participant!!.participant_id)

        binding.completeVisit.singleClick {

            participant!!.status = Folloup_Status

            if (participant!!.status != "completed")
            {
                val warningDialogFragment = VisitWarningDialogFragment()
                warningDialogFragment.arguments = bundleOf("is_cancel" to false, "participant" to participant)
                warningDialogFragment.show(fragmentManager!!)
            }
            else
            {
                if (isNetworkAvailable())
                {
                    participant?.isSync =false
                    measurementListViewModel.updateParticipantFollowUp(participant!!)
                }
                else
                {
                    participant?.isSync =true
                    jobManager.addJobInBackground(SyncParticipantListItemJob(participant!!))
                    val completedDialogFragment = VisitCompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                    completedDialogFragment.show(fragmentManager!!)
                }
            }
        }

        measurementListViewModel.participantFollowUpStatusUpdate?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data)
                if (assertsResource.data != null) {

                    val completedDialogFragment = VisitCompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                    completedDialogFragment.show(fragmentManager!!)

                    Log.d("MEASUREMENT_FRAGMENT", "SUBMIT_BUTTON_SUCCESS" + assertsResource.message.toString())
                    //Toast.makeText(activity, "Successfully update the participant Follow Up Status as: " + participant!!.status + assertsResource.message.toString(), Toast.LENGTH_LONG).show()

                } else {
                    Log.d("MEASUREMENT_FRAGMENT", "SUBMIT_BUTTON_FAILED" + assertsResource.message.toString())
                    Toast.makeText(activity, "Unable to update the participant via " + assertsResource.message.toString(), Toast.LENGTH_LONG).show()
                    Crashlytics.logException(Exception("Participant Update " + assertsResource.message.toString()))
                    binding.executePendingBindings()
                }
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun setParticipantStationOffline(participantListItem: ParticipantListItem) : ArrayList<ParticipantStation>
    {
        val stationList: ArrayList<ParticipantStation> = ArrayList<ParticipantStation>()

        var bpStatus : String = ""
        var bmStatus : String = ""
        var btStatus : String = ""
        var queStatus : String = ""
        var samStatus : String = ""
        var intStatus : String = ""
        var ecgStatus : String = ""
        var funStatus : String = ""
        var actStatus : String = ""

        if (participantListItem.bp_status == 100)
        {
            bpStatus = "Completed"
        }
        else if(participantListItem.bp_status == 1)
        {
            bpStatus = "In progress"
        }
        else if (participantListItem.bp_status == 0)
        {
            bpStatus = "Not Started"
        }
        else if (participantListItem.bp_status == -1)
        {
            bpStatus = "Cancelled"
        }

        // -------------------------------------------------------

        if (participantListItem.bm_status == 100)
        {
            bmStatus = "Completed"
        }
        else if(participantListItem.bm_status == 1)
        {
            bmStatus = "In progress"
        }
        else if (participantListItem.bm_status == 0)
        {
            bmStatus = "Not Started"
        }
        else if (participantListItem.bm_status == -1)
        {
            bmStatus = "Cancelled"
        }

        // -----------------------------------------------------------------

        if (participantListItem.ecg_status == 100)
        {
            ecgStatus = "Completed"
        }
        else if(participantListItem.ecg_status == 1)
        {
            ecgStatus = "In progress"
        }
        else if (participantListItem.ecg_status == 0)
        {
            ecgStatus = "Not Started"
        }
        else if (participantListItem.ecg_status == -1)
        {
            ecgStatus = "Cancelled"
        }

        //--------------------------------------------------

        if (participantListItem.bt_status == 100)
        {
            btStatus = "Completed"
        }
        else if(participantListItem.bt_status == 1)
        {
            btStatus = "In progress"
        }
        else if (participantListItem.bt_status == 0)
        {
            btStatus = "Not Started"
        }
        else if (participantListItem.bt_status == -1)
        {
            btStatus = "Cancelled"
        }

        //--------------------------------------------------------

        if (participantListItem.que_status == 100)
        {
            queStatus = "Completed"
        }
        else if(participantListItem.que_status == 1)
        {
            queStatus = "In progress"
        }
        else if (participantListItem.que_status == 0)
        {
            queStatus = "Not Started"
        }
        else if (participantListItem.que_status == -1)
        {
            queStatus = "Cancelled"
        }

        // -------------------------------------------------------------

        if (participantListItem.sam_status == 100)
        {
            samStatus = "Completed"
        }
        else if(participantListItem.sam_status == 1)
        {
            samStatus = "In progress"
        }
        else if (participantListItem.sam_status == 0)
        {
            samStatus = "Not Started"
        }
        else if (participantListItem.sam_status == -1)
        {
            samStatus = "Cancelled"
        }

        // ------------------------------------------------------------------

        if (participantListItem.int_status == 100)
        {
            intStatus = "Completed"
        }
        else if(participantListItem.int_status == 1)
        {
            intStatus = "In progress"
        }
        else if (participantListItem.int_status == 0)
        {
            intStatus = "Not Started"
        }
        else if (participantListItem.int_status == -1)
        {
            intStatus = "Cancelled"
        }

        // --------------------------------------------------------------------

        if (participantListItem.fun_status == 100)
        {
            funStatus = "Completed"
        }
        else if(participantListItem.fun_status == 1)
        {
            funStatus = "In progress"
        }
        else if (participantListItem.fun_status == 0)
        {
            funStatus = "Not Started"
        }
        else if (participantListItem.fun_status == -1)
        {
            funStatus = "Cancelled"
        }

        // -------------------------------------------------------------------------

        if (participantListItem.act_status == 100)
        {
            actStatus = "Completed"
        }
        else if(participantListItem.act_status == 1)
        {
            actStatus = "In progress"
        }
        else if (participantListItem.act_status == 0)
        {
            actStatus = "Not Started"
        }
        else if (participantListItem.act_status == -1)
        {
            actStatus = "Cancelled"
        }

        // ------------------------------------------------------------------

        val ps1 = ParticipantStation(
            participantListItem.participant_id,
            "9817448a-66e5-496e-8914-7b1a48bf4a51",
            "Body Measurements",bmStatus ,participantListItem.bm_status.toString()
        )

        val ps2 = ParticipantStation(
            participantListItem.participant_id,
            "0243affe-6110-11ee-8c99-0242ac120002",
            "Blood Pressure",bpStatus, participantListItem.bp_status.toString()
        )

        val ps3 = ParticipantStation(
            participantListItem.participant_id,
            "0243affe-6110-11ee-8c99-0242ac120002",
            "ECG", ecgStatus, participantListItem.ecg_status.toString()
        )

        val ps4 = ParticipantStation(
            participantListItem.participant_id,
            "7268c9de-1c27-4221-b804-40076d976c99",
            "Blood Test", btStatus ,participantListItem.bt_status.toString()
        )

        val ps5 = ParticipantStation(
            participantListItem.participant_id,
            "b277deb1-2c8f-4b39-8897-0a6c4d7edab7",
            "Health Questionnaire", queStatus, participantListItem.que_status.toString()
        )

        val ps6 = ParticipantStation(
            participantListItem.participant_id,
            "0243affe-6110-11ee-8c99-0242ac120002",
            "Biological Samples", samStatus, participantListItem.sam_status.toString()
        )

        val ps7 = ParticipantStation(
            participantListItem.participant_id,
            "29b452bf-9583-4a87-a785-1aab54cd0ce5",
            "Intake 24", intStatus ,participantListItem.int_status.toString()
        )

        val ps8 = ParticipantStation(
            participantListItem.participant_id,
            "158a357e-6110-11ee-8c99-0242ac120002",
            "Fundoscopy", funStatus, participantListItem.fun_status.toString()
        )

        val ps9 = ParticipantStation(
            participantListItem.participant_id,
            "0243affe-6110-11ee-8c99-0242ac120002",
            "Activity Tracker", actStatus, participantListItem.act_status.toString()
        )

        stationList.add(ps1)
        stationList.add(ps2)
        stationList.add(ps3)
        stationList.add(ps4)
        stationList.add(ps5)
        stationList.add(ps6)
        stationList.add(ps7)
        stationList.add(ps8)
        stationList.add(ps9)

        return stationList
    }

    private fun findFollowUpStatus(stationList: ArrayList<ParticipantStation>): String
    {
        for (station in stationList)
        {
            if (station.station_name == "Body Measurements")
            {
                if (station.isCancelled == 1)
                {
                    BM_Status = "Canceled"
                }
                else
                {
                    BM_Status = station.status_text
                }
            }

            if (station.station_name == "Blood Pressure")
            {
                if (station.isCancelled == 1)
                {
                    BP_Status = "Canceled"
                }
                else
                {
                    BP_Status = station.status_text
                }
            }

            if (station.station_name == "Biological Samples") // SAMPLE COLLECTION
            {
                if (station.isCancelled == 1)
                {
                    FBG_Status = "Canceled"
                }
                else
                {
                    FBG_Status = station.status_text
                }
            }

//            if (station.station_name == "Spirometry")
//            {
//                if (station.isCancelled == 1)
//                {
//                    SP_Status = "Canceled"
//                }
//                else
//                {
//                    SP_Status = station.status_text
//                }
//            }

            if (station.station_name == "Health Questionnaire")
            {
                if (station.isCancelled == 1)
                {
                    QU_Status = "Canceled"
                }
                else
                {
                    QU_Status = station.status_text
                }
            }

            if (station.station_name == "Intake 24")
            {
                if (station.isCancelled == 1)
                {
                    INT_Status = "Canceled"
                }
                else
                {
                    INT_Status = station.status_text
                }
            }

            if (station.station_name == "Blood Test")
            {
                if (station.isCancelled == 1)
                {
                    BT_Status = "Canceled"
                }
                else
                {
                    BT_Status = station.status_text
                }
            }

            if (station.station_name == "Fundoscopy")
            {
                if (station.isCancelled == 1)
                {
                    FUN_Status = "Canceled"
                }
                else
                {
                    FUN_Status = station.status_text
                }
            }

            if (station.station_name == "ECG")
            {
                if (station.isCancelled == 1)
                {
                    ECG_Status = "Canceled"
                }
                else
                {
                    ECG_Status = station.status_text
                }
            }

            if (station.station_name == "Axivity")
            {
                if (station.isCancelled == 1)
                {
                    ACT_Status = "Canceled"
                }
                else
                {
                    ACT_Status = station.status_text
                }
            }

            if (station.station_name == "Covid Questionnaire")
            {
                if (station.isCancelled == 1)
                {
                    COV_STATUS = "Canceled"
                    isInProgress = false
                }
                else
                {
                    COV_STATUS = station.status_text

                    if (station.status_code.equals("1"))
                    {
                        Log.d("MEASUREMENT_LIST_FRAGMENT", "COVID_STATUS_INSIDE: " + COV_STATUS + " CODE: " + station.status_code)
                        isInProgress = true
                    }
                    else
                    {
                        Log.d("MEASUREMENT_LIST_FRAGMENT", "COVID_STATUS_INSIDE: " + COV_STATUS + " CODE: " + station.status_code)
                        isInProgress = false
                    }
                }
            }

            Log.d("MEASUREMENT_LIST_FRAGMENT", "COVID_STATUS_AFTER: " + COV_STATUS + " CODE: " + station.status_code + " STATUS: " + isInProgress)
        }

        Log.d("MEASUREMENT_FRAGMENT", "STATION_STATUSES:"
                + "BM - "+ BM_Status
                + "BP - "+ BP_Status
                + "FBG - "+ FBG_Status
//                + "SP - "+ SP_Status
                + "QU - "+ QU_Status
                + "BT - "+ BT_Status
                + "INT - "+ INT_Status
                + "COV - "+ COV_STATUS
                + "ECG - "+ ECG_Status
                + "FUN - "+ FUN_Status
                + "ACT - " + ACT_Status)

        var followUpStatus : String = ""


        if ((BP_Status == "Completed" || BP_Status == "Canceled" )
//            && (SP_Status == "Completed" || SP_Status == "Canceled")
            && (FBG_Status == "Completed" || FBG_Status == "Canceled" || FBG_Status == "Processed")
            && (BT_Status == "Completed" || BT_Status == "Canceled")
            && (INT_Status == "Completed" || INT_Status == "Canceled")
            && (BM_Status == "Completed" || BM_Status == "Canceled")
            && (QU_Status == "Completed" || QU_Status == "Canceled")
            && (COV_STATUS == "Completed" || COV_STATUS == "Canceled")
            && (ECG_Status == "Completed" || ECG_Status == "Canceled")
            && (FUN_Status == "Completed" || FUN_Status == "Canceled")
            && (ACT_Status == "Completed" || ACT_Status == "Canceled"))
        {
//            if ((BM_Status == "Completed" || BM_Status == "Canceled") && (QU_Status == "Completed" || QU_Status == "Canceled"))
//            {
//                followUpStatus = "completed"
//            }
//            else if((BM_Status == "Completed" || BM_Status == "Canceled"))
//            {
//                followUpStatus = "questionnaire_pending"
//            }
//            else if ((QU_Status == "Completed" || QU_Status == "Canceled"))
//            {
//                followUpStatus = "physical_measurement_pending"
//            }
//            else
//            {
//                followUpStatus = "not_complete"
//            }
            followUpStatus = "completed"
        }
        else
        {
            followUpStatus = "not_complete"
        }

        return followUpStatus
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

    override fun onResume() {
        super.onResume()
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.detach(MeasurementListFragment()).attach(MeasurementListFragment()).commit()
    }

    override fun onPause() {
        super.onPause()
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.detach(MeasurementListFragment()).attach(MeasurementListFragment()).commit()
    }

    fun getBPStatus(participantId: String): Boolean
    {
        var isCompleted : Boolean? = false
        measurementListViewModel.setToGetBPStatus(participantId)

        measurementListViewModel.isBPStationCompleted?.observe(this, Observer { resources->

            isCompleted = resources?.status == Status.SUCCESS

        })

        return isCompleted!!
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
