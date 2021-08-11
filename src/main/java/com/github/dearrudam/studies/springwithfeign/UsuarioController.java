package com.github.dearrudam.studies.springwithfeign;

/*-
 * #%L
 * spring-with-feign
 * %%
 * Copyright (C) 2021 ArrudaLabs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.stream.Stream;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
