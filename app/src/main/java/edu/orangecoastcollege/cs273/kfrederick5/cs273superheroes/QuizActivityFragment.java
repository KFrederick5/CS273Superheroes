package edu.orangecoastcollege.cs273.kfrederick5.cs273superheroes;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {

    private static final String TAG = "SuperheroQuiz Activity";

    private static final int HEROES_IN_QUIZ = 10;

    private List<String> fileNameList;
    private List<String> heroList;
    private Set<String> questionSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private SecureRandom random;
    private Handler handler;

    private TextView questionNumberTextView;
    private ImageView heroImageView;
    private TextView answerTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        fileNameList = new ArrayList<>();
        heroList = new ArrayList<>();
        random = new SecureRandom();
        handler new Handler();

        questionNumberTextView = (TextView) v.findViewById(
                R.id.quizFragment);
        heroImageView = (ImageView) v.findViewById(R.id.heroImageView);
        answerTextView = (TextView) v.findViewById(R.id.)

        )


        return v;
    }
}
