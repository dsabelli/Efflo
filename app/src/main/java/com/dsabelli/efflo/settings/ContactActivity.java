package com.dsabelli.efflo.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dsabelli.efflo.R;

public class ContactActivity extends AppCompatActivity {
    private static final String CONTACT_EMAIL = "sabellid23@mytru.ca";
    boolean isSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        EditText subjectEditView = findViewById(R.id.subject_editText);
        EditText bodyEditView = findViewById(R.id.body_editText);
        Button sendBtn = findViewById(R.id.contact_send_btn);

        setEditTextColor(subjectEditView, R.color.white);
        setEditTextColor(bodyEditView, R.color.white);

        sendBtn.setOnClickListener(view -> sendEmail(subjectEditView, bodyEditView));

        handleBack();
    }

    // Method to set text color for EditText
    private void setEditTextColor(EditText editText, int colorResId) {
        editText.setTextColor(ContextCompat.getColor(this, colorResId));
    }

    // Method to handle sending an email
    private void sendEmail(EditText subjectEditView, EditText bodyEditView) {
        String emailSubject = subjectEditView.getText().toString().trim();
        String emailBody = bodyEditView.getText().toString().trim();
        if (!emailSubject.isEmpty() && !emailBody.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{CONTACT_EMAIL});
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            intent.putExtra(Intent.EXTRA_TEXT, emailBody);
            intent.setType("message/rfc822");
            try {
                isSent=true;
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ContactActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ContactActivity.this, "Subject and Body cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSent)finish();
    }

    // Method to handle back press
    public void handleBack() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Finish the activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
