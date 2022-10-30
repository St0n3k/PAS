package pl.lodz.pas.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e.getClass() == UserNotFoundException.class
                || e.getClass() == RoomNotFoundException.class
                || e.getClass() == RentNotFoundException.class) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (e.getClass() == ConflictException.class) {
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
