package org.nghru_lk.ghru.ui.participantlist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.birbit.android.jobqueue.JobManager
import kotlinx.android.synthetic.main.participant_list_fragment.*
import org.nghru_lk.ghru.AppExecutors
import org.nghru_lk.ghru.PatientAttendanceActivity
import org.nghru_lk.ghru.R
import org.nghru_lk.ghru.binding.FragmentDataBindingComponent
import org.nghru_lk.ghru.databinding.ParticipantListFragmentBinding
import org.nghru_lk.ghru.db.MemberTypeConverters
import org.nghru_lk.ghru.di.Injectable
import org.nghru_lk.ghru.ui.participantlist.statuscompleted.StatusCompletedDialogFragment
import org.nghru_lk.ghru.util.RecyclerItemClickListner
import org.nghru_lk.ghru.util.autoCleared
import org.nghru_lk.ghru.util.singleClick
import org.nghru_lk.ghru.vo.ParticipantListItem
import org.nghru_lk.ghru.vo.ParticipantListMeta
import org.nghru_lk.ghru.vo.Status
import timber.log.Timber
import javax.inject.Inject

class ParticipantListFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<ParticipantListFragmentBinding>()
//    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var participantListViewModel: ParticipantListViewModel

    private var adapter by autoCleared<ParticipantListAdapter>()

    @Inject
    lateinit var  jobManager: JobManager

    private var siteNames: MutableList<String> = arrayListOf()

    private var statuses: MutableList<String> = arrayListOf()

    private var participantListObject: ArrayList<ParticipantListItem?> = arrayListOf()

    private var participantListMeta: ParticipantListMeta? = null

    private var selectedStatus: String? = ""
    private var selectedSite: String? = ""
    private var searchKey: String? = ""

    private var currect_page:Int = 0
    private var total_pages:Int = 0

    var prefs : SharedPreferences? = null

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ParticipantListFragmentBinding>(
            inflater,
            R.layout.participant_list_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        return dataBinding.root
    }

    override fun onStart() {

        super.onStart()
        jobManager.start()
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

        binding.participantProgressBar.visibility = View.VISIBLE

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        siteNames.clear()
        siteNames.add(getString(R.string.filter1_default))

        participantListViewModel.setSite("All")
        participantListViewModel.setSiteId("en")


        participantListViewModel.siteSpinnerItem!!.observe(this, Observer {

            if (it.status.equals(Status.SUCCESS)) {

                if (!it.data!!.data!!.contains("")|| !it.data.data!!.contains("null"))
                {
                    for( siteName in it.data.data!!)
                    {
                        siteNames.add(siteName)
                    }
                }
            }
            else
            {
                Log.d("PARTICIPANT_FRAGMENT", "Site_spinner_Error_is: " + it.status.toString())
            }
        })


        val adapter1 = ArrayAdapter(context!!, R.layout.participant_spinner_dropdown_item, siteNames)
        binding.filterOne.setAdapter(adapter1)

        statuses.clear()
        statuses.add(getString(R.string.filter2_default))
        statuses.add(getString(R.string.follow_up_1))
        statuses.add(getString(R.string.follow_up_2))
        statuses.add(getString(R.string.follow_up_9))
        //statuses.add(getString(R.string.follow_up_4))
        statuses.add(getString(R.string.follow_up_5))
        statuses.add(getString(R.string.follow_up_6))
        statuses.add(getString(R.string.follow_up_7))
        statuses.add(getString(R.string.follow_up_8))
        val adapter2 = ArrayAdapter(context!!, R.layout.participant_spinner_dropdown_item, statuses)
        binding.filterTwo.setAdapter(adapter2)

        binding.homeViewModel = participantListViewModel

        val participantAdapter = ParticipantListAdapter(dataBindingComponent, appExecutors) {

            Timber.d(it.toString())
        }

        this.adapter = participantAdapter
        binding.nghruList.adapter = participantAdapter
        binding.nghruList.setLayoutManager(GridLayoutManager(activity, 1))
        //participantListViewModel.setId("en")

//        participantListViewModel.setSiteId("e")

        // first time populate the list --------------------------------------------------------------

        //participantListViewModel.setFilterId(page=1, status = "all", site = "all", keyWord = searchKey!!)

        participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

            if (isNetworkAvailable())
            {
                if (it.status.equals(Status.SUCCESS))
                {
                    participantListObject = it.data!!.data!!.listRequest!!
                    participantAdapter.submitList(participantListObject)
                    participantAdapter.notifyDataSetChanged()
                    participantListMeta = it.data.data!!.meta
                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                    binding.participantProgressBar.visibility = View.GONE
                    //listItemOnClick()
                }
                else
                {
                    participantAdapter.submitList(emptyList())
                    //binding.participantProgressBar.visibility = View.GONE
                }
            }
            else
            {
                Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }

        })

        // ---------------------------------------------------------------------------------------------


