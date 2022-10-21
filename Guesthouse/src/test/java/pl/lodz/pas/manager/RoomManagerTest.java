package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.model.Room;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class RoomManagerTest {
    Room room = new Room(1, 200.0, 10);
    @Test
    public void getRoomTest() {
        when()
                .get("/api/rooms/{id}", room.getRoomNumber())
        .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body("roomNumber", response -> equalTo(room.getRoomNumber()))
                .assertThat().body("price", response -> equalTo((float)room.getPrice()))
                .assertThat().body("size", response -> equalTo(room.getSize()));
    }

    @Test
    public void getRoomsTest() {
        when()
                .get("/api/rooms")
        .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    @Order(1)
    public void addRoomTest() {
        JSONObject req = new JSONObject(room);
        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
        .when()
                .post("/api/rooms")
        .then()
                .statusCode(200);
    }
}