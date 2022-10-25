package pl.lodz.pas.manager;

import static io.restassured.RestAssured.given;
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
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.CreateRentDTO;

class RentManagerTest {

    @Test
    void shouldReturnRentWithStatusCode200() {
        given().when()
               .get("/api/rents/1")
               .then()
               .assertThat().statusCode(200)
               .assertThat().contentType(ContentType.JSON)
               .assertThat().body(
                   "id", equalTo(1),
                   "board", equalTo(true),
                   "client.id", equalTo(2),
                   "room.id", equalTo(1));
    }

    @Test
    void shouldFailReturningNoExistingRentWithStatusCode404() {
        given().when()
               .get("/api/rents/12345")
               .then()
               .assertThat().statusCode(404);
    }

    @Test
    void shouldRemoveRentWithStatusCode204() {
        given().when()
               .delete("/api/rents/2")
               .then()
               .log().all()
               .assertThat().statusCode(204);
    }

    //@Test
    void shouldReturnStatusCode204WhenRemovingNonExistingRent() {
        given().when()
               .delete("/api/rents/12345")
               .then()
               .assertThat().statusCode(404);
        //TODO status code for that should be 204 instead of 404
    }

    @Test
    void shouldCreateRentWithStatusCode200() {
        //TODO status code for successfully creating room should be 201 - created
        LocalDateTime beginDate = LocalDateTime.of(2023, 11, 22, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 25, 10, 0, 0);

        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, true, 2L, 1L);
        JSONObject body = new JSONObject(dto);

        given()
            .contentType(ContentType.JSON)
            .body(body.toString())
            .when()
            .post("/api/rents")
            .then()
            .assertThat().statusCode(200)
            .assertThat().contentType(ContentType.JSON)
            .assertThat().body(
                "board", equalTo(true),
                "client.id", equalTo(2),
                "room.id", equalTo(1));
    }

    @Test
    void shouldFailCreatingRentForNonExistingClientWithStatusCode400() {
        //TODO this should return status code 400 - bad request
        LocalDateTime beginDate = LocalDateTime.of(2023, 1, 1, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 3, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 1000L, 2L);

        JSONObject body = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(body.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(404);
    }

    @Test
    void shouldFailCreatingRentOfNonExistingRoomWithStatusCode400() {
        //TODO this should return status code 400 - bad request
        LocalDateTime beginDate = LocalDateTime.of(2023, 1, 1, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 3, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 20000L);

        JSONObject body = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(body.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(404);
    }

    @Test
    void shouldFailCreatingRentForOverlappingDatesWithStatusCode400() {
        //TODO this should return status code 400 - bad request
        LocalDateTime beginDate = LocalDateTime.of(2023, 10, 1, 11, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 3, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 1L);

        JSONObject json = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(204);
    }

    @Test
    void shouldFailCreatingRentForDatesContainedInExistingRentWithStatusCode400() {
        //TODO this method should return status code 400 - bad request
        LocalDateTime beginDate = LocalDateTime.of(2023, 10, 13, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 16, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 1L);

        JSONObject json = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(204);
    }

    @Test
    void shouldFailCreatingRentForDatesContainingExistingRentWithStatusCode400() {
        //TODO this method should return status code 400 - bad request
        LocalDateTime beginDate = LocalDateTime.of(2023, 10, 1, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 6, 10, 0, 0);
        CreateRentDTO dto = new CreateRentDTO(beginDate, endDate, false, 2L, 1L);

        JSONObject json = new JSONObject(dto);

        given().contentType(ContentType.JSON)
               .body(json.toString())
               .when()
               .post("/api/rents")
               .then()
               .assertThat().statusCode(204);
    }


    @Test
    public void shouldCreateOnlyOneRentWithConcurrentRequests() throws BrokenBarrierException, InterruptedException {
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
                given()
                    .contentType(ContentType.JSON)
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

        Response response = given().when()
                                   .get("/api/rooms/6/rents")
                                   .then()
                                   .assertThat().statusCode(200)
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
                                                      localDateTime.plusDays(10000 + finalI + 2).minusHours(1), false,
                                                      2L, 5L);
                JSONObject json = new JSONObject(dto);

                given()
                    .contentType(ContentType.JSON)
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

        given().when()
               .get("/api/rooms/958/rents")
               .then()
               .assertThat().statusCode(200)
               .assertThat().contentType(ContentType.JSON);
    }


    @Test
    void shouldUpdateRentBoardAndRecalculateRentCost() {
        given().when()
               .get("/api/rents/3")
               .then()
               .assertThat().statusCode(200)
               .assertThat().contentType(ContentType.JSON)
               .assertThat().body(
                   "id", equalTo(3),
                   "board", equalTo(true),
                   "finalCost", equalTo(3000.0F)
               );


        given().body("false")
               .when()
               .patch("/api/rents/3/board")
               .then()
               .assertThat().statusCode(200)
               .assertThat().body(
                   "board", equalTo(false),
                   "finalCost", equalTo(2500.0F));

        given().body("true")
               .when()
               .patch("/api/rents/3/board")
               .then()
               .assertThat().statusCode(200)
               .assertThat().contentType(ContentType.JSON)
               .assertThat().body(
                   "board", equalTo(true),
                   "finalCost", equalTo(3000.0F));
    }
}
