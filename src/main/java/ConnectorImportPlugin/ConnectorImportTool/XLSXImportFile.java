package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

public class XLSXImportFile {

    public XLSXImportFile(FileInputStream fileInputStream) throws IOException
    {
        //creating Workbook instance that refers to .xlsx file
        this.wb = new XSSFWorkbook(fileInputStream);

        // Call method to have user select the sheet with data
        this.sheetList = selectSheet(this.wb);

        // Ask user whether data includes headers
        this.firstRowHeaders = headerPrompt();

        // Identify the 0 based index of the first column with data in the first row with data
        this.firstDataColNum = this.sheetList.get(0).getRow(this.sheetList.get(0).getFirstRowNum()).getFirstCellNum();

        // Identify the 0 based index of the last column with data in the first row with data
        this.lastDataColNum = this.sheetList.get(0).getRow(this.sheetList.get(0).getFirstRowNum()).getLastCellNum();

        // Identify the last row of each selected sheet with data
        for(int i = 0; i < sheetList.size(); i++)
        {
            this.lastDataRow.add(this.sheetList.get(i).getLastRowNum());
        }

        // Get column choices from file
        this.readHeaders();
    }

    private Boolean firstRowHeaders;
    private int headerRow, startDataRow, firstDataColNum, lastDataColNum; //lastDataRow
    private ArrayList<Integer> lastDataRow = new ArrayList<>();
    private ArrayList<String> headerNames = new ArrayList<>();  // To store the excel column name (e.g. column B)
    private ArrayList<Integer> headerIndex = new ArrayList<>();  // To store the matching integer index for the column (i.e. column B = 2)
    private XSSFWorkbook wb;
    private ArrayList<XSSFSheet> sheetList;
    private ArrayList<Integer> sheetListIndex = new ArrayList<>();
    private ArrayList<String> sheetNames = new ArrayList<>();

    public int getStartRow()
    {
        return this.startDataRow;
    }

    public ArrayList<Integer> getLastRow()
    {
        return this.lastDataRow;
    }

    public ArrayList<Integer> getSelectedSheetIndex()
    {
        return this.sheetListIndex;
    }

    // Prompt the user to select the sheet (or entire workbook) for data import
    private ArrayList<XSSFSheet> selectSheet(XSSFWorkbook workbook)
    {

        ArrayList<XSSFSheet> sheetsList = new ArrayList<>();
        Boolean validSelection;
        StringBuilder selection = new StringBuilder();

        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        // Collect all the various sheet names into a list of names and add them to choices array
        for(int i = 0; i < workbook.getNumberOfSheets(); i++)
        {
            this.sheetNames.add(workbook.getSheetName(i));
        }

        Object[] choices = new Object[this.sheetNames.size()+1];


        choices[0] = "(All Sheets)";

        for(int i = 0; i < this.sheetNames.size(); i++)
        {
            choices[i+1] = this.sheetNames.get(i).toString();
        }



        // Display the sheet selection dialog, collect the user's selection, and test the sheet to ensure data exists
        do
        {
            validSelection = true;

            // Create message dialog with drop down menu that includes the names of the sheets in the workbook for the user to choose from
            selection.append((String) JOptionPane.showInputDialog(parent, "Select the sheet that contains the desired data", "Sheet Selector", JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]));

            // If user selects (All Sheets) then add all the sheets in the workbook to the sheets list
//			if(selection.toString().equals(choices[choices.length-1]))
            if(selection.toString().equals(choices[0]))
            {
                sheetsList.clear();
                this.sheetListIndex.clear();

                for(int i = 0; i < this.sheetNames.size(); i++)
                {
                    sheetsList.add(workbook.getSheetAt(i));
                    this.sheetListIndex.add(i);

                }

                // Warn the user about format assumption
                JOptionPane.showMessageDialog(parent, "WARNING: All sheets are assumed to be formatted the same (i.e. Column A contains the same type of data across all sheets).", "Sheet Selection Warning", JOptionPane.WARNING_MESSAGE, null);

                // Cycle through all of the workbook's sheets
                for (XSSFSheet element : sheetsList) {
                    // If no data exists in one of the sheets, display error message and set validSelection to false so the do..while loop repeats
                    if(element.getFirstRowNum() == -1)
                    {
                        validSelection = false;
                        selection.delete(0, selection.length());
                        JOptionPane.showMessageDialog(parent, "One of the workbook sheets contains no data.  Please choose again.", "Sheet Selection Error", JOptionPane.ERROR_MESSAGE, null);
                        break;
                    }
                }

                // If only one sheet was selected...
            }else
            {
                // Get the sheet the user chose
                sheetsList.clear();
                sheetsList.add(workbook.getSheetAt(this.sheetNames.indexOf(selection.toString())));
                this.sheetListIndex.clear();
                this.sheetListIndex.add(this.sheetNames.indexOf(selection.toString()));

                // If no data exists in the selected sheet, display error message and trigger the do..while loop repeats
                if(sheetsList.get(0).getFirstRowNum() == -1)
                {
                    validSelection = false;
                    selection.delete(0, selection.length());
                    JOptionPane.showMessageDialog(parent, "Selected sheet (or sheets) contains no data.  Please choose again.", "Sheet Selection Error", JOptionPane.ERROR_MESSAGE, null);

                }
            }

            // If a valid sheet selection is made, terminate the do...while loop
        }while(!validSelection);

