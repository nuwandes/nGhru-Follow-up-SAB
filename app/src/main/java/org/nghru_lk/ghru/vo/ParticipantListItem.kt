package org.nghru_lk.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "participant_list_item")
data class ParticipantListItem (

    @Expose @SerializedName("firstname") @ColumnInfo(name = "firstname") var firstname: String?,
    @Expose @SerializedName("last_name") @ColumnInfo(name = "last_name") var last_name: String?,
    @Expose @SerializedName("participant_id") @ColumnInfo(name = "participant_id") var participant_id: String?,
    @Expose @SerializedName("gender") @ColumnInfo(name = "gender") var gender: String?,
    @Expose @SerializedName("dob") @ColumnInfo(name = "dob") var dob: String?,
    @Expose @SerializedName("nid") @ColumnInfo(name = "nid") var nid: String?,
    @Expose @SerializedName("phone") @ColumnInfo(name = "phone") var phone: String?,
    @Expose @SerializedName("site") @ColumnInfo(name = "site") var site: String?,
    @Expose @SerializedName("status") @ColumnInfo(name = "status") var status: String?,
    @Expose @SerializedName("address") @Embedded(prefix = "address_") var address: ParticipantListAddress?,
    @Expose @SerializedName("team") @ColumnInfo(name = "team") var team: String?,
    @Expose @SerializedName("height") @ColumnInfo(name = "height") var height: String?,
    @Expose @SerializedName("is_able") @ColumnInfo(name = "is_able") var is_able: Boolean = false,
    @Expose @SerializedName("inablitiy_reason") @ColumnInfo(name = "inablitiy_reason") var inablitiy_reason: String?,
    @Expose @SerializedName("is_verified") @ColumnInfo(name = "is_verified") var is_verified: Boolean = false,
    @Expose @SerializedName("verification_dob") @ColumnInfo(name = "verification_dob") var verification_dob: String?,
    @Expose @SerializedName("verification_id") @ColumnInfo(name = "verification_id") var verification_id: String?,
    @Expose @SerializedName("is_rescheduled") @ColumnInfo(name = "is_rescheduled") var is_rescheduled: Boolean = false,
    @Expose @SerializedName("rescheduled_date") @ColumnInfo(name = "rescheduled_date") var rescheduled_date: String?

) : Serializable, Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "statusId")
    var statusId: Int =0

    @ColumnInfo(name = "is_sync")
    var isSync: Boolean = false

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(ParticipantListAddress::class.java.classLoader),parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!
    ) {
        id = parcel.readInt()
        statusId = parcel.readInt()
        isSync = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstname)
        parcel.writeString(last_name)
        parcel.writeString(participant_id)
        parcel.writeString(gender)
        parcel.writeString(dob)
        parcel.writeString(nid)
        parcel.writeString(phone)
        parcel.writeString(site)
        parcel.writeString(status)
        parcel.writeInt(id)
        parcel.writeInt(statusId)
        parcel.writeParcelable(address, flags)
        parcel.writeString(team)
        parcel.writeString(height)
        parcel.writeByte(if (is_able) 1 else 0)
        parcel.writeString(inablitiy_reason)
        parcel.writeByte(if (is_verified) 1 else 0)
        parcel.writeString(verification_dob)
        parcel.writeString(verification_id)
        parcel.writeByte(if (is_rescheduled) 1 else 0)
        parcel.writeString(rescheduled_date)
        parcel.writeByte(if (isSync) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantListItem> {
        override fun createFromParcel(parcel: Parcel): ParticipantListItem {
            return ParticipantListItem(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantListItem?> {
            return arrayOfNulls(size)
        }
    }

}

data class ParticipantListAddress(
    @Expose @field:SerializedName("street") @ColumnInfo(name = "street") var street: String,
    @Expose @field:SerializedName("locality") @ColumnInfo(name = "locality") var locality: String,
    @Expose @field:SerializedName("postcode") @ColumnInfo(name = "postcode") var postcode: String,
    @Expose @field:SerializedName("country") @ColumnInfo(name = "country") var country: String

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(street)
        parcel.writeString(locality)
        parcel.writeString(postcode)
        parcel.writeString(country)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantListMeta> {
        override fun createFromParcel(parcel: Parcel): ParticipantListMeta {
            return ParticipantListMeta(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantListMeta?> {
            return arrayOfNulls(size)
        }
    }


}