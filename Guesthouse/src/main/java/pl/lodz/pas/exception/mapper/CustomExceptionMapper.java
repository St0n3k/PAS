package pl.lodz.pas.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.pas.exception.BaseApplicationException;
import pl.lodz.pas.exception.InvalidInputException;
import pl.lodz.pas.exception.rent.CreateRentException;
import pl.lodz.pas.exception.rent.RemoveRentException;
import pl.lodz.pas.exception.rent.RentNotFoundException;
import pl.lodz.pas.exception.rent.UpdateRentException;
import pl.lodz.pas.exception.room.CreateRoomException;
import pl.lodz.pas.exception.room.RoomHasActiveReservationsException;
import pl.lodz.pas.exception.room.RoomNotFoundException;
import pl.lodz.pas.exception.room.UpdateRoomException;
import pl.lodz.pas.exception.user.CreateUserException;
import pl.lodz.pas.exception.user.InactiveUserException;
import pl.lodz.pas.exception.user.UpdateUserException;
import pl.lodz.pas.exception.user.UserNotFoundException;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<BaseApplicationException> {
    @Override
    public Response toResponse(BaseApplicationException e) {
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

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
