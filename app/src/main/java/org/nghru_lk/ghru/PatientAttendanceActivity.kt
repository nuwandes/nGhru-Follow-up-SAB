package org.nghru_lk.ghru

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.register_patient_activity.*
import org.nghru_lk.ghru.util.LocaleManager
import javax.inject.Inject

class PatientAttendanceActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.participant_attendance_activity)

        setupNavigationByCountry()

    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager(base).setLocale())
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentDestination = Navigation.findNavController(this, R.id.container).currentDestination
        val parent = currentDestination?.parent
        if (parent == null || currentDestination.id != parent.id)
            super.onBackPressed()
        else
            onSupportNavigateUp()
        return true
    }

    private fun setupNavigationByCountry() {

        val navHostFragment = container as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navNewSg = inflater.inflate(R.navigation.participant_attendance)
        navNewSg.setDefaultArguments(intent.extras)
            navHostFragment.navController.graph = navNewSg
    }
}
