package com.example.mobapplicationdev;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.itangqi.waveloadingview.WaveLoadingView;

public class HomeFragment extends Fragment {
    int calories,carbs,fat,protien,water;
    String firstname,lastname,age,height;
    int homepagetot_calories,homepagetot_carbs,homepagetot_fat,homepagetot_protien,homepagetot_water;
    ProgressBar progressBarProtiens,progressBarFats,progressBarCalories,progressBarCarbs;
    WaveLoadingView waveLoadingViewWater;
    TextView edText;
    TextView welcomeUserName;
    Button BtnLogout;
    int[] onProgressStatus = {0,0,0,0,0};
    int counter;
    View mView;
    FirebaseAuth myFireAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_home, container,false);

        waveLoadingViewWater = (WaveLoadingView)v.findViewById(R.id.waveLoadingView);

        progressBarFats = (ProgressBar) v.findViewById(R.id.progressFats);

        progressBarProtiens = (ProgressBar) v.findViewById(R.id.progressBartrial);

        progressBarCarbs = (ProgressBar) v.findViewById(R.id.progressCarbo);

        progressBarCalories = (ProgressBar) v.findViewById(R.id.progressCalorie);



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myFireAuth = FirebaseAuth.getInstance();
        String user_id = myFireAuth.getCurrentUser().getUid();
        DatabaseReference databaseCoordinates2;
        databaseCoordinates2 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userinfo");
        databaseCoordinates2.addValueEventListener(new ValueEventListener() {
            FirebaseDb details = new FirebaseDb();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                details = dataSnapshot.getValue(FirebaseDb.class);
                // Do something with the retrieved data or Bruce Wayne
                firstname = details.firstname;
                lastname = details.lastname;
                age = details.age;
                height = details.height;

                homepagetot_calories = details.targetCalorie;
                homepagetot_carbs = details.targetCarbs;
                homepagetot_fat = details.targetFat;
                homepagetot_protien = details.targetProtein;
                homepagetot_water = details.targetWater;

                //Toast.makeText(getContext(), "totcal: "+homepagetot_calories+" totcarbs: "+homepagetot_carbs+" totprotien: "+homepagetot_protien, Toast.LENGTH_SHORT).show();


                welcomeUserName = getActivity().findViewById(R.id.welcome_user);
                welcomeUserName.setText("Welcome, "+firstname);

                DateFormat dateformatter = new SimpleDateFormat("MMddyyyy");
                dateformatter.setLenient(false);
                Date today = new Date();

                final String datetoday = dateformatter.format(today);
                DatabaseReference databaseCoordinates1;
                String user_id = myFireAuth.getCurrentUser().getUid();
                databaseCoordinates1 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userhealthinfo").child(datetoday);
                databaseCoordinates1.addValueEventListener(new ValueEventListener() {
                    FirebaseHealthdb details;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        details = dataSnapshot.getValue(FirebaseHealthdb.class);
                        // Do something with the retrieved data or Bruce Wayne
                        if(details==null){
                            calories=0;
                            carbs=0;
                            fat=0;
                            protien=0;
                            water=0;
                        }
                        else{
                            calories = details.calories;
                            carbs = details.carbs;
                            fat = details.fat;
                            protien = details.protien;
                            water = details.water;
                        }
                        int percentcalcwater;

//                        Toast.makeText(getContext(), "totcal+ "+homepagetot_calories+" calcurr "+calories, Toast.LENGTH_SHORT).show();


//                        Toast.makeText(getContext(), "totpro+ "+homepagetot_protien+" calcurr "+protien, Toast.LENGTH_SHORT).show();


//                        Toast.makeText(getContext(), "totcal+ "+homepagetot_fat+" calcurr "+fat, Toast.LENGTH_SHORT).show();

                        //water
                        percentcalcwater = ((water*100)/homepagetot_water);

                        waveLoadingViewWater = (WaveLoadingView)getActivity().findViewById(R.id.waveLoadingView);
                        waveLoadingViewWater.setProgressValue(percentcalcwater);
                        Toast.makeText(getContext(), " water count "+water+" percent :"+percentcalcwater, Toast.LENGTH_SHORT).show();

                        //fat
                        final int percentcalcfat = ((fat*100)/homepagetot_fat);

                        progressBarFats = (ProgressBar) getActivity().findViewById(R.id.progressFats);
//                        Toast.makeText(getContext(), "percFat "+percentcalcfat, Toast.LENGTH_SHORT).show();

                        final Handler mHandler = new Handler();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (onProgressStatus[0] <percentcalcfat){
                                    onProgressStatus[0]++;
                                    SystemClock.sleep(50);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
//                                            Toast.makeText(getContext(), "progressss", Toast.LENGTH_SHORT).show();
                                            progressBarFats.setProgress(onProgressStatus[0]);

                                        }
                                    });
                                }
                            }
                        }).start();


                        if(percentcalcfat >= 100){
                            progressBarFats.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        }

                        //protien
                        final int percentcalcprotien = ((protien*100)/homepagetot_protien);
                        progressBarProtiens = (ProgressBar) getActivity().findViewById(R.id.progressBartrial);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (onProgressStatus[1] <percentcalcprotien){
                                    onProgressStatus[1]++;
                                    android.os.SystemClock.sleep(50);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBarProtiens.setProgress(onProgressStatus[1]);
                                        }
                                    });
                                }
                            }
                        }).start();

                        if(percentcalcprotien >= 100){
                            progressBarProtiens.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        }

                        //carbs
                        final int percentcalccarbs = (carbs*100/homepagetot_carbs);
                        progressBarCarbs = (ProgressBar)getActivity().findViewById(R.id.progressCarbo);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (onProgressStatus[2] <percentcalccarbs){
                                    onProgressStatus[2]++;
                                    android.os.SystemClock.sleep(50);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBarCarbs.setProgress(onProgressStatus[2]);
                                        }
                                    });
                                }
                            }
                        }).start();
                        if(percentcalccarbs >= 100){

                            progressBarCarbs.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                        }
                        //calorie
                        final int percentcalccalorie = (calories*100/homepagetot_calories);
                        progressBarCalories = (ProgressBar)getActivity().findViewById(R.id.progressCalorie);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (onProgressStatus[3] <percentcalccalorie){
                                    onProgressStatus[3]++;
                                    android.os.SystemClock.sleep(50);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBarCalories.setProgress(onProgressStatus[3]);
                                        }
                                    });
                                }
                            }
                        }).start();

                        if(percentcalccalorie >= 100){

                            progressBarCalories.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                        }

                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        mView = getLayoutInflater().inflate(R.layout.dialog_water_input,null);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();


                        LinearLayout layoutWaterShowDialog = getActivity().findViewById(R.id.linearLayoutWater);
                        layoutWaterShowDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                edText = (TextView) mView.findViewById(R.id.dialogCountHolder);
                                Button dialogPlus = mView.findViewById(R.id.dialogPlus);
                                final Button dialogMinus = mView.findViewById(R.id.dialogMinus);
                                Button dialogSub = mView.findViewById(R.id.submitDialog);
                                counter = Integer.parseInt(edText.getText().toString());
                                FirebaseAuth firebaseAuth;
                                firebaseAuth = FirebaseAuth.getInstance();
                                String user_id = firebaseAuth.getCurrentUser().getUid();

                                DateFormat dateformatter = new SimpleDateFormat("MMddyyyy");
                                dateformatter.setLenient(false);
                                Date today = new Date();
                                final String datetoday = dateformatter.format(today);
                                DatabaseReference current_health_userdb = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userhealthinfo").child(datetoday);
                                current_health_userdb.addValueEventListener(new ValueEventListener() {
                                    FirebaseHealthdb detailscurr = new FirebaseHealthdb();
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        detailscurr = dataSnapshot.getValue(FirebaseHealthdb.class);
                                        if (details != null) {
                                            calories = details.calories;
                                            carbs = details.carbs;
                                            fat = details.fat;
                                            protien = details.protien;
                                            water = details.water;
                                        } else {
                                            calories = 0;
                                            carbs = 0;
                                            fat = 0;
                                            protien = 0;
                                            water = 0;
                                        }

                                        edText.setText(""+water);



                                    }



                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                dialogMinus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        counter = Integer.parseInt(edText.getText().toString());
                                        if(counter == 0){
                                            dialogMinus.setClickable(false);
                                        }
                                        if(counter>0){
                                            counter--;
                                            edText.setText(""+counter);
                                        }

                                    }
                                });


                                dialogPlus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        counter = Integer.parseInt(edText.getText().toString());


                                        counter++;
                                        edText.setText(""+counter);
                                        if(counter > 0){
                                            dialogMinus.setClickable(true);
                                        }
                                    }
                                });

                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                dialogSub.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        FirebaseHealthdb updatedWater = new FirebaseHealthdb();
                                        FirebaseAuth firebaseAuth;
                                        firebaseAuth = FirebaseAuth.getInstance();
                                        String user_id = firebaseAuth.getCurrentUser().getUid();
                                        water = Integer.parseInt(edText.getText().toString());
                                        updatedWater.calories = calories;
                                        updatedWater.protien = protien;
                                        updatedWater.carbs = carbs;
                                        updatedWater.fat = fat;
                                        updatedWater.water = water;
                                        DatabaseReference current_health_userdb1 = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userhealthinfo").child(datetoday);
                                        current_health_userdb1.setValue(updatedWater);
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("UserListActivity", "Error occured");
                        // Do something about the error
                    }});

                BtnLogout = getActivity().findViewById(R.id.logOut);
                BtnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        getActivity().getSupportFragmentManager().popBackStack();
                        Intent i = new Intent(getActivity(),SignUp.class);
                        startActivity(i);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserListActivity", "Error occured");
                // Do something about the error
            }});



    }
}
