package org.nghru_inn.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.io.Serializable


@Entity(tableName = "sites")
data class Site(
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @ColumnInfo(name = "site_name") var site_name: String?
) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(site_name)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Site> {
        override fun createFromParcel(parcel: Parcel): Site {
            return Site(parcel)
        }

        override fun newArray(size: Int): Array<Site?> {
            return arrayOfNulls(size)
        }
    }

}
