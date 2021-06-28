package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.fundoscopy.displaybarcode.DisplayBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.guide.ElectrodeFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.guide.GuideFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.guide.PreperationFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.guide.main.GuideMainFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.manualentry.ManualEntryFundoscopyFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.reading.FundoscopyReadingFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.reading.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.scanbarcode.ScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.fundoscopy.verifyid.FundoscopyVerifyIDFragment
import org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.stationcheck.StationCheckDialogFragment


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
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.fundoscopy.reading.completed.CompletedDialogFragment
}

