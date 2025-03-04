package com.qpa.service;

import com.qpa.entity.UserInfo;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    public UserInfo addUser(UserInfo user) { return userRepository.save(user); }
    public UserInfo getUserById(Long id) { return userRepository.findById(id).orElseThrow(() -> new InvalidEntityException("User not found")); }
    public List<UserInfo> getAllUsers() { return userRepository.findAll(); }
    public UserInfo updateUser(UserInfo user) { return userRepository.save(user); }
    public void deleteUser(Long id) { userRepository.deleteById(id); }
}