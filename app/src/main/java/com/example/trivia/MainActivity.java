package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.databinding.ActivityMainBindingImpl;
import com.example.trivia.model.Question;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    String url = "https://opentdb.com/api.php?amount=10&difficulty=easy&type=boolean";
    List<Question> questionList;
    String replacementString;
    private int score = 0;
    private boolean[] boolArray = new boolean[]{true, true, true, true, true, true, true, true, true, true};
    private int boolArrayCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.scoreTextview.setText(String.format("Score: %d", score)); //set score to 0 at start

        Log.d("boolArray", "onCreate: " + boolArray[0]);


        questionList = new Repository().getQuestions(questionArrayList -> {
            replacementString = questionArrayList.get(currentQuestionIndex).getAnswer().
                    replaceAll("&#039;", "").
                     replaceAll("&quot;", "").
                      replaceAll("&rsquo;", "");
            Log.d("Replace", "Replaced with: " + replacementString);
            binding.questionTextview.setText(replacementString);
            updateQuestion();

        });


        binding.buttonNext.setOnClickListener(view -> {
           currentQuestionIndex += 1;
           boolArrayCount++;
           if(currentQuestionIndex % questionList.size() == 0){
               currentQuestionIndex = 0;
               boolArrayCount = 0;
           }
            toggleButtonsNextAndPrev();
            updateQuestion();
        });

        binding.buttonPrev.setOnClickListener(view -> {
            currentQuestionIndex -= 1;
            boolArrayCount--;
            if(currentQuestionIndex < 0) {
                currentQuestionIndex = questionList.size() - 1;
                boolArrayCount = boolArray.length - 1;
            }
            toggleButtonsNextAndPrev();
            updateQuestion();
        });

        binding.buttonTrue.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();
            disableButtons();

        });

        binding.buttonFalse.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();
            disableButtons();

        });

        updateOutOf();
    }

    private void disableButtons() {
        boolArray[boolArrayCount] = false;
        if (boolArray[boolArrayCount] == false) {
            binding.buttonTrue.setEnabled(false);
            binding.buttonFalse.setEnabled(false);
            binding.buttonTrue.setAlpha(.5f);
            binding.buttonFalse.setAlpha(.5f);
        }
    }

    private void toggleButtonsNextAndPrev() {
        if (boolArray[boolArrayCount] == true) {
            binding.buttonFalse.setEnabled(true);
            binding.buttonTrue.setEnabled(true);
            binding.buttonTrue.setAlpha(1);
            binding.buttonFalse.setAlpha(1);
        } else {
            binding.buttonFalse.setEnabled(false);
            binding.buttonTrue.setEnabled(false);
            binding.buttonTrue.setAlpha(.5f);
            binding.buttonFalse.setAlpha(.5f);
        }
    }

    private void checkAnswer(boolean userChoseCorrect) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if(userChoseCorrect == answer){
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            addScore();
        }
        else{
            snackMessageId = R.string.incorrect_asnwer;
            shakeAnimation();
            removeScore();
        }

        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        replacementString = questionList.get(currentQuestionIndex).getAnswer().
                replaceAll("&#039;", "").
                 replaceAll("&quot;", "\"").
                  replaceAll("&rsquo;", "'");
        binding.questionTextview.setText(replacementString);
        updateOutOf();


    }

    private void updateOutOf() {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questionList.size()));
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.questionTextview.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.GREEN);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void addScore(){
        score = score + 10;
        binding.scoreTextview.setText(String.format("Score: %d", score));

    }

    private void removeScore(){
        if((score - 5) >= 0){
            score = score - 5;
        }
        binding.scoreTextview.setText(String.format("Score: %d", score));

    }






}