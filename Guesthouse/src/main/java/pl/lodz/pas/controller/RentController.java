package pl.lodz.pas.controller;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.dto.CreateRentDTO;
import pl.lodz.p.it.pas.dto.UpdateRentBoardDTO;
import pl.lodz.pas.exception.InvalidInputException;
import pl.lodz.pas.exception.rent.CreateRentException;
import pl.lodz.pas.exception.rent.RemoveRentException;
import pl.lodz.pas.exception.rent.RentNotFoundException;
import pl.lodz.pas.exception.room.RoomNotFoundException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;
import pl.lodz.pas.manager.RentManager;

@RequestScoped
@Path("/rents")
public class RentController {

    @Inject
    private RentManager rentManager;



    /**
     * Enpoint for creating a new rent. Rent will be created if client and room exists in database, and if rent period
     * is not colliding with existing rents.
     *
     * @param createRentDTO object containing information about rent which creation will be attempted.
     * @return status code 201 (CREATED) if rent was successfully created,
     * 409 (CONFLICT) if rent could not be created due to rent time period,
     * 400 (BAD_REQUEST) if client or room do not exist in database
     */

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rentRoom(@Valid CreateRentDTO createRentDTO)
        throws UserNotFoundException, RoomNotFoundException, InactiveUserException, CreateRentException {
        Rent rent = rentManager.rentRoom(createRentDTO);
        return Response.status(Response.Status.CREATED).entity(rent).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRentById(@PathParam("id") Long id) throws RentNotFoundException {
        Rent rent = rentManager.getRentById(id);
        return Response.status(Response.Status.OK).entity(rent).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRents() {
        List<Rent> rents = rentManager.getAllRents();
        return Response.status(Response.Status.OK).entity(rents).build();
    }


    /**
     * Endpoint used to change board option for given rent, cost is recalculated before saving to database
     *
     * @param id id of the rent to be updated
     * @param dto object containing the choice of board option (true/false)
     * @return status 200 (OK) if rent was updated, 409 (CONFLICT) otherwise
     */
    @PATCH
    @Path("/{id}/board")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRentBoard(@PathParam("id") Long id, @Valid UpdateRentBoardDTO dto)
        throws InvalidInputException, RentNotFoundException {
        Rent rent = rentManager.updateRentBoard(id, dto);
        return Response.status(Response.Status.OK).entity(rent).build();
    }

    /**
     * Endpoint for removing future rents, archived rent will not be removed
     *
     * @param rentId id of the rent to be removed
     * @return status code 204 (NO_CONTENT) if rent was removed, otherwise 409 (CONFLICT)
     */
    @DELETE
    @Path("/{id}")
    public Response removeRent(@PathParam("id") Long rentId) throws RemoveRentException {
        rentManager.removeRent(rentId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
