package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.datamanagement.DataManagementListFragment
import org.southasia.ghrufollowup_sab.ui.devices.DevicesFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.EnumerationFragment
import org.southasia.ghrufollowup_sab.ui.home.HomeFragment
import org.southasia.ghrufollowup_sab.ui.homeenumeration.HomeEnumerationFragment
import org.southasia.ghrufollowup_sab.ui.homeenumerationlist.HomeEmumerationListFragment
import org.southasia.ghrufollowup_sab.ui.logout.LogoutDialogFragment
import org.southasia.ghrufollowup_sab.ui.participantlist.ParticipantListFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.SampleMangementFragment
import org.southasia.ghrufollowup_sab.ui.station.StationFragment
import org.southasia.ghrufollowup_sab.ui.usersetting.UserSettingFragment

@Suppress("unused")
@Module
abstract class MainFragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeEnumerationFragment(): EnumerationFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeEmumerationListFragment(): HomeEmumerationListFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeEnumerationFragment(): HomeEnumerationFragment

    @ContributesAndroidInjector
    abstract fun contributeStationFragment(): StationFragment

    @ContributesAndroidInjector
    abstract fun contributeDevicesFragment(): DevicesFragment


    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

    @ContributesAndroidInjector
    abstract fun contributeUserSettingFragment(): UserSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeLogoutDialogFragment(): LogoutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDataManagementListFragment() : DataManagementListFragment

    @ContributesAndroidInjector
    abstract fun contributeParticipantListFragment(): ParticipantListFragment

}
