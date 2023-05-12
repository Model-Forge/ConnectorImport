package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.ActionsID;

public class MainMenuConfiguration implements AMConfigurator {

    private final MainMenuAction mainMenuAction;

    public MainMenuConfiguration(MainMenuAction action) {
        this.mainMenuAction = action;
    }

    @Override
    public void configure(ActionsManager actionsManager) {
        var newProjectAction = actionsManager.getActionFor(ActionsID.CHECK_SPELL);


        if (newProjectAction != null) {
            var category = (ActionsCategory) actionsManager.getActionParent(newProjectAction);

            var actionsInCategory = category.getActions();
            actionsInCategory.add(mainMenuAction);
            category.setActions(actionsInCategory);
        }
    }
}
