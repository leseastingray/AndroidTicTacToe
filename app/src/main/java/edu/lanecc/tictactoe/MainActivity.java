package edu.lanecc.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements TextView.OnEditorActionListener {

    /********** Instance variables ***********/

    // Widget object references
    EditText playerOEditText;
    EditText playerXEditText;
    Button endButton;

    // Game logic object
    TicTacToeLogic game = new TicTacToeLogic();

    // Player names
    String playerNameO = "";
    String playerNameX = "";

    /********* Preference variables ********/
    private SharedPreferences prefs;
    private boolean darkMode = false;
    private boolean largeText = false;
    private boolean rememberNames = true;

    // Dark Mode Field
    public static final String DARK_MODE = "DARK_MODE";
    // Large Text Field
    public static final String LARGE_TEXT = "LARGE_TEXT";



    /************ Life-cycle callback methods **************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        playerOEditText = findViewById(R.id.playerOEditText);
        playerOEditText.setOnEditorActionListener(this);
        playerXEditText = findViewById(R.id.playerXEditText);
        playerXEditText.setOnEditorActionListener(this);
        endButton = findViewById(R.id.resetButton);
        endButton.setVisibility(View.INVISIBLE);

        // Set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Get default preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.getBoolean(DARK_MODE, false);

    }

    @Override
    protected void onPause() {
        // save the EditText player names
        Editor editor = prefs.edit();
        editor.putString("playerNameO", playerNameO);
        editor.putString("playerNameX", playerNameX);
        editor.apply();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get preferences
        rememberNames = prefs.getBoolean("pref_remember_names", true);


        // get the instance variables
        if (rememberNames) {
            playerNameO = prefs.getString("playerNameO", "");
            playerNameX = prefs.getString("playerNameX", "");
        }
        else {
            playerNameO = "";
            playerNameX = "";
        }

        // set the names on their widgets
        playerOEditText.setText(playerNameO);
        playerXEditText.setText(playerNameX);

    }
    /************ Menu callback methods **************/
    // TODO must get settings options for preferences!

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu._main_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            startActivity(new Intent(
                    getApplicationContext(), SettingsActivity.class));
            return true;
        }
        else if (id == R.id.menu_about) {
            Toast.makeText(this, "This little app was edited by Amy Lese for Android Lab 3", Toast.LENGTH_LONG).show();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*************** Event handlers *************************/

    //This method will restart the game
    public void resetButton(View view){
        for (int i = 0 ; i < game.getNumberOfSquares(); i++) {
            ImageView imageView =
                    (ImageView)findViewById(R.id.constraint_layout).findViewWithTag(String.valueOf(i));
            imageView.setImageResource(0);
            endButton.setVisibility(View.INVISIBLE);
        }
        game.newGame();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if(i == EditorInfo.IME_ACTION_DONE ||
                i == EditorInfo.IME_ACTION_UNSPECIFIED ||
                i == EditorInfo.IME_ACTION_NEXT ||
                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            playerNameO = playerOEditText.getText().toString();
            if (playerNameO != "")
                game.setPlayerOName(playerNameO);

            playerNameX = playerXEditText.getText().toString();
            if (playerNameX != "")
                game.setPlayerXName(playerNameX);
        }

        // Hide the soft keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

        return false;
    }
    // Method to detect dark mode
    public boolean isDarkMode()
    {
        return darkMode;
    }
    // Method to detect large text
    public boolean isLargeText()
    {
        return largeText;
    }

    //This method to trigger the image appear when palyer click the box
    public void dropIn(View view){

        ImageView clickedView = (ImageView)view;
        int gamePosition = Integer.parseInt(clickedView.getTag().toString());
        if (game.makeMove(gamePosition)) {
            if(game.getCurrentPlayer() == XO.O) {
                // Put an O in the square
                clickedView.setImageResource(android.R.drawable.ic_delete);
            } else {
                // Put an X in the square
                clickedView.setImageResource(android.R.drawable.presence_online);
            }
            if(game.winningState()){
                endButton.setVisibility(View.VISIBLE);
                endButton.setText(game.getCurrentPlayerName() + " wins!"+"\n"+"Play again?");}
        } else {
            endButton.setVisibility(View.VISIBLE);
            endButton.setText("It is a draw"+"\n"+"Play again?");
        }
    }


}
