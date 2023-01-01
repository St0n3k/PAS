package pl.lodz.p.it.pas.guesthousemvc.beans.room;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.CreateRoomDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RoomRESTClient;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

@Named
@ViewScoped
public class AddRoomBean implements Serializable {

    @Getter
    private final CreateRoomDTO room = new CreateRoomDTO();

    @Inject
    private RoomRESTClient roomRESTClient;

    @Getter
    private int statusCode;


    public String addRoom() throws IOException, InterruptedException {
        statusCode = roomRESTClient.addRoom(room);
        if (statusCode == 201) {
            return "showRoomList"; // redirects to room list
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("addRoomForm:submit",
                    new FacesMessage(bundle.getString("room.add.error")));
        }
        return ""; // stays at current page
    }
}
