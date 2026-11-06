package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.SolicitudAmistad;
import sw2.lab5.Entity.Usuario;

import java.util.List;

@Repository
public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Integer> {
    List<SolicitudAmistad> findByEmisor(Usuario emisor);
    List<SolicitudAmistad> findByReceptor(Usuario receptor);
}
