package com.sau.project2.controller;

import com.sau.project2.model.Adviser;
import com.sau.project2.service.AdviserService;
import com.sau.project2.service.HdfsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class AdviserController {

    private final AdviserService adviserService;
    private final HdfsService hdfsService;

    @GetMapping("/advisers")
    public String list(Model model) {
        model.addAttribute("advisers", adviserService.findAll());
        return "advisers";
    }

    @GetMapping("/adviser/new")
    public String newForm(Model model) {
        model.addAttribute("adviser", new Adviser());
        return "adviser-form";
    }

    @GetMapping("/adviser/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Adviser a = adviserService.findById(id);
        if (a == null) return "redirect:/advisers";
        model.addAttribute("adviser", a);
        return "adviser-form";
    }

    @PostMapping("/adviser/save")
    public String save(@ModelAttribute Adviser adviser,
                       @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" +
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            hdfsService.upload(file, fileName);   // image bytes -> HDFS
            adviser.setPhoto(fileName);           // only the name -> PostgreSQL
        }
        adviserService.save(adviser);
        return "redirect:/advisers";
    }

    @PostMapping("/adviser/{id}/delete")
    public String delete(@PathVariable Integer id) {
        adviserService.delete(id);
        return "redirect:/advisers";
    }
}