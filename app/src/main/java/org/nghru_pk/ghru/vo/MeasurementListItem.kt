package org.nghru_pk.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class MeasurementListItem (
    var id: Int = 0,
    var resourceId: Int = 0,
    var station_name: String,
    var status: String
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(resourceId)
        parcel.writeString(station_name)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MeasurementListItem> {
        override fun createFromParcel(parcel: Parcel): MeasurementListItem {
            return MeasurementListItem(parcel)
        }

        override fun newArray(size: Int): Array<MeasurementListItem?> {
            return arrayOfNulls(size)
        }
    }

}