        return sheetsList;
    }


    // Prompt the user to provide an answer about whether or not column names/headers exist in the spreadsheet
    private Boolean headerPrompt()
    {
        // Get parent for the messages to be displayed
        var parent = MDDialogParentProvider.getProvider().getDialogParent(true);

        int choice = JOptionPane.showConfirmDialog(parent, "Does data include column headers?", "Header Row", JOptionPane.YES_NO_OPTION);

        if(choice == JOptionPane.YES_OPTION)
        {
            return true;
        }else
        {
            return false;
        }
    }

    private static boolean deleteLeadingSpace(StringBuilder s)
    {
        char firstChar = s.charAt(0);

        if(Character.isWhitespace(firstChar))
        {
            s.deleteCharAt(0);
            deleteLeadingSpace(s);
            return false;
        }else
        {
            return true;
        }

    }

    private static boolean deleteTrailingSpace(StringBuilder s)
    {
        char lastChar = s.charAt(s.length()-1);

        if(Character.isWhitespace(lastChar))
        {
            s.deleteCharAt(s.length()-1);
            deleteTrailingSpace(s);
            return false;
        }else
        {
            return true;
        }

    }

    // Search the specified sheet for the column header names.  If no names exist, use the column alphanumeric code
    private void readHeaders()
    {
        if(this.firstRowHeaders)
        {
            this.headerRow = this.sheetList.get(0).getFirstRowNum();
            this.startDataRow = this.sheetList.get(0).getFirstRowNum()+1;

            // If headers exist, extract the header names to be used in the column combo boxes
            // To ensure that there are no empty columns in the used range of columns cycle through each
            // For each of the columns within the used range of columns...
            for(int i = this.firstDataColNum; i < this.lastDataColNum; i++)
            {


                if(!(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getCellType() == CellType.BLANK))
                {
                    // Test if the data in the header column is String type
                    if(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getCellType() == CellType.STRING)
                    {
                        // Add the header text to the array
                        this.headerNames.add(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getStringCellValue());

                        // Save the corresponding index value in a parallel array
                        this.headerIndex.add(i);

                    }else if(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getCellType() == CellType.NUMERIC)
                    {
                        // Convert numeric header to string....
                        this.headerNames.add(String.valueOf(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getNumericCellValue()));

                        // Save the corresponding index value in a parallel array
                        this.headerIndex.add(i);

                    }else if(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getCellType() == CellType.BOOLEAN)
                    {
                        // Add boolean header to header array
                        if(this.sheetList.get(0).getRow(this.headerRow).getCell(i).getBooleanCellValue())
                        {
                            // Since .getBooleanCellValue() returns a 1 for true and 0 for false, save the string version
                            this.headerNames.add("True");

                            // Save the corresponding index value in a parallel array
                            this.headerIndex.add(i);

                        }else
                        {
                            // Since .getBooleanCellValue() returns a 1 for true and 0 for false, save the string version
                            this.headerNames.add("False");

                            // Save the corresponding index value in a parallel array
                            this.headerIndex.add(i);
                        }

                    }

                }

            }
        }else
        {
            this.startDataRow = 0;
            // If headers do NOT exist, use the excel column reference names
            // To ensure that there are no empty columns in the used range of columns cycle through each
            // For each of the columns within the used range of columns...
            for(int i = this.firstDataColNum; i < this.lastDataColNum; i++)
            {
                // If the cell is NOT blank...
                if(!(this.sheetList.get(0).getRow(this.startDataRow).getCell(i).getCellType() == CellType.BLANK))
                {
                    // Add the column index to ArrayList filledColNums
                    this.headerIndex.add(i);

                    // Add the column reference value to ArrayList filledColRefs by taking the cell alphanumeric reference and stripping out any digits (i.e. AC9 becomes AC)
                    this.headerNames.add(this.sheetList.get(0).getRow(this.startDataRow).getCell(i).getReference().replaceAll("\\d", "")); // Make this the column reference value
                }
            }
        }
    }

    // Return the names of the headers
    public ArrayList<String> getHeaderNames()
    {
        return this.headerNames;
    }

    // Return the column index associated with the header name
    public ArrayList<Integer> getHeaderIndex()
    {
        return this.headerIndex;
    }

    // Read cell and return its data
    public ArrayList<StringBuilder> getCellData(int sheetIndx, int row, int col)
    {
        ArrayList<StringBuilder> list = new ArrayList<>();
        StringBuilder value = new StringBuilder();

        if(col >= 0)
        {
            XSSFCell cell = sheetList.get(sheetIndx).getRow(row).getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            if(cell.getCellType() == CellType.STRING)
            {
                String debug = "";

                String[] cellRawValue = this.sheetList.get(sheetIndx).getRow(row).getCell(col).getStringCellValue().split("\n");
                //StringBuilder cellArray = new StringBuilder();
                for (String element : cellRawValue) {
                    StringBuilder cellValue = new StringBuilder(element);

                    if(element != null)
                    {
                        deleteLeadingSpace(cellValue);
                        deleteTrailingSpace(cellValue);
                        list.add(cellValue);
                        //debug = debug + cellRawValue[i];
                    }

                }

                return list;

            }else if(cell.getCellType() == CellType.BLANK)
            {
                value.append("");
                list.add(value);

                return list;

            }else if(cell.getCellType() == CellType.ERROR)
            {
                value.append("");
                list.add(value);

                return list;

            }else if(cell.getCellType() == CellType._NONE)
            {
                value.append("");
                list.add(value);

                return list;

            }else if(cell.getCellType() == CellType.NUMERIC)
            {
                value.append(String.valueOf((int) this.sheetList.get(sheetIndx).getRow(row).getCell(col).getNumericCellValue()));
                deleteLeadingSpace(value);
                deleteTrailingSpace(value);
                list.add(value);

                return list;

            }else if(cell.getCellType() == CellType.BOOLEAN)
            {
                value.append(String.valueOf(this.sheetList.get(sheetIndx).getRow(row).getCell(col).getBooleanCellValue()));
                deleteLeadingSpace(value);
                deleteTrailingSpace(value);
                list.add(value);

                return list;

            }else
            {
                value.append("");
                list.add(value);

                return list;
            }

        }else
        {
            value.append("");
            list.add(value);

            return list;
        }

    }
}