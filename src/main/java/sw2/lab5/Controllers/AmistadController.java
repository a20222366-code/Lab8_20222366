package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.SolicitudAmistad;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.SolicitudAmistadRepository;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/amistad")
public class AmistadController {

    @Autowired
    SolicitudAmistadRepository solicitudAmistadRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("")
    public String misSolicitudes(Model model, HttpSession session) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        model.addAttribute("enviadas", solicitudAmistadRepository.findByEmisor(yo));
        model.addAttribute("recibidas", solicitudAmistadRepository.findByReceptor(yo));
        return "amistad/lista";
    }

    @PostMapping("/enviar")
    public String enviar(@RequestParam("idReceptor") int idReceptor,
                         HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");

        Optional<Usuario> optReceptor = usuarioRepository.findById(idReceptor);
        if (!optReceptor.isPresent()) {
            attr.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/post";
        }
        Usuario receptor = optReceptor.get();

        if (receptor.getId_user() == yo.getId_user()) {
            attr.addFlashAttribute("error", "No puedes enviarte una solicitud a ti mismo");
            return "redirect:/post";
        }

        boolean yaExiste = false;
        List<SolicitudAmistad> enviadas = solicitudAmistadRepository.findByEmisor(yo);
        for (SolicitudAmistad s : enviadas) {
            if (s.getReceptor().getId_user() == receptor.getId_user()) {
                yaExiste = true;
            }
        }
        List<SolicitudAmistad> recibidas = solicitudAmistadRepository.findByReceptor(yo);
        for (SolicitudAmistad s : recibidas) {
            if (s.getEmisor().getId_user() == receptor.getId_user()) {
                yaExiste = true;
            }
        }
        if (yaExiste) {
            attr.addFlashAttribute("error", "Ya existe una solicitud con este usuario");
            return "redirect:/post";
        }

        SolicitudAmistad solicitud = new SolicitudAmistad();
        solicitud.setEmisor(yo);
        solicitud.setReceptor(receptor);
        solicitud.setEstado("PENDIENTE");
        solicitudAmistadRepository.save(solicitud);

        attr.addFlashAttribute("msg", "Solicitud enviada a " + receptor.getFirstname());
        return "redirect:/post";
    }
}
