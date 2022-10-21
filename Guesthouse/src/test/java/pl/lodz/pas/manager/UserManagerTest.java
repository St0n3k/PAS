package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.model.User;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {

    //TODO make more test cases
    @Test
    public void addUserTest() {
        User user = new User("test");
        JSONObject req = new JSONObject(user);
        System.out.println(req.toString());
        given().contentType(ContentType.JSON).body(req.toString()).when().post("/api/users").then().statusCode(200);
    }

    @Test
    public void getUsersTest() {
        when().get("/api/users")
                .then().assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void getUserTest() {
        User user = new User("test");
        when().get("/api/users/{username}", "test")
                .then().assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON)
                .assertThat().body("username", response -> equalTo("test"))
                .assertThat().body("active", response -> equalTo(true));
    }
}