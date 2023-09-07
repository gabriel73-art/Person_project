package com.bqpro.project.Exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
       super("Resource Not Found");
    }

}
