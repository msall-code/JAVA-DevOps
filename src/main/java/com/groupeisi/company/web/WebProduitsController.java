package com.groupeisi.company.web;

import com.groupeisi.company.dto.ProduitsDto;
import com.groupeisi.company.repository.UserAccountRepository;
import com.groupeisi.company.service.ProduitsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/produits")
@RequiredArgsConstructor
public class WebProduitsController {

    private final ProduitsService produitsService;
    private final UserAccountRepository userAccountRepository;

    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("produits", produitsService.findAll());
        model.addAttribute("userEmail", principal.getName());
        return "produits/list";
    }

    @GetMapping("/nouveau")
    public String newForm(Model model) {
        model.addAttribute("produit", new ProduitsDto());
        return "produits/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProduitsDto dto,
                       Principal principal,
                       Model model) {
        try {
            userAccountRepository.findByEmail(principal.getName())
                    .ifPresent(user -> dto.setUserId(user.getId()));
            produitsService.create(dto);
            return "redirect:/produits";
        } catch (Exception e) {
            model.addAttribute("produit", dto);
            model.addAttribute("error", e.getMessage());
            return "produits/form";
        }
    }

    @GetMapping("/edit/{ref}")
    public String editForm(@PathVariable String ref, Model model) {
        model.addAttribute("produit", produitsService.findByRef(ref));
        return "produits/form";
    }

    @PostMapping("/update/{ref}")
    public String update(@PathVariable String ref,
                         @ModelAttribute ProduitsDto dto,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            userAccountRepository.findByEmail(principal.getName())
                    .ifPresent(user -> dto.setUserId(user.getId()));
            produitsService.update(ref, dto);
            redirectAttributes.addFlashAttribute("success",
                    "Produit mis à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/produits";
    }

    @GetMapping("/delete/{ref}")
    public String delete(@PathVariable String ref,
                         RedirectAttributes redirectAttributes) {
        try {
            produitsService.delete(ref);
            redirectAttributes.addFlashAttribute("success",
                    "Produit supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/produits";
    }
}