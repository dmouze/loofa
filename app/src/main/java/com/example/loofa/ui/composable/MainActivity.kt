package com.example.loofa.ui.composable

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.loofa.ui.theme.LoofaTheme
import com.example.loofa.util.PERMISSIONS
import com.example.loofa.util.REQUEST_ALL_PERMISSION
import com.example.loofa.util.Util
import com.example.loofa.viewmodel.LoofaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<LoofaViewModel>()

    var mBluetoothAdapter: BluetoothAdapter? = null
    private var recv: String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onCreate(savedInstanceState)

        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }

        initObserving()

        setContent {
            LoofaTheme {
                LufaMainScreen(viewModel = viewModel)
            }
        }
    }


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.M)
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
                    Toast.makeText(this, "Uprawnienia muszą zostać przyznane", Toast.LENGTH_SHORT)
                        .show()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.setInProgress(false)
    }
}

@Composable
fun LufaMainScreen(viewModel: LoofaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { viewModel.onClickConnect() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp, top = 10.dp),
            colors = ButtonDefaults.buttonColors(
            )
        ) {
            Text(
                text = if (viewModel.btnConnected.get()) "Rozłącz" else "Połącz",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        var sendText by remember { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = sendText,
                onValueChange = { sendText = it },
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            )
            Button(
                onClick = { viewModel.onClickSendData(sendText) },
                enabled = viewModel.btnConnected.get(),
                modifier = Modifier.padding(start = 10.dp),
                colors = ButtonDefaults.buttonColors(
                )
            ) {
                Text(
                    text = "Wyślij dane",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(10.dp)
        ) {
            item {
                Text(
                    text = viewModel.txtRead.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (viewModel.inProgressView.get()) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = viewModel.txtProgress.toString(),
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
            }
        }
    }
}


