package apitest;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static base.Base.buildURL;
import static base.Base.getToken;
import static io.restassured.RestAssured.given;

@Test
public class SubmitResourceTest {
    public void submitTest() throws IOException {
        // /Users/jahidul/IdeaProjects/APIAutomatinByRestAssured/payloads/submit.json
        //String url = "https://5x9m5ed0tj.execute-api.us-east-1.amazonaws.com/test/submit";
        String token = "Bearer "+ getToken();
        String submitPayload = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ "/payloads/submit.json")));
        RequestSpecification requestSpecification = given().body(submitPayload);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", token );
        io.restassured.response.Response response = requestSpecification.post(buildURL("/test/submit"));
        String responseBody = response.asString();
        System.out.println(responseBody);
        JsonPath jsonPath = new JsonPath(responseBody);
        String actualUserName = jsonPath.get("userName");
        Assert.assertEquals(actualUserName, "123JohnJohn");

    }
}
