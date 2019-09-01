package in.kashewdevelopers.puzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CardView goFigureCard, sudokuCard;
    boolean backPressed = false;
    Toast backPressedToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goFigureCard = findViewById(R.id.goFigureCard);
        sudokuCard = findViewById(R.id.sudokuCard);
        backPressedToast = Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT);

        goFigureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GoFigure.class));
            }
        });

        sudokuCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Sudoku.class));
            }
        });

    }

    @Override
    public void onBackPressed() {

        if( backPressed ) super.onBackPressed();

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
}
