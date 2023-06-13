import Constant.api.Booking;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class BookingTest extends BaseTest {
    String token = getToken();
    Random random = new Random();

//    @BeforeTest
//    public void testSetup() {
//        token = getToken();
//        System.out.println("Token retrieved : " + token);
//    }

    @Test
    public void pingService() {
        Response response =  RestAssured.given()
                .spec(requestSpecBuilder())
                .when()
                    .get(Booking.ping)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(201)
                    .assertThat().statusLine("HTTP/1.1 201 Created")
                    .extract().response();
    }

    @Test
    public void getAllBookingId() {
        Response response =  RestAssured.given()
                .spec(requestSpecBuilder())
                .when()
                    .get(Booking.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().response();
    }

    @Test
    public void getBookingById() {
        List<Integer> bookingIdList = getAllBookingIdList();
        int randomId = bookingIdList.get(random.nextInt(bookingIdList.size()));
        Response response =  RestAssured.given()
                .spec(requestSpecBuilder())
                .when()
                    .get(Booking.bookingById, randomId)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().response();
        System.out.println(response.path("firstname").toString());
        System.out.println(response.path("lastname").toString());
    }

    @Test
    public void createBooking() {
        JSONObject innerObj = new JSONObject();
        innerObj.put("checkin", "2023-06-19");
        innerObj.put("checkout", "2023-06-21");

        JSONObject obj = new JSONObject();
        obj.put("firstname", "Yaya");
        obj.put("lastname", "Haleel");
        obj.put("totalprice", 9.99);
        obj.put("depositpaid", true);
        obj.put("bookingdates", innerObj);
        obj.put("additionalneeds", "none");

        Response response =  RestAssured.given()
                .spec(requestSpecBuilder())
                .body(obj.toString())
                .when()
                    .post(Booking.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().response();
        System.out.println(response.path("booking.firstname").toString());
        System.out.println(response.path("bookingid").toString());
    }

    @Test
    public void deleteBookingById() {
        int bookingId = getNewBookingId();
        Response response =  RestAssured.given()
                .spec(requestSpecBuilderWithToken())
                //.cookie("token", token)
                .when()
                    .delete(Booking.bookingById, bookingId)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(201)
                    .extract().response();
        System.out.println(response.statusLine());
        System.out.println("deleted response : " + response.body().toString());
    }

    public List<Integer> getAllBookingIdList() {
        return RestAssured.given()
                .spec(requestSpecBuilder())
                .cookie("token", token)
                .when()
                    .get(Booking.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().jsonPath().getList("bookingid");
    }

    public int getNewBookingId() {
        JSONObject innerObj = new JSONObject();
        innerObj.put("checkin", "2023-06-19");
        innerObj.put("checkout", "2023-06-21");

        JSONObject obj = new JSONObject();
        obj.put("firstname", "Yaya");
        obj.put("lastname", "Haleel");
        obj.put("totalprice", 9.99);
        obj.put("depositpaid", true);
        obj.put("bookingdates", innerObj);
        obj.put("additionalneeds", "none");

        return RestAssured.given()
                .spec(requestSpecBuilder())
                .body(obj.toString())
                .when()
                    .post(Booking.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(200)
                    .extract().jsonPath().get("bookingid");
    }
}
