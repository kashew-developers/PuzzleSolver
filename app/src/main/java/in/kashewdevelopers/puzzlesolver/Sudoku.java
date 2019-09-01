package in.kashewdevelopers.puzzlesolver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Sudoku extends AppCompatActivity {

    LinearLayout sudokuBoxLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        sudokuBoxLayout = findViewById(R.id.sudokuBoxes);
        createSudokuBoxes();

    }

    public void createSudokuBoxes() {

        for (int r = 0; r < 9; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < 9; i++) {
                EditText box = new EditText(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                layoutParams.setMargins(0, 0, 0, 0);
                box.setLayoutParams(layoutParams);

                if( r < 3 || r > 5 ){
                    if( i < 3 || i > 5 ) box.setBackgroundResource(R.drawable.shaded_box);
                    else box.setBackgroundResource(R.drawable.white_box);
                }
                else {
                    if( i < 3 || i > 5 ) box.setBackgroundResource(R.drawable.white_box);
                    else box.setBackgroundResource(R.drawable.shaded_box);
                }

                box.setGravity(Gravity.CENTER);
                box.setInputType(InputType.TYPE_CLASS_NUMBER);
                row.addView(box);
            }

            sudokuBoxLayout.addView(row);
        }
    }


}
