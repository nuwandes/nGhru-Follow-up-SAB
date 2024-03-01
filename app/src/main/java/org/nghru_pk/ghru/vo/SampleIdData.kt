package org.nghru_pk.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "sample_ids")
data class SampleIdData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Expose @SerializedName("id") @ColumnInfo(name = "key") val key: String?,
    @Expose @SerializedName("sample_id") @ColumnInfo(name = "sample_id") val storage_id: String?

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor(sampleId: String?) : this(
        id = 0,
        key = null,
        storage_id = sampleId
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(key)
        parcel.writeString(storage_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SampleIdData> {
        override fun createFromParcel(parcel: Parcel): SampleIdData {
            return SampleIdData(parcel)
        }

        override fun newArray(size: Int): Array<SampleIdData?> {
            return arrayOfNulls(size)
        }
    }

}
