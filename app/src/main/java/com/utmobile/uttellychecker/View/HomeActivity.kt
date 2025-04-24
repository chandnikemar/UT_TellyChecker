package com.utmobile.uttellychecker.View

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.utmobile.uttellychecker.R
import com.utmobile.uttellychecker.databinding.ActivityHomeBinding
import com.utmobile.uttellychecker.helper.Constants
import com.utmobile.uttellychecker.helper.Utils


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setSupportActionBar(binding.TPToolBar)
        supportActionBar?.title = "Menu"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.mcvAssignTl.setOnClickListener {

            val intent = Intent(this, AssigntlActivity::class.java)
            startActivity(intent)
        }

        binding.mcvAssignedTl.setOnClickListener {

            val intent = Intent(this, AssignedtlActivity::class.java)
            startActivity(intent)
        }
        val isAdmin = Utils.getSharedPrefs(this@HomeActivity, Constants.KEY_IS_ADMIN)

        if (isAdmin == "true") {
//            binding.cvAdminSetting.visibility = View.VISIBLE
        }
        binding.ivUserDetails.setOnClickListener {
            showLogoutPopup()
        }
//        var AssigntlList =binding.
//        binding..setOnClickListener {
//
//            val intent = Intent(this, MenuActivity::class.java)
//            startActivity(intent)
//        }
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//              MenuActivity()
//                finish() // or navigateTo(HomeActivity::class.java)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
    fun Activity.navigateTo(targetActivity: Class<out Activity>) {
        startActivity(Intent(this, targetActivity))
    }
    fun showLogoutPopup() {
        val builder: AlertDialog.Builder
        val alert: AlertDialog
        builder = AlertDialog.Builder(this@HomeActivity)
        builder.setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog: DialogInterface?, id: Int ->
                Utils.setSharedPrefs(this@HomeActivity, Constants.KEY_IS_ADMIN, "")
                Utils.setSharedPrefs(this@HomeActivity, Constants.KEY_USER_NAME, "")
                Utils.setSharedPrefs(this@HomeActivity, Constants.KEY_JWT_TOKEN, "")
                Utils.setSharedPrefsBoolean(this@HomeActivity, Constants.KEY_IS_LOGGED_IN, false)
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        alert = builder.create()
        alert.show()
    }
}