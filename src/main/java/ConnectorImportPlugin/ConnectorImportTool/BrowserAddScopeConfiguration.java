package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import com.nomagic.actions.ActionsManager;
//import com.nomagic.magicdraw.actions.MDActionsManager
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.ui.browser.Tree;
public class BrowserAddScopeConfiguration implements BrowserContextAMConfigurator
{

    private final BrowserAddScopeAction browserAction;

    public BrowserAddScopeConfiguration(BrowserAddScopeAction browserAction)
    {

        this.browserAction = browserAction;
    }

    @Override
    public void configure(ActionsManager actionsManager, Tree tree)
    {
        var category = new MDActionsCategory("", "");
        category.addAction(browserAction);
        actionsManager.addCategory(category);
    }

    @Override
    public int getPriority()
    {

        return LOW_PRIORITY;
    }
}

