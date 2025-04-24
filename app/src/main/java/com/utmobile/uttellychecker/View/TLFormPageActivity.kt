package com.utmobile.uttellychecker

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.utmobile.uttellychecker.databinding.ActivityTlformpageBinding
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.Resource
import com.utmobile.uttellychecker.helper.SessionManager
import com.utmobile.uttellychecker.helper.TokenManager
import com.utmobile.uttellychecker.helper.Utils
import com.utmobile.uttellychecker.model.AssignTlSubmit.AssignTlSubmitRequest
import com.utmobile.uttellychecker.repository.UTTellyRespository
import com.utmobile.uttellychecker.viewmodel.tlformpage.TLFormPageViewModel
import com.utmobile.uttellychecker.viewmodel.tlformpage.TlformpageViewModelFactory
import com.zebra.sdk.btleComm.BluetoothLeConnection
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TLFormPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTlformpageBinding
    private val viewModel: TLFormPageViewModel by viewModels {
        TlformpageViewModelFactory(application, UTTellyRespository())
    }
    private lateinit var session: SessionManager
    private var token: String? = ""
    private lateinit var progress: ProgressDialog
    private var baseUrl: String = ""

    private var userName: String? = ""
    private lateinit var userDetail: HashMap<String, String?>
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    private lateinit var tokenManager: TokenManager

    private var selectedLocationCode: String? = null
    private var selectedParentLocationName: String? = null

    //printer integration
    private var printerMac = ""
    private var activeConnection: BluetoothLeConnection? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityTlformpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = SessionManager(this)
        progress = ProgressDialog(this)
        progress.setMessage("Please Wait...")
        setSupportActionBar(binding.TPToolBar)
        supportActionBar?.title = "Assign TL Form"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // Fetch User Details from SessionManager
        val userDetails = session.getUserDetails()
        if (userDetails.isEmpty()) {
            Toasty.error(this, "User details are missing.", Toasty.LENGTH_SHORT).show()
        } else {
            token = userDetails["jwtToken"]
        }
        requestBluetoothPermissions()
        if (!Utils.getSharedPrefs(this@TLFormPageActivity, Constants.KEY_PRINTER_MAC).isNullOrEmpty()) {
            printerMac = Utils.getSharedPrefs(this@TLFormPageActivity, Constants.KEY_PRINTER_MAC).toString()
            lifecycleScope.launch {
                connectToPrinterDirectly()
            }
        } else {
            printerMac = ""
        }

        // Initialize tokenManager and session
        tokenManager = TokenManager(this)
        session = SessionManager(this)
        userDetail = session.getUserDetails()
        token = userDetails["jwtToken"]
        userName = userDetails["userName"]
        serverIpSharedPrefText = userDetails[Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails[Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"

        // Get data from the Intent
        val driverName = intent.getStringExtra("driverName")
        val vrn = intent.getStringExtra("vrn")
        val vehicleTransactionCode = intent.getStringExtra("vehicleTransactionCode")
        val elvCode = intent.getStringExtra("elvCode")
        val actionType = intent.getStringExtra("actionType")

        fetchAssignmentDetails(elvCode.toString())
        binding.tvDriverName.setText(driverName)
        binding.etVehicleNumber.setText(vrn)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fadein)
        binding.spinnerTl.startAnimation(animation)

        if (actionType == "AssignTl") {
            binding.ly1.visibility = View.VISIBLE   // Show Assign + Clear
            binding.ly2.visibility = View.GONE
        } else if (actionType == "AssignedTl") {
            binding.ly1.visibility = View.GONE
            binding.ly2.visibility = View.VISIBLE   // Show Reprint + Reassign
        }

        viewModel.locationsLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    hideProgressBar()
                    val locations = resource.data ?: emptyList()


                    val locationNames = mutableListOf("Select Truck Loader")
                    locationNames.addAll(locations.map { it.locationName })


                    val spinnerAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        locationNames
                    )
                    binding.spinnerTl.adapter = spinnerAdapter


                    binding.spinnerTl.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            private var isFirstSelection = true
                            override fun onItemSelected(
                                parent: AdapterView<*>?, view: View?, position: Int, id: Long,
                            ) {
                                if (isFirstSelection) {
                                    isFirstSelection = false
                                    return
                                }
                                if (position == 0) {

                                    selectedLocationCode = null
                                    selectedParentLocationName = null
                                    toggleButtons(false)
                                } else {
                                    // User selected a real TL
                                    val selectedLocation = locations[position - 1] // offset by 1
                                    selectedLocationCode = selectedLocation.locationCode
                                    selectedParentLocationName = selectedLocation.parentLocationName
                                    toggleButtons(true)
                                }

//                                val selectedLocation = locations[position]
//                                selectedLocationCode = selectedLocation.locationCode
//                                selectedParentLocationName = selectedLocation.parentLocationName


//                                binding.tvParentLocationName.text =
//                                    "Parent Location: $selectedParentLocationName"

                                android.util.Log.d(
                                    "TLFormPageActivity",
                                    "Selected TLCode: $selectedLocationCode"
                                )
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                selectedLocationCode = null
                                selectedParentLocationName = null
                                toggleButtons(false)
                            }
                        }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.submitTLResponseLiveData.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    hideProgressBar()
                    result.data?.let { resultResponse ->
                        try {
                            val data = result.data?.responseObject
                            if(data?.prnContentDetail!=null)
                            {
                                printZplInChunks(data.prnContentDetail)
                            }
                            else
                            {
                                Toasty.error(
                                    this,
                                    "Print Detail Not Found..!",
                                    Toasty.LENGTH_SHORT
                                ).show()
                            }
                            Toasty.success(
                                this,
                                "Assigned Successfully to ${data?.truckLoader}",
                                Toasty.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {

                        }


                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toasty.error(this, "Submit failed: ${result.message}", Toasty.LENGTH_LONG)
                        .show()
                    // Show error: result.message
                }
            }
        }
        viewModel.reAssignedResponseLiveData.observe(this) { result ->
            when (result) {
                is Resource.Loading<*> -> {
                    showProgressBar()
                }

                is Resource.Success<*> -> {
                    hideProgressBar()
                    result.data?.let { resultResponse ->
                        try {
                            val data = result.data?.responseObject
                            if(data?.prnContentDetail!=null)
                            {
                                printZplInChunks(data.prnContentDetail)
                            }
                            else
                            {
                                Toasty.error(
                                    this,
                                    "Print Detail Not Found..!",
                                    Toasty.LENGTH_SHORT
                                ).show()
                            }
                            Toasty.success(
                                this,
                                "ReAssigned Successfully to ${data?.truckLoader}",
                                Toasty.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toasty.error(this, "Submit failed: ${result.message}", Toasty.LENGTH_LONG).show()
                        }

                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    Toasty.error(
                        this, "ReAssigned " +
                                "Submit failed: ${result.message}", Toasty.LENGTH_LONG
                    ).show()

                }
            }
        }

        viewModel.reprintResponseLiveData.observe(this) { result ->
            when (result) {
                is Resource.Loading<*> -> {
                    showProgressBar()
                }

                is Resource.Success<*> -> {
                    hideProgressBar()
                    val data = result.data?.responseObject

                    if (data?.prnContentDetail != null) {
                        try {
                            printZplInChunks(data.prnContentDetail)
                            Toasty.success(
                                this,
                                "Reprint Successfully ",
                                Toasty.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toasty.error(
                                this,
                                "Error while printing: ${e.localizedMessage}",
                                Toasty.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toasty.error(
                            this,
                            "Print Detail Not Found..!",
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressBar()
                    Toasty.error(
                        this,
                        "Reprint  failed: ${result.message}",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
        }


        viewModel.assignedDetailTLLiveData.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    hideProgressBar()
                    val dataList = result.data
                    val data = dataList?.firstOrNull()
                    if (data != null) {
                        val response = data.responseObject
                        val bagQuantity = response.bagQuantity+" Bags"
                        val productqty = response.productWeight
                        val material = response.materialType
                        binding.tvMaterialValue.setText(material.toString())
                        binding.tvProductWeightValue.setText(productqty.toString())
                        binding.tvQtyValue.setText(bagQuantity.toString()+"Bags")
                        if (bagQuantity != null) {
                            binding.tvQtyValue.setText(bagQuantity.toString())

                        } else {
                            Toasty.info(this, "Bag quantity not available", Toasty.LENGTH_SHORT)
                                .show()
                        }
                        android.util.Log.d("TLFormPageActivity", "Response Data: $data")
                        android.util.Log.d(
                            "TLFormPageActivity",
                            "Bag Quantity: ${data.responseObject?.bagQuantity}"
                        )

//                        Toasty.success(
//                            this,
//                            "ReAssigned Successfully to ${response}",
//                            Toasty.LENGTH_SHORT
//                        ).show()
                    } else {
                        Toasty.info(this, "No assignment detail found", Toasty.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    Toasty.error(
                        this,
                        "ReAssigned Submit failed: ${result.message}",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
        }


        binding.btnSubmit1.setOnClickListener {
            val vehicleNumber = binding.etVehicleNumber.text.toString()
            val driverName = binding.tvDriverName.text.toString()

            if (selectedLocationCode.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a Truck Loader", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (vehicleNumber.isBlank()) {
                Toast.makeText(this, "Vehicle number is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (elvCode.isNullOrBlank()) {
                Toast.makeText(this, "ElvCode is missing.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showConfirmationDialog("Confirm Assign TL", "Are you sure you want to assign this TL?",shouldFinish = true) {
            val request = AssignTlSubmitRequest(
                ElvCode = elvCode,
                VRN = vehicleNumber,
                TLCode = selectedLocationCode!!,
                Reprint = false
            )
            token?.let {
                viewModel.submitTLassignPrintSlip(
                    token = "Bearer $it",
                    baseUrl = baseUrl,
                    request = request
                )
            }}
            // Handle the submit action here
        }
        binding.btnSubmit.setOnClickListener {
            binding.TVtruckLoad.visibility=View.GONE
            binding.cardTlSpinner.visibility=View.VISIBLE
            binding.ly2.visibility = View.GONE
            binding.ly3.visibility = View.VISIBLE
        }
        binding.btnSave3.setOnClickListener {
            val vehicleNumber = binding.etVehicleNumber.text.toString()
            val driverName = binding.tvDriverName.text.toString()
            if (selectedLocationCode.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a Truck Loader", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (vehicleNumber.isBlank()) {
                Toast.makeText(this, "Vehicle number is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (elvCode.isNullOrBlank()) {
                Toast.makeText(this, "ElvCode is missing.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showConfirmationDialog("Confirm Assign", "Are you sure you want to update assignment this TL?" ,
                shouldFinish = true) {
            val request = AssignTlSubmitRequest(
                ElvCode = elvCode,
                VRN = vehicleNumber,
                TLCode = selectedLocationCode!!,

                )
            token?.let {
                viewModel.reAsssigedReassignPrintSlip(
                    token = "Bearer $it",
                    baseUrl = baseUrl,
                    request = request
                )
            }
        }}
        binding.btnprint.setOnClickListener {
            val vehicleNumber = binding.etVehicleNumber.text.toString()

            if (selectedLocationCode.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a Truck Loader", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (vehicleNumber.isBlank()) {
                Toast.makeText(this, "Vehicle number is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (elvCode.isNullOrBlank()) {
                Toast.makeText(this, "ElvCode is missing.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showConfirmationDialog("Confirm Reprint", "Are you sure you want to Reprint ?",shouldFinish = true) {
            val request = AssignTlSubmitRequest(ElvCode = elvCode,)
            token?.let {
                viewModel.reprintReassignPrintSlip(
                    token = "Bearer $it",
                    baseUrl = baseUrl,
                    request = request
                )
            }
        }}
    }
    private fun toggleButtons(enabled: Boolean) {
        binding.btnSubmit1.isEnabled = enabled
        binding.btnSubmit.isEnabled = enabled
        binding.btnprint.isEnabled = enabled

        // Optional: change button appearance when disabled
        val alpha = if (enabled) 1.0f else 0.5f
        binding.btnSubmit1.alpha = alpha
        binding.btnSubmit.alpha = alpha
        binding.btnprint.alpha = alpha
    }

    private fun fetchAssignmentDetails(elvCode: String) {

        token?.let { token ->
            viewModel.getAssigningDetail("Bearer $token", baseUrl, elvCode)

        }
    }
    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }

    override fun onResume() {
        super.onResume()

        token?.let { viewModel.getTruckLoaderLocations(it, baseUrl) }

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
    private fun showConfirmationDialog(
        title: String,
        message: String,
        shouldFinish: Boolean = false,
        onConfirm: () -> Unit
    ) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            onConfirm()
//            if (shouldFinish) {
//                finish()
//            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


    //printer implementation

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED)
        ) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("TAG", "${it.key} = ${it.value}")
        }
    }

    private suspend fun connectToPrinterDirectly() {
        withContext(Dispatchers.IO) {
            if(printerMac!="")
            {
                val connection = BluetoothLeConnection(printerMac, applicationContext)

                try {
                    connection.open()
                    if (connection.isConnected) {
                        //val zplConfig = "^XA^PW574^LL751^XZ"  // width = 2.83", length = 3.7"
                        //connection.write(zplConfig.toByteArray())
                        //   binding.indicator.setImageResource(R.drawable.ic_circl_green)
                        activeConnection = connection // Save globally if needed
                        withContext(Dispatchers.Main) {
                            binding.indicator.setImageResource(R.drawable.ic_circl_green)
                            showToast("Connected to Printer: ${printerMac}")
                        }
                    } else {
                        // binding.indicator.setImageResource(R.drawable.ic_circl_red)
                        withContext(Dispatchers.Main) {
                            binding.indicator.setImageResource(R.drawable.ic_circl_red)
                            showToast("Failed to connect to Printer")
                        }
                    }
                }
                catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showToast("Connection failed: ${e.message}")
                    }
                }
            }
            else{
                Toasty.error(this@TLFormPageActivity, "Printer Mac Not found..!", Toasty.LENGTH_SHORT).show()
            }

        }
    }

    private fun printZplInChunks(zsspl: String) {

        coroutineScope.launch {
            try {
                if (activeConnection?.isConnected == true) {
                    // activeConnection?.write(zsspl.toByteArray(Charsets.UTF_8))
                    printZplInChunks(zpl = zsspl)
                }

            } catch (e: Exception) {
                Log.e("TAG", "Chunked ZPL print error: ${e.message}")
            }
        }

    }

    private fun printZplInChunks(zpl: String, chunkSize: Int = 256) {
        coroutineScope.launch {
            try {
                val data = zpl.toByteArray(Charsets.UTF_8)
                var offset = 0
                while (offset < data.size) {
                    val end = (offset + chunkSize).coerceAtMost(data.size)
                    val chunk = data.copyOfRange(offset, end)
                    if (activeConnection?.isConnected == true) {
                        activeConnection?.write(chunk)
                    } else {
                        Log.e("TAG", "Printer connection is not active")
                        break
                    }
                    offset = end
                    delay(30) // Non-blocking delay
                }
            } catch (e: Exception) {
                Log.e("TAG", "Chunked ZPL print error: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Optional: Close connection manually when needed (e.g., on app exit)
    override fun onDestroy() {
        super.onDestroy()
        activeConnection?.close() // Close the active connection
        activeConnection = null // Reset the connection
    }
}
