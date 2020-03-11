package com.search.risk.security;

import com.search.risk.view.MainView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.NotFoundException;

public class ConfigureUIServiceInitListener {

    private static void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isAccessGranted(event.getNavigationTarget())) { //
            if (SecurityUtils.isUserLoggedIn()) { //
                event.rerouteToError(NotFoundException.class); //
            } else {
                event.rerouteTo(MainView.class);
            }
        }

    }
}
