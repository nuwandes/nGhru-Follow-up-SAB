package org.nghru_lk.ghru.ui.questionnaire.web


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebStorage.QuotaUpdater
import android.widget.Toast
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
import io.reactivex.disposables.CompositeDisposable
import org.nghru_lk.ghru.R
import org.nghru_lk.ghru.binding.FragmentDataBindingComponent
import org.nghru_lk.ghru.databinding.WebFragmentBinding
import org.nghru_lk.ghru.di.Injectable
import org.nghru_lk.ghru.jobs.SyncSurveyJob
import org.nghru_lk.ghru.sync.SyncResponseEventType
import org.nghru_lk.ghru.sync.SyncServeyRxBus
import org.nghru_lk.ghru.ui.questionnaire.cancel.CancelDialogFragment
import org.nghru_lk.ghru.util.*
import org.nghru_lk.ghru.vo.*
//import org.southasia.ghru.vo.Date
import org.nghru_lk.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class WebFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<WebFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var viewModel: WebViewModel
    private var myWebSettings: WebSettings? = null
    private var databasePath: String? = null


    @Inject
    lateinit var jobManager: JobManager

    private val disposables = CompositeDisposable()

    private var participant: ParticipantListItem? = null
    var user: User? = null
    var meta: Meta? = null
    val endTime: String = ""
    private var questionnaire: Questionnaire? = null
    var questionareJson : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            meta = arguments?.getParcelable<Meta>("meta")!!
            participant = arguments?.getParcelable<ParticipantListItem>("ParticipantRequest")!!
            questionnaire = arguments?.getParcelable<Questionnaire>("Questionnaire")!!
            questionareJson = questionnaire?.json!!
        } catch (e: KotlinNullPointerException) {

        }

//        val endTime: String = convertTimeTo24Hours()
//        val endDate: String = getDate()
//        val endDateTime:String = endDate + " " + endTime

        disposables.add(
            SyncServeyRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    if (isNetworkAvailable()) {
                        activity?.runOnUiThread {
                            viewModel.setLocalUpdateParticipantQueStatus(participant!!)
                            meta?.endTime = binding.root.getLocalTimeString()

                            viewModel.setSurvey(
                                QuestionMeta(
                                    meta = meta,
                                    json = result.json,
                                    screeningId = participant?.participant_id!!,
                                    questionnaireId = questionnaire?.id,
                                    language = questionnaire?.language
                                )
                            )
                        }

                    } else {
                        meta?.endTime = binding.root.getLocalTimeString()
                        jobManager.addJobInBackground(
                            SyncSurveyJob(
                                QuestionMeta(
                                    meta = meta,
                                    json = result.json,
                                    screeningId = participant?.participant_id!!,
                                    questionnaireId = questionnaire?.id,
                                    language = questionnaire?.language
                                )
                            )
                        )
                        activity?.runOnUiThread{
                            viewModel.setLocalUpdateParticipantQueStatus(participant!!)
                        }

                        activity!!.finish()
                    }

                }, { error ->
                    error.printStackTrace()
                })
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<WebFragmentBinding>(
            inflater,
            R.layout.web_fragment,
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

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        //binding.participant = participant
        viewModel.setUser("user")
        viewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {
                // setupNavigationDrawer(userData.data)
                user = userData.data

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                meta = Meta(collectedBy = user?.id, startTime = sDateTime)

                meta?.registeredBy = user?.id
                //meta?.registeredBy = user?.id
            }

        })

        binding.titleName.setText(participant!!.firstname + " " + participant!!.last_name)
        binding.titleGender.setText(participant!!.gender)
        binding.titleParticipantId.setText(participant!!.participant_id)

        val dob_year: String = participant!!.dob!!.substring(0,4)
        val dob_month: String = participant!!.dob!!.substring(5,7)
        val dob_date : String = participant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
        binding.titleAge.setText(participantAge + "Y")

        myWebSettings = binding.webView.getSettings()
        databasePath = activity!!.getDir("database", Context.MODE_PRIVATE).getPath()

        myWebSettings!!.setJavaScriptEnabled(true)
        myWebSettings!!.setDatabaseEnabled(true)
        myWebSettings!!.setDatabasePath(databasePath)

        binding.webView.addJavascriptInterface(JavascriptInterface(activity, viewModel, jobManager), "Android")
        viewModel.survey?.observe(this, Observer { commonResponce ->

            //println(commonResponce.toString())
            if (commonResponce?.status == Status.SUCCESS) {
                //println(user)
               // println("Status.SUCCESS")
                Toast.makeText(activity!!, getString(R.string.questionnaire_success), Toast.LENGTH_SHORT).show()
                activity!!.finish()
//                val intent = Intent(activity, MeasurementListActivity::class.java)
//                startActivity(intent)

            } else if (commonResponce?.status == Status.ERROR) {
                //Crashlytics.logException(Exception(commonResponce.message?.message))
               // Crashlytics.setString("comment", binding.comment.text.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("questionareJson " + commonResponce.message?.data?.message))
                //Timber.d(commonResponce.message?.message)
              //  activity!!.showToast(commonResponce.message?.message!!)
            }
        })





