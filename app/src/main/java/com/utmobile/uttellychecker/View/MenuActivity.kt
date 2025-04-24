package com.utmobile.uttellychecker.View

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.utmobile.uttellychecker.R
import com.utmobile.uttellychecker.databinding.ActivityMenuBinding
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.Utils

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        binding.tvLogin.text = "Dashboard"

        supportActionBar?.title = "Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(false
        )
        supportActionBar?.setDisplayShowHomeEnabled(false)
        binding.TvMenu.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        val isAdmin = Utils.getSharedPrefs(this@MenuActivity, Constants.KEY_IS_ADMIN)
        if (isAdmin == "true") {
//            binding.cvAdminSetting.visibility = View.VISIBLE
        }
        binding.ivUserDetails.setOnClickListener {
            showLogoutPopup()
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
    fun showLogoutPopup() {
        val builder: AlertDialog.Builder
        val alert: AlertDialog
        builder = AlertDialog.Builder(this@MenuActivity)
        builder.setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                Utils.setSharedPrefs(this@MenuActivity, Constants.KEY_IS_ADMIN, "")
                Utils.setSharedPrefs(this@MenuActivity, Constants.KEY_USER_NAME, "")
                Utils.setSharedPrefs(this@MenuActivity, Constants.KEY_JWT_TOKEN, "")
                Utils.setSharedPrefsBoolean(this@MenuActivity, Constants.KEY_IS_LOGGED_IN, false)
                startActivity(Intent(this@MenuActivity , LoginActivity::class.java))
                finish()
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        alert = builder.create()
        alert.show()
    }


}
