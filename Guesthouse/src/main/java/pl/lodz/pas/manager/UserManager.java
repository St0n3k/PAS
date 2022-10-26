package pl.lodz.pas.manager;

import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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

//TODO implement getById endpoint
//TODO activate/deactive user endpoint
//TODO implement endpoint for archived/active rents for a user

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

    @GET
    @Path("/users/{username}/rents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRentsOfClient(@PathParam("username") String username) {
        List<Rent> rents = rentRepository.getByClientUsername(username);
        return Response.status(Response.Status.OK).entity(rents).build();
    }

    @POST
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerClient(RegisterClientDTO rcDTO) {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        ClientType defaultClientType = clientTypeRepository.getByType(Default.class);
        Client client = new Client(
            rcDTO.getUsername(),
            rcDTO.getFirstName(),
            rcDTO.getLastName(),
            rcDTO.getPersonalID(),
            address,
            defaultClientType);

        Client addedClient = (Client) userRepository.add(client);

        if (addedClient == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.CREATED).entity(addedClient).build();
    }

    @POST
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //This endpoint will be available only for admin
    public Response registerEmployee(RegisterEmployeeDTO reDTO) {
        Employee employee = new Employee(reDTO.getUsername(), reDTO.getFirstName(), reDTO.getLastName());

        Employee addedEmployee = (Employee) userRepository.add(employee);

        if (addedEmployee == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.CREATED).entity(addedEmployee).build();
    }


    @GET
    @Path("/users/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByUsername(@PathParam("username") String username, @QueryParam("match") boolean match) {
        if(!match){
            User user = userRepository.getUserByUsername(username);
            if (user == null) {
                throw new NotFoundException();
            }
            return Response.status(Response.Status.OK).entity(user).build();
        }
        List<User> users = userRepository.matchUserByUsername(username);
        return Response.status(Response.Status.OK).entity(users).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return Response.status(Response.Status.OK).entity(users).build();
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
