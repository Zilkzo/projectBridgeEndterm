package kz.bitlab.group21boot.controllers;

import kz.bitlab.group21boot.entities.*;
import kz.bitlab.group21boot.repositories.*;
import kz.bitlab.group21boot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CommentsReposiory commentsReposiory;

    @Autowired
    UserService userService;

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    CategoriesRepository categoriesRepository;

    @GetMapping(path = "/")
    public String index(Model model, @RequestParam(name = "key", defaultValue = "", required = false) String key){
        return "index";
    }


    //____________ADDING POST________________________
    @GetMapping(path = "/addPostsPage")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR')")
    public String addPostsPage(Model model){
        model.addAttribute("user", getUserData());
        return "addPosts";
    }




    @PostMapping(path = "/addPost")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR')")
    public String addItem(Model model,
                          @RequestParam(name = "title") String title,
                          @RequestParam(name = "shortContent") String shortContent,
                          @RequestParam(name = "content") String content,
                          @RequestParam(name = "author") String author){
        Users auth = userRepository.findByEmailAndIsActive(author, true);
        postsRepository.save(new NewPost(title, shortContent,content, auth, new Date()));
        return "redirect:/";
    }

    //___________________________EDIT________________________________________

    @GetMapping(path = "/editPage/{itemsid}")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public String editPage(Model model, @PathVariable(name = "itemsid") Long id){
        Items item = itemRepository.findById(id).get();
        model.addAttribute("item", item);

        return "edit";

    }

    //_______________________DELETE________________

    @GetMapping(path = "/deleteItem/{id}")
    public String deletePage(Model model,
                          @PathVariable(name = "id") Long id){

        Items item = itemRepository.findById(id).get();
        itemRepository.delete(item);
        Users u  = getUserData();

        return "redirect:/profile";


    }


