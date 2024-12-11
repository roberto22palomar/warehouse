package com.example.warehouse.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(value = "articulos")
public class ArticuloDocument {

    @Id
    private String id;

    @Field("nombre")
    private String nombre;

    @Field("categoria")
    private String categoria;

    @Field("marca")
    private String marca;

    @Field("modelo")
    private String modelo;

    @Field("estado")
    private String estado;

    @Field("cantidad")
    private int cantidad;


}
