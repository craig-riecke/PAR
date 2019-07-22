package edu.ithaca.dragon.par.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import edu.ithaca.dragon.par.domainModel.Question;
import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.util.JsonUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImageTaskResponseTest {

    @Test
    public void toAndBackJsonTest() throws IOException {
        ImageTaskResponse responseSet=new ImageTaskResponse("response1",Arrays.asList("PlaneQ1","StructureQ1","ZoneQ1"),Arrays.asList("Lateral","bone","3c"));
        List<ImageTaskResponse> responsesToFile = Arrays.asList(responseSet);

        JsonUtil.toJsonFile("src/test/resources/autoGenerated/ResponsesTest-toAndBackJsonTest.json", responsesToFile);
        List<ImageTaskResponse> responsesFromFile = JsonUtil.listFromJsonFile("src/test/resources/autoGenerated/ResponsesTest-toAndBackJsonTest.json", ImageTaskResponse.class);

        assertEquals(responsesToFile.size(),responsesFromFile.size());
        for (int i=0; i< responsesFromFile.size(); i++){
            assertEquals(responsesToFile.get(i), responsesFromFile.get(i));
        }
        Path path = Paths.get("src/test/resources/autoGenerated/ResponsesTest-toAndBackJsonTest.json");
        assertTrue(Files.deleteIfExists(path));
    }

    @Test
    public void findResponseToQuestionTest() throws IOException{
        QuestionPool qp = new QuestionPool(new JsonDatastore("src/test/resources/author/DemoQuestionPoolFollowup.json").loadQuestions());
        ImageTaskResponse responseSet = new ImageTaskResponse("response1",Arrays.asList("plane./images/demoEquine14.jpg","structure0./images/demoEquine14.jpg", "AttachQ1"),Arrays.asList("Lateral","bone","3c"));
        List<ImageTaskResponse> responsesToFile = Arrays.asList(responseSet);
        //test for sucsessfully getting the answer to a parent question
        assertEquals("Lateral", responseSet.findResponseToQuestion(qp.getQuestionFromId("plane./images/demoEquine14.jpg")));
        assertEquals("bone", responseSet.findResponseToQuestion(qp.getQuestionFromId("structure0./images/demoEquine14.jpg")));

        //getting null for a question that doesn't exist
        assertEquals(null, responseSet.findResponseToQuestion(new Question("notAValidQuestion", "notAValidQuestion", "notAValidQuestion", "notAValidQuestion", Arrays.asList("notAValidQuestion","notAValidQuestion2"), "notAValidQuestion")));

        //getting null for a followup question
        assertEquals(null, responseSet.findResponseToQuestion(qp.getQuestionFromId("AttachQ1")));
    }
}
