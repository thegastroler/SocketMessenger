package edu.school21.sockets.services;

import edu.school21.sockets.models.User;

import java.util.Optional;

public interface UsersService {

    void signUp(String email, String password);
    boolean signIn(String email, String password);
    Optional<User> findByEmail(String username);
}
