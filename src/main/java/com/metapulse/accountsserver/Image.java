package com.metapulse.accountsserver;

import jakarta.persistence.*;

/*The image entity, it has an id and the content wich is saved as bytes*/
@Entity
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] contenido;

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    // Getters y setters
}