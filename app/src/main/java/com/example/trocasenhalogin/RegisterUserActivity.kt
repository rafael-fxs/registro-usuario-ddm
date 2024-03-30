package com.example.trocasenhalogin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson

class RegisterUserActivity : AppCompatActivity() {
    private lateinit var inputRegisterUsername: EditText
    private lateinit var inputRegisterPassword: EditText
    private lateinit var inputRegisterRepeatPassword: EditText
    private lateinit var spinnerTypeUserRegister: Spinner;
    private lateinit var buttonCreate: Button
    private var userList: MutableList<User> = mutableListOf()
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputRegisterUsername = findViewById(R.id.editTextRegisterUsername);
        inputRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        inputRegisterRepeatPassword = findViewById(R.id.editTextRegisterRepeatPassword);
        spinnerTypeUserRegister = findViewById(R.id.spinnerTypeUser);

        val itensSpinner = intent.getStringArrayListExtra("itensSpinner")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, itensSpinner!!.toArray()).apply{
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerTypeUserRegister.adapter = adapterSpinner;
        spinnerTypeUserRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(this@RegisterUserActivity,
                    "Selected: ${itensSpinner[position]}",
                    Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        userList = Gson().fromJson(intent.getStringExtra("userList"), Array<User>::class.java).toMutableList()
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setEnabled(false);
        inputRegisterUsername.addTextChangedListener(textWatcher);
        inputRegisterPassword.addTextChangedListener(textWatcher);
        inputRegisterRepeatPassword.addTextChangedListener(textWatcher);
        buttonCreate.setOnClickListener{createUser()}

    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val login = inputRegisterUsername.text.trim().toString()
            val password = inputRegisterPassword.text.trim().toString()
            val passwordRepeated = inputRegisterRepeatPassword.text.trim().toString()
            buttonCreate.setEnabled(login.isNotBlank() && password.isNotBlank() && passwordRepeated.isNotBlank()
                    && (password == passwordRepeated))
        }
        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun createUser() {
        val login = inputRegisterUsername.text.trim().toString()
        val password = inputRegisterPassword.text.trim().toString()
        val passwordRepeated = inputRegisterRepeatPassword.text.trim().toString()
        val typeUser = spinnerTypeUserRegister.selectedItem.toString()
        val userFind = userList.find { it.username == login.lowercase() }

        if (userFind != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("User already registered with this username")
                .setPositiveButton("Close") { _, _ ->
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
            return
        }


        if (password == passwordRepeated) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            user = User(login.lowercase(), password, typeUser)
            val intent = Intent()
            intent.putExtra("user", user.toJson())
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}