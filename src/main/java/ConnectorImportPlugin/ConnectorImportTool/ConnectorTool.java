package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.MainFrame;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import ConnectorImportPlugin.ConnectorImportTool.gui.MainDialog;

public class ConnectorTool extends Plugin
{
    private MainDialog mainDialog;

    @Override
    public void init()
    {
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        //MainFrame parent = Application.getInstance().getMainFrame();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Initializing MainDialog...", false);

        mainDialog = new MainDialog(parent);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("MainDialog initialized.", false);

        createMainMenuAction();
        createAddScopeAction();
        createRemoveScopeAction();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Created MainMenuAction...", false);

    }

    // Set up the button on the main menu
    private void createMainMenuAction()
    {
        var action = new MainMenuAction("ConnectorMainMenuAction", "Connector Tool - Import Dialog", mainDialog);
        var configurator = new MainMenuConfiguration(action);
        ActionsConfiguratorsManager.getInstance().addMainMenuConfigurator(configurator);
    }

    // Set up the button to add to the plugin element scope from the containment tree
    private void createAddScopeAction()
    {
        var action = new BrowserAddScopeAction("ConnectorAddScopeAction", "Connector Tool (Add Package To Scope)", mainDialog);
        var browserConfiguration = new BrowserAddScopeConfiguration(action);
        ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(browserConfiguration);

    }

    // Set up the button to add to the plugin element scope from the containment tree
    private void createRemoveScopeAction()
    {
        var action = new BrowserRemoveScopeAction("ConnectorRemoveScopeAction", "Connector Tool (Remove From Scope)", mainDialog);
        var browserConfiguration = new BrowserRemoveScopeConfiguration(action);
        ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(browserConfiguration);

    }

    @Override
    public boolean close()
    {
        return true;
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }
}


