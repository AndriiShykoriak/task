package ua.com.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ua.com.task.dao.MessageRepo;
import ua.com.task.entity.Message;
import ua.com.task.entity.User;
import ua.com.task.service.UserSevice;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
/*
@CrossOrigin(origins = "http://localhost:4200")
@RestController*/
@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private UserSevice userSevice;
    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        Iterable<Message> messages = messageRepo.findAll();

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);

        return "main";
    }

    @GetMapping("/user-messages/{user}")
    public String  userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            @RequestParam (required = false)Message message,
            Model model
    ){
        Set<Message> messages = user.getMessages();
       /* model.addAttribute("share",user);*/
//        model.addAttribute("usersCount",user.getListUserShere().size());
//        model.addAttribute("messageCount",message.getListMessageShare().size());
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages",messages);
        model.addAttribute("message",message);
        model.addAttribute("isCurrentUser",currentUser.equals(user));

        return "userMessages";
    }



    @GetMapping(value = "/delete/{id}")
    public String messageDelete(@PathVariable ("id") long id){
        userSevice.deleteMessage(id);
        return "redirect:/main";
    }


    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult  bindingResult,// list arguments valid
            /*@RequestParam String text,
            @RequestParam String tag,*/
            Model model,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
/*        Message message = new Message(text, tag, user);*/
            message.setAuthor(user);

            if(bindingResult.hasErrors()){
                Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);

                model.mergeAttributes(errorsMap);
                model.addAttribute("message", message);
            }else {

                saveFile(message, file);
                model.addAttribute("message",null);
                messageRepo.save(message);
            }
        Iterable<Message> messages = messageRepo.findAll();

        model.addAttribute("messages", messages);

        return "main";
    }

    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam ("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if(message.getAuthor().equals(currentUser)){
            if(!StringUtils.isEmpty(text)){
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }

            saveFile(message,file);
            messageRepo.save(message);
        }
        return "redirect:/user-messages/" + user;
    }
    @GetMapping("/share")
    public String Share(){
        return "main";
    }
}
