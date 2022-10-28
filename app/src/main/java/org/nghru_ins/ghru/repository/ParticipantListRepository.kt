package org.nghru_ins.ghru.repository

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.nghru_ins.ghru.AppExecutors
import org.nghru_ins.ghru.R
import org.nghru_ins.ghru.api.ApiResponse
import org.nghru_ins.ghru.api.NghruService
import org.nghru_ins.ghru.util.LocaleManager
import org.nghru_ins.ghru.vo.*
import org.nghru_ins.ghru.vo.request.ParticipantListWithMeta
import java.io.Serializable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParticipantListRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val context: Context,
    private val localeManager: LocaleManager
//    private var stationItems: MutableLiveData<Resource<List<ParticipantStation>>>
) : Serializable {

    fun getParticipantListItems(): LiveData<Resource<ResourceData<ParticipantListWithMeta>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantListWithMeta>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListWithMeta>>> {
                return nghruService.allParticipants()
            }
        }.asLiveData()
    }

    fun filterParticipantListItems(page:Int, status: String, site:String, keyWord: String): LiveData<Resource<ResourceData<ParticipantListWithMeta>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantListWithMeta>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListWithMeta>>> {
                return nghruService.filterParticipants(page, status, site, keyWord)
            }
        }.asLiveData()
    }

    fun getSiteSpinnerItems(id: String): LiveData<Resource<ResourceData<Array<String>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<Array<String>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Array<String>>>> {
                return nghruService.getSiteNames()
            }
        }.asLiveData()
    }

    fun paginateParticipantList(pageNumber:Int): LiveData<Resource<ResourceData<ParticipantListWithMeta>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantListWithMeta>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListWithMeta>>> {
                return nghruService.paginateParticipants(pageNumber)
            }
        }.asLiveData()
    }

    fun getSingleParticipantStations(
        participantID: String):
            LiveData<Resource<StationData<List<ParticipantStation>>>> {

        return object : NetworkOnlyBoundResource<StationData<List<ParticipantStation>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<StationData<List<ParticipantStation>>>> {
                return nghruService.getSingleParticipant(participantID)
            }
        }.asLiveData()
    }

    fun getStationList(stations: List<ParticipantStation>): LiveData<Resource<List<ParticipantStation>>> {

        val stationItems = MutableLiveData<Resource<List<ParticipantStation>>>()
        val resource = Resource(Status.SUCCESS, stations, Message(null, null))
        stationItems.setValue(resource)

        return stationItems

    }

    fun getMeasurementListItems(stations: List<ParticipantStation>): LiveData<Resource<List<MeasurementListItem>>>
    {
        val test = ArrayList<MeasurementListItem>()

        var bodyStatus: String? = "Not started"
        var bloodStatus: String? = "Not started"
        //var spiroStatus: String? = "Not started"
        var queStatus: String? = "Not started"
        var samColStatus: String? = "Not started"
        var consentStatus: String? = "Not started"
        var bloodTestStatus: String? = "Not started"
        var intakeStatus: String? = "Not started"
        var covidStatus: String? = "Not started"

        for (station in stations)
        {
            if (station.station_name.equals("Body Measurements"))
            {
                if (station.isCancelled == 1)
                {
                    bodyStatus = "Canceled"
                }
                else
                {
                    bodyStatus = station.status_text
                }
            }

            if (station.station_name.equals("Blood Pressure"))
            {
                if (station.isCancelled == 1)
                {
                    bloodStatus = "Canceled"
                }
                else
                {
                    bloodStatus = station.status_text
                }
            }

            if (station.station_name.equals("Blood Test"))
            {
                if (station.isCancelled == 1)
                {
                    bloodTestStatus = "Canceled"
                }
                else
                {
                    bloodTestStatus = station.status_text
                }
            }

//            if (station.station_name.equals("Spirometry"))
//            {
//                if (station.isCancelled == 1)
//                {
//                    spiroStatus = "Canceled"
//                }
//                else
//                {
//                    spiroStatus = station.status_text
//                }
//            }

            if (station.station_name.equals("Health Questionnaire"))
            {
                if (station.isCancelled == 1)
                {
                    queStatus = "Canceled"
                }
                else
                {
                    queStatus = station.status_text
                }
            }

            if (station.station_name.equals("Biological Samples"))
            {
                if (station.isCancelled == 1)
                {
                    samColStatus = "Canceled"
                }
                else if (station.status_code.equals("1"))
                {
                    samColStatus = station.status_text
                }
                else
                {
                    samColStatus = "Completed"
                }
            }

            if (station.station_name.equals("Consent"))
            {
//                if (station.isCancelled == 1)
//                {
//                    consentStatus = "Canceled"
//                }
//                else
//                {
//                    consentStatus = station.status_text
//                }

                consentStatus = station.status_text

            }

            if (station.station_name.equals("Intake 24"))
            {
                if (station.isCancelled == 1)
                {
                    intakeStatus = "Canceled"
                }
                else
                {
                    intakeStatus = station.status_text
                }
            }

            if (station.station_name.equals("Covid Questionnaire"))
            {
                if (station.isCancelled == 1)
                {
                    covidStatus = "Canceled"
                }
                else
                {
                    covidStatus = station.status_text
                }
            }
        }

        val measurementItem0 = MeasurementListItem(
            0,
            R.drawable.ic_icon_settings_consent,
            getStringByLocalBefore17(context, R.string.measurement_list_consent, localeManager.getLanguage()),
            consentStatus!!
        )

        val measurementItem1 = MeasurementListItem(
            1,
            R.drawable.ic_icon_body_measurements,
            getStringByLocalBefore17(context, R.string.screening_body_measurements, localeManager.getLanguage()),
            bodyStatus!!
        )

        val measurementItem2 = MeasurementListItem(
            2,
            R.drawable.blood_pressure,
            getStringByLocalBefore17(context, R.string.bp_blood_pressure, localeManager.getLanguage()),
            bloodStatus!!
        )

//        val measurementItem3 = MeasurementListItem(
//            3,
//            R.drawable.ic_icon_spirometry,
//            getStringByLocalBefore17(context, R.string.spirometry, localeManager.getLanguage()),
//            spiroStatus!!
//        )

        val measurementItem4 = MeasurementListItem(
            4,
            R.drawable.fbg,
            getStringByLocalBefore17(context, R.string.measurement_list_blood_test, localeManager.getLanguage()),
            bloodTestStatus!!
        )

        val measurementItem5 = MeasurementListItem(
            5,
            R.drawable.questionnaire,
            getStringByLocalBefore17(context, R.string.questionnaire, localeManager.getLanguage()),
            queStatus!!
        )

        val measurementItem6 = MeasurementListItem(
            6,
            R.drawable.ic_icon_bio_samples,
            getStringByLocalBefore17(context, R.string.measurement_list_sample_collection, localeManager.getLanguage()),
            samColStatus!!
        )

        val measurementItem7 = MeasurementListItem(
            7,
            R.drawable.ic_icon_intake,
            getStringByLocalBefore17(context, R.string.measurement_list_intake, localeManager.getLanguage()),
            intakeStatus!!
        )

        val measurementItem8 = MeasurementListItem(
            8,
            R.drawable.self1_36,
            getStringByLocalBefore17(context, R.string.measurement_list_covid, localeManager.getLanguage()),
            covidStatus!!
        )

        test.add(measurementItem0)
        test.add(measurementItem1)
        test.add(measurementItem2)
        //test.add(measurementItem3)
        test.add(measurementItem4)
        test.add(measurementItem5)
        test.add(measurementItem6)
        test.add(measurementItem7)
        test.add(measurementItem8)

        val homeItems = MutableLiveData<Resource<List<MeasurementListItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)
        return homeItems
    }

    fun updateParticipantItem(
        participant: ParticipantListItem,
        screening_id: String
    ): LiveData<Resource<ResourceData<ParticipantListItem>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<ParticipantListItem>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListItem>>> {
                return nghruService.updateParticipant(screening_id, participant)
            }
        }.asLiveData()
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