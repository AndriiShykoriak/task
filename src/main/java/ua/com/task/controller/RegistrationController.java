package ua.com.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ua.com.task.entity.User;
import ua.com.task.entity.dto.CaptchaResponseDto;
import ua.com.task.service.UserSevice;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private final static String CAPTHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    @Autowired
    private UserSevice userSevice;

    @Autowired
    private RestTemplate restTemplate;
    @Value("${recaptcha.secret}")
    private String secret;
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponce,
            @Valid User user,
            BindingResult bindingResult,
            Model model) {
        String url = String.format(CAPTHA_URL, secret, captchaResponce);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if(!response.isSuccess()){
            model.addAttribute("captchaError","Fill captcha");
        }

        boolean  isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
        if(isConfirmEmpty){
            model.addAttribute("password2Error","password confirmation can't be empty");
        }

        boolean isPasswordDifferent = user.getPassword() != null && !user.getPassword().equals(passwordConfirm);

        if(isPasswordDifferent){
            model.addAttribute("passwordError","Password  are different");
        }

        if(isConfirmEmpty || bindingResult.hasErrors() || isPasswordDifferent || !response.isSuccess()){
            Map<String,String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if (!userSevice.addUser(user)) {
            model.addAttribute("message", "User exists!");
            return "registration";
        }

        return "redirect:/login" ;
    }
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userSevice.activateUser(code);

        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }
}