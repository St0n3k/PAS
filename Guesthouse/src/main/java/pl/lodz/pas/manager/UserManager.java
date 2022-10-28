package pl.lodz.pas.manager;

import java.util.List;
import java.util.Objects;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.dto.RegisterClientDTO;
import pl.lodz.pas.dto.RegisterEmployeeDTO;
import pl.lodz.pas.dto.UpdateUserDTO;
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


@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
@Path("/users")
public class UserManager {

    @Inject
    private UserRepository userRepository;
    @Inject
    private RentRepository rentRepository;
    @Inject
    private ClientTypeRepository clientTypeRepository;


    @POST
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerClient(@Valid RegisterClientDTO rcDTO) {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        ClientType defaultClientType = clientTypeRepository.getByType(Default.class);
        Client client = new Client(rcDTO.getUsername(),
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
    public Response registerEmployee(@Valid RegisterEmployeeDTO reDTO) {
        Employee employee = new Employee(reDTO.getUsername(), reDTO.getFirstName(), reDTO.getLastName());

        Employee addedEmployee = (Employee) userRepository.add(employee);

        if (addedEmployee == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.CREATED).entity(addedEmployee).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        User user = userRepository.getById(id);

        if (user == null) {
            throw new NotFoundException();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@QueryParam("username") String username, @QueryParam("match") boolean match) {
        List<User> users;
        if (username == null) {
            users = userRepository.getAllUsers();
        } else {
            if (!match) {
                User user = userRepository.getUserByUsername(username);

                if (user == null) {
                    throw new NotFoundException();
                }
                return Response.status(Response.Status.OK).entity(user).build();
            }
            users = userRepository.matchUserByUsername(username);
        }
        return Response.status(Response.Status.OK).entity(users).build();

    }

    @GET
    @Path("/{username}/rents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRentsOfClient(@PathParam("username") Long clientId,
                                        @QueryParam("past") Boolean past) {
        try {
            List<Rent> rents;
            if (past != null) { // find past or active rents
                rents = rentRepository.findByRoomAndStatus(clientId, past);
            } else { // find all rents
                rents = rentRepository.getByClientId(clientId);
            }
            return Response.status(Response.Status.OK).entity(rents).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, @Valid UpdateUserDTO dto) {
        User user = userRepository.getById(id);

        if (user == null) {
            throw new NotFoundException();
        }

        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        String personalId = dto.getPersonalId();
        String city = dto.getCity();
        String street = dto.getStreet();
        Integer number = dto.getNumber();

        if (Objects.equals(user.getRole(), "EMPLOYEE")) {
            Employee employee = (Employee) user;
            employee.setFirstName(firstName == null ? employee.getFirstName() : firstName);
            employee.setLastName(lastName == null ? employee.getLastName() : lastName);
            user = userRepository.update(employee);
        }

        if (Objects.equals(user.getRole(), "CLIENT")) {
            Client client = (Client) user;

            client.setFirstName(firstName == null ? client.getFirstName() : firstName);
            client.setLastName(lastName == null ? client.getLastName() : lastName);
            client.setPersonalId(personalId == null ? client.getPersonalId() : personalId);

            Address address = client.getAddress();

            address.setCity(city == null ? address.getCity() : city);
            address.setStreet(street == null ? address.getStreet() : street);
            address.setHouseNumber(number == null ? address.getHouseNumber() : number);
            user = userRepository.update(client);
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @PUT
    @Path("/{id}/activate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("id") Long id) {
        User user = userRepository.getById(id);

        if (user == null) {
            throw new NotFoundException();
        }
        user.setActive(true);
        user = userRepository.update(user);
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @PUT
    @Path("/{id}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("id") Long id) {
        User user = userRepository.getById(id);

        if (user == null) {
            throw new NotFoundException();
        }
        user.setActive(false);
        user = userRepository.update(user);
        return Response.status(Response.Status.OK).entity(user).build();
    }

}
