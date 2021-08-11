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

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
