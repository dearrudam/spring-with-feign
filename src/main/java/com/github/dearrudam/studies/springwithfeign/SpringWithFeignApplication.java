package com.github.dearrudam.studies.springwithfeign;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
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
@EnableFeignClients
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
    final UsuarioRepository usuarioRepository;
    final CarroRepository carroRepository;
    public CarroController(
        final UsuarioRepository usuarioRepository,
        final CarroRepository carroRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.carroRepository = carroRepository;
    }

    @GetMapping(path = "/usuarios/{usuarioId}/carros")
    ResponseEntity<?> listarCarros(
        @PathVariable Long usuarioId
    ) {
        final var usuarioRef = this.usuarioRepository.findById(usuarioId);
        if (usuarioRef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioRef.get().getCarros());
    }

    @PostMapping(path = "/usuarios/{usuarioId}/carros")
    ResponseEntity<?> adicionarCarro(
        @PathVariable Long usuarioId,
        @RequestBody @Valid Carro carro
    ) {
        final var usuarioRef = this.usuarioRepository.findById(usuarioId);
        if (usuarioRef.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var usuario = usuarioRef.get();
        carro.setId(null); // garantir que o carro será novo
        this.carroRepository.save(carro);
        usuario.getCarros().add(carro);
        this.usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario.getCarros());
    }
}

@Entity
@Data
class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String nome;
    @OneToMany
    private Set<Carro> carros;
}

@Entity
@Data
@EqualsAndHashCode(of = { "id" })
class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean rodizio;
    @AttributeOverrides(
        {
            @AttributeOverride(
                name = "codigo",
                column = @Column(name = "marca_codigo")
            ),
            @AttributeOverride(
                name = "nome",
                column = @Column(name = "marca_nome")
            )
        }
    )
    private Marca marca;
    @AttributeOverrides(
        {
            @AttributeOverride(
                name = "codigo",
                column = @Column(name = "modelo_codigo")
            ),
            @AttributeOverride(
                name = "nome",
                column = @Column(name = "modelo_nome")
            )
        }
    )
    private Modelo modelo;
    @AttributeOverrides(
        {
            @AttributeOverride(
                name = "codigo",
                column = @Column(name = "ano_codigo")
            ),
            @AttributeOverride(
                name = "nome",
                column = @Column(name = "ano_nome")
            )
        }
    )
    private Ano ano;
}

@Repository
interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("select u.id,u.nome from Usuario u")
    List<Object[]> listarUsuarios();
}

@Repository
interface CarroRepository extends JpaRepository<Carro, Long> {
}

@RestController
@Slf4j
class ModelosController {
    final ParallelumService parallelumService;

    ModelosController(final ParallelumService parallelumService) {
        this.parallelumService = parallelumService;
    }

    @GetMapping("/marcas")
    List<Marca> getMarcas() {
        return parallelumService.marcas();
    }

    @GetMapping("/marcas/{id}/modelos")
    ListaModelos getListaDeModelos(@PathVariable String id) {
        final var modelos = this.parallelumService.modelosPorCodigoMarca(id);
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

@Embeddable
@Data
@ToString
class Marca {
    private String codigo;
    private String nome;
}

@Embeddable
@Data
@ToString
class Modelo {
    private String nome;
    private String codigo;
}

@Embeddable
@Data
@ToString
class Ano {
    private String nome;
    private String codigo;
}