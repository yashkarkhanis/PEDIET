package com.example.mobapplicationdev;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class itemDetected extends AppCompatActivity {
    int prev_calories,prev_carbs,prev_fat,prev_protien,food_calories,food_carbs,food_fat,food_protien,prev_water,tot_calories,tot_carbs,tot_fat,tot_protien;
    int currpointer=1;
    FirebaseAuth firebaseAuth;
    int started = 0;
    String item;

    DatabaseReference dbRoot,dbUserPrev,dbUpload,dbFoodDet;
    TextView titleSet,editTextCalCalc,editTextProCalc,editTextFatCalc,editTextCarbsCalc,currCounter;
    Button btnConfirm,plusQuant,minusQuant;
    String datetoday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detected);


        firebaseAuth = FirebaseAuth.getInstance();
        final String user_id = firebaseAuth.getCurrentUser().getUid();
        DateFormat dateformatter = new SimpleDateFormat("MMddyyyy");
        dateformatter.setLenient(false);
        Date today = new Date();
        datetoday= dateformatter.format(today);
        Bundle extras = getIntent().getExtras();
        if(extras==null){
            item = null;
        }
        else{
            item = (extras.getString("itemdetected")).toLowerCase();

        }

        titleSet = findViewById(R.id.foodFactsTitle);
        titleSet.setText("Food Facts for "+item.toUpperCase());

        editTextCalCalc = findViewById(R.id.CalorieCalc);
        editTextProCalc = findViewById(R.id.ProteinCalc);
        editTextCarbsCalc = findViewById(R.id.CarbsCalc);
        editTextFatCalc = findViewById(R.id.FatCalc);


        dbRoot = FirebaseDatabase.getInstance().getReference();
        dbRoot.addValueEventListener(new ValueEventListener() {

            FirebaseHealthdb details;
            FirebaseHealthdb userPrevDetail = new FirebaseHealthdb();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                details = dataSnapshot.child("FoodDetails").child(item).getValue(FirebaseHealthdb.class);
                // Do something with the retrieved data or Bruce Wayne
                if(details != null) {
                    food_calories = details.calories;
                    food_carbs = details.carbs;
                    food_fat = details.fat;
                    food_protien = details.protien;
                }
                else{
                    food_calories = 0;
                    food_carbs = 0;
                    food_fat = 0;
                    food_protien = 0;
                }
                tot_calories = food_calories;
                tot_protien = food_protien;
                tot_carbs = food_carbs;
                tot_fat = food_fat;

                editTextCalCalc.setText(""+tot_calories);
                editTextProCalc.setText(""+tot_protien);
                editTextCarbsCalc.setText(""+tot_carbs);
                editTextFatCalc.setText(""+tot_fat);

                userPrevDetail = dataSnapshot.child("users").child(user_id).child("userhealthinfo").child(datetoday).getValue(FirebaseHealthdb.class);

                prev_calories = userPrevDetail.calories;
                prev_protien = userPrevDetail.protien;
                prev_carbs = userPrevDetail.carbs;
                prev_fat = userPrevDetail.fat;
                prev_water = userPrevDetail.water;


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserListActivity", "Error occured");
                // Do something about the error
            }});






        btnConfirm = findViewById(R.id.CalcOkay);

        plusQuant = findViewById(R.id.AddQuantity);
        minusQuant = findViewById(R.id.SubQuantity);
        currCounter = findViewById(R.id.counterCurr);

        plusQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tot_calories = tot_calories+food_calories;
                tot_protien = tot_protien+food_protien;
                tot_carbs = tot_carbs+food_carbs;
                tot_fat = tot_fat+food_fat;
                currpointer = currpointer+1;
                currCounter.setText(""+currpointer);

                editTextCalCalc.setText(""+tot_calories);
                editTextProCalc.setText(""+tot_protien);
                editTextCarbsCalc.setText(""+tot_carbs);
                editTextFatCalc.setText(""+tot_fat);

            }
        });

        minusQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currpointer>1){
                    tot_calories = tot_calories-food_calories;
                    tot_protien = tot_protien-food_protien;
                    tot_carbs = tot_carbs-food_carbs;
                    tot_fat = tot_fat-food_fat;
                    currpointer = currpointer-1;
                    currCounter.setText(""+currpointer);


                    editTextCalCalc.setText(""+tot_calories);
                    editTextProCalc.setText(""+tot_protien);
                    editTextCarbsCalc.setText(""+tot_carbs);
                    editTextFatCalc.setText(""+tot_fat);

                }
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(itemDetected.this, "current outside "+tot_fat+" "+tot_calories+" "+tot_carbs+" "+tot_protien, Toast.LENGTH_SHORT).show();

                dbUpload = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userhealthinfo").child(datetoday);
                Map newUserHealthinfo = new HashMap();
                newUserHealthinfo.put("fat",tot_fat+prev_fat);
                newUserHealthinfo.put("calories",tot_calories+prev_calories);
                newUserHealthinfo.put("carbs",tot_carbs+prev_carbs);
                newUserHealthinfo.put("protien",tot_protien+prev_protien);
                newUserHealthinfo.put("water",prev_water);
                Toast.makeText(itemDetected.this, " "+tot_fat+" "+tot_calories+" "+tot_carbs+" "+tot_protien, Toast.LENGTH_SHORT).show();
                dbUpload.setValue(newUserHealthinfo);

                Intent intent = new Intent(itemDetected.this,MainActivity.class);
               startActivity(intent);
                Toast.makeText(itemDetected.this, "CALORIES HOME", Toast.LENGTH_SHORT).show();
            }
        });
    }
}