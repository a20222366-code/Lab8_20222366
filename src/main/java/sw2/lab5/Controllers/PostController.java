package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Post;
import sw2.lab5.Entity.SolicitudAmistad;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.PostRepository;
import sw2.lab5.Repository.SolicitudAmistadRepository;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostRepository postRepository;
    @Autowired
    SolicitudAmistadRepository solicitudAmistadRepository;


    @GetMapping("")
    public String listaPost(Model model, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Usuario yo = (Usuario) session.getAttribute("usuario");

        List<Post> todos = postRepository.findAll();
        List<Post> visibles = new ArrayList<>();
        for (Post p : todos) {
            if ("admin".equals(rol)) {
                visibles.add(p);
            } else if (p.isPublished()) {
                visibles.add(p);
            } else if (yo != null && p.getUser().getId_user() == yo.getId_user()) {
                visibles.add(p);
            }
        }


        List<Integer> idsRelacionados = new ArrayList<>();
        if (yo != null) {
            List<SolicitudAmistad> enviadas = solicitudAmistadRepository.findByEmisor(yo);
            for (SolicitudAmistad s : enviadas) {
                idsRelacionados.add(s.getReceptor().getId_user());
            }
            List<SolicitudAmistad> recibidas = solicitudAmistadRepository.findByReceptor(yo);
            for (SolicitudAmistad s : recibidas) {
                idsRelacionados.add(s.getEmisor().getId_user());
            }
        }

        model.addAttribute("lista", visibles);
        model.addAttribute("idsRelacionados", idsRelacionados);
        return "post/lista";
    }

    @GetMapping("/create")
    public String nuevoPost(Model model) {
        model.addAttribute("post", new Post());
        return "post/form";
    }


    @GetMapping("/edit")
    public String editarPost(Model model, @RequestParam("id") int id,
                             HttpSession session, RedirectAttributes attr) {
        Optional<Post> optPost = postRepository.findById(id);
        if (optPost.isPresent()) {
            Post post = optPost.get();
            if (puedeEditar(post, session)) {
                model.addAttribute("post", post);
                return "post/form";
            }
            attr.addFlashAttribute("error", "No tienes permiso para editar este post");
        }
        return "redirect:/post";
    }


    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("post") Post post,
                          HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");

        if (post.getId_Post() == 0) {
            post.setUser(yo);
            postRepository.save(post);
            attr.addFlashAttribute("msg", "Post creado correctamente");
        } else {
            Optional<Post> optPost = postRepository.findById(post.getId_Post());
            if (!optPost.isPresent()) {
                return "redirect:/post";
            }
            Post original = optPost.get();
            if (!puedeEditar(original, session)) {
                attr.addFlashAttribute("error", "No tienes permiso para editar este post");
                return "redirect:/post";
            }
            original.setTitle(post.getTitle());
            original.setSummary(post.getSummary());
            original.setContent(post.getContent());
            original.setPublished(post.isPublished());
            postRepository.save(original);
            attr.addFlashAttribute("msg", "Post actualizado correctamente");
        }
        return "redirect:/post";
    }


    @PostMapping("/borrar")
    public String borrarPost(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Post> opt = postRepository.findById(id);
        if (opt.isPresent()) {
            postRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Post eliminado correctamente");
        }
        return "redirect:/post";
    }

    //Planteamos que admin edita cualquiera, pero el user solo puede los suyos
    private boolean puedeEditar(Post post, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        Usuario yo = (Usuario) session.getAttribute("usuario");
        if ("admin".equals(rol)) {
            return true;
        }
        return yo != null && post.getUser().getId_user() == yo.getId_user();
    }
}