package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.TestUtils;
import utils.URL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Base {

    private static Logger log = LogManager.getLogger(Base.class.getName());

    public static final String hostName = "https://5x9m5ed0tj.execute-api.us-east-1.amazonaws.com";

    public static String buildURL(String resource){
        String url = hostName + resource;
        log.info("End Point : " + resource);
        return url;
    }

    public static String getToken() throws IOException {
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
        // Token host name is different than the Application API Host Name
        String url = "https://izaan-test.auth.us-east-1.amazoncognito.com/oauth2/token";

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
        okhttp3.Response response = client.newCall(request).execute();
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


    /**
     * GetSessionId
     * */

    public static String getSessionId(){
        Response response;

        log.info("Starting Test Case : doLogin");

        String loginPayload = generatePayLoadString("login.json");

        String endPointURI = URL.getEndPoint("/rest/auth/1/session");

        response = POSTRequest(endPointURI, loginPayload);
        log.info("Login Response Body: "+response.getBody().asString());

        String strResponse = TestUtils.getResposeString(response);
        JsonPath jsonRes = TestUtils.jsonParser(strResponse);
        String sessionID = jsonRes.getString("session.value");
        log.info("JIRA JSession ID : " + sessionID);
        return sessionID;
    }

    /**
     * Payload generator
     *
     * */

    public static String generatePayLoadString(String filename){
        log.info("Inside PayloadConverter function");
        String filePath = System.getProperty("user.dir")+"/payloads/"+filename;
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public static Response POSTRequest(String uRI, String strJSON) {
        log.info("Inside POSTRequest call");
        RequestSpecification requestSpecification = RestAssured.given().body(strJSON);
        requestSpecification.contentType(ContentType.JSON);
        Response response = requestSpecification.post(uRI);
        log.debug(requestSpecification.log().all());
        return response;
    }

    /**
     * Methods to make http call
     */

    public static Response GETRequest(String uRI) {

        log.info("Inside GETRequest call");
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        Response response = requestSpecification.get(uRI);
        log.debug(requestSpecification.log().all());
        return response;
    }
    public static Response POSTRequest(String uRI, String strJSON, String sessionID) {
        log.info("Inside POSTRequest call");
        RequestSpecification requestSpecification = RestAssured.given().body(strJSON);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("cookie", "JSESSIONID=" + sessionID+"");
        Response response = requestSpecification.post(uRI);
        log.debug("Post Request Specifications"+requestSpecification.log().all());
        return response;
    }


    public static Response PUTRequest(String uRI, String strJSON, String sessionID) {
        log.info("Inside PUTRequest call");
        RequestSpecification requestSpecification = RestAssured.given().body(strJSON);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("cookie", "JSESSIONID=" + sessionID+"");
        Response response = requestSpecification.put(uRI);
        log.debug("PUT Request Specifications: "+requestSpecification.log().all());
        return response;
    }

    public static Response DELETERequest(String uRI, String strJSON) {
        log.info("Inside DELETERequest call");
        RequestSpecification requestSpecification = RestAssured.given().body(strJSON);
        requestSpecification.contentType(ContentType.JSON);
        Response response = requestSpecification.delete(uRI);
        log.debug(requestSpecification.log().all());
        return response;
    }

}
