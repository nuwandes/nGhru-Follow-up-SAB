package org.southasia.ghrufollowup_sab.ui.participantlist.attendance

import android.app.DatePickerDialog
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
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import org.southasia.ghrufollowup_sab.MeasurementListActivity
import org.southasia.ghrufollowup_sab.R
import org.southasia.ghrufollowup_sab.binding.FragmentDataBindingComponent
import org.southasia.ghrufollowup_sab.databinding.BasicDetailsFragmentNewBinding
import org.southasia.ghrufollowup_sab.databinding.BasicDetailsFragmentSgBinding
import org.southasia.ghrufollowup_sab.databinding.ParticipantAttendanceFragmentBinding
import org.southasia.ghrufollowup_sab.db.MemberTypeConverters.gson
import org.southasia.ghrufollowup_sab.di.Injectable
import org.southasia.ghrufollowup_sab.ui.participantlist.attendance.consent.completed.ConsentCompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.participantlist.preocessenddialog.NotAbleDialogFragment
import org.southasia.ghrufollowup_sab.ui.participantlist.verificationcompleted.VerificationCompletedDialogFragment
import org.southasia.ghrufollowup_sab.util.*
import org.southasia.ghrufollowup_sab.vo.*
import org.southasia.ghrufollowup_sab.vo.Date
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ParticipantAttendanceFragment : Fragment(), Injectable {

    val TAG = ParticipantAttendanceFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ParticipantAttendanceFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var basicDetailsViewModelNew: ParticipantAttendanceViewModule

//    private var participant: ParticipantListItem? = null

    private var participant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    private var isVerified : Boolean? = false

    private var isNotVerifiedAnyButtonClicked : Boolean? = false

    private var inabilityReason: String? = null

    val sdf = SimpleDateFormat(Constants.dataFormatOLD, Locale.US)

    var cal = Calendar.getInstance()

    private var isConsentExist : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
//            hoursFasted = "8"
            //participant = arguments!!.getBundle("single_participant")
        } catch (e: KotlinNullPointerException) {
            print(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ParticipantAttendanceFragmentBinding>(
            inflater,
            R.layout.participant_attendance_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
//        validator = Validator(binding)
        val appCompatActivity = requireActivity() as AppCompatActivity
//        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val json : String? = prefs?.getString("single_participant","")
        participant = gson.fromJson<ParticipantListItem>(json.toString())
        Log.d("PARTICIPANT_ATTENDANCE", " DATA: " + participant!!.participant_id)

        val dob_year: String = participant!!.dob!!.substring(0,4)
        val dob_month: String = participant!!.dob!!.substring(5,7)
        val dob_date : String = participant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())

        Log.d("PARTICIPANT_ATTENDANCE", "year: " + dob_year + ", month: " + dob_month + ", date: " + dob_date + "AGE: " + participantAge)


        binding.participantDetails.setText(participant!!.firstname + " " + participant!!.last_name+ ", " + participant!!.gender + ", " + participantAge + " Years, "  + participant!!.participant_id)

        basicDetailsViewModelNew.setParticipantForGetAsset(participant!!.participant_id!!)

        basicDetailsViewModelNew.getAssets?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data?.data)
                if (assertsResource.data?.data?.size != 0)
                {
                    Log.d("ATTENDANCE_FRAG", "ASSET EXISTS")
                    participant?.isConsent = true

                }
                else
                {
                    Log.d("ATTENDANCE_FRAG", "ASSET NOT EXISTS")
                    participant?.isConsent = false
                }
            }
            else
            {
                Log.d("ATTENDANCE_FRAG", "GET ASSET REQUEST FAILED")
            }
        })

        binding.buttonAbleYes.singleClick {
            //binding.buttonAbleYes.setTextColor(R.color.black)
            binding.buttonAbleYes.setBackground(getDrawable(context!!, R.drawable.border_rounded_corner_with_blue_color))
            binding.attendanceYes.visibility = View.VISIBLE
            binding.attendanceNo.visibility = View.GONE
            //binding.buttonAbleNo.setTextColor(R.color.black)
            binding.buttonAbleNo.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))

            participant!!.is_able = true
            participant!!.is_rescheduled = false
            participant!!.rescheduled_date = null
            participant!!.inablitiy_reason = null
        }

        binding.buttonAbleNo.singleClick {
            //binding.buttonAbleNo.setTextColor(R.color.black)
            binding.buttonAbleNo.setBackground(getDrawable(context!!, R.drawable.border_rounded_corner_with_blue_color))
            binding.attendanceNo.visibility = View.VISIBLE
            binding.attendanceYes.visibility = View.GONE
//            binding.buttonAbleYes.setTextColor(R.color.black)
            binding.buttonAbleYes.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))

            participant!!.is_able = false
            participant!!.is_verified = false
            participant!!.verification_dob = null
            participant!!.verification_id = null

        }

        binding.nidEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == (participant!!.nid.toString())) {
                    binding.nidEditText.setError(null)
//                    binding.buttonProceed.isEnabled = true
                    binding.nidOkImage.visibility = View.VISIBLE
                    binding.nidNotOkImage.visibility = View.GONE
                    isVerified = true
                }
                else if(s.length == 0)
                {
                    binding.nidEditText.setError(null)
//                    binding.buttonProceed.isEnabled = false
                    binding.nidOkImage.visibility = View.GONE
                    binding.nidNotOkImage.visibility = View.GONE
                    isVerified = false
                }
                else {
                    binding.nidEditText.setError("Entered NID not matched")
//                    binding.buttonProceed.isEnabled = false
                    binding.nidOkImage.visibility = View.GONE
                    binding.nidNotOkImage.visibility = View.VISIBLE
                    isVerified = false
                }
            }
        })

        binding.buttonUpdate.singleClick {

            findNavController().navigate(R.id.action_attendanceFragment_to_UpdateFragment)
        }

        binding.dobEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == (participant!!.dob.toString()))
                {
                    binding.dobEditText.setError(null)
                    //binding.buttonProceed.isEnabled = true
                    binding.dobOkImage.visibility = View.VISIBLE
                    binding.dobNotOkImage.visibility = View.GONE
                    isVerified = true
                }
                else if(s.length == 0)
                {
                    binding.dobEditText.setError(null)
                    //binding.buttonProceed.isEnabled = false
                    binding.dobOkImage.visibility = View.GONE
                    binding.dobNotOkImage.visibility = View.GONE
                    isVerified = false
                }
                else
                {
                    binding.dobEditText.setError("Entered DOB not matched")
                    //binding.buttonProceed.isEnabled = false
                    binding.dobOkImage.visibility = View.GONE
                    binding.dobNotOkImage.visibility = View.VISIBLE
                    isVerified = false
                }
            }
        })

        binding.rescheduleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().length >= 1)
                {
                    binding.rescheduleEditText.setError(null)
                    participant!!.rescheduled_date = binding.rescheduleEditText.text.toString()
                    inabilityReason = "Reschedule"
                    isNotVerifiedAnyButtonClicked = true
                    participant!!.is_rescheduled = true
                }
                else
                {
                    binding.rescheduleEditText.setError("Please enter DD/MM/YY")
                    //Toast.makeText(activity!!, "Input does not have correct length", Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.buttonProceed.singleClick {

            if (isVerified!!)
            {
                participant!!.is_verified = true

                if (binding.rescheduleEditText.text.toString().length>=8)
                {
                    participant!!.verification_dob = binding.dobEditText.text.toString()
                }
                else
                {
                    participant!!.verification_dob = null
                }

                participant!!.verification_dob = binding.dobEditText.text.toString()
                participant!!.verification_id = binding.nidEditText.text.toString()

                Log.d(TAG, "PROCEED_BUTTON IS_ABLE: " + participant!!.is_able
                        + ", IS_VERIFIED: " + participant!!.is_verified
                        + ", IS_RESCHEDULE: " + participant!!.is_rescheduled
                        + ", REASON: " + participant!!.inablitiy_reason
                        + ", VERFI_DOB: " + participant!!.verification_dob
                        + ", VERFI_ID: " + participant!!.verification_id
                        + ", RESCH_DATE: " +participant!!.rescheduled_date)

                if (participant?.isConsent!!)
                {
                    val json1: String = gson.toJson(participant)
                    prefs?.edit()?.putString("single_participant", json1)?.apply()
                    val intent = Intent(activity, MeasurementListActivity::class.java)
                    intent.putExtra("CONSENT_STATUS", true)
                    startActivity(intent)
                }
                else
                {
                    //findNavController().navigate(R.id.action_attendanceFragment_to_ConsentFragment, bundleOf("single_participant" to participant!!))
                    val consentCompletedDialogFragment = ConsentCompletedDialogFragment()
                    consentCompletedDialogFragment.arguments = bundleOf("single_participant" to participant!!)
                    consentCompletedDialogFragment.show(fragmentManager!!)
                }
            }
            else
            {
                if (binding.nidEditText.length() >=1 || binding.dobEditText.length() >= 1)
                {
                    participant!!.is_verified = false
                    participant!!.verification_dob = binding.dobEditText.text.toString()
                    participant!!.verification_id = binding.nidEditText.text.toString()

                    val verificationCompletedDialogFragment = VerificationCompletedDialogFragment()
//                    verificationCompletedDialogFragment.arguments = bundleOf("single_participant" to participant!!, "isConsentExist" to isConsentExist)
                    verificationCompletedDialogFragment.arguments = bundleOf("single_participant" to participant!!)
                    verificationCompletedDialogFragment.show(fragmentManager!!)

                    Log.d(TAG, "PROCEED_BUTTON IS_ABLE: " + participant!!.is_able
                            + ", IS_VERIFIED: " + participant!!.is_verified
                            + ", IS_RESCHEDULE: " + participant!!.is_rescheduled
                            + ", REASON: " + participant!!.inablitiy_reason
                            + ", VERFI_DOB: " + participant!!.verification_dob
                            + ", VERFI_ID: " + participant!!.verification_id
                            + ", RESCH_DATE: " +participant!!.rescheduled_date)
                }
                else
                {
                    Toast.makeText(activity!!, "Please type in participant verification", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.buttonEnd.singleClick {

            if (isNotVerifiedAnyButtonClicked == true)
            {
                participant!!.inablitiy_reason = inabilityReason
                participant!!.status = "unable_to_complete"

                if (binding.rescheduleEditText.text.toString().length>=8)
                {
                    participant!!.rescheduled_date = binding.rescheduleEditText.text.toString()
                }
                else
                {
                    participant!!.rescheduled_date = null
                }


                // update api call

                basicDetailsViewModelNew.updateParticipant(participant!!, participant!!.participant_id)


                Log.d(TAG, "END_BUTTON IS_ABLE: " + participant!!.is_able
                        + ", IS_VERIFIED: " + participant!!.is_verified
                        + ", IS_RESCHEDULE: " + participant!!.is_rescheduled
                        + ", REASON: " + participant!!.inablitiy_reason
                        + ", VERFI_DOB: " + participant!!.verification_dob
                        + ", VERFI_ID: " + participant!!.verification_id
                        + ", RESCH_DATE: " +participant!!.rescheduled_date)

                basicDetailsViewModelNew.participantUpdateComplete?.observe(this, Observer { assertsResource ->
                    if (assertsResource?.status == Status.SUCCESS) {
                        println(assertsResource.data?.data)
                        if (assertsResource.data != null) {
                        val notAbleDialogFragment = NotAbleDialogFragment()
                            notAbleDialogFragment.show(fragmentManager!!)

                            //Toast.makeText(activity!!, "Update Participant success", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "END_BUTTON_SUCCESS" + assertsResource.message.toString())

                        } else {
                            Log.d(TAG, "END_BUTTON_FAILED" + assertsResource.message.toString())
                            Toast.makeText(activity, "Unable to update the participant via " + assertsResource.message.toString(), Toast.LENGTH_LONG).show()
                            Crashlytics.logException(Exception("Participant Update " + assertsResource.message.toString()))
                            binding.executePendingBindings()
                        }
                    }
                })

            }
            else
            {
                Toast.makeText(activity!!, "Please select a reason", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonParticipantNotPresent.singleClick {

            inabilityReason = "Participant not present"
            binding.buttonParticipantNotPresent.setTextColor(R.color.black)
            binding.buttonParticipantNotPresent.setBackground(getDrawable(context!!, R.drawable.ic_button_fill_primary))
            binding.buttonParticipantNotConsent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantPassedAway.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotAddress.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonReschedule.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.rescheduleEditText.visibility = View.GONE
            isNotVerifiedAnyButtonClicked = true
        }

        binding.buttonParticipantNotConsent.singleClick {

            inabilityReason = "Participant does not consent"
            binding.buttonParticipantNotConsent.setTextColor(R.color.black)
            binding.buttonParticipantNotConsent.setBackground(getDrawable(context!!, R.drawable.ic_button_fill_primary))
            binding.buttonParticipantNotPresent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantPassedAway.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotAddress.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonReschedule.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.rescheduleEditText.visibility = View.GONE
            isNotVerifiedAnyButtonClicked = true
        }

        binding.buttonParticipantPassedAway.singleClick {

            inabilityReason = "Participant has passed away"
            binding.buttonParticipantPassedAway.setTextColor(R.color.black)
            binding.buttonParticipantPassedAway.setBackground(getDrawable(context!!, R.drawable.ic_button_fill_primary))
            binding.buttonParticipantNotPresent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotConsent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotAddress.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonReschedule.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.rescheduleEditText.visibility = View.GONE
            isNotVerifiedAnyButtonClicked = true
        }

        binding.buttonParticipantNotAddress.singleClick {

            inabilityReason = "No such person at address"
            binding.buttonParticipantNotAddress.setTextColor(R.color.black)
            binding.buttonParticipantNotAddress.setBackground(getDrawable(context!!, R.drawable.ic_button_fill_primary))
            binding.buttonParticipantNotPresent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotConsent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantPassedAway.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonReschedule.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.rescheduleEditText.visibility = View.GONE
            isNotVerifiedAnyButtonClicked = true
        }

        binding.buttonReschedule.singleClick {

            binding.rescheduleEditText.visibility = View.VISIBLE
            binding.buttonReschedule.setTextColor(R.color.black)
            binding.buttonReschedule.setBackground(getDrawable(context!!, R.drawable.ic_button_fill_primary))
            binding.buttonParticipantNotPresent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotConsent.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantPassedAway.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonParticipantNotAddress.setBackground(getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
        }

        basicDetailsViewModelNew.setUser("user")
        basicDetailsViewModelNew.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {
                // setupNavigationDrawer(userData.data)
//                user = userData.data

                val stTime: String = convertTimeTo24Hours()
                val stDate: String = getDate()
                val stDateTime:String = stDate + " " + stTime

//                meta = Meta(collectedBy = user?.id, startTime = stDateTime)
//                meta?.registeredBy = user?.id
            }

        })

        val dobListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            basicDetailsViewModelNew.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date = Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            basicDetailsViewModelNew.birthDate.postValue(sdf.format(cal.time))
            basicDetailsViewModelNew.birthDateVal.postValue(birthDate)

            binding.dobEditText.setText(sdf.format(cal.time))
            //binding.dobEditText.isFocusableInTouchMode = true

            val years = UserConfig.getAge(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )  //Calendar.getInstance().get(Calendar.YEAR) - year

            basicDetailsViewModelNew.age.value = years
            binding.executePendingBindings()
        }

        binding.dobEditText.singleClick {

            val datepicker = DatePickerDialog(
                activity!!, R.style.datepicker, dobListener,
                1998,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -80)
            datepicker.datePicker.minDate = calendar.timeInMillis
            datepicker.show()
            binding.root.hideKeyboard()

            //validateNextButton()
        }


        val resheduleListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            basicDetailsViewModelNew.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date = Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            basicDetailsViewModelNew.birthDate.postValue(sdf.format(cal.time))
            basicDetailsViewModelNew.birthDateVal.postValue(birthDate)

            binding.rescheduleEditText.setText(sdf.format(cal.time))
            //binding.rescheduleEditText.isFocusableInTouchMode = true

            val years = UserConfig.getAge(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )  //Calendar.getInstance().get(Calendar.YEAR) - year

            basicDetailsViewModelNew.age.value = years
            binding.executePendingBindings()
        }

        binding.rescheduleEditText.singleClick {
            var datepicker = DatePickerDialog(
                activity!!, R.style.datepicker, resheduleListener,
                1998,
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -80)
            datepicker.datePicker.minDate = calendar.timeInMillis
            datepicker.show()
            //validateNextButton()
        }

        basicDetailsViewModelNew.setParticipant(participant!!, participant!!.participant_id)

    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    // to set the 24 hours time ------------------------------ 7.2.2020 --------- Nuwan ----------

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: java.util.Date
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
        val date: java.util.Date
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

    // -------------------------------------------------------------------------------------------


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}





