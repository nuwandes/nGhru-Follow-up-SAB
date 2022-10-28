package org.nghru_ins.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_ins.ghru.ui.datamanagement.DataManagementListFragment
import org.nghru_ins.ghru.ui.devices.DevicesFragment
import org.nghru_ins.ghru.ui.enumeration.EnumerationFragment
import org.nghru_ins.ghru.ui.home.HomeFragment
import org.nghru_ins.ghru.ui.homeenumeration.HomeEnumerationFragment
import org.nghru_ins.ghru.ui.homeenumerationlist.HomeEmumerationListFragment
import org.nghru_ins.ghru.ui.logout.LogoutDialogFragment
import org.nghru_ins.ghru.ui.participantlist.ParticipantListFragment
import org.nghru_ins.ghru.ui.samplemanagement.SampleMangementFragment
import org.nghru_ins.ghru.ui.station.StationFragment
import org.nghru_ins.ghru.ui.usersetting.UserSettingFragment

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