//__________________DETAILS___________________________

    @GetMapping(path = "/details/{itemsId}")
    public String details(Model model, @PathVariable(name = "itemsId") Long itemid){
        Optional<Items>  item = itemRepository.findById(itemid);
        Items item1 = item.get();
        model.addAttribute("user", getUserData());
        model.addAttribute("items", item1);
        return "details";

    }

    @PostMapping(path = "/saveItem")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public String savePost(@RequestParam(name = "name") String name,
                           @RequestParam(name = "description") String description,
                           @RequestParam(name = "categories") String categories,
                           @RequestParam(name = "price") int price,
                           @RequestParam(name = "id") Long id){

        Items item = itemRepository.findById(id).get();
        Categories cat = categoriesRepository.findAllByName(categories);
        if(item!=null){
            item.setName(name);
            item.setPrice(price);
            item.setDescription(description);
            item.setCategories(cat);
            itemRepository.save(item);
        }
        return "redirect:/details/"+id;
    }



    @GetMapping(path = "/enter")
    public String enter(Model model){

        return "enter";

    }

    @GetMapping(path = "/register")
    public String register(Model model){
        return "register";
    }


   // ______________________ADDING USER AND MODERATOR_________________________

    @GetMapping(path = "/addUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addUser(Model model){
        return "addUser";
    }

    @GetMapping(path = "/addModerator")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addModerator(Model model){
        return "addModerator";
    }

    @PostMapping(path = "/adduser")
    public String addUser(@RequestParam(name = "email") String email,
                          @RequestParam(name = "password") String password,
                          @RequestParam(name = "re_password") String rePassword,
                          @RequestParam(name = "full_name", required = false, defaultValue = "") String fullName){

        String redirect = "redirect:/register?error";


      //  Users existedUser = userRepository.findByEmail(email);
        Users user = userRepository.findByEmailAndIsActive(email, true);
        if(user != null){
            redirect = "redirect:/register?emailIsAlreadyUsed";
            return redirect;
        }


        if(user==null){

            if(email.equals("ilyas@gmail.com") && password.equals(rePassword)){
                Set<Roles> roles = new HashSet<>();
                Roles userRole = roleRepository.getOne(1L);
                roles.add(userRole);

                user = new Users(email, password, fullName, true, roles);
                userService.registerUser(user);
                redirect = "redirect:/register?success";
            }
            else if(password.equals(rePassword)){

                Set<Roles> roles = new HashSet<>();
                Roles userRole = roleRepository.getOne(3L);
                roles.add(userRole);

                user = new Users(email, password, fullName, true, roles);
                userService.registerUser(user);
                redirect = "redirect:/register?success";

            }

        }

        return redirect;

    }


    @PostMapping(path = "/upduser")
    public String upduser(@RequestParam(name = "email") String email,
                          @RequestParam(name = "password") String password,
                          @RequestParam(name = "re_password") String rePassword,
                          @RequestParam(name = "full_name", required = false, defaultValue = "") String fullName){

        String redirect = "redirect:/register?error";

        Users user = userRepository.findByEmail(email);

        if(email != null){
            user.setEmail(email);
        }
        if(fullName != null){
            user.setFullName(fullName);
        }
        if(password!=null){
            if(password.equals(rePassword)){
                user.setPassword(password);
            }
        }
        userService.registerUser(user);
        return "redirect:/profile";

    }

    @PostMapping(path = "/addmod")
    public String addMod(@RequestParam(name = "email") String email,
                          @RequestParam(name = "password") String password,
                          @RequestParam(name = "re_password") String rePassword,
                          @RequestParam(name = "full_name", required = false, defaultValue = "") String fullName){

        String redirect = "redirect:/register?error";

        Users user = userRepository.findByEmailAndIsActive(email, true);

        if(user==null){

            if(password.equals(rePassword)){

                Set<Roles> roles = new HashSet<>();
                Roles userRole = roleRepository.getOne(2L);
                roles.add(userRole);

                user = new Users(email, password, fullName, true, roles);
                userService.registerUser(user);
                redirect = "redirect:/register?success";

            }

        }

        return redirect;

    }
    //___________________________________________________________________________________


    @GetMapping(path = "/profile")
    public String profile(Model model){
        Roles u = roleRepository.findAllById(3L);
        Roles m = roleRepository.findAllById(2L);
        List<Users> users = userRepository.findAllByRoles(u);
        List<Users> moderators = userRepository.findAllByRoles(m);
        List<Items> items = itemRepository.findAllByAuthor(getUserData());
        model.addAttribute("items", items);
        model.addAttribute("user", getUserData());
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);

        return "profile";
    }

    @GetMapping(path = "/updateProfile")
    public String updprofile(Model model){

        Users user = getUserData();
        model.addAttribute("user", user);


        return "updProfile";
    }



    public Users getUserData(){
        Users userData = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User secUser = (User)authentication.getPrincipal();
            userData = userRepository.findByEmailAndIsActive(secUser.getUsername(), true);
        }
        return userData;
    }


    //________________________Comment________________

    @PostMapping(path = "/editComment")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public String editComment(Model model,
                             @RequestParam(name = "idU") Long idU,
                              @RequestParam(name = "idC") Long idC,
                             @RequestParam(name = "comment") String comment){
        Comments com = commentsReposiory.findById(idC).get();
        com.setComment(comment);
        com.setDate(new Date());
        commentsReposiory.save(com);
        return "redirect:/details/"+idU;


    }


    @PostMapping(path = "/addComment")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public String addComment(Model model,
                              @RequestParam(name = "idP") Long idP,
                              @RequestParam(name = "newComment") String newcom,
                             @RequestParam(name = "idU") Long idU
                             ){
        Users u = userRepository.findById(idU).get();
        NewPost p = postsRepository.findById(idP).get();
        Comments com = new Comments(u,p,newcom,new Date());
        commentsReposiory.save(com);
        return "redirect:/details/"+idP;


    }

    @PostMapping(path = "/deleteComment")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public String deleteComment(Model model,
                             @RequestParam(name = "idP") Long idP,
                             @RequestParam(name = "idC") Long idC
    ){
        Comments com = commentsReposiory.findById(idC).get();
        commentsReposiory.delete(com);

        return "redirect:/details/"+idP;


    }


    @GetMapping(path = "/block/{id}")
    public String block(Model model, @PathVariable(name = "id") Long id){

        Users user = userRepository.findById(id).get();
        user.setActive(false);
        userRepository.save(user);
        Roles u = roleRepository.findAllById(3L);
        Roles m = roleRepository.findAllById(2L);
        List<Users> users = userRepository.findAllByRoles(u);
        List<Users> moderators = userRepository.findAllByRoles(m);
        model.addAttribute("user", getUserData());
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);

        return "profile";

    }
    @GetMapping(path = "/deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable(name = "id") Long id){

        Users user = userRepository.findById(id).get();
        userRepository.delete(user);

        Roles u = roleRepository.findAllById(3L);
        Roles m = roleRepository.findAllById(2L);
        List<Users> users = userRepository.findAllByRoles(u);
        List<Users> moderators = userRepository.findAllByRoles(m);
        model.addAttribute("user", getUserData());
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);

        return "profile";

    }

    @GetMapping(path = "/unblock/{id}")
    public String unblock(Model model, @PathVariable(name = "id") Long id){

        Users user = userRepository.findById(id).get();
        user.setActive(true);
        userRepository.save(user);

        Roles u = roleRepository.findAllById(3L);
        Roles m = roleRepository.findAllById(2L);
        List<Users> users = userRepository.findAllByRoles(u);
        List<Users> moderators = userRepository.findAllByRoles(m);
        model.addAttribute("user", getUserData());
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);

        return "profile";

    }






    @PostMapping(path = "/changePassword")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String changePassword(@RequestParam(name = "password") String password,
                              @RequestParam(name = "re_password") String repassword,
                              @RequestParam(name = "id") Long id){


        Users u = userRepository.findById(id).get();
        if(password.equals(repassword)){

            u.setPassword(password);

            userService.registerUser(u);


        }

        return "redirect:/profile";
    }


    @GetMapping(path = "/refreshPass/{id}")
    @PreAuthorize("isAuthenticated()")
    public String refreshPass(Model model, @PathVariable(name = "id") Long id){

        Users moderator = userRepository.findById(id).get();
        if(moderator!=null) {
            model.addAttribute("moderators", moderator);
        }
        return "passwChange";

    }

    @GetMapping(path = "/additem")
    public String additem(Model model){
        return "additempage";
    }


    @PostMapping(path = "/additemnew")
    public String additemnew(@RequestParam(name = "name") String name,
                           @RequestParam(name = "price") int price,
                           @RequestParam(name = "description") String description,
                           @RequestParam(name = "categories") String categories,
                             @RequestParam(name = "image") MultipartFile image){

        Categories cat = categoriesRepository.findAllByName(categories);
        Users u = getUserData();
        Items item = new Items(name, description, price, u, cat,image,null);
        itemRepository.save(item);

        String imageName = item.getId()+"_"+ item.getName() + ".jpg";
        String imagePath = "product_images/"+imageName;
        item.setImagePath(imagePath);
        itemRepository.save(item);


        try {
            byte[] bytes = image.getBytes();
            FileOutputStream fos = new FileOutputStream(new File("src/main/resources/static/assets/product_images/"+imageName));
            BufferedOutputStream stream = new BufferedOutputStream(fos);
            stream.write(bytes);
            fos.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error here");
        }

        return "redirect:/profile";
    }



    @GetMapping(path = "/women")
    public String women(Model model){
        Categories wom = categoriesRepository.findAllByName("women");
        List<Items> itemWom = itemRepository.findAllByCategories(wom);
        Roles u = roleRepository.findAllById(3L);
        Roles m = roleRepository.findAllById(2L);
        List<Users> users = userRepository.findAllByRoles(u);
        List<Users> moderators = userRepository.findAllByRoles(m);
        List<Items> items = itemRepository.findAllByAuthor(getUserData());
        model.addAttribute("items", items);
        model.addAttribute("user", getUserData());
        model.addAttribute("users", users);
        model.addAttribute("moderators", moderators);
        model.addAttribute("womenItems",itemWom);

        return "womenPage";
    }


    @GetMapping(path = "/men")
        public String men(Model model){
            Categories men = categoriesRepository.findAllByName("men");
            List<Items> itemMen = itemRepository.findAllByCategories(men);

            model.addAttribute("menItems",itemMen);

        return "menPage";
    }

    @GetMapping(path = "/all")
    public String all(Model model){
        List<Items> items = itemRepository.findAll();

        model.addAttribute("items",items);

        return "allItemsPage";
    }

    @GetMapping(path = "/kids")
    public String kids(Model model){
        Categories kids = categoriesRepository.findAllByName("kids");
        List<Items> itemKids = itemRepository.findAllByCategories(kids);

        model.addAttribute("itemKids",itemKids);

        return "kidsPage";
    }

    @GetMapping(path = "/accessories")
    public String accessories(Model model){
        Categories acc = categoriesRepository.findAllByName("accessories");
        List<Items> accessories = itemRepository.findAllByCategories(acc);

        model.addAttribute("accessories",accessories);

        return "accessoriesPage";
    }

    @GetMapping(path = "/footwear")
    public String footwear(Model model){
        Categories foot = categoriesRepository.findAllByName("footwear");
        List<Items> footwear = itemRepository.findAllByCategories(foot);

        model.addAttribute("footwear",footwear);

        return "footwearPage";
    }
}


