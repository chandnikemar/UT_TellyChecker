package com.utmobile.uttellychecker.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utmobile.uttellychecker.R
import com.utmobile.uttellychecker.TLFormPageActivity

import com.utmobile.uttellychecker.databinding.ItemLayout2Binding
import com.utmobile.uttellychecker.model.VehicleDetail

class AssignedTLAdapter(
    private val context: Context,
    private var vehicleList: List<VehicleDetail> // Accept the full list
) : RecyclerView.Adapter<AssignedTLAdapter.AssignedTLViewHolder>() {

    private var filteredList: List<VehicleDetail> = vehicleList // Start with the full list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignedTLViewHolder {
        val binding = ItemLayout2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignedTLViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignedTLViewHolder, position: Int) {
        val vehicle = filteredList[position]
        holder.bind(vehicle)

        holder.binding.clItem.setOnClickListener {
            val intent = Intent(context, TLFormPageActivity::class.java).apply {
                putExtra("driverName", vehicle.driverName)
                putExtra("vrn", vehicle.vrn)
                putExtra("vehicleTransactionCode", vehicle.vehicleTransactionCode)
                putExtra("elvCode", vehicle.elvCode)
                putExtra("actionType", "AssignedTl")
            }
            context.startActivity(intent)
        }
    }

    // Use filteredList size for item count
    override fun getItemCount(): Int = filteredList.size

    inner class AssignedTLViewHolder(val binding: ItemLayout2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vehicle: VehicleDetail) {
            binding.tvVin.text = "${vehicle.vrn}"
            binding.tvDriverName.text = "Driver: ${vehicle.driverName}"
            binding.tvElvCode.text = "ELV Code: ${vehicle.elvCode}"
            binding.tvCurrentMilestone.text = "Milestone: ${vehicle.currentMilestone}"

            // Show arrow up or down based on some condition
            val milestoneIcon = if (vehicle.currentMilestone.toIntOrNull() ?: 0 > 50) {
                R.drawable.ic_arrow_upward
            } else {
                R.drawable.ic_arrow_downward
            }

        }
    }

    // This method will filter the list based on the query and notify the adapter
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            vehicleList // Show the full list if the query is empty
        } else {
            vehicleList.filter {
                it.vrn.contains(query, ignoreCase = true) ||
                        it.driverName.contains(query, ignoreCase = true) ||
                        it.elvCode.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged() // Notify adapter to refresh the list
    }

    // This method allows external code to pass a new list to the adapter
    fun updateVehicleList(newList: List<VehicleDetail>) {
        vehicleList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}
