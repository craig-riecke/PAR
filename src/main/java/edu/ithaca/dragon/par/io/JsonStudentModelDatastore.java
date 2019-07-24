package edu.ithaca.dragon.par.io;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.studentModel.StudentModel;
import edu.ithaca.dragon.util.FileSystemUtil;
import edu.ithaca.dragon.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonStudentModelDatastore extends JsonQuestionPoolDatastore implements StudentModelDatastore {

    private String studentModelFilePath;
    private Map<String, StudentModel> studentModelMap;

    public JsonStudentModelDatastore(String questionFilePath, String studentModelFilePath) throws IOException {
        super(questionFilePath);
        this.studentModelFilePath = studentModelFilePath;
        studentModelMap = new HashMap<>();
    }

    private static StudentModel loadStudentModelFromFile(QuestionPool questionPool, String studentModelFilePath, String userId) throws IOException{
        String fullFileName = studentModelFilePath + "/" + userId + ".json";

        //check if file exists, return null if it doesn't
        File checkFile = new File(fullFileName);
        if(!checkFile.exists()){
            return null;
        }

        StudentModelRecord SMR = JsonUtil.fromJsonFile(fullFileName, StudentModelRecord.class);
        return SMR.buildStudentModel(questionPool);
    }

    @Override
    public StudentModel getStudentModel(String userId) throws IOException {
        return getOrCreateStudentModel(userId);
    }

    @Override
    public void saveStudentModel(StudentModel studentModel) throws IOException {

    }

    @Override
    public void addQuestions(List<Question> questions) throws IOException {

    }

    @Override
    public void imageTaskResponseSubmitted(StudentModel studentModel, ImageTaskResponse imageTaskResponse) throws IOException{
        if(studentModelFilePath == null)
            throw new IOException("studentModelFilePath is null");
        String fullFilePath = studentModelFilePath + "/" +  studentModel.getUserId() + ".json";
        JsonUtil.toJsonFile(fullFilePath, new StudentModelRecord(studentModel));
    }

    @Override
    public void logout(String userId) throws IOException{
        studentModelMap.remove(getStudentModel(userId));
    }

    //Side effect: if a new model is created, it is added to the given studentModelMap
    /**
     * @param userId
     * @return a StudentModel corresponding to the given userId that is also in the studentModelMap
     * @post if there was no corresponding StudentModel, a new on will be created and added to the map
     */
    public StudentModel getOrCreateStudentModel(String userId) throws IOException {
        //try to find the student in the map
        StudentModel studentModel = studentModelMap.get(userId);

        //if the student wasn't in the map, try to load from file
        if (studentModel == null) {
            studentModel = loadStudentModelFromFile(questionPool, studentModelFilePath, userId);
        }

        //the student didn't have a file, create a new student
        if (studentModel == null) {
            studentModel = new StudentModel(userId, getAllQuestions());

            //if the student didn't have a file, create a new student
            if (studentModel == null) {
                studentModel = new StudentModel(userId, getAllQuestions());
            }
            //add the student to the map
            studentModelMap.put(userId, studentModel);
        }
        return studentModel;
    }
}
