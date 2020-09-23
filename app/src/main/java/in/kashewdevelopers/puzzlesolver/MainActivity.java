package in.kashewdevelopers.puzzlesolver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    boolean backPressed = false;
    Toast backPressedToast;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isDarkThemeSelected()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setContentView(R.layout.activity_main);

        applyAds();

        backPressedToast = Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        boolean darkModeChecked = AppCompatDelegate
                .getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        menu.getItem(0).setChecked(darkModeChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.darkMode) {
            item.setChecked(!item.isChecked());
            if (item.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            saveTheme(item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
            return;
        }

        backPressed = true;
        backPressedToast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        backPressedToast.cancel();
        super.onDestroy();
    }


    // handle shared preference
    public boolean isDarkThemeSelected() {
        SharedPreferences sharedPreferences = getSharedPreferences("themeInfo", MODE_PRIVATE);
        return sharedPreferences.getBoolean("darkTheme", false);
    }

    public void saveTheme(boolean darkTheme) {
        SharedPreferences sharedPreferences = getSharedPreferences("themeInfo", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("darkTheme", darkTheme).apply();
    }


    // handle widget clicks
    public void goFigureClicked(View v) {
        startActivity(new Intent(this, GoFigure.class));
    }

    public void sudokuClicked(View v) {
        startActivity(new Intent(this, Sudoku.class));
    }


    // handle ads
    public void applyAds() {
        MobileAds.initialize(this);

        AdView adUnit = findViewById(R.id.adUnit);
        adUnit.loadAd(new AdRequest.Builder().build());
    }

}
