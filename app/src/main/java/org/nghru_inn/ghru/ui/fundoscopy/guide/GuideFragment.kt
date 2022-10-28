package org.nghru_inn.ghru.ui.fundoscopy.guide


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_inn.ghru.AppExecutors
import org.nghru_inn.ghru.R
import org.nghru_inn.ghru.binding.FragmentDataBindingComponent
import org.nghru_inn.ghru.databinding.GuideFragmentBinding
import org.nghru_inn.ghru.di.Injectable
import org.nghru_inn.ghru.util.LocaleManager
import org.nghru_inn.ghru.util.autoCleared
import javax.inject.Inject

class GuideFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager


    var binding by autoCleared<GuideFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var verifyIDViewModel: GuideViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<GuideFragmentBinding>(
            inflater,
            R.layout.guide_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
