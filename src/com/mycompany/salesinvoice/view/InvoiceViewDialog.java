
package com.mycompany.salesinvoice.view;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class InvoiceViewDialog extends JDialog{
    private JTextField customerField;
    private JTextField dateField;
    private JButton cancelBtn;
    private JButton okBtn;
    private JLabel customerLbl;
    private JLabel dateLbl;
    
    
    public InvoiceViewDialog(ViewInvoiceFrame frame){
        customerLbl=new JLabel("Customer Name: ");
        customerField=new JTextField(20);
        dateLbl=new JLabel("Invoice Date: ");
        dateField=new JTextField(20);
        okBtn=new JButton("OK");
        cancelBtn=new JButton("Cancel");
        okBtn.setActionCommand("CreatenewInvoiceOK");
        cancelBtn.setActionCommand("CancelCreatenewInvoice");
        
        okBtn.addActionListener(frame.getController());
        cancelBtn.addActionListener(frame.getController());
        setLayout(new GridLayout(3,2));
        add(dateLbl);
        add(dateField);
        add(customerLbl);
        add(customerField);
        add(okBtn);
        add(cancelBtn);
        
        pack();
        
    }

    public JTextField getCustomerField() {
        return customerField;
    }

    public JTextField getDateField() {
        return dateField;
    }
    
    
}
