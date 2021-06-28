package org.southasia.ghrufollowup_sab.ui.homeenumerationlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.R
import org.southasia.ghrufollowup_sab.databinding.HomeEmumerationListItemBinding
import org.southasia.ghrufollowup_sab.ui.common.DataBoundListAdapter
import org.southasia.ghrufollowup_sab.util.singleClick
import org.southasia.ghrufollowup_sab.vo.HomeEmumerationListItem


class HomeEmumerationListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HomeEmumerationListItem) -> Unit)?
) : DataBoundListAdapter<HomeEmumerationListItem, HomeEmumerationListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeEmumerationListItem>() {
        override fun areItemsTheSame(oldItem: HomeEmumerationListItem, newItem: HomeEmumerationListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeEmumerationListItem, newItem: HomeEmumerationListItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): HomeEmumerationListItemBinding {
        val binding = DataBindingUtil
            .inflate<HomeEmumerationListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.home_emumeration_list_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.homeEmumerationListItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: HomeEmumerationListItemBinding, item: HomeEmumerationListItem) {
        binding.homeEmumerationListItem = item
    }
}
