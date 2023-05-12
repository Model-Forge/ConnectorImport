package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.util.ArrayList;

public class Wire {

    private StringBuilder fromPartOwner, fromPart, toPartOwner, toPart, fromConn, toConn, fromConnPN, toConnPN, fromConnPin, toConnPin;//, sigName;
    public enum Import_Status {Pending_Import, Imported, Failed_Read_Only_Element, Failed_Missing_Required_Element}
    private String importStatus;
    private ArrayList<StringBuilder> partsList, connList, connPNList, connPinList, sigNameList;

    public Wire()
    {
        // Initialize variables
        fromPartOwner = new StringBuilder();
        fromPart = new StringBuilder();
        toPartOwner = new StringBuilder();
        toPart = new StringBuilder();
        fromConn = new StringBuilder();
        toConn = new StringBuilder();
        fromConnPN = new StringBuilder();
        toConnPN = new StringBuilder();
        fromConnPin = new StringBuilder();
        toConnPin = new StringBuilder();

        importStatus = Import_Status.Pending_Import.toString();

        sigNameList = new ArrayList<>();
        partsList = new ArrayList<>(2);
        connList = new ArrayList<>(2);
        connPNList = new ArrayList<>(2);
        connPinList = new ArrayList<>(2);
    }

    public Wire(StringBuilder fromPart, StringBuilder toPart, StringBuilder fromConn, StringBuilder toConn, StringBuilder fromConnPN, StringBuilder toConnPN, StringBuilder fromConnPin, StringBuilder toConnPin, ArrayList<StringBuilder> sigNameList)
    {
        // Set variables to
        sigNameList = new ArrayList<>();
        partsList = new ArrayList<>(2);
        connList = new ArrayList<>(2);
        connPNList = new ArrayList<>(2);
        connPinList = new ArrayList<>(2);

        fromPartOwner = new StringBuilder();
        toPartOwner = new StringBuilder();

        this.fromPart = new StringBuilder(fromPart.subSequence(0, fromPart.length()));
        partsList.add(this.fromPart);
        this.toPart = new StringBuilder(toPart.subSequence(0, toPart.length()));
        partsList.add(this.toPart);
        this.fromConn = new StringBuilder(fromConn.subSequence(0, fromConn.length()));
        connList.add(this.fromConn);
        this.toConn = new StringBuilder(toConn.subSequence(0, toConn.length()));
        connList.add(this.toConn);

        this.fromConnPN = new StringBuilder(fromConnPN.subSequence(0, fromConnPN.length()));
        connPNList.add(this.fromConnPN);

        this.toConnPN = new StringBuilder(toConnPN.subSequence(0, toConnPN.length()));
        connPNList.add(this.toConnPN);

        this.fromConnPin = new StringBuilder(fromConnPin.subSequence(0, fromConnPin.length()));
        connPinList.add(this.fromConnPin);

        this.toConnPin = new StringBuilder(toConnPin.subSequence(0, toConnPin.length()));
        connPinList.add(this.toConnPin);

        this.sigNameList = (ArrayList<StringBuilder>) sigNameList.clone();

        this.importStatus = Import_Status.Pending_Import.toString();

    }

    public Wire(StringBuilder fromPart, StringBuilder toPart, StringBuilder fromConn, StringBuilder toConn, StringBuilder fromConnPN, StringBuilder toConnPN)
    {
        // Set variables
        this.sigNameList = new ArrayList<>();
        this.partsList = new ArrayList<>(2);
        this.connList = new ArrayList<>(2);
        this.connPNList = new ArrayList<>(2);
        this.connPinList = new ArrayList<>(2);

        this.fromConnPin = new StringBuilder();
        this.toConnPin = new StringBuilder();

        this.fromPart = new StringBuilder(fromPart.subSequence(0, fromPart.length()));
        partsList.add(this.fromPart);
        this.toPart = new StringBuilder(toPart.subSequence(0, toPart.length()));
        partsList.add(this.toPart);
        this.fromConn = new StringBuilder(fromConn.subSequence(0, fromConn.length()));
        connList.add(this.fromConn);
        this.toConn = new StringBuilder(toConn.subSequence(0, toConn.length()));
        connList.add(this.toConn);

        this.fromConnPN = new StringBuilder(fromConnPN.subSequence(0, fromConnPN.length()));
        connPNList.add(this.fromConnPN);

        this.toConnPN = new StringBuilder(toConnPN.subSequence(0, toConnPN.length()));
        connPNList.add(this.toConnPN);

        this.importStatus = Import_Status.Pending_Import.toString();
    }

