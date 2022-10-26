package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.RegisterClientDTO;
import pl.lodz.pas.dto.RegisterEmployeeDTO;
import pl.lodz.pas.dto.UpdateUserDTO;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

class UserManagerTest {

    @Test
    public void shouldAddEmployeeWithStatusCode200() {
        RegisterEmployeeDTO dto = new RegisterEmployeeDTO(
                "jacek1",
                "Jacek",
                "Murański");

        JSONObject req = new JSONObject(dto);

        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when().post("/api/users/employees")
                .then().statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void shouldAddClientWithStatusCode200() {
        RegisterClientDTO dto = new RegisterClientDTO(
                "marek347",
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
                .when().post("/api/users/clients")
                .then().assertThat().statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void shouldReturnUserListWithStatusCode200() {
        when().get("/api/users")
                .then().assertThat().statusCode(Response.Status.OK.getStatusCode())
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void shouldReturnUserByUsername() {
        when()
                .get("/api/users?username=admin17")
                .then()
                .assertThat().statusCode(Response.Status.OK.getStatusCode())
                .assertThat().body("username", equalTo("admin17"))
                .assertThat().body("role", equalTo("ADMIN"))
                .assertThat().body("active", equalTo(true));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenSearchingNonExistingUsername() {
        when()
                .get("/api/users?username=random_user")
                .then()
                .assertThat().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void shouldUpdateUserWithStatusCode200() {
        UpdateUserDTO dto = new UpdateUserDTO(
                "Franciszek",
                null,
                null,
                null,
                "Wesoła",
                null);

        JSONObject req = new JSONObject(dto);
        when()
                .get("/api/users/4")
                .then()
                .assertThat().statusCode(Response.Status.OK.getStatusCode())
                .assertThat().body("firstName", equalTo("Jakub"))
                .assertThat().body("lastName", equalTo("Bukaj"))
                .assertThat().body("personalId", equalTo("3584873"))
                .assertThat().body("address.city", equalTo("Krakow"))
                .assertThat().body("address.street", equalTo("Smutna"))
                .assertThat().body("address.houseNumber", equalTo(13));


        given()
                .contentType(ContentType.JSON)
                .body(req.toString())
                .when().put("/api/users/4")
                .then().assertThat().statusCode(Response.Status.OK.getStatusCode())
                .assertThat().body("firstName", equalTo("Franciszek"))
                .assertThat().body("lastName", equalTo("Bukaj"))
                .assertThat().body("personalId", equalTo("3584873"))
                .assertThat().body("address.city", equalTo("Krakow"))
                .assertThat().body("address.street", equalTo("Wesoła"))
                .assertThat().body("address.houseNumber", equalTo(13));
    }

    @Test
    public void shouldActivateUserWithStatusCode200() {
        when()
                .put("/api/users/4/activate")
                .then()
                .assertThat().statusCode(Response.Status.OK.getStatusCode())
                .assertThat().body("active", equalTo(true));
    }

    @Test
    public void shouldDeactivateUserWithStatusCode200() {
        when()
                .put("/api/users/4/deactivate")
                .then()
                .assertThat().statusCode(Response.Status.OK.getStatusCode())
                .assertThat().body("active", equalTo(false));
    }

}
