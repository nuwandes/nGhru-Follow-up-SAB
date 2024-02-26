package org.nghru_bd.ghru.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator
import com.crashlytics.android.Crashlytics
import org.nghru_bd.ghru.BuildConfig
import org.nghru_bd.ghru.MainActivity
import org.nghru_bd.ghru.R
import org.nghru_bd.ghru.binding.FragmentDataBindingComponent
import org.nghru_bd.ghru.databinding.LoginFragmentBinding
import org.nghru_bd.ghru.db.AccessTokenDao
import org.nghru_bd.ghru.di.Injectable
import org.nghru_bd.ghru.ui.common.RetryCallback
import org.nghru_bd.ghru.util.TokenManager
import org.nghru_bd.ghru.util.autoCleared
import org.nghru_bd.ghru.util.hideKeyboard
import org.nghru_bd.ghru.util.singleClick
import org.nghru_bd.ghru.vo.ParticipantListItem
import org.nghru_bd.ghru.vo.Status
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LoginFragment : Fragment(), Injectable, EasyPermissions.PermissionCallbacks {
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    private val RC_SMS_PERM = 122

    private val LOCATION_AND_CAMERA: Array<String> =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<LoginFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private val registry = LifecycleRegistry(this)

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var validator: Validator

    @Inject
    lateinit var accessTokenDao: AccessTokenDao

    var prefs : SharedPreferences? = null

    var dateFormat : String = "yyyy-MM-dd hh:mm"

    private var siteNames: MutableList<String> = arrayListOf()

    private var participantListObject: List<ParticipantListItem> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<LoginFragmentBinding>(
            inflater,
            R.layout.login_fragment,
            container,
            false
        )

        binding = dataBinding
        validator = Validator(binding)
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                binding.userResource = null
                loginViewModel.onError()
            }
        }
        binding.textViewVesion.text = getApplicationVersionName()

        return dataBinding.root
    }

    private fun getApplicationVersionName(): String {

        try {
            val packageInfo = activity?.getPackageManager()?.getPackageInfo(activity?.getPackageName(), 0)
            return packageInfo?.versionName!!
        } catch (ignored: Exception) {
        }

        return ""
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        binding.linearLayout.visibility = View.INVISIBLE
        binding.linearLayout2.visibility = View.INVISIBLE
        binding.buttonLogin.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE

        loginViewModel.accessTokenOffline?.observe(this, Observer { accessToken ->


            if (accessToken?.status == Status.SUCCESS) {
                if (accessToken.data != null) {
                    if (accessToken.data.status) {
                        if (!isLoginClick) {
                            loadMainActivityNew()
                        }

                    } else {
                        binding.linearLayout.visibility = View.VISIBLE
                        binding.linearLayout2.visibility = View.VISIBLE
                        binding.buttonLogin.visibility = View.VISIBLE
                    }
                }
            }
            else if (accessToken?.status == Status.ERROR){
//                binding.linearLayout.visibility = View.VISIBLE
//                binding.linearLayout2.visibility = View.VISIBLE
//                binding.buttonLogin.visibility = View.VISIBLE
//                binding.progressBar.visibility = View.GONE
            }
        })

        if (tokenManager.getEmail() != null) {
            loginViewModel.setEmail(tokenManager.getEmail()!!)
        } else {
            binding.linearLayout.visibility = View.VISIBLE
            binding.linearLayout2.visibility = View.VISIBLE
            binding.buttonLogin.visibility = View.VISIBLE
        }



        if (BuildConfig.DEBUG) {

//            binding.textInputEditTextEmail.setText("roshan@well.tech")
//            binding.textInputEditTextPassword.setText("Qwerty123#")

//            binding.textInputEditTextEmail.setText("stagingbnqa@nghru.org")
//            binding.textInputEditTextPassword.setText("Asdfgh123#")

        }

        loginViewModel.accessToken?.observe(this, Observer { accessToken ->

            binding.userResource = accessToken
            if (accessToken?.status == Status.SUCCESS ) {
                //println(user)
                if(accessToken.data!=null) {
                    val token = accessToken.data!!
                    //tokenManager.saveToken(token)
                    tokenManager.saveEmail(binding.textInputEditTextEmail.text.toString())
                    binding.textViewError.visibility = View.INVISIBLE
                    token.status = true
                    //accessTokenDao.login(token)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.linearLayout.visibility = View.INVISIBLE
                    binding.linearLayout2.visibility = View.INVISIBLE
                    binding.buttonLogin.visibility = View.INVISIBLE

                    loginViewModel.setStationDevice("GET")

                }else
                {
                    Timber.d(getString(R.string.user_not_found))

                    binding.textViewError.visibility = View.VISIBLE
                    binding.textViewError.setText(getString(R.string.user_not_found))
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.linearLayout2.visibility = View.VISIBLE
                    binding.buttonLogin.visibility = View.VISIBLE
                    binding.userResource = null
                    loginViewModel.onError()
                }

            } else if (accessToken?.status == Status.ERROR) {
                Crashlytics.logException(Exception(accessToken.message?.message))
                Timber.d(accessToken.message?.message)

                binding.textViewError.visibility = View.VISIBLE
                binding.textViewError.setText(accessToken.message?.message)
                binding.linearLayout.visibility = View.VISIBLE
                binding.linearLayout2.visibility = View.VISIBLE
                binding.buttonLogin.visibility = View.VISIBLE
                binding.userResource = null
                loginViewModel.onError()
            }
        })

        loginViewModel.stationDevices?.observe(this, Observer {

            if (it?.status == Status.SUCCESS) {
                loginViewModel.setStationDeviceList(it.data?.data!!)
            }
            else if(it?.status == Status.ERROR){
                // binding.textViewError.visibility = View.VISIBLE
                // binding.textViewError.setText(it.message?.message)
                //loadMainActivity()
            }

        })

        loginViewModel.stationDeviceList?.observe(this, Observer {

            if (it?.status == Status.SUCCESS || it?.status == Status.ERROR){

                loginViewModel.setOfflineAllParticipants(page=1, status = "all", site = "all", keyWord = "")

               //loadMainActivity()
            }
        })

        binding.buttonLogin.singleClick {

            if (validator.validate())
            {
                binding.progressBar.visibility = View.VISIBLE
                val mPattern: Pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\\^&\\*])(?=.{8,})")

                val matche: Matcher = mPattern.matcher(binding.textInputEditTextPassword.text.toString())
                        binding.root.hideKeyboard()

                if(!matche.find())
                {
                    binding.textInputLayoutPassword.error = getString(R.string.passowrd_reg_error)
                    //weightEditText.setText(); // Don't know what to place
                }
                else
                {
                    if (isNetworkAvailable())
                    {
                        activity?.runOnUiThread(
                            object : Runnable {
                                override fun run() {
                                    binding.textViewError.text = ""
                                    binding.linearLayout.visibility = View.INVISIBLE
                                    binding.linearLayout2.visibility = View.INVISIBLE
                                    binding.buttonLogin.visibility = View.INVISIBLE
                                    isLoginClick = true

                                    loginViewModel.setLogin(
                                        binding.textInputEditTextEmail.text.toString(),
                                        binding.textInputEditTextPassword.text.toString(),
                                        isNetworkAvailable()
                                    )

                                }
                            }
                        )
                    }
                    else
                    {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(activity!!, "Please check your network connection", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.CAMERA
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    LOCATION_AND_CAMERA,
                    RC_SMS_PERM
                )
                ActivityCompat.requestPermissions(
                    activity!!,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        // loginViewModel.setDevices("devices")



        loginViewModel.getAllParticipantsOffline?.observe(activity!!, Observer {

            if (isNetworkAvailable())
            {
                if (it.status.equals(Status.SUCCESS))
                {

                    loginViewModel.setParticipantListItemList(it.data!!.data!!.listRequest!!)

                    // save the data in local db

                    //Toast.makeText(activity, "All participant API call Success", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                //Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }

        })

        loginViewModel.getParticipantListItemList.observe(this, Observer {

            if (it?.status == Status.SUCCESS || it?.status == Status.ERROR){

                loginViewModel.setSiteSpinnerId("Get")
                loginViewModel.setSiteSpinnerId("All")

                siteNames.clear()
                siteNames.add(getString(R.string.filter1_default))
            }
        })

        var localSites : ArrayList<String> = arrayListOf()
        var siteList : ArrayList<String>? = arrayListOf()
        localSites!!.add(getString(R.string.filter1_default))
        loginViewModel.getSiteSpinnerItems!!.observe(this, Observer {

            if (it.status.equals(Status.SUCCESS)) {

                if (!it.data!!.data!!.contains("")|| !it.data.data!!.contains("null"))
                {
                    for( siteName in it.data.data!!)
                    {
                        siteNames.add(siteName)
                    }

                    prefs?.edit()?.putString("SiteList", siteNames.toString())?.apply()

                    val sites : String? = prefs?.getString("SiteList","")

                    Log.wtf("LOGIN_FRAGMENT", "SITE_DATA: - " + sites)
                }

                loginViewModel.setSampleIds("GET")
            }
            else
            {
                Log.d("LOGIN_FRAGMENT", "Site_spinner_Error_is: " + it.status.toString())
            }
        })

        loginViewModel.getSampleIds?.observe(this, Observer {

            if (it?.status == Status.SUCCESS) {
                loginViewModel.setSampleIdList(it.data?.data!!)
            }
        })

        loginViewModel.getSampleIdList?.observe(this, Observer {

            if (it?.status == Status.SUCCESS ){

                //Toast.makeText(activity, "SampleId API call Success", Toast.LENGTH_LONG).show()

                loginViewModel.setFreezerIds("GET")

                //loadMainActivity(siteNames)
            }
            else
            {
                //Toast.makeText(activity, "Storage Id API call Failed", Toast.LENGTH_LONG).show()
            }
        })

        loginViewModel.getFreezerIds?.observe(this, Observer {

            if (it?.status == Status.SUCCESS) {
                loginViewModel.setFreezerIdList(it.data?.data!!)
            }
        })

        loginViewModel.getFreezerIdList?.observe(this, Observer {

            if (it?.status == Status.SUCCESS ){

                //Toast.makeText(activity, "FreezerId API call Success", Toast.LENGTH_LONG).show()

                loginViewModel.setStorageIds("GET")
            }
            else
            {
                //Toast.makeText(activity, "Storage Id API call Failed", Toast.LENGTH_LONG).show()
            }
        })

        loginViewModel.getStorageIds?.observe(this, Observer {

            if (it?.status == Status.SUCCESS) {
                loginViewModel.setStorageIdList(it.data?.data!!)
            }
        })


        loginViewModel.getStorageIdList?.observe(this, Observer {
            if (it?.status == Status.SUCCESS ){

                //Toast.makeText(activity, "Storage Id API call Success", Toast.LENGTH_LONG).show()

                loadMainActivity(siteNames)
            }
            else
            {
                //Toast.makeText(activity, "Storage Id API call Failed", Toast.LENGTH_LONG).show()
            }
        })
    }

    var isLoginClick: Boolean = false

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun getLocalTimeString(): String {
        val s = SimpleDateFormat(dateFormat, Locale.US)
        return s.format(Date())
    }
    fun  loadMainActivity(list : MutableList<String>)
    {
        binding.progressBar.visibility = View.GONE
        prefs?.edit()?.putBoolean("isTimeOut", false)?.apply()
        prefs?.edit()?.putString("loginDateTime", getLocalTimeString())?.apply()
        prefs?.edit()?.putString("dateTime", getLocalTimeString())?.apply()

        val intent = Intent(activity, MainActivity::class.java)
        intent.putStringArrayListExtra("SITE_ARRAY", ArrayList(list))
        startActivity(intent)
        activity!!.finish()
    }

    fun  loadMainActivityNew()
    {
        binding.progressBar.visibility = View.GONE
        prefs?.edit()?.putBoolean("isTimeOut", false)?.apply()
        prefs?.edit()?.putString("loginDateTime", getLocalTimeString())?.apply()
        prefs?.edit()?.putString("dateTime", getLocalTimeString())?.apply()

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }
}
