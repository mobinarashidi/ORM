package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController("")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping("/users/top")
    public List<TopUserDTO> getTopUsers(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "year") int year,
            @RequestParam(name = "gender") String gender
    ) {

        String normalizedGender = gender.equalsIgnoreCase("F") ? "Female"
                : gender.equalsIgnoreCase("M") ? "Male"
                : gender.toLowerCase();

        return userRepository.findAll().stream()
                .filter(user -> normalizedGender.equals(user.getGender()))
                .filter(user -> {
                    return Double.parseDouble(user.getStats_mean_score()) > 8;
                })
                .filter(user -> {
                    String date = user.getJoin_date();
                    if (date == null || date.length() < 4) return false;
                    int joinYear = Integer.parseInt(date.substring(0, 4));
                    return joinYear > year;
                })
                .sorted((u1, u2) -> {
                    double d1 = Double.parseDouble(u1.getStats_mean_score());
                    double d2 = Double.parseDouble(u2.getStats_mean_score());
                    return Double.compare(d2, d1);
                })
                .skip((long) (page - 1) * offset)
                .limit(offset)
                .map(user -> new TopUserDTO(
                        user.getUsername(),
                        user.getStats_mean_score()
                ))
                .toList();
    }


    @GetMapping("/users/active/{year}")
    public List<ActiveDTO> getActiveUsers(@PathVariable(name = "year") int year) {
        return userRepository.findAll().stream()
                .filter(user -> {
                    String date = user.getJoin_date();
                    if (date == null || date.length() < 4) return false;
                    int joinYear = Integer.parseInt(date.substring(0, 4));
                    return joinYear == year;
                })
                .filter(user -> {
                    Double.parseDouble(user.getUser_days_spent_watching());
                    return true;
                })
                .sorted((u1, u2) -> {
                    double d1 = Double.parseDouble(u1.getUser_days_spent_watching());
                    double d2 = Double.parseDouble(u2.getUser_days_spent_watching());
                    return Double.compare(d2, d1);
                })
                .limit(5)
                .map(user -> new ActiveDTO(
                        user.getUsername(),
                        user.getUser_days_spent_watching()
                ))
                .toList();
    }
}
