import com.fasterxml.jackson.core.JsonProcessingException;
import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import common.Utils;
import model.Booking;
import model.BookingDates;
import constants.api.BookingAPI;

public class BookingTest extends BaseTest {
    String token = getToken();
    Random random = new Random();

    @Test(description = "HealthCheck : Success 200 OK")
    @Description("Verify Booking Service Health - OK")
    public void pingService() {
        given()
            .spec(requestSpecBuilder())
            .when()
                .get(BookingAPI.ping)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .assertThat().statusLine("HTTP/1.1 201 Created")
                .extract().response();
    }

    // GetBookingIds Test
    // Get all ids - verify200
    // filter by check in
    // filter by check out
    // filter by non exist firstname | lastname | checkin | checkout
    // filter by partial firstname | lastname
    // filter by combination firstname + lastname
    // filter by combination of incorrect firstname + lastname
    // filter by combination correct firstname + lastname incorrect checkin - checkout date
    // filter by combination correct firstname + lastname + checkin/checkout - incorrect checkin|checkout
    // filter by combination checkin checkout
    // filter by full firstname | lastname + partial lastname + fullname
    // filter by incorrect checkin - checkout date format
    // filter by invalid checkin - checkout date
    // filter by firstname | lastname but value is id
    // filter by non-existing filter => totalprice | depositpaid | additionalneeds
    // filter by firstname and lastname with additional space
    // filter by firstname and lastname with symbol

    @Test(description = "GetBookingIds : Get all ids 200 OK")
    public void getAllBookingId() {
        given()
            .spec(requestSpecBuilder())
            .when()
                .get(BookingAPI.booking)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();
    }

