package br.com.api_techcollab.exceptions;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {


}
