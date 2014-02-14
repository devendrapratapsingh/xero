/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xero.service;

import com.xero.domain.Customer;
import com.xero.domain.Invoice;
import com.xero.domain.LineItem;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Devendra Singh
 */
public class InvoiceSenderTest {

    Customer cust;
    Invoice invoiceDomain;
    LineItem lineItem;

    public InvoiceSenderTest() {
    }

    @Before
    public void setUp() {
        cust = new Customer();
        invoiceDomain = new Invoice();
        lineItem = new LineItem();
        loadCustomer();
        loadInvoice();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInvoice method, of class InvoiceREST.
     */
    @org.junit.Test
    public void testProcessInvoice() {
        System.out.println("processInvoice");
        String param = "1";
        InvoiceSender instance = new InvoiceSender();
        String expResult = "OK";

        String result = instance.processInvoice(cust, invoiceDomain);
        result=getPostInvoiceStatus(result);
        assertEquals(expResult, result);

    }

    public Customer loadCustomer() {

        //    cust.setCustId("10101");
        cust.setFirstName("Upendra");
        cust.setLastName("Singh");
        cust.setEmail("to.upendra@gmail.com");
        return cust;
    }

    public Invoice loadInvoice() {

        lineItem.setAccountCode("200");
        lineItem.setQuantity(Long.valueOf("5"));
        lineItem.setUnitAmount(Double.valueOf("50.00D"));
        lineItem.setLineAmount(Double.valueOf(lineItem.getUnitAmount().doubleValue() * lineItem.getQuantity().doubleValue()));
        lineItem.setDescription("Programming books");
        List<LineItem> listItems = new ArrayList();
        listItems.add(lineItem);
        invoiceDomain.setLineItems(listItems);
        invoiceDomain.setCreateDate(Calendar.getInstance());
        Calendar dueDate = Calendar.getInstance();
        dueDate.add(Calendar.MONTH, +1);
        invoiceDomain.setDueDate(dueDate);
        return invoiceDomain;
    }

    private String getPostInvoiceStatus(String xml) {
        InputSource source = new InputSource(new StringReader(xml));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document document;
        String status=null;
        try {
            db = dbf.newDocumentBuilder();

            document = db.parse(source);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            status = xpath.evaluate("/Response/Status", document);
        } catch (ParserConfigurationException|SAXException|IOException|XPathExpressionException ex) {
            Logger.getLogger(InvoiceSenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } 

        return status;
    }

}
