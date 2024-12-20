package com.capstone.project.hondealz.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.response.HistoriesItem
import com.capstone.project.hondealz.databinding.HistoryItemBinding
import com.capstone.project.hondealz.view.scan.result.ResultActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoriesItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    inner class HistoryViewHolder(
        private val binding: HistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoriesItem) {
            with(binding) {
                tvMotorName.text = history.model ?: "N/A"

                tvMotorYear.text = history.year?.toString() ?: "N/A"

                val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
                tvMotorPrice.text = "Rp ${numberFormat.format(history.predictedPrice ?: 0)}"

                Glide.with(itemView.context)
                    .load(history.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivMotor)

                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    history.createdAt?.let { dateString ->
                        val cleanDateString = dateString.split("T").firstOrNull() ?: dateString
                        val parsedDate = inputFormat.parse(cleanDateString)
                        tvDate.text = parsedDate?.let { outputFormat.format(it) } ?: cleanDateString
                    }
                } catch (e: Exception) {
                    tvDate.text = history.createdAt ?: ""
                }
            }

            itemView.setOnClickListener {
                history.id?.let { id ->
                    val context = itemView.context
                    val intent = Intent(context, ResultActivity::class.java).apply {
                        putExtra(ResultActivity.EXTRA_HISTORY_ID, id)
                        putExtra(ResultActivity.EXTRA_BACKUP_IMAGE_URL, history.imageUrl)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoriesItem>() {
            override fun areItemsTheSame(
                oldItem: HistoriesItem,
                newItem: HistoriesItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: HistoriesItem,
                newItem: HistoriesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}