package pl.lodz.nbd;

import org.junit.jupiter.api.Test;
import pl.lodz.nbd.manager.ClientManager;
import pl.lodz.nbd.manager.RentManager;
import pl.lodz.nbd.manager.RoomManager;
import pl.lodz.nbd.model.Client;
import pl.lodz.nbd.model.ClientTypes.Bronze;
import pl.lodz.nbd.model.ClientTypes.Gold;
import pl.lodz.nbd.model.ClientTypes.Silver;
import pl.lodz.nbd.model.Rent;
import pl.lodz.nbd.model.Room;
import pl.lodz.nbd.repository.impl.ClientRepository;
import pl.lodz.nbd.repository.impl.ClientTypeRepository;
import pl.lodz.nbd.repository.impl.RentRepository;
import pl.lodz.nbd.repository.impl.RoomRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RentTest {

    private static final RoomRepository roomRepository = new RoomRepository();
    private static final RentRepository rentRepository = new RentRepository();
    private static final ClientRepository clientRepository = new ClientRepository();
    private static final ClientTypeRepository clientTypeRepository = new ClientTypeRepository();
    private static final ClientManager clientManager = new ClientManager(clientRepository, clientTypeRepository);
    private static final RoomManager roomManager = new RoomManager(roomRepository);
    private static final RentManager rentManager = new RentManager(clientRepository, roomRepository, rentRepository);

    void initializeData() {
        clientManager.registerClient("Jerzy", "Dudek", "999777", "Wisła", "Karpacka", 22);
        clientManager.registerClient("Kamil", "Stoch", "999888", "Odra", "Wiślana", 32);
        clientManager.registerClient("Remigiusz", "Dudek", "999999", "Wrocław", "Łódzka", 44);

        roomManager.addRoom(120, 2, 2137);
        roomManager.addRoom(210, 3, 999);
        roomManager.addRoom(102, 1, 998);

        rentManager.rentRoom(LocalDateTime.now(), LocalDateTime.now().plusDays(2), true, "999999", 999);
        rentManager.rentRoom(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), true, "999777", 999);
        rentManager.rentRoom(LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), true, "999888", 998);
        rentManager.rentRoom(LocalDateTime.now(), LocalDateTime.now().plusDays(4), true, "999888", 998);
        rentManager.rentRoom(LocalDateTime.now().plusDays(1300), LocalDateTime.now().plusDays(1400), true, "999777", 2137);
    }

    @Test
    void rentRoomTest() {

        Client client = clientManager.registerClient("Marek", "Kowalski", "000566", "Warszawa", "Astronautów", 1);
        Room room = roomManager.addRoom(100.0, 2, 400);

        //Rent create success, check if it is persisted and total cost is calculated properly(add 50 to costPerDay, because of board option)
        Rent rentThreeDays = rentManager.rentRoom(LocalDateTime.now(), LocalDateTime.now().plusDays(3), true, client.getPersonalId(), room.getRoomNumber());
        assertNotNull(rentThreeDays);
        assertEquals(rentThreeDays.getFinalCost(), (100.0 + 50.0) * 3);

        //Rent create success, check if it persisted and days are rounded up(no board option)
        Rent rentThirtyHours = rentManager.rentRoom(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(4).plusHours(30), false, client.getPersonalId(), room.getRoomNumber());
        assertNotNull(rentThirtyHours);
        assertEquals(rentThirtyHours.getFinalCost(), 100.0 * 2);

        //Rent create fail, dates are colliding with other rent of the room
        assertNull(rentManager.rentRoom(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), true, client.getPersonalId(), room.getRoomNumber()));

        //Rent create fail, client doesn't exist
        assertNull(rentManager.rentRoom(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), true, "111111", room.getRoomNumber()));

        //Rent create fail, room doesn't exist
        assertNull(rentManager.rentRoom(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), true, client.getPersonalId(), 999));
    }


    @Test
    void optimisticLockTestSameDay() throws BrokenBarrierException, InterruptedException {

        Client client = clientManager.registerClient("Marek", "Kowalski", "055566", "Warszawa", "Astronautów", 1);
        Room room = roomManager.addRoom(100.0, 2, 405);

        int threadNumber = 10;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
        List<Thread> threads = new ArrayList<>(threadNumber);
        AtomicInteger numberFinished = new AtomicInteger();

        for (int i = 0; i < threadNumber; i++) {
            threads.add(new Thread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                rentManager.rentRoom(LocalDateTime.now().plusDays(40), LocalDateTime.now().plusDays(41), false, client.getPersonalId(), room.getRoomNumber());
                numberFinished.getAndIncrement();
            }));
        }

        threads.forEach(Thread::start);
        cyclicBarrier.await();
        while (numberFinished.get() != threadNumber) {
        }
        assertEquals(rentManager.getAllRentsOfRoom(room.getRoomNumber()).size(), 1);
    }

    @Test
    void optimisticLockTestOverlap() throws BrokenBarrierException, InterruptedException {

        Client client = clientManager.registerClient("Marek", "Kowalski", "050566", "Warszawa", "Astronautów", 1);
        Room room = roomManager.addRoom(100.0, 2, 404);

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
                rentManager.rentRoom(localDateTime.plusDays(100 + finalI), localDateTime.plusDays(100 + finalI + 2).minusHours(1), false, client.getPersonalId(), room.getRoomNumber());
                numberFinished.getAndIncrement();
            }));
        }

        threads.forEach(Thread::start);
        cyclicBarrier.await();
        while (numberFinished.get() != threadNumber) {
        }
        assertEquals(rentManager.getAllRentsOfRoom(room.getRoomNumber()).size(), 2);
    }

    @Test
    void clientTypeDiscountTest() {

        Room room = roomManager.addRoom(100.0, 2, 10);
        Client client = clientManager.registerClient("Jarosław", "Jaki", "604566", "Wadowice", "Przybyszewskiego", 1);

        Rent defaultRent = rentManager.rentRoom(LocalDateTime.now(), LocalDateTime.now().plusDays(1), false, client.getPersonalId(), room.getRoomNumber());

        client = clientManager.changeTypeTo(Bronze.class, client);
        Rent bronzeRent = rentManager.rentRoom(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), false, client.getPersonalId(), room.getRoomNumber());

        client = clientManager.changeTypeTo(Silver.class, client);
        Rent silverRent = rentManager.rentRoom(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), false, client.getPersonalId(), room.getRoomNumber());

        client = clientManager.changeTypeTo(Gold.class, client);
        Rent goldRent = rentManager.rentRoom(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5), false, client.getPersonalId(), room.getRoomNumber());

        assertEquals(defaultRent.getFinalCost(), 100);
        assertEquals(bronzeRent.getFinalCost(), 100 - (0.05 * 100));
        assertEquals(silverRent.getFinalCost(), 100 - (0.10 * 100));
        assertEquals(goldRent.getFinalCost(), 100 - (0.15 * 100));
    }

    @Test
    void cascadeDeleteTest() {
        initializeData();

        List<Rent> rents = rentManager.getAllRentsOfRoom(999);
        assertEquals(2, rents.size());
        assertTrue(roomManager.removeRoom(roomManager.getByRoomNumber(999)));
        rents = rentManager.getAllRentsOfRoom(999);
        assertEquals(0, rents.size());

        rents = rentManager.getAllRentsOfClient("999888");
        assertEquals(2, rents.size());
        assertTrue(clientManager.removeClient(clientManager.getByPersonalId("999888")));
        rents = rentManager.getAllRentsOfClient("999888");
        assertEquals(0, rents.size());

        List<Rent> rentsToBeRemoved = rentManager.getAllRentsOfRoom(2137);
        assertEquals(1, rentsToBeRemoved.size());
        
        rentManager.removeRent(rentsToBeRemoved.get(0));

        rentsToBeRemoved = rentManager.getAllRentsOfRoom(2137);
        assertEquals(0, rentsToBeRemoved.size());
    }

    @Test
    void updateRentBoardTest() {
        Client client = clientManager.registerClient("Marek", "Kowalski", "140566", "Warszawa", "Astronautów", 1);
        Room room = roomManager.addRoom(100.0, 2, 1400);

        Rent rent = rentManager.rentRoom(LocalDateTime.now().plusDays(300), LocalDateTime.now().plusDays(320), false, client.getPersonalId(), room.getRoomNumber());
        assertEquals(20 * 100, rent.getFinalCost());
        rentManager.updateRentBoard(rent.getId(), true);

        rent = rentManager.getRentById(rent.getId());

        //Plus 50 per day, because of board option
        assertEquals(20 * (100 + 50), rent.getFinalCost());
    }
}
