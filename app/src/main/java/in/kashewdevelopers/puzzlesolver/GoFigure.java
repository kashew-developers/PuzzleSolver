package in.kashewdevelopers.puzzlesolver;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Stack;

public class GoFigure extends AppCompatActivity {

    EditText input_1, input_2, input_3, input_4, answer;
    TextView operator_1, operator_2, operator_3;
    Button solve_button;
    Toast incomplete_input, cannot_solve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_figure);

        // Initialize Valiables
        input_1 = findViewById(R.id.box1);
        input_2 = findViewById(R.id.box2);
        input_3 = findViewById(R.id.box3);
        input_4 = findViewById(R.id.box4);

        answer = findViewById(R.id.box5);

        operator_1 = findViewById(R.id.operator1);
        operator_2 = findViewById(R.id.operator2);
        operator_3 = findViewById(R.id.operator3);

        solve_button = findViewById(R.id.solveButton);

        incomplete_input = Toast.makeText(this, "Enter all values", Toast.LENGTH_SHORT);
        cannot_solve = Toast.makeText(this, "This set of numbers cannot be solved", Toast.LENGTH_SHORT);

    }

    public int getOperatorPriority(String operator){

        switch( operator ){
            case "+":
            case "-": return 0;
            case "*":
            case "/": return 1;
        }

        return 0;
    }

    public boolean evaluateEquation(String[] equation, int target){

        Stack<String> result = new Stack<>();
        Stack<String> operators = new Stack<>();

        for(int i = 0; i < equation.length; i++){

            if( i % 2 == 0 ){
                result.push(equation[i]);
            }
            else {

                while ( ! operators.empty() && getOperatorPriority(operators.peek()) >= getOperatorPriority(equation[i])) {

                    int value2 = Integer.parseInt(result.pop());
                    int value1 = Integer.parseInt(result.pop());
                    String previous_operator = operators.pop();

                    if (previous_operator.equals("+")) value1 += value2;
                    else if (previous_operator.equals("-")) value1 -= value2;
                    else if (previous_operator.equals("*")) value1 *= value2;
                    else if (previous_operator.equals("/")) try {value1 /= value2;} catch (Exception e){ value1 = 0;}

                    result.push(String.valueOf(value1));
                }
                operators.push(equation[i]);

            }

        }

        while( ! operators.empty() ){

            int value2 = Integer.parseInt(result.pop());
            int value1 = Integer.parseInt(result.pop());
            String previous_operator = operators.pop();

            if( previous_operator.equals("+") ) value1 += value2;
            else if( previous_operator.equals("-") ) value1 -= value2;
            else if( previous_operator.equals("*") ) value1 *= value2;
            else if( previous_operator.equals("/") ) try {value1 /= value2;} catch (Exception e){ value1 = 0;}

            result.push( String.valueOf(value1) );
        }

        return target == Integer.parseInt( result.peek() );
    }

    public void incrementOperators(int[] oprators){

        int i = 2;
        oprators[i]++;
        while ( i > 0 && oprators[i] > 3 ){
            oprators[i] = 0;
            oprators[--i]++;
        }

    }

    public void onSolveClick(View v){

        // Check if all required inputs (5 values) are given
        // if not, show toast & exit function
        if( input_1.getText().toString().equals("") || input_2.getText().toString().equals("") ||
                input_3.getText().toString().equals("") || input_4.getText().toString().equals("") ||
                answer.getText().toString().equals("") ){
            incomplete_input.show();
            return;
        }

        String[] equation = {input_1.getText().toString(), "+", input_2.getText().toString(), "+",
                                input_3.getText().toString(), "+", input_4.getText().toString()};
        String[] operatorMapping = {"+", "-", "*", "/"};
        int[] operators = {0, 0, 0};
        int target = Integer.parseInt( answer.getText().toString() );
        boolean solved = false;

        while( operators[0] <= 3 ){

            equation[1] = operatorMapping[ operators[0] ];
            equation[3] = operatorMapping[ operators[1] ];
            equation[5] = operatorMapping[ operators[2] ];

            if( evaluateEquation(equation, target) ) {
                operator_1.setText( equation[1] );
                operator_2.setText( equation[3] );
                operator_3.setText( equation[5] );
                solved = true;
                break;
            }

            incrementOperators(operators);

        }

        if( ! solved ){
            cannot_solve.show();
        }

    }

    public void onClearClick(View v){

        input_1.setText(R.string.blank);
        input_2.setText(R.string.blank);
        input_3.setText(R.string.blank);
        input_4.setText(R.string.blank);

        answer.setText(R.string.blank);

        operator_1.setText(R.string.blank);
        operator_2.setText(R.string.blank);
        operator_3.setText(R.string.blank);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        incomplete_input.cancel();
        cannot_solve.cancel();
    }
}
