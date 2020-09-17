package edu.ithaca.dragon.par.pedagogicalModel;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.studentModel.ResponsesPerQuestion;
import edu.ithaca.dragon.par.studentModel.StudentModel;

import java.util.Date;
import java.util.List;

public class MessageGenerator {
    private static final String increaseLevelString = "You're doing great!";
    private static final String masterRepeat = "You've mastered the material and started repeating questions";
    private static final String masterNoRepeat = "You have mastered the material, feel free to keep practicing";
    //note: decreaseLevel and repeatLevel messages can't be a private static final Strings because it has more personalized feedback

    //TODO: can make the pieces of the strings private static final


    private static final int repeatWindow = 1800000; //30 min in mili

    public static void generateMessage(StudentModel studentModel, ImageTask imageTask, int previousLevel){
        int level = studentModel.getLastLevelRecorded();
        imageTask.setMessage(null);

        //mastered student message
        if (level == 7){
            level7Message(studentModel, imageTask);
        }

        else{
            //down level
            //if the student has just decreased in level, this will set the appropriate message.
            decreaseLevelMessage(studentModel, imageTask, previousLevel);

            //up level
            if (imageTask.getMessage() == null){
                increaseLevelMessage(studentModel, imageTask, previousLevel);
            }

            //stayed on same level, not mastered

        }

    }

    public static void decreaseLevelMessage(StudentModel studentModel, ImageTask imageTask, int previousLevel){
        int level = studentModel.getLastLevelRecorded();
        if (previousLevel-level > 0 && previousLevel != -1){
            List<String> questionsInPreviousLevel = EquineQuestionTypes.getTypesForLevel(previousLevel);
            String questionsOneString = "";
            for(String q: questionsInPreviousLevel){
                questionsOneString += q + "/";
            }
            questionsOneString = questionsOneString.substring(0,questionsOneString.length()-1);
            imageTask.setMessage("Looks like you're having trouble with " +  questionsOneString + " questions, go look at resources and come back if you need to");
        }
        else{
            imageTask.setMessage(null);
        }
    }

    public static void increaseLevelMessage(StudentModel studentModel, ImageTask imageTask, int previousLevel) {
        int level = studentModel.getLastLevelRecorded();
        if (previousLevel-level < 0 && previousLevel != -1){
            imageTask.setMessage(increaseLevelString);
        }
        else{
            imageTask.setMessage(null);
        }
    }

    public static void level7Message(StudentModel studentModel, ImageTask imageTask){
        int level = studentModel.getLastLevelRecorded();

        //mastered student
        if (level == 7){
            //repeating questions
            List<Question> allQuestions = studentModel.getUserQuestionSet().getAllQuestions();
            int index = -1;
            for (int i = 0; i < allQuestions.size(); i++){
                //there should only be one zone question in this task
                if (allQuestions.get(i).getId().equals(imageTask.getTaskQuestions().get(0).getId())){
                    index = i;
                }
            }
            //question found
            if (index != -1){
                //get index of first question
                if (studentModel.getUserQuestionSet().getTimesSeen(allQuestions.get(index).getId())>0){
                    imageTask.setMessage(masterRepeat);
                }
                //not seen yet
                else {
                    imageTask.setMessage(masterNoRepeat);
                }
            }
            //not found, message is null
            else{
                imageTask.setMessage(null);
            }
        }
        else{
            imageTask.setMessage(null);
        }
    }

    public static void repeatLevelMessage(StudentModel studentModel, ImageTask imageTask, int previousLevel){
        int level = studentModel.getLastLevelRecorded();

        //stayed on same level, not mastered
        if (previousLevel == level){
            //get index of first question
            int ind = -1;
            List<Question> allQuestions = studentModel.getUserQuestionSet().getAllQuestions();
            for (int i = 0; i < allQuestions.size(); i++){
                //if the id of the current question equals the first in the task
                if (allQuestions.get(i).getId().equals(imageTask.getTaskQuestions().get(0).getId())){
                    ind = i;
                }
            }
            if(ind!=-1){
                ResponsesPerQuestion responsesPerQuestion = studentModel.getUserResponseSet().getResponseById(allQuestions.get(ind).getId());
                //no responses
                if (responsesPerQuestion.getAllResponses().size()<1){
                    imageTask.setMessage(null);
                }
                else {
                    //if there's at least one response
                    if (responsesPerQuestion.getAllResponses().size() >= 1) {
                        //most recent response milliseconds
                        long sec2 = responsesPerQuestion.getAllResponses().get(responsesPerQuestion.getAllResponses().size() - 1).getMillSeconds();

                        //seen within the time window
                        Date date = new Date();
                        if (date.getTime() - sec2 <= repeatWindow) {
                            List<String> questionsInLevel = EquineQuestionTypes.getTypesForLevel(level);
                            String questionsOneString = "";
                            for (String q : questionsInLevel) {
                                questionsOneString += q + "/";
                            }
                            questionsOneString = questionsOneString.substring(0, questionsOneString.length() - 1);
                            imageTask.setMessage("You've seen this question recently, you might be stuck on " + questionsOneString + " questions.");
                        }
                    }
                    else{
                        imageTask.setMessage(null);
                    }
                }
            }
            else{
                imageTask.setMessage(null);
            }

        }
        else{
            imageTask.setMessage(null);
        }
    }
}
