package org.nghru_hpv.ghru.ui.intake.readings


import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_hpv.ghru.R
import org.nghru_hpv.ghru.binding.FragmentDataBindingComponent
import org.nghru_hpv.ghru.databinding.IntakeWebFragmentBinding
import org.nghru_hpv.ghru.databinding.WebFragmentBinding
import org.nghru_hpv.ghru.db.MemberTypeConverters
import org.nghru_hpv.ghru.di.Injectable
import org.nghru_hpv.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.nghru_hpv.ghru.ui.intake.cancel.CancelDialogFragment
import org.nghru_hpv.ghru.util.autoCleared
import org.nghru_hpv.ghru.util.fromJson
import org.nghru_hpv.ghru.util.getLocalTimeString
import org.nghru_hpv.ghru.util.singleClick
import org.nghru_hpv.ghru.vo.Meta
import org.nghru_hpv.ghru.vo.ParticipantListItem
import org.nghru_hpv.ghru.vo.Status
import org.nghru_hpv.ghru.vo.User
import org.nghru_hpv.ghru.vo.request.IntakeRequestNew
import org.nghru_hpv.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class IntakeReadingsFragment  : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<IntakeWebFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: IntakeReadingsViewModel
    private var myWebSettings: WebSettings? = null
    private var databasePath: String? = null

    @Inject
    lateinit var jobManager: JobManager
    private var participant: ParticipantRequest? = null

    var webUrl: String = ""

    var user: User? = null
    var meta: Meta? = null

