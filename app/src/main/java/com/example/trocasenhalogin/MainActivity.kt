package com.example.trocasenhalogin

import android.app.AlertDialog
import android.content.Context
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
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var buttonSignIn: Button
    private lateinit var buttonRegisterUser: Button
    private var userList: MutableList<User> = mutableListOf(User("user", "1234", "Adm"))
    private val itensSpinner = arrayListOf("User","Adm","Mkt")
    private var userAunteticated: User? = null;

    private val changeUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
        if(it.resultCode == RESULT_OK){
            val json = it.data?.getStringExtra("user")
            val newUser = json?.let { User.fromJson(it) }!!
            userAunteticated!!.username = newUser.username;
            userAunteticated!!.password = newUser.password;
        }
    }

    private val registerUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
        if(it.resultCode == RESULT_OK){
            val json = it.data?.getStringExtra("user")
            val newUser = json?.let { User.fromJson(it) }!!
            userList.add(newUser)
            saveContentToFile(this, Gson().toJson(userList),"userList.txt")
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
        buttonRegisterUser = findViewById(R.id.buttonRegister)
        inputUsername.addTextChangedListener(textWatcher);
        inputPassword.addTextChangedListener(textWatcher);
        buttonSignIn.setOnClickListener{signIn()}
        buttonRegisterUser.setOnClickListener{registerUser()}
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
        userAunteticated = null;
        val login = inputUsername.text.trim().toString()
        val password = inputPassword.text.trim().toString()
        val userFind = userList.find { it.username == login && it.password == password }
        if (userFind != null) {
            userAunteticated = userFind;
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            cleanFields()
            val intent = Intent(this,ChangeUserActivity::class.java)
            changeUserLauncher.launch(intent)
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

    private fun registerUser() {
        val intent = Intent(this,RegisterUserActivity::class.java)
        intent.putStringArrayListExtra("itensSpinner", itensSpinner)
        intent.putExtra("userList", Gson().toJson(userList))
        registerUserLauncher.launch(intent)
        return
    }

    private fun cleanFields() {
        inputUsername.setText("");
        inputPassword.setText("");
    }

    private fun saveContentToFile(context: Context, content: String, filename: String){
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }
    }
}
