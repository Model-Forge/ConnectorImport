package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.awt.event.ActionEvent;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.ui.browser.Node;
import ConnectorImportPlugin.ConnectorImportTool.gui.MainDialog;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

public class BrowserRemoveScopeAction extends DefaultBrowserAction {

    private MainDialog mainDialog;

    public BrowserRemoveScopeAction(String id, String name, MainDialog dialog)
    {
        super(id, name, null, null);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("BrowserRemoveScopeAction super called.", false);

        mainDialog = dialog;

        // Debug
        //Application.getInstance().getGUILog().writeLogText("BrowserRemoveScopeAction mainDialog set.", false);
    }



    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        // Debug
        //Application.getInstance().getGUILog().writeLogText("BrowserRemoveScopeAction Getting tree.", false);

        var tree = getTree();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("BrowserRemoveScopeAction Tree retrieved.", false);

        var selectedNode = tree.getSelectedNode();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Selected node: " + ((BaseElement) selectedNode.getUserObject()).getHumanName(), false);

        if (selectedNode != null)
        {
            // Debug
            //Application.getInstance().getGUILog().writeLogText("Selected node: " + ((BaseElement) selectedNode.getUserObject()).getHumanName() + " is not null.", false);

            if (selectedNode.getUserObject() instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package)
            {
                // Debug
                //Application.getInstance().getGUILog().writeLogText("Selected node is a package.", false);

                var pkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) selectedNode.getUserObject();

                // Debug
                //Application.getInstance().getGUILog().writeLogText("Package is " + pkg.getName(), false);

                mainDialog.removeFromScopoe(pkg);

                // Debug
                //Application.getInstance().getGUILog().writeLogText("Removed " + pkg.getName() + " from MainDialog scope.", false);


            }else
            {
                var element = (BaseElement) selectedNode.getUserObject();

                Application.getInstance().getGUILog().showMessage("Selected element is of type: " + element.getHumanType() + ".  "
                        + "Only packages can be added or removed from tool scope.");
            }
        }

    }

}

