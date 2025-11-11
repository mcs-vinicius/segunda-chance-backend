package com.generation.segundachance.configuration;

import com.generation.segundachance.model.Categoria;
import com.generation.segundachance.model.Produto;
import com.generation.segundachance.model.Usuario;
import com.generation.segundachance.repository.CategoriaRepository;
import com.generation.segundachance.repository.ProdutoRepository;
import com.generation.segundachance.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // Importante
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Profile("!test") // Garante que rode em 'prod', mas não em 'test'
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder passwordEncoder;

    // Injeção via construtor
    public DataInitializer(CategoriaRepository categoriaRepository,
                           UsuarioRepository usuarioRepository,
                           ProdutoRepository produtoRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // Lógica para verificar se o banco está vazio
        if (usuarioRepository.count() == 0 && categoriaRepository.count() == 0) {
            logger.info("Banco de dados vazio. Populando com dados de teste...");

            // (Aqui entra todo o código de criação de Categoria, Usuario e Produto)
            
            // --- 1. Criar Categorias ---
            logger.info("Criando categorias...");
            Categoria cat1 = new Categoria();
            cat1.setNomeCategoria("Eletrônicos");
            cat1.setTipo("Tecnologia");
            cat1.setFoto("https://i.imgur.com/gC5aG1g.png");

            Categoria cat2 = new Categoria();
            cat2.setNomeCategoria("Móveis");
            cat2.setTipo("Casa e Decoração");
            cat2.setFoto("https://i.imgur.com/bB4B0wN.png");
            
            categoriaRepository.saveAll(List.of(cat1, cat2));

            // --- 2. Criar Usuários ---
            logger.info("Criando usuários...");
            Usuario user1 = new Usuario();
            user1.setNomeUsuario("Administrador");
            user1.setUsuario("admin@email.com"); 
            user1.setSenha(passwordEncoder.encode("admin123")); 
            user1.setFoto("https://i.imgur.com/I8MfmC8.png");

            usuarioRepository.save(user1);

            // --- 3. Criar Produtos ---
            logger.info("Criando produtos...");
            Produto p1 = new Produto();
            p1.setNomeProduto("Notebook Usado Dell i5");
            p1.setDescricao("Notebook em bom estado, 8GB RAM, 256GB SSD.");
            p1.setPreco(new BigDecimal("1800.00"));
            p1.setFoto("https://i.imgur.com/x5F1tYm.png"); 
            p1.setCategoria(cat1); // Associa à categoria Eletrônicos
            p1.setUsuario(user1);  // Associa ao admin

            produtoRepository.save(p1);

            logger.info("População inicial do banco de dados concluída.");
            
        } else {
            logger.info("O banco de dados já contém dados. Nenhuma ação de população foi realizada.");
        }
    }
}
