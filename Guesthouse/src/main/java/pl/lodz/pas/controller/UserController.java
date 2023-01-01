package pl.lodz.pas.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.pas.dto.RegisterAdminDTO;
import pl.lodz.p.it.pas.dto.RegisterClientDTO;
import pl.lodz.p.it.pas.dto.RegisterEmployeeDTO;
import pl.lodz.p.it.pas.dto.UpdateUserDTO;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.user.Admin;
import pl.lodz.p.it.pas.model.user.Client;
import pl.lodz.p.it.pas.model.user.Employee;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.pas.exception.user.CreateUserException;
import pl.lodz.pas.exception.user.UpdateUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.manager.UserManager;

import java.util.List;

@RequestScoped
@Path("/users")
public class UserController {
    @Inject
    private UserManager userManager;

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
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    public Response registerClient(@Valid RegisterClientDTO rcDTO) throws CreateUserException {
        Client client = userManager.registerClient(rcDTO);
        return Response.status(Response.Status.CREATED).entity(client).build();
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
    @RolesAllowed({"ADMIN"})
    public Response registerEmployee(@Valid RegisterEmployeeDTO reDTO) throws CreateUserException {
        Employee employee = userManager.registerEmployee(reDTO);
        return Response.status(Response.Status.CREATED).entity(employee).build();
    }

    @POST
    @Path("/admins")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response registerAdmin(@Valid RegisterAdminDTO raDto) throws CreateUserException {
        Admin admin = userManager.registerAdmin(raDto);
        return Response.status(Response.Status.CREATED).entity(admin).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response getUserById(@PathParam("id") Long id) throws UserNotFoundException {
        User user = userManager.getUserById(id);
        return Response.status(Response.Status.OK).entity(user).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response getAllUsers(@QueryParam("username") String username) {
        List<User> users = userManager.getAllUsers(username);
        return Response.status(Response.Status.OK).entity(users).build();
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllClients(@QueryParam("username") String username) {
        List<Client> clients = userManager.getClients(username);
        return Response.status(Response.Status.OK).entity(clients).build();
    }

    @GET
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response getAllEmployees() {
        List<Employee> employee = userManager.getEmployees();
        return Response.status(Response.Status.OK).entity(employee).build();
    }

    @GET
    @Path("/admins")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response getAllAdmins() {
        List<Admin> admins = userManager.getAdmins();
        return Response.status(Response.Status.OK).entity(admins).build();
    }

    @GET
    @Path("/search/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response getUserByUsername(@PathParam("username") String username) throws UserNotFoundException {
        User user = userManager.getUserByUsername(username);
        return Response.status(Response.Status.OK).entity(user).build();
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
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response getAllRentsOfClient(@PathParam("id") Long clientId,
                                        @QueryParam("past") Boolean past) throws UserNotFoundException {
        List<Rent> rents = userManager.getAllRentsOfClient(clientId, past);
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
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response updateUser(@PathParam("id") Long id, @Valid UpdateUserDTO dto)
        throws UserNotFoundException, UpdateUserException {
        User user = userManager.updateUser(id, dto);
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
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response activateUser(@PathParam("id") Long id) throws UserNotFoundException, UpdateUserException {
        User user = userManager.activateUser(id);
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
    @RolesAllowed({"ADMIN", "EMPLOYEE"})
    public Response deactivateUser(@PathParam("id") Long id) throws UserNotFoundException, UpdateUserException {
        User user = userManager.deactivateUser(id);
        return Response.status(Response.Status.OK).entity(user).build();
    }
}
