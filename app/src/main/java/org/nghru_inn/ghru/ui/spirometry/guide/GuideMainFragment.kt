package org.nghru_inn.ghru.ui.spirometry.guide

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_inn.ghru.R
import org.nghru_inn.ghru.binding.FragmentDataBindingComponent
import org.nghru_inn.ghru.databinding.SpirometryGuideMainFragmentBinding
import org.nghru_inn.ghru.db.MemberTypeConverters
import org.nghru_inn.ghru.di.Injectable
import org.nghru_inn.ghru.util.*
import org.nghru_inn.ghru.vo.ParticipantListItem
import org.nghru_inn.ghru.vo.Status
import org.nghru_inn.ghru.vo.request.ParticipantRequest
import java.util.*
import javax.inject.Inject

class GuideMainFragment : Fragment(), Injectable {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<SpirometryGuideMainFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var guideMainViewModel: GuideMainViewModel

    private var participantRequest: ParticipantRequest? = null

    //    ----------------------------------------------------------------------------

    var prefs : SharedPreferences? = null

    private var selectedParticipant: ParticipantListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<SpirometryGuideMainFragmentBinding>(
            inflater,
            R.layout.spirometry_guide_main_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.expandProcedure = false
        binding.linearLayoutPrepContainer.collapse()
        binding.root.hideKeyboard()

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = guideMainViewModel
        binding.participant = participantRequest

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        selectedParticipant = MemberTypeConverters.gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("GUIDE_MAIN_FRAGMENT", " DATA: " + selectedParticipant!!.participant_id)

        binding.titleName.setText(selectedParticipant!!.firstname + " " + selectedParticipant!!.last_name)
        binding.titleGender.setText(selectedParticipant!!.gender)
        binding.titleParticipantId.setText(selectedParticipant!!.participant_id)

        guideMainViewModel.setScreeningId(selectedParticipant!!.participant_id!!)

        guideMainViewModel.getParticipant.observe(this, androidx.lifecycle.Observer {participantResource->

            if (participantResource?.status == Status.SUCCESS) {
                participantRequest = participantResource.data?.data

                Log.d("GUIDE_MAIN_FRAGMENT", "PAR_REQ_SUCCESS")

            } else if (participantResource?.status == Status.ERROR) {

                Log.d("GUIDE_MAIN_FRAGMENT", "PAR_REQ_FAILED")
            }
            binding.executePendingBindings()
        })

        val dob_year: String = selectedParticipant!!.dob!!.substring(0,4)
        val dob_month: String = selectedParticipant!!.dob!!.substring(5,7)
        val dob_date : String = selectedParticipant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())
        binding.titleAge.setText(participantAge + "Y")

        binding.vidoeView.setVideoPath("android.resource://" + activity?.getPackageName() + "/" + R.raw.nuvoair)
//        mediaController = new MediaController(TestActivity.this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
//        videoView.start();
        var mediaController = MediaController(activity)
        mediaController.setAnchorView(binding.vidoeView);
        binding.vidoeView.setMediaController(mediaController);
        binding.vidoeView.singleClick {
            binding.vidoeView.start();
        }
        binding.previousButton.setOnClickListener {
            navController().popBackStack()
        }

        binding.nextButton.singleClick {

            if (validateNextButton()) {
                navController().navigate(
                    R.id.action_guideMainFragment_to_TestsFragment,
                    bundleOf("participant" to participantRequest)
                )
            }
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->

            guideMainViewModel.setHasExplained(isChecked)
            validateNextButton()
        }


        binding.prepEC.setOnClickListener {
            if (binding.expandProcedure!!) {

                //collapse(binding.linearLayoutEcContainer)
                binding.linearLayoutPrepContainer.collapse()
                binding.expandProcedure = false

            } else {
                //itexpand()
                binding.linearLayoutPrepContainer.expand()
                binding.expandProcedure = true
            }
            binding.executePendingBindings()
        }

    }

    private fun validateNextButton(): Boolean {

        if (guideMainViewModel.hasExplained.value != null && guideMainViewModel.hasExplained.value!!) {
            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check, null)
            binding.textViewError.visibility = View.GONE
            return true
        } else {

            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check_error, null)
            binding.textViewError.visibility = View.VISIBLE
            return false

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
