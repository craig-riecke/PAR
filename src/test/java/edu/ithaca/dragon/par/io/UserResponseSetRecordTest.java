package edu.ithaca.dragon.par.io;

import edu.ithaca.dragon.par.domainModel.QuestionPool;
import edu.ithaca.dragon.par.studentModel.UserResponseSet;
import edu.ithaca.dragon.util.JsonUtil;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserResponseSetRecordTest {
    @Test
    public void toJsonAndBackTest() throws IOException {
        List<ImageTaskResponse> responsesFromFile = JsonUtil.listFromJsonFile("src/test/resources/author/SampleResponses.json", ImageTaskResponse.class);
        UserResponseSet que=new UserResponseSet(responsesFromFile.get(0).getUserId());
        QuestionPool qp = new QuestionPool(new JsonDatastore("src/test/resources/author/SampleQuestionPool.json"));


        UserResponseSetRecord myURSR=new UserResponseSetRecord(que);

        //write to Json
        JsonUtil.toJsonFile("src/test/resources/autoGenerated/UserResponseSetRecordTest-toJsonAndBackTest.json",myURSR);
        //read from Json
        UserResponseSetRecord URSRFromJson=JsonUtil.fromJsonFile("src/test/resources/autoGenerated/UserResponseSetRecordTest-toJsonAndBackTest.json",UserResponseSetRecord.class);
        //make UserResponseSet from UserResponseSAetRecord
        UserResponseSet fromRecord=URSRFromJson.buildUserResponseSet(qp);
        //compare response sets
        assertEquals(que,fromRecord);

        Path path = Paths.get("src/test/resources/autoGenerated/UserResponseSetRecordTest-toJsonAndBackTest.json");
        assertTrue(Files.deleteIfExists(path));
    }
}
