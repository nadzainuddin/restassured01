# REST ASSURED - JAVA
Mini automation project for [restful-booker api](https://restful-booker.herokuapp.com/apidoc/index.html#) using  **REST ASSURED Java**, **TestNG** and **Allure** for reporting purpose.

- [x] GetBookingIds : Success retrieving all booking ids
- [x] GetBookingIds : Success filter by firstname
- [x] GetBookingIds : Success filter by lastname
- [x] GetBookingIds : Success filter by firstname lastname
- [x] GetBookingIds : Success filter by checkIn
- [x] GetBookingIds : Success filter by checkOut
- [x] GetBookingIds : Success filter by checkIn checkOut
- [x] GetBookingIds : Failed filter by firstname partially
- [x] GetBookingIds : Failed filter by lastname partially
- [x] GetBookingIds : Failed to filter by correct firstname incorrect lastname partially
- [x] GetBookingIds : Failed to filter by firstname and lastname with spaces
- [x] GetBookingIds : Failed to filter by incorrect firstname correct lastname partially
- [x] GetBookingIds : Failed to filter by non-existing param
- [x] GetBookingIds : Failed to filter by totalprice
- [x] GetBookingIds : Failed to filter by depositpaid
- [x] GetBookingIds : Failed to filter by additionalneeds
- [x] GetBookingIds : Failed to filter by misspelled param
- [x] GetBookingIds : Failed to filter by non-existing value
- [x] GetBookingIds : Failed to filter by invalid bookingDates value
- [x] GetBookingIds : Failed to filter by correct firstname lastname incorrect bookingDates
- [x] GetBookingIds : Failed to filter by incorrect checkin format
- [x] GetBookingIds : Failed to filter by incorrect checkout format
- [x] GetBooking : Success to retrieve booking by Id
- [x] GetBooking : Failed to retrieve booking by non-existing Id
- [ ] CreateBooking : valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
- [ ] CreateBooking : records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
- [x] CreateBooking : Success create booking with valid data
- [x] CreateBooking : Failed create booking with empty data
- [x] CreateBooking : Failed create booking with invalid bookingDates format
- [x] CreateBooking : Failed create booking with invalid bookingDates format [2]
- [x] CreateBooking : Failed create booking with invalid deposit format
- [x] CreateBooking : Failed create booking with invalid totalprice format
- [ ] UpdateBooking : Failed update non-existing id => max id + 1
- [ ] UpdateBooking : Success update valid records
- [ ] UpdateBooking : Failed to update empty records
- [ ] UpdateBooking : Failed to update valid records with invalid totalprice format
- [ ] UpdateBooking : Failed to update valid records with null values => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
- [ ] UpdateBooking : Failed to update records with missing field => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds
- [ ] UpdateBooking : Failed to update partially
- [ ] UpdateBooking : Failed to update records without auth (since stated auth is opt)
- [x] PartialUpdateBooking : Success update firstname
- [ ] PartialUpdateBooking : Success update partial valid records  => firstname | lastname | totalprice | depositpaid | checkin |checkout | additionalneeds with auth
- [ ] PartialUpdateBooking : Success update partial valid records with no auth (since stated auth is opt)
- [x] DeleteBooking : Success delete existing id with token
- [x] DeleteBooking : Success delete existing id with basic auth
- [x] DeleteBooking : Success delete existing id without token or basic auth (since stated auth is opt)
- [x] DeleteBooking : Success delete non-existing id with token
- [x] DeleteBooking : Success delete non-existing id with basic auth
- [x] DeleteBooking : Success delete booking by Id without basic token (since stated auth is opt)
