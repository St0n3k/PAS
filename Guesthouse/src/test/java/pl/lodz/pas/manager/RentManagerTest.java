package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.CreateRentDTO;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


class RentManagerTest {

    @Test
    @Order(3)
    void getRentById() {
        given().when()
                .get("/api/rents/1")
                .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body(
                        "id", equalTo(1),
                        "board", equalTo(true),
                        "client.id", equalTo(2),
                        "room.id", equalTo(1));

        given().when()
                .get("/api/rents/12345")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    @Order(4)
    void removeRentTest() {
        given().when()
                .delete("/api/rents/1")
                .then()
                .log().all()
                .assertThat().statusCode(204);

        given().when()
                .delete("/api/rents/12345")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    @Order(1)
    void rentRoomPositiveTest() {
        final var beginDate = LocalDateTime.of(2023, 10, 22, 11, 0, 0);
        final var endDate = LocalDateTime.of(2023, 10, 25, 10, 0, 0);

        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, true, 2L, 1L);
        JSONObject body = new JSONObject(dto);

        given()
                .contentType(ContentType.JSON)
                .body(body.toString())
                .when()
                .post("/api/rents")
                .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body(
                        "id", equalTo(1),
                        "board", equalTo(true),
                        "client.id", equalTo(2),
                        "room.id", equalTo(1));
    }

    @Test
    void rentRoomNegativeTest() {
        final var beginDate = LocalDateTime.of(2023, 10, 1, 11, 0, 0);
        final var endDate = LocalDateTime.of(2023, 10, 3, 10, 0, 0);
        CreateRentDTO dto;

        // non-existent client
        dto = new CreateRentDTO(beginDate, endDate, false, 1000L, 2L);
        JSONObject body1 = new JSONObject(dto);

        given().contentType(ContentType.JSON)
                .body(body1.toString())
                .when()
                .post("/api/rents")
                .then()
                .assertThat().statusCode(404);

        // non-existent room
        dto = new CreateRentDTO(beginDate, endDate, false, 2L, 20000L);
        JSONObject body2 = new JSONObject(dto);

        given().contentType(ContentType.JSON)
                .body(body2.toString())
                .when()
                .post("/api/rents")
                .then()
                .assertThat().statusCode(404);
    }

//    @Test
//    @Order(2)
//    void updateRentBoardTest() {
//        given()
//                .body("true")
//                .log().body()
//        .when()
//                .put("/api/rents/1/board")
//        .then()
//            .assertThat().statusCode(200)
//            .assertThat().body("board", equalTo(true));
//
//        given()
//                .body("false")
//        .when()
//                .put("/api/rents/1/board")
//        .then()
//            .assertThat().statusCode(200)
//            .assertThat().contentType(ContentType.JSON)
//            .assertThat().body("board", equalTo(false));
//    }
}