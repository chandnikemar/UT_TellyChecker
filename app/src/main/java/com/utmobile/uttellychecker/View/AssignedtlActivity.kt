package com.utmobile.uttellychecker.View

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.utmobile.uttellychecker.R
import com.utmobile.uttellychecker.adapter.AssignedTLAdapter
import com.utmobile.uttellychecker.databinding.ActivityAssignedtlBinding
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.Resource
import com.utmobile.uttellychecker.helper.SessionManager
import com.utmobile.uttellychecker.repository.UTTellyRespository
import com.utmobile.uttellychecker.viewmodel.AssigedTLlist.AssignedTLViewModelFactory
import com.utmobile.uttellychecker.viewmodel.AssigedTLlist.AssignedTLlistViewModel
import es.dmoral.toasty.Toasty

class AssignedtlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignedtlBinding
    private lateinit var viewModel: AssignedTLlistViewModel
    private lateinit var session: SessionManager
    private var token: String? = ""
    private lateinit var progress: ProgressDialog
    private var baseUrl: String = ""
    private lateinit var assignedTLAdapter: AssignedTLAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_assignedtl)

        // Initialize SessionManager and ProgressDialog
        session = SessionManager(this)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")

        setSupportActionBar(binding.TPToolBar)
        supportActionBar?.title = "Assigned TL"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val userDetails = session.getUserDetails()
        if (userDetails.isEmpty()) {
            Toasty.error(this, "User details are missing.", Toasty.LENGTH_SHORT).show()
            return
        } else {
            token = userDetails["jwtToken"]
        }

        // Initialize Repository and ViewModel
        val utTellyRespository = UTTellyRespository()
        val viewModelFactory = AssignedTLViewModelFactory(application, utTellyRespository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AssignedTLlistViewModel::class.java)

        // Set up user details and base URL
        val userDetail = session.getUserDetails()
        val serverIpSharedPrefText = userDetail[Constants.KEY_SERVER_IP].toString()
        val serverHttpPrefText = userDetail[Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"

        // Set up the RecyclerView Adapter
        assignedTLAdapter = AssignedTLAdapter(this, emptyList())  // Initialize with empty list
        binding.rvAssignTLList.layoutManager = LinearLayoutManager(this)
        binding.rvAssignTLList.adapter = assignedTLAdapter

        // Observer for the assignedTLLiveData
        viewModel.assignedTLLiveData.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    progress.show()
                }
                is Resource.Success -> {
                    progress.dismiss()
                    val assignedTLList = response.data
                    if (assignedTLList.isNullOrEmpty()) {
                        Toasty.error(this, "No assigned TL data available", Toasty.LENGTH_SHORT).show()
                    } else {
                        assignedTLAdapter.updateVehicleList(assignedTLList)
                        setupSearch()
                    }
                }
                is Resource.Error -> {
                    progress.dismiss()
                    Toasty.error(this, " ${response.message}", Toasty.LENGTH_SHORT).show()
                }
            }
        }

        loadAssignedTLData()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                assignedTLAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mcvBtnClear.setOnClickListener {
            binding.etSearch.setText("")
        }
    }


    private fun loadAssignedTLData() {
        val bearerToken = token ?: ""
        viewModel.getAssignedVehicles(bearerToken, baseUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
