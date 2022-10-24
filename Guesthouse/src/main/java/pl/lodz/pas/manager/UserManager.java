package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.RegisterClientDTO;
import pl.lodz.pas.dto.RegisterEmployeeDTO;
import pl.lodz.pas.model.Address;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.user.Client;
import pl.lodz.pas.model.user.ClientTypes.ClientType;
import pl.lodz.pas.model.user.ClientTypes.Default;
import pl.lodz.pas.model.user.Employee;
import pl.lodz.pas.model.user.User;
import pl.lodz.pas.repository.impl.ClientTypeRepository;
import pl.lodz.pas.repository.impl.RentRepository;
import pl.lodz.pas.repository.impl.UserRepository;

import java.util.List;

//TODO implement methods to serve different user level classes

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/")
public class UserManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RentRepository rentRepository;
    @Inject
    private ClientTypeRepository clientTypeRepository;

    public List<Rent> getAllRentsOfClient(String personalId) {
        // TODO Move to ClientManager
        return rentRepository.getByClientPersonalId(personalId);
    }

    @POST
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User registerClient(RegisterClientDTO rcDTO) {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        ClientType defaultClientType = clientTypeRepository.getByType(Default.class);
        Client client = new Client(
                rcDTO.getUsername(),
                rcDTO.getFirstName(),
                rcDTO.getLastName(),
                rcDTO.getPersonalID(),
                address,
                defaultClientType);
        return userRepository.add(client);
    }

    @POST
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //This endpoint will be available only for admin
    public User registerEmployee(RegisterEmployeeDTO reDTO) {
        Employee employee = new Employee(reDTO.getUsername(), reDTO.getFirstName(), reDTO.getLastName());
        return userRepository.add(employee);
    }


    @GET
    @Path("/users/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User getUserByUsername(@PathParam("username") String username) {
        return userRepository.getUserByUsername(username);
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }


    public Client getByPersonalId(String personalId) {
        return userRepository.getClientByPersonalId(personalId);
    }

    public boolean removeClient(Client client) {
        return userRepository.remove(client);
    }

//    public Client updateClient(Client client) {
//        return userRepository.update(client);
//    }

//    public Client changeTypeTo(Class type, Client client) {
//        client.setClientType(clientTypeRepository.getByType(type));
//
//        client = updateClient(client);
//        return client;
//    }
}
