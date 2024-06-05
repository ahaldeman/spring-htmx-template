package dev.templates.springhtmxtemplate.user

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class UserController(
    private val userService: UserService
) {
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/registration")
    fun registration(model: Model): String {
        model.addAttribute("user", User())
        return "registration"
    }

    @PostMapping("/registration")
    fun registerUser(user: User): String {
        userService.save(user)
        return "redirect:/login"
    }

    @GetMapping("/")
    fun home(model: Model): String {
        val currentUser = userService.getCurrentUser()
        model.addAttribute("user", currentUser)
        return "index"
    }
}