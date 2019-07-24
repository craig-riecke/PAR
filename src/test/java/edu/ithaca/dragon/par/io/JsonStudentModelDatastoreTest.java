package edu.ithaca.dragon.par.io;


import edu.ithaca.dragon.par.pedagogicalModel.TaskGenerator;
import edu.ithaca.dragon.par.studentModel.StudentModel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStudentModelDatastoreTest {

    @Test
    public void loadBadStudentModelTest() throws IOException{
        //throws exception when StudentModelDatastore does not have a studentModelFilePath
        StudentModelDatastore studentModelDatastoreA = new JsonStudentModelDatastore("src/test/resources/author/SampleQuestionPool.json", null);
        assertNull(studentModelDatastoreA.getStudentModel("asd"));
    }

    @Test
    public void loadIndividualStudentTest() throws IOException{
        StudentModelDatastore studentModelDatastore = new JsonStudentModelDatastore("src/test/resources/author/SampleQuestionPool.json", "src/test/resources/author/students");

        //load an existing file and make sure it exists
        StudentModel testUser100 = studentModelDatastore.getStudentModel("TestUser100");
        assertEquals("TestUser100", testUser100.getUserId());

        //try to load in a non-existing file
        StudentModel notAUser = studentModelDatastore.getStudentModel("ThisIsNotAValidUserId");
        assertNull(notAUser);
    }

    @Test
    public void getOrCreateStudentModelTest(@TempDir Path tempDir) throws IOException{
        JsonStudentModelDatastore jsonStudentModelDatastore = new JsonStudentModelDatastore("src/test/resources/author/SampleQuestionPool.json", tempDir.toString());
        Path newStudentPath = tempDir.resolve("TestUser100.json");
        Files.copy(Paths.get("src/test/resources/author/students/TestUser100.json"), newStudentPath, StandardCopyOption.REPLACE_EXISTING);

        //load a user that already has a file
        StudentModel studentModel1 = jsonStudentModelDatastore.getOrCreateStudentModel("TestUser100");
        assertEquals("TestUser100", studentModel1.getUserId());

        //load a user that does not have a file
        StudentModel studentModel2 = jsonStudentModelDatastore.getOrCreateStudentModel("NewUser1");
        assertEquals("NewUser1", studentModel2.getUserId());
        Path path1 = Paths.get("src/test/resources/author/students/NewUser1");
        assertFalse(Files.exists(path1)); //a file should not have have been written until an imageTask is submitted

        //make a change to a user, log them out, then reload them to see if changes were saved
        assertEquals(0, studentModel1.getSeenQuestionCount());
        TaskGenerator.makeTask(studentModel1);
        jsonStudentModelDatastore.imageTaskResponseSubmitted(studentModel1, new ImageTaskResponse("TestUser100", Arrays.asList("PlaneQ1"), Arrays.asList("longitudinal")));
        assertEquals(1, studentModel1.getSeenQuestionCount());
        jsonStudentModelDatastore.logout("TestUser100");
        StudentModel studentModel3 = jsonStudentModelDatastore.getOrCreateStudentModel("TestUser100");
        assertEquals(1, studentModel3.getSeenQuestionCount());
    }
}
