package org.nghru_ins.ghru.ui.usersetting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pixplicity.easyprefs.library.Prefs
import org.nghru_ins.ghru.AppExecutors
import org.nghru_ins.ghru.BuildConfig
import org.nghru_ins.ghru.R
import org.nghru_ins.ghru.binding.FragmentDataBindingComponent
import org.nghru_ins.ghru.databinding.UserSettingFragmentBinding
import org.nghru_ins.ghru.di.Injectable
import org.nghru_ins.ghru.util.LocaleManager
import org.nghru_ins.ghru.util.autoCleared
import org.nghru_ins.ghru.util.singleClick
import javax.inject.Inject


class UserSettingFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager


    var binding by autoCleared<UserSettingFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var usersettingViewModel: UserSettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding = DataBindingUtil.inflate<UserSettingFragmentBinding>(
            inflater,
            R.layout.user_setting_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.textInputEditTextLocalIp.setText(Prefs.getString("Ipaddress", BuildConfig.SERVER_URL))
        binding.buttonComplete.singleClick {
            var Ipaddress: String = binding.textInputEditTextLocalIp.text.toString()
            //  Prefs.putString("Ipaddress", Ipaddress)
        }
        binding.textViewVesion.text = getApplicationVersionName()
    }

    private fun getApplicationVersionName(): String {

        try {
            val packageInfo = activity?.getPackageManager()?.getPackageInfo(activity?.getPackageName(), 0)
            return packageInfo?.versionName!!
        } catch (ignored: Exception) {
        }

        return ""
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
