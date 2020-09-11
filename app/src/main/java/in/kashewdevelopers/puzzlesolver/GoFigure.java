package in.kashewdevelopers.puzzlesolver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class GoFigure extends AppCompatActivity {

    EditText input_1, input_2, input_3, input_4, answer;
    TextView operator_1, operator_2, operator_3;
    ProgressBar progressBar;
    Switch bodmasSwitch;
    Toast incompleteInput, cannotSolveToast, backPressedToast;
    boolean backPressed = false, useBodmasRule = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_figure);

        initialize();
        addBackButton();
    }

    @Override
    public void onBackPressed() {
        if (backPressed)
            super.onBackPressed();

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
        incompleteInput.cancel();
        cannotSolveToast.cancel();
        backPressedToast.cancel();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


     // initialization
    public void initialize() {
        initializeWidgets();
        initializeToasts();
    }

    public void initializeWidgets() {
        input_1 = findViewById(R.id.box1);
        input_2 = findViewById(R.id.box2);
        input_3 = findViewById(R.id.box3);
        input_4 = findViewById(R.id.box4);

        answer = findViewById(R.id.box5);

        operator_1 = findViewById(R.id.operator1);
        operator_2 = findViewById(R.id.operator2);
        operator_3 = findViewById(R.id.operator3);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        bodmasSwitch = findViewById(R.id.bodmasToggle);
    }

    @SuppressLint("ShowToast")
    public void initializeToasts() {
        incompleteInput = Toast.makeText(this, R.string.boxes_empty, Toast.LENGTH_SHORT);
        incompleteInput.setGravity(Gravity.CENTER, 0, 0);

        cannotSolveToast = Toast.makeText(this, R.string.cannot_solve_go_figure, Toast.LENGTH_LONG);
        cannotSolveToast.setGravity(Gravity.CENTER, 0, 0);

        backPressedToast = Toast.makeText(this, R.string.back_press, Toast.LENGTH_SHORT);
        backPressedToast.setGravity(Gravity.CENTER, 0, 0);
    }

    public void addBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    // functionality
    public int getOperatorPriority(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 0;
            case "*":
            case "/":
                return 1;
        }
        return 0;
    }

    public void incrementOperators(int[] operators) {
        int i = 2;
        operators[i]++;
        while (i > 0 && operators[i] > 3) {
            operators[i] = 0;
            operators[--i]++;
        }
    }

    public void findSolution() {
        String[] equation = {input_1.getText().toString(), "+", input_2.getText().toString(), "+",
                input_3.getText().toString(), "+", input_4.getText().toString()};
        String[] operatorMapping = {"+", "-", "*", "/"};
        int[] operators = {0, 0, 0};
        int target = Integer.parseInt(answer.getText().toString());
        boolean solved = false;

        while (operators[0] <= 3) {
            equation[1] = operatorMapping[operators[0]];
            equation[3] = operatorMapping[operators[1]];
            equation[5] = operatorMapping[operators[2]];

            if (evaluateEquation(equation, target)) {
                operator_1.setText(equation[1]);
                operator_2.setText(equation[3]);
                operator_3.setText(equation[5]);
                solved = true;
                break;
            }

            incrementOperators(operators);
        }

        if (!solved) {
            cannotSolveToast.show();
        }
    }

    public boolean evaluateEquation(String[] equation, int target) {
        if (useBodmasRule) {
            return solveWithBodmas(equation) == target;
        } else {
            return solveWithoutBodmas(equation) == target;
        }
    }

    public int solveWithBodmas(String[] equation) {
        Stack<String> result = new Stack<>();
        Stack<String> operators = new Stack<>();
        int value1, value2;

        for (int i = 0; i < equation.length; i++) {
            if (i % 2 == 0) {
                result.push(equation[i]);
            } else {
                while (!operators.empty() &&
                        getOperatorPriority(operators.peek()) >= getOperatorPriority(equation[i])) {
                    value2 = Integer.parseInt(result.pop());
                    value1 = Integer.parseInt(result.pop());

                    value1 = solve(value1, operators.pop(), value2);
                    result.push(String.valueOf(value1));
                }
                operators.push(equation[i]);
            }
        }

        while (!operators.empty()) {
            value2 = Integer.parseInt(result.pop());
            value1 = Integer.parseInt(result.pop());

            value1 = solve(value1, operators.pop(), value2);
            result.push(String.valueOf(value1));
        }

        return Integer.parseInt(result.peek());
    }

    public int solveWithoutBodmas(String[] equation) {
        int ans = Integer.parseInt(equation[0]);
        for (int i = 1; i < 6; i += 2) {
            ans = solve(ans, equation[i], Integer.parseInt(equation[i + 1]));
        }
        return ans;
    }

    public int solve(int num1, String operator, int num2) {
        switch (operator) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                return (num2 != 0) ? num1 / num2 : 0;
        }
        return 0;
    }


    // handle widget clicks
    public void onBodmasClicked(View v) {
        useBodmasRule = bodmasSwitch.isChecked();
    }

    public void onClearClicked(View v) {
        input_1.setText(R.string.blank);
        input_2.setText(R.string.blank);
        input_3.setText(R.string.blank);
        input_4.setText(R.string.blank);

        answer.setText(R.string.blank);

        operator_1.setText(R.string.blank);
        operator_2.setText(R.string.blank);
        operator_3.setText(R.string.blank);
    }

    public void onSolveClicked(View v) {
        // Check if all required inputs (5 values) are given
        // if not, show toast & exit
        if (input_1.getText().length() <= 0 || input_2.getText().length() <= 0 ||
                input_3.getText().length() <= 0 || input_4.getText().length() <= 0 ||
                answer.getText().length() <= 0) {
            incompleteInput.show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        incompleteInput.cancel();

        operator_1.setText(R.string.blank);
        operator_2.setText(R.string.blank);
        operator_3.setText(R.string.blank);

        findSolution();

        progressBar.setVisibility(View.INVISIBLE);
    }

}