//        participantListViewModel.participantlistItem.observe(this, Observer {
//
////                if (it.status.equals(Status.SUCCESS)) {
////
//////                    participantListObject.addAll(it.data!!.data?.listRequest!!)
////                    participantListObject = it.data!!.data!!.listRequest!!
////                    participantAdapter.submitList(participantListObject)
////                    participantAdapter.notifyDataSetChanged()
////
////                    participantListMeta = it.data.data!!.meta
////                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
////                    total_pages = it.data.data.meta!!.last_page!!.toInt()
////
//////                    total_pages = Integer.parseInt(it.data.data!!.meta!!.total)
////
////                }
////                else
////                {
////                    participantAdapter.submitList(emptyList())
////                }
//
//
//
//        })

        binding.firstButton.singleClick {

            if (participantListMeta != null)
            {
                //binding.participantProgressBar.visibility = View.GONE
                val lastPage = participantListMeta!!.last_page!!.toInt()

                val firstPage = lastPage - (lastPage-1)

                if (filter_one.selectedItem.toString().equals("All"))
                {
                    selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite  = filter_one.selectedItem.toString()
                }

//                if (filter_two.selectedItem.toString().equals("All"))
//                {
//                    selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                }
//                else
//                {
//                    selectedStatus  = filter_two.selectedItem.toString()
//                }

                participantListViewModel.setFilterId(page=firstPage, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        participantListObject = it.data!!.data!!.listRequest!!
                        participantAdapter.submitList(participantListObject)
                        participantAdapter.notifyDataSetChanged()
                        participantListMeta = it.data.data!!.meta
                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                        //binding.participantProgressBar.visibility = View.GONE
                        //listItemOnClick()
                    }
                    else
                    {
                        participantAdapter.submitList(emptyList())
                        //binding.participantProgressBar.visibility = View.GONE
                    }
                })
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }
        }

        binding.previousButton.singleClick {

            if (participantListMeta != null)
            {
                //binding.participantProgressBar.visibility = View.GONE
                val newPageNumber = participantListMeta!!.current_page!!.toInt() - 1

                if (filter_one.selectedItem.toString().equals("All"))
                {
                    selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite  = filter_one.selectedItem.toString()
                }

//                if (filter_two.selectedItem.toString().equals("All"))
//                {
//                    selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                }
//                else
//                {
//                    selectedStatus  = filter_two.selectedItem.toString()
//                }

                participantListViewModel.setFilterId(page=newPageNumber, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        participantListObject = it.data!!.data!!.listRequest!!
                        participantAdapter.submitList(participantListObject)
                        participantAdapter.notifyDataSetChanged()
                        participantListMeta = it.data.data!!.meta
                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                        //binding.participantProgressBar.visibility = View.GONE
                        //listItemOnClick()
                    }
                    else
                    {
                        participantAdapter.submitList(emptyList())
                        //binding.participantProgressBar.visibility = View.GONE
                    }
                })
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }
        }

        binding.nextButton.singleClick {

            if (participantListMeta != null)
            {
                //binding.participantProgressBar.visibility = View.GONE
                if (participantListMeta!!.current_page!!.toInt() != participantListMeta!!.last_page!!.toInt())
                {
                    val newPageNumber = participantListMeta!!.current_page!!.toInt() + 1

                    Log.d("PAGINATION", "Next Page numer" + newPageNumber)

                    if (filter_one.selectedItem.toString().equals("All"))
                    {
                        selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                    }
                    else
                    {
                        selectedSite  = filter_one.selectedItem.toString()
                    }

//                    if (filter_two.selectedItem.toString().equals("All"))
//                    {
//                        selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                    }
//                    else
//                    {
//                        selectedStatus  = filter_two.selectedItem.toString()
//                    }

                    participantListViewModel.setFilterId(page=newPageNumber, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                    participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (it.status.equals(Status.SUCCESS))
                        {
                            participantListObject = it.data!!.data!!.listRequest!!
                            participantAdapter.submitList(participantListObject)
                            participantAdapter.notifyDataSetChanged()
                            participantListMeta = it.data.data!!.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            //binding.participantProgressBar.visibility = View.GONE
                            //listItemOnClick()
                        }
                        else
                        {
                            participantAdapter.submitList(emptyList())
                            //binding.participantProgressBar.visibility = View.GONE
                        }
                    })
                }
                else
                {
                    Toast.makeText(activity, "Data already reached the last page", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }


        }

        binding.lastButton.singleClick {

            if (participantListMeta != null)
            {
                //binding.participantProgressBar.visibility = View.GONE
                val lastPage = participantListMeta!!.last_page!!.toInt()

                if (filter_one.selectedItem.toString().equals("All"))
                {
                    selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite  = filter_one.selectedItem.toString()
                }

//                if (filter_two.selectedItem.toString().equals("All"))
//                {
//                    selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                }
//                else
//                {
//                    selectedStatus  = filter_two.selectedItem.toString()
//                }

                participantListViewModel.setFilterId(page=lastPage, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        participantListObject = it.data!!.data!!.listRequest!!
                        participantAdapter.submitList(participantListObject)
                        participantAdapter.notifyDataSetChanged()
                        participantListMeta = it.data.data!!.meta
                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                        //binding.participantProgressBar.visibility = View.GONE
                        //listItemOnClick()
                    }
                    else
                    {
                        participantAdapter.submitList(emptyList())
                        //binding.participantProgressBar.visibility = View.GONE
                    }
                })
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }
        }

        binding.filterTwo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long)
            {
                if (position == 0)
                {
                    if (participantListMeta != null)
                    {
//                        selectedStatus = statuses[position]
                        //selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
                        if (filter_one.selectedItem.toString().equals("All"))
                        {
                            selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                        }
                        else
                        {
                            selectedSite = binding.filterOne.selectedItem.toString()
                        }
                        participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)
//
                        participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
//                        participantListObject.addAll(it.data!!.data?.listRequest!!)
                                participantListObject = it.data!!.data!!.listRequest!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
                                participantListMeta = it.data.data!!.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                //listItemOnClick()
                            }
                            else
                            {
                                participantAdapter.submitList(emptyList())
                            }
                        })
                    }
                    else
                    {
                        //selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
                        if (filter_one.selectedItem.toString().equals("All"))
                        {
                            selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                        }
                        else
                        {
                            selectedSite = binding.filterOne.selectedItem.toString()
                        }
                        participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)
