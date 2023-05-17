package ru.yandex.yandexlavka.utils;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.yandexlavka.exceptions.NotFoundException;
import ru.yandex.yandexlavka.exceptions.OrderException;
import ru.yandex.yandexlavka.responses.BadRequestResponse;
import ru.yandex.yandexlavka.responses.NotFoundResponse;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(value = {NotFoundException.class, OrderException.class})
    public ResponseEntity<NotFoundResponse> handleNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse());
    }

    @ExceptionHandler(value = {ParseException.class, HttpMessageNotReadableException.class, SQLException.class,
            MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class, DateTimeParseException.class, UnexpectedTypeException.class,
            IllegalArgumentException.class})
    public ResponseEntity<BadRequestResponse> handleBadRequestException() {
        return ResponseEntity.badRequest().body(new BadRequestResponse());
    }
}
