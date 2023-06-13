import com.fasterxml.jackson.core.JsonProcessingException;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Random;

import common.Utils;
import model.Booking;
import model.BookingDates;
import constants.api.BookingAPI;

public class BookingTest extends BaseTest {
    String token = getToken();
    Random random = new Random();

    @Test
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

    @Test
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

    @Test
    public void deleteBookingById() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        Response response =  given()
                .spec(requestSpecBuilderWithToken())
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
}
