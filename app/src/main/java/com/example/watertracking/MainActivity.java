package com.example.watertracking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etWaterInput, etGoal;
    Button btnAdd, btnRestart, btnSetGoal;
    TextView tvTotalIntake, tvWaterIntakeList;
    int totalIntake = 0, dailyGoal = 0;
    StringBuilder waterIntakeList = new StringBuilder();
    File waterIntakeFile;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etWaterInput = findViewById(R.id.etWaterInput);
        etGoal = findViewById(R.id.etGoal);
        btnAdd = findViewById(R.id.btnAdd);
        btnRestart = findViewById(R.id.btnRestart);
        btnSetGoal = findViewById(R.id.btnSetGoal);
        tvTotalIntake = findViewById(R.id.tvTotalIntake);
        tvWaterIntakeList = findViewById(R.id.tvWaterIntakeList);

        waterIntakeFile = new File(getFilesDir(), "water_intake_data.txt");
        sharedPreferences = getSharedPreferences("WaterTrackingPrefs", MODE_PRIVATE);

        loadWaterIntakeData();
        loadGoal();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etWaterInput.getText().toString();
                if (!input.isEmpty()) {
                    int intake = Integer.parseInt(input);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String date = sdf.format(new Date());

                    waterIntakeList.append("Added on ").append(date).append(": ").append(intake).append(" ml\n");

                    totalIntake += intake;
                    updateTotalIntakeUI();

                    etWaterInput.setText("");

                    saveWaterIntakeData();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartWaterTracking();
            }
        });

        btnSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalInput = etGoal.getText().toString();
                if (!goalInput.isEmpty()) {
                    dailyGoal = Integer.parseInt(goalInput);
                    saveGoal();
                    updateTotalIntakeUI();
                    Toast.makeText(MainActivity.this, "Goal set to " + dailyGoal + " ml", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid goal", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadWaterIntakeData() {
        if (waterIntakeFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(waterIntakeFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if (parts.length > 1) {
                        String intakeStr = parts[1].split(" ")[0];
                        int intake = Integer.parseInt(intakeStr);
                        totalIntake += intake;
                    }
                    waterIntakeList.append(line).append("\n");
                }
                reader.close();
                updateTotalIntakeUI();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveWaterIntakeData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(waterIntakeFile, false));
            writer.write(waterIntakeList.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartWaterTracking() {
        totalIntake = 0;
        waterIntakeList.setLength(0);
        tvWaterIntakeList.setText("Water Intake List: ");
        updateTotalIntakeUI();

        if (waterIntakeFile.exists()) {
            waterIntakeFile.delete();
        }

        Toast.makeText(this, "All data has been reset", Toast.LENGTH_SHORT).show();
    }

    private void loadGoal() {
        dailyGoal = sharedPreferences.getInt("dailyGoal", 0);
        updateTotalIntakeUI();
    }

    private void saveGoal() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("dailyGoal", dailyGoal);
        editor.apply();
    }

    private void updateTotalIntakeUI() {
        tvTotalIntake.setText("Total Water Intake: " + totalIntake + " ml / Goal: " + dailyGoal + " ml");
        tvWaterIntakeList.setText("Water Intake List:\n" + waterIntakeList.toString());
    }
}
