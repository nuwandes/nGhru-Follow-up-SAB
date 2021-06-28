package org.southasia.ghrufollowup_sab.vo.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.southasia.ghrufollowup_sab.vo.ParticipantStation
import java.io.Serializable

class ParticipantStationRequest (
    @Expose @SerializedName("stations") val stationList: ArrayList<ParticipantStation?>?= null
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(ParticipantStation)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(stationList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantStationRequest> {
        override fun createFromParcel(parcel: Parcel): ParticipantStationRequest {
            return ParticipantStationRequest(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantStationRequest?> {
            return arrayOfNulls(size)
        }
    }

}