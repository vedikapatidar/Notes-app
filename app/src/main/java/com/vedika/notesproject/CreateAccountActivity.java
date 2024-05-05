package com.vedika.notesproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar ProgressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirmpassword_edit_text);
        createAccountBtn = findViewById(R.id.create_account_button);
        ProgressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_button);

        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> finish());


    }

    void createAccount(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }

        createAccountInFirebase(email,password);

    }
    void createAccountInFirebase(String email,String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            Utility.showToast(CreateAccountActivity.this,"Succesfully Account is created");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());

                        }

                    }
                }
        );
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            ProgressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else{
            ProgressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email,String password,String confirmPassword)
    {

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is Invalid");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Password length must be greater than 6");
            return false;
        }
        if(!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Entered password is not same check it once");
            return false;
        }
        return true;

    }
}