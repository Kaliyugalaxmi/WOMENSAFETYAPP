package com.example.womensafetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatBot extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText userMessageEditText;
    private ImageButton sendButton;
    ImageView cancel;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userMessageEditText = findViewById(R.id.userMessageEditText);
        sendButton = findViewById(R.id.sendButton);
        cancel=findViewById(R.id.cancel);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
        sendButton.setOnClickListener(v -> {
            String userMessage = userMessageEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Add User message to chat
                chatMessages.add(new ChatMessage(userMessage, true));
                chatAdapter.notifyDataSetChanged();

                // Clear input field
                userMessageEditText.setText("");

                // Scroll to the bottom of the chat
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

                // Get Bot's response
                String botResponse = getBotResponse(userMessage);
                chatMessages.add(new ChatMessage(botResponse, false));
                chatAdapter.notifyDataSetChanged();

                // Scroll to the bottom of the chat
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatBot.this, Dashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }


    private String getBotResponse(String userMessage) {
        userMessage = userMessage.toLowerCase();  // To make keyword checking case-insensitive

        if (userMessage.contains("hi") || userMessage.contains("hello")) {
            return "Hello! How can I assist you today?";
        }
        if (userMessage.contains("how are you")) {
            return "I'm here to assist you. How can I make your day better?";
        }
        if (userMessage.contains("what is your purpose")) {
            return "My purpose is to help you feel safe, informed, and motivated. How can I assist you today?";
        }
        if (userMessage.contains("tell me something inspiring")) {
            return "You are stronger than you think, braver than you feel, and smarter than you know. Keep going!";
        }
        if (userMessage.contains("traveling alone")) {
            return "Always inform someone about your travel plans, avoid isolated areas, keep your phone charged, and have emergency contacts saved in your phone.";
        }
        if (userMessage.contains("unsafe in public")) {
            return "Stay in well-lit areas, move towards crowds, alert authorities if necessary, and use the SOS feature in the SheSafe app.";
        }
        if (userMessage.contains("online harassment")) {
            return "Avoid sharing personal information, use strong passwords, block and report suspicious accounts, and talk to someone you trust.";
        }
        if (userMessage.contains("self-defense techniques")) {
            return "Effective techniques include striking the attacker's eyes, nose, or groin. You can also use everyday objects like keys or pens for defense.";
        }
        if (userMessage.contains("everyday objects")) {
            return "Keys, pens, umbrellas, and even bags can be used as weapons to protect yourself. Check out the Techniques section of the app for more details.";
        }
        if (userMessage.contains("what to do in an emergency")) {
            return "Stay calm, find a safe place if possible, call for help using the SOS feature, and alert people around you.";
        }
        if (userMessage.contains("night travel safety tips")) {
            return "Avoid isolated areas, keep your phone charged, stay in well-lit areas, and inform someone about your travel plans.";
        }
        if (userMessage.contains("how does location feature work")) {
            return "The location feature allows you to send your current location to your emergency contacts instantly.";
        }
        if (userMessage.contains("how to add emergency contacts")) {
            return "Go to the Emergency Contacts section and select the contacts you want to add from your phone’s contact list.";
        }
        if (userMessage.contains("how to change password")) {
            return "You can change your password from the Settings menu by selecting 'Change Password'.";
        }
        if (userMessage.contains("i feel unsafe")) {
            return "I'm here to help you. Try to stay calm, reach out to your emergency contacts, or use the SOS feature if needed.";
        }
        if (userMessage.contains("i'm feeling scared")) {
            return "It's okay to feel scared. Take a deep breath, find a safe place, and remember you can always use the SOS feature for help.";
        }
        if (userMessage.contains("i feel alone") || userMessage.contains("i feel hopeless")) {
            return "You are not alone. Remember, strength comes from within, and help is always near. Keep going, and don't be afraid to reach out.";
        }
        if (userMessage.contains("give me strength") || userMessage.contains("encourage me") || userMessage.contains("give me motivation")) {
            return "You are powerful, resilient, and capable of overcoming anything. No obstacle is too big if you believe in yourself. Keep moving forward.";
        }
        if (userMessage.contains("i feel tired")) {
            return "It's okay to feel tired. Take a break, breathe, and remember you are doing great. Keep pushing forward!";
        }
        if (userMessage.contains("how to stay strong") || userMessage.contains("motivate me")) {
            return "Stay strong by believing in your own strength. Take things one step at a time and never lose hope. You’ve got this!";
        }
        if (userMessage.contains("tell me a joke")) {
            String[] jokes = {
                    "Why did the Java developer wear glasses? Because they couldn't C#! 😂",
                    "Why do programmers prefer dark mode? Because light attracts bugs! 😄",
                    "Why was the computer cold? Because it left its Windows open! 🥶",
                    "How many programmers does it take to change a light bulb? None. It's a hardware problem! 💡",
                    "Why did the programmer quit his job? Because he didn't get arrays. 😜",
                    "Why did the computer go to the doctor? Because it had a bad case of Windows XP! 😂",
                    "Why did the smartphone need glasses? Because it lost all its contacts! 📱🤣"
            };
            int randomIndex = new java.util.Random().nextInt(jokes.length);
            return jokes[randomIndex];
        }
        return "Sorry, I didn't understand that. Can you try again?";
    }
}
