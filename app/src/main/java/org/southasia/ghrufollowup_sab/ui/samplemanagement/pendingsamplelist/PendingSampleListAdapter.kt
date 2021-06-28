package org.southasia.ghrufollowup_sab.ui.samplemanagement.pendingsamplelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.southasia.ghrufollowup_sab.AppExecutors
import org.southasia.ghrufollowup_sab.R
import org.southasia.ghrufollowup_sab.databinding.SampleItemBinding
import org.southasia.ghrufollowup_sab.ui.common.DataBoundListAdapter
import org.southasia.ghrufollowup_sab.util.singleClick
import org.southasia.ghrufollowup_sab.vo.request.SampleRequest


class PendingSampleListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((SampleRequest) -> Unit)?
) : DataBoundListAdapter<SampleRequest, SampleItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<SampleRequest>() {
        override fun areItemsTheSame(oldItem: SampleRequest, newItem: SampleRequest): Boolean {
            return oldItem.sampleId == newItem.sampleId
        }

        override fun areContentsTheSame(oldItem: SampleRequest, newItem: SampleRequest): Boolean {
            return oldItem.sampleId == newItem.sampleId
                    && oldItem.timestamp == newItem.timestamp
        }
    }
) {

    override fun createBinding(parent: ViewGroup): SampleItemBinding {
        val binding = DataBindingUtil
            .inflate<SampleItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.sample_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick { it ->
            binding.sample?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: SampleItemBinding, item: SampleRequest) {
        binding.sample = item
    }
}
