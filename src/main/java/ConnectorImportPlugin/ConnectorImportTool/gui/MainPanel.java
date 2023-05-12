/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ConnectorImportPlugin.ConnectorImportTool.gui;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Signal;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import ConnectorImportPlugin.ConnectorImportTool.ElementGenerator;
import ConnectorImportPlugin.ConnectorImportTool.InterconnectSet;
import ConnectorImportPlugin.ConnectorImportTool.Wire;
import ConnectorImportPlugin.ConnectorImportTool.Wire.Import_Status;
import ConnectorImportPlugin.ConnectorImportTool.XLSXExportFile;
import ConnectorImportPlugin.ConnectorImportTool.XLSXImportFile;

public class MainPanel extends javax.swing.JPanel {

    public MainPanel(MainDialog dialog)
    {
        // Debug
        //Application.getInstance().getGUILog().writeLogText("Initializing components...", false);

        initComponents();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Components initialized", false);

        callingDialog = dialog;

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Calling dialog set", false);

        contextList = new HashSet<NamedElement>();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ContextList initialized.", false);

        contextField.setText("...");

        // Debug
        //Application.getInstance().getGUILog().writeLogText("ContextField text set.", false);

        interconnModeSet = false;
        // Debug
        //Application.getInstance().getGUILog().writeLogText("InterconnMode set", false);

        finder = Finder.byNameRecursively();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Finder initialized.", false);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Initializing project...", false);

        project = Application.getInstance().getProject();

        if(project == null)
        {
            // Debug
            //Application.getInstance().getGUILog().writeLogText("Project is null.", false);
        }else
        {
            // Debug
            //Application.getInstance().getGUILog().writeLogText("Project set.", false);
        }

        String debugFileName = "\\ConnectorTool_Import_Debug_" + getDateTimeStamp() + "_";

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Setting up debugFile", false);

