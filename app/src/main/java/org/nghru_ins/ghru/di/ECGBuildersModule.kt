package org.nghru_ins.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_ins.ghru.ui.ecg.guide.ElectrodeFragment
import org.nghru_ins.ghru.ui.ecg.guide.GuideFragment
import org.nghru_ins.ghru.ui.ecg.guide.PreperationFragment
import org.nghru_ins.ghru.ui.ecg.guide.main.GuideMainFragment
import org.nghru_ins.ghru.ui.ecg.main.InputFragment
import org.nghru_ins.ghru.ui.ecg.manualentry.ManualEntryECGFragment
import org.nghru_ins.ghru.ui.ecg.scanbarcode.ScanBarcodeFragment
import org.nghru_ins.ghru.ui.ecg.trace.TraceFragment
import org.nghru_ins.ghru.ui.ecg.trace.complete.CompleteDialogFragment
import org.nghru_ins.ghru.ui.ecg.trace.reason.ReasonDialogFragment
import org.nghru_ins.ghru.ui.ecg.verifyid.ECGVerifyIDFragment
import org.nghru_ins.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_ins.ghru.ui.stationcheck.StationCheckDialogFragment

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
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_ins.ghru.ui.ecg.trace.completed.CompletedDialogFragment
}

