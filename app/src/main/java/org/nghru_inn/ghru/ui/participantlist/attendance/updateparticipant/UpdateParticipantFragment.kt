package org.nghru_inn.ghru.ui.participantlist.attendance.updateparticipant

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.nghru_inn.ghru.R
import org.nghru_inn.ghru.binding.FragmentDataBindingComponent
import org.nghru_inn.ghru.databinding.UpdateParticipantFragmentBinding
import org.nghru_inn.ghru.db.MemberTypeConverters.gson
import org.nghru_inn.ghru.di.Injectable
import org.nghru_inn.ghru.jobs.SyncParticipantListItemJob
import org.nghru_inn.ghru.ui.participantlist.preocessenddialog.NotAbleDialogFragment
import org.nghru_inn.ghru.util.*
import org.nghru_inn.ghru.vo.*
import org.nghru_inn.ghru.vo.Date
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UpdateParticipantFragment : Fragment(), Injectable {

    val TAG = UpdateParticipantFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<UpdateParticipantFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var updateParticipantViewModule: UpdateParticipantViewModule

    private var participant: ParticipantListItem? = null

    var prefs : SharedPreferences? = null

    val sdf = SimpleDateFormat(Constants.dataFormatOLD, Locale.US)

    var cal = Calendar.getInstance()

    var selectedGender : String? = null

    var isGenderSelected : Boolean = false

    @Inject
    lateinit var jobManager: JobManager

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
        val dataBinding = DataBindingUtil.inflate<UpdateParticipantFragmentBinding>(
            inflater,
            R.layout.update_participant_fragment,
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
        binding.participant = participant

        val dob_year: String = participant!!.dob!!.substring(0,4)
        val dob_month: String = participant!!.dob!!.substring(5,7)
        val dob_date : String = participant!!.dob!!.substring(8,10)

        val participantAge: String = getAge(dob_year.toInt(), dob_month.toInt(), dob_date.toInt())

        Log.d("PARTICIPANT_ATTENDANCE", "year: " + dob_year + ", month: " + dob_month + ", date: " + dob_date + "AGE: " + participantAge)


        binding.participantDetails.setText(participant!!.firstname + " " + participant!!.last_name+ ", " + participant!!.gender + ", " + participantAge + " Years, "  + participant!!.participant_id)

        updateParticipantViewModule.setUser("user")
        updateParticipantViewModule.user?.observe(this, Observer { userData ->
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
            updateParticipantViewModule.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date = Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            updateParticipantViewModule.birthDate.postValue(sdf.format(cal.time))
            updateParticipantViewModule.birthDateVal.postValue(birthDate)

            binding.dobEditText.setText(sdf.format(cal.time))
            //binding.dobEditText.isFocusableInTouchMode = true

            val years = UserConfig.getAge(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )  //Calendar.getInstance().get(Calendar.YEAR) - year

            updateParticipantViewModule.age.value = years
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
        }


        val resheduleListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            updateParticipantViewModule.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val birthDate: Date = Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            updateParticipantViewModule.birthDate.postValue(sdf.format(cal.time))
            updateParticipantViewModule.birthDateVal.postValue(birthDate)

            binding.dobEditText.setText(sdf.format(cal.time))
            binding.dobEditText.setError(null)
            //binding.rescheduleEditText.isFocusableInTouchMode = true

            val years = UserConfig.getAge(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )  //Calendar.getInstance().get(Calendar.YEAR) - year

            updateParticipantViewModule.age.value = years
            binding.executePendingBindings()
        }

        binding.dobEditText.singleClick {
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
        }

        updateParticipantViewModule.setParticipant(participant!!, participant!!.participant_id)

        binding.buttonMale.singleClick {

            selectedGender = "male"
            isGenderSelected = true
            binding.buttonFemale.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonGenderOther.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonMale.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.radio_button_selected_bg))
        }

        binding.buttonFemale.singleClick {

            selectedGender = "female"
            isGenderSelected = true
            binding.buttonMale.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonGenderOther.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonFemale.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.radio_button_selected_bg))
        }

        binding.buttonGenderOther.singleClick {

            selectedGender = "other"
            isGenderSelected = true
            binding.buttonFemale.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonMale.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.shape_rounded_corners_4dp))
            binding.buttonGenderOther.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.radio_button_selected_bg))
        }

        binding.buttonUpdate.singleClick {

            if (validateUpdate())
            {
                participant!!.firstname = binding.firstNameEditText.text.toString()
                participant!!.last_name = binding.lastNameEditText.text.toString()
                participant!!.address!!.street = binding.streetEditText.text.toString()
                participant!!.address!!.locality = binding.localityEditText.text.toString()
                participant!!.address!!.postcode = binding.postCodeEditText.text.toString()
                participant!!.address!!.country = binding.countryEditText.text.toString()
                participant!!.dob = binding.dobEditText.text.toString()
                participant!!.nid = binding.nidEditText.text.toString()
                //participant!!.inablitiy_reason = "Participant not present"

                if (isGenderSelected)
                {
                    participant!!.gender = selectedGender
                }
                else
                {
                    participant!!.gender = participant!!.gender
                }
                participant!!.phone = binding.phoneEditText.text.toString()


                //updateParticipantViewModule.updateParticipant(participant!!)
                if (isNetworkAvailable())
                {
                    participant?.isSync =false
                    updateParticipantViewModule.updateParticipant(participant!!)
                }
                else
                {
                    participant?.isSync =true
                    jobManager.addJobInBackground(SyncParticipantListItemJob(participant!!))
                    val notAbleDialogFragment = NotAbleDialogFragment()
                    notAbleDialogFragment.show(fragmentManager!!)
                }
                Log.d("UPDATE_PARTICIPANT", "VALIDATION SUCCESS")
            }
            else
            {
                Log.d("UPDATE_PARTICIPANT", "VALIDATION FAILED")
            }

//            onTextChanges(binding.firstNameEditText)
//            onTextChanges(binding.lastNameEditText)
//            onTextChanges(binding.streetEditText)
//            onTextChanges(binding.localityEditText)
//            onTextChanges(binding.dobEditText)
//            onTextChanges(binding.nidEditText)
        }

        // update api call

        updateParticipantViewModule.participantUpdateComplete?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data)
                if (assertsResource.data != null) {
                val notAbleDialogFragment = NotAbleDialogFragment()
                    notAbleDialogFragment.show(fragmentManager!!)

                    //Toast.makeText(activity!!, "Update Participant success", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "UPDATE_PARTICIPANT" + assertsResource.message.toString())

                } else {
                    Log.d(TAG, "UPDATE_PARTICIPANT" + assertsResource.message.toString())
                    Toast.makeText(activity, "Unable to update the participant via " + assertsResource.message.toString(), Toast.LENGTH_LONG).show()
                    Crashlytics.logException(Exception("Participant Update " + assertsResource.message.toString()))
                    binding.executePendingBindings()
                }
            }
        })

    }

    private fun validateUpdate(): Boolean
    {
        if (binding.firstNameEditText.length()==0)
        {
            binding.firstNameEditText.setError("Please enter first name")
            return false
        }
        else if (binding.lastNameEditText.length() == 0)
        {
            binding.lastNameEditText.setError("Please enter last name")
            return false
        }
        else if (binding.streetEditText.length() == 0)
        {
            binding.streetEditText.setError("Please enter street")
            return false
        }
        else if (binding.localityEditText.length() == 0)
        {
            binding.localityEditText.setError("Please enter locality")
            return false
        }
        else if (binding.dobEditText.length() == 0)
        {
            binding.dobEditText.setError("Please enter valid dob")
            return false
        }
        else if (binding.nidEditText.length() == 0)
        {
            binding.nidEditText.setError("Please enter NID")
            return false
        }
//        else if (binding.genderEditText.length() == 0)
//        {
//            binding.genderEditText.setError("Please enter Gender")
//            return false
//        }
        else if (binding.phoneEditText.length() == 0)
        {
            binding.phoneEditText.setError("Please enter Phone number")
            return false
        }
        else
        {
            return true
        }
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                if (editText.length()== 0)
                {
                    editText.setError("Please enter valid data")
                }
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





