package org.southasia.ghrufollowup_sab

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.southasia.ghrufollowup_sab.util.LocaleManager
import javax.inject.Inject

class SampleStorageActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_staorage_activity)

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
}
