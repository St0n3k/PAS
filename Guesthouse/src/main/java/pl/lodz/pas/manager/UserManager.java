package pl.lodz.pas.manager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
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
import pl.lodz.pas.exception.CreateUserException;
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


    /**
     * Endpoint which is used to register new client,
     * username of client has to be unique, otherwise exception will be thrown
     *
     * @param rcDTO object containing information of client
     * @return status code
     * 201(CREATED) + saved client if registration was successful
     * 409(CONFLICT) if registration attempt was unsuccessful (could be due to not unique username)
     */
    @POST
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Client registerClient(@Valid RegisterClientDTO rcDTO) throws CreateUserException {
        Address address = new Address(rcDTO.getCity(), rcDTO.getStreet(), rcDTO.getNumber());
        Optional<ClientType> defaultClientTypeOptional = clientTypeRepository.getByType(Default.class);

        if (defaultClientTypeOptional.isEmpty()) {
            throw new BadRequestException();
        }

        Client client = new Client(rcDTO.getUsername(),
                                   rcDTO.getFirstName(),
                                   rcDTO.getLastName(),
                                   rcDTO.getPersonalID(),
                                   address,
                                   defaultClientTypeOptional.get());

        try {
            client = (Client) userRepository.add(client);
        } catch (Exception e) {
            throw new CreateUserException();
        }
        return client;
    }


    /**
     * Endpoint which is used to register new employee,
     * username of employee has to be unique, otherwise exception will be thrown
     *
     * @param reDTO object containing information of client
     * @return status code
     * 201(CREATED) + saved employee if registration was successful
     * 409(CONFLICT) if registration attempt was unsuccessful (could be due to not unique username)
     */
    @POST
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerEmployee(@Valid RegisterEmployeeDTO reDTO) {
        Employee employee = new Employee(reDTO.getUsername(), reDTO.getFirstName(), reDTO.getLastName());
        Employee addedEmployee = (Employee) userRepository.add(employee);

        if (addedEmployee == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.CREATED).entity(employee).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(optionalUser.get()).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@QueryParam("username") String username) {
        List<User> users;
        if (username == null) {
            users = userRepository.getAllUsers();
        } else {
            users = userRepository.matchUserByUsername(username);
        }
        return Response.status(Response.Status.OK).entity(users).build();
    }

    @GET
    @Path("/search/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByUsername(@PathParam("username") String username) {
        Optional<User> optionalUser = userRepository.getUserByUsername(username);

        if (optionalUser.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(optionalUser.get()).build();
    }


    /**
     * Endpoint used for finding all rents of client
     *
     * @param clientId id of the client
     * @param past flag indicating if the result will be list of past rents or list of future rents
     * @return
     */
    @GET
    @Path("/{id}/rents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRentsOfClient(@PathParam("id") Long clientId,
                                        @QueryParam("past") Boolean past) {
        if (userRepository.getById(clientId).isEmpty()) {
            throw new NotFoundException();
        }
        List<Rent> rents;
        if (past != null) { // find past or active rents
            rents = rentRepository.findByClientAndStatus(clientId, past);
        } else { // find all rents
            rents = rentRepository.getByClientId(clientId);
        }
        return Response.status(Response.Status.OK).entity(rents).build();
    }


    /**
     * Endpoint used for updating given user
     *
     * @param id id of the user
     * @param dto object containing new properties of user
     * @return status code
     * 200(OK) if update was successful
     * 409(CONFLICT) if update was unsuccessful (could be due to new username not being unique)
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, @Valid UpdateUserDTO dto) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException();
        }
        User user = optionalUser.get();

        String username = dto.getUsername();
        String firstName = dto.getFirstName();
        String lastName = dto.getLastName();
        String personalId = dto.getPersonalId();
        String city = dto.getCity();
        String street = dto.getStreet();
        Integer number = dto.getNumber();

        if (username != null && userRepository.getUserByUsername(username).isPresent()) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        if (Objects.equals(user.getRole(), "EMPLOYEE")) {
            Employee employee = (Employee) user;

            employee.setUsername(username == null ? employee.getUsername() : username);
            employee.setFirstName(firstName == null ? employee.getFirstName() : firstName);
            employee.setLastName(lastName == null ? employee.getLastName() : lastName);


            optionalUser = userRepository.update(employee);

            if (optionalUser.isEmpty()) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            user = optionalUser.get();
        }

        if (Objects.equals(user.getRole(), "CLIENT")) {
            Client client = (Client) user;

            client.setUsername(username == null ? client.getUsername() : username);
            client.setFirstName(firstName == null ? client.getFirstName() : firstName);
            client.setLastName(lastName == null ? client.getLastName() : lastName);
            client.setPersonalId(personalId == null ? client.getPersonalId() : personalId);

            Address address = client.getAddress();

            address.setCity(city == null ? address.getCity() : city);
            address.setStreet(street == null ? address.getStreet() : street);
            address.setHouseNumber(number == null ? address.getHouseNumber() : number);

            optionalUser = userRepository.update(client);
            if (optionalUser.isEmpty()) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            user = optionalUser.get();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }


    /**
     * Endpoint used for activating given user
     *
     * @param id id of the user
     * @return status code
     * 200(OK) if activation was successful
     * 409(CONFLICT) if activation was unsuccessful
     */
    @PUT
    @Path("/{id}/activate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("id") Long id) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException();
        }
        User user = optionalUser.get();
        user.setActive(true);

        optionalUser = userRepository.update(user);
        if (optionalUser.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        user = optionalUser.get();
        return Response.status(Response.Status.OK).entity(user).build();
    }


    /**
     * Endpoint used for deactivating given user
     *
     * @param id id of the user
     * @return status code
     * 200(OK) if deactivation was successful
     * 409(CONFLICT) if deactivation was unsuccessful
     */
    @PUT
    @Path("/{id}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("id") Long id) {
        Optional<User> optionalUser = userRepository.getById(id);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException();
        }
        User user = optionalUser.get();
        user.setActive(false);

        optionalUser = userRepository.update(user);
        if (optionalUser.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        user = optionalUser.get();
        return Response.status(Response.Status.OK).entity(user).build();
    }

}
