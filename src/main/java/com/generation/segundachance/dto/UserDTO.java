package com.generation.segundachance.dto;

import java.util.List;

public record UserDTO(String nomeUsuario, String usuario, String foto, List<ProductDTO> produtos) {

	public UserDTO(String nome, String email, String foto) {
        this(nome, email, foto, null);
    }

}
