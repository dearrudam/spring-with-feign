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

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
