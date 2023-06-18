import constants.api.BookingAPI;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.authentication.AuthenticationScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.given;

public class BaseTest {
    Base64 base64 = new Base64();

    public RequestSpecification requestSpecBuilder() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .setBaseUri(BookingAPI.baseURL)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }

    public RequestSpecification requestSpecBuilderWithToken() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .addCookie("token", getToken())
                .setBaseUri(BookingAPI.baseURL)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }

    public RequestSpecification requestSpecBuilderWithAuth() {
        String username = "admin";
        String password = "password123";
        String encodedString = new String(base64.encode((username+":"+password).getBytes()));
        return new RequestSpecBuilder()
                .addHeader("Content-Type", String.valueOf(ContentType.JSON))
                .addHeader("Authorization", "Basic " + encodedString)
                .setBaseUri(BookingAPI.baseURL)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }

    public ResponseSpecification responseSpecBuilder(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
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
                    .post(BookingAPI.auth)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().response().jsonPath().get("token").toString();
    }
}
