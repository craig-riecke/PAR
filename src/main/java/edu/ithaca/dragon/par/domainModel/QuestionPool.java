package edu.ithaca.dragon.par.domainModel;

import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.io.Datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuestionPool {
    private List<Question> allQuestions;


    public QuestionPool(List<Question> allQuestions){
        this.allQuestions = new ArrayList<>(allQuestions);
    }

    public QuestionPool(Datastore datastore) throws IOException {
        allQuestions = datastore.loadQuestions();
    }

    public List<Question> getAllQuestions(){
        return new ArrayList<>(allQuestions);
    }

    public Question getQuestionFromId(String questionIdIn){
        Question q = getQuestionFromId(questionIdIn, allQuestions);
        if(q == null){
            throw new RuntimeException("Question with id:" + questionIdIn + " does not exist");
        }
        return q;
    }

    public static Question getQuestionFromId(String questionIdIn, List<Question> questionList){

        for(Question q : questionList){
            if(q.getId().equals(questionIdIn)){
                return q;
            }

            //call getQuestionFromId on the followup questions
            Question q2 = getQuestionFromId(questionIdIn, q.getFollowupQuestions());
            if(q2 != null){
                return q2;
            }
        }
        return null;
    }

    public static List<Question> getTopLevelQuestionsFromUrl(List<Question> questions, String imageUrlIn){
        List<Question> toReturn = new ArrayList<>();
        for(int i=0; i < questions.size(); i++){
            if(questions.get(i).getImageUrl().equals(imageUrlIn))
                toReturn.add(questions.get(i));
        }
        return toReturn;
    }

    public List<Question> getQuestionsFromUrl(String questionUrl){
        return QuestionPool.getQuestionsWithUrl(allQuestions, questionUrl);
    }

    public static List<Question> getQuestionsWithUrl(List<Question> questionList, String questionUrl){
        List<Question> toReturn = new ArrayList<>();
        for(Question questionToCheck : questionList){
            if(questionToCheck.getImageUrl().equals(questionUrl)){
                toReturn.add(questionToCheck);
            }
            toReturn.addAll(getQuestionsWithUrl(questionToCheck.getFollowupQuestions(), questionUrl));
        }
        return toReturn;
    }
    //This is currently only used in tests
    public List<Question> getQuestionsFromIds(List<String> idsIn){
        List<Question> toReturn = new ArrayList<>();
        boolean validId;

        //TODO: find a better algorithm?
        for(String currId : idsIn){
            validId = false;
            for(Question currQuestion : allQuestions){
                if(currQuestion.getId().equals(currId)){
                    toReturn.add(currQuestion);
                    validId = true;
                }
            }
            if(!validId) //the id is invalid
                throw new RuntimeException("Question with id:" + currId + " does not exist");
        }

        return toReturn;
    }

    public void checkWindowSize(List<String> enumNames, List<Integer> typeCounts){

    }

    public boolean checkWindowSize(int desiredWindowSize){
        if(desiredWindowSize<1){
            return false;
        }
        //create parallel arrays of types and count of times seen
        List<String> enumNames = Stream.of(EquineQuestionTypes.values()).map(Enum::name).collect(Collectors.toList());
        List<Integer> typeCounts = new ArrayList<>();

        //initialize typeCounts with 0s
        for(int i=0; i<enumNames.size(); i++){
            typeCounts.add(0);
        }

        //loop through all questions
        for(Question currQuestion : allQuestions){

            //check for attachment followup questions
            //TODO: This is not recursive and assumes that all followup questions are attachment questions with no further followup questions
            //TODO: Make it recursive!!
            for(Question currFollowup : currQuestion.getFollowupQuestions()){
                if(currFollowup.getType().equals(EquineQuestionTypes.attachment.toString())){
                    typeCounts.set(2, typeCounts.get(2) + 1);
                }
            }

            //find which type it is
            for(int i=0; i<enumNames.size(); i++){
                if(enumNames.get(i).equals(currQuestion.getType())){
                    //increment the typecount
                    typeCounts.set(i, typeCounts.get(i) + 1);
                    break;
                }
            }
        }

        //check if the typecounts are high enough
        for(int i = 0; i<typeCounts.size();i++){
            if(typeCounts.get(i) < desiredWindowSize)
                return false;
        }
        return true;
    }
}