    public void setPartOwner(StringBuilder partOwnerName, Boolean from)
    {
        if(from)
        {
            if(this.fromPartOwner.length() > 0)
            {
                this.fromPartOwner.delete(0, this.fromPartOwner.length());
                this.fromPartOwner.setLength(partOwnerName.length());
                this.fromPartOwner.replace(0, partOwnerName.length(), partOwnerName.substring(0));
            }else
            {
                this.fromPartOwner.setLength(partOwnerName.length());
                this.fromPartOwner.replace(0, partOwnerName.length(), partOwnerName.substring(0));
            }
        }else
        {
            if(this.toPartOwner.length() > 0)
            {
                this.toPartOwner.delete(0, this.toPartOwner.length()-1);
                this.toPartOwner.setLength(partOwnerName.length());
                this.toPartOwner.replace(0, partOwnerName.length(), partOwnerName.substring(0));
            }else
            {
                this.toPartOwner.setLength(partOwnerName.length());
                this.toPartOwner.replace(0, partOwnerName.length(), partOwnerName.substring(0));
            }
        }
    }

    public StringBuilder getPartOwner(Boolean from)
    {
        if(from)
        {
            return this.fromPartOwner;
        }else
        {
            return this.toPartOwner;
        }

    }

    /**
     * Sets the From Part field, by first checking if the field already contains characters.  If it does, it deletes them and replaces with the new name.  If it doesn't have a name already it sets it.
     * @param partName The name which should be set for the field
     * @param from	True = from part, false = to part
     */
    public void setPart(StringBuilder partName, Boolean from)
    {
        if(from)
        {
            if(this.fromPart.length() > 0)
            {
                this.fromPart.delete(0, this.fromPart.length());
                this.fromPart.setLength(partName.length());
                this.fromPart.replace(0, partName.length(), partName.substring(0));
            }else
            {
                this.fromPart.setLength(partName.length());
                this.fromPart.replace(0, partName.length(), partName.substring(0));
            }
        }else
        {
            if(this.toPart.length() > 0)
            {
                this.toPart.delete(0, this.toPart.length()-1);
                this.toPart.setLength(partName.length());
                this.toPart.replace(0, partName.length(), partName.substring(0));
            }else
            {
                this.toPart.setLength(partName.length());
                this.toPart.replace(0, partName.length(), partName.substring(0));
            }
        }
    }

    /**
     * Returns the "From Part"
     * @return fromPart StringBuilder
     */
    public StringBuilder getPart(Boolean from)
    {
        if(from)
        {
            return this.fromPart;
        }else
        {
            return this.toPart;
        }

    }

    public ArrayList<StringBuilder> getPartsList()
    {
        return this.partsList;
    }

    public void setConnector(StringBuilder connName, Boolean from)
    {
        if(from)
        {
            if(this.fromConn.length() > 0)
            {
                this.fromConn.delete(0, this.fromPart.length());
                this.fromConn.setLength(connName.length());
                this.fromConn.replace(0, connName.length(), connName.substring(0));
            }else
            {
                this.fromConn.setLength(connName.length());
                this.fromConn.replace(0, connName.length(), connName.substring(0));
            }
        }else
        {
            if(this.toConn.length() > 0)
            {
                this.toConn.delete(0, this.toPart.length()-1);
                this.toConn.setLength(connName.length());
                this.toConn.replace(0, connName.length(), connName.substring(0));
            }else
            {
                this.toConn.setLength(connName.length());
                this.toConn.replace(0, connName.length(), connName.substring(0));
            }
        }
    }

