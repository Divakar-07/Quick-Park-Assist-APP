package com.qpa.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qpa.entity.UserInfo;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/register")
    public String showAddForm(Model model, HttpServletRequest request) {
        if (userService.isAuthenticated(request)){
            return "redirect:/";
        }
        model.addAttribute("user", new UserInfo());
        return "Register"; 
    }

    @GetMapping("/login")
     public String Login(HttpServletRequest request) {
        if (userService.isAuthenticated(request)){
            return "redirect:/";
        }
        return "Login";
    }
    

 @PostMapping("/save")
public String saveUser(@Valid @ModelAttribute UserInfo user, 
                      BindingResult result,
                      RedirectAttributes ra) {
try {
        if(result.hasErrors()) {
            return "Register";
        }
        
        if (user.getDateOfRegister() == null) {
            user.setDateOfRegister(LocalDate.now());
        }
        if (user.getStatus() == null) {
            user.setStatus(UserInfo.Status.ACTIVE);
        }
        System.out.println("reached inside the save user");
        userService.addUser(user);
        ra.addFlashAttribute("success", "User saved successfully!");
        
        return "redirect:/";
} catch (Exception e) {
    return e.getMessage();
    // TODO: handle exception
}
}

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        if (userService.isAuthenticated(request)){
            model.addAttribute("user", userService.getUserById(id));
            return "ADD_user";
        }
        else {
            return "unauthorized request";
        }
            
    }

@GetMapping("/delete/{id}")
@ResponseBody
public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("User deleted successfully");
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body("Cannot delete user as it has related entities.");
    } catch (InvalidEntityException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An error occurred while deleting the user.");
    }
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
