package pl.lodz.p.it.pas.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.lodz.p.it.pas.exception.*;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({
        CreateRoomException.class,
        RoomHasActiveReservationsException.class,
        UpdateRoomException.class,
        CreateRentException.class,
        UpdateRentException.class,
        RemoveRentException.class,
        CreateUserException.class,
        UpdateUserException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleConflictExceptions(BaseApplicationException ex) { }

    @ExceptionHandler({
        RoomNotFoundException.class,
        UserNotFoundException.class,
        RentNotFoundException.class,
        ClientTypeNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundExceptions(BaseApplicationException ex) { }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        InvalidInputException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNotValidException(BaseApplicationException ex) { }

    @ExceptionHandler(InactiveUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleAuthorizationException(BaseApplicationException ex) { }
}
