package com.example.pr20_aubovsergey_pr_21102;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnSave, btnRead;
    EditText etName, etSurname, etPatronymic, etAge, etHeight, etWeight;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView lvInfo;
    private ArrayList<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRead = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(v -> {readData();});
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {saveData();});

        etSurname = findViewById(R.id.etSurname);
        etName = findViewById(R.id.etName);
        etPatronymic = findViewById(R.id.etPatronymic);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);

        readData();
    }

    private void saveData(){

        HashMap<String, Object> user = new HashMap<String, Object>();

        user.put("surname", etSurname.getText().toString());
        user.put("name", etName.getText().toString());
        user.put("patronymic", etPatronymic.getText().toString());
        user.put("age", etAge.getText().toString());
        user.put("height", etHeight.getText().toString());
        user.put("weight", etWeight.getText().toString());

        etSurname.getText().clear();
        etName.getText().clear();
        etPatronymic.getText().clear();
        etAge.getText().clear();
        etHeight.getText().clear();
        etWeight.getText().clear();

        db.collection("users")

                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        readData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        readData();
                    }
                });

    }

    private void readData(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>)document.getData();
                                userList.add(data);
                            }
                            String[] from = {"surname", "name", "patronymic", "age", "height", "weight"};
                            int[] to = {R.id.tvSurname, R.id.tvName, R.id.tvPatronymic, R.id.tvAge, R.id.tvHeight, R.id.tvWeight};

                            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, userList, R.layout.item, from, to);

                            lvInfo = (ListView) findViewById(R.id.lvInfo);
                            lvInfo.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}