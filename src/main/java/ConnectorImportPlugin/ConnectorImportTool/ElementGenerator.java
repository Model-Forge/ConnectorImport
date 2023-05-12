package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.jmi.helpers.TagsHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Signal;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.StructuredClassifier;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.EncapsulatedClassifier;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

public class ElementGenerator {

    private Project project;
    private com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package modelRoot;
    private List projectModels;
    private ElementsFactory factory;
    private ModelElementsManager manager;
    private Profile SysMLprofile;
    private Finder.ByNameRecursivelyFinder finderByName;
    private Finder.ByScopeFinder finderByScope;
    private com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package profilesPkg;
    private Stereotype ppStereo;
    private Stereotype ifStereo;
    private Stereotype proxyStereo;
    private Stereotype nceStereo;
    private Collection rootPkgContents;
    private PrintWriter debugOutput;
    private Stereotype itemFlowStereo;

    //public ElementGenerator(ArrayList<NamedElement> contextList, PrintWriter debugOutput)
    public ElementGenerator(HashSet<NamedElement> contextList, PrintWriter debugOutput)
    {

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Executing ElementGenerator constructor...", false);

        project = Application.getInstance().getProject();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator project set.", false);

        modelRoot = project.getPrimaryModel();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator modelRoot set.", false);

        projectModels = project.getModels();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator projectModels set.", false);

        factory = project.getElementsFactory();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator factory set.", false);

        manager = ModelElementsManager.getInstance();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator manager set.", false);

        SysMLprofile = StereotypesHelper.getProfile(project, "SysML");

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator SysMLprofile set.", false);

        finderByName = Finder.byNameRecursively();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator finderByName set.", false);

        finderByScope = Finder.byScope();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator finderByScope set.", false);

        profilesPkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) finderByName.find(projectModels, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class, "MD Customization for SysML");

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator profilesPkg set.", false);

        ppStereo = finderByName.find(profilesPkg, Stereotype.class, "PartProperty");

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator ppStereo set.", false);

        ifStereo = StereotypesHelper.getStereotype(project, "InterfaceBlock", SysMLprofile);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator ifStereo set.", false);

        itemFlowStereo = StereotypesHelper.getStereotype(project, "ItemFlow", SysMLprofile);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator itemFlowStereo set.", false);

        proxyStereo = StereotypesHelper.getStereotype(project, "ProxyPort", SysMLprofile);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator proxyStereo set.", false);

        nceStereo = StereotypesHelper.getStereotype(project, "NestedConnectorEnd", SysMLprofile);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator nceStereo set.", false);


        // Updated
        rootPkgContents = finderByScope.find(contextList);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator rootPkgContents set.", false);

        this.debugOutput = debugOutput;

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ElementGenerator debugOutput set.", false);