    @Test(description = "GetBookingIds : Success filter by firstname")
    public void getAllBookingIdByFirstname() {
        Booking booking = getBookingDetails();
        Response response = given()
                .spec(requestSpecBuilder())
                .queryParam("firstname", booking.getFirstname())
            .when()
                .get(BookingAPI.booking)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        // other alternative
        // => if there is db, compare with db
        // => limit to 10
        // => choose randomId to compareExpectedValue
        List<Integer> resultIds = response.jsonPath().getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = given()
                    .spec(requestSpecBuilder())
                    .when()
                    .get(BookingAPI.bookingById, resultIds.get(i))
                    .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response().jsonPath().get("firstname");
            Assert.assertEquals(expectedStr, booking.getFirstname());
        }
        //System.out.println(response.jsonPath().getList("bookingid", Integer.class) + " " );
    }

    @Test(description = "GetBookingIds : Success filter by lastname")
    public void getAllBookingIdByLastname() {
        Booking booking = getBookingDetails();
        Response response = given()
            .spec(requestSpecBuilder())
            .queryParam("lastname", booking.getLastname())
            .when()
                .get(BookingAPI.booking)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        List<Integer> resultIds = response.jsonPath().getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = given()
                .spec(requestSpecBuilder())
                .when()
                    .get(BookingAPI.bookingById, resultIds.get(i))
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response().jsonPath().get("lastname");
            Assert.assertEquals(expectedStr, booking.getLastname());
        }
    }

    @Test(description = "GetBookingIds : Success filter by checkIn")
    public void getAllBookingIdByCheckIn() {
        Booking booking = getBookingDetails();
        Response response = given()
                .spec(requestSpecBuilder())
                .queryParam("checkin", booking.getBookingdates().getCheckin())
                .when()
                .get(BookingAPI.booking)
                .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        List<Integer> resultIds = response.jsonPath().getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = given()
                    .spec(requestSpecBuilder())
                    .when()
                    .get(BookingAPI.bookingById, resultIds.get(i))
                    .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response().jsonPath().get("bookingdates.checkin");
            Assert.assertEquals(expectedStr, booking.getBookingdates().getCheckin());
        }
    }

    @Test(description = "GetBookingIds : Success filter by checkOut")
    public void getAllBookingIdByCheckOut() {
        Booking booking = getBookingDetails();
        Response response = given()
                .spec(requestSpecBuilder())
                .queryParam("checkout", booking.getBookingdates().getCheckout())
                .when()
                .get(BookingAPI.booking)
                .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        List<Integer> resultIds = response.jsonPath().getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = given()
                    .spec(requestSpecBuilder())
                    .when()
                    .get(BookingAPI.bookingById, resultIds.get(i))
                    .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response().jsonPath().get("bookingdates.checkout");
            Assert.assertEquals(expectedStr, booking.getBookingdates().getCheckout());
        }
    }

    @Test(description = "GetBookingIds : Success filter by checkIn checkOut")
    public void getAllBookingIdByCheckInCheckOut() {
        Booking booking = getBookingDetails();
        System.out.println(booking.getBookingdates().getCheckin() + "--" + booking.getBookingdates().getCheckout());
        Response response = given()
                .spec(requestSpecBuilder())
                .queryParam("checkin", booking.getBookingdates().getCheckin())
                .queryParam("checkout", booking.getBookingdates().getCheckout())
                .when()
                .get(BookingAPI.booking)
                .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().response();

        List<Integer> resultIds = response.jsonPath().getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            JsonPath expected = given()
                    .spec(requestSpecBuilder())
                    .when()
                    .get(BookingAPI.bookingById, resultIds.get(i))
                    .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().response().jsonPath();
            Assert.assertEquals(expected.get("bookingdates.checkin"), booking.getBookingdates().getCheckin());
            Assert.assertEquals(expected.get("bookingdates.checkout"), booking.getBookingdates().getCheckout());
        }
    }

    // GetBooking
    // filter by correct id
    // filter by non-existing id => max + 1


    @Test(description = "GetBooking : Success to retrieve booking by Id")
    @Description("Verify able to retrieve booking by Id")
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

    // CreateBooking
    // create valid records
    // create empty records
    // create valid records with invalid totalprice format
    // create valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // create records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds

    @Test(description = "CreateBooking : Success create booking with valid data")
    public void createBooking() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates("2023-06-19", "2023-06-21");

        Booking booking = new Booking("Erina", "Endah", 59.00, false,
                                bookingDates, "Hey");

        Response response =  given()
                .spec(requestSpecBuilder())
                .body(Utils.convertObj(booking))
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

    // UpdateBooking (header cookie + auth => optional)
    // update non-existing id => max id + 1
    // update valid records
    // update empty records
    // update valid records with invalid totalprice format
    // update valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // update records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // update partially

    // PartialUpdateBooking (all body field are opt | Cookie & Auth = opt)
    // update partial valid records  => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // update full records
    // update non-existing id => max id + 1
    // update empty records
    // update valid records with invalid totalprice format
    // update valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // update records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds


    /****************************************** DeleteBooking ************************************************/
    @Test(description = "DeleteBooking : Success delete existing id with token")
    public void deleteBookingByIdWithToken() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        given()
            .spec(requestSpecBuilderWithToken())
            .when()
                .delete(BookingAPI.bookingById, bookingId)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .assertThat().statusLine("HTTP/1.1 201 Created");
    }

    @Test(description = "DeleteBooking : Success delete existing id with basic auth")
    public void deleteBookingByIdWithAuth() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        given()
            .spec(requestSpecBuilderWithAuth())
            .when()
                .delete(BookingAPI.bookingById, bookingId)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .assertThat().statusLine("HTTP/1.1 201 Created");
    }

    @Test(description = "DeleteBooking : Success delete existing id without token or basic auth")
    public void deleteBookingByIdWithoutTokenAuth() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        given()
            .spec(requestSpecBuilder())
            .when()
                .delete(BookingAPI.bookingById, bookingId)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .assertThat().statusLine("HTTP/1.1 403 Forbidden");
    }

    @Test(description = "DeleteBooking : Success delete non-existing id with token")
    public void deleteNonExistingIdWithToken() {
        //int nonExistingId = Collections.max(getAllBookingIdList()) + 1;
        given()
            .spec(requestSpecBuilderWithToken())
            .when()
                .delete(BookingAPI.bookingById, -1)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .assertThat().statusLine("HTTP/1.1 405 Method Not Allowed");
    }

    @Test(description = "DeleteBooking : Success delete non-existing id with basic auth")
    public void deleteNonExistingIdWithBasicAuth() {
        //int nonExistingId = Collections.max(getAllBookingIdList()) + 1;
        given()
            .spec(requestSpecBuilderWithAuth())
            .when()
                .delete(BookingAPI.bookingById, -1)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .assertThat().statusLine("HTTP/1.1 405 Method Not Allowed");
    }

    @Test(description = "DeleteBooking : Success delete booking by Id without basic token")
    public void deleteBookingByIdWithoutToken() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        given()
                .spec(requestSpecBuilder())
                .when()
                .delete(BookingAPI.bookingById, bookingId)
                .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
                .assertThat().statusLine("HTTP/1.1 405 Method Not Allowed");
    }

    public List<Integer> getAllBookingIdList() {
//        return given()
//                .spec(requestSpecBuilder())
//                .cookie("token", token)
//                .when()
//                    .get(BookingAPI.booking)
//                .then()
//                    .log().ifError()
//                    .assertThat().statusCode(HttpStatus.SC_OK)
//                    .extract().jsonPath().getList("bookingid");
//
        List<Integer> bookingIds = given()
                .spec(requestSpecBuilder())
                .cookie("token", token)
                .when()
                .get(BookingAPI.booking)
                .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getList("bookingid");

        System.out.println("Before : " + bookingIds);
        Collections.sort(bookingIds);
        System.out.println("After : " + bookingIds);
        return bookingIds;

    }

    public int getNewBookingId() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates("2023-06-19", "2023-06-21");
        Booking booking = new Booking("Yaya", "Zaman", 59.00, false,
                bookingDates, "Nothing");

        return given()
                .spec(requestSpecBuilder())
                .body(Utils.convertObj(booking))
                .when()
                    .post(BookingAPI.booking)
                .then()
                    .log().ifError()
                    .assertThat().statusCode(HttpStatus.SC_OK)
                    .extract().jsonPath().get("bookingid");
    }

    public Booking getBookingDetails() {
        List<Integer> bookingIdList = getAllBookingIdList();
        int randomId = bookingIdList.get(random.nextInt(bookingIdList.size()));
        return given()
                .spec(requestSpecBuilder())
                .when()
                .get(BookingAPI.bookingById, randomId)
                .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getObject("", Booking.class);
    }
}
