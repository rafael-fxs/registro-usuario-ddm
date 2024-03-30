package com.example.trocasenhalogin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var buttonSignIn: Button
    private var user: User = User("user", "1234");

    val resultLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
        if(it.resultCode == RESULT_OK){
            val json = it.data?.getStringExtra("user")
            user = json?.let { User.fromJson(it) }!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputUsername = findViewById(R.id.editTextUsername);
        inputPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignIn.setEnabled(false);
        inputUsername.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
        buttonSignIn.setOnClickListener{signIn()}
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val login = inputUsername.text.trim()
            val password = inputPassword.text.trim()
            buttonSignIn.setEnabled(login.isNotEmpty() && password.isNotEmpty())
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun signIn() {
        val login = inputUsername.text.trim().toString()
        val password = inputPassword.text.trim().toString()
        if (user.authenticate(login, password)) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            cleanFields()
            val intent = Intent(this,ChangeUserActivity::class.java)
            resultLaucher.launch(intent)
            return
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("The fields are incorrect")
            .setPositiveButton("Close") { _, _ ->
                cleanFields()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun cleanFields() {
        inputUsername.setText("");
        inputPassword.setText("");
    }
}

class User(private val username: String, private val password: String) : Serializable {
    fun authenticate(inputUsername: String, inputPassword: String): Boolean {
        return inputUsername == username && inputPassword == password
    }
    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): User {
            return Gson().fromJson(json, User::class.java)
        }
    }
}

