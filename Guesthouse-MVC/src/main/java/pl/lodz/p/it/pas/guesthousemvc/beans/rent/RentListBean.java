package pl.lodz.p.it.pas.guesthousemvc.beans.rent;

import lombok.Getter;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.RentRESTClient;
import pl.lodz.p.it.pas.model.Rent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Named
public class RentListBean {

    @Getter
    private List<Rent> rentList = new ArrayList<>();

    @Inject
    private RentRESTClient rentRESTClient;

    @PostConstruct
    private void init() {
        try {
            refreshRentList();
        } catch (IOException | InterruptedException ignored) {
        }
    }


    public void refreshRentList() throws IOException, InterruptedException {
        rentList = rentRESTClient.getRentList();
    }


    public void removeRent(int id) throws IOException, InterruptedException {
        int statusCode = rentRESTClient.removeRent(id);
        if (statusCode != 204) {
            FacesContext facesContext = FacesContext.getCurrentInstance();

            String messageBundleName = facesContext.getApplication().getMessageBundle();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

            facesContext.addMessage("table",
                    new FacesMessage(bundle.getString("rent.remove.error")));
        }
        refreshRentList();
    }

    public void addBoard(Long id) throws IOException, InterruptedException {
        UpdateRentBoardDTO dto = new UpdateRentBoardDTO(true);
        rentRESTClient.updateRoom(id, dto);
    }

    public void removeBoard(Long id) throws IOException, InterruptedException {
        UpdateRentBoardDTO dto = new UpdateRentBoardDTO(false);
        rentRESTClient.updateRoom(id, dto);
    }
}
