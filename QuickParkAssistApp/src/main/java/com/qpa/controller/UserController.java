package com.qpa.controller;

import com.qpa.entity.UserInfo;
import com.qpa.service.UserService;

import jakarta.validation.Valid;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("user", new UserInfo());
        return "ADD_user"; 
    }

 @PostMapping("/save")
public String saveUser(@Valid @ModelAttribute UserInfo user, 
                      BindingResult result,
                      RedirectAttributes ra) {
    if(result.hasErrors()) {
        return "ADD_user";
    }
    
    if (user.getDateOfRegister() == null) {
        user.setDateOfRegister(LocalDate.now());
    }
    if (user.getStatus() == null) {
        user.setStatus(UserInfo.Status.ACTIVE);
    }
    
    userService.addUser(user);
    ra.addFlashAttribute("success", "User saved to database successfully!");
    return "redirect:/";
}

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "ADD_user";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User Removed from database successfully!");
        return "redirect:/";
    }

    //  viewbyid
    @GetMapping("/{id}")  // This maps to /users/{id}
    public ResponseEntity<UserInfo> getUserById(@PathVariable Long id) {
        UserInfo user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if user not found
        }
    }
	
/*
addUser
viewUserById
updateUser

	
 */
}
