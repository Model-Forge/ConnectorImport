package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.util.ArrayList;

public class InterconnectSet {

    private ArrayList<Wire> interconnSet;

    public InterconnectSet()
    {
        this.interconnSet = new ArrayList<>();
    }

    public boolean add(Wire c)
    {
        boolean inList = false;

        inList = ContainsWire(c);

        if(!inList)
        {
            Wire newWire = new Wire();

            newWire.setPartOwner(c.getPartOwner(true), true);
            newWire.setPartOwner(c.getPartOwner(false), false);
            newWire.setPart(c.getPart(true), true);
            newWire.setPart(c.getPart(false), false);
            newWire.setConnector(c.getConn(true), true);
            newWire.setConnector(c.getConn(false), false);
            newWire.setConnectorPN(c.getConnPN(true), true);
            newWire.setConnectorPN(c.getConnPN(false), false);
            newWire.setSigList(c.getSigList());

            interconnSet.add(newWire);

            return true;
        }else
        {
            ArrayList<StringBuilder> newSignalList = c.getSigList();

            int equalIdx = getEqualIndex(c);

            ArrayList<StringBuilder> currentSignalList = interconnSet.get(equalIdx).getSigList();

            ArrayList<StringBuilder> sigListToAdd = new ArrayList<>();

            boolean sigInList = false;

            // For each signal in the new signal list
            for(StringBuilder newSig : newSignalList)
            {
                for(StringBuilder currentSig : currentSignalList)
                {
                    if(newSig.compareTo(currentSig) == 0)
                    {
                        sigInList = true;
                    }
                }

                if(!sigInList)
                {
                    sigListToAdd.add(newSig);
                }
            }

            for(StringBuilder toAdd : sigListToAdd)
            {
                interconnSet.get(equalIdx).addSignal(toAdd);
            }

            return false;
        }

    }

    public int size()
    {
        return this.interconnSet.size();
    }

    public Wire get(int index)
    {
        return this.interconnSet.get(index);
    }

    public ArrayList<Wire> toArrayList()
    {
        return this.interconnSet;
    }

    public boolean clear()
    {
        this.interconnSet.clear();
        return true;
    }

    private boolean ContainsWire(Wire c)
    {

        int index = getEqualIndex(c);

        if(index >= 0)
        {
            return true;
        }else
        {
            return false;
        }

    }

    public int getEqualIndex(Wire c)
    {
        int index = -1;

        // For each wire in the interconnect set
        for(int i = 0; i < this.interconnSet.size(); i++)
        {
            // Check if the From Part Owner matches
            if(interconnSet.get(i).getPartOwner(true).compareTo(c.getPartOwner(true)) == 0)
            {
                // Check if the To Part Owner matches
                if(interconnSet.get(i).getPartOwner(false).compareTo(c.getPartOwner(false)) == 0)
                {
                    // Check if the From Part matches
                    if(this.interconnSet.get(i).getPart(true).compareTo(c.getPart(true)) == 0)
                    {
                        // Check if the To Part matches
                        if(this.interconnSet.get(i).getPart(false).compareTo(c.getPart(false)) == 0)
                        {
                            // Check if the From Port matches
                            if(this.interconnSet.get(i).getConn(true).compareTo(c.getConn(true)) == 0)
                            {
                                // Check if the To Port matches
                                if(this.interconnSet.get(i).getConn(false).compareTo(c.getConn(false)) == 0)
                                {
                                    // If all the above tests are true, then the wire already exists in
                                    // the interconnect set and its index is "i"
                                    index = i;
                                }
                            }
                        }
                    }
                }
            }

        }

        return index;
    }

}
