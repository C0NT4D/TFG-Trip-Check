package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.network.ChatMessage
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter

    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar: Toolbar = findViewById(R.id.toolbar_chat)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Asistente de Viaje"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        recyclerView = findViewById(R.id.recycler_view_chat)
        editText = findViewById(R.id.edit_text_chat)
        sendButton = findViewById(R.id.btn_send_chat)

        setupRecyclerView()

        sendButton.setOnClickListener {
            val userMessage = editText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                sendMessage(userMessage)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter
    }

    private fun sendMessage(messageText: String) {
        val userMessage = ChatMessage(messageText, true)
        chatAdapter.addMessage(userMessage)
        recyclerView.scrollToPosition(messages.size - 1)
        editText.text.clear()

        lifecycleScope.launch {
            try {
                val requestBody = mapOf("message" to messageText)
                val response = RetrofitClient.myBackendService.askChatbot(requestBody)
                val botResponseText = response["answer"] ?: "No he podido obtener una respuesta."

                val botMessage = ChatMessage(botResponseText, false)
                chatAdapter.addMessage(botMessage)
                recyclerView.scrollToPosition(messages.size - 1)

            } catch (e: Exception) {
                val errorMessage = "Error de conexión: ${e.message}"
                chatAdapter.addMessage(ChatMessage(errorMessage, false))
                recyclerView.scrollToPosition(messages.size - 1)
                Toast.makeText(this@ChatActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
