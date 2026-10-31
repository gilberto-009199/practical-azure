package com.gilberto009199.azure.caso_spring_h2_app_service.views;

import com.gilberto009199.azure.caso_spring_h2_app_service.domain.entity.Contato;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.SerializableConsumer;

public class ContatoForm extends FormLayout {

    private final Span titulo = new Span("Novo contato");
    private final TextField nome = new TextField("Nome");
    private final TextField telefone = new TextField("Telefone");
    private final EmailField email = new EmailField("Email");
    private final Binder<Contato> binder = new Binder<>(Contato.class);

    private Contato contato;

    public ContatoForm(SerializableConsumer<Contato> onSave, Runnable onCancel) {
        binder.forField(nome)
                .asRequired("Nome é obrigatório")
                .withValidator(new StringLengthValidator("Nome deve ter entre 3 e 120 caracteres", 3, 120))
                .bind(Contato::getNome, Contato::setNome);
        binder.forField(telefone)
                .asRequired("Telefone é obrigatório")
                .withValidator(v -> {
                    int digitos = v.replaceAll("\\D", "").length();
                    return digitos == 10 || digitos == 11;
                }, "Telefone deve ter 10 ou 11 dígitos, ex.: (11) 99999-9999")
                .bind(Contato::getTelefone, Contato::setTelefone);
        binder.forField(email)
                .asRequired("Email é obrigatório")
                .withValidator(new EmailValidator("Informe um email válido, ex.: nome@exemplo.com"))
                .withValidator(new StringLengthValidator("Email deve ter no máximo 160 caracteres", 1, 160))
                .bind(Contato::getEmail, Contato::setEmail);

        Button salvar = new Button("Salvar", e -> {
            if (contato == null) {
                return;
            }
            try {
                binder.writeBean(contato);
            } catch (ValidationException ex) {
                Notification.show("Corrija os campos destacados no formulário", 3000, Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            onSave.accept(contato);
        });
        salvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        salvar.setIcon(VaadinIcon.CHECK.create());
        Button cancelar = new Button("Cancelar", VaadinIcon.CLOSE_SMALL.create(), e -> onCancel.run());

        nome.setPlaceholder("Nome completo");
        nome.setPrefixComponent(VaadinIcon.USER.create());
        nome.setMaxLength(120);
        nome.setClearButtonVisible(true);
        telefone.setPlaceholder("(11) 99999-9999");
        telefone.setPrefixComponent(VaadinIcon.PHONE.create());
        telefone.setAllowedCharPattern("[0-9()\\-\\s+]");
        telefone.setMaxLength(20);
        telefone.setClearButtonVisible(true);
        email.setPlaceholder("nome@exemplo.com");
        email.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        email.setMaxLength(160);
        email.setClearButtonVisible(true);
        titulo.addClassName("form-subtitle");

        HorizontalLayout acoes = new HorizontalLayout(salvar, cancelar);
        acoes.setSpacing(true);

        setResponsiveSteps(new ResponsiveStep("0", 1));
        add(titulo, nome, telefone, email, acoes);
    }

    public void setContato(Contato contato) {
        this.contato = contato;
        titulo.setText(contato.getId() == null ? "Novo contato" : "Editar contato");
        binder.readBean(contato);
    }
}
