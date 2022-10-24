package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.RegisterClientDTO;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class UserManagerTest {

    //TODO make more test cases

    @Test
    public void shouldAddEmployeeWithStatusCode200() {
        JSONObject req = new JSONObject();
        req.put("username", "jacek1");
        req.put("firstName", "Jacek");
        req.put("lastName", "Murański");
        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when().post("/api/employees")
                .then().statusCode(200);
    }

    @Test
    public void shouldAddClientWithStatusCode200() {
        RegisterClientDTO dto = new RegisterClientDTO(
                "marek3",
                "Mariusz",
                "Pasek",
                "0124738",
                "Łódź",
                "Wesoła",
                7);

        JSONObject req = new JSONObject(dto);

        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when().post("/api/users")
                .then().assertThat().statusCode(200);
    }

    @Test
    public void shouldReturnUserListWithStatusCode200() {
        when().get("/api/users")
                .then().assertThat().statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void shouldReturnUserByUsername() {
        when()
                .get("/api/users/{username}", "admin")
                .then()
                .assertThat().statusCode(200)
                .assertThat().body("username", equalTo("admin"))
                .assertThat().body("role", equalTo("ADMIN"))
                .assertThat().body("active", equalTo(true));
    }
    
}