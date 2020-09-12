package in.kashewdevelopers.puzzlesolver;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class Sudoku extends AppCompatActivity {

    LinearLayout grid, inputPanel;
    Button clearButton;
    boolean backPressed;

    Toast pressBackToast, selectBlockForInputToast, lessInputToast, invalidInputToast;

    TextView[][] sudokuBoxesView;
    TextView selectedBlock;
    Drawable previousBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        initialize();
        createUiElements();
        addBackButton();
    }

    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
            return;
        }

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
        lessInputToast.cancel();
        pressBackToast.cancel();
        selectBlockForInputToast.cancel();
        invalidInputToast.cancel();
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

        sudokuBoxesView = new TextView[9][9];
        backPressed = false;
        selectedBlock = null;
        previousBackground = null;
    }

    public void initializeWidgets() {
        clearButton = findViewById(R.id.clearButton);
        grid = findViewById(R.id.sudokuBoxes);
        inputPanel = findViewById(R.id.inputPanel);
    }

    @SuppressLint("ShowToast")
    public void initializeToasts() {
        invalidInputToast = Toast.makeText(this, R.string.invalid_sudoku_input, Toast.LENGTH_LONG);
        invalidInputToast.setGravity(Gravity.CENTER, 0, 0);

        pressBackToast = Toast.makeText(this, R.string.back_press, Toast.LENGTH_SHORT);
        pressBackToast.setGravity(Gravity.CENTER, 0, 0);

        selectBlockForInputToast = Toast.makeText(this, R.string.sudoku_select_block, Toast.LENGTH_SHORT);
        selectBlockForInputToast.setGravity(Gravity.CENTER, 0, 0);

        lessInputToast = Toast.makeText(this, R.string.sudoku_select_min_blocks, Toast.LENGTH_LONG);
        lessInputToast.setGravity(Gravity.CENTER, 0, 0);
    }


    // create UI elements
    public void createUiElements() {
        // Get Screen Resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x;

        createSudokuBoxes(screenWidth);
        createInputPanel(screenWidth);
    }

    public void createSudokuBoxes(int screenWidth) {
        grid.getLayoutParams().width = screenWidth - 50;
        grid.getLayoutParams().height = screenWidth - 50;

        for (int r = 0; r < 9; r++) {
            // create row
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    0, 1));

            for (int i = 0; i < 9; i++) {
                TextView box = new TextView(this);
                box.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                box.setTypeface(null, Typeface.BOLD_ITALIC);
                box.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(0, 0, 0, 0);
                box.setLayoutParams(layoutParams);

                // create shaded boxes or white boxes
                int resourceId;
                if (r < 3 || r > 5) {
                    resourceId = (i < 3 || i > 5) ? R.drawable.shaded_box : R.drawable.white_box;
                } else {
                    resourceId = (i < 3 || i > 5) ? R.drawable.white_box : R.drawable.shaded_box;
                }
                box.setBackgroundResource(resourceId);

                box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sudokuBoxClicked(v);
                    }
                });
                row.addView(box);
                sudokuBoxesView[r][i] = box;
            }
            grid.addView(row);
        }
    }

    public void createInputPanel(int screenWidth) {
        // top row (1 - 5)
        inputPanel.getLayoutParams().width = screenWidth - 50;
        LinearLayout row = new LinearLayout(this);
        inputPanel.addView(row);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (screenWidth - 50) / 9);
        layoutParams.setMargins(0, 20, 0, 0);
        row.setLayoutParams(layoutParams);

        for (int i = 1; i <= 5; i++) {
            layoutParams = new LinearLayout.LayoutParams((screenWidth - 50) / 9, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, (i == 5) ? 0 : (screenWidth - 50) / 9, 0);
            TextView box = getInputPanelElement(i, layoutParams);
            row.addView(box);
        }

        // bottom row (6 - 9)
        row = new LinearLayout(this);
        inputPanel.addView(row);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenWidth - 50) / 9);
        layoutParams.setMargins(0, 10, 0, 0);
        row.setLayoutParams(layoutParams);

        for (int i = 6; i <= 9; i++) {
            layoutParams = new LinearLayout.LayoutParams((screenWidth - 50) / 9, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins((screenWidth - 50) / 9, 0, 0, 0);
            TextView box = getInputPanelElement(i, layoutParams);
            row.addView(box);
        }
    }

    public TextView getInputPanelElement(int value, LinearLayout.LayoutParams layoutParams) {
        TextView box = new TextView(this);
        box.setBackgroundResource(R.drawable.input_box);
        box.setText(String.valueOf(value));
        box.setTypeface(null, Typeface.BOLD);
        box.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        box.setGravity(Gravity.CENTER);
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputButtonClicked(v);
            }
        });
        box.setLayoutParams(layoutParams);
        return box;
    }


    // functionality
    public void addBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean validateInput(int[][] sudokuMatrix) {
        Set<Integer> uniqueValues = new HashSet<>();

        // Check if all rows are valid
        for (int row = 0; row < 9; row++) {
            for (int i = 0; i < 9; i++) {
                if (sudokuMatrix[row][i] != 0 && uniqueValues.contains(sudokuMatrix[row][i]))
                    return false;
                else
                    uniqueValues.add(sudokuMatrix[row][i]);
            }
            uniqueValues.clear();
        }

        // Check if all columns are valid
        for (int col = 0; col < 9; col++) {
            for (int i = 0; i < 9; i++) {
                if (sudokuMatrix[i][col] != 0 && uniqueValues.contains(sudokuMatrix[i][col]))
                    return false;
                else
                    uniqueValues.add(sudokuMatrix[i][col]);
            }
            uniqueValues.clear();
        }

        // Check if all sectors are valid
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                for (int i = r * 3; i < (r * 3) + 3; i++) {
                    for (int j = c * 3; j < (c * 3) + 3; j++) {
                        if (sudokuMatrix[i][j] != 0 && uniqueValues.contains(sudokuMatrix[i][j]))
                            return false;
                        else
                            uniqueValues.add(sudokuMatrix[i][j]);
                    }
                }
                uniqueValues.clear();
            }
        }

        return true;
    }

    public boolean solve(int[][] matrix, int[] position) {
        /*
         * This method take the matrix & the current position as input.
         *
         * if the current position is beyond the matrix, we have completed the puzzle
         *
         * if the current position already has a value not equal to zero, that position is already
         * solved set "solved" to TRUE and recursively call "solve" for the next position
         *
         * if current position is empty, iterate through 1 - 9 to find correct value
         * in each iteration, check if the "value" is valid for current position,
         *    if it is valid, assign it in the matrix & recursively call "solve" for next position
         *    if it is not valid, "value++" check next value
         *
         * when loop terminates, check if the puzzle is solved,
         * if not, assign 0 to the matrix in the current position
         */
        boolean solved = false;

        // check if we have completed the puzzle
        if (position[0] > 8) {
            return true;
        } else if (matrix[position[0]][position[1]] != 0) {
            nextBox(position);
            return solve(matrix, position);
        }

        // Iterate from 1 to 9 to check every possible value in the current position
        int value = 1;

        // if solved == True, puzzle is solved, exit loop
        // if value >= 10, all possibilities have been checked, exit loop
        while (!solved && value < 10) {

            if (isNumberValid(matrix, position[0], position[1], value)) {

                // Assign solution in matrix
                matrix[position[0]][position[1]] = value;
                sudokuBoxesView[position[0]][position[1]].setText(String.valueOf(value));

                // Find position of next box
                int[] nextBoxPosition = {position[0], position[1]};
                nextBox(nextBoxPosition);

                // solve recursively for next position
                solved = solve(matrix, nextBoxPosition);
            }

            // Try next value for the current position
            value++;
        }

        // if the puzzle is not solved, assign 0 to current position box (previous value)
        if (!solved) {
            matrix[position[0]][position[1]] = 0;
            sudokuBoxesView[position[0]][position[1]].setText("");
        }

        return solved;
    }

    public void nextBox(int[] position) {
        // Find position of next box
        position[1]++;
        if (position[1] > 8) {
            position[1] = 0;
            position[0]++;
        }
    }

    public boolean isNumberValid(int[][] matrix, int row, int column, int target) {
        /*
         * check if the given TARGET is valid for the given
         * position (row & column) in the matrix.
         */
        return !numberExistInRow(matrix, row, target) &&
                !numberExistInColumn(matrix, column, target) &&
                !numberExistInSubGrid(matrix, row, column, target);
    }

    public boolean numberExistInRow(int[][] matrix, int row, int target) {
        for (int c = 0; c < 9; c++) {
            if (matrix[row][c] == target)
                return true;
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
                if (matrix[i][j] == target)
                    return true;
            }
        }
        return false;
    }


    // handle widget clicks
    public void onClearClicked(View v) {
        // clear only the selected block
        if (selectedBlock != null) {
            selectedBlock.setText("");
            return;
        }

        // clear the entire grid
        for (int i = 0; i < grid.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) grid.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView box = (TextView) row.getChildAt(j);
                box.setText(R.string.blank);
            }
        }
    }

    public void sudokuBoxClicked(View v) {
        if (selectedBlock == null) {
            // new block selected, highlight it
            selectedBlock = (TextView) v;
            previousBackground = v.getBackground();
            v.setBackgroundResource(R.drawable.selected_box);
            clearButton.setText(R.string.clearBox);
        } else if (selectedBlock == v) {
            // clicked the same block, un-highlight and unselect
            v.setBackground(previousBackground);
            selectedBlock = null;
            clearButton.setText(R.string.clearSudoku);
        } else {
            // selected a different block,
            // un-highlight previous block
            // select current clicked block
            selectedBlock.setBackground(previousBackground);
            selectedBlock = (TextView) v;
            previousBackground = v.getBackground();
            v.setBackgroundResource(R.drawable.selected_box);
        }
    }

    public void inputButtonClicked(View v) {
        // if no block is selected, show toast
        if (selectedBlock == null) {
            selectBlockForInputToast.show();
            return;
        }

        // set clicked value to block
        selectedBlock.setText(((TextView) v).getText());
    }

    public void onSolveClicked(View v) {
        // If a box is selected, unselect it
        if (selectedBlock != null) {
            sudokuBoxClicked(selectedBlock);
        }

        /*
         * When user clicks the SOLVE button,
         * read all the values inserted in the sudoku matrix
         * and store in sudokuMatrix.
         *
         * if the input box is empty, insert 0
         *
         * if puzzle is solved, SOLVED = true, showOutput by changing
         * the values of empty sudoku boxes.
         */
        int[][] sudokuMatrix = new int[9][9];
        int zeroCount = 0;

        // Traverse through each box & store its value in matrix
        for (int r = 0; r < grid.getChildCount(); r++) {
            LinearLayout row = (LinearLayout) grid.getChildAt(r);
            for (int c = 0; c < row.getChildCount(); c++) {
                TextView box = (TextView) row.getChildAt(c);
                sudokuMatrix[r][c] = (box.getText().length() != 0) ? (Integer.parseInt(box.getText().toString())) : 0;
                if (sudokuMatrix[r][c] == 0)
                    zeroCount++;
            }
        }

        // Minimum 16 numbers are required to solve a sudoku
        if (81 - zeroCount < 16) {
            lessInputToast.show();
            return;
        }

        // Check if the input is valid
        if (!validateInput(sudokuMatrix)) {
            invalidInputToast.show();
        }

        /*
         * send sudokuMatrix & position of the first box
         * to the SOLVE method.
         * Solve returns TRUE if puzzle solved,
         * FALSE otherwise
         */
        solve(sudokuMatrix, new int[]{0, 0});
    }

}