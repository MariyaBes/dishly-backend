package com.virality.dishly.service;

public class UserAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyUsedException() {
        super("Такой Email уже есть в системе!");
    }
}
