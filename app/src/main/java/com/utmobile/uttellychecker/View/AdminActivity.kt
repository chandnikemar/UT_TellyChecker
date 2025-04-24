package com.utmobile.uttellychecker

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.utmobile.uttellychecker.View.LoginActivity
import com.utmobile.uttellychecker.databinding.ActivityAdminBinding
import com.utmobile.uttellychecker.helper.Constants

import com.utmobile.uttellychecker.helper.SessionManager
import com.utmobile.uttellychecker.helper.Utils

class AdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminBinding
    private var builder: AlertDialog.Builder? = null
    private var alert: AlertDialog? = null
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null

    //private var portSharedPrefText: Int? = 0
    private lateinit var session: SessionManager
    private lateinit var user: HashMap<String, String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin)
        session = SessionManager(this)
        user = session.getUserDetails()
        serverIpSharedPrefText = user["serverIp"].toString()
        serverHttpPrefText = user["http"].toString()

        if (Utils.getSharedPrefs(this@AdminActivity, Constants.KEY_PRINTER_MAC).isNullOrEmpty()) {
            binding.edBluetoothMac.setText(
                "${
                    Utils.getSharedPrefs(
                        this@AdminActivity,
                        Constants.KEY_PRINTER_MAC
                    )
                }"
            )
        }


        binding.edServerIp.setText(serverIpSharedPrefText)
        if (serverHttpPrefText.toString() == "null") {
            binding.edHttp.setText("")
        } else {
            binding.edHttp.setText(serverHttpPrefText.toString())
        }
        binding.btnSave.setOnClickListener {
            val serverIp = binding.edServerIp.text.toString().trim()
            var edHttp = binding.edHttp.text.toString().trim()
            if (serverIp == "" || edHttp == "") {
                if (serverIp == "" && edHttp == "") {
                    Toast.makeText(
                        this,
                        "Please enter all values!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.edServerIp.error = "Please enter Domain Name"
                    //binding.edPort.error = "Please enter value"
                    binding.edHttp.error = "Please enter value"
                } else if (serverIp == "") {
                    Toast.makeText(this, "Please Enter ServerIP!!", Toast.LENGTH_SHORT)
                        .show()
                    binding.edServerIp.error = "Please enter Domain Name"
                } else if (edHttp == "") {
                    Toast.makeText(
                        this,
                        "Please Enter Http/Https Number!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                showDialog(serverIp, edHttp)

                //if (serverIp != serverIpSharedPref || portNumber != portSharedPrefText) {
                //}

            }

        }
        binding.btnUpdateBluetoothMac.setOnClickListener {
            updatePrinterMac()
        }
    }

    private fun updatePrinterMac() {
        try {
            var edPrinterMac = binding.edBluetoothMac.text.toString().trim()
            if(edPrinterMac == "" )
            {
                Toast.makeText(
                    this,
                    "Please enter all values!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
            {
                Utils.setSharedPrefs(this@AdminActivity, Constants.KEY_PRINTER_MAC, "${edPrinterMac}")
                Toast.makeText(
                    this,
                    "Updated Successful!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {

        }
    }

    private fun showDialog(
        serverIp: String?,
        http: String?,
    ) {
        builder = AlertDialog.Builder(this)
        builder!!.setMessage("Changes will take effect after Re-Login!")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                session.saveAdminDetails(serverIp, http)
                startActivity(Intent(this, LoginActivity::class.java))
                this@AdminActivity?.finishAffinity()
            }
            .setNegativeButton("No, Continue") { dialog: DialogInterface, id: Int ->
                dialog.cancel()
                binding.edServerIp.setText(serverIpSharedPrefText)
            }
        alert = builder!!.create()
        alert!!.show()
    }
}