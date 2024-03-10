package org.nghru_inn.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_inn.ghru.ui.fundoscopy.displaybarcode.DisplayBarcodeFragment
import org.nghru_inn.ghru.ui.fundoscopy.guide.ElectrodeFragment
import org.nghru_inn.ghru.ui.fundoscopy.guide.GuideFragment
import org.nghru_inn.ghru.ui.fundoscopy.guide.PreperationFragment
import org.nghru_inn.ghru.ui.fundoscopy.guide.main.GuideMainFragment
import org.nghru_inn.ghru.ui.fundoscopy.manualentry.ManualEntryFundoscopyFragment
import org.nghru_inn.ghru.ui.fundoscopy.reading.FundoscopyReadingFragment
import org.nghru_inn.ghru.ui.fundoscopy.reading.reason.ReasonDialogFragment
import org.nghru_inn.ghru.ui.fundoscopy.scanbarcode.ScanBarcodeFragment
import org.nghru_inn.ghru.ui.fundoscopy.verifyid.FundoscopyVerifyIDFragment
import org.nghru_inn.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_inn.ghru.ui.stationcheck.StationCheckDialogFragment


@Suppress("unused")
@Module
abstract class FundoscopyBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFundoscopyVerifyIDFragment(): FundoscopyVerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeDisplayBarcodeFragment(): DisplayBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeGuideMainFragment(): GuideMainFragment

    @ContributesAndroidInjector
    abstract fun contributeGuideFragment(): GuideFragment

    @ContributesAndroidInjector
    abstract fun contributeElectrodeFragment(): ElectrodeFragment

    @ContributesAndroidInjector
    abstract fun contributePreperationFragment(): PreperationFragment

    @ContributesAndroidInjector
    abstract fun contributeFundoscopyReadingFragment(): FundoscopyReadingFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFundoscopyFragment(): ManualEntryFundoscopyFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_inn.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
}

