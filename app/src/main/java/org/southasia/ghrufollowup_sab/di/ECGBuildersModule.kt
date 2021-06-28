package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.ecg.guide.ElectrodeFragment
import org.southasia.ghrufollowup_sab.ui.ecg.guide.GuideFragment
import org.southasia.ghrufollowup_sab.ui.ecg.guide.PreperationFragment
import org.southasia.ghrufollowup_sab.ui.ecg.guide.main.GuideMainFragment
import org.southasia.ghrufollowup_sab.ui.ecg.main.InputFragment
import org.southasia.ghrufollowup_sab.ui.ecg.manualentry.ManualEntryECGFragment
import org.southasia.ghrufollowup_sab.ui.ecg.scanbarcode.ScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.ecg.trace.TraceFragment
import org.southasia.ghrufollowup_sab.ui.ecg.trace.complete.CompleteDialogFragment
import org.southasia.ghrufollowup_sab.ui.ecg.trace.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.ecg.verifyid.ECGVerifyIDFragment
import org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class ECGBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeECGVerifyIDFragment(): ECGVerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeGuideMainFragment(): GuideMainFragment


    @ContributesAndroidInjector
    abstract fun contributeGuideFragment(): GuideFragment

    @ContributesAndroidInjector
    abstract fun contributeInputFragment(): InputFragment

    @ContributesAndroidInjector
    abstract fun contributeElectrodeFragment(): ElectrodeFragment

    @ContributesAndroidInjector
    abstract fun contributePreperationFragment(): PreperationFragment

    @ContributesAndroidInjector
    abstract fun contributeTraceFragment(): TraceFragment

    @ContributesAndroidInjector
    abstract fun contributeCompleteDialogFragment(): CompleteDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragmentt(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryECGFragment(): ManualEntryECGFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.ecg.trace.completed.CompletedDialogFragment
}

