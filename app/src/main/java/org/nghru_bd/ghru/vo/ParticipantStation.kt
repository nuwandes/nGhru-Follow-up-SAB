package org.nghru_bd.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ParticipantStation (
    @Expose @SerializedName("participant_id") var participant_id: String?,
    @Expose @SerializedName("station_id") var station_id: String?,
    @Expose @SerializedName("station_name") var station_name: String?,
    @Expose @SerializedName("status_text") var status_text: String?,
    @Expose @SerializedName("status_code") var status_code: String?,
    @Expose @SerializedName("is_cancelled") var isCancelled: Int? = 0

) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        isCancelled = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    constructor(participant_id: String?, station_id: String?, station_name: String?, status_text: String?) : this(

        participant_id = participant_id,
        station_id = station_id,
        station_name = station_name,
        status_text = null,
        status_code = null,
        isCancelled = null
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(participant_id)
        parcel.writeString(station_id)
        parcel.writeString(station_name)
        parcel.writeString(status_text)
        parcel.writeString(status_code)
        parcel.writeValue(isCancelled)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantStation> {
        override fun createFromParcel(parcel: Parcel): ParticipantStation {
            return ParticipantStation(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantStation?> {
            return arrayOfNulls(size)
        }
    }

}