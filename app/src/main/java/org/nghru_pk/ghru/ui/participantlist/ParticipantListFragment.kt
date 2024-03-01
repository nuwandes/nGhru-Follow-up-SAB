package org.nghru_pk.ghru.ui.participantlist

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
import org.nghru_pk.ghru.AppExecutors
import org.nghru_pk.ghru.PatientAttendanceActivity
import org.nghru_pk.ghru.R
import org.nghru_pk.ghru.binding.FragmentDataBindingComponent
import org.nghru_pk.ghru.databinding.ParticipantListFragmentBinding
import org.nghru_pk.ghru.db.MemberTypeConverters
import org.nghru_pk.ghru.di.Injectable
import org.nghru_pk.ghru.ui.participantlist.statuscompleted.StatusCompletedDialogFragment
import org.nghru_pk.ghru.util.RecyclerItemClickListner
import org.nghru_pk.ghru.util.autoCleared
import org.nghru_pk.ghru.util.singleClick
import org.nghru_pk.ghru.vo.ParticipantListItem
import org.nghru_pk.ghru.vo.ParticipantListMeta
import org.nghru_pk.ghru.vo.Status
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

    private var participantListObject: List<ParticipantListItem> = arrayListOf()

    private var participantListMeta: ParticipantListMeta? = null

    private var selectedStatus: String? = ""
    private var selectedSite: String? = ""
    private var searchKey: String? = ""

    private var currect_page:Int = 0
    private var total_pages:Int = 0

    var prefs : SharedPreferences? = null

    var siteArray : ArrayList<String>? = arrayListOf()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            siteArray = arguments?.getStringArrayList("SITE_ARRAY")

        } catch (e: KotlinNullPointerException) {
            print(e)
        }
    }

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

        if (isNetworkAvailable())
        {
            binding.bottomLayout.visibility = View.VISIBLE
        }
        else
        {
            binding.bottomLayout.visibility = View.GONE
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        siteNames.clear()
        siteNames.add(getString(R.string.filter1_default))

        if (!isNetworkAvailable())
        {
//            val sites : String? = prefs?.getString("SiteList","")
//            val mutableList: MutableList<String> = sites?.split(",")?.toMutableList() ?: mutableListOf()
//
//            val siteNames = sites!!.split(",")
//            Log.wtf("PARTICIPANT_LIST_FRAGMENT", "SITE_DATA: - " + mutableList)

            if (siteArray != null)
            {
                var siteList = siteArray!!.toMutableList()

                val adapter1 = ArrayAdapter(context!!, R.layout.participant_spinner_dropdown_item, siteList)
                binding.filterOne.setAdapter(adapter1)
            }
            else
            {
                val sites : String? = prefs?.getString("SiteList","")
                val mutableList: MutableList<String> = sites?.split(",")?.toMutableList() ?: mutableListOf()

                val siteNames = sites!!.split(",")
                Log.wtf("PARTICIPANT_LIST_FRAGMENT", "SITE_DATA: - " + mutableList)

                val adapter1 = ArrayAdapter(context!!, R.layout.participant_spinner_dropdown_item, siteNames)
                binding.filterOne.setAdapter(adapter1)
            }


        }
        else
        {
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

                        Log.wtf("PARTICIPANT_LIST_FRAGMENT", "SITE_DATA: - " + siteNames)
                        val adapter1 = ArrayAdapter(context!!, R.layout.participant_spinner_dropdown_item, siteNames)
                        binding.filterOne.setAdapter(adapter1)
                    }
                }
                else
                {
                    Log.d("PARTICIPANT_FRAGMENT", "Site_spinner_Error_is: " + it.status.toString())
                }
            })
        }

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

        if (isNetworkAvailable())
        {
            participantListViewModel.setFilterId(page=1, status = "all", site = "all", keyWord = searchKey!!)
        }
        else
        {
            participantListViewModel.setAllParticipantListItemsFromDb("GET")
        }


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
                }
                else
                {
                    participantAdapter.submitList(emptyList())
                    //binding.participantProgressBar.visibility = View.GONE
                }
            }
            else
            {
                Toast.makeText(activity, "Check your network connection", Toast.LENGTH_LONG).show()
            }

        })

        participantListViewModel.getAllParticipantListItemsFromDb?.observe(activity!!, Observer {

            if (it.status.equals(Status.SUCCESS))
            {
                Toast.makeText(activity, "Locally loaded successfully", Toast.LENGTH_LONG).show()
                participantListObject = it.data!!
                participantAdapter.submitList(it.data)
                participantAdapter.notifyDataSetChanged()
                binding.participantProgressBar.visibility = View.GONE
            }
//            else
//            {
//                Toast.makeText(activity, "Locally loading failed", Toast.LENGTH_LONG).show()
//            }

        })

        // ---------------------------------------------------------------------------------------------

        binding.firstButton.singleClick {

            if (participantListMeta != null)
            {
                val lastPage = participantListMeta!!.last_page!!.toInt()

                val firstPage = lastPage - (lastPage-1)

                if (binding.filterOne.selectedItem.toString().equals("All"))
                {
                    selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite  = binding.filterOne.selectedItem.toString()
                }

                participantListViewModel.setFilterId(page=firstPage, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        participantListObject = it.data!!.data!!.listRequest!!
                        participantAdapter.submitList(participantListObject)
                        participantAdapter.notifyDataSetChanged()
                        participantListMeta = it.data.data!!.meta
                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                    }
                    else
                    {
                        participantAdapter.submitList(emptyList())
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
                val newPageNumber = participantListMeta!!.current_page!!.toInt() - 1

                if (binding.filterOne.selectedItem.toString().equals("All"))
                {
                    selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite  = binding.filterOne.selectedItem.toString()
                }

                participantListViewModel.setFilterId(page=newPageNumber, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        participantListObject = it.data!!.data!!.listRequest!!
                        participantAdapter.submitList(participantListObject)
                        participantAdapter.notifyDataSetChanged()
                        participantListMeta = it.data.data!!.meta
                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                    }
                    else
                    {
                        participantAdapter.submitList(emptyList())
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
                if (participantListMeta!!.current_page!!.toInt() != participantListMeta!!.last_page!!.toInt())
                {
                    val newPageNumber = participantListMeta!!.current_page!!.toInt() + 1

                    Log.d("PAGINATION", "Next Page numer" + newPageNumber)

                    if (binding.filterOne.selectedItem.toString().equals("All"))
                    {
                        selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
                    }
                    else
                    {
                        selectedSite  = binding.filterOne.selectedItem.toString()
                    }

                    participantListViewModel.setFilterId(page=newPageNumber, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                    participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                        if (it.status.equals(Status.SUCCESS))
                        {
                            participantListObject = it.data!!.data!!.listRequest!!
                            participantAdapter.submitList(participantListObject)
                            participantAdapter.notifyDataSetChanged()
                            participantListMeta = it.data.data!!.meta
                            binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                        }
                        else
                        {
                            participantAdapter.submitList(emptyList())
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

                if (binding.filterOne.selectedItem.toString().equals("All"))
                {
                    selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite  = binding.filterOne.selectedItem.toString()
                }

                participantListViewModel.setFilterId(page=lastPage, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                    if (it.status.equals(Status.SUCCESS))
                    {
                        participantListObject = it.data!!.data!!.listRequest!!
                        participantAdapter.submitList(participantListObject)
                        participantAdapter.notifyDataSetChanged()
                        participantListMeta = it.data.data!!.meta
                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                    }
                    else
                    {
                        participantAdapter.submitList(emptyList())
                    }
                })
            }
            else
            {
                Toast.makeText(activity, "No data to display", Toast.LENGTH_LONG).show()
            }
        }

        // status filter
        binding.filterTwo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long)
            {
                if (isNetworkAvailable())
                {
                    if (position == 0)
                    {
                        if (participantListMeta != null)
                        {
                            if (binding.filterOne.selectedItem.toString().equals("All"))
                            {
                                selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
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
                                    participantListObject = it.data!!.data!!.listRequest!!
                                    participantAdapter.submitList(participantListObject)
                                    participantAdapter.notifyDataSetChanged()
                                    participantListMeta = it.data.data!!.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                }
                                else
                                {
                                    participantAdapter.submitList(emptyList())
                                }
                            })
                        }
                        else
                        {
//                            if (binding.filterOne.selectedItem.toString().equals("All"))
//                            {
//                                selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
//                            }
//                            else
//                            {
//                                selectedSite = binding.filterOne.selectedItem.toString()
//                            }
                            participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)
//
                            participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                                if (it.status.equals(Status.SUCCESS))
                                {
                                    participantListObject = it.data!!.data!!.listRequest!!
                                    participantAdapter.submitList(participantListObject)
                                    participantAdapter.notifyDataSetChanged()
                                    participantListMeta = it.data.data!!.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
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
                        if (binding.filterOne.selectedItem.toString().equals("All"))
                        {
                            selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
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
                                participantListObject = it.data!!.data!!.listRequest!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
                                participantListMeta = it.data.data!!.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
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
                    // offline filteration

                    if (binding.filterTwo.selectedItem.equals("All"))
                    {
                        participantListViewModel.setAllParticipantListItemsFromDb("Go")
                        participantListViewModel.setAllParticipantListItemsFromDb("Got")
                    }
                    else
                    {
                        participantListViewModel.setStatusParticipantListItemsFromDb(getStatusString())

                        participantListViewModel.getStatusParticipantListItemsFromDb?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
                                participantListObject = it.data!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
//                        participantListMeta = it.data.data!!.meta
//                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                listItemOnClick()
                            }
                            else
                            {
                                participantAdapter.submitList(emptyList())
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }

        }

        binding.filterOne.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long)
            {
                if (isNetworkAvailable())
                {
                    if (position == 0)
                    {
                        if (participantListMeta != null)
                        {
                            selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()

                            participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                            participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                                if (it.status.equals(Status.SUCCESS))
                                {
                                    participantListObject = it.data!!.data!!.listRequest!!
                                    participantAdapter.submitList(participantListObject)
                                    participantAdapter.notifyDataSetChanged()
                                    participantListMeta = it.data.data!!.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                }
                                else
                                {
                                    participantAdapter.submitList(emptyList())
                                }
                            })
                        }
                        else
                        {
                            selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()

                            participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                            participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                                if (it.status.equals(Status.SUCCESS))
                                {
                                    participantListObject = it.data!!.data!!.listRequest!!
                                    participantAdapter.submitList(participantListObject)
                                    participantAdapter.notifyDataSetChanged()
                                    participantListMeta = it.data.data!!.meta
                                    binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
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

                        participantListViewModel.setFilterId(page=1, status = getStatusString(), site = selectedSite!!, keyWord = searchKey!!)

                        participantListViewModel.filterparticipantListItems?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
                                participantListObject = it.data!!.data!!.listRequest!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
                                participantListMeta = it.data.data!!.meta
                                binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
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
                    // offline filter by Site
//                    if (position == 0)
//                    {
//                        selectedSite  = filter_one.selectedItem.toString().toLowerCase()
//                    }
//                    else
//                    {
//                        selectedSite = siteNames[position]
//                    }

                    selectedSite = binding.filterOne.selectedItem.toString().trimStart()

                    if (selectedSite.equals("[All"))
                    {
                        participantListViewModel.setAllParticipantListItemsFromDb("Go")
                        participantListViewModel.setAllParticipantListItemsFromDb("Got")
                    }
                    else
                    {
                        //participantListObject = emptyList()

                        participantListViewModel.setSiteParticipantListItemsFromDb(selectedSite!!)

                        participantListViewModel.getSiteParticipantListItemsFromDb?.observe(activity!!, Observer {

                            if (it.status.equals(Status.SUCCESS))
                            {
                                participantListObject = it.data!!
                                participantAdapter.submitList(participantListObject)
                                participantAdapter.notifyDataSetChanged()
//                        participantListMeta = it.data.data!!.meta
//                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                                listItemOnClick()
                            }
                            else
                            {
                                participantAdapter.submitList(emptyList())
                            }
                        })
                    }
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

            if (isNetworkAvailable())
            {
                if (binding.filterOne.selectedItem.toString().equals("All"))
                {
                    selectedSite  = binding.filterOne.selectedItem.toString().toLowerCase()
                }
                else
                {
                    selectedSite = binding.filterOne.selectedItem.toString()
                }

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
            else
            {
                // offline search
                if (searchKey.equals(""))
                {
                    participantListViewModel.setAllParticipantListItemsFromDb("Go")
                    participantListViewModel.setAllParticipantListItemsFromDb("Got")
                }
                else
                {
                    participantListViewModel.setSearchParticipantListItemsFromDb(searchKey!!)

                    participantListViewModel.getSearchParticipantListItemsFromDb?.observe(activity!!, Observer {

                        if (it.status.equals(Status.SUCCESS))
                        {
                            participantListObject = it.data!!
                            participantAdapter.submitList(participantListObject)
                            participantAdapter.notifyDataSetChanged()
//                        participantListMeta = it.data.data!!.meta
//                        binding.paginationText.setText(participantListMeta?.current_page + " of " + participantListMeta?.last_page)
                            listItemOnClick()
                        }
                        else
                        {
                            participantAdapter.submitList(emptyList())
                        }
                    })
                }
            }
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
                    Log.d("PARTICIPANT_LIST" , " dOB_IS: " + singleParticipant.dob + " verification_IS: " + singleParticipant.verification_id)
                }
                else
                {
                    val json: String = MemberTypeConverters.gson.toJson(singleParticipant)
                    Log.d("PARTICIPANT_LIST" , " dOB_IS: " + singleParticipant.dob + " verification_IS: " + singleParticipant.verification_id)
                    prefs?.edit()?.putString("single_participant", json)?.apply()
                    val intent = Intent(activity, PatientAttendanceActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onItemLongClick(view: View?, position: Int) {
                //TODO("do nothing")
            }
        }))
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
