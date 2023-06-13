import com.fasterxml.jackson.core.JsonProcessingException;
import constants.api.BookingAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import model.Booking;
import model.BookingDates;
import common.utils.CommonMethodAPI;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class BookingAPITest extends BaseTest {
    String token = getToken();
    Random random = new Random();

//    @BeforeTest
//    public void testSetup() {
//        token = getToken();
//        System.out.println("Token retrieved : " + token);
//    }

    @Test
    public void pingService() {
        Response response =  given()
                .spec(requestSpecBuilder())
                .when()
                    .get(BookingAPI.ping)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_CREATED)
                    .assertThat().statusLine("HTTP/1.1 201 Created")
                    .extract().response();
    }

    @Test
    public void getAllBookingId() {
        Response response =  given()
                .spec(requestSpecBuilder())
                .when()
                    .get(BookingAPI.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response();
    }

    @Test
    public void getBookingById() {
        List<Integer> bookingIdList = getAllBookingIdList();
        int randomId = bookingIdList.get(random.nextInt(bookingIdList.size()));
        Response response = given()
                .spec(requestSpecBuilder())
                .when()
                    .get(BookingAPI.bookingById, randomId)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response();
        System.out.println(response.path("firstname").toString());
        System.out.println(response.path("lastname").toString());
    }

    @Test
    public void createBooking() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates("2023-06-19", "2023-06-21");
        Booking booking = new Booking("Erina", "Endah", 59.00, false,
                                bookingDates, "Hey");

        System.out.println(booking.getFirstname());
        Response response =  given()
                .spec(requestSpecBuilder())
                .body(CommonMethodAPI.convertObj(booking))
                .log().body()
                .when()
                    .post(BookingAPI.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response();
        System.out.println(response.path("booking.firstname").toString());
        System.out.println(response.path("bookingid").toString());
    }

    @Test
    public void deleteBookingById() {
        int bookingId = getNewBookingId();
        Response response =  given()
                .spec(requestSpecBuilderWithToken())
                //.cookie("token", token)
                .when()
                    .delete(BookingAPI.bookingById, bookingId)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_CREATED)
                    .extract().response();
        System.out.println(response.statusLine());
        System.out.println("deleted response : " + response.body().toString());
    }

    public List<Integer> getAllBookingIdList() {
        return given()
                .spec(requestSpecBuilder())
                .cookie("token", token)
                .when()
                    .get(BookingAPI.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
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

        return given()
                .spec(requestSpecBuilder())
                .body(obj.toString())
                .when()
                    .post(BookingAPI.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().jsonPath().get("bookingid");
    }
}
