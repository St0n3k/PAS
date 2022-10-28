package pl.lodz.pas.manager;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.ws.rs.core.Response.Status;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.CreateRentDTO;

class RentManagerTest {

    @Test
    void shouldReturnRentWithStatusCode200() {
        when().get("/api/rents/1")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(1),
                    "board", equalTo(true),
                    "client.id", equalTo(2),
                    "room.id", equalTo(1));
    }

    @Test
    void shouldFailReturningNoExistingRentWithStatusCode404() {
        when().get("/api/rents/12345")
              .then()
              .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldRemoveRentWithStatusCode204() {
        when().delete("/api/rents/2")
              .then()
              .statusCode(Status.NO_CONTENT.getStatusCode());

        when().get("/api/rents/2")
              .then()
              .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldReturnStatusCode204WhenRemovingNonExistingRent() {
        when().delete("/api/rents/12345")
              .then()
              .statusCode(Status.NO_CONTENT.getStatusCode());

        when().get("/api/rents/12345")
              .then()
              .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldCreateRentWithStatusCode201() {
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 22, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 25, 10, 0, 0);

        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, true, 2L, 1L);
        JSONObject body = new JSONObject(dto);

        int id = given().contentType(ContentType.JSON)
                        .body(body.toString())
                        .when()
                        .post("/api/rents")
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .contentType(ContentType.JSON)
                        .body("board", equalTo(true),
                              "client.id", equalTo(2),
                              "room.id", equalTo(1))
                        .extract()
                        .response()
                        .path("id");

        when().get("api/rents/" + id)
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(id),
                    "board", equalTo(true),
                    "client.id", equalTo(2),
                    "room.id", equalTo(1));

    }

    @Test
    void shouldFailCreatingRentForNonExistingClientWithStatusCode400() {
        LocalDateTime beginDate = LocalDateTime.of(2023, 1, 1, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 3, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 1000L, 2L);

        JSONObject body = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(body.toString())
               .when()
               .post("/api/rents")
               .then()
               .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldFailCreatingRentOfNonExistingRoomWithStatusCode400() {
        LocalDateTime beginDate = LocalDateTime.of(2023, 1, 1, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 3, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 20000L);

        JSONObject body = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(body.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldFailCreatingRentForOverlappingDatesWithStatusCode409() {
        LocalDateTime beginDate = LocalDateTime.of(2023, 10, 1, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 3, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 1L);

        JSONObject json = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(Status.CONFLICT.getStatusCode());
    }

    @Test
    void shouldFailCreatingRentForDatesContainedInExistingRentWithStatusCode409() {
        LocalDateTime beginDate = LocalDateTime.of(2023, 10, 13, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 16, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 1L);

        JSONObject json = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(Status.CONFLICT.getStatusCode());
    }

    @Test
    void shouldFailCreatingRentForDatesContainingExistingRentWithStatusCode409() {
        LocalDateTime beginDate = LocalDateTime.of(2023, 10, 1, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 6, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 1L);

        JSONObject json = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(Status.CONFLICT.getStatusCode());
    }


    @Test
    void shouldCreateOnlyOneRentWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
        int threadNumber = 10;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
        List<Thread> threads = new ArrayList<>(threadNumber);
        AtomicInteger numberFinished = new AtomicInteger();
        LocalDateTime begin = LocalDateTime.now().plusDays(40);
        LocalDateTime end = LocalDateTime.now().plusDays(41);
        for (int i = 0; i < threadNumber; i++) {
            threads.add(new Thread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                CreateRentDTO dto = new CreateRentDTO(begin, end, false, 2L, 6L);
                JSONObject json = new JSONObject(dto);
                given().contentType(ContentType.JSON)
                       .body(json.toString())
                       .when()
                       .post("/api/rents")
                       .then()
                       .extract().response();
                numberFinished.getAndIncrement();
            }));
        }

        threads.forEach(Thread::start);
        cyclicBarrier.await();
        while (numberFinished.get() != threadNumber) {
        }

        Response response = when().get("/api/rooms/6/rents")
                                  .then()
                                  .assertThat().statusCode(Status.OK.getStatusCode())
                                  .assertThat().contentType(ContentType.JSON)
                                  .extract().response();
        List<String> jsonResponse = response.jsonPath().getList("$");
        assertEquals(1, jsonResponse.size());
    }


    @Test
    void shouldCreateTwoRentsWithNonOverlappingDates() throws BrokenBarrierException, InterruptedException {
        int threadNumber = 4;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
        List<Thread> threads = new ArrayList<>(threadNumber);
        AtomicInteger numberFinished = new AtomicInteger();

        LocalDateTime localDateTime = LocalDateTime.now();

        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            threads.add(new Thread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                CreateRentDTO dto = new CreateRentDTO(localDateTime.plusDays(10000 + finalI),
                                                      localDateTime.plusDays(10000 + finalI + 2).minusHours(1),
                                                      false, 2L, 5L);
                JSONObject json = new JSONObject(dto);

                given().contentType(ContentType.JSON)
                       .body(json.toString())
                       .when()
                       .post("/api/rents");
                numberFinished.getAndIncrement();
            }));
        }

        threads.forEach(Thread::start);
        cyclicBarrier.await();
        while (numberFinished.get() != threadNumber) {
        }

        when().get("/api/rooms/5/rents")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("size()", equalTo(2));
    }

    @Test
    void shouldFailWithStatusCode4040WhenGettingRentsOfNonExistentRoom() {
        when().get("/api/rooms/12345/rents")
              .then()
              .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldGetEmptyArrayWithStatusCode200() {
        when().get("/api/rooms/8/rents")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .body("$", empty());
    }

    @Test
    void shouldUpdateRentBoardAndRecalculateRentCost() {
        when().get("/api/rents/3")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(3),
                    "board", equalTo(true),
                    "finalCost", equalTo(3000.0F));


        given().body("false")
               .when()
               .patch("/api/rents/3/board")
               .then()
               .statusCode(Status.OK.getStatusCode())
               .body(
                   "board", equalTo(false),
                   "finalCost", equalTo(2500.0F));

        // perform GET request to verify changes
        when().get("/api/rents/3")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(3),
                    "board", equalTo(false),
                    "finalCost", equalTo(2500.0F));

        // update board once again
        given().body("true")
               .when()
               .patch("/api/rents/3/board")
               .then()
               .statusCode(Status.OK.getStatusCode())
               .contentType(ContentType.JSON)
               .body("board", equalTo(true),
                     "finalCost", equalTo(3000.0F));

        // perform GET request to verify changes
        when().get("/api/rents/3")
              .then()
              .statusCode(Status.OK.getStatusCode())
              .contentType(ContentType.JSON)
              .body("id", equalTo(3),
                    "board", equalTo(true),
                    "finalCost", equalTo(3000.0F));
    }

    @Test
    void shouldFailWithStatusCode400WhenBeginDateIsPast() {
        LocalDateTime begin = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        CreateRentDTO dto = new CreateRentDTO(begin, end, false, 2L, 10L);
        JSONObject requestBody = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(requestBody.toString())
               .when().post("api/rents")
               .then()
               .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldFailWithStatusCode400WhenBeginDateIsAfterEndDate() {
        LocalDateTime begin = LocalDateTime.of(2023, 6, 30, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 6, 25, 10, 0);

        CreateRentDTO dto = new CreateRentDTO(begin, end, false, 2L, 10L);
        JSONObject requestBody = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(requestBody.toString())
               .when().post("api/rents")
               .then()
               .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldFailWithStatusCode400WhenBothDatesArePast() {
        LocalDateTime begin = LocalDateTime.of(2020, 6, 30, 10, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 29, 10, 0);

        CreateRentDTO dto = new CreateRentDTO(begin, end, false, 2L, 10L);
        JSONObject requestBody = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(requestBody.toString())
               .when().post("api/rents")
               .then()
               .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void shouldFailWithStatusCode409WhenRemovingAnActiveRent() {
        when().delete("/api/rents/5")
              .then()
              .statusCode(409);
    }

    @Test
    void shouldFailCreatingRentForInactiveUserWithStatusCode401() {
        Long clientId = Integer.toUnsignedLong(when().get("/api/users?username=jakub3")
                                                     .getBody().path("id"));
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 22, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 25, 10, 0, 0);

        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, true, clientId, 8L);
        JSONObject body = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(body.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(Status.UNAUTHORIZED.getStatusCode());
    }
}
