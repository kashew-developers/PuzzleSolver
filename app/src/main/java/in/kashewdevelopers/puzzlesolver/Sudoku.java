package in.kashewdevelopers.puzzlesolver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Sudoku extends Activity {

    LinearLayout sudokuBoxLayout;
    Toast cannotSolveToast, pressBackToast;
    boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        sudokuBoxLayout = findViewById(R.id.sudokuBoxes);
        createSudokuBoxes();

        cannotSolveToast = Toast.makeText(this, "This cannot be solved", Toast.LENGTH_SHORT);
        pressBackToast = Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT);

    }

    public void createSudokuBoxes() {

        /*
        Sudoku contains 81 boxes / positions (9 x 9 matrix)
        Adding each individual box using XML is tedious process
        and modification is also difficult, it is better to dynamically
        add all the required boxes.
         */

        for (int r = 0; r < 9; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int i = 0; i < 9; i++) {
                EditText box = new EditText(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                layoutParams.setMargins(0, 0, 0, 0);
                box.setLayoutParams(layoutParams);

                // create shaded boxes or white boxes
                if (r < 3 || r > 5) {
                    if (i < 3 || i > 5) box.setBackgroundResource(R.drawable.shaded_box);
                    else box.setBackgroundResource(R.drawable.white_box);
                } else {
                    if (i < 3 || i > 5) box.setBackgroundResource(R.drawable.white_box);
                    else box.setBackgroundResource(R.drawable.shaded_box);
                }

                box.setGravity(Gravity.CENTER);
                box.setInputType(InputType.TYPE_CLASS_NUMBER);

                // Close keyboard after taking input
                final EditText temp = box;
                box.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (s.toString().length() > 0) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(temp.getWindowToken(), 0);
                        }
                    }
                });

                row.addView(box);
            }

            sudokuBoxLayout.addView(row);
        }
    }

    public void onSolveClick(View v) {

        /*

        When user clicks the SOLVE button,
        read all the values inserted in the sudoku matrix
        and store in an int[][] named sudokuMatrix.

        if the input box is empty, insert 0
        else convert string input to integer and assign to matrix

        if user inserts a value that is less than 1 or greater than 9,
        (sudoku is valid only for the range [1,9]), assign 0 to the matrix


        if puzzle is solved, SOLVED = true, showOutput by changing
        the values of other sudoku boxes.
        Else show a toast that it cannot be solved.

         */

        int[][] sudokuMatrix = new int[9][9];
        boolean solved = false;

        // Traverse through each box & store its value in matrix
        for (int r = 0; r < sudokuBoxLayout.getChildCount(); r++) {

            LinearLayout row = (LinearLayout) sudokuBoxLayout.getChildAt(r);

            for (int c = 0; c < row.getChildCount(); c++) {

                EditText box = (EditText) row.getChildAt(c);
                int value = (box.getText().length() != 0) ? (Integer.parseInt(box.getText().toString())) : 0;
                value = (value > 0 && value < 10) ? value : 0;
                sudokuMatrix[r][c] = value;

            }

        }

        /*
        send input matrix & position of the first box
        to the SOLVE method.
        Solve returns TRUE if puzzle solved,
        FALSE otherwise
         */
        solved = solve(sudokuMatrix, new int[]{0, 0});

        // if puzzle is solved, show solution, else show toast
        if( solved ) showSolution(sudokuMatrix);
        else cannotSolveToast.show();

    }

    public void showSolution(int[][] matrix){

        /*
        If the puzzle is solved, take matrix (solution)
        and assign values to all the boxes in the sudoku
         */

        for(int i = 0; i < 9; i++){
            LinearLayout row = (LinearLayout) sudokuBoxLayout.getChildAt(i);
            for(int j = 0; j < 9; j++){
                EditText box = (EditText) row.getChildAt(j);
                box.setText( String.valueOf(matrix[i][j]) );
            }
        }
    }

    public boolean solve(int[][] matrix, int[] rowColumn) {

        /*
        This method take the matrix & the current position as input.

        if the current position already has a value not equal to zero, that position is already solved
        set "solved" to TRUE and recursively call "solve" for the next position

        if the current position is beyond the matrix, we have completed the puzzle

        if current position is empty, iterate through 1 - 9 to find correct value
        in each iteration, check if the "value" is valid for current position,
            if it is valid, assign it in the matrix & recursively call "solve" for next position
            if it is not valid, "value++" check next value

        when loop terminates, check if the puzzle is solved,
        if not, assign 0 to the matrix in the current position
         */

        boolean solved = false;

        // check if we have completed the puzzle
        if( rowColumn[0] > 8 ){
            solved = true;
        }
        else if( matrix[rowColumn[0]][rowColumn[1]] != 0 ) {
            nextBox(rowColumn);
            solved = solve(matrix, rowColumn);
        }

        // Iterate from 1 to 9 to check every possible value in the current position
        int value = 1;

        // if solved == True, puzzle is solved, exit loop
        // if value >= 10, all possibilities have been checked, exit loop
        while (!solved && value < 10) {

            if (isNumberValid(matrix, rowColumn[0], rowColumn[1], value)) {

                // Assign solution in matrix
                matrix[rowColumn[0]][rowColumn[1]] = value;

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
        if( ! solved ){
            matrix[rowColumn[0]][rowColumn[1]] = 0;
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
        if (!numberExistInRow(matrix, row, target) &&
                !numberExistInColumn(matrix, column, target) &&
                !numberExistInSubGrid(matrix, row, column, target)) {

            return true;
        }

        return false;

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

        for (int i = 0; i < sudokuBoxLayout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) sudokuBoxLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                EditText box = (EditText) row.getChildAt(j);
                box.setText(R.string.blank);
            }
        }

    }

    @Override
    public void onBackPressed() {

        if( backPressed ) super.onBackPressed();

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
        pressBackToast.cancel();
        cannotSolveToast.cancel();
        super.onDestroy();
    }
}
