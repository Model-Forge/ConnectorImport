package ConnectorImportPlugin.ConnectorImportTool.gui;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import javax.swing.*;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.MainFrame;
//import com.nomagic.magicdraw.ui.browser.Node;

public class MainDialog extends JDialog {

    private final MainPanel panel;

    public MainDialog(java.awt.Frame parent) {

        super(parent, "Connector Import Tool", false);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Initializing Panel...", false);

        this.panel = new MainPanel(this);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Panel initialized...", false);

        this.setContentPane(panel);
        this.pack();

        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    }

    public void addToScope(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkg)
    {
        panel.addToScope(pkg);
    }

    public void removeFromScopoe(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkg)
    {
        panel.removeFromScope(pkg);
    }

}
