package com.primeestate.service.impl;
import com.primeestate.model.User;
import com.primeestate.model.enums.UserStatus;
import com.primeestate.repository.UserRepository;
import com.primeestate.service.UserService;
import com.primeestate.dto.UserLoginRequest;
import com.primeestate.dto.UserProfileResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override public UserProfileResponse registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) throw new IllegalArgumentException("Email already exists");
        user.setAccountStatus(UserStatus.ACTIVE);
        return mapToResponse(userRepository.save(user));
    }

    @Override public UserProfileResponse loginUser(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!user.getPassword().equals(request.getPassword())) throw new IllegalArgumentException("Invalid email or password");
        if (user.getAccountStatus() == UserStatus.INACTIVE) throw new IllegalStateException("Account deactivated");
        return mapToResponse(user);
    }

    @Override public UserProfileResponse getUserProfile(Integer id) {
        return mapToResponse(userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    @Override public List<UserProfileResponse> searchUsers(String query, boolean isAdmin) {
        if (!isAdmin) throw new SecurityException("Unauthorized");
        return userRepository.findByNameContainingIgnoreCase(query).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override public UserProfileResponse updateUser(Integer id, User updated) {
        User existing = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        existing.setName(updated.getName());
        existing.setContactNumber(updated.getContactNumber());
        existing.setPreferredLocation(updated.getPreferredLocation());
        existing.setPreferredAgentSpecialization(updated.getPreferredAgentSpecialization());
        return mapToResponse(userRepository.save(existing));
    }

    @Override public void deactivateUser(Integer id) {
        User existing = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        existing.setAccountStatus(UserStatus.INACTIVE);
        userRepository.save(existing);
    }

    private UserProfileResponse mapToResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getContactNumber(), user.getPreferredLocation(), user.getPreferredAgentSpecialization(), user.getAccountStatus());
    }
}
