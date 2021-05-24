package com.github.dearrudam.studies.springwithfeign;

import java.util.List;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringWithFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringWithFeignApplication.class, args);
    }
}

@RestController
@RequestMapping(path = "/usuarios")
class UsuarioController {
    private UsuarioRepository usuarioRepository;

    public UsuarioController(final UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    Stream<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
            .stream()
            .map(u -> new UsuarioDTO(u.getId(), u.getNome()));
    }

    @PostMapping
    Usuario salvar(@Valid @RequestBody Usuario usuario) {
        this.usuarioRepository.save(usuario);
        return usuario;
    }

    // PUT /usuarios/1
    @PutMapping(path = "{id}")
    Usuario editar(@PathVariable("id") Long id, @Valid @RequestBody Usuario usuario) {
        usuario.setId(id);
        this.usuarioRepository.save(usuario);
        return usuario;
    }

    @DeleteMapping(path = "{id}")
    ResponseEntity remover(@PathVariable("id") Long id) {
        final var usuarioRef = this.usuarioRepository.findById(id);
        if (usuarioRef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}

@Data
@AllArgsConstructor
class UsuarioDTO {
    private Long id;
    private String nome;
}

@RestController
class CarroController {

    @GetMapping(path = "/usuarios/{id}")
    public List<Carro> listarCarros(
        @PathVariable Long useId) {
        // buscar o usuario
        // retornar o atributo carros
        return null;
    }

    @PostMapping(path = "/usuarios/{id}")
    public Usuario adicionarCarro(
        @PathVariable Long useId,
        @RequestBody Carro carro) {
        // buscar o usuario
        // retornar o atributo carros
        return null;
    }
}

@Entity
@Data
class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @OneToMany
    private List<Carro> carros;
}

@Entity
@Data
class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean rodizio;
    private Marca marca;
    private Modelo modelo;
    private Ano ano;
}

@Repository
interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("select new UsuarioDTO(u.id,u.nome) from Usuario u")
    List<UsuarioDTO> listarUsuarios();
}

@RestController
@Slf4j
class ModelosController {
    final ParallelumService service;

    ModelosController(final ParallelumService service) {
        this.service = service;
    }

    @GetMapping("/marcas")
    List<Marca> getMarcas() {
        return service.marcas();
    }

    @GetMapping("/marcas/{id}/modelos")
    ListaModelos getListaDeModelos(@PathVariable String id) {
        final var modelos = this.service.modelosPorCodigoMarca(id);
        return modelos;
    }
}

@FeignClient(name = "ParallelumService", url = "https://parallelum.com.br")
interface ParallelumService {
    @GetMapping("/fipe/api/v1/carros/marcas")
    List<Marca> marcas();
    @GetMapping("/fipe/api/v1/carros/marcas/{id}/modelos")
    ListaModelos modelosPorCodigoMarca(@PathVariable("id") String id);
}

@Data
@ToString
class ListaModelos {
    private List<Modelo> modelos;
    private List<Ano> anos;
}

@Data
@ToString
class Marca {
    private String codigo;
    private String nome;
}

@Data
@ToString
class Modelo {
    private String nome;
    private Integer codigo;
}

@Data
@ToString
class Ano {
    private String nome;
    private Integer codigo;
}