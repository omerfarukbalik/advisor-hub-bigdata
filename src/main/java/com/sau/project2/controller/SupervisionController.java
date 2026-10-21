package com.sau.project2.controller;

import com.sau.project2.model.Supervision;
import com.sau.project2.service.AdviserService;
import com.sau.project2.service.StudyService;
import com.sau.project2.service.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SupervisionController {

    private final SupervisionService supervisionService;
    private final AdviserService adviserService;
    private final StudyService studyService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("rows", supervisionService.findAllView());
        return "index";
    }

    @GetMapping("/supervision/new")
    public String newForm(Model model) {
        return form(new Supervision(), model);
    }

    @GetMapping("/supervision/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Supervision s = supervisionService.findById(id);
        return s == null ? "redirect:/" : form(s, model);
    }

    private String form(Supervision s, Model model) {
        model.addAttribute("supervision", s);
        model.addAttribute("advisers", adviserService.findAll());   // dropdown
        model.addAttribute("studies", studyService.findAll());      // dropdown
        return "supervision-form";
    }

    @PostMapping("/supervision/save")
    public String save(@ModelAttribute Supervision supervision) {
        supervisionService.save(supervision);
        return "redirect:/";
    }

    @PostMapping("/supervision/{id}/delete")
    public String delete(@PathVariable Integer id) {
        supervisionService.delete(id);
        return "redirect:/";
    }
}