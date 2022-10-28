package org.nghru_lk.ghru.ui.participantlist.measurementlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_lk.ghru.AppExecutors
import org.nghru_lk.ghru.R
import org.nghru_lk.ghru.databinding.MeasurementListItemBinding
import org.nghru_lk.ghru.databinding.ParticipantListItemBinding
import org.nghru_lk.ghru.ui.common.DataBoundListAdapter
import org.nghru_lk.ghru.util.singleClick
import org.nghru_lk.ghru.vo.*

class MeasurementListAdapter (
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val isConsent: Boolean?,
    private val callback: ((MeasurementListItem) -> Unit)?

) : DataBoundListAdapter<MeasurementListItem, MeasurementListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<MeasurementListItem>() {
        override fun areItemsTheSame(oldItem: MeasurementListItem, newItem: MeasurementListItem): Boolean {
            return oldItem.id == newItem.id


        }

        override fun areContentsTheSame(oldItem: MeasurementListItem, newItem: MeasurementListItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.station_name == newItem.station_name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): MeasurementListItemBinding {
        val binding = DataBindingUtil
            .inflate<MeasurementListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.measurement_list_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.measurementListItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: MeasurementListItemBinding, item: MeasurementListItem) {
        binding.measurementListItem = item

        val greenColor: String = "#388e3c"
        val redColor: String = "#d34836"

        //.d("MEASUREMENT_LIST_ADAPTER","CONSENT_STATUS: " + isConsent!!)

        if (item.station_name == "Consent")
        {
            //Log.d("MEASUREMENT_LIST_ADAPTER","INSIDE_CONSENT: " + isConsent!!)
            if (isConsent!!)
            {
                //Log.d("MEASUREMENT_LIST_ADAPTER","ISCONSENT_TRUE: " + isConsent!!)
                item.status = "Completed"
            }
            else
            {
                //Log.d("MEASUREMENT_LIST_ADAPTER","ISCONSENT_FALSE: " + isConsent!!)
                item.status = "Not started"
            }
            binding.buttonArrow.visibility = View.GONE
        }

        if (item.status == "Completed")
        {
            binding.participantId.setTextColor(Color.parseColor(greenColor))
        }
        else
        {
            binding.participantId.setTextColor(Color.parseColor(redColor))
        }
    }

}