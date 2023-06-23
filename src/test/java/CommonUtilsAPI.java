import com.fasterxml.jackson.core.JsonProcessingException;
import common.Utils;
import constants.api.BookingAPI;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import java.util.Map;

public class CommonUtilsAPI extends BaseTest {

    static void GET(RequestSpecification requestSpecification, String uri, int status) {
        given().spec(requestSpecification)
            .when()
                .get(uri)
            .then()
                .log().ifError()
                .assertThat().statusCode(status);
    }

    public static JsonPath GETQueryParam(RequestSpecification requestSpecification, String field, String value,
                                         String uri, int status) {
        return given().spec(requestSpecification)
                .queryParam(field, value)
            .when()
                .get(uri)
            .then()
                .log().ifError()
                .assertThat().statusCode(status)
                .extract().response().jsonPath();
    }

    static JsonPath GETQueryParams(RequestSpecification requestSpecification, Map<String, Object> map,
                                   String uri, int status) {
        return given().spec(requestSpecification)
                .queryParams(map)
            .when()
                .get(uri)
            .then()
                .log().ifError()
                .assertThat().statusCode(status)
                .extract().response().jsonPath();
    }

    public static String GETFieldValue(RequestSpecification requestSpecification, String uri, String fieldname,
                                       int bookingId, int status) {
        return given().spec(requestSpecification)
            .when()
                .get(uri, bookingId)
            .then()
                .log().ifError()
                .assertThat().statusCode(status)
                .extract().response().jsonPath().get(fieldname);
    }

    static void GETWithPathParam(RequestSpecification requestSpecification, String uri, int pathParam, int status) {
        given().spec(requestSpecification)
            .when()
                .get(uri, pathParam)
            .then()
                .log().ifError()
                .assertThat().statusCode(status);
    }

    static JsonPath POSTWithBodyReq(RequestSpecification requestSpecification, Object object,
                                    String uri, int status) throws JsonProcessingException {
        return given().spec(requestSpecification)
                .body(Utils.convertObj(object))
            .when()
                .post(uri)
            .then()
                .log().ifError()
                .assertThat().statusCode(status)
                .extract().response().jsonPath();
    }

    static void POSTBodyReq(RequestSpecification requestSpecification, Object object,
                                    String uri, int status) throws JsonProcessingException {
        given().spec(requestSpecification)
                .body(Utils.convertObj(object))
            .when()
                .post(uri)
            .then()
                .log().ifError()
                .assertThat().statusCode(status);
    }

    static void POSTBodyReq(RequestSpecification requestSpecification, Map<String, Object> bodyReq,
                            String uri, int status) throws JsonProcessingException {
        given().spec(requestSpecification)
                .body(bodyReq)
            .when()
                .post(uri)
            .then()
                .log().ifError()
                .assertThat().statusCode(status);
    }

//    public JsonPath POSTQueryParam(String field, String value) {
//
//    }
//
//    public JsonPath POSTQueryParams(Map<String, Object> map) {
//
//    }
}