        // Debug
        //Application.getInstance().getGUILog().writeLogText("Completed ElementGenerator constructor.", false);
    }

    public ArrayList chainToRootBlock(Property partA, Type rootBlock)
    {
        boolean stop = false;
        ArrayList listA = new ArrayList();

        listA.add(partA);

        StringBuilder debugList = new StringBuilder();

        // If partA is owned by the rootBlock, short circuit the rest of the function and simply return
        if(partA.getOwner().equals(rootBlock))
        {
            stop = true;
            return listA;
        }

        var partAowner = (Type) partA.getOwner();
        var partA1 = partAowner.get_typedElementOfType();

        var partA1Itr = partA1.iterator();

        // While another property exists in the list AND the rootblock has not yet been reached
        while(partA1Itr.hasNext() && !stop)
        {
            var nextPart = partA1Itr.next();

            // If the next property in the list has the PartProperty stereotype applied AND the rootblock has not yet been reached
            if(StereotypesHelper.hasStereotype(nextPart, ppStereo) && !stop && rootPkgContents.contains(nextPart))
            {
                // Add the part property to the list
                listA.add(nextPart);

                debugList.append(((NamedElement) nextPart).getName() + "\n");

                // If the Part Property owner is the rootblock, set the stop variable to true
                if(nextPart.getOwner().equals(rootBlock))
                {
                    stop = true;
                    return listA;
                }

                // Debug
                //Application.getInstance().getGUILog().showMessage("Added " + nextPart.getName() + " to chain.");
            }
        }

        var partA2owner = (Type) ((TypedElement) listA.get(listA.size()-1)).getOwner();
        var partA2 = partA2owner.get_typedElementOfType();

        var partA2Itr = partA2.iterator();

        // While another property exists in the list AND the rootblock has not yet been reached
        while(partA2Itr.hasNext() && !stop)
        {
            var nextPart = partA2Itr.next();

            // If the next property in the list has the PartProperty stereotype applied AND the rootblock has not yet been reached
            if(StereotypesHelper.hasStereotype(nextPart, ppStereo) && !stop && rootPkgContents.contains(nextPart))
            {
                // Add the part property to the list
                listA.add(nextPart);

                debugList.append(((NamedElement) nextPart).getName() + "\n");

                // If the Part Property owner is the rootblock, set the stop variable to true
                if(nextPart.getOwner().equals(rootBlock))
                {
                    stop = true;
                    return listA;
                }

                // Debug
                //Application.getInstance().getGUILog().showMessage("Added " + nextPart.getName() + " to chain.");
            }
        }

        // Debug
        //JOptionPane.showMessageDialog(null, "After 2 hops, chain includes:\n" + debugList.toString());
        //Application.getInstance().getGUILog().showMessage("After 2 hops, chain includes:\n" + debugList.toString());

        var partA3owner = (Type) ((TypedElement) listA.get(listA.size()-1)).getOwner();
        var partA3 = partA3owner.get_typedElementOfType();

        var partA3Itr = partA3.iterator();


        // While another property exists in the list AND the rootblock has not yet been reached
        while(partA3Itr.hasNext() && !stop)
        {
            var nextPart = partA3Itr.next();

            // If the next property in the list has the PartProperty stereotype applied AND the rootblock has not yet been reached
            if(StereotypesHelper.hasStereotype(nextPart, ppStereo) && !stop && rootPkgContents.contains(nextPart))
            {
                // Add the part property to the list
                listA.add(nextPart);

                debugList.append(((NamedElement) nextPart).getName() + "\n");

                // If the Part Property owner is the rootblock, set the stop variable to true
                if(nextPart.getOwner().equals(rootBlock))
                {
                    stop = true;
                    return listA;
                }

                // Debug
                //Application.getInstance().getGUILog().showMessage("Added " + nextPart.getName() + " to chain.");
            }
        }

        var partA4owner = (Type) ((TypedElement) listA.get(listA.size()-1)).getOwner();
        var partA4 = partA4owner.get_typedElementOfType();

        var partA4Itr = partA4.iterator();


        // While another property exists in the list AND the rootblock has not yet been reached
        while(partA4Itr.hasNext() && !stop)
        {
            var nextPart = partA4Itr.next();

            // If the next property in the list has the PartProperty stereotype applied AND the rootblock has not yet been reached
            if(StereotypesHelper.hasStereotype(nextPart, ppStereo) && !stop && rootPkgContents.contains(nextPart))
            {
                // Add the part property to the list
                listA.add(nextPart);

                debugList.append(((NamedElement) nextPart).getName() + "\n");

                // If the Part Property owner is the rootblock, set the stop variable to true
                if(nextPart.getOwner().equals(rootBlock))
                {

                    stop = true;
                    return listA;
                }

                // Debug
                //Application.getInstance().getGUILog().showMessage("Added " + nextPart.getName() + " to chain.");
            }
        }

        var partA5owner = (Type) ((TypedElement) listA.get(listA.size()-1)).getOwner();
        var partA5 = partA5owner.get_typedElementOfType();

        var partA5Itr = partA5.iterator();


        // While another property exists in the list AND the rootblock has not yet been reached
        while(partA5Itr.hasNext() && !stop)
        {
            var nextPart = partA5Itr.next();

            // If the next property in the list has the PartProperty stereotype applied AND the rootblock has not yet been reached
            if(StereotypesHelper.hasStereotype(nextPart, ppStereo) && !stop && rootPkgContents.contains(nextPart))
            {
                // Add the part property to the list
                listA.add(nextPart);

                debugList.append(((NamedElement) nextPart).getName() + "\n");

                // If the Part Property owner is the rootblock, set the stop variable to true
                if(nextPart.getOwner().equals(rootBlock))
                {
                    stop = true;
                    return listA;
                }

                // Debug
                //Application.getInstance().getGUILog().showMessage("Added " + nextPart.getName() + " to chain.");
            }
        }

        return listA;
    }

    public Type findRootBlock(Property partA, Property partB)
    {
        Type rootBlock;

        if(partA.equals(null) || partB.equals(null))
        {
            return null;
        }

        // If partA and partB share an owner, return that and skip everything else
        if(partA.getOwner().equals(partB.getOwner()))
        {
            rootBlock = (Type) partA.getOwner();

            return rootBlock;
        }

        /************************/
        /* First part hierarchy */
        /************************/

        ArrayList listA = new ArrayList();

        listA.add(partA);

        var partA1 = ((Type) partA.getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partA1) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listA.add(nextPart);
            }

        }

        var partA2 = ((Type) ((TypedElement) listA.get(listA.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partA2) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listA.add(nextPart);
            }
        }

        var partA3 = ((Type) ((TypedElement) listA.get(listA.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partA3) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listA.add(nextPart);
            }
        }

        var partA4 = ((Type) ((TypedElement) listA.get(listA.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partA4) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listA.add(nextPart);
            }
        }

        var partA5 = ((Type) ((TypedElement) listA.get(listA.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partA5) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listA.add(nextPart);
            }
        }

        /************************/
        /* Second part hierarchy */
        /************************/

        ArrayList listB = new ArrayList();

        listB.add(partB);

        var partB1 = ((Type) partB.getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partB1) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listB.add(nextPart);
            }
        }

        var partB2 = ((Type) ((TypedElement) listB.get(listB.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partB2) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listB.add(nextPart);
            }
        }

        var partB3 = ((Type) ((TypedElement) listB.get(listB.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partB3) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listB.add(nextPart);
            }
        }

        var partB4 = ((Type) ((TypedElement) listB.get(listB.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partB4) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listB.add(nextPart);
            }
        }

        var partB5 = ((Type) ((TypedElement) listB.get(listB.size()-1)).getOwner()).get_typedElementOfType();

        for (TypedElement nextPart : partB5) {
            if(rootPkgContents.contains(nextPart) && StereotypesHelper.hasStereotype(nextPart, ppStereo))
            {
                listB.add(nextPart);
            }
        }

        ArrayList intersectList = new ArrayList(listB);

        intersectList.retainAll(listA);

        if(!intersectList.isEmpty())
        {
            rootBlock = ((TypedElement) intersectList.get(0)).getType();

        }else
        {

            if( ((TypedElement) listA.get(listA.size()-1)).getOwner().equals( ((TypedElement) listB.get(listB.size()-1)).getOwner()))
            {
                rootBlock = (Type) ((TypedElement) listA.get(listA.size()-1)).getOwner();
            }
            else
            {
                rootBlock = null;

                // Debug
                debugOutput.println("Part: " + partA.getName() + " and " + partB.getName() + " do not have a common root!");
            }
        }

        return rootBlock;
    }

    //private HashMap findConnectorParts(ArrayList<NamedElement> rootList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg, String endPartOwnerName, String endPartName, String endConnName, String endConnPNName, String endPinName) throws ReadOnlyElementException
    private HashMap findConnectorParts(HashSet<NamedElement> rootList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg, String endPartOwnerName, String endPartName, String endConnName, String endConnPNName, String endPinName) throws ReadOnlyElementException
    {
        // Initialize the variable driving whether or not the function needs to test if a connector element already exists between the pins
        //var testConnectorEnds = true;
        TypedElement endPart;
        Type endBlock;
        Port endConn;
        EncapsulatedClassifier endConnPN;
        Port endPin;
        Type rootBlock;
        HashMap returnedData = new HashMap();

        // If the endPartOwnerName is provided, use that to narrow the search for the part to just the parts owned by the endPartOwner
        if(endPartOwnerName.compareTo("") != 0)
        {
            // Debug
            debugOutput.println("Searching for endpart: " + endPartName + " in endPartOwnerName: " + endPartOwnerName);

            endBlock = (Type) finderByName.find(rootList, Class.class, endPartOwnerName, true);

            if(endBlock != null)
            {
                debugOutput.println("endBlock found and set to: " + endBlock.getName());
            }else
            {
                debugOutput.println("endBlock not found.  Set to null.  Exiting part search.");

                return returnedData;
            }

            var endBlockParts = ((StructuredClassifier) endBlock).getPart();

            if(endBlockParts.size() > 0)
            {
                endPart = finderByName.find(endBlockParts, Property.class, endPartName, true);

            }else
            {
                debugOutput.println("endBlock does not own any parts.  Exiting part search.");

                return returnedData;
            }

            if(endPart != null)
            {
                debugOutput.println("endPart found and set to: " + endPart.getName());

                returnedData.put("part",endPart);

            }else
            {
                // Debug
                debugOutput.println("endPart not found.  Set to null.  Exiting part / property search.");

                return returnedData;
            }

        }else
        {
            // Debug
            debugOutput.println("endPartOwnerName empty.  Performing global search for part: " + endPartName);

            endPart = (TypedElement) finderByName.find(rootList, Property.class, endPartName, true);

            if(endPart != null)
            {
                // Debug
                debugOutput.println("endPart set to: " + endPart.getName());

                returnedData.put("part",endPart);

                endBlock = endPart.getType();

                if(endBlock != null)
                {
                    // Debug
                    debugOutput.println("endBlock set to: " + endBlock.getName());
                }else
                {
                    // Debug
                    debugOutput.println("endBlock not found.  Set to null.  Cannot create ports.  Exiting property search.");

                    return returnedData;
                }

            }else
            {
                // Debug
                debugOutput.println("endPart not found.  Set to null.  Exiting part / property search.");

                return returnedData;
            }

        }

        var portList = new ArrayList();

        var endBlockOwnedPortsItr = ((EncapsulatedClassifier) endBlock).getOwnedPort().iterator();

        //var endBlockOwnedPortsItr = endBlockOwnedPorts.iterator();

        while(endBlockOwnedPortsItr.hasNext())
        {
            portList.add(endBlockOwnedPortsItr.next());
        }

        var endBlockInheritedPorts = ((Classifier) endBlock).getInheritedMember();

        for (NamedElement inherited : endBlockInheritedPorts)
        {
            if(inherited instanceof Port)
            {
                portList.add(inherited);
            }

        }

        // Debug
        debugOutput.println("Seaching for endConn: " + endConnName);

        endConn = (Port) finderByName.find(portList, Port.class, endConnName, true);

        // If the fromPort does not exist, create it
        if(endConn == null)
        {

            // Debug
            debugOutput.println("endConn " + endConnName + " not found, creating it...");

            // Create the proxy port to represent the connector
            endConn = factory.createPortInstance();
            StereotypesHelper.addStereotype(endConn, proxyStereo);
            endConn.setName(endConnName);
            endConn.setOwner(endBlock);

            // Debug
            debugOutput.println("endConn created:  " + endConn.getName());

            // Add the from Wire to the model
            manager.addElement(endConn, endBlock);

        }else
        {
            // Debug
            debugOutput.println("Found endConn " + endConn.getName());
        }

        returnedData.put("conn",endConn);

        // Get the type of the connector as currently set
        endConnPN = (EncapsulatedClassifier) endConn.getType();

        // If the connector is not typed or the type name doesn't match, create the new I/F Block or find existing one and set as type
        if((endConnPN == null) || (endConnPN.getName().compareTo(endConnPNName) != 0))
        {
            // Debug
            debugOutput.println("fromConnPN not correct, creating: " + endConnPNName);

            // If an interface block package is supplied, search its contents for the interface block to use as the port type
            if(ifBlockPkg != null)
            {

                // Search the ifBlock Package for a classifier with the endConnPNName
                EncapsulatedClassifier fromIFBlock = (EncapsulatedClassifier) finderByName.find(ifBlockPkg, Class.class, endConnPNName, true);

                // Debug
                debugOutput.println("Searching for Encapsulated Classifier in package: " + ifBlockPkg.getName());

                // If a result is returned from the search and it has the interface block stereotype applied
                if(fromIFBlock != null && StereotypesHelper.hasStereotype(fromIFBlock, ifStereo))
                {
                    // Debug
                    debugOutput.println("Interface Block " + fromIFBlock.getName()  + " found in package: " + ifBlockPkg.getName());

                    endConnPN = fromIFBlock;

                    // Set the connector type to the endConnPN
                    endConn.setType(endConnPN);

                    // Debug
                    debugOutput.println("Interface Block: " + endConnPN.getName() + " set as type for port " + endConn.getName());

                    // If no results is returned in the search in the ifBlock Package or the result doesn't have the interface block
                    // stereotype applied then create the interface block instead
                }else
                {
                    // Debug
                    debugOutput.println("No appropriate Encapsulated Classifier found to type port: " + endConn.getName() + ".  Creating an Interface Block named: " + endConnPNName);

                    // Create the interface block and set as the type
                    endConnPN = factory.createClassInstance();
                    endConnPN.setName(endConnPNName);
                    endConnPN.setOwner(ifBlockPkg);

                    // Retrieve the InterfaceBlock stereotype and apply it to the newly created IF Block
                    StereotypesHelper.addStereotype(endConnPN, ifStereo);

                    // Debug
                    debugOutput.println("Created ifBlock: " + endConnPN.getName());

                    // Set the I/F block as the port type
                    endConn.setType(endConnPN);

                    // Debug
                    debugOutput.println("Interface Block: " + endConnPN.getName() + " set as type for port " + endConn.getName());

                    // Add the from Wire I/F Block to the model
                    manager.addElement(endConnPN, ifBlockPkg);


                }

                // If no interface block package is provided then
            }else
            {
                // Debug
                debugOutput.println("No appropriate Encapsulated Classifier found to type port: " + endConn.getName() + ".  Creating an Interface Block named: " + endConnPNName);

                // Create the interface block and set as the type
                endConnPN = factory.createClassInstance();
                endConnPN.setName(endConnPNName);
                endConnPN.setOwner(endBlock);

                // Retrieve the InterfaceBlock stereotype and apply it to the newly created IF Block
                StereotypesHelper.addStereotype(endConnPN, ifStereo);

                // Debug
                debugOutput.println("Created ifBlock: " + endConnPN.getName());

                // Set the I/F block as the port type
                endConn.setType(endConnPN);

                // Add the from Wire I/F Block to the model
                manager.addElement(endConnPN, endBlock);

                // Debug
                debugOutput.println("Interface Block: " + endConnPN.getName() + " set as type for port " + endConn.getName());

            }
        }

        // Add the endConnPN to the map with the 'type' key
        returnedData.put("type",endConnPN);

        if(endPinName.compareTo("") != 0)
        {

            // Debug
            debugOutput.println("Searching for pin: " + endPinName);

            // Search for a port owned by the fromConnPN whose name matches fromPinName
            endPin = (Port) finderByName.find(endConnPN.getOwnedPort(), Port.class, endPinName, true);

            // If the I/F block doesn't have a proxy port whose name matches the pin name, then create it
            if(endPin == null)
            {
                // Debug
                debugOutput.println("endPin not found.  Creating pin");

                // Add the required pin to the IF block and apply the ProxyPort stereotype
                endPin = factory.createPortInstance();
                StereotypesHelper.addStereotype(endPin, proxyStereo);
                endPin.setName(endPinName);
                endPin.setOwner(endConnPN);

                debugOutput.println("Pin " + endPin.getName() + " created.");

                // Add the from Pin to the model
                manager.addElement(endPin, endConnPN);

            }

            returnedData.put("pin",endPin);
        }

        return returnedData;

    }

    //public Connector generateConn(ArrayList<NamedElement> rootList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg, String fromPartOwnerName, String fromPartName, String fromConnName, String fromConnPNName, String fromPinName, String toPartOwnerName, String toPartName, String toConnName, String toConnPNName, String toPinName) throws Exception, ReadOnlyElementException, NullPointerException
    public Connector generateConn(HashSet<NamedElement> rootList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg, String fromPartOwnerName, String fromPartName, String fromConnName, String fromConnPNName, String fromPinName, String toPartOwnerName, String toPartName, String toConnName, String toConnPNName, String toPinName) throws Exception, ReadOnlyElementException, NullPointerException
    {
        // Initialize the variable driving whether or not the function needs to test if a connector element already exists between the pins
        TypedElement fromPart;
        TypedElement toPart;
        Type fromBlock;
        Type toBlock;
        Port fromConn;
        Port toConn;
        EncapsulatedClassifier fromConnPN;
        EncapsulatedClassifier toConnPN;
        Port fromPin;
        Port toPin;
        Connector conn = null;
        Type rootBlock;

        // If either of the part names, connector names, connector part number names, or pin names are blank, throw a NullPointerException
        if( (fromPartName.compareTo("") == 0) || (toPartName.compareTo("") == 0) )
        {
            throw(new Exception("Part Name Not Supplied"));

        }else if( (fromConnName.compareTo("") == 0) || (toConnName.compareTo("") == 0) )
        {
            throw(new Exception("Connector Name Not Supplied"));

        }else if( (fromConnPNName.compareTo("") == 0) || (toConnPNName.compareTo("") == 0) )
        {
            throw(new Exception("Connector Type Name Not Supplied"));

        }else if( (fromPinName.compareTo("") == 0) || (toPinName.compareTo("") == 0) )
        {
            throw(new Exception("Pin Name Not Supplied"));
        }

        HashMap fromProperties = findConnectorParts(rootList, ifBlockPkg, fromPartOwnerName, fromPartName, fromConnName, fromConnPNName, fromPinName);

        fromPart = (TypedElement) fromProperties.get("part");
        fromConn = (Port) fromProperties.get("conn");
        fromConnPN = (EncapsulatedClassifier) fromProperties.get("type");
        fromPin = (Port) fromProperties.get("pin");

        if(fromPart == null)
        {
            throw(new Exception("From Part not found"));

        }else if(fromConn == null)
        {
            throw(new Exception("From Port not found"));

        }else if(fromConnPN == null)
        {
            throw(new Exception("From Port Type not found"));

        }else if(fromPin == null)
        {
            throw(new Exception("From Nested Port not found"));

        }

        // Debug
        debugOutput.println("Found connector FROM PARTS: " + fromPart.getName()
                + " " + fromConn.getName()
                + " " + fromConnPN.getName()
                + " " + fromPin.getName());

        HashMap toProperties = findConnectorParts(rootList, ifBlockPkg, toPartOwnerName, toPartName, toConnName, toConnPNName, toPinName);

        toPart = (TypedElement) toProperties.get("part");
        toConn = (Port) toProperties.get("conn");
        toConnPN = (EncapsulatedClassifier) toProperties.get("type");
        toPin = (Port) toProperties.get("pin");

        if(toPart == null)
        {
            throw(new Exception("To Part not found"));

        }else if(toConn == null)
        {
            throw(new Exception("To Port not found"));

        }else if(toConnPN == null)
        {
            throw(new Exception("To Port Type not found"));

        }else if(toPin == null)
        {
            throw(new Exception("To Nested Port not found"));

        }

        // Debug
        debugOutput.println("Found connector TO PARTS: "
                + " " + toPart.getName()
                + " " + toConn.getName()
                + " " + toConnPN.getName()
                + " " + toPin.getName());

        /******************************/
        /* Wire creation section */
        /******************************/

        // Find the root block to own the connector
        rootBlock = findRootBlock((Property) fromPart, (Property) toPart);

        if(rootBlock != null)
        {
            // Debug
            debugOutput.println("rootBlock found: " + rootBlock.getName());
        }else
        {
            // Debug
            debugOutput.println("parts " + fromPart.getName() + " and " + toPart.getName() + " do not have a common parent block in the selected scope.  Cannot create connector.");

            throw(new Exception("No common root block for parts found in scope."));
        }

        // Create the chain of parts going from one connector end up to but not including the root part
        ArrayList chainA = new ArrayList();
        chainA.add(chainToRootBlock((Property) fromPart, rootBlock));
        chainA = (ArrayList) chainA.get(0);

        ArrayList partChainA = new ArrayList(chainA.size());

        // Debug
        debugOutput.println("Creating chain of from end parts to rootblock to be used in ConnectorEnd propertyPath.");

        // Reverse the order of chainA so that the top level part property is first and the lowest is last
        for(int i = 0; i < chainA.size(); i++)
        {
            partChainA.add(chainA.get(chainA.size()-1-i));

            // Debug
            debugOutput.println("partChainA: " + ((Property) chainA.get(chainA.size()-1-i)).getName());
        }

        ArrayList chainB = new ArrayList();
        chainB.add(chainToRootBlock((Property) toPart, rootBlock));
        chainB = (ArrayList) chainB.get(0);

        ArrayList partChainB = new ArrayList(chainB.size());

        // Reverse the order of chainB so that the top level part property is first and the lowest is last
        for(int i = 0; i < chainB.size(); i++)
        {
            partChainB.add(chainB.get(chainB.size()-1-i));

            // Debug
            debugOutput.println("partChainB: " + ((NamedElement) chainB.get(chainB.size()-1-i)).getName());
        }

        // In the case where the connector goes from a part to itself, remove the first element of the part chains of each
        if(fromPart.equals(toPart))
        {
            // Debug
            debugOutput.println("fromPart and toPart equal.  Removing " + ((NamedElement) partChainA.get(0)).getName() + " from part chains");

            partChainA.remove(0);
            partChainB.remove(0);

        }

        // Debug
        debugOutput.println("Checking for existing connectors...");

        for (ConnectorEnd testEnd : fromConn.get_connectorEndOfPartWithPort()) {

            // Debug
            debugOutput.println("Checking ConnectorEnd: " + testEnd.getRole().getName());

            var testEndPPsize = testEnd.getTaggedValue().get(0).getValue().size();

            // Debug
            debugOutput.println("Checking ConnectorEnd propertyPath Size: " + testEndPPsize);

            Property testEndFromPart;

            if(testEndPPsize < 2)
            {
                testEndFromPart = (Property) testEnd.getTaggedValue().get(0).getValue().get(testEndPPsize-1);
            }else
            {
                testEndFromPart = (Property) testEnd.getTaggedValue().get(0).getValue().get(testEndPPsize-2);
            }

            // Debug
            debugOutput.println("Checking ConnectorEnd fromPart: " + ((NamedElement) testEndFromPart).getName());


            if(testEnd.getRole().equals(fromPin) && testEndFromPart.equals(fromPart))
            {
                // Debug
                debugOutput.println("Found matching connector end: fromPin " + fromPin.getName() + " matched with end " + testEnd.getRole().getName());

                var testConn = testEnd.get_connectorOfEnd();

                // Debug
                debugOutput.println("Retrieved connector");

                if(testConn.getEnd().get(0).getRole().equals(toPin))
                {
                    // Debug
                    debugOutput.println("Connector end(0) role matches: " + toPin.getName());

                    var testConnToEnd = testConn.getEnd().get(0);

                    // Debug
                    debugOutput.println("Retrieved connector end(0)");

                    var testConnToEndPPsize = testConnToEnd.getTaggedValue().get(0).getValue().size();

                    // Debug
                    debugOutput.println("Checking toEnd propertyPath Size: " + testConnToEndPPsize);

                    Property testConnToEndToPart;

                    if(testConnToEndPPsize < 2)
                    {
                        testConnToEndToPart = (Property) testConnToEnd.getTaggedValue().get(0).getValue().get(testConnToEndPPsize-1);
                    }else
                    {
                        testConnToEndToPart = (Property) testConnToEnd.getTaggedValue().get(0).getValue().get(testConnToEndPPsize-2);
                    }

                    // Debug
                    debugOutput.println("Checking ConnectorEnd toPart: " + ((NamedElement) testConnToEndToPart).getName());

                    if(testConnToEndToPart.equals(toPart))
                    {
                        conn = testConn;

                        // Debug
                        debugOutput.println("Found existing connector from "
                                + ((NamedElement) testEndFromPart).getName()
                                + " " + testEnd.getPartWithPort().getName()
                                + " " + testEnd.getRole().getName()
                                + " to "
                                + ((NamedElement) testConnToEndToPart).getName()
                                + " " + testConnToEnd.getPartWithPort().getName()
                                + " " + testConnToEnd.getRole().getName());

                        return conn;
                    }

                }else if(testConn.getEnd().get(1).getRole().equals(toPin))
                {
                    // Debug
                    debugOutput.println("Connector end(1) role matches: " + toPin.getName());

                    var testConnToEnd = testConn.getEnd().get(1);

                    // Debug
                    debugOutput.println("Retrieved connector end(1)");

                    var testConnToEndPPsize = testConnToEnd.getTaggedValue().get(0).getValue().size();

                    // Debug
                    debugOutput.println("Checking toEnd propertyPath Size: " + testConnToEndPPsize);

                    Property testConnToEndToPart;

                    if(testConnToEndPPsize < 2)
                    {
                        testConnToEndToPart = (Property) testConnToEnd.getTaggedValue().get(0).getValue().get(testConnToEndPPsize-1);
                    }else
                    {
                        testConnToEndToPart = (Property) testConnToEnd.getTaggedValue().get(0).getValue().get(testConnToEndPPsize-2);
                    }


                    // Debug
                    debugOutput.println("Checking ConnectorEnd toPart: " + ((NamedElement) testConnToEndToPart).getName());

                    if(testConnToEndToPart.equals(toPart))
                    {
                        conn = testConn;

                        // Debug
                        debugOutput.println("Found existing connector from "
                                + ((NamedElement) testEndFromPart).getName()
                                + " " + testEnd.getPartWithPort().getName()
                                + " " + testEnd.getRole().getName()
                                + " to "
                                + ((NamedElement) testConnToEndToPart).getName()
                                + " " + testConnToEnd.getPartWithPort().getName()
                                + " " + testConnToEnd.getRole().getName());

                        return conn;
                    }
                }
            }
        }

        // Debug
        debugOutput.println("Creating connector between " + fromPart.getName() + " " + fromConn.getName() + " " + fromPin.getName() + " and " + toPart.getName() + " " + toConn.getName() + toPin.getName() );

        // Create the connector instance
        conn = factory.createConnectorInstance();
        var fromEnd = conn.getEnd().get(0);
        var toEnd = conn.getEnd().get(1);

        if(fromPart.equals(toPart))
        {
            // Debug
            debugOutput.println("fromPart: " + fromPart.getName() + " and toPart: " + toPart.getName() + " are equal.\nSetting connector owner to: " + fromPart.getType().getName());

            conn.setOwner(fromPart.getType());


        }else
        {
            // Debug
            debugOutput.println("fromPart: " + fromPart.getName() + " and toPart: " + toPart.getName() + " are NOT equal.\nSetting connector owner to: " + rootBlock.getName());

            conn.setOwner(rootBlock);
        }

        // Set the role for the "From" and "To" sides
        fromEnd.setRole(fromPin);

        // Debug
        debugOutput.println("Setting fromEnd Role: " + fromPin.getName());

        toEnd.setRole(toPin);

        // Debug
        debugOutput.println("Setting toEnd Role: " + toPin.getName());

        // Apply the "Nested Wire End" stereotype from the SysML profile to each of the connector ends
        //nceStereo = StereotypesHelper.getStereotype(project, "NestedConnectorEnd", SysMLprofile);
        StereotypesHelper.addStereotype(fromEnd, nceStereo);
        StereotypesHelper.addStereotype(toEnd, nceStereo);

        // Create an iterator to cycle through each of the partChainA contents
        Iterator chainAItr = partChainA.iterator();

        // Add the "propertyPath" tagged value to each of the connector ends and fill it with the part properties associated with the "From" and "To" sides
        if(chainAItr.hasNext())
        {
            var nextA = (NamedElement) chainAItr.next();
            TagsHelper.setStereotypePropertyValue(fromEnd, nceStereo, "propertyPath", nextA);

            // Debug
            debugOutput.println("Added to fromEnd propertyPath: " + nextA.getName());

        }

        // Add each of the rest of the parts in the chain up to the root block
        while(chainAItr.hasNext())
        {
            var nextA = (NamedElement) chainAItr.next();
            TagsHelper.setStereotypePropertyValue(fromEnd, nceStereo, "propertyPath", nextA, true);

            // Debug
            debugOutput.println("Added to fromEnd propertyPath: " + nextA.getName());

        }

        // Create an iterator to cycle through each of the partChainB contents
        Iterator chainBItr = partChainB.iterator();

        // Debug
        StringBuilder toDebugList = new StringBuilder();
        if(chainBItr.hasNext())
        {
            // Skip the first part in the chain since it has already been added
            var nextB = (NamedElement) chainBItr.next();
            TagsHelper.setStereotypePropertyValue(toEnd, nceStereo, "propertyPath", nextB);

            // Debug
            debugOutput.println("Added to toEnd propertyPath: " + nextB.getName());

        }

        // Add each of the rest of the parts in the chain up to the root block
        while(chainBItr.hasNext())
        {
            var nextB = (NamedElement) chainBItr.next();
            TagsHelper.setStereotypePropertyValue(toEnd, nceStereo, "propertyPath", nextB, true);

            // Debug
            debugOutput.println("Added to toEnd propertyPath: " + nextB.getName());

        }

        // Add the connectors to the propertyPath
        TagsHelper.setStereotypePropertyValue(fromEnd, nceStereo, "propertyPath", fromConn, true);

        // Debug
        debugOutput.println("Added to fromEnd propertyPath: " + fromConn.getName());

        TagsHelper.setStereotypePropertyValue(toEnd, nceStereo, "propertyPath", toConn, true);

        // Debug
        debugOutput.println("Added to toEnd propertyPath: " + toConn.getName());

        // Ensure the Part With Port property is set (for some reason it isn't always set by adding the propertyPath values)
        if(fromEnd.getPartWithPort() == null)
        {
            fromEnd.setPartWithPort(fromConn);

            // Debug
            debugOutput.println("Set fromEnd PartWithPort: " + fromConn.getName());
        }

        if(toEnd.getPartWithPort() == null)
        {
            toEnd.setPartWithPort(toConn);

            // Debug
            debugOutput.println("Set toEnd PartWithPort: " + toConn.getName());
        }

        try
        {
            // If the connector loops back to the same part (even if different port) then the part type must be the owning block
            if(fromPart.equals(toPart))
            {
                // Add the connector under the owning block
                manager.addElement(conn, fromPart.getType());

                // Debug
                debugOutput.println("Added connector under: " + fromPart.getType().getName());

            }else
            {
                // Add the connector under the owning block
                manager.addElement(conn, rootBlock);
                // Debug
                debugOutput.println("Added connector under: " + rootBlock.getName());

            }
        }catch(Exception exc)
        {

            // Alert user to issue
            debugOutput.println("Error: " + exc.getLocalizedMessage() +" while creating connector: " + fromPart.getName() + "." + fromConn.getName() + "." + fromPin.getName() + "." + toPart.getName() + "." + toConn.getName() + "." + toPin.getName() );

        }

        debugOutput.println("Returning connector : " + fromPart.getName() + "." + fromConn.getName() + "." + fromPin.getName() + "." + toPart.getName() + "." + toConn.getName() + "." + toPin.getName());

        return conn;

    }

    //public Connector generateInterconn(ArrayList<NamedElement> rootList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg, String fromPartOwnerName, String fromPartName, String fromConnName, String fromConnPNName, String toPartOwnerName, String toPartName, String toConnName, String toConnPNName) throws Exception,NullPointerException,ReadOnlyElementException
    public Connector generateInterconn(HashSet<NamedElement> rootList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg, String fromPartOwnerName, String fromPartName, String fromConnName, String fromConnPNName, String toPartOwnerName, String toPartName, String toConnName, String toConnPNName) throws Exception,NullPointerException,ReadOnlyElementException
    {
        // Initialize the variable driving whether or not the function needs to test if a connector element already exists between the pins
        TypedElement fromPart;
        TypedElement toPart;
        Type fromBlock;
        Type toBlock;
        Port fromConn;
        Port toConn;
        EncapsulatedClassifier fromConnPN;
        EncapsulatedClassifier toConnPN;
        Connector conn = null;
        Type rootBlock;

        if( (fromPartName.compareTo("") == 0) || (toPartName.compareTo("") == 0) )
        {
            throw(new Exception("Part Name Not Supplied"));

        }else if( (fromConnName.compareTo("") == 0) || (toConnName.compareTo("") == 0) )
        {
            throw(new Exception("Connector Name Not Supplied"));

        }else if( (fromConnPNName.compareTo("") == 0) || (toConnPNName.compareTo("") == 0) )
        {
            throw(new Exception("Connector Type Name Not Supplied"));

        }


        HashMap fromProperties = findConnectorParts(rootList, ifBlockPkg, fromPartOwnerName, fromPartName, fromConnName, fromConnPNName, "");

        fromPart = (TypedElement) fromProperties.get("part");
        fromConn = (Port) fromProperties.get("conn");
        fromConnPN = (EncapsulatedClassifier) fromProperties.get("type");

        if(fromPart == null)
        {
            throw(new Exception("From Part not found"));

        }else if(fromConn == null)
        {
            throw(new Exception("From Port not found"));

        }else if(fromConnPN == null)
        {
            throw(new Exception("From Port Type not found"));

        }

        // Debug
        debugOutput.println("Found interconnect FROM PARTS: " + fromPart.getName()
                + " " + fromConn.getName()
                + " " + fromConnPN.getName());

        HashMap toProperties = findConnectorParts(rootList, ifBlockPkg, toPartOwnerName, toPartName, toConnName, toConnPNName, "");

        toPart = (TypedElement) toProperties.get("part");
        toConn = (Port) toProperties.get("conn");
        toConnPN = (EncapsulatedClassifier) toProperties.get("type");

        if(toPart == null)
        {
            throw(new Exception("To Part not found"));

        }else if(toConn == null)
        {
            throw(new Exception("To Port not found"));

        }else if(toConnPN == null)
        {
            throw(new Exception("To Port Type not found"));

        }


        // Debug
        debugOutput.println("Found interconnect TO PARTS: " + toPart.getName()
                + " " + toConn.getName()
                + " " + toConnPN.getName());

        // Find the root block to own the connector
        rootBlock = findRootBlock((Property) fromPart, (Property) toPart);

        if(rootBlock != null)
        {
            // Debug
            debugOutput.println("rootBlock found: " + rootBlock.getName());

        }else
        {
            // Debug
            debugOutput.println("parts " + fromPart.getName() + " and " + toPart.getName() + " do not have a common parent block in the selected scope.  Cannot create connector.");

            throw(new Exception("No common root block for parts found in scope."));
        }

        // Create the chain of parts going from one connector end up to but not including the root part
        ArrayList chainA = new ArrayList();
        chainA.add(chainToRootBlock((Property) fromPart, rootBlock));
        chainA = (ArrayList) chainA.get(0);

        ArrayList partChainA = new ArrayList(chainA.size());

        // Debug
        debugOutput.println("Creating chain of from end parts to rootblock to be used in ConnectorEnd propertyPath.");

        // Reverse the order of chainA so that the top level part property is first and the lowest is last
        for(int i = 0; i < chainA.size(); i++)
        {
            partChainA.add(chainA.get(chainA.size()-1-i));

            // Debug
            debugOutput.println("Chain A part added: " + ((Property) partChainA.get(i)).getName());
        }

        // Debug
        debugOutput.println("partChainA size: " + partChainA.size());

        ArrayList chainB = new ArrayList();

        chainB.add(chainToRootBlock((Property) toPart, rootBlock));

        chainB = (ArrayList) chainB.get(0);

        ArrayList partChainB = new ArrayList(chainB.size());

        // Debug
        debugOutput.println("Creating chain of to end parts to rootblock to be used in ConnectorEnd propertyPath.");

        // Reverse the order of chainB so that the top level part property is first and the lowest is last
        for(int i = 0; i < chainB.size(); i++)
        {
            partChainB.add(chainB.get(chainB.size()-1-i));

            // Debug
            debugOutput.println("Chain B part added: " + ((Property) partChainB.get(i)).getName());
        }

        // Debug
        debugOutput.println("partChainB size: " + partChainB.size());

        // Debug
        debugOutput.println("Checking for existing connectors...");

        for(ConnectorEnd testEnd : ((Property) fromPart).get_connectorEndOfPartWithPort() )
        {
            // Debug
            debugOutput.println("Checking ConnectorEnd fromPart: "+ fromPart.getName() + " and role: " + testEnd.getRole().getName());

            //var testEndPPsize = testEnd.getTaggedValue().get(0).getValue().size();

            //var testEndFromPart = testEnd.getTaggedValue().get(0).getValue().get(testEndPPsize-1);

            // Debug
            debugOutput.println("Checking ConnectorEnd Role");

            if(testEnd.getRole().equals(fromConn))
            {
                // Debug
                debugOutput.println("testEnd role matches " + fromConn.getName() + " and testEndFromPart matches " + fromPart.getName());

                var testConn = testEnd.get_connectorOfEnd();

                ArrayList<ConnectorEnd> endList = new ArrayList<ConnectorEnd>();

                endList.addAll(testConn.getEnd());

                endList.remove(testEnd);

                ConnectorEnd toEnd = endList.get(0);

                if(toEnd.getRole().equals(toConn))
                {
                    // Debug
                    debugOutput.println("testConn toEnd Role matches: " + toConn.getName());

                    var testConnToEndPPsize = toEnd.getTaggedValue().get(0).getValue().size();

                    var testConnToEndToPart = toEnd.getTaggedValue().get(0).getValue().get(testConnToEndPPsize-1);

                    // Debug
                    debugOutput.println("testConn toEnd is: " + ((Property) testConnToEndToPart).getName());

                    if(testConnToEndToPart.equals(toPart))
                    {
                        conn = testConn;

                        // Debug
                        debugOutput.println("Connector exists between " + conn.getEnd().get(0).getPartWithPort().getName() + "." + conn.getEnd().get(0).getRole().getName() + " and " + conn.getEnd().get(1).getPartWithPort().getName() + "." + conn.getEnd().get(1).getRole().getName() + ". Returning connector.");

                        return conn;
                    }

                }
            }
        }

        // Debug
        debugOutput.println("Creating connector between " + fromConn.getName() + " and " + toConn.getName());

        // Create the connector instance
        conn = factory.createConnectorInstance();
        var fromEnd = conn.getEnd().get(0);
        var toEnd = conn.getEnd().get(1);

        conn.setOwner(rootBlock);

        // Debug
        debugOutput.println("Conn owner set to: " + rootBlock.getName());

        // Set the role for the "From" and "To" sides
        fromEnd.setRole(fromConn);

        // Debug
        debugOutput.println("FromRole set to: " + fromEnd.getRole().getName());

        toEnd.setRole(toConn);

        // Debug
        debugOutput.println("ToRole set to: " + toEnd.getRole().getName());

        // Apply the "Nested Wire End" stereotype from the SysML profile to each of the connector ends
        //nceStereo = StereotypesHelper.getStereotype(project, "NestedConnectorEnd", SysMLprofile);
        StereotypesHelper.addStereotype(fromEnd, nceStereo);
        StereotypesHelper.addStereotype(toEnd, nceStereo);

        // Create an iterator to cycle through each of the partChainA contents
        Iterator chainAItr = partChainA.iterator();

        // Add the "propertyPath" tagged value to each of the connector ends and fill it with the part properties associated with the "From" and "To" sides
        if(chainAItr.hasNext())
        {
            var nextA = (NamedElement) chainAItr.next();
            TagsHelper.setStereotypePropertyValue(fromEnd, nceStereo, "propertyPath", nextA);
            // Debug
            debugOutput.println("Added to fromEnd propertyPath: " + nextA.getName());
        }

        // Add each of the rest of the parts in the chain up to the root block
        while(chainAItr.hasNext())
        {
            var nextA = (NamedElement) chainAItr.next();
            TagsHelper.setStereotypePropertyValue(fromEnd, nceStereo, "propertyPath", nextA, true);
            // Debug
            debugOutput.println("Added to fromEnd propertyPath: " + nextA.getName());
        }

        // Create an iterator to cycle through each of the partChainB contents
        Iterator chainBItr = partChainB.iterator();

        // Skip the first part in the chain since it has already been added
        if(chainBItr.hasNext())
        {
            var nextB = (NamedElement) chainBItr.next();
            TagsHelper.setStereotypePropertyValue(toEnd, nceStereo, "propertyPath", nextB);

            // Debug
            debugOutput.println("Added to toEnd propertyPath: " + nextB.getName());
        }

        // Add each of the rest of the parts in the chain up to the root block
        while(chainBItr.hasNext())
        {
            var nextB = (NamedElement) chainBItr.next();
            TagsHelper.setStereotypePropertyValue(toEnd, nceStereo, "propertyPath", nextB, true);
            // Debug
            debugOutput.println("Added to toEnd propertyPath: " + nextB.getName());
        }

        // Ensure the Part With Port property is set (for some reason it isn't always set by adding the propertyPath values)
        if(fromEnd.getPartWithPort() == null)
        {
            fromEnd.setPartWithPort((Property) fromPart);

            // Debug
            debugOutput.println("FromEnd PartWithPort set to: " + fromEnd.getPartWithPort().getName());
        }

        if(toEnd.getPartWithPort() == null)
        {
            toEnd.setPartWithPort((Property) toPart);

            // Debug
            debugOutput.println("ToEnd PartWithPort set to: " + toEnd.getPartWithPort().getName());

        }

        try
        {
            // Debug
            debugOutput.println("Adding interconnect to model under: " + rootBlock.getName());

            manager.addElement(conn, rootBlock);

            // Debug
            debugOutput.println("Interconnect added to model!");


            return conn;

        }catch(Exception exc)
        {
            // Alert user to issue
            debugOutput.println("Error " + exc.getLocalizedMessage() +" while creating interconnect: " + fromPart.getName() + " " + fromConn.getName() + " " + toPart.getName() + " " + toConn.getName());

        }

        return conn;

    }

    public Generalization generateGeneralization(Signal specificSig, Signal generalSig, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package sigPkg)
    {
        Generalization sigGeneralization = null;

        if(specificSig.getOwnedElement().size() > 1)
        {

            for (Element ownedEl : specificSig.getOwnedElement())
            {
                if(ownedEl instanceof Generalization)
                {

                    var genSig = ((Generalization) ownedEl).getGeneral();

                    // Debug
                    debugOutput.println("Signal " + specificSig.getName() + " is a type of " + genSig.getName() + " signal.");

                    if(genSig.getName().compareTo(generalSig.getName()) == 0)
                    {
                        // Debug
                        debugOutput.println("The signal " + specificSig.getName() + " already has a generalization relationship with " + generalSig.getName());

                        sigGeneralization = (Generalization) ownedEl;

                        return sigGeneralization;
                    }
                }
            }

        }else
        {
            // Debug
            debugOutput.println("Creating a generalization between " + specificSig.getName() + " and " + generalSig.getName());

            sigGeneralization = factory.createGeneralizationInstance();

            sigGeneralization.setSpecific(specificSig);

            // Debug
            debugOutput.println("Generalization specific set to: " + sigGeneralization.getSpecific().getName());

            sigGeneralization.setGeneral(generalSig);

            // Debug
            debugOutput.println("Generalization general set to: " + sigGeneralization.getGeneral().getName());

            try
            {
                // Add the Generalization to the model nested under the specific signal
                manager.addElement(sigGeneralization, specificSig);

                // Debug
                debugOutput.println("Generalization between " + specificSig.getName() + " and " + generalSig.getName() + " added to model");

            }catch(ReadOnlyElementException roe)
            {
                // Alert user that the generalization was not created correctly
                debugOutput.println("Failed to create Generalization between signals " + specificSig.getName() + " and " + generalSig.getName());

                return null;
            }

        }

        return sigGeneralization;

    }

    public Signal generateSignal(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package sigPkg, String sigName) throws ReadOnlyElementException
    {

        Signal signal;

        // If a signal with the desired name can be found within the designated package
        if(finderByName.find(rootPkgContents, Signal.class, sigName, true) != null)
        {
            // set the signal variable to the found signal
            signal = (Signal) finderByName.find(rootPkgContents, Signal.class, sigName, true);

            // Debug
            debugOutput.println("Found existing signal: " + signal.getName());

        }else // Create the signal
        {

            // Create the signal, give it a name, and store it under the Signal Package
            signal = factory.createSignalInstance();
            signal.setName(sigName);
            signal.setOwner(sigPkg);

            // Add the element to the project
            manager.addElement(signal, sigPkg);

            // Debug
            debugOutput.println("Created signal: " + signal.getName());
        }

        return signal;
    }

    public com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package generatePkg(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package rootPkg, String pkgName)
    {
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkg;

        // If a package with the desired name is found within the root package
        if(finderByName.find(rootPkg, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class, pkgName, true) != null)
        {
            // set the pkg variable to the found Package
            pkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) finderByName.find(rootPkg, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class, pkgName, true);

            debugOutput.println("Found existing package named: " + pkg.getName());

        }else // Create the package
        {
            // Create the signal, give it a name, and store it under the Signal Package
            pkg = factory.createPackageInstance();
            pkg.setName(pkgName);
            pkg.setOwner(rootPkg);

            try
            {
                // Add the from Wire to the model
                manager.addElement(pkg, rootPkg);

                // Debug
                debugOutput.println("Created package: " + pkg.getName() + " under " + rootPkg.getName());

            }catch(Exception exc)
            {
                // Alert user to issue
                debugOutput.println("Error " + exc.getLocalizedMessage() + " encountered while creating package " + pkgName);

            }
        }

        return pkg;
    }

    public InformationFlow generateItemFlow(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package owningPkg, Signal signal, Connector realizingConn, String fromPortName) throws ReadOnlyElementException
    {
        // Identify the ports to be the source and target
        var connEnds = realizingConn.getEnd();
        Port fromPort;
        Port toPort;

        var flowList = realizingConn.get_informationFlowOfRealizingConnector();

        // Search through the exist
        for (InformationFlow flow : flowList)
        {
            var conveyedList = flow.getConveyed();


            for (Classifier conveyed : conveyedList)
            {
                // If the conveyed classifier is the same signal then the desired itemFlow already exists and return that
                if(conveyed.equals(signal) && ((NamedElement) flow.getSource().toArray()[0]).getName().compareTo(fromPortName) == 0)
                {
                    // Debug
                    debugOutput.println("Found matching itemFlow for " + signal.getName());

                    return flow;
                }
            }

        }

        // If the first returned connectorEnd's name matches the fromPortName then assign it as the fromPort
        if(connEnds.get(0).getRole().getName().compareTo(fromPortName) == 0)
        {
            fromPort = (Port) connEnds.get(0).getRole();
            toPort = (Port) connEnds.get(1).getRole();

            // Debug
            debugOutput.println("Flow Source found: " + fromPort.getName() + " and target found: " + toPort.getName());

            // Else assign the other end as the fromPort
        }else
        {
            fromPort = (Port) connEnds.get(1).getRole();
            toPort = (Port) connEnds.get(0).getRole();

            // Debug
            debugOutput.println("Flow Source found: " + fromPort.getName() + " and target found: " + toPort.getName());
        }

        String fromPath, toPath;

        // Create an information flow and apply the <<ItemFlow>> stereotype
        var itemFlow = factory.createInformationFlowInstance();

        StereotypesHelper.addStereotype(itemFlow, itemFlowStereo);

        // Set the owner of the itemFlow
        itemFlow.setOwner(owningPkg);

        // Get the sending part property name and if it has a nested port then get the owning port name
        var fromElPath = realizingConn.getEnd().get(0).getTaggedValue().get(0).getValue();
        if(fromElPath.size() < 2)
        {
            fromPath = ((NamedElement) fromElPath.get(fromElPath.size()-1)).getName();
        }else
        {
            fromPath = ((NamedElement) fromElPath.get(fromElPath.size()-2)).getName() + "." + ((NamedElement) fromElPath.get(fromElPath.size()-1)).getName();
        }

        // Debug
        debugOutput.println("Retrieved fromPath for use in human readable name: " + fromPath);

        // Get the receiving part property name and if it has a nested port then get the owning port name
        var toElPath = realizingConn.getEnd().get(1).getTaggedValue().get(0).getValue();
        if(toElPath.size() < 2)
        {
            toPath = ((NamedElement) toElPath.get(toElPath.size()-1)).getName();
        }else
        {
            toPath = ((NamedElement) toElPath.get(toElPath.size()-2)).getName() + "." + ((NamedElement) toElPath.get(toElPath.size()-1)).getName();
        }

        // Debug
        debugOutput.println("Retrieved toPath for use in human readable name: " + toPath);

        // Create a human understandable name for the itemFlow
        itemFlow.setName(fromPath + " to " + toPath + " (" + signal.getName() + ")");

        // Set the source and target ports for the itemFlow
        itemFlow.getInformationSource().add(fromPort);

        // Debug
        debugOutput.println("Set itemFlow source to " + fromPort.getName());

        itemFlow.getInformationTarget().add(toPort);

        // Debug
        debugOutput.println("Set itemFlow target to " + toPort.getName());

        // Set the conveyed classifier signal
        itemFlow.getConveyed().add(signal);

        // Debug
        debugOutput.println("Added signal " + signal.getName() + " as itemFlow conveyed classifier.");

        // Set the realizing connector
        itemFlow.getRealizingConnector().add(realizingConn);

        // Debug
        debugOutput.println("Set realizing connector for itemFlow");

        try
        {
            // Add the from Wire to the model
            manager.addElement(itemFlow, owningPkg);

            // Debug
            debugOutput.println("Created itemFlow: " + itemFlow.getName());

        }catch(Exception exc)
        {
            // Alert user to issue
            debugOutput.println("Error " + exc.getLocalizedMessage() + " while creating itemFlow " + itemFlow.getName());

        }

        return itemFlow;
    }


}
