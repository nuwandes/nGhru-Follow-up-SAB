package org.nghru_lk.ghru.repository

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.nghru_lk.ghru.R
import org.nghru_lk.ghru.util.LocaleManager
import org.nghru_lk.ghru.vo.HomeEmumerationListItem
import org.nghru_lk.ghru.vo.Message
import org.nghru_lk.ghru.vo.Resource
import org.nghru_lk.ghru.vo.Status
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeEmumerationListRepository @Inject constructor(
    private val context: Context,
    private val localeManager: LocaleManager
) {

    fun getHomeEmumerationListItems(): LiveData<Resource<List<HomeEmumerationListItem>>> {


        val mHomeEmumerationListItem = HomeEmumerationListItem(
            1,
            getStringByLocalBefore17(context, R.string.enumeration, localeManager.getLanguage()),
            R.drawable.ic_icon_enumeration
        )

        val mHomeEmumerationListItem1 = HomeEmumerationListItem(
            2,
            getStringByLocalBefore17(context, R.string.screening, localeManager.getLanguage()),
            R.drawable.ic_icon_screening
        )

        val mHomeEmumerationListItem6 = HomeEmumerationListItem(
            6,
            getStringByLocalBefore17(context, R.string.home_sample_management, localeManager.getLanguage()),
            R.drawable.ic_icon_pathology
        )

        val mHomeEmumerationListItem2 = HomeEmumerationListItem(
            3,
            getStringByLocalBefore17(context, R.string.home_reports, localeManager.getLanguage()),
            R.drawable.ic_icon_medical_report
        )

        val test = ArrayList<HomeEmumerationListItem>()

        //test.add(mHomeEmumerationListItem)
        test.add(mHomeEmumerationListItem1)
        test.add(mHomeEmumerationListItem6)
        //test.add(mHomeEmumerationListItem2)

        val homeItems = MutableLiveData<Resource<List<HomeEmumerationListItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)
        return homeItems
    }


    private fun getStringByLocalBefore17(context: Context, resId: Int, language: String): String {
        val currentResources = context.resources
        val assets = currentResources.assets
        val metrics = currentResources.displayMetrics
        val config = Configuration(currentResources.configuration)
        val locale = Locale(language)
        Locale.setDefault(locale)
        config.locale = locale
        val defaultLocaleResources = Resources(assets, metrics, config)
        val string = defaultLocaleResources.getString(resId)
        // Restore device-specific locale
        Resources(assets, metrics, currentResources.configuration)
        return string
    }
}