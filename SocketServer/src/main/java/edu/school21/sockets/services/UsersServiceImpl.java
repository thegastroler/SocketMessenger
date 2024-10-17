package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signUp(String email, String password) throws DuplicateKeyException {
        User user = new User();
        password = passwordEncoder.encode(password);
        user.setEmail(email);
        user.setPassword(password);
        usersRepository.save(user);
    }

    @Override
    public boolean signIn(String email, String password) {
        boolean accept = false;
        Optional<User> optUser = usersRepository.findByEmail(email);
        if (optUser.isPresent()) {
            User user = optUser.get();
            String hashedPassword = user.getPassword();
            if (passwordEncoder.matches(password, hashedPassword)) {
                accept = true;
            }
        }
        return accept;
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return usersRepository.findByEmail(username);
    }
}
