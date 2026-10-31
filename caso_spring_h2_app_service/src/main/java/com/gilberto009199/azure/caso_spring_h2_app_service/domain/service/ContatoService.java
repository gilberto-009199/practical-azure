package com.gilberto009199.azure.caso_spring_h2_app_service.domain.service;

import com.gilberto009199.azure.caso_spring_h2_app_service.domain.entity.Contato;
import com.gilberto009199.azure.caso_spring_h2_app_service.domain.exception.ContatoInvalidoException;
import com.gilberto009199.azure.caso_spring_h2_app_service.domain.repository.ContatoRepository;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContatoService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final ContatoRepository repository;

    @Transactional(readOnly = true)
    public List<Contato> listar() {
        return repository.findAll();
    }

    @Transactional
    public Contato salvar(Contato contato) {
        validar(contato);
        contato.setNome(contato.getNome().trim());
        contato.setTelefone(contato.getTelefone().trim());
        contato.setEmail(contato.getEmail().trim());
        return repository.save(contato);
    }

    @Transactional
    public void excluir(Contato contato) {
        repository.deleteById(contato.getId());
    }

    private void validar(Contato contato) {
        String nome = contato.getNome() == null ? "" : contato.getNome().trim();
        if (nome.length() < 3 || nome.length() > 120) {
            throw new ContatoInvalidoException("Nome deve ter entre 3 e 120 caracteres");
        }
        String digitos = contato.getTelefone() == null ? "" : contato.getTelefone().replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            throw new ContatoInvalidoException("Telefone deve ter 10 ou 11 dígitos");
        }
        String email = contato.getEmail() == null ? "" : contato.getEmail().trim();
        if (email.length() > 160 || !EMAIL.matcher(email).matches()) {
            throw new ContatoInvalidoException("Email inválido");
        }
    }
}
