
package com.gilberto009199.azure.caso_spring_h2_app_service.views;

import com.gilberto009199.azure.caso_spring_h2_app_service.domain.entity.Contato;
import com.gilberto009199.azure.caso_spring_h2_app_service.domain.exception.ContatoInvalidoException;
import com.gilberto009199.azure.caso_spring_h2_app_service.domain.service.ContatoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Contatos")
public class HomeView extends VerticalLayout {

    private final ContatoService service;
    private final Grid<Contato> grid = new Grid<>(Contato.class, false);
    private final Span contador = new Span();
    private final ContatoForm form;

    public HomeView(ContatoService service) {
        this.service = service;
        this.form = new ContatoForm(this::salvar, this::novo);

        grid.addComponentColumn(HomeView::nomeComAvatar).setHeader("Nome")
                .setComparator(Contato::getNome).setAutoWidth(true);
        grid.addComponentColumn(c -> celulaComIcone(VaadinIcon.PHONE, c.getTelefone()))
                .setHeader("Telefone").setAutoWidth(true);
        grid.addComponentColumn(c -> celulaComIcone(VaadinIcon.ENVELOPE, c.getEmail()))
                .setHeader("Email").setAutoWidth(true);
        grid.addComponentColumn(contato -> {
            Button editar = new Button(VaadinIcon.EDIT.create(), e -> form.setContato(contato));
            editar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editar.addClassName("acao-editar");
            editar.setTooltipText("Editar");
            Button excluir = new Button(VaadinIcon.TRASH.create(), e -> confirmarExclusao(contato));
            excluir.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            excluir.addClassName("acao-excluir");
            excluir.setTooltipText("Excluir");
            return new HorizontalLayout(editar, excluir);
        }).setHeader("Ações").setAutoWidth(true).setFlexGrow(0);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        Div cardForm = card("Cadastro", VaadinIcon.USER_CARD, "cadastro", form);
        cardForm.setWidth("360px");
        cardForm.getStyle().set("flex-shrink", "0");

        Div cardTabela = card("Listagem", VaadinIcon.LIST, "listagem", grid);
        cardTabela.getStyle().set("flex", "1").set("min-width", "0");

        HorizontalLayout conteudo = new HorizontalLayout(cardForm, cardTabela);
        conteudo.setWidthFull();
        conteudo.setSpacing(true);
        conteudo.setAlignItems(Alignment.START);

        setSizeFull();
        setPadding(true);
        add(cabecalho(), conteudo);
        novo();
        atualizarLista();
    }

    private Div cabecalho() {
        Span logo = new Span(VaadinIcon.USERS.create());
        logo.addClassName("logo");
        Span subtitulo = new Span("Gerencie seus contatos em um só lugar");
        subtitulo.addClassName("subtitle");
        Div titulos = new Div(new H1("Contatos"), subtitulo);
        contador.addClassName("badge");
        Div cabecalho = new Div(logo, titulos, contador);
        cabecalho.addClassName("app-header");
        return cabecalho;
    }

    private static Component nomeComAvatar(Contato c) {
        String nome = c.getNome() == null ? "" : c.getNome().trim();
        Span avatar = new Span(iniciais(nome));
        avatar.addClassName("avatar");
        avatar.getStyle().set("background", "hsl(" + Math.abs(nome.hashCode() % 360) + ",65%,45%)");
        Div celula = new Div(avatar, new Span(nome));
        celula.addClassName("cell-nome");
        return celula;
    }

    private static String iniciais(String nome) {
        String[] partes = nome.split("\\s+");
        String primeira = partes[0].isEmpty() ? "?" : partes[0].substring(0, 1);
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (primeira + ultima).toUpperCase();
    }

    private static Component celulaComIcone(VaadinIcon icone, String texto) {
        Div celula = new Div(icone.create(), new Span(texto));
        celula.addClassName("cell-icon");
        return celula;
    }

    private static Div card(String titulo, VaadinIcon icone, String estilo, Component conteudo) {
        Span icon = new Span(icone.create());
        icon.addClassName("icone");
        Div cabecalho = new Div(icon, new H3(titulo));
        cabecalho.addClassName("card-header");
        Div card = new Div(cabecalho, conteudo);
        card.addClassNames("card", estilo);
        return card;
    }

    private void novo() {
        grid.asSingleSelect().clear();
        form.setContato(new Contato());
    }

    private void salvar(Contato contato) {
        try {
            service.salvar(contato);
            novo();
            atualizarLista();
            notificar("Contato salvo com sucesso", NotificationVariant.LUMO_SUCCESS);
        } catch (ContatoInvalidoException e) {
            notificar(e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmarExclusao(Contato contato) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Excluir contato");
        dialog.setText("Deseja realmente excluir " + contato.getNome() + "? Esta ação não pode ser desfeita.");
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        dialog.setConfirmText("Excluir");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> excluir(contato));
        dialog.open();
    }

    private void excluir(Contato contato) {
        service.excluir(contato);
        novo();
        atualizarLista();
        notificar("Contato excluído", NotificationVariant.LUMO_WARNING);
    }

    private static void notificar(String mensagem, NotificationVariant variante) {
        Notification n = Notification.show(mensagem, 3000, Notification.Position.TOP_END);
        n.addThemeVariants(variante);
    }

    private void atualizarLista() {
        var contatos = service.listar();
        grid.setItems(contatos);
        contador.setText(contatos.size() + (contatos.size() == 1 ? " contato" : " contatos"));
    }
}

