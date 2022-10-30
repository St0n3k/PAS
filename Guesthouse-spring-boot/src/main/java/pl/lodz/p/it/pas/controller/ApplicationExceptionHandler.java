package pl.lodz.p.it.pas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.lodz.p.it.pas.exception.BaseApplicationException;
import pl.lodz.p.it.pas.exception.CreateRoomException;
import pl.lodz.p.it.pas.exception.RoomHasActiveReservationsException;
import pl.lodz.p.it.pas.exception.RoomNotFoundException;
import pl.lodz.p.it.pas.exception.UpdateRoomException;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({
        CreateRoomException.class,
        RoomHasActiveReservationsException.class,
        UpdateRoomException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleConflictExceptions(BaseApplicationException ex) { }

    @ExceptionHandler({
        RoomNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundExceptions(BaseApplicationException ex) { }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNotValidException(MethodArgumentNotValidException ex) { }
}
