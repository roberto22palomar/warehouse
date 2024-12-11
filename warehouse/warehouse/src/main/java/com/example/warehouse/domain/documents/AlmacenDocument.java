package com.example.warehouse.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(value = "almacenes")
public class AlmacenDocument {

    @Id
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("ubicacion")
    private String ubicacion;

    @Field("usuariosIds")
    private List<String> usuariosIds;

    @Field("articulosIds")
    private List<String> articulosIds;

}
