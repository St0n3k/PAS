package pl.lodz.pas.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.pas.exception.*;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e.getClass() == UserNotFoundException.class
                || e.getClass() == RoomNotFoundException.class
                || e.getClass() == RentNotFoundException.class) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (e.getClass() == CreateRoomException.class
                || e.getClass() == RoomHasActiveReservationsException.class
                || e.getClass() == UpdateRoomException.class
                || e.getClass() == CreateRentException.class
                || e.getClass() == UpdateRentException.class
                || e.getClass() == RemoveRentException.class
                || e.getClass() == CreateUserException.class
                || e.getClass() == UpdateUserException.class) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        if (e.getClass() == InvalidInputException.class) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (e.getClass() == InactiveUserException.class) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return null;
    }
}
