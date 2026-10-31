package com.gilberto009199.azure.caso_spring_h2_app_service;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.component.dependency.StyleSheet;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class CasoSpringH2AppServiceApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(CasoSpringH2AppServiceApplication.class, args);
    }
}
