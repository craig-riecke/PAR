package edu.ithaca.dragon.par.io;

import edu.ithaca.dragon.par.domainModel.QuestionPool;

import edu.ithaca.dragon.par.studentModel.UserResponse;
import edu.ithaca.dragon.util.JsonUtil;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserResponseRecordTest {
     @Test
    public void toJsonAndBackTest() throws IOException{
         QuestionPool qp = new QuestionPool(new JsonDatastore("src/test/resources/author/SampleQuestionPool.json"));
         UserResponse response=new UserResponse("kandace",qp.getQuestionFromId("StructureQ2"),"hi");

         UserResponseRecord responseRecord=new UserResponseRecord(response);
         //write to Json
         JsonUtil.toJsonFile("src/test/resources/autoGenerated/UserResponseRecordTest-toJsonAndBackTest.json", responseRecord);
         // read
         UserResponseRecord record2=JsonUtil.fromJsonFile("src/test/resources/autoGenerated/UserResponseRecordTest-toJsonAndBackTest.json", UserResponseRecord.class);
         //
         UserResponse fromRecord= record2.buildUserResponse(qp);
         //
         assertEquals(response,fromRecord);

         Path path = Paths.get("src/test/resources/autoGenerated/UserResponseRecordTest-toJsonAndBackTest.json");
         assertTrue(Files.deleteIfExists(path));
     }
}
