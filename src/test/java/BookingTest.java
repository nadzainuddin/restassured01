import api.Constant;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class BookingTest extends BaseTest {
    String token;
    Random random = new Random();

    @BeforeTest
    public void testSetup() {
        token = getToken();
        System.out.println("Token retrieved : " + token);
    }

    @Test
    public void pingService() {
        Response response =  RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get(Constant.ping)
                .then().log().ifError()
                .extract().response();
        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(response.statusLine(), "HTTP/1.1 201 Created");
    }

    @Test
    public void getAllBookingId() {
        Response response =  RestAssured.given()
                .header("Content-Type", "application/json")
                .when().get("https://restful-booker.herokuapp.com/booking")
                .then().log().ifError()
                .assertThat().statusCode(200)
                .extract().response();
    }

    @Test
    public void getBookingById() {
        List<Integer> bookingIdList = getAllBookingIdList();
        int randomId = bookingIdList.get(random.nextInt(bookingIdList.size()));
        Response response =  RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get(Constant.bookingById, randomId)
                .then().log().ifError()
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
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body(obj.toString())
                .when()
                .post(Constant.booking)
                .then().log().ifError()
                .assertThat().statusCode(200)
                .extract().response();
        System.out.println(response.path("booking.firstname").toString());
        System.out.println(response.path("bookingid").toString());
    }

    @Test
    public void deleteBookingById() {
        int bookingId = getNewBookingId();
        Response response =  RestAssured.given()
                .header("Content-Type", "application/json")
                .cookie("token", token)
                .when()
                .delete(Constant.bookingById, bookingId)
                .then()
                .log().ifError()
                .assertThat().statusCode(201)
                .extract().response();
        System.out.println(response.statusLine());
    }

    public List<Integer> getAllBookingIdList() {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .cookie("token=" + token)
                //.header(Cookie, "token=" + token)
                .when().get("https://restful-booker.herokuapp.com/booking")
                .then().log().ifError()
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
                .header("Content-Type", "application/json")
                .body(obj.toString())
                .when()
                .post(Constant.booking)
                .then().log().ifError()
                .assertThat().statusCode(200)
                .extract().jsonPath().get("bookingid");
    }
}
