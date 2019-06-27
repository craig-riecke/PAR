package edu.ithaca.dragon.par.pedagogicalModel;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.studentModel.StudentModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskGenerator {

    /**
     * Generates an ImageTask with a single Question
     * Adds unseen Questions before adding seen Questions
     * @param studentModel
     * @return
     */
    public static Question getInitialQuestionForTask(StudentModel studentModel){
        checkStudentModel(studentModel);

        //there are questions the student has not seen
        if(studentModel.getUnseenQuestionCount()>0){
            //make and return an imageTask with the first question from the studentModels.unseenQuestions
            Question firstQuestionFromUnseen = studentModel.getUserQuestionSet().getUnseenQuestions().get(0);
            return firstQuestionFromUnseen;
        }

        //the student has seen every question, get a question that they have already seen
        Question leastSeenQuestion = getLeastSeenQuestion(studentModel);

        return leastSeenQuestion;
    }

    /**
     * Generates a Task with Questions that share the same URL
     * Currently includes questions that may have duplicate types
     * @param studentModel
     * @return
     */
    public static ImageTask makeTask(StudentModel studentModel){
        checkStudentModel(studentModel);

        //get an initial question url
        Question initialQuestion = TaskGenerator.getInitialQuestionForTask(studentModel);

        //put initialQuestion, unseenQuestions and seenQuestions all in a list
        List<Question> unseenQuestionsWithCorrectUrl = TaskGenerator.getQuestionsWithUrl(studentModel.getUserQuestionSet().getUnseenQuestions(), initialQuestion.getImageUrl());
        List<Question> seenQuestionsWithCorrectUrl = TaskGenerator.getQuestionsWithUrl(studentModel.getUserQuestionSet().getSeenQuestions(), initialQuestion.getImageUrl());
        List<Question> questionList = new ArrayList<>();
        questionList.addAll(unseenQuestionsWithCorrectUrl);
        questionList.addAll(seenQuestionsWithCorrectUrl);


        ImageTask imageTask = new ImageTask(initialQuestion.getImageUrl(), questionList);

        //let studentModel know that unseen questions are seen
        for(Question currUnseenQuestion : unseenQuestionsWithCorrectUrl){
            studentModel.getUserQuestionSet().givenQuestion(currUnseenQuestion.getId());
        }

        return imageTask;
    }

    /**
     * Creates a list of questions that have the questionUrl out of questionList
     */
    public static List<Question> getQuestionsWithUrl(List<Question> questionList, String questionUrl){
        List<Question> toReturn = new ArrayList<>();
        for(Question questionToCheck : questionList){
            if(questionToCheck.getImageUrl().equals(questionUrl))
                toReturn.add(questionToCheck);
        }
        return toReturn;
    }

    public static void addAllQuestions(ImageTask it, List<Question> sameUrlListUnseen, List<Question> sameUrlListSeen){
        for (int i = 0; i<sameUrlListUnseen.size(); i++){
            if (!sameUrlListUnseen.get(i).getId().equals(it.getTaskQuestions().get(0).getId())) {
                it.addQuestion(sameUrlListUnseen.get(i));
            }
        }
        for (int i = 0; i<sameUrlListSeen.size(); i++){
            if (!sameUrlListSeen.get(i).getId().equals(it.getTaskQuestions().get(0).getId())) {
                it.addQuestion(sameUrlListSeen.get(i));
            }
        }
    }

    public static void checkStudentModel(StudentModel studentModel){
        if(studentModel.getUnseenQuestionCount() == 0 && studentModel.getSeenQuestionCount() == 0){
            throw new RuntimeException("The studentModel has no questions");
        }
    }

    public static Question getLeastSeenQuestion(StudentModel studentModel){
        //TODO check if there are unseen questions?

        List<Question> seenQuestions = studentModel.getUserQuestionSet().getSeenQuestions();

        if(seenQuestions.size()==0){
            throw new RuntimeException("The studentModel has no questions");
        }

        int index = 0;
        for(int i = 0; i<seenQuestions.size(); i++){
            if(studentModel.getUserQuestionSet().getTimesSeen(seenQuestions.get(i).getId()) < studentModel.getUserQuestionSet().getTimesSeen(seenQuestions.get(index).getId())){
                index = i;
            }
        }
        return seenQuestions.get(index);
    }
}