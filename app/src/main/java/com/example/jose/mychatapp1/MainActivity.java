package com.example.jose.mychatapp1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jose.mychatapp1.Model.ChatMessage;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class MainActivity extends AppCompatActivity {

    private static  int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter adapter;
    RelativeLayout activity_main;

    //add emojicon
    EmojiconEditText emojiconEditText;
    ImageView emojibutton, submitButton;
    EmojIconActions emojiconActions;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Successfully sign it, Welcome!" , Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            } else {
                Snackbar.make(activity_main, "Logout wrong, please try again!" , Snackbar.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main, "SignOut ", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity_main = findViewById(R.id.activity_main);

        //add Emoji
        emojibutton = findViewById(R.id.emoji_button);
        submitButton = findViewById(R.id.send_button);
        emojiconEditText =  findViewById(R.id.emojicon_edit_text);
        emojiconActions = new EmojIconActions(getApplicationContext(), activity_main, emojibutton, emojiconEditText);
        emojiconActions.ShowEmojicon();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("OldChat1").push().setValue(new ChatMessage(
                        emojiconEditText.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                //Clear message when send
                emojiconEditText.setText("");
                emojiconEditText.requestFocus();
            }
        });


        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(activity_main, "Welcome:  " + FirebaseAuth.getInstance()
                    .getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            //load content
            displayChatMessage();
        }
        
    }

    private void displayChatMessage() {
        ListView listOfMessage = findViewById(R.id.list_of_message);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item,  FirebaseDatabase.getInstance().getReference().child("OldChat1")) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageUser, messageTime, messageText;

                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageText = (BubbleTextView)v.findViewById(R.id.message_text);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageUser.setText(model.getMessageUser());
                messageText.setText(model.getMessageText());
                messageTime.setText(android.text.format.DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessagetime()));
            }
        };
        listOfMessage.setAdapter(adapter);
    }
}




























