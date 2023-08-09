package com.kierman.lufanalezaco.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityMainBinding
import com.kierman.lufanalezaco.util.*
import com.kierman.lufanalezaco.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    var mBluetoothAdapter: BluetoothAdapter? = null
    private var recv: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }

        initObserving()
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            viewModel.onClickConnect()
        }
    }

    private fun initObserving() {
        // Obserwowanie postępu
        viewModel.inProgress.observe(this) {
            if (it.getContentIfNotHandled() == true) {
                viewModel.inProgressView.set(true)
            } else {
                viewModel.inProgressView.set(false)
            }
        }

        // Obserwowanie stanu postępu
        viewModel.progressState.observe(this) {
            viewModel.txtProgress.set(it)
        }

        // Żądanie włączenia Bluetooth
        viewModel.requestBleOn.observe(this) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startForResult.launch(enableBtIntent)
        }

        // Zdarzenie połączenia/rozłączenia Bluetooth
        viewModel.connected.observe(this) {
            if (it != null) {
                if (it) {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(true)
                    Util.showNotification("Urządzenie zostało połączone.")
                } else {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(false)
                    Util.showNotification("Połączenie z urządzeniem zostało przerwane.")
                }
            }
        }

        // Błąd połączenia Bluetooth
        viewModel.connectError.observe(this) {
            Util.showNotification("Błąd połączenia. Proszę sprawdzić urządzenie.")
            viewModel.setInProgress(false)
        }

        // Odebranie danych
        viewModel.putTxt.observe(this) {
            if (it != null) {
                recv += it
                sv_read_data.fullScroll(View.FOCUS_DOWN)
                viewModel.txtRead.set(recv)
            }
        }
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, permission) }
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // Sprawdzanie uprawnień
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // Jeśli żądanie zostało anulowane, wynikowe tablice są puste.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Uprawnienia przyznane!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Uprawnienia muszą zostać przyznane", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.txtRead.set("tutaj można zobaczyć przychodzącą wiadomość")
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterReceiver()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        viewModel.setInProgress(false)
    }
}
