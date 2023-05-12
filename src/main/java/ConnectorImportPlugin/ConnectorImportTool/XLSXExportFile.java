package ConnectorImportPlugin.ConnectorImportTool;
/**
 *  ConnectorImportPlugin and all associated classes or files are provided
 *  in accordance with the terms provided in the provided license file.
 *  Copyright 2023 Jason Aepli
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXExportFile {

    public XLSXExportFile(String filePath, String fileName) {


        file = new File(filePath + "\\" + fileName);

        try
        {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();

            failedImportList = new ArrayList<>();
            headerRowNum = 0;

            sheet = workbook.createSheet("Failed to Import");

            columnHeaders = new ArrayList<>();
            columnHeaders.add("From Part Owner");
            columnHeaders.add("From Part");
            columnHeaders.add("From Port");
            columnHeaders.add("From Port Type");
            columnHeaders.add("From Port Nested Port");
            columnHeaders.add("To Part Owner");
            columnHeaders.add("To Part");
            columnHeaders.add("To Port");
            columnHeaders.add("To Port Type");
            columnHeaders.add("To Port Nested Port");
            columnHeaders.add("Conveyed Signal");
            columnHeaders.add("Import Status");

            headerRow = sheet.createRow(headerRowNum);
            //headerRow.setRowStyle(null);

            for(int i = 0; i < columnHeaders.size(); i++)
            {
                XSSFCell cell = headerRow.createCell(i);

                cell.setCellType(CellType.STRING);

                cell.setCellValue(columnHeaders.get(i));
            }

        }catch(FileNotFoundException fnf)
        {
            // Need to figure out what to do here
        }catch(IOException ioe)
        {
            // Need to figure out what to do here
        }

    }

    private Path path;

    private String fileName;

    private ArrayList<Wire> failedImportList;

    private File file;

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private int headerRowNum;

    private XSSFRow headerRow;

    private FileOutputStream out;

    private ArrayList<String> columnHeaders;

    public boolean addWires(ArrayList<Wire> wireList)
    {
        for(int row = 0; row < wireList.size(); row++)
        {
            XSSFRow newRow = sheet.createRow(row+headerRowNum+1);

            XSSFCell fromPartOwnerCell = newRow.createCell(0);
            XSSFCell fromPartCell = newRow.createCell(1);
            XSSFCell fromPortCell = newRow.createCell(2);
            XSSFCell fromPortTypeCell = newRow.createCell(3);
            XSSFCell fromNestedPortCell = newRow.createCell(4);
            XSSFCell toPartOwnerCell = newRow.createCell(5);
            XSSFCell toPartCell = newRow.createCell(6);
            XSSFCell toPortCell = newRow.createCell(7);
            XSSFCell toPortTypeCell = newRow.createCell(8);
            XSSFCell toNestedPortCell = newRow.createCell(9);
            XSSFCell conveyedSignalCell = newRow.createCell(10);
            XSSFCell importStatusCell = newRow.createCell(11);

            fromPartOwnerCell.setCellType(CellType.STRING);
            fromPartCell.setCellType(CellType.STRING);
            fromPortCell.setCellType(CellType.STRING);
            fromPortTypeCell.setCellType(CellType.STRING);
            fromNestedPortCell.setCellType(CellType.STRING);
            toPartOwnerCell.setCellType(CellType.STRING);
            toPartCell.setCellType(CellType.STRING);
            toPortCell.setCellType(CellType.STRING);
            toPortTypeCell.setCellType(CellType.STRING);
            toNestedPortCell.setCellType(CellType.STRING);
            conveyedSignalCell.setCellType(CellType.STRING);
            importStatusCell.setCellType(CellType.STRING);

            fromPartOwnerCell.setCellValue(wireList.get(row).getPartOwner(true).toString());
            fromPartCell.setCellValue(wireList.get(row).getPart(true).toString());
            fromPortCell.setCellValue(wireList.get(row).getConn(true).toString());
            fromPortTypeCell.setCellValue(wireList.get(row).getConnPN(true).toString());
            fromNestedPortCell.setCellValue(wireList.get(row).getConnPin(true).toString());
            toPartOwnerCell.setCellValue(wireList.get(row).getPartOwner(false).toString());
            toPartCell.setCellValue(wireList.get(row).getPart(false).toString());
            toPortCell.setCellValue(wireList.get(row).getConn(false).toString());
            toPortTypeCell.setCellValue(wireList.get(row).getConnPN(false).toString());
            toNestedPortCell.setCellValue(wireList.get(row).getConnPin(false).toString());

            importStatusCell.setCellValue(wireList.get(row).getImportStatus().toString());

            StringBuilder sigCell = new StringBuilder();

            var sigItr = wireList.get(row).getSigList().iterator();

            while(sigItr.hasNext())
            {
                sigCell.append(sigItr.next().toString());

                if(sigItr.hasNext())
                {
                    sigCell.append("\n");
                }
            }

            conveyedSignalCell.setCellValue(sigCell.toString());

        }

        return true;
    }

    public boolean addTableCells(TableModel table)
    {
        for(int row = 0; row < table.getRowCount(); row++ )
        {
            XSSFRow newRow = sheet.createRow(row+headerRowNum+1);

            XSSFCell fromPartOwnerCell = newRow.createCell(0);
            XSSFCell fromPartCell = newRow.createCell(1);
            XSSFCell fromPortCell = newRow.createCell(2);
            XSSFCell fromPortTypeCell = newRow.createCell(3);
            XSSFCell fromNestedPortCell = newRow.createCell(4);
            XSSFCell toPartOwnerCell = newRow.createCell(5);
            XSSFCell toPartCell = newRow.createCell(6);
            XSSFCell toPortCell = newRow.createCell(7);
            XSSFCell toPortTypeCell = newRow.createCell(8);
            XSSFCell toNestedPortCell = newRow.createCell(9);
            XSSFCell conveyedSignalCell = newRow.createCell(10);
            XSSFCell importStatusCell = newRow.createCell(11);

            fromPartOwnerCell.setCellType(CellType.STRING);
            fromPartCell.setCellType(CellType.STRING);
            fromPortCell.setCellType(CellType.STRING);
            fromPortTypeCell.setCellType(CellType.STRING);
            fromNestedPortCell.setCellType(CellType.STRING);
            toPartOwnerCell.setCellType(CellType.STRING);
            toPartCell.setCellType(CellType.STRING);
            toPortCell.setCellType(CellType.STRING);
            toPortTypeCell.setCellType(CellType.STRING);
            toNestedPortCell.setCellType(CellType.STRING);
            conveyedSignalCell.setCellType(CellType.STRING);
            importStatusCell.setCellType(CellType.STRING);

            fromPartOwnerCell.setCellValue((String) table.getValueAt(row, 0));
            fromPartCell.setCellValue((String) table.getValueAt(row, 1));
            fromPortCell.setCellValue((String) table.getValueAt(row, 2));
            fromPortTypeCell.setCellValue((String) table.getValueAt(row, 3));
            fromNestedPortCell.setCellValue((String) table.getValueAt(row, 4));
            toPartOwnerCell.setCellValue((String) table.getValueAt(row, 5));
            toPartCell.setCellValue((String) table.getValueAt(row, 6));
            toPortCell.setCellValue((String) table.getValueAt(row, 7));
            toPortTypeCell.setCellValue((String) table.getValueAt(row, 8));
            toNestedPortCell.setCellValue((String) table.getValueAt(row, 9));
            conveyedSignalCell.setCellValue((String) table.getValueAt(row, 10));
            importStatusCell.setCellValue((String) table.getValueAt(row, 11));
        }

        return true;
    }

    public boolean writeToFile()
    {
        try
        {
            workbook.write(out);

            out.close();

        }catch(IOException io)
        {
            return false;
        }

        return true;

    }


}
