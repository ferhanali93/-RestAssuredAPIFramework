package utils;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import okhttp3.*;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class FirstRestCallTest {

    /**
     *
     * This class is a simple API test class with all steps in one class
     *
     * */

    public String getToken() throws IOException {
        /**
         * 1. Get Token /oauth2/token
         * 2. Submit /test/submit
         * What do we need to make a call to get Token?
         * a. Host Name
         * b. End Point
         * c. Body
         * d. Authorization -->
         * e. Send headers Content-Type, Authorization
         * f. What type call POST
         * Processing the response
         * a. Validated Status Code
         * b. Get access_token and store in a variable
         *
         * */
        String hostName = "https://izaan-test.auth.us-east-1.amazoncognito.com";
        String endpoint = "/oauth2/token";
        String url = hostName + endpoint;

        OkHttpClient client = new OkHttpClient.Builder().build();
        // Defining what type of information we are sending in the body
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        // Define Content/Body
        RequestBody body = RequestBody.create(mediaType, "scope=izaan_test/post_info&grant_type=client_credentials");
        // Encode username and password
        String encoding = Base64.getEncoder().encodeToString(("1u5io4va9sr45n79fceg2damjf:1qbkthvp7lbc7aavuhhmfg8f2crekor9h2h7abu2oru1nlpj71fe").getBytes("UTF-8"));
        String authorization = "Basic " + encoding;
        // Define complete request
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
       Response response = client.newCall(request).execute();
       // Convert responseBody as String
       String responseBody = response.body().string();
       //System.out.println(responseBody);
        // Convert String jsonbody to JsonPath object
        JsonPath jsonPath = new JsonPath(responseBody);
        // Get key value using jsonpath of the key
        String token = jsonPath.get("access_token");
        System.out.println("Token: " + token);
        // Kill the connection pool to continue
        client.connectionPool().evictAll();
      return token;
    }
    // Make a post call to test/submit endpoint

    public void submitTest() throws IOException {
        // /Users/jahidul/IdeaProjects/APIAutomatinByRestAssured/payloads/submit.json
        String url = "https://5x9m5ed0tj.execute-api.us-east-1.amazonaws.com/test/submit";
        String submitPayload = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ "/payloads/submit.json")));
        RequestSpecification requestSpecification = given().body(submitPayload);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("Authorization", getToken());
        io.restassured.response.Response response = requestSpecification.post(url);
        String responseBody = response.asString();
        System.out.println(responseBody);
        JsonPath jsonPath = new JsonPath(responseBody);
        String actualUserName = jsonPath.get("userName");
        Assert.assertEquals(actualUserName, "123JohnJohn");

    }
   // @Test
    public void getPeople() throws IOException {
        String url = "https://5x9m5ed0tj.execute-api.us-east-1.amazonaws.com/test/people";
        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", getToken())
                .when()
                .get(url);
        String res = response.asString();
        System.out.println(res);
        JsonPath jsonPath = new JsonPath(res);
        String jsonResponseBodyKeyValueAsString = jsonPath.get("body");
        JsonPath jsonPath1 = new JsonPath(jsonResponseBodyKeyValueAsString);
        String actualName = jsonPath1.get("name");
        System.out.println(actualName);
        Assert.assertEquals(actualName, "Luke Skywalker");

    }
}
