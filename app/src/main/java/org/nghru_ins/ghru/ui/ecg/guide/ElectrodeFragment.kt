package org.nghru_ins.ghru.ui.ecg.guide


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_ins.ghru.R
import org.nghru_ins.ghru.binding.FragmentDataBindingComponent
import org.nghru_ins.ghru.databinding.ElectroFragmentBinding
import org.nghru_ins.ghru.di.Injectable
import org.nghru_ins.ghru.util.autoCleared
import javax.inject.Inject

class ElectrodeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ElectroFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var verifyIDViewModel: GuideViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ElectroFragmentBinding>(
            inflater,
            R.layout.electro_fragment,
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