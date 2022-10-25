package pl.lodz.pas.manager;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.model.Room;

class RoomManagerTest {


    @Test
    public void shouldReturnRoomWithStatusCode200() {
        when()
            .get("/api/rooms/{id}", 643)
            .then()
            .assertThat().statusCode(Response.Status.OK.getStatusCode())
            .assertThat().contentType(ContentType.JSON)
            .assertThat().body("roomNumber", equalTo(643))
            .assertThat().body("price", equalTo(250.0F))
            .assertThat().body("size", equalTo(6));
    }

    @Test
    public void shouldReturnListOfRoomsWithStatusCode200() {
        when()
            .get("/api/rooms")
            .then()
            .assertThat().statusCode(Response.Status.OK.getStatusCode())
            .assertThat().contentType(ContentType.JSON);
        //TODO add some assertions
    }

    @Test
    public void shouldCreateRoomWithStatusCode201() {

        Room room = new Room(1, 600.0, 1);

        JSONObject req = new JSONObject(room);
        given()
            .contentType(ContentType.JSON)
            .body(req.toString())
            .when()
            .post("/api/rooms")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void shouldFailCreatingRoomWithExistingNumberWithStatusCode409() {
        Room room = new Room(643, 200.0, 10);

        JSONObject req = new JSONObject(room);
        given()
            .contentType(ContentType.JSON)
            .body(req.toString())
            .when()
            .post("/api/rooms")
            .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode());
    }
}
