package org.nghru_pk.ghru.ui.homeenumeration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.nghru_pk.ghru.EnumerationActivity
import org.nghru_pk.ghru.R
import org.nghru_pk.ghru.binding.FragmentDataBindingComponent
import org.nghru_pk.ghru.databinding.HomeEnumerationFragmentBinding
import org.nghru_pk.ghru.di.Injectable
import org.nghru_pk.ghru.util.autoCleared
import org.nghru_pk.ghru.util.singleClick
import javax.inject.Inject

class HomeEnumerationFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<HomeEnumerationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeenumerationViewModel: HomeEnumerationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<HomeEnumerationFragmentBinding>(
            inflater,
            R.layout.home_enumeration_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)

        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonNewHouseHold.singleClick {
            val intent = Intent(activity, EnumerationActivity::class.java)
            startActivity(intent)
        }

    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
