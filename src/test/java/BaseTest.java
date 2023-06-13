import Constant.api.Booking;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public class BaseTest {
    private static RequestSpecification requestSpec;

//    @BeforeClass
//    public void setup() {
////        RequestSpecification requestSpec = new RequestSpecBuilder()
////                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
////                .setBaseUri(Booking.baseURL)
////                .addFilter(new RequestLoggingFilter())
////                .build();
////        RestAssured.requestSpecification = requestSpec;
//        requestSpecBuilder();
//    }

    public RequestSpecification requestSpecBuilder() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .setBaseUri(Booking.baseURL)
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    public RequestSpecification requestSpecBuilderWithToken() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .addCookie("token", getToken())
                .setBaseUri(Booking.baseURL)
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    public String getToken() {
        JSONObject credentials = new JSONObject();
        credentials.put("password", "password123");
        credentials.put("username", "admin");
        return RestAssured.given()
                .spec(requestSpecBuilder())
                .body(credentials.toString())
                .when()
                    .post(Booking.auth)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().response().jsonPath().get("token").toString();
    }
}