//
                        participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
//                        participantListObject.addAll(it.data!!.data?.listRequest!!)
                                participantListObject = it.data!!.data!!.listRequest!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
                                participantListMeta = it.data.data!!.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                //listItemOnClick()
                            }
                            else
                            {
                                participantAdapter.submitList(emptyList())
                            }
                        })
                    }
                }
                else
                {
                    //selectedStatus = statuses[position]

                    if (filter_one.selectedItem.toString().equals("All"))
                    {
                        selectedSite  = filter_one.selectedItem.toString().toLowerCase()
                    }
                    else
                    {
                        selectedSite = binding.filterOne.selectedItem.toString()
                    }
                    participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)
//
                    participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (it.status.equals(Status.SUCCESS))
                        {
//                        participantListObject.addAll(it.data!!.data?.listRequest!!)
                            participantListObject = it.data!!.data!!.listRequest!!
                            participantAdapter.submitList(participantListObject)
                            participantAdapter.notifyDataSetChanged()
                            participantListMeta = it.data.data!!.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            //listItemOnClick()
                        }
                        else
                        {
                            participantAdapter.submitList(emptyList())
                        }
                    })
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }

        }

        binding.filterOne.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long)
            {
                if (position == 0)
                {
                    if (participantListMeta != null)
                    {
                        selectedSite  = filter_one.selectedItem.toString().toLowerCase()

//                        if (filter_two.selectedItem.toString().equals("All"))
//                        {
//                            selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                        }
//                        else
//                        {
//                            selectedStatus = binding.filterTwo.selectedItem.toString()
//                        }
                        participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                        participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
//                        participantListObject.addAll(it.data!!.data?.listRequest!!)
                                participantListObject = it.data!!.data!!.listRequest!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
                                participantListMeta = it.data.data!!.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                //listItemOnClick()
                            }
                            else
                            {
                                participantAdapter.submitList(emptyList())
                            }
                        })
                    }
                    else
                    {
                        selectedSite  = filter_one.selectedItem.toString().toLowerCase()

//                        if (filter_two.selectedItem.toString().equals("All"))
//                        {
//                            selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                        }
//                        else
//                        {
//                            selectedStatus = binding.filterTwo.selectedItem.toString()
//                        }
                        participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                        participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
//                        participantListObject.addAll(it.data!!.data?.listRequest!!)
                                participantListObject = it.data!!.data!!.listRequest!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
                                participantListMeta = it.data.data!!.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                //listItemOnClick()
                            }
                            else
                            {
                                participantAdapter.submitList(emptyList())
                            }
                        })
                    }
                }
                else
                {
                    selectedSite = siteNames[position]

//                    if (filter_two.selectedItem.toString().equals("All"))
//                    {
//                        selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//                    }
//                    else
//                    {
//                        selectedStatus = binding.filterTwo.selectedItem.toString()
//                    }
                    participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                    participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (it.status.equals(Status.SUCCESS))
                        {
//                        participantListObject.addAll(it.data!!.data?.listRequest!!)
                            participantListObject = it.data!!.data!!.listRequest!!
                            participantAdapter.submitList(participantListObject)
                            participantAdapter.notifyDataSetChanged()
                            participantListMeta = it.data.data!!.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            //listItemOnClick()
                        }
                        else
                        {
                            participantAdapter.submitList(emptyList())
                        }
                    })
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }

        }

        participantListViewModel.setUser("Login")
        participantListViewModel.user?.observe(this, Observer { user ->
            if (user?.data != null) {
                binding.user = user.data
                binding.executePendingBindings()
            }

        })

        binding.textInputEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //println(s)
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 0)
                {
                    searchKey = s.toString()

                } else {
                    participantListViewModel.setId("en")
                }
            }
        })

        binding.searchButton.singleClick {

            if (filter_one.selectedItem.toString().equals("All"))
            {
                selectedSite  = filter_one.selectedItem.toString().toLowerCase()
            }
            else
            {
                selectedSite = binding.filterOne.selectedItem.toString()
            }

//            if (filter_two.selectedItem.toString().equals("All"))
//            {
//                selectedStatus  = filter_two.selectedItem.toString().toLowerCase()
//            }
//            else
//            {
//                selectedStatus = binding.filterTwo.selectedItem.toString()
//            }
            participantListViewModel.setFilterId(page=1, status = getStatusString(), site = getStatusString(), keyWord = searchKey!!)

            participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                if (it.status.equals(Status.SUCCESS))
                {
                    participantListObject = it.data!!.data!!.listRequest!!
                    participantAdapter.submitList(participantListObject)
                    participantAdapter.notifyDataSetChanged()
                    participantListMeta = it.data.data!!.meta
                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                    listItemOnClick()
                }
                else
                {
                    participantAdapter.submitList(emptyList())
                }
            })
        }

        listItemOnClick()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun getStatusString(): String
    {
        var returnString: String = ""

        if (binding.filterTwo.selectedItem.toString().equals("Not started"))
        {
            returnString = "not_started"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Not complete"))
        {
            returnString = "not_complete"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Not complete - Physical measurement pending"))
        {
            returnString = "physical_measurement_pending"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Not complete - Questionnaire pending"))
        {
            returnString = "questionnaire_pending"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Completed"))
        {
            returnString = "completed"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("All"))
        {
            returnString = "all"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Declined"))
        {
            returnString = "declined"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Unable to complete"))
        {
            returnString = "unable_to_complete"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Unable to contact"))
        {
            returnString = "unable_to_contact"
        }
        else if (binding.filterTwo.selectedItem.toString().equals("Not scheduled"))
        {
            returnString = "not_scheduled"
        }

        return returnString
    }

    private fun listItemOnClick()
    {
        binding.nghruList.addOnItemTouchListener(RecyclerItemClickListner(activity!!, binding.nghruList, object : RecyclerItemClickListner.OnItemClickListener {

            override fun onItemClick(view: View, position: Int)
            {
                val singleParticipant = participantListObject.get(position)

                if (singleParticipant!!.status.equals("completed"))
                {
                    val statusCompletedDialogFragment = StatusCompletedDialogFragment()
                    statusCompletedDialogFragment.arguments = bundleOf("single_participant" to singleParticipant!!)
                    statusCompletedDialogFragment.show(fragmentManager!!)
                    Log.d("PARTICIPANT_LIST" , " dOB_IS: " + singleParticipant.dob)
                }
                else
                {
                    val json: String = MemberTypeConverters.gson.toJson(singleParticipant)
                    Log.d("PARTICIPANT_LIST" , " dOB_IS: " + singleParticipant.dob)
                    prefs?.edit()?.putString("single_participant", json)?.apply()
                    val intent = Intent(activity, PatientAttendanceActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
