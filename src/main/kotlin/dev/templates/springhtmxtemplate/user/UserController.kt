package dev.templates.springhtmxtemplate.user

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserController(
    private val userService: UserService
) {
    @GetMapping("/login")
    fun login(@RequestParam("error") error: String?, model: Model): String {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password")
        }
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