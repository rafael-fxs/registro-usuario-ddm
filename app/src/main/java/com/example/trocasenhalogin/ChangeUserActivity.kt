package com.example.trocasenhalogin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChangeUserActivity : AppCompatActivity() {
    private lateinit var inputNewUsername: EditText
    private lateinit var inputNewPassword: EditText
    private lateinit var buttonChange: Button
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputNewUsername = findViewById(R.id.editTextRegisterUsername);
        inputNewPassword = findViewById(R.id.editTextRegisterPassword);
        buttonChange = findViewById(R.id.buttonCreate);
        buttonChange.setEnabled(false);
        inputNewUsername.addTextChangedListener(textWatcher);
        inputNewPassword.addTextChangedListener(textWatcher);
        buttonChange.setOnClickListener{changeUser()}
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val login = inputNewUsername.text.trim()
            val password = inputNewPassword.text.trim()
            buttonChange.setEnabled(login.isNotEmpty() && password.isNotEmpty())
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun changeUser() {
        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
        user = User(inputNewUsername.text.trim().toString().lowercase(), inputNewPassword.text.trim().toString(), "")
        val intent = Intent()
        intent.putExtra("user", user.toJson())
        setResult(RESULT_OK,intent)
        finish()
    }
}