package com.groupeisi.company.web;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.repository.UserAccountRepository;
import com.groupeisi.company.service.AchatsService;
import com.groupeisi.company.service.ProduitsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/achats")
@RequiredArgsConstructor
public class WebAchatsController {

    private final AchatsService achatsService;
    private final ProduitsService produitsService;
    private final UserAccountRepository userAccountRepository;

    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("achats", achatsService.findAll());
        model.addAttribute("userEmail", principal.getName());
        return "achats/list";
    }

    @GetMapping("/nouveau")
    public String newForm(Model model) {
        model.addAttribute("achat", new AchatsDto());
        model.addAttribute("produits", produitsService.findAll());
        return "achats/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AchatsDto dto,
                       Principal principal,
                       Model model) {
        try {
            userAccountRepository.findByEmail(principal.getName())
                    .ifPresent(user -> dto.setUserId(user.getId()));
            achatsService.create(dto);
            return "redirect:/achats";
        } catch (Exception e) {
            model.addAttribute("achat", dto);
            model.addAttribute("produits", produitsService.findAll());
            model.addAttribute("error", e.getMessage());
            return "achats/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            achatsService.delete(id);
            redirectAttributes.addFlashAttribute("success",
                    "Achat supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/achats";
    }
}