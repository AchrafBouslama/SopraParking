package com.example.achref.Controller.user;
import com.example.achref.Entities.user.User;
import com.example.achref.Services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@CrossOrigin(origins="*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/Createprofile")
    public void createUserProfile(@RequestBody User user) {
      userProfileService.createUserProfile(user);
    }


    @GetMapping("/displayUser")
    public List<User> displayUser(){
        return userProfileService.displayUser();
    }
    @DeleteMapping("/deleteQuiz/{idUser}")
    public void deleteQuiz(@PathVariable Integer idUser){
        userProfileService.deleteUser(idUser);
    }

    @PutMapping("/updateQuiz")
    public void updateUser(@RequestBody User user){
        userProfileService.updateUser(user);
    }

}