    public StringBuilder getConn(Boolean from)
    {
        if(from)
        {
            return this.fromConn;
        }else
        {
            return this.toConn;
        }

    }

    public ArrayList<StringBuilder> getConnList()
    {
        return this.connList;
    }

    public void setConnectorPN(StringBuilder connPN, Boolean from)
    {
        if(from)
        {
            if(this.fromConnPN.length() > 0)
            {
                this.fromConnPN.delete(0, this.fromConnPN.length());
                this.fromConnPN.setLength(connPN.length());
                this.fromConnPN.replace(0, connPN.length(), connPN.substring(0));
            }else
            {
                this.fromConnPN.setLength(connPN.length());
                this.fromConnPN.replace(0, connPN.length(), connPN.substring(0));
            }
        }else
        {
            if(this.toConnPN.length() > 0)
            {
                this.toConnPN.delete(0, this.toConnPN.length()-1);
                this.toConnPN.setLength(connPN.length());
                this.toConnPN.replace(0, connPN.length(), connPN.substring(0));
            }else
            {
                this.toConnPN.setLength(connPN.length());
                this.toConnPN.replace(0, connPN.length(), connPN.substring(0));
            }
        }
    }

    public StringBuilder getConnPN(Boolean from)
    {
        if(from)
        {
            return this.fromConnPN;
        }else
        {
            return this.toConnPN;
        }

    }

    public ArrayList<StringBuilder> getConnPNList()
    {
        return this.connPNList;
    }

    public void setConnectorPin(StringBuilder connPin, Boolean from)
    {
        if(from)
        {
            if(this.fromConnPin.length() > 0)
            {
                this.fromConnPin.delete(0, this.fromConnPin.length());
                this.fromConnPin.setLength(connPin.length());
                this.fromConnPin.replace(0, connPin.length(), connPin.substring(0));
            }else
            {
                this.fromConnPin.setLength(connPin.length());
                this.fromConnPin.replace(0, connPin.length(), connPin.substring(0));
            }
        }else
        {
            if(this.toConnPin.length() > 0)
            {
                this.toConnPin.delete(0, this.toConnPin.length()-1);
                this.toConnPin.setLength(connPin.length());
                this.toConnPin.replace(0, connPin.length(), connPin.substring(0));
            }else
            {
                this.toConnPin.setLength(connPin.length());
                this.toConnPin.replace(0, connPin.length(), connPin.substring(0));
            }
        }
    }

    public StringBuilder getConnPin(Boolean from)
    {
        if(from)
        {
            return this.fromConnPin;
        }else
        {
            return this.toConnPin;
        }

    }

    public ArrayList<StringBuilder> getConnPinList()
    {
        return this.connPinList;
    }

    public void setSigName(ArrayList<StringBuilder> names)
    {
        if(this.sigNameList.size() > 0)
        {
            this.sigNameList.clear();
            this.sigNameList = (ArrayList<StringBuilder>) names.clone();
        }else
        {
            this.sigNameList = (ArrayList<StringBuilder>) names.clone();
        }
    }


    public ArrayList<StringBuilder> getSigName()
    {
        return this.sigNameList;
    }

    public ArrayList<StringBuilder> getSigList()
    {
        return this.sigNameList;
    }

    public boolean addSignal(StringBuilder sig)
    {
        this.sigNameList.add(sig);
        return true;
    }

    public boolean setSigList(ArrayList<StringBuilder> sigList)
    {
        sigNameList.clear();

        for(int i = 0; i < sigList.size(); i++)
        {
            sigNameList.add(sigList.get(i));
        }

        return true;
    }


