package com.utmobile.uttellychecker.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utmobile.uttellychecker.TLFormPageActivity

import com.utmobile.uttellychecker.databinding.ItemLayoutBinding
import com.utmobile.uttellychecker.model.AssignTlSubmit.VehicleDetailAssign

class AssignTLAdapter(
    private val context: Context,
    private val vehicleList: List<VehicleDetailAssign>
) : RecyclerView.Adapter<AssignTLAdapter.AssignTLViewHolder>() {
    private var filteredList: List<VehicleDetailAssign> = vehicleList.toList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignTLViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignTLViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignTLViewHolder, position: Int) {
        val vehicle = filteredList[position]
        holder.bind(vehicle)

        holder.binding.clItem.setOnClickListener {
            val intent = Intent(context, TLFormPageActivity::class.java).apply {
                putExtra("driverName", vehicle.driverName)
                putExtra("vrn", vehicle.vrn)
                putExtra("vehicleTransactionCode", vehicle.vehicleTransactionCode)
                putExtra("elvCode", vehicle.elvCode)
                putExtra("actionType", "AssignTl")
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = filteredList.size

    inner class AssignTLViewHolder(val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicle: VehicleDetailAssign) {
            binding.tvDriverName.text = vehicle.driverName
            binding.tvVin.text = vehicle.vrn
            binding.tvElvCode.text = vehicle.elvCode
        }
    }
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            vehicleList
        } else {
            vehicleList.filter {
                it.vrn.contains(query, ignoreCase = true) ||
                        it.driverName.contains(query, ignoreCase = true) ||
                        it.elvCode.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
