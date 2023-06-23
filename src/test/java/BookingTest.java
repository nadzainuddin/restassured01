import com.fasterxml.jackson.core.JsonProcessingException;
import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.BookingResponse;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import common.Utils;
import model.Booking;
import model.BookingDates;
import constants.api.BookingAPI;

public class BookingTest extends BaseTest {
    String token = getToken();
    Random random = new Random();

    // ********************************* HealthCheck *************************************** //
    @Test(description = "HealthCheck : Success 200 OK")
    @Description("Verify Booking Service Health - OK")
    public void pingService() {
        CommonUtilsAPI.GET(requestSpecBuilder(), BookingAPI.ping, HttpStatus.SC_CREATED);
    }

    // ********************************* GetBookingIds Test ******************************** //
    @Test(description = "GetBookingIds : Success retrieving all booking ids")
    public void getAllBookingId() {
        CommonUtilsAPI.GET(requestSpecBuilder(), BookingAPI.booking, HttpStatus.SC_OK);
    }

    @Test(description = "GetBookingIds : Success filter by firstname")
    public void getAllBookingIdByFirstname() {
        Booking booking = getBookingDetails();
        JsonPath resp = CommonUtilsAPI.GETQueryParam(
                requestSpecBuilder(),"firstname", booking.getFirstname(), BookingAPI.booking,
                HttpStatus.SC_OK);
        // other alternative
        // => if there is db, compare with db
        // => limit to 10
        // => choose randomId to compareExpectedValue
        List<Integer> resultIds = resp.getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = CommonUtilsAPI.GETFieldValue(requestSpecBuilder(),
                    BookingAPI.bookingById, "firstname", resultIds.get(i), HttpStatus.SC_OK);
            Assert.assertEquals(expectedStr, booking.getFirstname());
        }
    }

    @Test(description = "GetBookingIds : Success filter by lastname")
    public void getAllBookingIdByLastname() {
        Booking booking = getBookingDetails();
        JsonPath resp = CommonUtilsAPI.GETQueryParam(
                requestSpecBuilder(),"lastname", booking.getLastname(), BookingAPI.booking, HttpStatus.SC_OK);

        List<Integer> resultIds = resp.getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = CommonUtilsAPI.GETFieldValue(requestSpecBuilder(),
                    BookingAPI.bookingById, "lastname", resultIds.get(i), HttpStatus.SC_OK);
            Assert.assertEquals(expectedStr, booking.getLastname());
        }
    }

    @Test(description = "GetBookingIds : Success filter by firstname lastname")
    public void getAllBookingIdByFirstnameLastname() {
        Booking booking = getBookingDetails();
        Map<String, Object> map = new HashMap<>();
        map.put("firstname", booking.getFirstname());
        map.put("lastname", booking.getLastname());

        JsonPath resp = CommonUtilsAPI.GETQueryParams(
                requestSpecBuilder(),map, BookingAPI.booking, HttpStatus.SC_OK);
        List<Integer> resultIds = resp.getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            JsonPath res = CommonUtilsAPI.GET(requestSpecBuilder(),
                    BookingAPI.bookingById, resultIds.get(i), HttpStatus.SC_OK);
            Assert.assertEquals(res.get("firstname"), booking.getFirstname());
            Assert.assertEquals(res.get("lastname"), booking.getLastname());
        }
    }

    @Test(description = "GetBookingIds : Success filter by checkIn")
    public void getAllBookingIdByCheckIn() {
        Booking booking = getBookingDetails();
        JsonPath resp = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(),"checkin",
                booking.getBookingdates().getCheckin(), BookingAPI.booking, HttpStatus.SC_OK);

        List<Integer> resultIds = resp.getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = CommonUtilsAPI.GETFieldValue(requestSpecBuilder(),
                    BookingAPI.bookingById, "bookingdates.checkin", resultIds.get(i), HttpStatus.SC_OK);
            Assert.assertEquals(expectedStr, booking.getBookingdates().getCheckin());
        }
    }

    @Test(description = "GetBookingIds : Success filter by checkOut")
    public void getAllBookingIdByCheckOut() {
        Booking booking = getBookingDetails();
        JsonPath resp = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(),"checkout",
                booking.getBookingdates().getCheckout(), BookingAPI.booking, HttpStatus.SC_OK);

        List<Integer> resultIds = resp.getList("bookingid", Integer.class);
        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            String expectedStr = CommonUtilsAPI.GETFieldValue(requestSpecBuilder(),
                    BookingAPI.bookingById, "bookingdates.checkout", resultIds.get(i), HttpStatus.SC_OK);
            Assert.assertEquals(expectedStr, booking.getBookingdates().getCheckout());
        }
    }

    @Test(description = "GetBookingIds : Success filter by checkIn checkOut")
    public void getAllBookingIdByCheckInCheckOut() {
        Booking booking = getBookingDetails();
        Map<String, Object> map = new HashMap<>();
        map.put("checkin", booking.getBookingdates().getCheckin());
        map.put("checkout", booking.getBookingdates().getCheckout());

        JsonPath resp = CommonUtilsAPI.GETQueryParams(requestSpecBuilder(),map,
                BookingAPI.booking, HttpStatus.SC_OK);
        List<Integer> resultIds = resp.getList("bookingid", Integer.class);

        int count = resultIds.size() > 5 ? 5 : 1; // since the value is using existing data => should match at least 1
        for(int i = 0; i<count; i++) {
            JsonPath res = CommonUtilsAPI.GET(requestSpecBuilder(),
                    BookingAPI.bookingById, resultIds.get(i), HttpStatus.SC_OK);
            Assert.assertEquals(res.get("bookingdates.checkin"), booking.getBookingdates().getCheckin());
            Assert.assertEquals(res.get("bookingdates.checkout"), booking.getBookingdates().getCheckout());
        }
    }

    @Test(description = "GetBookingIds : Failed filter by firstname partially")
    public void getAllBookingIdByFirstnamePartially() {
        String firstname = getBookingDetails().getFirstname();
        if (firstname.length() > 3) {
            JsonPath resp = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), "firstname",
                    firstname.substring(0, 3), BookingAPI.booking, HttpStatus.SC_OK);
            // Should be 0 since there is no requirement allowing firstname to be searched partially
            Assert.assertEquals(resp.getList("bookingid").size(), 0);
        }
    }

    @Test(description = "GetBookingIds : Failed filter by lastname partially")
    public void getAllBookingIdByLastnamePartially() {
        String lastname = getBookingDetails().getLastname();
        if (lastname.length() > 3) {
            JsonPath resp = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), "lastname",
                    lastname.substring(0, 3), BookingAPI.booking, HttpStatus.SC_OK);
            // Should be 0 since there is no requirement allowing lastname to be searched partially
            Assert.assertEquals(resp.getList("bookingid").size(), 0);
        }
    }

    @Test(description = "GetBookingIds : Failed to filter by correct firstname incorrect lastname partially")
    public void getAllBookingIdByFirstnameIncorrectLastname() {
        Map<String, Object> req = new HashMap<>();
        req.put("firstname", getBookingDetails().getFirstname());
        req.put("lastname", getBookingDetails().getLastname()+"$");

        JsonPath res = CommonUtilsAPI.GETQueryParams(requestSpecBuilder(), req,
                BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by firstname and lastname with spaces")
    public void getAllBookingIdByFirstnameLastnameWithSpaces() {
        Map<String, Object> req = new HashMap<>();
        req.put("firstname", getBookingDetails().getFirstname() + " ");
        req.put("lastname", " " + getBookingDetails().getLastname());

        JsonPath res = CommonUtilsAPI.GETQueryParams(requestSpecBuilder(), req,
                BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by incorrect firstname correct lastname partially")
    public void getAllBookingIdByLastnameIncorrectFirstname() {
        Map<String, Object> req = new HashMap<>();
        req.put("firstname", "%"+getBookingDetails().getFirstname());
        req.put("lastname", getBookingDetails().getLastname());

        JsonPath res = CommonUtilsAPI.GETQueryParams(requestSpecBuilder(), req,
                BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by non-existing param")
    public void getAllBookingIdByNonExistingParam() {
        CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), "test", "Josh",
                BookingAPI.booking, HttpStatus.SC_NOT_FOUND);
    }

    @Test(description = "GetBookingIds : Failed to filter by totalprice")
    public void getAllBookingIdByTotalPrice() {
        JsonPath res = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), "totalprice",
                getBookingDetails().getTotalprice(), BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by depositpaid")
    public void getAllBookingIdByDepositPaid() {
        JsonPath res = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), "depositpaid",
                getBookingDetails().isDepositpaid(), BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by additionalneeds")
    public void getAllBookingIdByAdditionalNeeds() {
        JsonPath res = CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), "additionalneeds",
                getBookingDetails().getAdditionalneeds(), BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by misspelled param")
    public void getAllBookingIdByMisspelledParam() {
        Booking booking = getBookingDetails();
        Map<String, Object> req = new HashMap<>();
        req.put("firstname", booking.getFirstname());
        req.put("lasname", booking.getLastname());
        req.put("checkin", booking.getBookingdates().getCheckin());
        req.put("checkout", booking.getBookingdates().getCheckout());

        JsonPath res = CommonUtilsAPI.GETQueryParams(requestSpecBuilder(), req,
                BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(
            description = "GetBookingIds : Failed to filter by non-existing value",
            dataProvider = "non-existing-value"
    )
    public void getAllBookingIdByNonExistingValue(String fieldname, String value) {
        Map<String, Object> req = new HashMap<>();
        req.put(fieldname, value);
        Response res = CommonUtilsAPI.GETQueryParamResp(requestSpecBuilder(), fieldname, value,
                BookingAPI.booking, HttpStatus.SC_OK);
        List<Integer> resultIds = res.jsonPath().getList("bookingid", Integer.class);
        Assert.assertEquals(resultIds.size(), 0, "Records found with " + fieldname + " : " + value);
    }

    @Test(
            description = "GetBookingIds : Failed to filter by invalid bookingDates value",
            dataProvider = "invalid-date"
    )
    public void getAllBookingIdByInvalidBookingDates(String fieldname, String value) {
        CommonUtilsAPI.GETQueryParam(requestSpecBuilder(), fieldname, value,
                BookingAPI.booking, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test(
            description = "GetBookingIds : Failed to filter by correct firstname lastname incorrect bookingDates",
            dataProvider = "incorrect-date"
    )
    public void getAllBookingIdByCorrectNameIncorrectBookingDates(String fieldname, String value) {
        Booking booking = getBookingDetails();
        Map<String, Object> req = new HashMap<>();
        req.put("firstname", booking.getFirstname());
        req.put("lastname", booking.getLastname());
        req.put(fieldname, value);

        JsonPath res = CommonUtilsAPI.GETQueryParams(requestSpecBuilder(), req, BookingAPI.booking,
                HttpStatus.SC_OK);
        Assert.assertEquals(res.getList("bookingid").size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by incorrect checkin format")
    public void getAllBookingIdByIncorrectCheckInDateFormat() {
        Booking booking = getBookingDetails();
        Response res = CommonUtilsAPI.GETQueryParamResp(requestSpecBuilder(), "checkin",
                booking.getBookingdates().getCheckin().replaceAll("-", "\\."),
                BookingAPI.booking, HttpStatus.SC_OK);
        List<Integer> resultIds = res.jsonPath().getList("bookingid", Integer.class);
        Assert.assertEquals(resultIds.size(), 0);
    }

    @Test(description = "GetBookingIds : Failed to filter by incorrect checkout format")
    public void getAllBookingIdByIncorrectCheckOutDateFormat() {
        Booking booking = getBookingDetails();
        Response res = CommonUtilsAPI.GETQueryParamResp(requestSpecBuilder(), "checkout",
                booking.getBookingdates().getCheckout().replaceAll("-", "/"),
                BookingAPI.booking, HttpStatus.SC_OK);
        List<Integer> resultIds = res.jsonPath().getList("bookingid", Integer.class);
        Assert.assertEquals(resultIds.size(), 0);
    }

    // **************************************** GetBooking ************************************ //
    @Test(description = "GetBooking : Success to retrieve booking by Id")
    public void getBookingById() {
        List<Integer> bookingIdList = getAllBookingIdList();
        int randomId = bookingIdList.get(random.nextInt(bookingIdList.size()));
        CommonUtilsAPI.GETWithPathParam(requestSpecBuilder(), BookingAPI.bookingById, randomId,
                HttpStatus.SC_OK);
    }

    @Test(description = "GetBooking : Failed to retrieve booking by non-existing Id")
    public void getBookingByNonExistingId() {
        CommonUtilsAPI.GETWithPathParam(requestSpecBuilder(), BookingAPI.bookingById, -1,
                HttpStatus.SC_NOT_FOUND);
        // If there is access to db => sort and get max => max + 1
        // (unable to sort by collections as the id is dynamically retrieved)
    }

    // CreateBooking
    // create valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // create records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds

    @Test(description = "CreateBooking : Success create booking with valid data")
    public void createBooking() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates(
                "2023-06-19",
                "2023-06-21");

        Booking booking = new Booking(
                "Erina",
                "Endah",
                59.00,
                false,
                bookingDates,
                "Hey");

        JsonPath response =  CommonUtilsAPI.POSTWithBodyReq(requestSpecBuilder(), booking,
                BookingAPI.booking, HttpStatus.SC_OK);
        Assert.assertEquals(response.get("booking.firstname"), booking.getFirstname());
        Assert.assertEquals(response.get("booking.lastname"), booking.getLastname());
        Assert.assertEquals(Double.valueOf(response.get("booking.totalprice").toString()), booking.getTotalprice());
        Assert.assertEquals(response.get("booking.depositpaid"), booking.isDepositpaid());
        Assert.assertEquals(response.get("booking.bookingdates.checkin"), booking.getBookingdates().getCheckin());
        Assert.assertEquals(response.get("booking.bookingdates.checkout"), booking.getBookingdates().getCheckout());
        Assert.assertEquals(response.get("booking.additionalneeds"), booking.getAdditionalneeds());
    }

    @Test(description = "CreateBooking : Failed create booking with empty data")
    public void createBookingWithEmptyData() throws JsonProcessingException {
        Booking booking = null;
        CommonUtilsAPI.POSTBodyReq(requestSpecBuilder(), booking, BookingAPI.booking, HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "CreateBooking : Failed create booking with invalid bookingDates format")
    public void createBookingWithInvalidBookingDatesFormat() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates(
                "2023/06/19",
                "2023/06/21");

        Booking booking = new Booking(
                "Erina",
                "Endah",
                59.00,
                false,
                bookingDates,
                "Hey");
        CommonUtilsAPI.POSTBodyReq(requestSpecBuilder(), booking, BookingAPI.booking, HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "CreateBooking : Failed create booking with invalid bookingDates format [2]")
    public void createBookingWithInvalidBookingDates2Format() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates(
                "19.06.2023",
                "21.06.2023");

        Booking booking = new Booking(
                "Erina",
                "Endah",
                59.00,
                false,
                bookingDates,
                "Hey");
        CommonUtilsAPI.POSTBodyReq(requestSpecBuilder(), booking, BookingAPI.booking, HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "CreateBooking : Failed create booking with invalid deposit format")
    public void createBookingWithInvalidDepositFormat() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates(
                "2023-06-19",
                "2023-06-21");

        Map<String, Object> map = new HashMap<>();
        map.put("firstname", "Erina");
        map.put("lastname", "Endah");
        map.put("totalprice", 99);
        map.put("depositpaid", "true");
        map.put("bookingdates", bookingDates);
        map.put("additionalneeds", "nothing");
        CommonUtilsAPI.POSTBodyReq(requestSpecBuilder(), map, BookingAPI.booking, HttpStatus.SC_BAD_REQUEST);
    }

    @Test(description = "CreateBooking : Failed create booking with invalid totalprice format")
    public void createBookingWithInvalidTotalPriceFormat() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates(
                "2023-06-19",
                "2023-06-21");

        Map<String, Object> map = new HashMap<>();
        map.put("firstname", "Erina");
        map.put("lastname", "Endah");
        map.put("totalprice", "99");
        map.put("depositpaid", true);
        map.put("bookingdates", bookingDates);
        map.put("additionalneeds", "nothing");
        CommonUtilsAPI.POSTBodyReq(requestSpecBuilder(), map, BookingAPI.booking, HttpStatus.SC_BAD_REQUEST);
    }

    // ********************************* UpdateBooking ************************************** //
    // (header cookie + auth => optional)
    // update non-existing id => max id + 1
    // update valid records
    // update empty records
    // update valid records with invalid totalprice format
    // update valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // update records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    // update partially

    // ************************************ PartialUpdateBooking ************************************* //
    // (all body field are opt | Cookie & Auth = opt)
    // update partial valid records  => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
    @Test(description = "PartialUpdateBooking : Success update firstname")
    public void partialUpdateFirstName() throws JsonProcessingException {
        BookingResponse details = getNewBookingDetails();
        String updatedName = details.getBooking().getFirstname() + "0";

        Map<String, Object> map = new HashMap<>();
        map.put("firstname", updatedName);
        JsonPath res = CommonUtilsAPI.PATCH(requestSpecBuilderWithToken(), map, BookingAPI.bookingById,
                details.getBookingid(), HttpStatus.SC_OK);
        Assert.assertEquals(res.get("firstname"), updatedName);
    }
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
        CommonUtilsAPI.DELETEWithPathParam(requestSpecBuilderWithToken(),
                BookingAPI.bookingById, bookingId, HttpStatus.SC_CREATED);
    }

    @Test(description = "DeleteBooking : Success delete existing id with basic auth")
    public void deleteBookingByIdWithAuth() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        CommonUtilsAPI.DELETEWithPathParam(requestSpecBuilderWithAuth(),
                BookingAPI.bookingById, bookingId, HttpStatus.SC_CREATED);
    }

    @Test(description = "DeleteBooking : Success delete existing id without token or basic auth")
    public void deleteBookingByIdWithoutTokenAuth() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        CommonUtilsAPI.DELETEWithPathParam(requestSpecBuilder(),
                BookingAPI.bookingById, bookingId, HttpStatus.SC_FORBIDDEN);
    }

    @Test(description = "DeleteBooking : Success delete non-existing id with token")
    public void deleteNonExistingIdWithToken() {
        //int nonExistingId = Collections.max(getAllBookingIdList()) + 1;
        CommonUtilsAPI.DELETEWithPathParam(requestSpecBuilder(),
                BookingAPI.bookingById, -1, HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test(description = "DeleteBooking : Success delete non-existing id with basic auth")
    public void deleteNonExistingIdWithBasicAuth() {
        //int nonExistingId = Collections.max(getAllBookingIdList()) + 1;
        CommonUtilsAPI.DELETEWithPathParam(requestSpecBuilderWithAuth(),
                BookingAPI.bookingById, -1, HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test(description = "DeleteBooking : Success delete booking by Id without basic token")
    public void deleteBookingByIdWithoutToken() throws JsonProcessingException {
        int bookingId = getNewBookingId();
        CommonUtilsAPI.DELETEWithPathParam(requestSpecBuilder(),
                BookingAPI.bookingById, bookingId, HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    public List<Integer> getAllBookingIdList() {
        List<Integer> bookingIds = given().spec(requestSpecBuilder())
                .cookie("token", token)
            .when()
                .get(BookingAPI.booking)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getList("bookingid");
        Collections.sort(bookingIds);
        return bookingIds;

    }

    public int getNewBookingId() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates("2023-06-19", "2023-06-21");
        Booking booking = new Booking("Yaya", "Zaman", 59.00, false,
                bookingDates, "Nothing");

        return given().spec(requestSpecBuilder())
                .body(Utils.convertObj(booking))
            .when()
                .post(BookingAPI.booking)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().get("bookingid");
    }

    public BookingResponse getNewBookingDetails() throws JsonProcessingException {
        BookingDates bookingDates = new BookingDates("2023-06-19", "2023-06-21");
        Booking booking = new Booking("Yaya", "Zaman", 59.00, false,
                bookingDates, "Nothing");

        return given().spec(requestSpecBuilder())
                .body(Utils.convertObj(booking))
            .when()
                .post(BookingAPI.booking)
            .then()
                .log().ifError()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .extract().jsonPath().getObject("", BookingResponse.class);
    }

    public Booking getBookingDetails() {
        List<Integer> bookingIdList = getAllBookingIdList();
        int randomId = bookingIdList.get(random.nextInt(bookingIdList.size()));
        return CommonUtilsAPI.GETBookingDetails(requestSpecBuilder(), BookingAPI.bookingById, randomId,
                HttpStatus.SC_OK);
    }

    @DataProvider(name = "non-existing-value")
    public Object[][] nonExistingValue(){
        return new Object[][] {
            {"firstname", "non-existing"},
            {"lastname", "non-existing"},
            {"checkin", "2099-12-01"},
            {"checkout", "2099-12-30"}
            /*
            more accurate test for checkin + checkout value can be done if there is database
            where the checkin and checkout record's value can be sorted out to find latest and
            first date new checkin + checkout value can use : either latest + 1 or first - 1 date
            */
        };
    }

    @DataProvider(name = "invalid-date")
    public Object[][] invalidDate(){
        return new Object[][] {
            {"checkin", "2019-13-12"},
            {"checkout", "2019-02-31"}
        };
    }

    @DataProvider(name = "incorrect-date")
    public Object[][] incorrectDate(){
        return new Object[][] {
            {"checkin", "2999-11-12"},
            {"checkout", "2999-02-19"}
        };
    }
}
