package pl.lodz.p.it.pas.guesthousemvc.beans.room;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.UpdateRoomDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;
import pl.lodz.p.it.pas.model.Room;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@Named
@ViewScoped
public class EditRoomBean implements Serializable {
    @Inject
    private RoomRESTClient roomRESTClient;

    private Long roomId;

    @Getter
    private UpdateRoomDTO updateRoomDTO;
    private UpdateRoomDTO oldRoomDTO;

    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("room_id");
        this.roomId = Long.valueOf(id);
        try {
            Room room = roomRESTClient.getRoomById(this.roomId);
            this.updateRoomDTO = new UpdateRoomDTO(
                    room.getRoomNumber(),
                    room.getSize(),
                    room.getPrice()
            );
            this.oldRoomDTO = new UpdateRoomDTO(
                    room.getRoomNumber(),
                    room.getSize(),
                    room.getPrice()
            );
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public String updateRoom() throws IOException, InterruptedException {
        if (updateRoomDTO.getRoomNumber().equals(oldRoomDTO.getRoomNumber())) {
            updateRoomDTO.setRoomNumber(null);
        }
        if (updateRoomDTO.getSize().equals(oldRoomDTO.getSize())) {
            updateRoomDTO.setSize(null);
        }
        if (updateRoomDTO.getPrice().equals(oldRoomDTO.getPrice())) {
            updateRoomDTO.setPrice(null);
        }

        int statusCode = roomRESTClient.updateRoom(this.roomId, this.updateRoomDTO);
        if (statusCode == 200) {
            return "showRoomList";
        } else {
            //TODO Display error message
        }
        return "";
    }
}
