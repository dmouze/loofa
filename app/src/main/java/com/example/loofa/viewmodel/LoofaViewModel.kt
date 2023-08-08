package com.example.loofa.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.loofa.Repository
import com.example.loofa.util.*
import java.nio.charset.Charset

class LoofaViewModel(private val repository: Repository): ViewModel() {

    val connected: LiveData<Boolean?>
        get() = repository.connected
    val progressState: LiveData<String>
        get() = repository.progressState
    var btnConnected = ObservableBoolean(false)

    var inProgressView = ObservableBoolean(false)
    var txtProgress: ObservableField<String> = ObservableField("")

    private val _requestBleOn = MutableLiveData<Event<Boolean>>()
    val requestBleOn: LiveData<Event<Boolean>>
        get() = _requestBleOn

    val inProgress: LiveData<Event<Boolean>>
        get() = repository.inProgress

    val connectError: LiveData<Event<Boolean>>
        get() = repository.connectError

    val txtRead: ObservableField<String> = ObservableField("")
    val putTxt: LiveData<String>
        get() = repository.putTxt

    fun setInProgress(en: Boolean){
        repository.inProgress.value = Event(en)
    }
    fun onClickConnect(){
        if(connected.value==false || connected.value == null){
            if (repository.isBluetoothSupport()) {   // Sprawdzenie czy Bluetooth jest obsługiwany
                if(repository.isBluetoothEnabled()){ // Sprawdzenie czy Bluetooth jest włączony
                    // Pasek postępu
                    setInProgress(true)
                    // Rozpoczęcie skanowania urządzeń
                    repository.scanDevice()
                }else{
                    // Jeśli Bluetooth jest obsługiwany, ale nie jest włączony
                    // Wywołanie prośby o włączenie Bluetooth
                    _requestBleOn.value = Event(true)
                }
            }
            else{ // Jeśli Bluetooth nie jest obsługiwany
                Util.showNotification("Bluetooth nie jest obsługiwany.")
            }
        }else{
            repository.disconnect()
        }
    }

    fun unregisterReceiver(){
        repository.unregisterReceiver()
    }

    fun onClickSendData(sendTxt: String){
        val byteArr = sendTxt.toByteArray(Charset.defaultCharset())
        repository.sendByteData(byteArr)
        Util.showNotification("Wysłano dane!")
    }
}