        try
        {
            debugTempFile = File.createTempFile(debugFileName, ".log");

            // Debug
            //Application.getInstance().getGUILog().writeLogText("debugFile temp file created", false);

            debugTempFile.deleteOnExit();

            debugOutput = new PrintWriter(debugTempFile);

            // Debug
            //Application.getInstance().getGUILog().writeLogText("Assigned printWriter", false);


        }catch(IOException io)
        {
            // Get user to choose a new directory to save the temporary debug file
            Application.getInstance().getGUILog().showError("System default temporary file storage directory is not accessible."
                    + "\nPlease choose a directory to save the debug file.");

        }


    }

    public void initializeProject()
    {

        project = Application.getInstance().getProject();

        // Updated
        gen = new ElementGenerator(contextList, debugOutput);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Element generator initialized.", false);
    }

    public void addToScope(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkg)
    {
        // Debug
        //Application.getInstance().getGUILog().writeLogText("Adding package " + pkg.getName() + " to scope.", false);

        contextList.add(pkg);

        String pkgName = pkg.getName();

        importPkgComboBox.addItem(pkgName);

        var contextItr = contextList.iterator();

        StringBuilder contextStr = new StringBuilder();

        while(contextItr.hasNext())
        {
            contextStr.append(contextItr.next().getName());

            if(contextItr.hasNext())
            {
                contextStr.append(", ");
            }
        }

        contextField.setText(contextStr.toString());

        rootPkg = (NamedElement) contextList.toArray()[0];
    }

    public void removeFromScope(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkg)
    {
        // Debug
        //Application.getInstance().getGUILog().writeLogText("Removing package " + pkg.getName(), false);

        boolean completed = contextList.remove(pkg);

        String pkgName = pkg.getName();

        importPkgComboBox.removeItem(pkgName);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Package removed from contextList.  Success:  " + completed, false);

        var contextItr = contextList.iterator();
        // Debug
        //Application.getInstance().getGUILog().writeLogText("Created iterator.", false);

        StringBuilder contextStr = new StringBuilder();

        while(contextItr.hasNext())
        {

            contextStr.append(contextItr.next().getName());

            if(contextItr.hasNext())
            {
                contextStr.append(", ");
            }
        }

        contextField.setText(contextStr.toString());

        rootPkg = (NamedElement) contextList.toArray()[0];

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlePanel = new javax.swing.JPanel();
        readMeButton = new javax.swing.JButton();
        contextLabel = new javax.swing.JLabel();
        selectWireFile = new javax.swing.JButton();
        selectSigFile = new javax.swing.JButton();
        contextField = new javax.swing.JTextField();
        wireFileSelected = new javax.swing.JTextField();
        sigFileSelected = new javax.swing.JTextField();
        rowsLabel = new javax.swing.JLabel();
        wireRowsField = new javax.swing.JTextField();
        sigRowsField = new javax.swing.JTextField();
        elemLbl = new javax.swing.JLabel();
        connField = new javax.swing.JTextField();
        genField = new javax.swing.JTextField();
        successLbl = new javax.swing.JLabel();
        connSucceededField = new javax.swing.JTextField();
        genSucceededField = new javax.swing.JTextField();
        connDebug = new javax.swing.JButton();
        scopeLbl = new javax.swing.JLabel();
        removeScope = new javax.swing.JButton();
        importPkgComboBox = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        ifBlockMode = new javax.swing.JComboBox<>();
        connectorLbl = new javax.swing.JLabel();
        generalizationLbl = new javax.swing.JLabel();
        connImportButton = new javax.swing.JButton();
        generalizationImportBtn = new javax.swing.JButton();
        interconnectButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        clearWiresButton = new javax.swing.JButton();
        clearSignals = new javax.swing.JButton();
        ifBlockModeLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        titlePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        readMeButton.setText("Read Me");
        readMeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readMeButtonActionPerformed(evt);
            }
        });

        contextLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        selectWireFile.setText("Connector File");
        selectWireFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectWireFileActionPerformed(evt);
            }
        });

        selectSigFile.setText("Signal File");
        selectSigFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectSigFileActionPerformed(evt);
            }
        });

        contextField.setEditable(false);

        wireFileSelected.setEditable(false);
        wireFileSelected.setText("...");

        sigFileSelected.setEditable(false);
        sigFileSelected.setText("...");

        rowsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rowsLabel.setText("Excel Rows");

        wireRowsField.setEditable(false);
        wireRowsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        wireRowsField.setText("0");

        sigRowsField.setEditable(false);
        sigRowsField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sigRowsField.setText("0");

        elemLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        elemLbl.setText("Rows Read");

        connField.setEditable(false);
        connField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        connField.setText("0");

        genField.setEditable(false);
        genField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        genField.setText("0");

        successLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        successLbl.setText("Imported");

        connSucceededField.setEditable(false);
        connSucceededField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        connSucceededField.setText("0");

        genSucceededField.setEditable(false);
        genSucceededField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        genSucceededField.setText("0");

        connDebug.setText("Open Debug Log");
        connDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connDebugActionPerformed(evt);
            }
        });

        scopeLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        scopeLbl.setText("Included Scope:");

        removeScope.setText("Remove Last");
        removeScope.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLastActionPerformed(evt);
            }
        });

        importPkgComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(Set As Root For Import)" }));

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
                titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(titlePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(selectWireFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(readMeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                        .addComponent(selectSigFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scopeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(titlePanelLayout.createSequentialGroup()
                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(wireFileSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(sigFileSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(connDebug, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(titlePanelLayout.createSequentialGroup()
                                                                .addComponent(sigRowsField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(genField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                                                                .addComponent(wireRowsField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(connField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                                                                .addComponent(rowsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(elemLbl)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(titlePanelLayout.createSequentialGroup()
                                                                .addComponent(successLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                        .addGroup(titlePanelLayout.createSequentialGroup()
                                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addComponent(genSucceededField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                                                        .addComponent(connSucceededField, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(14, 14, 14)
                                                                .addComponent(contextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(377, Short.MAX_VALUE))))
                                        .addGroup(titlePanelLayout.createSequentialGroup()
                                                .addComponent(contextField)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(removeScope)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(importPkgComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(107, 107, 107))))
        );
        titlePanelLayout.setVerticalGroup(
                titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(titlePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(readMeButton)
                                                .addComponent(connDebug))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(rowsLabel)
                                                .addComponent(elemLbl)
                                                .addComponent(successLbl)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(contextLabel)
                                        .addGroup(titlePanelLayout.createSequentialGroup()
                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(selectWireFile)
                                                        .addComponent(wireFileSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(wireRowsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(connField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(connSucceededField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(selectSigFile)
                                                        .addComponent(sigFileSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(sigRowsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(genField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(genSucceededField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(contextField)
                                                                .addComponent(removeScope)
                                                                .addComponent(importPkgComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(scopeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        ifBlockMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unique to Block", "Common Types" }));
        ifBlockMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ifBlockModeActionPerformed(evt);
            }
        });

        connectorLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        connectorLbl.setText("Connectors:");

        generalizationLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        generalizationLbl.setText("Generalizations:");

        connImportButton.setText("Import to Model");
        connImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connImportButtonActionPerformed(evt);
            }
        });

        generalizationImportBtn.setText("Import to Model");
        generalizationImportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generalizationImportBtnActionPerformed(evt);
            }
        });

        interconnectButton.setText("Convert to Interconnect");
        interconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interconnectButtonActionPerformed(evt);
            }
        });

        exportButton.setText("Save Import Table");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        clearWiresButton.setText("Clear Import Data");
        clearWiresButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearWiresButtonActionPerformed(evt);
            }
        });

        clearSignals.setText("Clear Import Data");
        clearSignals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSignalsActionPerformed(evt);
            }
        });

        ifBlockModeLbl.setText("Interface Block Mode:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(ifBlockModeLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(connectorLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(generalizationLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(generalizationImportBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(connImportButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ifBlockMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(5, 5, 5)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(clearSignals, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(clearWiresButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(interconnectButton)
                                .addContainerGap(351, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ifBlockMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ifBlockModeLbl))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(connectorLbl)
                                        .addComponent(connImportButton)
                                        .addComponent(interconnectButton)
                                        .addComponent(exportButton)
                                        .addComponent(clearWiresButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(generalizationLbl)
                                        .addComponent(generalizationImportBtn)
                                        .addComponent(clearSignals))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null, null, null, null, null, null}
                },
                new String [] {
                        "From Part Owner", "From Part", "From Port", "From Port Type", "From Nested Port", "To Part Owner", "To Part", "To Port", "To Port Type", "To Nested Port", "Signal", "Import Status"
                }
        ) {
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setShowGrid(true);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeLastActionPerformed(java.awt.event.ActionEvent evt)
    {
        var contextListItr = contextList.iterator();

        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkgToRemove = null;

        while(contextListItr.hasNext())
        {
            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nextPkg = (Package) contextListItr.next();

            if(!contextListItr.hasNext())
            {
                pkgToRemove = nextPkg;
            }
        }

        contextList.remove(pkgToRemove);

        importPkgComboBox.removeItem(pkgToRemove.getName());

        StringBuilder newContextString = new StringBuilder("");

        var newListItr = contextList.iterator();

        while(newListItr.hasNext())
        {
            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nextPkg = (Package) newListItr.next();

            newContextString.append(nextPkg.getName());

            if(newListItr.hasNext())
            {
                newContextString.append(", ");
            }
        }

        contextField.setText(newContextString.toString());
    }

    private void connDebugActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Get parent dialog for messages
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        try
        {

            if(debugTempFile.exists())
            {
                debugOutput.flush();

                if(Desktop.isDesktopSupported())
                {
                    Desktop.getDesktop().open(debugTempFile);
                }else
                {
                    Application.getInstance().getGUILog().showError(parent ,"Desktop is not supported.\n\nPlease open Read Me.pdf in <installation dir>\\manual\\ manually.", "Desktop Interface Error");
                }


            }else
            {
                Application.getInstance().getGUILog().showError("Connector Tool Plugin Read Me.pdf file not found in <installation dir>\\plugins\\ConnectorToolPlugin\\");
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ifBlockModeActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Do nothing yet...
    }

    private void readMeButtonActionPerformed(java.awt.event.ActionEvent evt)
    {

        String dir = System.getProperty("user.dir");

        try
        {

            File file = new File(dir + "\\plugins\\ConnectorImportPlugin\\ConnectorImportPlugin User Guide.pdf");

            if(file.exists())
            {
                if(Desktop.isDesktopSupported())
                {
                    Desktop.getDesktop().open(file);
                }else
                {
                    Application.getInstance().getGUILog().showError(null ,"Desktop is not supported.\n\nPlease open Read Me.pdf in <installation dir>\\manual\\ manually.", "Desktop Interface Error");
                }


            }else
            {
                Application.getInstance().getGUILog().showError("Connector Tool Plugin Read Me.pdf file not found in <installation dir>\\plugins\\ConnectorToolPlugin\\");
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void selectWireFileActionPerformed(java.awt.event.ActionEvent evt)
    {

        // Get parent dialog for messages
        //var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        MDDialogParentProvider.getProvider().setDialogParent(callingDialog);

        // Call File Chooser dialog
        JFileChooser fc = new JFileChooser();

        // Get response from user
        int returnVal = fc.showOpenDialog(callingDialog);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {

            wireFile = fc.getSelectedFile();

            try
            {
                if(wireFile.canRead())
                {

                    readWireData(wireFile);

                    wireFileSelected.setText(wireFile.getName());

                    // Set the text field telling the user how many connectors were created by the import
                    connField.setText(Integer.toString(connectorList.size()));

                    int numRows = 0;

                    for(Integer sheetLastRow : wireLastRow)
                    {
                        numRows += sheetLastRow.intValue();
                    }
                    wireRowsField.setText(Integer.toString(numRows));

                }else
                {
                    // Warn the user the file cannot be read
                    Application.getInstance().getGUILog().showMessage(callingDialog, "Selected file is Read Only.\nChoose a file that can be read before proceeding.");
                }
            }catch(Exception e)
            {
                // Warn the user the file cannot be read
                Application.getInstance().getGUILog().showMessage(callingDialog,"Error opening file.\nMSG: " + e.getLocalizedMessage());
            }

        }
    }

    private void selectSigFileActionPerformed(java.awt.event.ActionEvent evt)
    {

        // Get parent dialog for messages
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        // Call File Chooser dialog
        JFileChooser sigFC = new JFileChooser();

        int returnVal = sigFC.showOpenDialog(parent);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            signalFile = sigFC.getSelectedFile();

            try
            {
                if(signalFile.canRead())
                {

                    readSignalData(signalFile);

                    sigFileSelected.setText(signalFile.getName());

                    int numRows = 0;

                    for(Integer sheetLastRow : sigLastRow)
                    {
                        numRows += sheetLastRow.intValue();
                    }

                    // Debug
                    debugOutput.println("Number of excel rows read: " + numRows);

                    sigRowsField.setText(Integer.toString(numRows));

                    Set<StringBuilder> specificSigs = signalHierarchy.keySet();

                    int numGens = 0;

                    for(StringBuilder specific : specificSigs)
                    {
                        ArrayList<StringBuilder> genList = (ArrayList<StringBuilder>) signalHierarchy.get(specific);

                        numGens += genList.size();
                    }

                    // Debug
                    debugOutput.println("Number of generalization objects to be created: " + numGens);

                    genField.setText(Integer.toString(numGens));

                }else
                {
                    // Warn the user the file cannot be read
                    Application.getInstance().getGUILog().showMessage(parent,"Selected file is Read Only.\nChoose a file that can be read before proceeding.");
                }

            }catch(Exception e)
            {

                StackTraceElement[] stack = e.getCause().getStackTrace();

                //Application.getInstance().getGUILog().writeLogText("Error opening file.\nMSG: " + e.getLocalizedMessage() + "\n\n" + e.getMessage() + "\n\n" + e.toString()
                //        + "\n\nStack Trace:\n" + stack.toString(), false);

                // Warn the user the file cannot be read
                Application.getInstance().getGUILog().showError(parent, e.getMessage(), e);
            }

        }
    }

    private void connImportButtonActionPerformed(java.awt.event.ActionEvent evt)
    {

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        importList.clear();

        if(interconnModeSet)
        {
            importList = interconnSet.toArrayList();

        }else
        {
            importList = (ArrayList) connectorList.clone();

        }

        wireProgDiag = new ImportProgressDialog(parent, false);

        wireProgDiag.setTextField("Importing " + importList.size() + " connector(s) to the model.");

        wireProgDiag.setMaximum(importList.size());

        wireProgDiag.setVisible(true);

        wireImporter = new WireImporter();

        wireImporter.execute();

    }

    private void generalizationImportBtnActionPerformed(java.awt.event.ActionEvent evt)
    {

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        generalizationsProgDiag = new ImportProgressDialog(parent, false);

        int numGeneralizations = 0;

        for(Integer lastRow : sigLastRow)
        {
            numGeneralizations += (lastRow.intValue() - sigStartRow);
        }

        generalizationsProgDiag.setTextField("Importing " + numGeneralizations + " rows of generalizations to the model.");

        generalizationsProgDiag.setMaximum(numGeneralizations);

        generalizationsProgDiag.setVisible(true);

        genImporter = new GeneralizationsImporter();

        genImporter.execute();
    }

    private void interconnectButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Convert pin by pin connectivity to interconnect level by comparing each From Part/Wire and To Part/Wire pairs
        // and if there is a match, adding the signal names to the running list
        interconnSet.clear();

        for (Wire element : connectorList) {
            boolean success = false;

            success = interconnSet.add(element);

        }

        setTable(interconnSet.toArrayList());

        connField.setText(Integer.toString(interconnSet.size()));

        interconnModeSet = true;
    }

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Get dialog parent for message
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        // If no connectors have been read from excel
        if(connectorList.size() == 0)
        {
            // Alert the user that no connector data has been read from an excel file
            Application.getInstance().getGUILog().showMessage(parent, "No data read from Excel.  Please select file to read from.");

        }else
        {

            JFileChooser fc = new JFileChooser();

            // Only allow choosing directories for saving the exported file in
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.showSaveDialog(parent);

            // Create a file representing the selected file
            File file = fc.getSelectedFile();

            // Create an Excel file to export the data
            XLSXExportFile export = new XLSXExportFile(file.getPath(), "Connector Tool - Import Status_" + getDateTimeStamp() + ".xlsx");

//    		if(interconnModeSet)
//    		{
//    			// Add the complete connector list to the excel file to be exported
//        		export.addWires(interconnectList);
//
//    		}else
//    		{
//    			// Add the complete connector list to the excel file to be exported
//        		export.addWires(connectorList);
//    		}

            // Extract the table from the JTable
            DefaultTableModel table = (DefaultTableModel) jTable1.getModel();

            // Populate the export object with values from the ConnectorTool MainPanel JTable
            export.addTableCells(table);

            // Create the excel file in the file system
            export.writeToFile();
        }

    }

    private void clearWiresButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Delete all of the contents of each list
        connectorList.clear();
        connectorFailedList.clear();
        interconnectList.clear();
        importList.clear();
        interconnModeSet = false;

        // Extract the table data being displayed
        DefaultTableModel existingModel = (DefaultTableModel) this.jTable1.getModel();

        // Go through each row in the table and delete it
        for(int row = (existingModel.getRowCount()-1); row >= 0; row--)
        {
            existingModel.removeRow(row);
        }

        connField.setText("0");
        wireRowsField.setText("0");
        connSucceededField.setText("0");
    }

    private void clearSignalsActionPerformed(java.awt.event.ActionEvent evt)
    {
        // Delete all of the contents of the signal mapping
        signalHierarchy.clear();

        // Reset the Generalizations field
        sigRowsField.setText("0");
        genField.setText("0");
        genSucceededField.setText("0");
    }

    public void setTable(ArrayList<Wire> wireList)
    {

        DefaultTableModel existingModel = (DefaultTableModel) jTable1.getModel();

        for(int row = (existingModel.getRowCount()-1); row >= 0; row--)
        {
            existingModel.removeRow(row);
        }

        for (Wire element : wireList) {
            String[] importData = new String[12];

            importData[0] = element.getPartOwner(true).toString();
            importData[1] = element.getPart(true).toString();
            importData[2] = element.getConn(true).toString();
            importData[3] = element.getConnPN(true).toString();
            importData[4] = element.getConnPin(true).toString();
            importData[5] = element.getPartOwner(false).toString();
            importData[6] = element.getPart(false).toString();
            importData[7] = element.getConn(false).toString();
            importData[8] = element.getConnPN(false).toString();
            importData[9] = element.getConnPin(false).toString();
            importData[10] = element.getSigList().toString();
            importData[11] = element.getImportStatus().toString();


            existingModel.addRow(importData);

        }


    }

    private void readWireData(File file)
    {
        ArrayList<String> acceptableTypes = new ArrayList<>();
        // The below 3 RFC 2045 MIME file types are the only acceptable formats
        acceptableTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // .xlsx type
        acceptableTypes.add("application/vnd.ms-excel.sheet.macroenabled.12"); // .xlsm type
        acceptableTypes.add("application/vnd.ms-excel"); // .xlsx type

        connectorList.clear();
        connectorFailedList.clear();
        interconnSet.clear();

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        try
        {
            Path path = Path.of(file.getPath());

            // Create input stream for reading bytes from excel file
            wireFIS = new FileInputStream(file);

            // Probe the file at the specified path for its file type
            String fileType;

            fileType = Files.probeContentType(path);

            // Test the file type to ensure it is acceptable
            if(!(acceptableTypes.contains(fileType)))
            {
                Application.getInstance().getGUILog().showError(parent, "The type of file chosen is " + fileType + " which is not compatible.  \n\nPlease choose a file of type .XLS, .XLSX, or .XLSM");
                //JOptionPane.showMessageDialog(parent, "The type of file chosen is " + fileType + " which is not compatible.  \n\nPlease choose a file of type .XLS, .XLSX, or .XLSM", "File Type Error", JOptionPane.ERROR_MESSAGE, null);

            }else if(fileType.equals("application/vnd.ms-excel"))
            {
                // Warn the user the selected file is not the latest version of excel
                Application.getInstance().getGUILog().showMessage(parent, "Selected file is .xls, please convert the file to .xlsx before importing.");

            }else
            {
                XLSXImportFile wireFile = new XLSXImportFile(wireFIS);
                wireHeaderNames = wireFile.getHeaderNames();
                wireHeaderIndex = wireFile.getHeaderIndex();
                wireSheetListIndx = wireFile.getSelectedSheetIndex();
                wireStartRow = wireFile.getStartRow();
                wireLastRow = wireFile.getLastRow();

                try
                {
                    fromPartOwnerColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "From Part Owner").intValue();
                    fromPartColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "From Part").intValue();
                    fromConnColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "From Port").intValue();
                    fromConnPNColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "From Port Type").intValue();
                    fromConnPinColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "From Nested Port").intValue();
                    toPartOwnerColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "To Part Owner").intValue();
                    toPartColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "To Part").intValue();
                    toConnColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "To Port").intValue();
                    toConnPNColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "To Port Type").intValue();
                    toConnPinColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "To Nested Port").intValue();
                    sigNameColIndx = selectHeader(wireHeaderNames, wireHeaderIndex, "Signal").intValue();

                }catch(NullPointerException n)
                {
                    fromPartOwnerColIndx = -1;
                    fromPartColIndx = -1;
                    toPartOwnerColIndx = -1;
                    toPartColIndx = -1;
                    fromConnColIndx = -1;
                    toConnColIndx = -1;
                    fromConnPNColIndx = -1;
                    toConnPNColIndx = -1;
                    fromConnPinColIndx = -1;
                    toConnPinColIndx = -1;
                    sigNameColIndx = -1;
                }


                DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();

                for(int row = (tableModel.getRowCount()-1); row >= 0 ; row--)
                {
                    tableModel.removeRow(row);
                }

                // If valid column indices for each of the minimum data points for a valid connector are provided then read the cell data.
                if( (fromPartColIndx >= 0) && (toPartColIndx >= 0) && (fromConnColIndx >= 0) && (toConnColIndx >= 0) && (fromConnPNColIndx >= 0) && (toConnPNColIndx >= 0) )
                {
                    // Creates a list of the connectors
                    for(int sheet = 0; sheet < wireSheetListIndx.size(); sheet++)
                    {
                        for(int row = wireStartRow; row <= wireLastRow.get(sheet); row++)
                        {
                            Wire tempConn = new Wire();

                            StringBuilder fromPartOwner = wireFile.getCellData(sheet, row, fromPartOwnerColIndx).get(0);

                            StringBuilder fromPart = wireFile.getCellData(sheet, row, fromPartColIndx).get(0);

                            StringBuilder fromConn = wireFile.getCellData(sheet, row, fromConnColIndx).get(0);

                            StringBuilder fromConnPN = wireFile.getCellData(sheet, row, fromConnPNColIndx).get(0);

                            StringBuilder fromPin = wireFile.getCellData(sheet, row, fromConnPinColIndx).get(0);

                            StringBuilder toPartOwner = wireFile.getCellData(sheet, row, toPartOwnerColIndx).get(0);

                            StringBuilder toPart = wireFile.getCellData(sheet, row, toPartColIndx).get(0);

                            StringBuilder toConn = wireFile.getCellData(sheet, row, toConnColIndx).get(0);

                            StringBuilder toConnPN = wireFile.getCellData(sheet, row, toConnPNColIndx).get(0);

                            StringBuilder toPin = wireFile.getCellData(sheet, row, toConnPinColIndx).get(0);

                            ArrayList<StringBuilder> signals = wireFile.getCellData(sheet, row, sigNameColIndx);

                            tempConn.setPartOwner(fromPartOwner,true);
                            tempConn.setPart(fromPart, true);
                            tempConn.setPartOwner(toPartOwner,false);
                            tempConn.setPart(toPart, false);
                            tempConn.setConnector(fromConn, true);
                            tempConn.setConnector(toConn, false);
                            tempConn.setConnectorPN(fromConnPN, true);
                            tempConn.setConnectorPN(toConnPN, false);
                            tempConn.setConnectorPin(fromPin, true);
                            tempConn.setConnectorPin(toPin, false);
                            tempConn.setSigList(signals);

                            connectorList.add(tempConn);

                            ArrayList<String> sigListStrings = new ArrayList();

                            for(int sig = 0; sig < signals.size(); sig++)
                            {
                                sigListStrings.add(signals.get(sig).toString());
                            }

                            String[] importedData = {fromPartOwner.toString(), fromPart.toString(), fromConn.toString(), fromConnPN.toString(), fromPin.toString(), toPartOwner.toString(), toPart.toString(), toConn.toString(), toConnPN.toString(), toPin.toString(), sigListStrings.toString(), tempConn.getImportStatus().toString()};

                            tableModel.addRow(importedData);

                        }

                    }
                }


            }

            wireFIS.close();

        }catch(FileNotFoundException fnf)
        {
            StringBuilder msg = new StringBuilder(fnf.getMessage());

            wireFileSelected.setText("...");

            Application.getInstance().getGUILog().showError(parent, file.getName() + " " + msg.substring(msg.indexOf("(")+1, msg.indexOf(")")),"File Not Found");

        }catch(NullPointerException npe)
        {
            Application.getInstance().getGUILog().showError(parent, npe.getMessage(), "Null Pointer Exception");

            wireFileSelected.setText("...");

        }catch(IOException io)
        {
            Application.getInstance().getGUILog().showError(parent, io.getMessage(), "File Access Error");

            wireFileSelected.setText("...");

        }
    }

    private void readSignalData(File file)
    {
        signalHierarchy = new HashMap();

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        ArrayList<String> acceptableTypes = new ArrayList<>();

        // The below 3 RFC 2045 MIME file types are the only acceptable formats
        acceptableTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // .xlsx type
        acceptableTypes.add("application/vnd.ms-excel.sheet.macroenabled.12"); // .xlsm type
        acceptableTypes.add("application/vnd.ms-excel"); // .xlsx type

        try
        {
            Path path = Path.of(file.getPath());

            // Create input stream for reading bytes from excel file
            sigFIS = new FileInputStream(file);

            // Probe the file at the specified path for its file type
            String fileType;

            fileType = Files.probeContentType(path);

            // Test the file type to ensure it is acceptable
            if(!(acceptableTypes.contains(fileType)))
            {

                JOptionPane.showMessageDialog(parent, "The type of file chosen is " + fileType + " which is not compatible.  \n\nPlease choose a file of type .XLS, .XLSX, or .XLSM", "File Type Error", JOptionPane.ERROR_MESSAGE, null);

            }else if(fileType.equals("application/vnd.ms-excel"))
            {
                // Warn the user the selected file is not the latest version of excel
                Application.getInstance().getGUILog().showMessage(parent, "Selected file is .xls, please convert the file to .xlsx before importing.");

            }else
            {
                XLSXImportFile signalFile = new XLSXImportFile(sigFIS);

                sigHeaderNames = signalFile.getHeaderNames();

                sigHeaderIndex = signalFile.getHeaderIndex();

                sigSheetListIndx = signalFile.getSelectedSheetIndex();

                sigStartRow = signalFile.getStartRow();

                sigLastRow = signalFile.getLastRow();

                try
                {
                    specificSigColIndx = selectHeader(sigHeaderNames, sigHeaderIndex, "Specific Signal").intValue();

                    generalSigColIndx = selectHeader(sigHeaderNames, sigHeaderIndex, "General Signal").intValue();

                }catch(NullPointerException n)
                {
                    specificSigColIndx = -1;
                    generalSigColIndx = -1;

                }


                // If valid column indices for each of the minimum data points for a valid connector are provided then read the cell data.
                if( (specificSigColIndx >= 0) && (generalSigColIndx >= 0) )
                {
                    // Creates a list of the connectors
                    for(int sheet = 0; sheet < sigSheetListIndx.size(); sheet++)
                    {
                        for(int row = sigStartRow; row <= sigLastRow.get(sheet); row++)
                        {

                            StringBuilder specificSig = signalFile.getCellData(sheet, row, specificSigColIndx).get(0);

                            ArrayList<StringBuilder> generalSigList = signalFile.getCellData(sheet, row, generalSigColIndx);

                            // If the specific signal is already a key in the map
                            if(signalHierarchy.containsKey(specificSig))
                            {
                                // Add the generalSigList values to the key's value
                                ((ArrayList<StringBuilder>) signalHierarchy.get(specificSig)).addAll(generalSigList);

                            }else
                            {
                                // Create a new key value pair entry
                                signalHierarchy.put(specificSig, generalSigList);
                            }

                        }

                    }
                }
            }

            sigFIS.close();

        }catch(FileNotFoundException fnf)
        {
            StringBuilder msg = new StringBuilder(fnf.getMessage());

            sigFileSelected.setText("...");

            Application.getInstance().getGUILog().showError(parent, file.getName() + " " + msg.substring(msg.indexOf("(")+1, msg.indexOf(")")),"File Not Found");

        }catch(NullPointerException npe)
        {
            Application.getInstance().getGUILog().showHTMLError(parent, npe.getMessage(), npe);

        }catch(IOException io)
        {
            Application.getInstance().getGUILog().showError(parent, io.getMessage(), "File Access Error");

            sigFileSelected.setText("...");

        }
    }


    public static Integer selectHeader(ArrayList<String> headerNames, ArrayList<Integer> headerIndex, String header)
    {

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        Object[] choices = new Object[headerNames.size()+1];

        for(int i = 0; i < headerNames.size(); i++)
        {
            choices[i] = headerNames.get(i);
        }

        // Add a choice to allow the user to indicate the data doesn't exist in the dataset
        choices[headerNames.size()] = "(Not Included)";

        String selection = (String) JOptionPane.showInputDialog(parent, "Select the column that contains the " + header + " data", header + " Selector", JOptionPane.PLAIN_MESSAGE, null, choices, choices[choices.length-1]);

        // If the user selects the last option
        if(selection.equals(choices[headerNames.size()]))
        {
            // Return a -1 to indicate the desired data does not exist in the excel data set
            return -1;

        }else if(selection.compareTo(Integer.toString(JOptionPane.CANCEL_OPTION)) == 0)
        {
            // If user cancels out of the column selection, the program should ignore that data
            return -1;

        }else
        {
            // Return the index of the header name that matches the data to be used for that attribute later in the program
            return headerIndex.get(headerNames.indexOf(selection));
        }
    }

    private String getDateTimeStamp()
    {
        // Set up the debug log for use during import and potential export by user
        Calendar date = Calendar.getInstance();

        int day = date.get(Calendar.DAY_OF_MONTH);

        StringBuilder dd = new StringBuilder(Integer.toString(day));

        // if day is less than two digits
        if(dd.length()<2)
        {
            dd.insert(0, '0');
        }

        int month = (date.get(Calendar.MONTH)+1);

        StringBuilder MM = new StringBuilder(Integer.toString(month));

        if(MM.length()<2)
        {
            MM.insert(0, '0');
        }

        int year = date.get(Calendar.YEAR);

        int hour = date.get(Calendar.HOUR_OF_DAY);

        StringBuilder hh = new StringBuilder(Integer.toString(hour));

        if(hh.length()<2)
        {
            hh.insert(0, '0');
        }

        int minute = date.get(Calendar.MINUTE);

        StringBuilder mm = new StringBuilder(Integer.toString(minute));

        if(mm.length()<2)
        {
            mm.insert(0, '0');
        }

        return (MM.toString() + dd.toString() + year + "_" + hh.toString() + mm.toString());

    }

    private void importConnectors()
    {
        // Clear out any old failed connector import list
        connectorFailedList.clear();

        String rootPkgName = (String) importPkgComboBox.getSelectedItem();

        var contextListItr = contextList.iterator();

        while(contextListItr.hasNext())
        {
            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package pkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) contextListItr.next();

            if(pkg.getName().compareTo(rootPkgName) == 0)
            {
                rootPkg = pkg;
            }
        }

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Cleared connectorFailedList.", false);

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Retrieved dialog parent.", false);

        initializeProject();

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Project intialized.", false);

        // Start the session with Cameo
        SessionManager.getInstance().createSession(project, "Connector Tool - Import " + importList.size() + " Connector(s)");

        // Debug
        //Application.getInstance().getGUILog().writeLogText("Session created.", false);

        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package ifBlockPkg;

        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package sigPkg = finder.find(contextList, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class, "Connector Tool - Imported Signals");

        int numSuccessful = 0;

        if(sigPkg == null)
        {
            // Debug
            debugOutput.println("Signal Package not found. Creating...");

            sigPkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) gen.generatePkg((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, "Connector Tool - Imported Signals");

            // Debug
            debugOutput.println("Signal Package : " + sigPkg.getName() + " created.");

        }else
        {
            // Debug
            debugOutput.println("Signal Package found.");
        }

        for(int i = 0; i < importList.size(); i++)
        {
            // Debug
            //Application.getInstance().getGUILog().writeLogText("***************ATTEMPTING TO CREATE CONNECTOR: " + (i+1) + "   [" + importList.get(i).printConnector().toString() + "]***************", false);
            debugOutput.println("***************ATTEMPTING TO CREATE CONNECTOR: " + (i+1) + "   [" + importList.get(i).printConnector().toString() + "]***************");

            boolean completed;

            try
            {
                // Extract the relevant component names from the connector list
                String fromPartOwner = importList.get(i).getPartOwner(true).toString();
                String fromPart = importList.get(i).getPart(true).toString();
                String fromConn = importList.get(i).getConn(true).toString();
                String fromConnPN = importList.get(i).getConnPN(true).toString();
                String fromPin = importList.get(i).getConnPin(true).toString();
                String toPartOwner = importList.get(i).getPartOwner(false).toString();
                String toPart = importList.get(i).getPart(false).toString();
                String toConn = importList.get(i).getConn(false).toString();
                String toConnPN = importList.get(i).getConnPN(false).toString();
                String toPin = importList.get(i).getConnPin(false).toString();

                ArrayList<StringBuilder> sigList = importList.get(i).getSigList();

                Connector conn;

                if(((String) ifBlockMode.getSelectedItem()).compareTo("Common Types") == 0)
                {
                    // Debug
                    debugOutput.println("Creating Interface Block Package.");

                    ifBlockPkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) gen.generatePkg((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, "Connector Tool - Imported Interface Blocks");

                    // Debug
                    debugOutput.println("Package " + ifBlockPkg.getName() + " created.");

                    if(interconnModeSet)
                    {
                        // Debug
                        debugOutput.println("Creating connector " + i + "[" + fromPart + ", " + fromConn + ", " + toPart + ", " + toConn + "]");

                        // Create the connector from the source and target parts, ports, etc.
                        conn = (Connector) gen.generateInterconn(contextList, ifBlockPkg, fromPartOwner, fromPart, fromConn, fromConnPN, toPartOwner, toPart, toConn, toConnPN);

                        debugOutput.println("*RETURNING FROM ELEMENT GENERATOR*\nConnector from "
                                + ((NamedElement) conn.getEnd().get(0).getTaggedValue().get(0).getValue().get(conn.getEnd().get(0).getTaggedValue().get(0).getValue().size()-1)).getName()
                                + " " + conn.getEnd().get(0).getPartWithPort().getName()
                                + " " + conn.getEnd().get(0).getRole().getName()
                                + " " + ((NamedElement) conn.getEnd().get(1).getTaggedValue().get(0).getValue().get(conn.getEnd().get(1).getTaggedValue().get(0).getValue().size()-1)).getName()
                                + " " + conn.getEnd().get(1).getPartWithPort().getName()
                                + " " + conn.getEnd().get(1).getRole().getName()
                                + " created.");

                    }else
                    {

                        // Debug
                        debugOutput.println("Creating connector " + i + "[" + fromPart + ", " + fromConn + ", " + fromPin + ", " + toPart + ", " + toConn + ", " + toPin + "]");

                        // Create the connector from the source and target parts, ports, etc.
                        conn = (Connector) gen.generateConn(contextList, ifBlockPkg, fromPartOwner, fromPart, fromConn, fromConnPN, fromPin, toPartOwner, toPart, toConn, toConnPN, toPin);

                        debugOutput.println("*RETURNING FROM ELEMENT GENERATOR*\nConnector from "
                                + ((NamedElement) conn.getEnd().get(0).getTaggedValue().get(0).getValue().get(conn.getEnd().get(0).getTaggedValue().get(0).getValue().size()-2)).getName()
                                + " " + conn.getEnd().get(0).getPartWithPort().getName()
                                + " " + conn.getEnd().get(0).getRole().getName()
                                + " " + ((NamedElement) conn.getEnd().get(1).getTaggedValue().get(0).getValue().get(conn.getEnd().get(1).getTaggedValue().get(0).getValue().size()-2)).getName()
                                + " " + conn.getEnd().get(1).getPartWithPort().getName()
                                + " " + conn.getEnd().get(1).getRole().getName()
                                + " created.");

                    }

                }else
                {
                    ifBlockPkg = null;

                    if(interconnModeSet)
                    {

                        // Debug
                        debugOutput.println("Creating interconnect " + i + "[" + fromPart + ", " + fromConn + ", " + toPart + ", " + toConn + "]");

                        // Create the connector from the source and target parts, ports, etc.
                        conn = (Connector) gen.generateInterconn(contextList, null, fromPartOwner, fromPart, fromConn, fromConnPN, toPartOwner, toPart, toConn, toConnPN);

                        debugOutput.println("*RETURNING FROM ELEMENT GENERATOR*\nConnector from "
                                + ((NamedElement) conn.getEnd().get(0).getTaggedValue().get(0).getValue().get(conn.getEnd().get(0).getTaggedValue().get(0).getValue().size()-1)).getName()
                                + " " + conn.getEnd().get(0).getPartWithPort().getName()
                                + " " + conn.getEnd().get(0).getRole().getName()
                                + " " + ((NamedElement) conn.getEnd().get(1).getTaggedValue().get(0).getValue().get(conn.getEnd().get(1).getTaggedValue().get(0).getValue().size()-1)).getName()
                                + " " + conn.getEnd().get(1).getPartWithPort().getName()
                                + " " + conn.getEnd().get(1).getRole().getName()
                                + " created.");

                    }else
                    {
                        // Debug
                        debugOutput.println("Creating connector " + i + "[" + fromPart + ", " + fromConn + ", " + fromPin + ", " + toPart + ", " + toConn + ", " + toPin + "]");
                        // Create the connector from the source and target parts, ports, etc.
                        conn = (Connector) gen.generateConn(contextList, null, fromPartOwner, fromPart, fromConn, fromConnPN, fromPin, toPartOwner, toPart, toConn, toConnPN, toPin);

                        debugOutput.println("*RETURNING FROM ELEMENT GENERATOR*\nConnector from "
                                + ((NamedElement) conn.getEnd().get(0).getTaggedValue().get(0).getValue().get(conn.getEnd().get(0).getTaggedValue().get(0).getValue().size()-2)).getName()
                                + " " + conn.getEnd().get(0).getPartWithPort().getName()
                                + " " + conn.getEnd().get(0).getRole().getName()
                                + " " + ((NamedElement) conn.getEnd().get(1).getTaggedValue().get(0).getValue().get(conn.getEnd().get(1).getTaggedValue().get(0).getValue().size()-2)).getName()
                                + " " + conn.getEnd().get(1).getPartWithPort().getName()
                                + " " + conn.getEnd().get(1).getRole().getName()
                                + " created.");
                    }
                }

                // Iterate through the signal list and create a signal and matching itemFlow to be realized on the provided connector
                for(int j = 0; j < sigList.size(); j++)
                {
                    // Get the signal name from the signal name list
                    String sigName = importList.get(i).getSigList().get(j).toString();

                    // Debug
                    debugOutput.println("Creating signal: " + sigName);

                    // If the signal name is empty, don't create a signal or itemFlow (don't want a conveyed signal with no name)
                    if(sigName.compareTo("") != 0)
                    {
                        var signal = (Signal) gen.generateSignal(sigPkg, sigName);

                        // Debug
                        debugOutput.println("Signal " + signal.getName() + " created.");

                        if(interconnModeSet)
                        {
                            // Debug
                            debugOutput.println("Creating itemFlow for signal " + signal.getName());

                            var itemFlow = (InformationFlow) gen.generateItemFlow((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, signal, conn, fromConn);
                            // Debug
                            debugOutput.println("itemFlow " + ((NamedElement) itemFlow).getName() + " created and realized by connector.");

                        }else
                        {
                            // Debug
                            debugOutput.println("Creating itemFlow for signal " + signal.getName());

                            var itemFlow = (InformationFlow) gen.generateItemFlow((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, signal, conn, fromPin);
                            // Debug
                            debugOutput.println("itemFlow " + ((NamedElement) itemFlow).getName() + " created and realized by connector.");
                        }

                    }

                }

                completed = true;

                importList.get(i).setImportStatus(Import_Status.Imported);

                numSuccessful++;

                wireProgDiag.updateProgress(i);

            }catch(NullPointerException npe)
            {
                importList.get(i).setImportStatus(Import_Status.Failed_Missing_Required_Element);

                // Debug
                debugOutput.println("*FAILED* Unable to create connector: " + importList.get(i).printConnector().toString() + "REASON: Missing Required Element");

                completed = false;
                connectorFailedList.add(importList.get(i));
                // Debug
                //Application.getInstance().getGUILog().showMessage("Failed to create " + connectorList.get(i).printConnector() + ".  Status: " + Import_Status.Failed_Missing_Required_Element);
                //////////////////////////////////////////////////////////////////////////////////
                // save this connector off to the "failed to generate" list for export to excel //
                //////////////////////////////////////////////////////////////////////////////////

            }catch(ReadOnlyElementException roe)
            {
                importList.get(i).setImportStatus(Import_Status.Failed_Read_Only_Element);

                completed = false;
                connectorFailedList.add(importList.get(i));


                // Debug
                debugOutput.println("*FAILED* Unable to create connector: " + importList.get(i).printConnector().toString() + "REASON: Read Only Element");

                //Application.getInstance().getGUILog().showMessage("Failed to create " + connectorList.get(i).printConnector() + ".  Status: " + Import_Status.Failed_Read_Only_Element);
                //////////////////////////////////////////////////////////////////////////////////
                // save this connector off to the "failed to generate" list for export to excel //
                //////////////////////////////////////////////////////////////////////////////////
            }catch(Exception mie)
            {
                importList.get(i).setImportStatus(mie.getMessage());

                completed = false;
                connectorFailedList.add(importList.get(i));

                // Debug
                debugOutput.println("*FAILED* Unable to create connector: " + importList.get(i).printConnector().toString() + " REASON: " + mie.getMessage());

            }

        }

        connSucceededField.setText(Integer.toString(numSuccessful));

        // Terminate the session with Cameo
        SessionManager.getInstance().closeSession(project);

        // Debug
        Application.getInstance().getGUILog().writeLogText("**********************FINISHED IMPORT**********************", false);
        debugOutput.println("**********************FINISHED IMPORT**********************");

        // Display the imported connector data in the table
        setTable(importList);

        // If there are connectors that failed to import
        if(connectorFailedList.size() > 0)
        {
            // Inform the user and ask if the user wants to save the list to an Excel file
            String msg = connectorFailedList.size() + " wires failed to import. Export list of failed imports to MS Excel?";
            int selection = JOptionPane.showConfirmDialog(parent, msg, "Wire Import Failure", JOptionPane.YES_NO_OPTION);

            // Set a variable to be used to force the user to select a folder that can be written to
            boolean fileReadOnly = false;

            // Have the user select a folder that is writeable to save the export to
            do
            {
                // If the user choose a file and selects the save button
                if(selection == JOptionPane.YES_OPTION)
                {
                    // Generate a file chooser dialog
                    final JFileChooser fc = new JFileChooser();

                    // Force the user to choose a directory
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    fc.showSaveDialog(parent);

                    // Get the selected file from the file chooser
                    File file = fc.getSelectedFile();

                    // If the directory can be written to, export the data to a new excel file
                    if(file.canWrite())
                    {
                        fileReadOnly = false;

                        XLSXExportFile export = new XLSXExportFile(file.getPath(), "Connector Tool - Failed to Import - " + getDateTimeStamp() + ".xlsx");

                        export.addWires(connectorFailedList);
                        export.writeToFile();

                        // If the directory cannot be written to alert the user and reperform the directory selection
                    }else
                    {
                        Application.getInstance().getGUILog().showMessage(parent,"Selected directory is Read Only./nPlease choose another directory.");
                        fileReadOnly = true;
                    }

                }

            }while(fileReadOnly);

        }


    }

    private void importGeneralizations()
    {
        // Start the session with Cameo
        SessionManager.getInstance().createSession(project, "Connector Tool - Import Generalizations");

        // Debug
        debugOutput.println("Creating signal Package...");

        var sigPkg = finder.find((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class, "Connector Tool - Imported Signals");
        //Package sigPkg = null;

        Set<StringBuilder> keys = signalHierarchy.keySet();

        int progress = 0;

        int numSuccessful = 0;

        for(StringBuilder specificSigName : keys)
        {
            // Debug
            debugOutput.println("Processing specific signal: " + specificSigName.toString());

            ArrayList<StringBuilder> generalSigList = (ArrayList<StringBuilder>) signalHierarchy.get(specificSigName);

            // Debug
            debugOutput.println("Specific signal " + specificSigName.toString() + " has " + generalSigList.size() + " general classifiers.");

            Signal specificSig = (Signal) finder.find((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, Signal.class, specificSigName.toString());

            try
            {
                if(specificSig == null)
                {
                    // Debug
                    debugOutput.println("Specific signal " + specificSigName.toString() + " not found in model.");

                    // If the signal package doesn't exist in the model, create it
                    if(sigPkg == null)
                    {
                        sigPkg = (Signal) gen.generatePkg((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, "Connector Tool - Imported Signals");
                    }

                    // Create the specific signal
                    specificSig = (Signal) gen.generateSignal((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) sigPkg, specificSigName.toString());


                    // Debug
                    debugOutput.println("Specific signal " + specificSig.getName() + " created.");

                    // For each of the signals listed as general classifiers of the specific signal, create a generalization relationship
                    // between the specific and general signal
                    for(StringBuilder genSigName : generalSigList)
                    {
                        // Find the general signal if a version exists in the model
                        Signal generalSig = (Signal) finder.find((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, Signal.class, genSigName.toString());

                        if(generalSig == null)
                        {

                            // Debug
                            debugOutput.println("General signal " + genSigName.toString() + " not found in model.");


                            generalSig = (Signal) gen.generateSignal((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) sigPkg, genSigName.toString());


                            // Debug
                            debugOutput.println("General signal " + generalSig.getName() + " created.");

                        }

                        gen.generateGeneralization(specificSig, generalSig,(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) sigPkg);

                        // Debug
                        debugOutput.println("Generalization between signal " + specificSig.getName() + " and " + generalSig.getName() + " created.");


                        ////////////////////////////////////////
                        // CREATE LIST TO TRACK IMPORT STATUS //
                        ////////////////////////////////////////
                    }

                }else
                {

                    // If the signal package doesn't exist in the model, create it
                    if(sigPkg == null)
                    {
                        sigPkg = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) gen.generatePkg((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, "Connector Tool - Imported Signals");
                    }


                    // Debug
                    debugOutput.println("Specific signal " + specificSig.getName() + " found.");

                    // For each of the signals listed as general classifiers of the specific signal, create a generalization relationship
                    // between the specific and general signal
                    for(StringBuilder genSigName : generalSigList)
                    {
                        // Find the general signal if a version exists in the model
                        Signal generalSig = (Signal) finder.find((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) rootPkg, Signal.class, genSigName.toString());

                        if(generalSig == null)
                        {

                            // Debug
                            debugOutput.println("General signal " + genSigName.toString() + " not found in model.");


                            generalSig = (Signal) gen.generateSignal((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) sigPkg, genSigName.toString());


                            // Debug
                            debugOutput.println("General signal " + generalSig.getName() + " created.");

                        }

                        gen.generateGeneralization(specificSig,generalSig,(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) sigPkg);

                        // Debug
                        debugOutput.println("Generalization between signal " + specificSig.getName() + " and " + generalSig.getName() + " created.");


                        ////////////////////////////////////////
                        // CREATE LIST TO TRACK IMPORT STATUS //
                        ////////////////////////////////////////
                    }

                    numSuccessful++;

                }

            }catch(ReadOnlyElementException roe)
            {
                // Alert user to issue
                debugOutput.println("Unable to create generalization from " + specificSigName.toString());

            }catch(NullPointerException npe)
            {
                // Alert user to issue
                debugOutput.println("Unable to create generalization from " + specificSigName.toString());

            }catch(Exception e)
            {
                // Alert user to issue
                debugOutput.println("Unable to create generalization from " + specificSigName.toString());
                debugOutput.println(e.getMessage());
            }

            progress++;

            generalizationsProgDiag.updateProgress(progress);

            genSucceededField.setText(Integer.toString(numSuccessful));

        }

        // Terminate the session with Cameo
        SessionManager.getInstance().closeSession(project);
    }

    private class WireImporter extends SwingWorker<Void, Void>
    {
        @Override
        public Void doInBackground()
        {

            // Debug
            debugOutput.println("Starting connector import process");

            importConnectors();

            // Debug
            debugOutput.println("Exited connector import process.");

            return null;
        }

        @Override
        public void done()
        {

            wireProgDiag.updateProgress(importList.size());

            wireProgDiag.setTextField("Finished import.  Number of connectors failed to import: " + connectorFailedList.size());

            // Debug
            debugOutput.println("Set progress bar.");
        }
    }

    private class GeneralizationsImporter extends SwingWorker<Void, Void>
    {
        @Override
        public Void doInBackground()
        {
            initializeProject();

            // Debug
            debugOutput.println("Starting Generalizations import process.");

            importGeneralizations();

            // Debug
            debugOutput.println("Exited Generalizations import process.");

            return null;
        }

        @Override
        public void done()
        {
            // Set the progress bar to the maximum value
            generalizationsProgDiag.updateProgress(generalizationsProgDiag.getMaximum());

            generalizationsProgDiag.setTextField("Finished importing " + generalizationsProgDiag.getMaximum() + " generalizations.");

            // Debug
            debugOutput.println("Set progress bar.");
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearSignals;
    private javax.swing.JButton clearWiresButton;
    private javax.swing.JButton connDebug;
    private javax.swing.JTextField connField;
    private javax.swing.JButton connImportButton;
    private javax.swing.JTextField connSucceededField;
    private javax.swing.JLabel connectorLbl;
    private javax.swing.JTextField contextField;
    private javax.swing.JLabel contextLabel;
    private javax.swing.JLabel elemLbl;
    private javax.swing.JButton exportButton;
    private javax.swing.JTextField genField;
    private javax.swing.JTextField genSucceededField;
    private javax.swing.JButton generalizationImportBtn;
    private javax.swing.JLabel generalizationLbl;
    private javax.swing.JComboBox<String> ifBlockMode;
    private javax.swing.JLabel ifBlockModeLbl;
    private javax.swing.JComboBox<String> importPkgComboBox;
    private javax.swing.JButton interconnectButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton readMeButton;
    private javax.swing.JButton removeScope;
    private javax.swing.JLabel rowsLabel;
    private javax.swing.JLabel scopeLbl;
    private javax.swing.JButton selectSigFile;
    private javax.swing.JButton selectWireFile;
    private javax.swing.JTextField sigFileSelected;
    private javax.swing.JTextField sigRowsField;
    private javax.swing.JLabel successLbl;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JTextField wireFileSelected;
    private javax.swing.JTextField wireRowsField;
    // End of variables declaration//GEN-END:variables

    private HashSet<NamedElement> contextList;
    public enum ItemFlowCreationMode {Between_Parts, Direct}
    public enum ifBlockCreationMode {Unique_to_Block, Common_Types}
    InterconnectSet interconnSet = new InterconnectSet();
    private ArrayList<String> wireHeaderNames = new ArrayList<>();
    private ArrayList<String> sigHeaderNames = new ArrayList<>();
    private int fromPartOwnerColIndx, fromPartColIndx, toPartColIndx, fromConnColIndx, toPartOwnerColIndx, toConnColIndx, fromConnPNColIndx, toConnPNColIndx, fromConnPinColIndx, toConnPinColIndx, sigNameColIndx;
    private int specificSigColIndx, generalSigColIndx;
    private ArrayList<Integer> wireHeaderIndex = new ArrayList<>();
    private ArrayList<Integer> sigHeaderIndex = new ArrayList<>();
    private ArrayList<Wire> connectorList = new ArrayList<>();
    private ArrayList<Wire> connectorFailedList = new ArrayList<>();
    private ArrayList<Wire> interconnectList = new ArrayList<>();
    private ArrayList<Wire> importList = new ArrayList<>();
    private HashMap signalHierarchy;
    private int wireStartRow;
    private int sigStartRow;
    private ArrayList<Integer> wireLastRow = new ArrayList<>();
    private ArrayList<Integer> sigLastRow = new ArrayList<>();
    private ArrayList<Integer> wireSheetListIndx = new ArrayList<>();
    private ArrayList<Integer> sigSheetListIndx = new ArrayList<>();
    private File wireFile;
    private File signalFile;
    private FileInputStream wireFIS;
    private FileInputStream sigFIS;
    private String[] wireColumnHeaders = {"From Part Owner", "From Part", "From Port", "From Port Type", "From Port Nested Port", "To Part Owner", "To Part", "To Port", "To Port Type", "To Port Nested Port", "Conveyed Signal", "Import Status"};
    private MainDialog callingDialog;
    private NamedElement rootPkg;
    private JComboBox itemFlowMode;
    private boolean interconnModeSet;
    private ElementGenerator gen;
    private Finder.ByNameRecursivelyFinder finder;
    private Project project;
    private ImportProgressDialog wireProgDiag;
    private ImportProgressDialog generalizationsProgDiag;
    private WireImporter wireImporter;
    private GeneralizationsImporter genImporter;
    private JButton clearSignalsButton;
    private JLabel connectorsLabel;
    private File connectorDebugLog;
    private File generalizationDebugLog;
    private PrintWriter debugOutput;
    private File debugTempFile;

}
