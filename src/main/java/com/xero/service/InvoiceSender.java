/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xero.service;

import com.xero.ArrayOfInvoice;
import com.xero.ArrayOfLineItem;
import com.xero.Contact;
import com.xero.LineItem;
import com.xero.Invoice;
import com.xero.InvoiceStatus;
import com.xero.InvoiceType;
import com.xero.domain.Customer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.MappingException;

/**
 *
 * @author Devendra Singh
 */

public class InvoiceSender {

    private XeroAPIConnector xeroConnector;
    
   
 
    public String processInvoice( com.xero.domain.Customer cust, com.xero.domain.Invoice invoiceDomain ) {
        String response = null; 
        try {
            ArrayOfInvoice arrayOfInvoice = invoiceMapper(cust, invoiceDomain);
            xeroConnector = new XeroAPIConnector();
            response = xeroConnector.postInvoices(arrayOfInvoice);
        } catch (Exception e) {
            Logger.getLogger(InvoiceSender.class.getName()).log(Level.SEVERE, "Error in invoice post" + e);
        }
        Logger.getLogger(InvoiceSender.class.getName()).fine("response"+response);
        return response;
    }

    private ArrayOfInvoice invoiceMapper(Customer cust, com.xero.domain.Invoice invoiceDomain) throws MappingException {
        ArrayOfInvoice arrayOfInvoice = new ArrayOfInvoice();
        ArrayOfLineItem arrayOfLineItem = new ArrayOfLineItem();
        List<LineItem> lineItems = arrayOfLineItem.getLineItem();
        Mapper mapper = new DozerBeanMapper();
        LineItem lineitem = mapper.map(invoiceDomain.getLineItems().get(0), LineItem.class);
        lineItems.add(lineitem);
        Contact contact = mapper.map(cust, Contact.class);
        contact.setName(contact.getFirstName() + " " + contact.getLastName());
        Invoice invoice = new Invoice();
        invoice = mapper.map(invoiceDomain, Invoice.class);
        invoice.setLineItems(arrayOfLineItem);
        invoice.setContact(contact);
        invoice.getLineAmountTypes().add("Inclusive");
        invoice.setType(InvoiceType.ACCREC);
        invoice.setStatus(InvoiceStatus.AUTHORISED);
        List<Invoice> invoices = arrayOfInvoice.getInvoice();
        invoices.add(invoice);
        return arrayOfInvoice;
    }

}
