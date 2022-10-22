package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

class UserManagerTest {

    //TODO make more test cases

    @Test
    public void addEmployeeTest() {
        JSONObject req = new JSONObject();
        req.put("username", "jacek1");
        req.put("firstName", "Jacek");
        req.put("lastName", "Murański");
        System.out.println(req);
        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when().post("/api/employees")
                .then().statusCode(200);
    }

    @Test
    public void addClientTest() {
        JSONObject req = new JSONObject();
        req.put("username", "marek3");
        req.put("firstName", "Mariusz");
        req.put("lastName", "Pasek");
        req.put("personalID", "0124738");
        req.put("city", "Łódź");
        req.put("street", "Wesoła");
        req.put("number", 7);
        System.out.println(req);
        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when().post("/api/users")
                .then().statusCode(200);
    }

    @Test
    public void getUsersTest() {
        when().get("/api/users")
                .then().assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

//    @Test
//    public void getUserTest() {
//        User user = new User("test");
//        when().get("/api/users/{username}", "test")
//                .then().assertThat().statusCode(200)
//                .assertThat().contentType(ContentType.JSON)
//                .assertThat().body("username", response -> equalTo("test"))
//                .assertThat().body("active", response -> equalTo(true));
//    }
}