//    ----------------------------------------------------------------------------

    var prefs : SharedPreferences? = null

    private var selectedParticipant: ParticipantListItem? = null

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
        val dataBinding = DataBindingUtil.inflate<IntakeWebFragmentBinding>(
            inflater,
            R.layout.intake_web_fragment,
            container,
            false
        )
        binding = dataBinding
        binding.participant = participant;
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

        myWebSettings = binding.webViewDiet.getSettings()
        databasePath = activity!!.getDir("database", Context.MODE_PRIVATE).getPath()

        myWebSettings!!.setJavaScriptEnabled(true)
        myWebSettings!!.setDatabaseEnabled(true)
        myWebSettings!!.setDatabasePath(databasePath)
        myWebSettings!!.setLoadWithOverviewMode(true)
        myWebSettings!!.setUseWideViewPort(true)
        myWebSettings!!.setAppCacheEnabled(true)
        myWebSettings!!.setCacheMode(WebSettings.LOAD_NO_CACHE)
        myWebSettings!!.setDatabaseEnabled(true)
        myWebSettings!!.setDomStorageEnabled(true)
        myWebSettings!!.setGeolocationEnabled(false)
        myWebSettings!!.setSaveFormData(false)
        myWebSettings!!.setJavaScriptCanOpenWindowsAutomatically(true)

        binding.webViewDiet.addJavascriptInterface(JavascriptInterface(activity, viewModel, jobManager), "Android")

        binding.webViewDiet.setWebChromeClient(object : WebChromeClient() {
            private val TAG = "WebView"

            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.d(TAG, cm.sourceId() + ": Line " + cm.lineNumber() + " : " + cm.message())
                return true
            }

            override fun onExceededDatabaseQuota(
                url: String, databaseIdentifier: String, currentQuota: Long, estimatedSize: Long,
                totalUsedQuota: Long, quotaUpdater: WebStorage.QuotaUpdater
            ) {
                quotaUpdater.updateQuota(estimatedSize * 2)
            }

        })



        binding.completeButton.singleClick {
            //showCompleteCOnfirmationDialog()
            showAlert()
        }

        binding.cancelButton.singleClick {

            if (participant != null)
            {
                val cancelDialogFragment = CancelDialogFragment()
                cancelDialogFragment.arguments = bundleOf("participant" to participant)
                cancelDialogFragment.show(fragmentManager!!)
            }
            else
            {
                Log.d("GUIDE_FRAG", "INSIDE_CANCEL_PARTICIPANT_NULL: ")
                viewModel.setScreeningId("AAA")

                viewModel.participant.observe(this, Observer { participantResource ->

                    if (participantResource?.status == Status.SUCCESS)
                    {
                        participant = participantResource.data?.data
                        participant!!.meta = meta

                        val cancelDialogFragment = CancelDialogFragment()
                        cancelDialogFragment.arguments = bundleOf("participant" to participant)
                        cancelDialogFragment.show(fragmentManager!!)
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

//        val intakeRequest = IntakeRequestNew(meta = participant!!.meta)
////        viewModel.setParticipant(intakeRequest, participant?.screeningId)
////        Log.d("INTAKE_READING_FRAGMENT","REQUEST_BODY: " + intakeRequest.meta)
//
//        viewModel.setIntakeMeta(intakeRequest = intakeRequest, screen_id = participant!!.screeningId)

        viewModel.intakePostComplete?.observe(this, Observer { participantResource ->
            if (participantResource?.status == Status.SUCCESS) {
                println(participantResource.data?.data)
                if (participantResource.data != null) {

                    val intakeData = participantResource.data.data
                    binding.webViewDiet.loadUrl(intakeData?.intake_url)
                    webUrl = intakeData!!.intake_url

                    Log.d("INTAKE_FRAGMENT", "URL: " + webUrl)

                }
            }
        })

        binding.webViewDiet.setWebViewClient(object : WebViewClient() {

            @SuppressWarnings("deprecation")
            override fun onReceivedError(view: WebView , errorCode: Int , description: String , failingUrl: String ) {
                Toast.makeText(activity!!, description, Toast.LENGTH_SHORT).show()
                Log.d("INTAKE_FRAGMENT","ERROR_IS: " + description)
            }

            override fun onReceivedError(view: WebView , req: WebResourceRequest , rerr: WebResourceError ) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if(url != null) {
                    //binding.webView.loadUrl(webUrl)
                }

            }
            //
            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.d("INTAKE_READING","HTTP_ERROR_REQUEST: " + request.toString() + " AND HTTP_ERROR_RESPONSE " + errorResponse.toString())
            }

            override fun onReceivedHttpAuthRequest(
                view: WebView?,
                handler: HttpAuthHandler?,
                host: String?,
                realm: String?
            ) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm)
                Log.d("INTAKE_READING" , "HTTP_AUTH_REQUEST_ERROR: " + realm + ", " + host)
            }

            override fun shouldInterceptRequest (view: WebView? , url: String? ): WebResourceResponse? {
                if (url!!.contains(".mime")) {
                    //return getCssWebResourceResponseFromAsset()
                    Log.d("INTAKE_READING" , "INTERCEPT_REQUEST: " + url)
                } else {
                    return super.shouldInterceptRequest(view, url)
                }

                return null
            }
        })

        viewModel.intakeUpdateComplete?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data?.data)
                if (assertsResource.data != null) {
                    val completedDialogFragment = CompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                    completedDialogFragment.show(fragmentManager!!)

                } else {
                    binding.completeButton.visibility = View.VISIBLE
                    toast(assertsResource.message.toString())
                    Crashlytics.logException(Exception("IntakeComplete " + assertsResource.message.toString()))
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

    fun navController() = findNavController()

    private fun showCompleteCOnfirmationDialog(){

        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(context!!)

        // builder.setTitle("Title")
        builder.setMessage(getString(R.string.Intake_confirmation_message))

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> { updateIntakeStation() }

//                DialogInterface.BUTTON_NEGATIVE -> toast("Negative/No button clicked.")

            }
        }

        builder.setPositiveButton(getString(R.string.app_yes),dialogClickListener)
        builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)

        dialog = builder.create()

        dialog.show()
    }

    private fun showAlert()
    {
        val alert =  AlertDialog.Builder(context!!)

        val edittext = EditText(context!!)
        edittext.hint = "Enter Password"
        edittext.maxLines = 1

        val layout = FrameLayout(context!!)
        layout.setPaddingRelative(45,15,45,0)
        alert.setTitle(getString(R.string.diet_msg_title))
        alert.setMessage(getString(R.string.diet_msg_message))
        layout.addView(edittext)
        alert.setView(layout)

        alert.setPositiveButton(getString(R.string.app_button_ok), DialogInterface.OnClickListener {
                dialog, which ->
            run {
                val qName = edittext.text.toString()

                if (qName.equals("ghru"))
                {
                    updateIntakeStation()
                }
                else
                {
                    toast("Password mismatch")
                }
            }
        })
        alert.setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener {

                dialog, which ->
            run {
                dialog.dismiss()
            }
        })
        alert.show()
    }

    private fun toast(message: String) {
        Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
    }
    private fun updateIntakeStation()
    {
        binding.completeButton.visibility = View.GONE
        val intakeRequest = IntakeRequestNew(meta = participant!!.meta)

        val endTime: String = convertTimeTo24Hours()
        val endDate: String = getDate()
        val endDateTime:String = endDate + " " + endTime

        intakeRequest.meta!!.endTime = endDateTime
        intakeRequest.status = "100"
//        intakeRequest.status = "100"
        //intakeRequest.meta = participant!!.meta
//        intakeRequest.status = "100"
        viewModel.updateParticipant(intakeRequest, participant?.screeningId)

    }

    class JavascriptInterface(
        val mContext: FragmentActivity?,
        val viewModel: IntakeReadingsViewModel,
        val jobManager: JobManager
    ) {
        fun finish() {

        }

        fun showAndroidToast() {
            Toast.makeText(mContext, "ss", Toast.LENGTH_LONG).show()
        }

        @android.webkit.JavascriptInterface
        fun showToast(json: String) {
            //SyncServeyRxBus.getInstance().post(SyncResponseEventType.SUCCESS, json = json)

        }

        private fun isNetworkAvailable(): Boolean {
            val connectivityManager = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }

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


