package com.example.readerapp.screen.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.model.MUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewmodel : ViewModel() {
    //  val loadingState= MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun SignInWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit
    ) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(
                            "FB",
                            "SignInWithEmailAndPassword yesssssssssss: ${task.result.toString()}"
                        )
                        //TODO("Take them home")
                        home()
                    } else {
                        Log.d("FB", "SignInWithEmailAndPassword: ${task.result.toString()}")
                    }
                }

        } catch (ex: Exception) {
            Log.d("TAG", "SignInWithEmailAndPassword: $ex.message")
        }


    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //ha @gmail.com
                    val displayname = task.result.user?.email?.split('@')?.get(0)
                    createUser(displayname)
                    home()
                } else {
                    Log.d("FB", "createUserWithEmailAndPassword: ${task.result.toString()}")
                }
                _loading.value = false
            }

        }
    }

    private fun createUser(displayname: String?) {

        val userId = auth.currentUser?.uid

        val user = MUser(
            userId = userId.toString(),
            displayName = displayname.toString(),
            avatarUrl = "",
            quote = "Life is Greate", profession = "Andorid Developer", id = null
        ).tomap()
//        user["user_Id"] = userId.toString()
//        user["displayname"] = displayname.toString()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }
}