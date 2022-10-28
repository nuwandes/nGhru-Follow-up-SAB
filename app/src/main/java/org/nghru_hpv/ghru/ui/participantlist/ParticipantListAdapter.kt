package org.nghru_hpv.ghru.ui.participantlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.nghru_hpv.ghru.AppExecutors
import org.nghru_hpv.ghru.R
import org.nghru_hpv.ghru.databinding.ParticipantListItemBinding
import org.nghru_hpv.ghru.ui.common.DataBoundListAdapter
import org.nghru_hpv.ghru.util.singleClick
import org.nghru_hpv.ghru.vo.ParticipantListItem

class ParticipantListAdapter (
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((ParticipantListItem) -> Unit)?
) : DataBoundListAdapter<ParticipantListItem, ParticipantListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<ParticipantListItem>() {
        override fun areItemsTheSame(oldItem: ParticipantListItem, newItem: ParticipantListItem): Boolean {
            return oldItem.firstname == newItem.firstname


        }

        override fun areContentsTheSame(oldItem: ParticipantListItem, newItem: ParticipantListItem): Boolean {
            return oldItem.firstname == newItem.firstname
                    && oldItem.last_name == newItem.last_name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ParticipantListItemBinding {
        val binding = DataBindingUtil
            .inflate<ParticipantListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.participant_list_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.participantListItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ParticipantListItemBinding, item: ParticipantListItem) {
        binding.participantListItem = item

        if (item.status.equals("completed"))
        {
            item.statusId = R.drawable.ic_icon_status_tick
        }
        else if (item.status.equals("not_started"))
        {
            item.statusId = R.drawable.ic_icon_status_warning_yellow
        }
        else if (item.status.equals("not_complete") || item.status.equals("not_scheduled"))
        {
            item.statusId = R.drawable.ic_icon_status_warning_yellow
        }
        else if (item.status.equals("physical_measurement_pending"))
        {
            item.statusId = R.drawable.ic_icon_status_warning_yellow
        }
        else if (item.status.equals("questionnaire_pending"))
        {
            item.statusId = R.drawable.ic_icon_status_warning_yellow
        }
        else if (item.status.equals("declined"))
        {
            item.statusId = R.drawable.not_started
        }
        else if (item.status.equals("unable_to_complete"))
        {
            item.statusId = R.drawable.not_started
        }
        else if (item.status.equals("unable_to_contact"))
        {
            item.statusId = R.drawable.not_started
        }

        item.id ++

    }

}