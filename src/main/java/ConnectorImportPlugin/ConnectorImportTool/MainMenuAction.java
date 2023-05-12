package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import ConnectorImportPlugin.ConnectorImportTool.gui.MainDialog;
import java.awt.event.ActionEvent;

public class MainMenuAction extends MDAction {

    private MainDialog mainDialog;

    public MainMenuAction(String id, String name, MainDialog dialog)
    {
        super(id, name, null, null);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("MainMenuAction super called.", false);

        mainDialog = dialog;

        // Debug
        //Application.getInstance().getGUILog().writeLogText("MainMenuAction mainDialog set.", false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        // Debug
        //Application.getInstance().getGUILog().writeLogText("Opening mainDialog...", false);

        mainDialog.setVisible(true);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("mainDialog set visible", false);

    }
}

