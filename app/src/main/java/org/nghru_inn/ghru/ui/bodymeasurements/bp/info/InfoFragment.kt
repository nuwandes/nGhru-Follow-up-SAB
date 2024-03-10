package org.nghru_inn.ghru.ui.bodymeasurements.bp.info


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
import org.nghru_inn.ghru.databinding.InfoFragmentBinding
import org.nghru_inn.ghru.di.Injectable
import org.nghru_inn.ghru.util.autoCleared
import javax.inject.Inject

class InfoFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<InfoFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<InfoFragmentBinding>(
            inflater,
            R.layout.info_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        return dataBinding.root
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
