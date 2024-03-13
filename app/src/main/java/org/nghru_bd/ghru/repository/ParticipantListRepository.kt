package org.nghru_bd.ghru.repository

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.birbit.android.jobqueue.JobManager
import org.nghru_bd.ghru.AppExecutors
import org.nghru_bd.ghru.R
import org.nghru_bd.ghru.api.ApiResponse
import org.nghru_bd.ghru.api.NghruService
import org.nghru_bd.ghru.db.ParticipantListItemDao
import org.nghru_bd.ghru.db.SiteDao
import org.nghru_bd.ghru.jobs.SyncParticipantListItemJob
import org.nghru_bd.ghru.util.LocaleManager
import org.nghru_bd.ghru.vo.*
import org.nghru_bd.ghru.vo.request.ParticipantListWithMeta
import java.io.Serializable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParticipantListRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val context: Context,
    private val localeManager: LocaleManager,
    private val participantListItemDao: ParticipantListItemDao,
    private val jobManager: JobManager,
    private val siteDao: SiteDao
//    private var stationItems: MutableLiveData<Resource<List<ParticipantStation>>>
) : Serializable {

    fun getParticipantListItems(): LiveData<Resource<ResourceData<ParticipantListWithMeta>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantListWithMeta>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListWithMeta>>> {
                return nghruService.allParticipants()
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

    fun getStationList(
        stations: List<ParticipantStation>
    ): LiveData<Resource<List<ParticipantStation>>> {
        val stationItems = MutableLiveData<Resource<List<ParticipantStation>>>()
        val resource = Resource(Status.SUCCESS, stations, Message(null, null))
        stationItems.setValue(resource)

        return stationItems

    }

    fun getSiteItems(): LiveData<Resource<Array<String>>> {

        return object : NetworkOnlyBoundResource<Array<String>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<Array<String>>> {
                return nghruService.getSiteNamesBySite()
            }
        }.asLiveData()
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
        var ecgStatus: String? = "Not started"
        var fundoStatus: String? = "Not started"
        var activityStatus: String? = "Not started"

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

            if (station.station_name.equals("ECG"))
            {
                if (station.isCancelled == 1)
                {
                    ecgStatus = "Canceled"
                }
                else
                {
                    ecgStatus = station.status_text
                }
            }

            if (station.station_name.equals("Fundoscopy"))
            {
                if (station.isCancelled == 1)
                {
                    fundoStatus = "Canceled"
                }
                else
                {
                    fundoStatus = station.status_text
                }
            }

            if (station.station_name.equals("Axivity"))
            {
                if (station.isCancelled == 1)
                {
                    activityStatus = "Canceled"
                }
                else
                {
                    activityStatus = station.status_text
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

        val measurementItem9 = MeasurementListItem(
            9,
            R.drawable.ic_icon_ecg,
            getStringByLocalBefore17(context, R.string.ecg, localeManager.getLanguage()),
            ecgStatus!!
        )

        val measurementItem10 = MeasurementListItem(
            10,
            R.drawable.ic_icon_fundoscopy,
            getStringByLocalBefore17(context, R.string.fundoscopy, localeManager.getLanguage()),
            fundoStatus!!
        )

        val measurementItem11 = MeasurementListItem(
            11,
            R.drawable.ic_icon_activity_tracker,
            getStringByLocalBefore17(context, R.string.activity_tracker, localeManager.getLanguage()),
            activityStatus!!
        )

        //test.add(measurementItem0)
        test.add(measurementItem1)
        test.add(measurementItem2)
        //test.add(measurementItem3)
        test.add(measurementItem4)
        test.add(measurementItem5)
        test.add(measurementItem6)
        test.add(measurementItem7)
        //test.add(measurementItem8)
        test.add(measurementItem9)
        test.add(measurementItem10)
        test.add(measurementItem11)

        val homeItems = MutableLiveData<Resource<List<MeasurementListItem>>>()
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

    // getting API data
    fun filterParticipantListItems(page:Int, status: String, site:String, keyWord: String): LiveData<Resource<ResourceData<ParticipantListWithMeta>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantListWithMeta>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListWithMeta>>> {
                return nghruService.filterParticipants(page, status, site, keyWord)
            }
        }.asLiveData()
    }

    fun insertParticipantListItemList(participantListItemList: List<ParticipantListItem>):
            LiveData<Resource<List<ParticipantListItem>>> {
        participantListItemDao.deleteAll()
        return object : LocalBoundInsertAllResource<List<ParticipantListItem>>(appExecutors) {

            override fun loadFromDb(): LiveData<List<ParticipantListItem>> {
                return participantListItemDao.getAllParticipantListItems()
            }

            override fun insertDb(): Unit {

                return participantListItemDao.insertAll(participantListItemList)
            }
        }.asLiveData()
    }

    fun getAllParticipantItemList(
    ): LiveData<Resource<List<ParticipantListItem>>> {
        return object : LocalBoundResource<List<ParticipantListItem>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ParticipantListItem>> {
                return participantListItemDao.getAllParticipantListItemsToHome()
            }
        }.asLiveData()
    }

//    fun updateSingleParticipant(participant: ParticipantListItem
//    ): LiveData<Resource<ParticipantListItem>> {
//        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
//            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
//                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
//            }
//
//            override fun updateDb(): Int {
//                return participantListItemDao.updateSingleParticipantListItem(
//                    participant.inablitiy_reason,
//                    participant.is_able,
//                    participant.is_rescheduled,
//                    participant.is_verified,
//                    participant.isSync,
//                    participant.participant_id!!
//                )
//            }
//
//        }.asLiveData()
//    }

    fun updateAndSyncParticipant(
        participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : MyNetworkBoundUpdateResource<ParticipantListItem,ResourceData<ParticipantListItem>>(appExecutors) {

            override fun createJob(insertedID: Int) {
                participant.id = insertedID
                jobManager.addJobInBackground(SyncParticipantListItemJob(participant))
            }
            override fun isNetworkAvilable(): Boolean {

                return participant.isSync!!
            }
            override fun updateDb(): Int {

                return participantListItemDao.updateSingleParticipantListItem(
                    participant.inablitiy_reason,
                    participant.is_able,
                    participant.is_rescheduled,
                    participant.is_verified,
                    participant.isSync,
                    participant.participant_id!!)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListItem>>> {
                return nghruService.updateParticipantSync(participant.participant_id!!, participant)
            }

        }.asLiveData()
    }

    fun updateParticipantItem(
        participant: ParticipantListItem,
        screening_id: String
    ): LiveData<Resource<ResourceData<ParticipantListItem>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<ParticipantListItem>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListItem>>> {
                return nghruService.updateParticipantSync(screening_id, participant)
            }
        }.asLiveData()
    }

    fun getAllUnSyncParticipantItem(
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundResource<ParticipantListItem>(appExecutors) {
            override fun loadFromDb(): LiveData<ParticipantListItem> {
                return participantListItemDao.getAllUnSyncParticipantListItem(true)
            }
        }.asLiveData()
    }

    fun insertSites(
        site: Site
    ): LiveData<Resource<Site>> {
        return object : LocalBoundInsertResource<Site>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<Site> {
                return siteDao.getSiteById("")
            }

            override fun insertDb(): Long {

                return siteDao.insert(site)
            }
        }.asLiveData()
    }

    fun getSearchParticipantItemList(
        searchParam : String
    ): LiveData<Resource<List<ParticipantListItem>>> {
        return object : LocalBoundResource<List<ParticipantListItem>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ParticipantListItem>> {
                return participantListItemDao.getSearchParticipant(searchParam)
            }
        }.asLiveData()
    }

    fun getStatusParticipantItemList(
        status : String
    ): LiveData<Resource<List<ParticipantListItem>>> {
        return object : LocalBoundResource<List<ParticipantListItem>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ParticipantListItem>> {
                return participantListItemDao.getStatusParticipant(status)
            }
        }.asLiveData()
    }

    fun getAllParticipantListItemsOffline(page:Int, status: String, site:String, keyWord: String, offline: Boolean): LiveData<Resource<ResourceData<ParticipantListWithMeta>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantListWithMeta>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantListWithMeta>>> {
                return nghruService.getAllParticipantsOffline(page, status, site, keyWord, offline)
            }
        }.asLiveData()
    }

    fun updateParticipantBMStatusNew(
        participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResourceNew<ParticipantListItem, Int>(appExecutors) {
            override fun updateDb(): Int {
                return participantListItemDao.updateBMStatus(participant.participant_id!!)
            }
        }.asLiveData()
    }

    fun updateParticipantBMStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateBMStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun updateParticipantBPStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateBPStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun updateParticipantBTStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateBTStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun updateParticipantSampleStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateSampleStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun updateParticipantEcgStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateEcgStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun updateParticipantFundoStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateFundoStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun updateParticipantActivityStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateActivityStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }

    fun getSingleParticipantItem(
        screening_id: String
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundResource<ParticipantListItem>(appExecutors) {
            override fun loadFromDb(): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipant(screening_id)
            }
        }.asLiveData()
    }

    fun getSiteParticipantItemList(
        site : String
    ): LiveData<Resource<List<ParticipantListItem>>> {
        return object : LocalBoundResource<List<ParticipantListItem>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ParticipantListItem>> {
                return participantListItemDao.getSiteParticipant(site)
            }
        }.asLiveData()
    }

    fun updateParticipantQueStatus(participant: ParticipantListItem
    ): LiveData<Resource<ParticipantListItem>> {
        return object : LocalBoundUpateResource<ParticipantListItem, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<ParticipantListItem> {
                return participantListItemDao.getSingleParticipantByParticipantId(participant.participant_id!!)
            }

            override fun updateDb(): Int {
                return participantListItemDao.updateQueStatus(
                    participant.participant_id!!
                )
            }

        }.asLiveData()
    }
}