package in.kashewdevelopers.puzzlesolver;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Sudoku extends Activity {

    LinearLayout sudokuBoxes, inputPanel;
    Toast pressBackToast, selectBlockForInput, lessInput, invalidInput;
    Button clearButton;
    boolean backPressed;

    TextView[][] sudokuBoxesView;
    TextView selectedBlock;
    Drawable previousBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        sudokuBoxesView = new TextView[9][9];
        backPressed = false;
        selectedBlock = null;
        previousBackground = null;

        invalidInput = Toast.makeText(this, "Invalid Input. Check the entered numbers", Toast.LENGTH_LONG);
        pressBackToast = Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT);
        selectBlockForInput = Toast.makeText(this, "select any above block for input", Toast.LENGTH_SHORT);
        lessInput = Toast.makeText(this, "At Least 16 numbers should be entered", Toast.LENGTH_LONG);

        clearButton = findViewById(R.id.clearButton);
        sudokuBoxes = findViewById(R.id.sudokuBoxes);
        inputPanel = findViewById(R.id.inputPanel);
        createSudokuBoxes();

    }

    public void createSudokuBoxes() {

        // Get Screen Resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;

        sudokuBoxes.getLayoutParams().width = screenWidth - 50;
        sudokuBoxes.getLayoutParams().height = screenWidth - 50;

        for (int r = 0; r < 9; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

            for (int i = 0; i < 9; i++) {
                TextView box = new TextView(this);
                box.setTextSize(18);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(0, 0, 0, 0);
                box.setLayoutParams(layoutParams);
                box.setTypeface(null, Typeface.BOLD_ITALIC);

                // create shaded boxes or white boxes
                if (r < 3 || r > 5) {
                    if (i < 3 || i > 5) box.setBackgroundResource(R.drawable.shaded_box);
                    else box.setBackgroundResource(R.drawable.white_box);
                } else {
                    if (i < 3 || i > 5) box.setBackgroundResource(R.drawable.white_box);
                    else box.setBackgroundResource(R.drawable.shaded_box);
                }

                box.setGravity(Gravity.CENTER);
                box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sudokuBoxClicked(v);
                    }
                });
                row.addView(box);

                sudokuBoxesView[r][i] = box;
            }

            sudokuBoxes.addView(row);
        }


        inputPanel.getLayoutParams().width = screenWidth - 50;
        LinearLayout row = new LinearLayout(this);
        inputPanel.addView(row);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenWidth - 50) / 9);
        layoutParams.setMargins(0, 20, 0, 0);
        row.setLayoutParams(layoutParams);

        for (int i = 1; i <= 5; i++) {
            TextView box = new TextView(this);
            layoutParams = new LinearLayout.LayoutParams((screenWidth - 50) / 9, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, (i == 5) ? 0 : (screenWidth - 50) / 9, 0);
            box.setLayoutParams(layoutParams);
            box.setBackgroundResource(R.drawable.input_box);
            box.setText(String.valueOf(i));
            box.setTextSize(18);
            box.setTypeface(null, Typeface.BOLD);
            box.setGravity(Gravity.CENTER);
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputButtonClicked(v);
                }
            });
            row.addView(box);
        }

        row = new LinearLayout(this);
        inputPanel.addView(row);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenWidth - 50) / 9);
        layoutParams.setMargins(0, 10, 0, 0);
        row.setLayoutParams(layoutParams);

        for (int i = 6; i <= 9; i++) {
            TextView box = new TextView(this);
            layoutParams = new LinearLayout.LayoutParams((screenWidth - 50) / 9, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins((screenWidth - 50) / 9, 0, 0, 0);
            box.setLayoutParams(layoutParams);
            box.setBackgroundResource(R.drawable.input_box);
            box.setText(String.valueOf(i));
            box.setTypeface(null, Typeface.BOLD);
            box.setTextSize(18);
            box.setGravity(Gravity.CENTER);
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputButtonClicked(v);
                }
            });
            row.addView(box);
        }

    }

    public void sudokuBoxClicked(View v) {

        if (selectedBlock == null) {
            selectedBlock = (TextView) v;
            previousBackground = v.getBackground();
            v.setBackgroundResource(R.drawable.selected_box);
            clearButton.setText(R.string.clearBox);
        } else if (selectedBlock == v) {
            v.setBackground(previousBackground);
            selectedBlock = null;
            clearButton.setText(R.string.clearSudoku);
        } else {
            selectedBlock.setBackground(previousBackground);
            selectedBlock = (TextView) v;
            previousBackground = v.getBackground();
            v.setBackgroundResource(R.drawable.selected_box);
        }

    }

    public void inputButtonClicked(View v) {

        if (selectedBlock == null) {
            selectBlockForInput.show();
            return;
        }

        selectedBlock.setText(((TextView) v).getText());

    }

    public boolean validateInput(int[][] sudokuMatrix) {

        // Check if all rows are valid
        for (int row = 0; row < 9; row++) {

            Map<Integer, Integer> numbers = new HashMap<>();
            for (int i = 0; i < 9; i++) {
                if (sudokuMatrix[row][i] != 0 && numbers.containsKey(sudokuMatrix[row][i]))
                    return false;
                else
                    numbers.put(sudokuMatrix[row][i], 0);
            }
        }

        // Check if all columns are valid
        for (int col = 0; col < 9; col++) {

            Map<Integer, Integer> numbers = new HashMap<>();
            for (int i = 0; i < 9; i++) {
                if (sudokuMatrix[i][col] != 0 && numbers.containsKey(sudokuMatrix[i][col]))
                    return false;
                else
                    numbers.put(sudokuMatrix[i][col], 0);
            }
        }

        // Check if all sectors are valid
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                Map<Integer, Integer> numbers = new HashMap<>();
                for (int i = r * 3; i < (r * 3) + 3; i++) {
                    for (int j = c * 3; j < (c * 3) + 3; j++) {
                        if (sudokuMatrix[i][j] != 0 && numbers.containsKey(sudokuMatrix[i][j]))
                            return false;
                        else
                            numbers.put(sudokuMatrix[i][j], 0);
                    }
                }

            }
        }

        return true;

    }

    public void onSolveClick(View v) {

        // If a box is selected, unselect it
        if (selectedBlock != null) {
            sudokuBoxClicked(selectedBlock);
        }


        /*
            When user clicks the SOLVE button,
            read all the values inserted in the sudoku matrix
            and store in sudokuMatrix.

            if the input box is empty, insert 0

            if puzzle is solved, SOLVED = true, showOutput by changing
            the values of empty sudoku boxes.
         */

        int[][] sudokuMatrix = new int[9][9];
        int zeroCount = 0;

        // Traverse through each box & store its value in matrix
        for (int r = 0; r < sudokuBoxes.getChildCount(); r++) {

            LinearLayout row = (LinearLayout) sudokuBoxes.getChildAt(r);

            for (int c = 0; c < row.getChildCount(); c++) {

                TextView box = (TextView) row.getChildAt(c);
                sudokuMatrix[r][c] = (box.getText().length() != 0) ? (Integer.parseInt(box.getText().toString())) : 0;

                if (sudokuMatrix[r][c] == 0) zeroCount++;

            }

        }

        // Minimum 16 numbers are required to solve a sudoku
        if (81 - zeroCount < 16) {
            lessInput.show();
            return;
        }

        // Check if the input is valid
        if (!validateInput(sudokuMatrix)) {
            invalidInput.show();
        }

        /*
            send sudokuMatrix & position of the first box
            to the SOLVE method.
            Solve returns TRUE if puzzle solved,
            FALSE otherwise
         */

        solve(sudokuMatrix, new int[]{0, 0});

    }

    public boolean solve(int[][] matrix, int[] rowColumn) {

        /*
            This method take the matrix & the current position as input.

            if the current position is beyond the matrix, we have completed the puzzle

            if the current position already has a value not equal to zero, that position is already solved
            set "solved" to TRUE and recursively call "solve" for the next position

            if current position is empty, iterate through 1 - 9 to find correct value
            in each iteration, check if the "value" is valid for current position,
                if it is valid, assign it in the matrix & recursively call "solve" for next position
                if it is not valid, "value++" check next value

            when loop terminates, check if the puzzle is solved,
            if not, assign 0 to the matrix in the current position
         */

        boolean solved = false;

        // check if we have completed the puzzle
        if (rowColumn[0] > 8) {
            solved = true;
            return solved;
        } else if (matrix[rowColumn[0]][rowColumn[1]] != 0) {
            nextBox(rowColumn);
            solved = solve(matrix, rowColumn);
            return solved;
        }

        // Iterate from 1 to 9 to check every possible value in the current position
        int value = 1;

        // if solved == True, puzzle is solved, exit loop
        // if value >= 10, all possibilities have been checked, exit loop
        while (!solved && value < 10) {

            if (isNumberValid(matrix, rowColumn[0], rowColumn[1], value)) {

                // Assign solution in matrix
                matrix[rowColumn[0]][rowColumn[1]] = value;
                sudokuBoxesView[rowColumn[0]][rowColumn[1]].setText(String.valueOf(value));

                // Find position of next box
                int[] nextBoxPosition = {rowColumn[0], rowColumn[1]};
                nextBox(nextBoxPosition);

                // solve recursively for next position
                solved = solve(matrix, nextBoxPosition);

            }

            // Try next value for the current position
            value++;

        }

        // if the puzzle is not solved, assign 0 to current position box (previous value)
        if (!solved) {
            matrix[rowColumn[0]][rowColumn[1]] = 0;
            sudokuBoxesView[rowColumn[0]][rowColumn[1]].setText("");
        }

        return solved;

    }

    public void nextBox(int[] rowColumn) {

        // Find position of next box
        rowColumn[1]++;
        if (rowColumn[1] > 8) {
            rowColumn[1] = 0;
            rowColumn[0]++;
        }

    }

    public boolean isNumberValid(int[][] matrix, int row, int column, int target) {

        /*
        check if the given TARGET is valid for the given
        position (row & column) in the matrix.
         */

        return !numberExistInRow(matrix, row, target) &&
                !numberExistInColumn(matrix, column, target) &&
                !numberExistInSubGrid(matrix, row, column, target);

    }

    public boolean numberExistInRow(int[][] matrix, int row, int target) {

        for (int c = 0; c < 9; c++) {
            if (matrix[row][c] == target) return true;
        }

        return false;

    }

    public boolean numberExistInColumn(int[][] matrix, int column, int target) {

        for (int r = 0; r < 9; r++) {
            if (matrix[r][column] == target) return true;
        }

        return false;
    }

    public boolean numberExistInSubGrid(int[][] matrix, int row, int column, int target) {

        int row_start = (row / 3) * 3;
        int col_start = (column / 3) * 3;

        for (int i = row_start; i < (row_start + 3); i++) {
            for (int j = col_start; j < (col_start + 3); j++) {
                if (matrix[i][j] == target) return true;
            }
        }

        return false;
    }

    public void onClearClick(View v) {

        if (selectedBlock != null) {
            selectedBlock.setText("");
            return;
        }

        for (int i = 0; i < sudokuBoxes.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) sudokuBoxes.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView box = (TextView) row.getChildAt(j);
                box.setText(R.string.blank);
            }
        }

    }

    @Override
    public void onBackPressed() {

        if (backPressed) super.onBackPressed();

        backPressed = true;
        pressBackToast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressed = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        lessInput.cancel();
        pressBackToast.cancel();
        selectBlockForInput.cancel();
        invalidInput.cancel();
        super.onDestroy();
    }

}
