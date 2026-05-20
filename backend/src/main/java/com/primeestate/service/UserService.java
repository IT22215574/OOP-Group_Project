package com.primeestate.service;
import com.primeestate.model.User;
import com.primeestate.dto.UserLoginRequest;
import com.primeestate.dto.UserProfileResponse;
import java.util.List;
public interface UserService {
    UserProfileResponse registerUser(User user);
    UserProfileResponse loginUser(UserLoginRequest request);
    UserProfileResponse getUserProfile(Integer id);
    List<UserProfileResponse> searchUsers(String query, boolean isAdmin);
    UserProfileResponse updateUser(Integer id, User updatedUser);
    void deactivateUser(Integer id);
}
