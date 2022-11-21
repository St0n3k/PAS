package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class AddRentBean {

    @Inject
    private RentRESTClient rentRESTClient;

    @Inject
    private UserRESTClient userRESTClient;

    @Inject
    private RoomRESTClient roomRESTClient;

    @Getter
    private final CreateRentDTO rent = new CreateRentDTO();

    @Getter
    @Setter
    private String chosenClient;

    @Getter
    @Setter
    private String chosenRoom;

    @Getter
    @Setter
    private String beginTime;
    @Getter
    @Setter
    private String endTime;

    @Getter
    @Setter
    private List<ClientAdapter> clientList;

    @Getter
    @Setter
    private List<RoomAdapter> roomList;

    private class RoomAdapter {
        @Getter
        @Setter
        private Long id;
        @Getter
        @Setter
        private Integer roomNumber;

        public RoomAdapter(Long id, Integer roomNumber) {
            this.id = id;
            this.roomNumber = roomNumber;
        }

        @Override
        public String toString() {
            return String.valueOf(roomNumber);
        }
    }

    private class ClientAdapter {
        @Getter
        @Setter
        private Long id;
        @Getter
        @Setter
        private String username;

        public ClientAdapter(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        @Override
        public String toString() {
            return id + " - " + username;
        }
    }

    @PostConstruct
    private void init() {
        try {
            List<Client> clients = userRESTClient.getClientList();
            this.clientList = clients
                    .stream()
                    .filter(User::isActive)
                    .map(client -> new ClientAdapter(client.getId(), client.getUsername()))
                    .collect(Collectors.toList());
            List<Room> rooms = roomRESTClient.getRoomList();
            this.roomList = rooms
                    .stream()
                    .map(room -> new RoomAdapter(room.getId(), room.getRoomNumber()))
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public String addRent() throws IOException, InterruptedException {
        Optional<RoomAdapter> room = roomList.stream().filter(r -> Objects.equals(r.getRoomNumber(), Integer.valueOf(chosenRoom))).findFirst();
        this.rent.setRoomId(room.get().getId());

        String clientId = this.chosenClient.substring(0, chosenClient.indexOf('-')).trim();
        this.rent.setClientId(Long.valueOf(clientId));

        this.rent.setBeginTime(LocalDateTime.parse(beginTime));
        this.rent.setEndTime(LocalDateTime.parse(endTime));

        int statusCode = rentRESTClient.addRent(rent);
        if (statusCode == 201) {
            return "showRentList";
        } else {
            //TODO Display error message
        }
        return "";
    }
}

