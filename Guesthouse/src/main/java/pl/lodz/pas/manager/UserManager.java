package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.model.Address;
import pl.lodz.pas.model.Client;
import pl.lodz.pas.model.ClientTypes.ClientType;
import pl.lodz.pas.model.ClientTypes.Default;
import pl.lodz.pas.model.User;
import pl.lodz.pas.repository.impl.UserRepository;
import pl.lodz.pas.repository.impl.ClientTypeRepository;

import java.util.List;

//TODO implement methods to serve different user level classes

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/users")
public class UserManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private ClientTypeRepository clientTypeRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User registerUser(User user){
        return userRepository.add(user);
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User getUserByUsername(@PathParam("username") String username){
        return userRepository.getUserByUsername(username);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<User> getUserByUsername(){
        return userRepository.getAllUsers();
    }
//    public Client registerClient(String firstName, String lastName, String personalId, String city, String street, int number) {
//
//        //Values are validated in constructors
//        Address address = new Address(city, street, number);
//        ClientType defaultClientType = clientTypeRepository.getByType(Default.class);
//        Client client = new Client(firstName, lastName, personalId, address, defaultClientType);
//
//        return userRepository.add(client);
//    }

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
