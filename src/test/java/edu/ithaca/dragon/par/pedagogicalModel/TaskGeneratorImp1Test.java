package edu.ithaca.dragon.par.pedagogicalModel;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.domainModel.equineUltrasound.EquineQuestionTypes;
import edu.ithaca.dragon.par.io.ImageTask;
import edu.ithaca.dragon.par.io.JsonQuestionPoolDatastore;
import edu.ithaca.dragon.par.studentModel.StudentModel;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaskGeneratorImp1Test {

    @Test
    public void makeTaskWithSingleQuestionTest() throws IOException {
        //set up questionPool and studentModel, create an imageTask with the studentModel
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/SampleQuestionPool.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser1", questionPool.getAllQuestions());

        //no questions have been seen
        assertEquals(15, studentModel.getUnseenQuestionCount());

        //make an imageTask and check aspects of it
        Question task1Question = TaskGeneratorImp1.getInitialQuestionForTask(studentModel, 1);
        ImageTask task1 = new ImageTask(task1Question.getImageUrl(), Arrays.asList(task1Question));
        assertEquals("./images/demoEquine04.jpg", task1.getImageUrl());
        assertEquals(1, task1.getTaskQuestions().size());

        //make a new imageTask and check aspects of it
        Question task2Question = TaskGeneratorImp1.getInitialQuestionForTask(studentModel, 1);
        ImageTask task2 = new ImageTask(task2Question.getImageUrl(), Arrays.asList(task1Question));
        assertEquals("./images/demoEquine04.jpg", task1.getImageUrl());
        assertEquals(1, task2.getTaskQuestions().size());

    }
//TODO:TEST CURRENTLY FAILS WITH ORIGINAL CHOOSE TOP LEVEL QUESTION
    @Test
    public void makeTaskWithSingleQuestionTestBreak() throws IOException{
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/SampleQuestionPool.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser1", questionPool.getAllQuestions());
        studentModel.getUserQuestionSet().increaseTimesSeenAllQuestions(studentModel.getUserQuestionSet().getTopLevelUnseenQuestions());
        Question task1Question = TaskGeneratorImp1.getInitialQuestionForTask(studentModel, 1);
        ImageTask task1 = new ImageTask(task1Question.getImageUrl(), Arrays.asList(task1Question));
        assertEquals("./images/demoEquine04.jpg", task1.getImageUrl());

    }

    @Test
    public void makeTaskTest() throws IOException{
        TaskGenerator taskGenerator = new TaskGeneratorImp1();
        //set up questionPool and studentModel, create an imageTask with the studentModel
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/DemoQuestionPool.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser1", questionPool.getAllQuestions());

        //make an imageTask and check aspects of it
        ImageTask task1 = taskGenerator.makeTask(studentModel, 4);

        assertEquals("./images/demoEquine14.jpg", task1.getImageUrl());

        assertEquals(1, task1.getTaskQuestions().size());

        //make a new imageTask and check aspects of it
        ImageTask task2 = taskGenerator.makeTask(studentModel, 4);
        assertEquals("./images/demoEquine02.jpg", task2.getImageUrl());
        assertEquals(1, task2.getTaskQuestions().size());
    }

    @Test
    public void studentModelWithNoQuestionsTest() throws IOException{
        QuestionPool emptyQP = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/SampleQuestionsEmpty.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser1", emptyQP.getAllQuestions());

        //try to make a single Question
        try{
            Question newQ = TaskGeneratorImp1.getInitialQuestionForTask(studentModel, 1);
            fail();
        }catch(Exception ee){

        }

        //try to make a task
        try{
            ImageTask imageTask = new TaskGeneratorImp1().makeTask(studentModel, 4);
            fail();
        }catch(Exception ee){

        }
    }

    @Test
    public void setTypeTest(){
        //this test will be written once StudentModel has individual scores for each question and TaskGeneratorImp1
        //can use those to determine level
        assertEquals(4,4);
    }

    @Test
    public void removeTypeTest() throws IOException{
        List<Question> questions= JsonUtil.listFromJsonFile("src/test/resources/author/SampleQuestionPool.json", Question.class);
        questions = TaskGeneratorImp1.removeTypeFromQuestionList(questions, EquineQuestionTypes.plane.toString());
        assertEquals(10, questions.size());
        questions = TaskGeneratorImp1.removeTypeFromQuestionList(questions, EquineQuestionTypes.structure.toString());
        assertEquals(5, questions.size());
        questions = TaskGeneratorImp1.removeTypeFromQuestionList(questions, EquineQuestionTypes.zone.toString());
        assertEquals(0, questions.size());

        questions = TaskGeneratorImp1.removeTypeFromQuestionList(questions, EquineQuestionTypes.plane.toString());
        assertEquals(0, questions.size());
        questions = TaskGeneratorImp1.removeTypeFromQuestionList(questions, EquineQuestionTypes.structure.toString());
        assertEquals(0, questions.size());
        questions = TaskGeneratorImp1.removeTypeFromQuestionList(questions, EquineQuestionTypes.zone.toString());
        assertEquals(0, questions.size());


    }

    @Test
    public void addAllQuestionsTest()throws IOException{
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/DemoQuestionPool.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser1", questionPool.getAllQuestions());

        //first url
        Question q1 = studentModel.getUserQuestionSet().getTopLevelUnseenQuestions().get(0);
        List<Question> qs = TaskGeneratorImp1.addAllQuestions(studentModel, q1);
        assertEquals(5, qs.size());

        //second url
        Question q2 = studentModel.getUserQuestionSet().getTopLevelUnseenQuestions().get(5);
        qs = TaskGeneratorImp1.addAllQuestions(studentModel, q2);
        assertEquals(6, qs.size());

        //third url
        Question q3 = studentModel.getUserQuestionSet().getTopLevelUnseenQuestions().get(11);
        qs = TaskGeneratorImp1.addAllQuestions(studentModel, q3);
        assertEquals(4, qs.size());

        //fourth url
        Question q4 = studentModel.getUserQuestionSet().getTopLevelUnseenQuestions().get(15);
        qs = TaskGeneratorImp1.addAllQuestions(studentModel, q4);
        assertEquals(3, qs.size());
        //repeat
        qs = TaskGeneratorImp1.addAllQuestions(studentModel, q4);
        assertEquals(3, qs.size());
        qs = TaskGeneratorImp1.addAllQuestions(studentModel, q4);
        assertEquals(3, qs.size());

        //last question
        Question q5 = studentModel.getUserQuestionSet().getTopLevelUnseenQuestions().get(questionPool.getAllQuestions().size()-1);
        qs = TaskGeneratorImp1.addAllQuestions(studentModel, q5);
        assertEquals(6, qs.size());
    }

    @Test
    public void filterQuestionsTest()throws IOException{
       //plane only
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/DemoQuestionPool.json").getAllQuestions());
        List<Question> questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(1, questionList);
        assertEquals(10, questionList.size());

        //plane and structure only
        questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(2, questionList);
        assertEquals(37, questionList.size());

        //structure only
        questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(3, questionList);
        assertEquals(27, questionList.size());

        //structure and attachment only
        questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(4, questionList);
        assertEquals(27, questionList.size());

        //structure and attachment only
        questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(5, questionList);
        assertEquals(27, questionList.size());

        //structure, attachment, and zone only
        questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(6, questionList);
        assertEquals(37, questionList.size());

        //zone only
        questionList = questionPool.getAllQuestions();
        questionList = TaskGeneratorImp1.filterQuestions(7, questionList);
        assertEquals(10, questionList.size());
    }

    @Test
    public void imageTaskWithFollowupQuestionsTest(@TempDir Path tempDir) throws IOException{
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/DemoQuestionPoolFollowup.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser111", questionPool.getAllQuestions());

        ImageTask imageTask = TaskGeneratorImp1.makeTaskGivenLevel(studentModel, 3);
        //JsonUtil.toJsonFile("src/test/resources/autoGenerated/imageTaskWithFollowup.json", imageTask);
        assertEquals(3, imageTask.getTaskQuestions().size());
        assertEquals(0, imageTask.getTaskQuestions().get(0).getFollowupQuestions().size());

        //TODO: check increase of times seen

        studentModel = new StudentModel("TestUser112", questionPool.getAllQuestions());
        imageTask = TaskGeneratorImp1.makeTaskGivenLevel(studentModel, 4);
        //JsonUtil.toJsonFile("src/test/resources/autoGenerated/imageTaskWithFollowup.json", imageTask);
        assertEquals(3, imageTask.getTaskQuestions().size());
        assertEquals(3, imageTask.getTaskQuestions().get(0).getFollowupQuestions().size());
        //but it should filter out the plane follow up to the follow up
        assertEquals(0, imageTask.getTaskQuestions().get(0).getFollowupQuestions().get(0).getFollowupQuestions().size());


    }

    @Test
    public void removeTypeFromQuestionTest() throws IOException{
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/DemoQuestionPoolFollowup.json").getAllQuestions());
        Question noFollowups = questionPool.getQuestionFromId("plane./images/demoEquine14.jpg");
        Question twoFollowups = questionPool.getQuestionFromId("structure3./images/demoEquine10.jpg");
        Question recFollowups = questionPool.getQuestionFromId("structure0./images/demoEquine14.jpg");

        //Trying to remove nonexistant followup questions should have no effect on the Question
        Question noFollowsAfter = TaskGeneratorImp1.removeTypeFromQuestion(noFollowups, EquineQuestionTypes.attachment.toString());
        assertEquals(noFollowups, noFollowsAfter);

        //The method should not remove the base question
        assertThrows(RuntimeException.class, () -> {Question noFollowsAfterPlane = TaskGeneratorImp1.removeTypeFromQuestion(noFollowups, EquineQuestionTypes.plane.toString());});

        //Removing attachment followups should create a question with no followups
        Question twoFollowupsAfter = TaskGeneratorImp1.removeTypeFromQuestion(twoFollowups, EquineQuestionTypes.attachment.toString());
        assertEquals(0, twoFollowupsAfter.getFollowupQuestions().size());
        assertFalse(twoFollowups == twoFollowupsAfter);

        //Removing a followup to a followup
        Question recFollowupsAfter = TaskGeneratorImp1.removeTypeFromQuestion(recFollowups, EquineQuestionTypes.plane.toString());
        assertFalse(recFollowupsAfter == recFollowups);
        assertEquals("plane", recFollowups.getFollowupQuestions().get(2).getFollowupQuestions().get(0).getType());
        assertEquals(0, recFollowupsAfter.getFollowupQuestions().get(2).getFollowupQuestions().size());
    }
//test should fail due to recursion issue
    @Test
    public void equineQuestionTypesMapTest() throws IOException{
        QuestionPool questionPool = new QuestionPool(new JsonQuestionPoolDatastore("src/test/resources/author/DemoQuestionPoolFewFollowups.json").getAllQuestions());
        StudentModel studentModel = new StudentModel("TestUser1", questionPool.getAllQuestions());
        Map<EquineQuestionTypes,List<Question>> equineQuestionMap=new LevelTaskGenerator().equineQuestionTypesMap(studentModel);
        assertEquals(4,equineQuestionMap.size());
        //do bonus and attachment not being recorded
        assertEquals(13,equineQuestionMap.get(EquineQuestionTypes.plane).size());
        assertEquals(27,equineQuestionMap.get(EquineQuestionTypes.structure).size());
        assertEquals(7,equineQuestionMap.get(EquineQuestionTypes.attachment).size());
        assertEquals(10,equineQuestionMap.get(EquineQuestionTypes.zone).size());

        System.out.println(equineQuestionMap.get(EquineQuestionTypes.attachment).size());
    }
/*
    @Test
    public void emptyQuestionSetTest()throws IOException {
        Datastore datastore = new JsonDatastore("src/test/resources/author/simpleTestSet/currentQuestionPool.json", "src/test/resources/author/simpleTestSet/students");
        StudentModel testUser2 = datastore.loadStudentModel("testUser2");

        ImageTask imageTask2 = TaskGeneratorImp1.makeTaskGivenLevel(testUser2,1);
        ImageTask imageTask = TaskGeneratorImp1.makeTaskGivenLevel(testUser2,7);

        System.out.println(imageTask2.getTaskQuestions());
        System.out.println(imageTask.getTaskQuestions());

        assertEquals(1,imageTask.getTaskQuestions().size());

    }

 */


}
