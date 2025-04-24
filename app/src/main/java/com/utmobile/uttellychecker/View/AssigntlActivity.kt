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
import com.utmobile.uttellychecker.adapter.AssignTLAdapter
import com.utmobile.uttellychecker.databinding.ActivityAssigntlBinding
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.Resource
import com.utmobile.uttellychecker.helper.SessionManager
import com.utmobile.uttellychecker.helper.TokenManager
import com.utmobile.uttellychecker.repository.UTTellyRespository
import com.utmobile.uttellychecker.viewmodel.AssigedTLlist.AssignedTLViewModelFactory
import com.utmobile.uttellychecker.viewmodel.AssigedTLlist.AssignedTLlistViewModel
import es.dmoral.toasty.Toasty

class AssigntlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssigntlBinding
    private lateinit var viewModel: AssignedTLlistViewModel
    private lateinit var session: SessionManager
    private var token: String? = ""
    private lateinit var progress: ProgressDialog
    private var baseUrl: String = ""

    private var userName: String? = ""
    private lateinit var userDetail: HashMap<String, String?>
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    private lateinit var tokenManager: TokenManager

    private lateinit var assignTLAdapter: AssignTLAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_assigntl)

        session = SessionManager(this)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")

        setSupportActionBar(binding.TPToolBar)
        supportActionBar?.title = "Assign TL"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val userDetails = session.getUserDetails()
        if (userDetails.isEmpty()) {
            Toasty.error(this, "User details are missing.", Toasty.LENGTH_SHORT).show()
        } else {
            token = userDetails["jwtToken"]
        }

        val utTellyRespository = UTTellyRespository()
        val viewModelFactory = AssignedTLViewModelFactory(application, utTellyRespository)
        viewModel = ViewModelProvider(this, viewModelFactory)[AssignedTLlistViewModel::class.java]

        tokenManager = TokenManager(this)
        session = SessionManager(this)
        userDetail = session.getUserDetails()
        token = userDetails["jwtToken"]
        userName = userDetails["userName"]
        serverIpSharedPrefText = userDetails[Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails[Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"


        viewModel.assignTLLiveData.observe(this) { response ->
            when (response) {
                is Resource.Loading -> progress.show()
                is Resource.Success -> {
                    progress.dismiss()
                    val assignTLList = response.data
                    if (!assignTLList.isNullOrEmpty()) {
                        assignTLAdapter = AssignTLAdapter(this, assignTLList)
                        binding.rvAssignTLList.layoutManager = LinearLayoutManager(this)
                        binding.rvAssignTLList.adapter = assignTLAdapter

                        setupSearch()
                    } else {
                        Toasty.error(this, "No assigned TL data available", Toasty.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> {
                    progress.dismiss()
                    Toasty.error(this, " ${response.message}", Toasty.LENGTH_SHORT).show()
                }
            }
        }

        loadAssignTLData()
    }

    private fun loadAssignTLData() {
        val bearerToken = token ?: ""
        viewModel.getAssignVehicles(bearerToken, baseUrl)
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                assignTLAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mcvBtnClear.setOnClickListener {
            binding.etSearch.setText("")
        }
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
