package org.nghru_hpv.ghru.di;

import android.content.Context;
import com.birbit.android.jobqueue.network.NetworkUtil;

public class NHealthNetworkUtil implements NetworkUtil {
    private int networkStatus;

    protected void setNetworkStatus(int networkStatus) {
        this.networkStatus = networkStatus;
    }

    @Override
    public int getNetworkStatus(Context context) {
        return networkStatus;
    }
}
