package org.southasia.ghrufollowup_sab.ui.participantlist.measurementlist

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
import com.crashlytics.android.Crashlytics
import org.southasia.ghrufollowup_sab.*
import org.southasia.ghrufollowup_sab.binding.FragmentDataBindingComponent
import org.southasia.ghrufollowup_sab.databinding.MeasurementListFragmentBinding
import org.southasia.ghrufollowup_sab.db.MemberTypeConverters
import org.southasia.ghrufollowup_sab.di.Injectable
import org.southasia.ghrufollowup_sab.ui.participantlist.measurementlist.completevisitcompleted.VisitCompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.participantlist.measurementlist.completevisitwarning.VisitWarningDialogFragment
import org.southasia.ghrufollowup_sab.util.autoCleared
import org.southasia.ghrufollowup_sab.util.fromJson
import org.southasia.ghrufollowup_sab.util.singleClick
import org.southasia.ghrufollowup_sab.vo.MeasurementListItem
import org.southasia.ghrufollowup_sab.vo.ParticipantListItem
import org.southasia.ghrufollowup_sab.vo.ParticipantStation
import org.southasia.ghrufollowup_sab.vo.Status
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
    private var SP_Status: String? = "Not started"
    private var FBG_Status: String? = "Not started"
    private var QU_Status: String? = "Not started"
    private var BT_Status: String? = "Not started"
    private var INT_Status: String? = "Not started"
    private var COV_STATUS: String? = "Not started"
    private var Folloup_Status: String? = ""

    private var participant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var isConsent : Boolean? = null

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
        val con = intent1?.getBooleanExtra("CONSENT_STATUS", false)

        if (con != null)
        {
            Log.d("MEASUREMENT_LIST", " IS_CONSENT: " + con)

            isConsent = con
        }

        binding.measurementProgressBar.visibility = View.VISIBLE

        binding.homeViewModel = measurementListViewModel

        val measurementAdapter = MeasurementListAdapter(dataBindingComponent, appExecutors, isConsent) { measurementListItem ->

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

            if (measurementListItem.id == 8) {
                if (isInProgress!!)
                {
                    val intent = Intent(activity, CovidQuestionnaireNewActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    val intent = Intent(activity, CovidQuestionnaireActivity::class.java)
                    startActivity(intent)
                }

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

        measurementListViewModel.StationList?.observe(this, Observer { resource ->

            if (resource?.data != null) {
                //L.d("data saved")
            }

        })

        binding.buttonRefresh.singleClick {

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
                measurementListViewModel.updateParticipantFollowUp(participant!!, participant!!.participant_id)
            }
        }

        measurementListViewModel.participantFollowUpStatusUpdate?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data?.data)
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

            if (station.station_name == "Spirometry")
            {
                if (station.isCancelled == 1)
                {
                    SP_Status = "Canceled"
                }
                else
                {
                    SP_Status = station.status_text
                }
            }

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
                + "SP - "+ SP_Status
                + "QU - "+ QU_Status
                + "BT - "+ BT_Status
                + "INT - "+ INT_Status
                + "COV - "+ COV_STATUS)

        var followUpStatus : String = ""


        if ((BP_Status == "Completed" || BP_Status == "Canceled" )
            && (SP_Status == "Completed" || SP_Status == "Canceled")
            && (FBG_Status == "Completed" || FBG_Status == "Canceled" || FBG_Status == "Processed")
            && (BT_Status == "Completed" || BT_Status == "Canceled")
            && (INT_Status == "Completed" || INT_Status == "Canceled")
            && (BM_Status == "Completed" || BM_Status == "Canceled")
            && (QU_Status == "Completed" || QU_Status == "Canceled")
            && (COV_STATUS == "Completed" || COV_STATUS == "Canceled"))
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
