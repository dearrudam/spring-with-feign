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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