//        viewModel.language?.observe(this, Observer { commonResponce ->
//
//           // println(commonResponce.toString())
//            if (commonResponce?.status == Status.SUCCESS) {
//                //println(user)
//                questionareJson = commonResponce.data?.json!!
//                binding.webView.loadUrl("file:///android_asset/q/index.html")
//
//            } else if (commonResponce?.status == Status.ERROR) {
//                //Crashlytics.logException(Exception(commonResponce.message?.message))
//            }
//        })
//
//        viewModel.getQuestionnaire(network =  isNetworkAvailable(), language = "en")

        binding.webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if(questionareJson != null) {
                    binding.webView.loadUrl("javascript:init(" + questionareJson + ")")

                    Log.d("WEB_FRAG","URL: " + url)
                    Log.d("WEB_FRAG","URL_questionnaire: " + questionareJson)
                }

            }

//            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                super.onPageStarted(view, url, favicon)
//                view?.loadUrl("javascript:init(\"bciwbcibe\")");
//            }

        })




        binding.webView.setWebChromeClient(object : WebChromeClient() {
            private val TAG = "WebView"

            override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
                Log.d(TAG, cm.sourceId() + ": Line " + cm.lineNumber() + " : " + cm.message())
                return true
            }

            override fun onExceededDatabaseQuota(
                url: String, databaseIdentifier: String, currentQuota: Long, estimatedSize: Long,
                totalUsedQuota: Long, quotaUpdater: QuotaUpdater
            ) {
                quotaUpdater.updateQuota(estimatedSize * 2)
            }
        })

        binding.webView.loadUrl("file:///android_asset/q/index.html")
        Log.d("WEB_FRAG","SECOND_URL: " + "file:///android_asset/q/index.html")
        Log.d("WEB_FRAG","SECOND_URL_questionnaire: " + questionareJson)

        binding.buttonCancel.singleClick {

            val cancelDialogFragment = CancelDialogFragment()
            cancelDialogFragment.arguments = bundleOf("participant" to participant)
            cancelDialogFragment.show(fragmentManager!!)
        }

        viewModel.getLocalUpdateParticipantQueStatus?.observe(this, Observer { bmStatus ->

            if (bmStatus?.status == Status.SUCCESS)
            {
                Toast.makeText(activity, "Questionnaire status locally updated", Toast.LENGTH_LONG).show()
                Log.wtf("WEB_FRAGMENT","LOCALLY SAVED SUCCESS")

            }
            else if(bmStatus?.status == Status.ERROR)
            {
                Toast.makeText(activity, "Update Questionnaire status failed", Toast.LENGTH_LONG).show()
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    class JavascriptInterface(
        val mContext: FragmentActivity?,
        val viewModel: WebViewModel,
        val jobManager: JobManager
    ) {
        fun finish() {

        }

        fun showAndroidToast() {
            Toast.makeText(mContext, "ss", Toast.LENGTH_LONG).show();
        }

        @android.webkit.JavascriptInterface
        fun showToast(json: String) {
            SyncServeyRxBus.getInstance().post(SyncResponseEventType.SUCCESS, json = json)

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
