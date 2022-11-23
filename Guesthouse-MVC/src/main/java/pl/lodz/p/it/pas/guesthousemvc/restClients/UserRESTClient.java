package pl.lodz.p.it.pas.guesthousemvc.restClients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.lodz.p.it.pas.dto.RegisterAdminDTO;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.guesthousemvc.utils.Utils;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.user.Admin;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.Employee;

import javax.enterprise.context.RequestScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RequestScoped
public class UserRESTClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public UserRESTClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<Client> getClientList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/clients")).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Client>>() {
        });
    }

    public List<Employee> getEmployeeList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/employees")).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Employee>>() {
        });
    }

    public List<Admin> getAdminList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/admins")).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Admin>>() {
        });
    }

    public Client getClientById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/" + id)).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Client.class);
    }

    public Employee getEmployeeById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/" + id)).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        return mapper.readValue(response.body(), Employee.class);
    }

    public Admin getAdminById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/" + id)).GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        return mapper.readValue(response.body(), Admin.class);
    }

    public int activateClient(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/activate"))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int deactivateUser(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/deactivate"))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int registerClient(RegisterClientDTO registerClientDTO) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/clients"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(registerClientDTO)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int registerEmployee(RegisterEmployeeDTO registerEmployeeDTO) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/employees"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(registerEmployeeDTO)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int registerAdmin(RegisterAdminDTO dto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/admins"))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dto)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public int updateUser(Long id, UpdateUserDTO dto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(Utils.API_URL + "/users/" + id))
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dto)))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public List<Rent> getRentsByClientId(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Utils.API_URL + "/users/" + id + "/rents"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<Rent>>() {
        });
    }
}
