package pl.lodz.pas.manager;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.RegisterClientDTO;
import pl.lodz.pas.dto.RegisterEmployeeDTO;
import pl.lodz.pas.dto.UpdateUserDTO;

class UserManagerTest {

    @Test
    void shouldAddEmployeeWithStatusCode201() {
        RegisterEmployeeDTO dto = new RegisterEmployeeDTO("jacek1",
                                                          "Jacek",
                                                          "Murański");

        JSONObject req = new JSONObject(dto);

        int id = given().contentType(ContentType.JSON)
                        .body(req.toString())
                        .when()
                        .post("/api/users/employees")
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .extract().jsonPath().getInt("id");

        when().get("/api/users/" + id)
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(id),
                    "username", equalTo("jacek1"),
                    "firstName", equalTo("Jacek"),
                    "lastName", equalTo("Murański"));
    }

    @Test
    void shouldAddClientWithStatusCode201() {
        RegisterClientDTO dto = new RegisterClientDTO("marek347", "Mariusz", "Pasek",
                                                      "0124738", "Łódź", "Wesoła", 7);

        JSONObject req = new JSONObject(dto);

        int id = given().contentType(ContentType.JSON)
                        .body(req.toString())
                        .when().post("/api/users/clients")
                        .then().assertThat().statusCode(Response.Status.CREATED.getStatusCode())
                        .extract().jsonPath().getInt("id");

        when().get("/api/users/" + id)
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(id),
                    "username", equalTo("marek347"),
                    "firstName", equalTo("Mariusz"),
                    "lastName", equalTo("Pasek"),
                    "personalId", equalTo("0124738"),
                    "address.city", equalTo("Łódź"),
                    "address.street", equalTo("Wesoła"),
                    "address.houseNumber", equalTo(7));
    }

    @Test
    void shouldReturnUserListWithStatusCode200() {
        when().get("/api/users")
              .then().assertThat().statusCode(Response.Status.OK.getStatusCode())
              .assertThat().contentType(ContentType.JSON);
    }

    @Test
    void shouldReturnUserByUsername() {
        when().get("/api/users?username=admin17")
              .then()
              .assertThat().statusCode(Response.Status.OK.getStatusCode())
              .assertThat().body("username", equalTo("admin17"))
              .assertThat().body("role", equalTo("ADMIN"))
              .assertThat().body("active", equalTo(true));
    }

    @Test
    void shouldReturnNotFoundStatusWhenSearchingNonExistingUsername() {
        when().get("/api/users?username=random_user")
              .then()
              .assertThat().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void shouldUpdateUserWithStatusCode200() {
        UpdateUserDTO dto = new UpdateUserDTO("Franciszek",
                                              null,
                                              null,
                                              null,
                                              "Wesoła",
                                              null);

        JSONObject req = new JSONObject(dto);
        when().get("/api/users/4")
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .body("firstName", equalTo("Jakub"),
                    "lastName", equalTo("Bukaj"),
                    "personalId", equalTo("3584873"),
                    "address.city", equalTo("Krakow"),
                    "address.street", equalTo("Smutna"),
                    "address.houseNumber", equalTo(13));


        given().contentType(ContentType.JSON)
               .body(req.toString())
               .when().put("/api/users/4")
               .then().assertThat().statusCode(Response.Status.OK.getStatusCode())
               .body("firstName", equalTo("Franciszek"),
                     "lastName", equalTo("Bukaj"),
                     "personalId", equalTo("3584873"),
                     "address.city", equalTo("Krakow"),
                     "address.street", equalTo("Wesoła"),
                     "address.houseNumber", equalTo(13));

        when().get("/api/users/4")
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .body("firstName", equalTo("Franciszek"))
              .body("lastName", equalTo("Bukaj"))
              .body("personalId", equalTo("3584873"))
              .body("address.city", equalTo("Krakow"))
              .body("address.street", equalTo("Wesoła"))
              .body("address.houseNumber", equalTo(13));
    }

    @Test
    void shouldActivateUserWithStatusCode200() {
        when().put("/api/users/4/activate")
              .then()
              .assertThat().statusCode(Response.Status.OK.getStatusCode())
              .assertThat().body("active", equalTo(true));

        when().get("/api/users/4")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("active", equalTo(true));
    }

    @Test
    void shouldDeactivateUserWithStatusCode200() {
        when().put("/api/users/4/deactivate")
              .then()
              .assertThat().statusCode(Response.Status.OK.getStatusCode())
              .assertThat().body("active", equalTo(false));

        when().get("/api/users/4")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("active", equalTo(false));
    }

    @Test
    void shouldFailWhenCreatingUserWithSameUsernameWithStatusCode409() {
        RegisterClientDTO clientDTO = new RegisterClientDTO("test1234", "Kamil", "Graczyk",
                                                            "777999", "Łódź", "Piotrkowska", 20);
        JSONObject json = new JSONObject(clientDTO);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/users/clients")
               .then()
               .statusCode(Response.Status.CREATED.getStatusCode());

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/users/clients")
               .then()
               .statusCode(Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    void shouldFailWhileRegisteringClientWithInvalidAttributes() {
        RegisterClientDTO clientDTO = new RegisterClientDTO("Wicher2022",
                                                            "Mariusz!",
                                                            "Pasek?",
                                                            "0124a738",
                                                            "Łódź!",
                                                            "Wesoła@`'",
                                                            -1);
        JSONObject json = new JSONObject(clientDTO);
        given().body(json.toString())
               .contentType(ContentType.JSON)
               .when()
               .post("/api/users/clients")
               .then()
               .assertThat().statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
