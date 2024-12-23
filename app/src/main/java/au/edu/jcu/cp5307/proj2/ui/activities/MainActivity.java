package au.edu.jcu.cp5307.proj2.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import au.edu.jcu.cp5307.proj2.R;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button settingsButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        setupListeners();
    }

    private void initFields() {
        startButton = findViewById(R.id.main_start_button);
        settingsButton = findViewById(R.id.main_settings_button);
        exitButton = findViewById(R.id.main_exit_button);
    }

    private void setupListeners() {
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
        exitButton.setOnClickListener(v -> finishAffinity());
        startButton.setOnClickListener(v -> {
            Intent startGameIntent = new Intent(this, GameActivity.class);
            startActivity(startGameIntent);
        });
    }
}