package com.capstone.project.hondealz.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.project.hondealz.R
import com.capstone.project.hondealz.data.response.HistoriesItem
import com.capstone.project.hondealz.databinding.HistoryItemBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoriesItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {
    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)

        holder.itemView.setOnClickListener {
            history.id?.let { id -> onItemClickListener?.invoke(id) }
        }
    }

    inner class HistoryViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: HistoriesItem) {
            with(binding) {
                // Set motor name
                tvMotorName.text = history.model ?: "N/A"

                // Set motor year
                tvMotorYear.text = history.year?.toString() ?: "N/A"

                // Set predicted price with Indonesian number format
                val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
                tvMotorPrice.text = "Rp ${numberFormat.format(history.predictedPrice ?: 0)}"

                // Load motor image
                Glide.with(itemView.context)
                    .load(history.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivMotor)

                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    history.createdAt?.let { dateString ->
                        // Ambil hanya bagian tanggal (sebelum T jika ada)
                        val cleanDateString = dateString.split("T").firstOrNull() ?: dateString
                        val parsedDate = inputFormat.parse(cleanDateString)
                        tvDate.text = parsedDate?.let { outputFormat.format(it) } ?: cleanDateString
                    }
                } catch (e: Exception) {
                    tvDate.text = history.createdAt ?: ""
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