/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletException;

/**
 *
 * @author mlarour
 */
public class SnannyConfig
{
    // properties location
    private static final String PROPERTIES_LOCATION = "/WEB-INF/snannyConfig.properties";
    
    // properties keys
    private static final String PROPERTY_XML_CHARSET        = "charset";
    private static final String PROPERTY_SNANNY_UPLOAD      = "snnany-upload";
    private static final String PROPERTY_SNANNY_SOST_SERVER = "snanny-sostServer";
    
    private static SnannyConfig snannyConfig = null;
    
    private String snannySostServer;
    private Charset charset;
    private File snannyUpload;
    
    
    
    /** Getter for the singleton, perform initialization at first call
     * 
     * @param servletContext the servlet context
     * @return the the unique instance
     * @throws SnannySostServerException if properties can't be loaded at first call
     */
    public static synchronized SnannyConfig singleton() throws SnannyConfigException
    {        
        if(snannyConfig == null)
        {
            snannyConfig = new SnannyConfig();
        }
        return(snannyConfig);                        
    }
    
    /** SensorNannyConfig constructor
     * 
     * @param servletContext context of deployed servlet
     * @throws SnannySostServerException if properties can't be loaded
     */
    private SnannyConfig() throws SnannyConfigException
    {
        Properties properties = new Properties();
        // load properties
        try
        {                   
           properties.load(FacesContext.getCurrentInstance().
                                        getExternalContext().
                                        getResourceAsStream(PROPERTIES_LOCATION));
        }
        catch(IOException ioe)
        {       
            throw new SnannyConfigException("Can't get snannyconfig.properties");                     
        }
        
        // initialize charset
        if(properties.getProperty(PROPERTY_XML_CHARSET) == null)
        {
           throw new SnannyConfigException("Can't get charset from  snannyconfig.properties");           
        }
        else
        {
            try
            {
                charset = Charset.forName(properties.getProperty(PROPERTY_XML_CHARSET));
            }
            catch(Exception ex)
            {
                throw new SnannyConfigException("Can't create charset from  snannyconfig.properties");
            }
        }
        // initialize snanny-sostServer
        if(properties.getProperty(PROPERTY_SNANNY_SOST_SERVER) == null)
        {
            throw new SnannyConfigException("Can't get snanny-sostServer from  snannyconfig.properties");            
        }
        else
        {
            snannySostServer = properties.getProperty(PROPERTY_SNANNY_SOST_SERVER);
        }
        // initialize snnany-upload
        if(properties.getProperty(PROPERTY_SNANNY_UPLOAD) == null)
        {
            throw new SnannyConfigException("Can't get snanny upload directory from  snannyconfig.properties");
        }
        else
        {
            snannyUpload = new File(properties.getProperty(PROPERTY_SNANNY_UPLOAD));
        }
    }
    
    /** Getter for the charset of files (eg UTF-8)
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @return the snannySostServer
     */
    public String getSnannySostServer() {
        return snannySostServer;
    }

    /**
     * @return the snannyUpload
     */
    public File getSnannyUpload() {
        return snannyUpload;
    }

    
}
