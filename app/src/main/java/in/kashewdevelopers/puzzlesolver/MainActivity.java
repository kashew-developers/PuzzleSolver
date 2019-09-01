package in.kashewdevelopers.puzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CardView goFigureCard, sudokuCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goFigureCard = findViewById(R.id.goFigureCard);
        sudokuCard = findViewById(R.id.sudokuCard);

        goFigureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GoFigure.class));
            }
        });

    }
}
