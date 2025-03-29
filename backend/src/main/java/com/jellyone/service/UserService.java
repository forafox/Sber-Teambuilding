package com.jellyone.service;

import com.jellyone.domain.User;
import com.jellyone.domain.enums.Role;
import com.jellyone.exception.ResourceAlreadyExistsException;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(String username, String password, String name, String email) {
        log.info("Try to register user with username: {}", username);
        checkUserAlreadyExists(username);

        User user = new User(0L, username, passwordEncoder.encode(password), name, email, Role.USER);
        userRepository.save(user);
        log.info("Registered user {}", username);
    }

    public User getById(Long id) {
        log.info("Try to retrieve user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getByUsername(String username) {
        log.info("Try to retrieve user with username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Page<User> getUsers(String search, int page, int size) {
        log.info("Try to retrieve users with search: {}", search);
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllUsersWithSomeParameters(search, pageable);
    }

    private void checkUserAlreadyExists(String username) {
        log.info("Try to check if user with username: {} already exists", username);
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new ResourceAlreadyExistsException("User already exists");
        }
    }

    private Specification<User> getUserSearchSpecification(String search) {
        log.info("Try to create user search specification with search: {}", search);
        return (root, query, criteriaBuilder) -> {
            if (search.isBlank()) {
                return null;
            }
            String searchPattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), searchPattern)
            );
        };
    }
}
