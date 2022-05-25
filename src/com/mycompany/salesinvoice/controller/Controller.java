
package com.mycompany.salesinvoice.controller;

import com.mycompany.salesinvoice.model.Invoice;
import com.mycompany.salesinvoice.model.InvoiceModelTable;
import com.mycompany.salesinvoice.model.Item;
import com.mycompany.salesinvoice.model.ItemModelTable;
import com.mycompany.salesinvoice.view.InvoiceViewDialog;
import com.mycompany.salesinvoice.view.ItemViewDialog;
import com.mycompany.salesinvoice.view.ViewInvoiceFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import static java.text.MessageFormat.format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.TemporalQueries.zone;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sound.midi.Soundbank;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener,ListSelectionListener{
    private ViewInvoiceFrame frame;
    private InvoiceViewDialog invoDialog;
    private ItemViewDialog itDialog;
    
    
    public Controller(ViewInvoiceFrame frame){
        this.frame=frame;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand=e.getActionCommand();
        System.out.println("Action "+actionCommand);
         switch(actionCommand){
            case"Load File":
                loadFile();
                break;
            case"Save File":
                saveFile();
                break;
            case"Create New Invoice":
                createNewInvoice();
                break;
            case"Delete Invoice":
                deleteInvoice();
                break; 
            case"Create New Item":
                createNewItem();
                break;   
            case"Delete Item":
                deleteItem();
                break;
            case "CreatenewInvoiceOK":
                CreatenewInvoiceOK();
                break;
            case "CancelCreatenewInvoice":
                CancelCreatenewInvoice();
                break;
            case "CreateNewItemOK":
                CreateNewItemOK();
                break;
            case "CancelCreateNewItem":
                CancelCreateNewItem();
                break;
        }
       
    }
     @Override
    public void valueChanged(ListSelectionEvent e) {
        int selected=frame.getjTableInvoiceTable().getSelectedRow();
        if (selected!=-1){
            System.out.println("You selected row: "+selected);
            Invoice currentInvoice=frame.getInvoices().get(selected);
            frame.getjLabelInvoiceNumber().setText(""+currentInvoice.getNumber());
            frame.getjLabelInvoiceDate().setText(currentInvoice.getDate());
            frame.getjLabelCustomerName().setText(currentInvoice.getCustomer());
            frame.getjLabelInvoiceTotal().setText(""+currentInvoice.getTotalOfInvoice());
            ItemModelTable itemmodel=new ItemModelTable(currentInvoice.getItems());
            frame.getjTableItemTable().setModel(itemmodel);
            itemmodel.fireTableDataChanged();
        }
    }
   

    private void loadFile() {
        JFileChooser fc=new JFileChooser();
        try{
        int r=fc.showOpenDialog(frame);
        if(r==JFileChooser.APPROVE_OPTION){
            File headerFile=fc.getSelectedFile();
            Path headerPath=Paths.get(headerFile.getAbsolutePath());
            List<String> headerInvoices=Files.readAllLines(headerPath);
            System.out.println("Invoices have been read Successfully !");
            ArrayList<Invoice>invoicesList=new ArrayList<>();
            for(String headerInvoice:headerInvoices){
                try{
                String[] headerelements=headerInvoice.split(",");
                int invoiceNum=Integer.parseInt(headerelements[0]);
                String invoiceDate=headerelements[1];
                String customerName=headerelements[2];
                Invoice invoice=new Invoice(invoiceNum,invoiceDate,customerName);
                invoicesList.add(invoice);
                }catch(Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame,"Line format error","Error",JOptionPane.ERROR_MESSAGE);
                }
            }
            r=fc.showOpenDialog(frame);
            if(r==JFileChooser.APPROVE_OPTION){
                File itemFile=fc.getSelectedFile();
                Path itemPath=Paths.get(itemFile.getAbsolutePath());
                List<String>invoiceItems=Files.readAllLines(itemPath);
                System.out.println("Items have been read Successfully !");
                for(String invoiceItem:invoiceItems){
                    try{
                        String itemelements[]=invoiceItem.split(",");
                        int invoiceNum=Integer.parseInt(itemelements[0]);
                        String itemName=itemelements[1];
                        double itemPrice=Double.parseDouble(itemelements[2]);
                        int count=Integer.parseInt(itemelements[3]);
                        Invoice inv=null;
                        for(Invoice invoice:invoicesList)
                        {
                            if(invoice.getNumber()==invoiceNum)
                            {
                                inv=invoice;
                                break;
                            }
                        }
                        Item item=new Item(itemName, itemPrice, count, inv);
                        inv.getItems().add(item);
                    } catch(Exception ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame,"Line format error","Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
    
            frame.setInvoices(invoicesList);
            InvoiceModelTable invoiceModelTable=new InvoiceModelTable((invoicesList));
            frame.setInvoicemodel(invoiceModelTable);
            frame.getjTableInvoiceTable().setModel(invoiceModelTable);
            frame.getInvoicemodel().fireTableDataChanged();
        }
    }
        catch(IOException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame,"Can't read file","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        ArrayList<Invoice> invoices=frame.getInvoices();
        String heads="";
        String items="";
        for(Invoice in:invoices ){
            String invCSV=in.getAsCSVFile();
            heads+=invCSV;
            heads+="\n";
            
            for(Item it:in.getItems()){
                String itCSV=it.getAsCSVFile();
                items+=itCSV;
                items+="\n";
            }
        }
        try{
           JFileChooser fc=new JFileChooser();
           int result=fc.showSaveDialog(frame);
           if(result==JFileChooser.APPROVE_OPTION){
               File headerFile=fc.getSelectedFile();
               FileWriter headerfw=new FileWriter(headerFile);
               headerfw.write(heads);
               headerfw.flush();
               headerfw.close();
               result=fc.showSaveDialog(frame);
               if(result==JFileChooser.APPROVE_OPTION){
                   File itemFile=fc.getSelectedFile();
                   FileWriter itemfw=new FileWriter(itemFile);
               itemfw.write(items);
               itemfw.flush();
               itemfw.close();
               }
       }
    }
        catch(Exception ex){
            
        }
    }
    private void createNewInvoice() {
        invoDialog=new InvoiceViewDialog(frame);
        invoDialog.setVisible(true);
    }

    private void deleteInvoice() {
        int selected=frame.getjTableInvoiceTable().getSelectedRow();
        if(selected!=-1){
            frame.getInvoices().remove(selected);
            frame.getInvoicemodel().fireTableDataChanged();
        }
    }

    private void createNewItem() {
       itDialog=new ItemViewDialog(frame);
       itDialog.setVisible(true);
       
    }

    private void deleteItem() {
        int inv=frame.getjTableInvoiceTable().getSelectedRow();
        int selected=frame.getjTableItemTable().getSelectedRow();
        if(inv!=-1&&selected!=-1){
           Invoice invoice=frame.getInvoices().get(inv);
           invoice.getItems().remove(selected);
           ItemModelTable item=new ItemModelTable(invoice.getItems());
           frame.getjTableItemTable().setModel(item);
           item.fireTableDataChanged();
           frame.getInvoicemodel().fireTableDataChanged();
        }
        
    }
    
    private void CreatenewInvoiceOK() {
        String date=invoDialog.getDateField().getText();
        String customer=invoDialog.getCustomerField().getText();
        int num=frame.getNextInvoiceNumber();
        try{
         String[] datelement=date.split("-");
         if(datelement.length<3){
            JOptionPane.showMessageDialog(frame,date+" is wrong date format","Error",JOptionPane.ERROR_MESSAGE);
         }
         else{
             int d=Integer.parseInt(datelement[0]);
             int m=Integer.parseInt(datelement[1]);
             int y=Integer.parseInt(datelement[2]);
             if(d>31||m>12||1980>y&&y>2022){
                    JOptionPane.showMessageDialog(frame,date+" is wrong date format","Error",JOptionPane.ERROR_MESSAGE); 
             } else{
        Invoice invoice=new Invoice(num,date,customer);
        frame.getInvoices().add(invoice);
        frame.getInvoicemodel().fireTableDataChanged();
        invoDialog.setVisible(false);
        invoDialog.dispose();
        invoDialog=null;
         }}
        }catch(Exception ex){
            JOptionPane.showMessageDialog(frame,date+" is wrong date format","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void CancelCreatenewInvoice() {
        invoDialog.setVisible(false);
        invoDialog.dispose();
        invoDialog=null;
   }

    private void CreateNewItemOK() {
        String item=itDialog.getItNameField().getText();
        String countStr=itDialog.getItCountField().getText();
        String priceStr=itDialog.getItPriceField().getText();
        int count=Integer.parseInt(countStr);
        double price =Double.parseDouble(priceStr);
        int selected =frame.getjTableInvoiceTable().getSelectedRow();
        if(selected!=-1){
            Invoice invo=frame.getInvoices().get(selected);
            Item it=new Item(item,price,count,invo);
            invo.getItems().add(it);
            ItemModelTable itemModelTable=(ItemModelTable) frame.getjTableItemTable().getModel();
            //itemModelTable.getItems().add(it);
            itemModelTable.fireTableDataChanged(); 
            frame.getInvoicemodel().fireTableDataChanged();
        
        }
        itDialog.setVisible(false);
        itDialog.dispose();
        itDialog=null;
    
    }

    private void CancelCreateNewItem() {
        itDialog.setVisible(false);
        itDialog.dispose();
        itDialog=null;
    }

   
}
