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
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.List;
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
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Esta classe popula o banco de dados com dados de teste
 * se ele estiver vazio na inicialização.
 * * Ela é ativada para qualquer perfil, exceto 'test',
 * garantindo que não seja executada durante os testes automatizados.
 */
@Configuration
@Profile("!test") // Garante que este 'runner' não seja executado durante os testes
public class DataInitializer implements CommandLineRunner {

    // Logger para registrar as ações no console
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    // Injeção dos repositórios e do codificador de senha
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder passwordEncoder;

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
        
        // Verifica se o banco de dados já tem dados
        if (usuarioRepository.count() == 0 && categoriaRepository.count() == 0) {
            logger.info("Banco de dados vazio. Populando com dados de teste...");

            // --- 1. Criar Categorias ---
            logger.info("Criando categorias...");
            Categoria cat1 = new Categoria();
            cat1.setNomeCategoria("Eletrônicos");
            cat1.setTipo("Tecnologia");
            cat1.setFoto("https://i.imgur.com/gC5aG1g.png"); // Foto genérica

            Categoria cat2 = new Categoria();
            cat2.setNomeCategoria("Móveis");
            cat2.setTipo("Casa e Decoração");
            cat2.setFoto("https://i.imgur.com/bB4B0wN.png"); // Foto genérica

            Categoria cat3 = new Categoria();
            cat3.setNomeCategoria("Roupas e Acessórios");
            cat3.setTipo("Vestuário");
            cat3.setFoto("https://i.imgur.com/pYq7sWj.png"); // Foto genérica
            
            // Salva as categorias no banco antes de usá-las
            categoriaRepository.saveAll(List.of(cat1, cat2, cat3));

            // --- 2. Criar Usuários ---
            logger.info("Criando usuários...");
            Usuario user1 = new Usuario();
            user1.setNomeUsuario("Administrador");
            user1.setUsuario("admin@email.com"); // O campo 'usuario' é o email
            user1.setSenha(passwordEncoder.encode("admin123")); // Senha codificada
            user1.setFoto("https://i.imgur.com/I8MfmC8.png"); // Foto padrão

            Usuario user2 = new Usuario();
            user2.setNomeUsuario("Usuário Teste");
            user2.setUsuario("usuario@email.com");
            user2.setSenha(passwordEncoder.encode("user1234")); // Senha codificada
            user2.setFoto("https://i.imgur.com/I8MfmC8.png");

            // Salva os usuários no banco antes de usá-los
            usuarioRepository.saveAll(List.of(user1, user2));

            // --- 3. Criar Produtos ---
            logger.info("Criando produtos...");
            Produto p1 = new Produto();
            p1.setNomeProduto("Notebook Usado Dell i5");
            p1.setDescricao("Notebook em bom estado, 8GB RAM, 256GB SSD.");
            p1.setPreco(new BigDecimal("1800.00"));
            p1.setFoto("https://i.imgur.com/x5F1tYm.png"); // Foto genérica
            p1.setCategoria(cat1); // Associa à categoria Eletrônicos
            p1.setUsuario(user1);  // Associa ao admin

            Produto p2 = new Produto();
            p2.setNomeProduto("Sofá Retrátil 3 Lugares");
            p2.setDescricao("Sofá cor cinza, 2 anos de uso, em perfeito estado.");
            p2.setPreco(new BigDecimal("950.50"));
            p2.setFoto("https://i.imgur.com/xTf2tYq.png"); // Foto genérica
            p2.setCategoria(cat2); // Associa à categoria Móveis
            p2.setUsuario(user2);  // Associa ao usuário teste

            Produto p3 = new Produto();
            p3.setNomeProduto("Jaqueta de Couro");
            p3.setDescricao("Jaqueta de couro sintético, tamanho M, pouco usada.");
            p3.setPreco(new BigDecimal("120.00"));
            p3.setFoto("https://i.imgur.com/dK7fA8N.png"); // Foto genérica
            p3.setCategoria(cat3); // Associa à categoria Roupas
            p1.setUsuario(user1);  // Associa ao admin

            produtoRepository.saveAll(List.of(p1, p2, p3));

            logger.info("População inicial do banco de dados concluída.");
            
        } else {
            logger.info("O banco de dados já contém dados. Nenhuma ação de população foi realizada.");
        }
    }
}
