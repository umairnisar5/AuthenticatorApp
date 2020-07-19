package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    TextView fullName,email,phone,balance;
    Button DepositBtn, creditBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    EditText editTextAmount;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.profilePhone);
        email = findViewById(R.id.profileEmail);
        fullName = findViewById(R.id.profileName);
        editTextAmount = findViewById(R.id.editTextAmount);
        balance = findViewById(R.id.balance);
        DepositBtn = findViewById(R.id.depositBtn);
        creditBtn = findViewById(R.id.creditBtn);


        fAuth  = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                        phone.setText(document.getString("phone"));
                        email.setText(document.getString("email"));
                        fullName.setText(document.getString("fName"));

                    } else {
                        Log.d("TAG", "No such document");
                        Toast.makeText(MainActivity.this, "No such document", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    Toast.makeText(MainActivity.this, "Error happened", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void getOldBalance(final String type) {
        DocumentReference documentReference = fStore.collection("users").document(userId);
        final String enterAmount = editTextAmount.getText().toString();
        if (enterAmount.isEmpty()){
            Toast.makeText(this, "Please Enter Your Amount", Toast.LENGTH_SHORT).show();
        } else {
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.i("TAG", "DocumentSnapshot data: " + document.getData());
                            int myOldBalance = Integer.parseInt(document.getString("balance"));
                            if (type == "+") {
                                updateBalanceFirebase(myOldBalance);
                            } else {
                                updateMinusBalance(myOldBalance);
                            }
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }


    private void updateBalanceFirebase(int oldBalance) {
        DocumentReference documentReference = fStore.collection("users").document(userId);
        final String enterAmount = editTextAmount.getText().toString();
        int newAmount = Integer.parseInt(enterAmount);
        int result = oldBalance + newAmount;
        String resultString = Integer.toString(result);
        documentReference.update("balance", resultString);
        balance.setText(resultString);
    }

    private void updateMinusBalance(int oldBalance) {
        DocumentReference documentReference = fStore.collection("users").document(userId);
        final String enterAmount = editTextAmount.getText().toString();
        int newAmount = Integer.parseInt(enterAmount);
        int result = oldBalance - newAmount;
        String resultString = Integer.toString(result);
        documentReference.update("balance", resultString);
        balance.setText(resultString);
    }



    public void depositMoney (View view) {
        getOldBalance("+");
    }



    public void yourValue (View view) {
        getOldBalance("-");
    }
    

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();

    }
}