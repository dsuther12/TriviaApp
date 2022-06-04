package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    ArrayList<Question> questionArrayList = new ArrayList<Question>();
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject = new JSONObject();

    String url = "https://opentdb.com/api.php?amount=10&difficulty=easy&type=boolean";

    public List<Question> getQuestions(final AnswerListAsyncResponse callBack){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, response -> {

            try {
                jsonArray = response.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < 10; i++) {


                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Question question = new Question(jsonObject.getString("question"), jsonObject.getBoolean("correct_answer"));

                    //Add questions to arrayList
                    questionArrayList.add(question);

//                    Log.d("question", "onCreate: " + questionArrayList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            if(callBack != null){
                callBack.processFinished(questionArrayList);
            }

        },
                error -> {

                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        return questionArrayList;


    }
}
