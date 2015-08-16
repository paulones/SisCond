/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ppcc.siscond.controller.listener;

import com.ppcc.siscond.util.Cookie;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 * @author paulones
 */
public class AccessListener implements PhaseListener {

    @Override
    public void afterPhase(PhaseEvent event) {

        // Obtém o contexto atual
        FacesContext context = event.getFacesContext();
        // Obtém a página que atualmente está interagindo com o ciclo
        // Se for a página houver o termo, seta a variável como true
        boolean isLoginPage = context.getViewRoot().getViewId().lastIndexOf("login") > -1;
        boolean isBlockPage = context.getViewRoot().getViewId().lastIndexOf("bloquear-tela") > -1;

        String cpf = Cookie.getCookie("usuario");
        String previousBlockedPage = Cookie.getCookie("pagina_anterior");
        if (cpf == null && previousBlockedPage != null) {
            Cookie.apagarCookie("pagina_anterior");
        }
        try {
            if (isLoginPage) {
                if (cpf != null) {
                    if (previousBlockedPage == null) {
                        Cookie.addCookie("usuario", cpf, 36000);
                        event.getFacesContext().getExternalContext().redirect("/home.xhtml");
                    } else {
                        event.getFacesContext().getExternalContext().redirect("/bloquear-tela.xhtml");
                    }
                }
            } else {
                if (cpf == null) {
                    event.getFacesContext().getExternalContext().redirect("/login.xhtml");
                } else {
                    if (!isBlockPage) {
                        if (previousBlockedPage != null) {
                            event.getFacesContext().getExternalContext().redirect("/bloquear-tela.xhtml");
                        } else {
                            Cookie.addCookie("usuario", cpf, 36000);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AccessListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void beforePhase(PhaseEvent pe) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

}
