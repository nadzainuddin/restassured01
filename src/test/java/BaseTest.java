import api.Constant;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.auth.Credentials;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class BaseTest {
//    private static RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
//        RequestSpecification requestSpecification = new RequestSpecBuilder().setBaseUri("https://restful-booker.herokuapp.com")
//                .setContentType(ContentType.JSON)
//                .setAccept("application/json")
//                .addFilter(new RequestLoggingFilter())
//                .build();
//        RestAssured.requestSpecification = requestSpecification;
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .setBaseUri(Constant.baseURL)
                .addFilter(new RequestLoggingFilter())
                .build();
        RestAssured.requestSpecification = requestSpec;
    }

//    public static RequestSpecification requestSpecification() {
//        requestSpec = new RequestSpecBuilder()
//                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
//                .setBaseUri(Constant.baseURL)
//                .addFilter(new RequestLoggingFilter())
//                .build();
//        RestAssured.requestSpecification = requestSpec;
//        return RestAssured.requestSpecification;
//    }


    public RequestSpecification requestWithCookie() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .addHeader("Cookie", "token="+getToken())
                //.setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    public String getToken() {
        JSONObject credentials = new JSONObject();
        credentials.put("password", "password123");
        credentials.put("username", "admin");
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(credentials.toString())
//                .auth()
//                .preemptive()
//                .basic("admin", "password123")
//                .log().body()
                .when().post(Constant.baseURL +"/auth")
                .then()
                .log().all()
                .log().ifError()
                .assertThat().statusCode(200)
                .extract().response().jsonPath().get("token").toString();
    }

    public String getTokenWithCookie() {
        JSONObject credentials = new JSONObject();
        credentials.put("username", "admin");
        credentials.put("password", "password123");
        System.out.println(credentials.get("username"));
        Response response =  RestAssured.given()
                .header("Content-Type", "application/json")
                .cookie("token" + getToken())
                .body(credentials.toString())
                .when().get("https://restful-booker.herokuapp.com/auth")
                .then().log().ifError()
                .assertThat().statusCode(200)
                .extract().response();
        return response.path("token").toString();
    }
}
