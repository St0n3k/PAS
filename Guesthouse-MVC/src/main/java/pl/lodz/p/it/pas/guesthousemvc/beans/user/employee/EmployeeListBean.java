package pl.lodz.p.it.pas.guesthousemvc.beans.user.employee;

import pl.lodz.p.it.pas.guesthousemvc.restClients.UserRESTClient;
import pl.lodz.p.it.pas.model.user.Employee;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class EmployeeListBean implements Serializable {

    private List<Employee> employeeList = new ArrayList<>();

    @Inject
    private UserRESTClient userRESTClient;

    @PostConstruct
    private void init() {
        try {
            refreshEmployeeList();
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void refreshEmployeeList() throws IOException, InterruptedException {
        this.employeeList = userRESTClient.getEmployeeList();
    }

    public void deactivateEmployee(Long id) throws IOException, InterruptedException {
        userRESTClient.deactivateUser(id);
        refreshEmployeeList();
    }

    public void activateEmployee(Long id) throws IOException, InterruptedException {
        userRESTClient.activateClient(id);
        refreshEmployeeList();
    }
}
