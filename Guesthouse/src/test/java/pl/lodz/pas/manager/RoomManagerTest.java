package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.model.Room;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class RoomManagerTest {
    @Test
    public void getRoomTest() {
        Room room = new Room(1912, 200.0, 10);
        when().get("/api/rooms/{id}", 1912)
                .then().assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body("roomNumber", response -> equalTo(1912))
                .assertThat().body("price", response -> equalTo(200.0F))
                .assertThat().body("size", response -> equalTo(10));
    }

    @Test
    public void getRoomsTest() {
        when().get("/api/rooms")
                .then().assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void addRoomTest() {
        Room room = new Room(1912, 200.0, 10);
        JSONObject req = new JSONObject(room);
        given().contentType(ContentType.JSON).body(req.toString()).when().post("/api/rooms").then().statusCode(200);
    }
}