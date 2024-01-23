package dev.toktab.Tgram.controller;

import dev.toktab.Tgram.model.User;
import dev.toktab.Tgram.repository.UserRepo;
import dev.toktab.Tgram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String goHome(){
        return "Publicly accessible url";
    }
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody User user){
        return userService.create(user);
    }
    @GetMapping("/admin/users/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> getAllUsers(){
        return userService.getAll();
    }
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Object> getMyDetails(){
        if(!userService.isEnabled(userService.getActiveUserDetails())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User disabled. Please log out.\n'/logout");
        }
        return userService.getActiveUserDetails();
    }
    //todo delete/disable profile; update
    @GetMapping("/profile/delete")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Object> disableUser(){//todo add Are you sure text so they can verify if they want to disable/delete or not
        var activeUser = userService.getActiveUserDetails();
        userService.disable(activeUser);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User disabled. Please log out.\n'/logout");
    }

//    @GetMapping("/users/single")
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
//    public ResponseEntity<Object> getMyDetails(){
//        return ResponseEntity.ok(myUserRepo.findByEmail(getLoggedInUserDetails().getUsername()));
//    }
//    public UserDetails getLoggedInUserDetails(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
//            return (UserDetails) authentication.getPrincipal();
//        }
//        return null;
//    }
}