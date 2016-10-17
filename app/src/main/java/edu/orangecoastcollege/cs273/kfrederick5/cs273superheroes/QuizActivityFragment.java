package edu.orangecoastcollege.cs273.kfrederick5.cs273superheroes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {

    private static final String TAG = "SuperheroQuiz Activity";


    private static final int HEROES_IN_QUIZ = 10;
    private static final int TOTAL_HEROES = 26;

    private ArrayList<Superhero> buttonList;
    private List<Superhero> heroList;

    private int questionSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private SecureRandom random;
    private Handler handler;

    private TextView questionNumberTextView;
    private ImageView heroImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;
    private TextView categoryTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        buttonList = new ArrayList<>();
        heroList = new ArrayList<>();

        try{
            buttonList = JSONLoader.loadJSONFromAsset(getActivity());
        }
        catch (IOException e){
            Log.e("Superhero", "Error loading JSON data." + e.getMessage());
        }

        random = new SecureRandom();
        handler = new Handler();

        questionNumberTextView = (TextView) v.findViewById(
                R.id.questionTextView);
        heroImageView = (ImageView) v.findViewById(R.id.heroImageView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] =
                (LinearLayout) v.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] =
                (LinearLayout) v.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] =
                (LinearLayout) v.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] =
                (LinearLayout) v.findViewById(R.id.row4LinearLayout);
        answerTextView = (TextView) v.findViewById(R.id.answerTextView);
        categoryTextView = (TextView) v.findViewById(R.id.categoryTextView);


        for (LinearLayout row : guessLinearLayouts) {
            int childCount = row.getChildCount();

            for (int column = 0; column < childCount; column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(
                getString(R.string.question, 1, HEROES_IN_QUIZ));
        return v;
    }

    public void updateGuessRows(SharedPreferences sharedPreferences){
        //get the number of guess buttons that should be displayed
        String choices =
                sharedPreferences.getString(QuizActivity.CHOICES, "4");
        guessRows = Integer.parseInt(choices) / 2;

        //hide all guess button LinearLayouts
        for (LinearLayout layout: guessLinearLayouts)
            layout.setVisibility(View.GONE);

        //display appropriate guess button LinearLayouts
        for(int row = 0; row < guessRows; row++)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }


    public void updateQuestions(SharedPreferences sharedPreferences){

        /*
        question set being used
        0 = Heroes names, 1 = Powers, 2 = Unique
         */
        questionSet = Integer.parseInt(sharedPreferences.getString(
                QuizActivity.QUESTIONS, "0"));
        categoryTextView.setText(questionSet == 2 ? getString(R.string.guess_thing):
                questionSet == 1 ? getString(R.string.guess_power) :
                        getString(R.string.guess_hero));

    }

    public void resetQuiz() {

        correctAnswers = 0; // reset the # of correct answers
        totalGuesses = 0; // reset total # guesses
        heroList.clear(); // clear prior list of quiz countries

        int heroCounter = 1;

        //add FLAGS_IN_QUIZ random file names to the quizCountriesList
        while (heroCounter <= HEROES_IN_QUIZ) {
            int randomIndex = random.nextInt(TOTAL_HEROES);

            //get the random file name
            Superhero singleHero = buttonList.get(randomIndex);

            //if the region is enabled and it hasn't already been chosen
            if (!heroList.contains(singleHero)) {
                heroList.add(singleHero); // add the file to the list
                ++heroCounter;
            }
        }

        loadNextHero(); // start the quiz by loading the first hero.
    }

    private void loadNextHero(){
        Superhero nextHero = heroList.remove(0);
        correctAnswer = questionSet == 0 ? nextHero.getName() : questionSet ==
                1 ? nextHero.getSuperpower() : nextHero.getOneThing(); //update the correct answer
        answerTextView.setText(""); // clear answerTextView

        //display current question number
        questionNumberTextView.setText(getString(R.string.question,
                (correctAnswers + 1), HEROES_IN_QUIZ));

        //use assetManager to load next image from assets folder
        AssetManager assets = getActivity().getAssets();

        //get an InputStream to the asset representing the next hero
        //and try to use the InputStream
        try(InputStream stream =
                    assets.open("pics/" + nextHero.getImageName())) {
            //load the asset as a Drawable and display on the heroImageView
            Drawable imageOfHero = Drawable.createFromStream(
                    stream, nextHero.getImageName());
            heroImageView.setImageDrawable(imageOfHero);
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading" + nextHero.getImageName(), exception);
        }

        Collections.shuffle(buttonList); // shuffle file names

        //put the correct answer at the end of buttonList
        int correct = buttonList.indexOf(nextHero);
        buttonList.add(buttonList.remove(correct));

        // add 2, 4, 6, or 8 guess Buttons based on the value of guessRows
        for (int row = 0; row < guessRows; row++){
            int columnCount = guessLinearLayouts[row].getChildCount();
            //place Buttons in currentTableRow
            for(int column = 0; column < columnCount; column++) {
                //get reference to Button to configure
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);
                newGuessButton.setText(getAnswers(row,column));
            }
        }

        //randomly replace one Button with the correct answer
        int row = random.nextInt(guessRows); // pick random row
        int column = random.nextInt(2); // pick random column
        LinearLayout randomRow = guessLinearLayouts[row]; // get the row

        ((Button) randomRow.getChildAt(column)).setText(correctAnswer);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();

            ++totalGuesses; //increment number of guesses the user has made

            if (guess.equals(correctAnswer)) { //if the guess is correct
                ++correctAnswers; // increment the number of correct answers

                //display correct answer in green text
                answerTextView.setText(correctAnswer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer,
                        getContext().getTheme()));

                disableButtons(); //disable all guess Buttons

                //if the user has correctly identified FLAGS_IN_QUIZ flags
                if (correctAnswers == HEROES_IN_QUIZ) {
                    //DialogFragment to display quiz stats and start new quiz
                    DialogFragment quizResults = new DialogFragment() {
                        //create an AlertDialog and return it
                        @Override
                        public Dialog onCreateDialog(Bundle bundle) {

                            AlertDialog.Builder builder = new
                                    AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(
                                    R.string.results, totalGuesses,
                                    (1000.0 / (double) totalGuesses)));

                            //"Reset Quiz" Button
                            builder.setPositiveButton(R.string.reset_quiz,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            resetQuiz();
                                        }
                                    });

                            return builder.create(); // return the AlertDialog
                        }
                    };

                    //use FragmentManager to display DialogFragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else { // answer is correct but quiz isn't over
                    //load the next flag after a 2-second delay
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextHero();
                        }
                    }, 2000); // 2000 milliseconds for 2-second delay
                }
            } else {//answer incorrect

                //display "Incorrect!" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(
                        R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false); //disable incorrect answer
            }
        }
    };

    private void disableButtons() {
        for(int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for(int i = 0; i < guessRow.getChildCount(); i++)
                guessRow.getChildAt(i).setEnabled(false);
        }
    }

    private String getAnswers(int row, int column){
        String buttonText = "";
        switch (questionSet){
            case 0:
                buttonText = buttonList.get((row * 2) + column).getName();
                break;

            case 1:
                buttonText = buttonList.get((row * 2)+ column).getSuperpower();
                break;

            case 2:
                buttonText = buttonList.get((row * 2) + column).getOneThing();
                break;
        }
        return buttonText;
    }
}


