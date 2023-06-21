import constants.api.BookingAPI;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CommonUtilsAPI extends BaseTest {

    static void GET(RequestSpecification requestSpecification, String uri, int status) {
        given().spec(requestSpecification)
                .when()
                .get(uri)
                .then()
                .log().ifError()
                .assertThat().statusCode(status);
    }

    public static JsonPath GETQueryParam(RequestSpecification requestSpecification, String field, String value, String uri, int status) {
        return given().spec(requestSpecification)
                .queryParam(field, value)
                .when()
                .get(uri)
                .then()
                .log().ifError()
                .assertThat().statusCode(status)
                .extract().response().jsonPath();
    }

    static JsonPath GETQueryParams(RequestSpecification requestSpecification, Map<String, Object> map, String uri, int status) {
        return given().spec(requestSpecification)
                .queryParams(map)
                .when()
                .get(uri)
                .then()
                .log().ifError()
                .assertThat().statusCode(status)
                .extract().response().jsonPath();
    }

//    public JsonPath POSTQueryParam(String field, String value) {
//
//    }
//
//    public JsonPath POSTQueryParams(Map<String, Object> map) {
//
//    }
}
