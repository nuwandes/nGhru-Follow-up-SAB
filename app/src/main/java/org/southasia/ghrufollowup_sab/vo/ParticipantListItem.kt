package org.southasia.ghrufollowup_sab.vo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ParticipantListItem (
    @Expose @SerializedName("firstname") var firstname: String?,
    @Expose @SerializedName("last_name") var last_name: String?,
    @Expose @SerializedName("participant_id") var participant_id: String?,
    @Expose @SerializedName("gender") var gender: String?,
    @Expose @SerializedName("dob") var dob: String?,
    @Expose @SerializedName("nid") var nid: String?,
    @Expose @SerializedName("phone") var phone: String?,
    @Expose @SerializedName("site") var site: String?,
    @Expose @SerializedName("status") var status: String?,
    @Expose @SerializedName("address") var address: ParticipantListAddress?,
    @Expose @SerializedName("team") var team: String?,
    @Expose @SerializedName("height") var height: String?,
    @Expose @SerializedName("is_able") var is_able: Boolean = false,
    @Expose @SerializedName("inablitiy_reason") var inablitiy_reason: String?,
    @Expose @SerializedName("is_verified") var is_verified: Boolean = false,
    @Expose @SerializedName("verification_dob") var verification_dob: String?,
    @Expose @SerializedName("verification_id") var verification_id: String?,
    @Expose @SerializedName("is_rescheduled") var is_rescheduled: Boolean = false,
    @Expose @SerializedName("rescheduled_date") var rescheduled_date: String?

) : Serializable, Parcelable {

    var id: Int = 0
    var statusId: Int =0

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
    @Expose @field:SerializedName("street") var street: String,
    @Expose @field:SerializedName("locality") var locality: String,
    @Expose @field:SerializedName("postcode") var postcode: String,
    @Expose @field:SerializedName("country") var country: String

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