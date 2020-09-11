package in.kashewdevelopers.puzzlesolver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    boolean backPressed = false;
    Toast backPressedToast;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressedToast = Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT);
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


    // handle widget clicks
    public void goFigureClicked(View v) {
        startActivity(new Intent(this, GoFigure.class));
    }

    public void sudokuClicked(View v) {
        startActivity(new Intent(this, Sudoku.class));
    }

}
