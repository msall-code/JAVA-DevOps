package com.groupeisi.company.web;

import com.groupeisi.company.dto.AchatsDto;
import com.groupeisi.company.dto.VentesDto;
import com.groupeisi.company.repository.UserAccountRepository;
import com.groupeisi.company.service.AchatsService;
import com.groupeisi.company.service.ProduitsService;
import com.groupeisi.company.service.VentesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventes")
@RequiredArgsConstructor
public class WebVentesController {

    private final VentesService ventesService;
    private final ProduitsService produitsService;
    private final AchatsService achatsService;
    private final UserAccountRepository userAccountRepository;

    @GetMapping
    public String list(Model model, Principal principal) {

        List<AchatsDto> tousLesAchats = achatsService.findAll();

        // Filtre en Java — achats non encore vendus
        List<AchatsDto> achatsNonValides = tousLesAchats.stream()
                .filter(a -> a.getVenteValidee() == null
                        || !a.getVenteValidee())
                .collect(Collectors.toList());

        model.addAttribute("ventes", ventesService.findAll());
        model.addAttribute("achatsNonValides", achatsNonValides);
        model.addAttribute("userEmail", principal.getName());
        return "ventes/list";
    }

    @GetMapping("/nouveau")
    public String newForm(Model model) {
        model.addAttribute("vente", new VentesDto());
        model.addAttribute("produits", produitsService.findAll());
        return "ventes/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute VentesDto dto,
                       Principal principal,
                       Model model) {
        try {
            userAccountRepository.findByEmail(principal.getName())
                    .ifPresent(user -> dto.setUserId(user.getId()));
            ventesService.create(dto);
            return "redirect:/ventes";
        } catch (Exception e) {
            model.addAttribute("vente", dto);
            model.addAttribute("produits", produitsService.findAll());
            model.addAttribute("error", e.getMessage());
            return "ventes/form";
        }
    }

    @GetMapping("/valider/{achatId}")
    public String validerVente(@PathVariable Long achatId,
                               RedirectAttributes redirectAttributes) {
        try {
            ventesService.createFromAchat(achatId);
            redirectAttributes.addFlashAttribute("success",
                    "Vente validée ! Stock mis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            ventesService.delete(id);
            redirectAttributes.addFlashAttribute("success",
                    "Vente supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventes";
    }
}