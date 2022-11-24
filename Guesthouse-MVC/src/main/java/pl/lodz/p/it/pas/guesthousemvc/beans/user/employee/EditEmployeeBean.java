package pl.lodz.p.it.pas.guesthousemvc.beans.user.employee;

import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Employee;

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
public class EditEmployeeBean implements Serializable {

    @Inject
    private UserRESTClient userRESTClient;

    private Long employeeId;

    private UpdateUserDTO updateUserDTO;
    private UpdateUserDTO oldUserDTO;

    @PostConstruct
    private void init() {
        Map<String, String> params =
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("employee_id");
        //String id = "3";
        this.employeeId = Long.valueOf(id);
        try {
            Employee employee = userRESTClient.getEmployeeById(this.employeeId);
            this.updateUserDTO = new UpdateUserDTO(
                    employee.getUsername(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    null,
                    null,
                    null,
                    null
            );
            this.oldUserDTO = new UpdateUserDTO(
                    employee.getUsername(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    null,
                    null,
                    null,
                    null
            );
        } catch (InterruptedException | IOException ignored) {
        }
    }

    public UpdateUserDTO getUpdateUserDTO() {
        return updateUserDTO;
    }

    public String updateEmployee() throws IOException, InterruptedException {
        UpdateUserDTO dto = new UpdateUserDTO(
                updateUserDTO.getUsername(),
                updateUserDTO.getFirstName(),
                updateUserDTO.getLastName(),
                null,
                null,
                null,
                null
        );
        if (updateUserDTO.getUsername().equals(oldUserDTO.getUsername())) {
            dto.setUsername(null);
        }
        if (updateUserDTO.getFirstName().equals(oldUserDTO.getFirstName())) {
            dto.setFirstName(null);
        }
        if (updateUserDTO.getLastName().equals(oldUserDTO.getLastName())) {
            dto.setLastName(null);
        }

        int statusCode = userRESTClient.updateUser(this.employeeId, dto);
        if (statusCode == 200) {
            return "showEmployeeList";
        } else {
            //TODO Display error message
        }
        return "";
    }

}
