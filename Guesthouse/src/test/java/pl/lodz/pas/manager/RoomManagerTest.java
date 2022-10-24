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
    public void shouldReturnRoomWithStatusCode200() {
        when()
                .get("/api/rooms/{id}", 643)
                .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body("roomNumber", equalTo(643))
                .assertThat().body("price", equalTo(250.0F))
                .assertThat().body("size", equalTo(6));
    }

    @Test
    public void shouldReturnListOfRooms() {
        when()
                .get("/api/rooms")
                .then()
                .assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON);
        //TODO add some assertions
    }

    @Test
    public void shouldCreateRoomWithStatusCode201() {
        //TODO change status code to 201 - created

        Room room = new Room(1, 600.0, 1);

        JSONObject req = new JSONObject(room);
        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when()
                .post("/api/rooms")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldFailCreatingRoomWithExistingNumberWithStatusCode400() {
        Room room = new Room(1, 200.0, 10);
        //TODO change returned status code to 400 - bad request (or 409)?
        JSONObject req = new JSONObject(room);
        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when()
                .post("/api/rooms")
                .then()
                .statusCode(204);
    }
}