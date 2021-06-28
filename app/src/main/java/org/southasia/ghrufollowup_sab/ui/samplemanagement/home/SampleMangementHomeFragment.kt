package org.southasia.ghrufollowup_sab.ui.samplemanagement.home


import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.squareup.otto.Subscribe
import io.reactivex.disposables.CompositeDisposable
import org.southasia.ghrufollowup_sab.R
import org.southasia.ghrufollowup_sab.binding.FragmentDataBindingComponent
import org.southasia.ghrufollowup_sab.databinding.SampleMangementHomeFragmentBinding
import org.southasia.ghrufollowup_sab.di.Injectable
import org.southasia.ghrufollowup_sab.event.*
import org.southasia.ghrufollowup_sab.jobs.SyncSampledProcessJob
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.util.getLocalTimeString
import org.southasia.ghrufollowup_sab.util.hideKeyboard
import org.southasia.ghrufollowup_sab.util.setDrawbleLeftColor
import org.southasia.ghrufollowup_sab.util.singleClick
import org.southasia.ghrufollowup_sab.vo.*
//import org.southasia.ghru.vo.Date
import org.southasia.ghrufollowup_sab.vo.request.SampleRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class SampleMangementHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: SampleMangementHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: SampleMangementHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager


    private var hb1Ac: Hb1AcDto? = null
    private var fastingBloodGlucose: FastingBloodGlucoseDto? = null
    // private var lipidProfile: LipidProfileDto? = null

    private var lipidProfileAllDto: LipidProfileAllDto? = null


    private var hOGTT: HOGTTDto? = null

    private var totalCholesterol: TotalCholesterolDto? = null

    private var hemoglobin: HemoglobinDto? = null

    private var hDL: HDLDto? = null

    private var triglyceridesDto: TriglyceridesDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sampleRequest = arguments?.getParcelable("SampleRequestResource")
            //L.d(sampleRequest.toString() + "sampleRequest ${sampleRequest?.storageId}")
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            TotalCholesterolRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    // if (result == null) {
                    Timber.d(result.toString())
                    totalCholesterol = result
                    navController().popBackStack()
                    binding.linearLayoutTotalCholesterol.visibility = View.VISIBLE
                    updateProcessValidUI(binding.TCTextView)
                    binding.executePendingBindings()
                    // }
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            FastingBloodGlucoseRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    //if (result == null) {
                    Timber.d(result.toString())
                    fastingBloodGlucose = result
                    binding.linearLayoutFastingBloodGlucose.visibility = View.VISIBLE
                    updateProcessValidUI(binding.fbgTextView)
                    binding.executePendingBindings()
                    // }
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<SampleMangementHomeFragmentBinding>(
            inflater,
            R.layout.sample_mangement_home_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.sample = sampleRequest

        if (fastingBloodGlucose != null) {
            binding.fbgCompleteView.visibility = View.VISIBLE
            binding.linearLayoutBlood.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)

        }

        if (totalCholesterol != null) {
            binding.TCCompleteView.visibility = View.VISIBLE
            binding.LinearLayoutTC.background =
                    resources.getDrawable(R.drawable.ic_process_complete_bg, null)

        }

        viewModel.sampleMangementPocess?.observe(this, Observer { sampleMangementPocess ->
            Timber.d(sampleMangementPocess.toString())
            if (sampleMangementPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (sampleMangementPocess?.status == Status.ERROR) {
                Crashlytics.setString("sample", sampleRequest.toString())
                Crashlytics.setString("hb1Ac", hb1Ac.toString())
                Crashlytics.setString("fastingBloodGlucose", fastingBloodGlucose.toString())
                Crashlytics.setString("lipidProfileAllDto", lipidProfileAllDto.toString())
                Crashlytics.setString("hOGTT", hOGTT.toString())

                Crashlytics.logException(Exception("sample collection " + sampleMangementPocess.message.toString()))
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
                //Crashlytics.logException(Exception(sampleMangementPocess.message?.message))
                //var error = accessToken.dat
            }
        })

        viewModel.sampleRequestLocal?.observe(this, Observer { sampleMangementPocess ->

            if (sampleMangementPocess?.status == Status.SUCCESS) {
                //L.d(sampleMangementPocess.toString())
                Timber.d(sampleMangementPocess.toString())
            } else if (sampleMangementPocess?.status == Status.ERROR) {
            }
        })

        viewModel.sampleMangementPocessLocal?.observe(this, Observer { sampleMangementPocess ->

            if (sampleMangementPocess?.status == Status.SUCCESS) {

                if (!isNetworkAvailable()) {
                    //L.d(sampleMangementPocess.data.toString())
                    jobManager.addJobInBackground(
                        SyncSampledProcessJob(
                            sampleProcess = sampleMangementPocess.data!!,
                            sampleRequest = sampleRequest
                        )
                    )
                    val completedDialogFragment = CompletedDialogFragment()
                    completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                    completedDialogFragment.show(fragmentManager!!)
                } else {
                    //L.d("else")
                    viewModel.setSync(hb1Ac, fastingBloodGlucose, lipidProfileAllDto, hOGTT, hemoglobin, sampleRequest)
                }

            } else if (sampleMangementPocess?.status == Status.ERROR) {
                //Crashlytics.logException(Exception(sampleMangementPocess.message?.message))
                //var error = accessToken.dat
            }
        })


        fun isValied(): Boolean {
            return if (totalCholesterol != null)
                true else {
                false
            }

        }


        binding.buttonSubmit.singleClick {

            val endTime: String = convertTimeTo24Hours()
            val endDate: String = getDate()
            val endDateTime:String = endDate + " " + endTime

            if (isValied() && (fastingBloodGlucose != null || hb1Ac != null || hOGTT != null)) {
                // if (!isNetworkAvailable()) {
                sampleRequest?.meta?.endTime = endDateTime
                lipidProfileAllDto = LipidProfileAllDto(
                    totalCholesterol = totalCholesterol,
                    triglycerol = triglyceridesDto,
                    hdl = hDL
                )
                viewModel.setSyncLocal(hb1Ac, fastingBloodGlucose, lipidProfileAllDto, hOGTT, hemoglobin, sampleRequest, true)
                // }
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }
            else
            {
                binding.sampleValidationError = true

                if (fastingBloodGlucose == null) {
                    updateProcessErrorUI(binding.fbgTextView)
                }

                if (!isValied()) {
                    updateProcessErrorUI(binding.TCTextView)
                }
            }

        }

        binding.linearLayoutBlood.singleClick {
           // viewModel.sampleValidationError.value = false
            binding.sampleValidationError = false
            //updateProcessValidUI(binding.fbgTextView)
            navController().navigate(R.id.action_sampleMangementHomeViewModel_to_FastingBloodGlucoseFragment)
        }

        binding.LinearLayoutTC.singleClick {
            //viewModel.sampleValidationError.value = false
            binding.sampleValidationError = false
            //updateProcessValidUI(binding.hb1AcTextView)
            navController().navigate(R.id.action_sampleMangementHomeViewModel_to_TotalCholesterolFragment)
        }
    }


    private fun updateProcessErrorUI(view: TextView) {
        view.setTextColor(Color.parseColor("#FF5E45"))
        view.setDrawbleLeftColor("#FF5E45")
    }

    private fun updateProcessValidUI(view: TextView) {
        view.setTextColor(Color.parseColor("#00548F"))
        view.setDrawbleLeftColor("#00548F")
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    @Subscribe
    fun onFastingBloodGlucoseDto(event: FastingBloodGlucoseDto) {
        Log.d("onFastingBloodo", event.toString())
        binding.linearLayoutFastingBloodGlucose.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
