package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.UsuarioRepository;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UsuarioController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/list")
    public String listausuario(Model model) {
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        return "/user/list";
    }

    @GetMapping("/new")
    public String nuevousuario(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("listausuarios", usuarioRepository.findAll());
        return "/user/form";
    }
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes){
        usuarioRepository.save(usuario);
        return "redirect: /user/list";
    }

    @GetMapping("/editar")
    public String editar(Model model, @ModelAttribute("usuario") Usuario usuario, @RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
            model.addAttribute("usuario", usuario);
            return "/usuario/form";
        } else {
            return "user/list";
        }
    }
    @GetMapping("/borrar")
    public String borrar(Model model, @RequestParam("id") int id, RedirectAttributes redirectAttributes){
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if(usuario.isPresent()){
            usuarioRepository.deleteById(id);
        }
        return "user/list";
    }
}
