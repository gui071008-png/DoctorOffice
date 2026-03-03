package service;

import data.UserRepository;
import model.User;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public User validateCredentials(String identifier, String password, String role) {
        if (!util.ValidationUtil.isNotBlank(identifier) || !util.ValidationUtil.isNotBlank(password)) {
            return null;
        }
        return userRepository.findByEmailAndRole(identifier, password, role);
    }

    public boolean emailExists(String email) {
        return userRepository.emailExists(email);
    }
}
