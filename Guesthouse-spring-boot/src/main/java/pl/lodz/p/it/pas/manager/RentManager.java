package pl.lodz.p.it.pas.manager;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.p.it.pas.repository.impl.RentRepository;
import pl.lodz.p.it.pas.repository.impl.RoomRepository;
import pl.lodz.p.it.pas.repository.impl.UserRepository;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/rents")
@RestController
public class RentManager {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RentRepository rentRepository;

    @PostMapping
    public ResponseEntity<Rent> rentRoom(@Valid @RequestBody CreateRentDTO createRentDTO) {
        Optional<User> optionalUser = userRepository.getById(createRentDTO.getClientId());
        Optional<Room> optionalRoom = roomRepository.getById(createRentDTO.getRoomId());

        if (optionalUser.isEmpty() || optionalRoom.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Client client = (Client) optionalUser.get();
        Room room = optionalRoom.get();

        if (!client.isActive()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        double finalCost = calculateTotalCost(createRentDTO.getBeginTime(), createRentDTO.getEndTime(),
                                              room.getPrice(), createRentDTO.isBoard(), client.getClientType());
        Rent rent = new Rent(createRentDTO.getBeginTime(), createRentDTO.getEndTime(), createRentDTO.isBoard(),
                             finalCost, client, room);

        Optional<Rent> optionalRent;
        synchronized (rentRepository) {
            optionalRent = Optional.ofNullable(rentRepository.add(rent));
        }
        if (optionalRent.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(optionalRent.get(), HttpStatus.CREATED);
    }
//
//    @GET
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getRentById(@PathParam("id") Long id) {
//        Rent rent = rentRepository.getById(id);
//
//        if (rent == null) {
//            throw new NotFoundException();
//        }
//
//        return Response.status(Response.Status.OK).entity(rent).build();
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAllRents() {
//        List<Rent> list = rentRepository.getAll();
//
//        return Response.status(Response.Status.OK).entity(list).build();
//    }
//
//
//    @PATCH
//    @Path("/{id}/board")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateRentBoard(@PathParam("id") Long id, Boolean board) {
//        if (board == null) {
//            throw new BadRequestException();
//        }
//        Rent rentToModify = rentRepository.getById(id);
//
//        if (rentToModify == null) {
//            throw new NotFoundException();
//        }
//        rentToModify.setBoard(board);
//        double newCost = calculateTotalCost(rentToModify.getBeginTime(),
//                                            rentToModify.getEndTime(),
//                                            rentToModify.getRoom().getPrice(),
//                                            rentToModify.isBoard(),
//                                            rentToModify.getClient().getClientType());
//        rentToModify.setFinalCost(newCost);
//
//        Rent updatedRent = rentRepository.update(rentToModify);
//
//        return Response.status(Response.Status.OK).entity(updatedRent).build();
//    }
//
//    @DELETE
//    @Path("/{id}")
//    public Response removeRent(@PathParam("id") Long rentId) {
//        Rent rent = rentRepository.getById(rentId);
//        if (rent == null) {
//            return Response.status(Response.Status.NO_CONTENT).build();
//        }
//        LocalDateTime now = LocalDateTime.now();
//        if (rent.getBeginTime().isAfter(now)) {
//            rentRepository.removeById(rentId);
//            return Response.status(Response.Status.NO_CONTENT).build();
//        }
//        return Response.status(Response.Status.CONFLICT).build();
//
//    }
//
    private double calculateTotalCost(LocalDateTime beginTime, LocalDateTime endTime, double costPerDay, boolean board,
                                      ClientType clientType) {
        Duration duration = Duration.between(beginTime, endTime);
        if (board) {
            costPerDay += 50; //Daily board is worth 50
        }
        return clientType.applyDiscount(Math.ceil(duration.toHours() / 24.0) * costPerDay);
    }

}