    public void setImportStatus(Import_Status status)
    {
        this.importStatus = status.toString();
    }

    public void setImportStatus(String status)
    {
        this.importStatus = status;
    }

    public String getImportStatus()
    {
        return this.importStatus;
    }

    public void reverse()
    {

        // Reverse parts
        StringBuilder tempPart = new StringBuilder(this.fromPart.toString());

        this.fromPart.delete(0, this.fromPart.length());
        this.fromPart.append(this.toPart.toString());

        this.toPart.delete(0, this.toPart.length());
        this.toPart.append(tempPart.toString());

        // Reverse connectors
        StringBuilder tempConn = new StringBuilder(this.fromConn.toString());

        this.fromConn.delete(0, this.fromConn.length());
        this.fromConn.append(this.toConn.toString());

        this.toConn.delete(0, this.toConn.length());
        this.toConn.append(tempConn.toString());

        // Reverse connector PNs
        StringBuilder tempConnPN = new StringBuilder(this.fromConnPN.toString());

        this.fromConnPN.delete(0, this.fromConnPN.length());
        this.fromConnPN.append(this.toConnPN.toString());

        this.toConnPN.delete(0, this.toConnPN.length());
        this.toConnPN.append(tempConnPN.toString());

        // Reverse connector Pins
        StringBuilder tempConnPin = new StringBuilder(this.fromConnPin.toString());

        this.fromConnPin.delete(0, this.fromConnPin.length());
        this.fromConnPin.append(this.toConnPin.toString());

        this.toConnPin.delete(0, this.toConnPin.length());
        this.toConnPin.append(tempConnPin.toString());

    }

    public Boolean isEqual(Wire c)
    {
        boolean partEqual = false;
        boolean connEqual = false;
        boolean connTypeEqual = false;
        boolean pinEqual = false;


        // Check if parts are the same
        for (StringBuilder element : this.getPartsList()) {
            for (StringBuilder element2 : c.getPartsList()) {
                if(element.compareTo(element2) == 0)
                {
                    partEqual = true;
                }
            }
        }


        // Check if connectors are the same
        for (StringBuilder element : this.getConnList()) {
            for (StringBuilder element2 : c.getConnList()) {
                if(element.compareTo(element2) == 0)
                {
                    connEqual = true;
                }
            }
        }


        // Check if connector types are the same
        for (StringBuilder element : this.getConnPNList()) {
            for (StringBuilder element2 : c.getConnPNList()) {
                if(element.compareTo(element2) == 0)
                {
                    connTypeEqual = true;
                }
            }
        }

        // Check if connector pins are the same
        for (StringBuilder element : this.getConnPinList()) {
            for (StringBuilder element2 : c.getConnPinList()) {
                if(element.compareTo(element2) == 0)
                {
                    pinEqual = true;
                }
            }
        }

        return (partEqual && connEqual && pinEqual && connTypeEqual);
    }


    public Wire createInteconnect()
    {
        Wire newConnector = new Wire(this.fromPart, this.toPart, this.fromConn, this.toConn, this.fromConnPN, this.toConnPN);

        return newConnector;
    }

    public StringBuilder printConnector()
    {
        StringBuilder tempString = new StringBuilder();

        tempString.append(this.fromPart.toString() + " ");
        tempString.append(this.fromConn.toString() + " ");
        tempString.append(this.fromConnPN.toString() + " ");
        tempString.append(this.fromConnPin.toString() + " ");
        tempString.append(this.toPart.toString() + " ");
        tempString.append(this.toConn.toString() + " ");
        tempString.append(this.toConnPN.toString() + " ");
        tempString.append(this.toConnPin.toString() + " ");
        for (StringBuilder element : this.sigNameList) {
            tempString.append(element.toString() + " ");
        }

        return tempString;
    }
}
