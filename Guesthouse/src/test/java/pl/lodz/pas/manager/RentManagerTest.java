package pl.lodz.pas.manager;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pl.lodz.pas.dto.CreateRentDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


class RentManagerTest {

    @Test
    @Order(3)
    void getRentById() {
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

        given().when()
                .get("/api/rents/12345")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    @Order(4)
    void removeRentTest() {
        given().when()
                .delete("/api/rents/1")
                .then()
                .log().all()
                .assertThat().statusCode(204);

        given().when()
                .delete("/api/rents/12345")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    @Order(1)
    void rentRoomPositiveTest() {
        final var beginDate = LocalDateTime.of(2023, 10, 22, 11, 0, 0);
        final var endDate = LocalDateTime.of(2023, 10, 25, 10, 0, 0);

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
                        "id", equalTo(1),
                        "board", equalTo(true),
                        "client.id", equalTo(2),
                        "room.id", equalTo(1));
    }

    @Test
    void rentRoomNegativeTest() {
        final var beginDate = LocalDateTime.of(2023, 10, 1, 11, 0, 0);
        final var endDate = LocalDateTime.of(2023, 10, 3, 10, 0, 0);
        CreateRentDTO dto;

        // non-existent client
        dto = new CreateRentDTO(beginDate, endDate, false, 1000L, 2L);
        JSONObject body1 = new JSONObject(dto);

        given().contentType(ContentType.JSON)
                .body(body1.toString())
                .when()
                .post("/api/rents")
                .then()
                .assertThat().statusCode(404);

        // non-existent room
        dto = new CreateRentDTO(beginDate, endDate, false, 2L, 20000L);
        JSONObject body2 = new JSONObject(dto);

        given().contentType(ContentType.JSON)
                .body(body2.toString())
                .when()
                .post("/api/rents")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    public void optimisticLockTest() throws BrokenBarrierException, InterruptedException {

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

//    @Test
//    void optimisticLockTestOverlap() throws BrokenBarrierException, InterruptedException {
//
//        int threadNumber = 4;
//        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
//        List<Thread> threads = new ArrayList<>(threadNumber);
//        AtomicInteger numberFinished = new AtomicInteger();
//
//        JSONObject req = new JSONObject();
//        req.put("username", "wicher");
//        req.put("firstName", "Aleksander");
//        req.put("lastName", "Wichrzyński");
//        req.put("personalID", "0124738");
//        req.put("city", "Łódź");
//        req.put("street", "Wesoła");
//        req.put("number", 7);
//        given()
//                .contentType(ContentType.JSON)
//                .body(req.toString())
//                .when().post("/api/users");
//
//        LocalDateTime localDateTime = LocalDateTime.now();
//        for (int i = 0; i < threadNumber; i++) {
//            int finalI = i;
//            threads.add(new Thread(() -> {
//                try {
//                    cyclicBarrier.await();
//                } catch (InterruptedException | BrokenBarrierException e) {
//                    throw new RuntimeException(e);
//                }
//                CreateRentDTO dto = new CreateRentDTO(localDateTime.plusDays(100 + finalI), localDateTime.plusDays(100 + finalI + 2).minusHours(1), false, 4L, 5L);
//                JSONObject json = new JSONObject(dto);
//                System.out.println(json.toString());
//                given()
//                        .contentType(ContentType.JSON)
//                        .body(json.toString())
//                        .when()
//                        .post("/api/rents");
//                numberFinished.getAndIncrement();
//            }));
//        }
//
//        threads.forEach(Thread::start);
//        cyclicBarrier.await();
//        while (numberFinished.get() != threadNumber) {
//        }
//        given().when()
//                .get("/api/rents/byRoom/958")
//                .then()
//                .assertThat().statusCode(200)
//                .assertThat().contentType(ContentType.JSON);
//    }

//    @Test
//    @Order(2)
//    void updateRentBoardTest() {
//        given()
//                .body("true")
//                .log().body()
//        .when()
//                .put("/api/rents/1/board")
//        .then()
//            .assertThat().statusCode(200)
//            .assertThat().body("board", equalTo(true));
//
//        given()
//                .body("false")
//        .when()
//                .put("/api/rents/1/board")
//        .then()
//            .assertThat().statusCode(200)
//            .assertThat().contentType(ContentType.JSON)
//            .assertThat().body("board", equalTo(false));
//    }
}