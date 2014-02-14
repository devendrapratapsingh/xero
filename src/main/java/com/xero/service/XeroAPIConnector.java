/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xero.service;


import com.xero.ArrayOfInvoice;
import com.xero.ObjectFactory;
import com.xero.ResponseType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient3.HttpClient3;
import net.oauth.signature.RSA_SHA1;


/**
 *
 * @author x076108
 */
public class XeroAPIConnector {

    private static final Logger LOGGER = Logger.getLogger(XeroAPIConnector.class.getName());
    private static final String ENDPOINT_URL = "endpointUrl";
    private static final String CONSUMER_KEY = "consumerKey";
    private static final String CONSUMER_SECRET = "consumerSecret";
    private static final String PRIVATE_KEY_FILE = "privateKeyFile";
    private String endpointUrl;
    private String consumerKey;
    private String consumerSecret;
    private String privateKey;
    private Properties props;

    public XeroAPIConnector() {

        loadXeroProps("xeroApi.properties");

        endpointUrl = props.getProperty(ENDPOINT_URL);
        consumerKey = props.getProperty(CONSUMER_KEY);
        consumerSecret = props.getProperty(CONSUMER_SECRET);
        privateKey = loadFromFile(props.getProperty(PRIVATE_KEY_FILE));

    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public OAuthAccessor buildAccessor() {  

        OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, null, null);
        consumer.setProperty(RSA_SHA1.PRIVATE_KEY, privateKey);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);

        OAuthAccessor accessor = new OAuthAccessor(consumer);
        accessor.accessToken = consumerKey;
        accessor.tokenSecret = consumerSecret;

        return accessor;
    }

    public String  postInvoices(ArrayOfInvoice arrayOfInvoices) throws OAuthProblemException, Exception {
        try {
            String invoiceXML = convertInvoiceToXml(arrayOfInvoices);
            OAuthClient client = new OAuthClient(new HttpClient3());
            OAuthAccessor accessor = buildAccessor();
            OAuthMessage response = client.invoke(accessor, OAuthMessage.POST, endpointUrl + "Invoices", OAuth.newList("xml", invoiceXML));
            return response.readBodyAsString();
        } catch (OAuthProblemException ex) {
            throw new OAuthProblemException("Error posting contancts"+ ex);
        } catch (Exception ex) {
            throw new Exception("Error", ex);
        }
    }
public static String convertInvoiceToXml(ArrayOfInvoice arrayOfInvoices) {

        String invoicesString = null;

        try {

            
            
            
            JAXBContext context = JAXBContext.newInstance(ResponseType.class);
            Marshaller marshaller = context.createMarshaller();
             marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);

            ObjectFactory factory = new ObjectFactory();
            JAXBElement<ArrayOfInvoice> element = factory.createInvoices(arrayOfInvoices);

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(element, stringWriter);
            invoicesString = stringWriter.toString();

        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
return invoicesString;
}

    private void loadXeroProps(String propertyFile) {
        try {
            props = new Properties();
            InputStream inputStream = getClass().getResourceAsStream("/"+propertyFile);
            this.props.load(inputStream);
            
//            props.setProperty(ENDPOINT_URL, "https://api.xero.com/api.xro/2.0/");
//            props.setProperty(PRIVATE_KEY_FILE, "/privateKey.pem");
//            props.setProperty(CONSUMER_KEY, "I6QC8W5ZSSMODZNIXVH9PDUX5D2Y4Y");
//            props.setProperty(CONSUMER_SECRET, "JRQNKTQSBPFLHFOO8VCOETY6UBIHW1");
//            
            inputStream.close();
        } catch (Exception exp) {
            LOGGER.log(Level.SEVERE, "Xero API Property File not found", exp.getMessage());
        }
    }

    private String loadFromFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            File file=new File(getClass().getResource("/"+fileName).getPath());
           
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException exp) {
            LOGGER.log(Level.SEVERE, "File not found:", exp.getMessage());
        }
        return stringBuilder.toString();
    }

}
