package com.groupeisi.company.web;

import com.groupeisi.company.dto.UserAccountDto;
import com.groupeisi.company.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebAuthController {

    private final UserAccountService userAccountService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        try {
            UserAccountDto dto = UserAccountDto.builder()
                    .email(email)
                    .password(password)
                    .role("USER")
                    .build();
            userAccountService.create(dto);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}