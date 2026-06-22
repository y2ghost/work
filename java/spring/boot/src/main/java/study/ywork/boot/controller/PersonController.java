package study.ywork.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.boot.dao.PersonDao;
import study.ywork.boot.domain.Person;

@Controller
@RequestMapping("/person")
public class PersonController {
    private PersonDao dao;

    public PersonController(PersonDao dao) {
        this.dao = dao;
    }

    @PostMapping
    public String handlePostRequest(Person person) {
        dao.save(person);
        return "redirect:/person";
    }

    @GetMapping
    public String handleGetRequest(Model model) {
        model.addAttribute("persons", dao.loadAll());
        return "thymeleaf-person-view";
    }
}
