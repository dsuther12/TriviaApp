package com.example.trivia.data;

import com.example.trivia.model.Question;

import java.util.List;

public interface AnswerListAsyncResponse {
    void processFinished(List<Question> questionArrayList);

}
