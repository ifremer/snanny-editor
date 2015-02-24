/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import config.SnannyConfig;
import config.SnannyConfigException;
import freemarker.template.TemplateException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import xml.XmlBuilder;

/** Bean for primefaces interface
 *
 * @author mlarour
 */
@ManagedBean
@ViewScoped
public class ObservationBean implements Serializable {

    private static final String insertObservationEndUrlString = "sos?service=SOS&version=2.0&request=insertObservation";
    private static final String restEndUrlBeforeUuidString = "record/";

    /** Getter for insertObservationUrlString 
     * @return "sos?service=SOS&amp;version=2.0&amp;request=insertObservation" 
     */
    public String getInsertObservationUrlString() {
        return insertObservationUrlString;
    }

    /** Setter for insertObservationUrlString
     * @param aInsertObservationUrlString the insertObservationUrlString to set
     */
    public void setInsertObservationUrlString(String aInsertObservationUrlString) {
        insertObservationUrlString = aInsertObservationUrlString;
    }

        
    private String siteCode         = null;
    private String dataType         = null;
    private Date   from             = null;
    private Date   to               = null;    
    private String uuid             = UUID.randomUUID().toString();
    private String sensoruuid       = null;
    private String datasetName      = null;
    private Double latitude         = null;        
    private Double longitude        = null;
    private String fileName         = null;
    private String fileNameRequired = null;    
    private String fileFormat       = null;    
    
    private UploadedFile uploadedFile;    
    
    private String oem;
    private String sosRequest;

    private SnannyConfig snannyConfig = null;
    private boolean configurable = true;
    
    private static String insertObservationUrlString;  
    
    /** Post Constructor initialization
     * 
     */
    @PostConstruct
    private void init()
    {        
        //initPylos();
        try
        {
            snannyConfig = SnannyConfig.singleton();
            setInsertObservationUrlString(snannyConfig.getSnannySostServer()+insertObservationEndUrlString);
        
        }
        catch(SnannyConfigException sce)
        {
            setConfigurable(false);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, sce.getMessage(),sce.getMessage()));
        }        
    }
    /** Clean bean values
     * 
     */
    private void clean()
    {
        siteCode    = "";
        dataType    = "Time-series";
        from        = null;
        to          = null;
        uuid        = UUID.randomUUID().toString();
        sensoruuid  = "";        
        datasetName = "";
        latitude    = 0.0;
        longitude   = 0.0;        
        fileName         = "";
        fileNameRequired = "";
        fileFormat       = "application/netcdf";        
    }
    
    /** Sample initialization for tests
     * 
     */
    private void initPylos()
    {
        System.out.println("**** observationBean.initPylos()");
        siteCode    = "E1M3A";
        dataType    = "Time-series";
        from        = Date.from(LocalDateTime.of(2014, 10, 10, 0, 0,0).toInstant(ZoneOffset.UTC));
        to          = Date.from(LocalDateTime.of(2014, 10, 31,21, 0,0).toInstant(ZoneOffset.UTC));
        /** 
         * uuid        = UUID.randomUUID().toString(); **/
        sensoruuid  = "6c6bf0c8-334d-48db-bda5-297b642a097b";        
        datasetName = "MO_201410_TS_MO_61277";
        latitude    = 35.723;
        longitude   = 25.462;        
        fileName         = uuid+".MO_201410_TS_MO_61277.nc";
        fileNameRequired = uuid+".MO_201410_TS_MO_61277.nc";
        fileFormat       = "application/netcdf";        
    }
    /** Observation Bean Constructor for primefaces interface
     * 
     */
    public ObservationBean() {
    }
                
    /**  Perform the physical copy of the uploaded file on disk server defined 
     * with snnany-upload= in WEB-INF/snannyConfig.properties
     * 
     * @param event the File Upload event having the temporary file uploaded by primefaces 
     */
     public void handleFileUpload(FileUploadEvent event)
    {        
        fileName=uuid+"."+event.getFile().getFileName();
        fileNameRequired=fileName;       
        try
        {            
           Path downloadFile = new File(snannyConfig.getSnannyUpload(),fileName).toPath();           
           Files.copy(event.getFile().getInputstream(),downloadFile,StandardCopyOption.REPLACE_EXISTING);           
        }
        catch(IOException ioe)
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, ioe.getMessage(),ioe.getMessage()));
        }        
        
    }
           
    
    /** getter for the observation data model
     * 
     * @return the observation data model
     */
    private HashMap getSensorMLmodel()
    {
        HashMap sensorMLmodel = new HashMap();
                       
        sensorMLmodel.put("siteCode",siteCode);
        sensorMLmodel.put("dataType",dataType);
        sensorMLmodel.put("from",from);
        sensorMLmodel.put("to",to);        
        sensorMLmodel.put("uuid",uuid);        
        sensorMLmodel.put("sensorMLrest",snannyConfig.getSnannySostServer()+restEndUrlBeforeUuidString+sensoruuid);
        sensorMLmodel.put("datasetName",datasetName);         
        sensorMLmodel.put("latitude",latitude);
        sensorMLmodel.put("longitude",longitude);
        Path downloadFile = new File(snannyConfig.getSnannyUpload(),fileName).toPath();
        sensorMLmodel.put("downloadFile",downloadFile.toString());
        sensorMLmodel.put("fileFormat",fileFormat);
        
        return(sensorMLmodel);
    }
    
    /** getter for the observation data model to process with freemarker 
     * observation template to get xml observation (O&M)
     * 
     * @return the observation data model
     */
    private HashMap getSosRequestModel(String sensorML)
    {
        HashMap sosRequestModel = new HashMap();                       
        sosRequestModel.put("sensorML",sensorML);                
        return(sosRequestModel);
    }
    
    /** build observation xml,sos request xml, post to sost-server and clean bean 
     * 
     * @param actionEvent the primefaces action
     */
    public void insertAction(ActionEvent actionEvent)
    {
        try
        {                       
            buildXmls();
            insertObservation();
            clean();            
        }
        catch(TemplateException | IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(),ex.getMessage()));
        }
        

    }
    
    /** Post the xml request (insert observation) to sost-server
     * 
     * @throws MalformedURLException
     * @throws IOException 
     */
    private void insertObservation() throws MalformedURLException, IOException
    {
        System.out.println(sosRequest);
        URL insertObservationUrl = new URL(getInsertObservationUrlString());
        HttpURLConnection connection     = (HttpURLConnection)insertObservationUrl.openConnection();
        connection.setInstanceFollowRedirects(false); 
        connection.setRequestMethod("POST"); 
        connection.setRequestProperty("Content-Type", "text/xml"); 
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", "" + sosRequest.length());
        connection.setUseCaches (false);
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,snannyConfig.getCharset());
        outputStreamWriter.write(sosRequest);
        outputStreamWriter.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),snannyConfig.getCharset()));
        String status = "";
        String line = null;
        while ((line = in.readLine()) != null) {
             status+=line;
        }
        in.close();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"status", status);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    /** build observation xml,sos request xml
     * 
     * @throws IOException
     * @throws TemplateException 
     */
    private void buildXmls() throws IOException, TemplateException
    {
        oem = XmlBuilder.buildSensorML(getSensorMLmodel());
        sosRequest = XmlBuilder.buildSosRequest(getSosRequestModel(oem));
    }
    
    /** Fill the preview tabs (O&amp;M xml and SOS request) in primefaces interface
     * 
     * @param actionEvent the primefaces action
     */
    public void previewAction(ActionEvent actionEvent)
    {        
        try
        {                       
            buildXmls();
        }
        catch(TemplateException | IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(),ex.getMessage()));
        }
    }        
    
    /** Getter for O&amp;M xml content
     * @return the xml bean value
     */
    public String getOem() {
        
        return oem;
    }

    /** Setter for O&amp;M xml content
     * @param oem content bean value to set
     */
    public void setOem(String oem) {
        this.oem = oem;
    }
    

    /** Getter for the latitude
     * @return the latitude bean value
     */
    public Double getLatitude() {
        return latitude;
    }

    /** Setter for the latitude
     * @param latitude the latitude bean value to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /** Setter for the longitude
     * @return the longitude bean value
     */
    public Double getLongitude() {
        return longitude;
    }

    /** Setter for the longitude
     * @param longitude the longitude bean value to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /** Getter for the data type
     * @return the data type bean value
     */
    public String getDataType() {
        return dataType;
    }

    /** Setter for the data type
     * @param dataType the data type bean value to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**Getter for the site Code
     * @return the site code bean value
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**Setter for the site Code
     * @param siteCode the site code bean value to set
     */
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    /** Getter for the begin date
     * @return the begin date bean value
     */
    public Date getFrom() {
        return from;
    }

    /**Setter for the begin date
     * @param from the begin date bean value to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /** Getter for the end date
     * @return the end date bean value
     */
    public Date getTo() {
        return to;
    }

    /** Setter for the end date
     * @param to the end date  bean value to set
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /** Getter for the file format
     * @return the file format bean value
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /** Setter for the file format
     * @param fileFormat the file format bean value to set
     */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    

    /** Getter for the uuid
     * @return the uuid bean value
     */
    public String getUuid() {
        return uuid;
    }

    /** Setter for the uuid
     * @param uuid the uuid bean value to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /** Getter for the dataset name
     * @return the dataset name bean value
     */
    public String getDatasetName() {
        return datasetName;
    }

    /** Setter for the dataset name
     * @param datasetName the dataset name bean value to set
     */
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

     

    /** Getter for the uploaded file name
     * @return the uploaded file name bean value
     */
    public String getFileName() {        
        return fileName;
    }

    /** Setter for the uploaded file name
     * @param fileName the uploaded file name bean value to set
     */
    public void setFileName(String fileName) {        
        this.fileName = fileName;
    }

    /** Getter for the sos request xml content
     * @return the sos request content bean value
     */
    public String getSosRequest() {
        return sosRequest;
    }

    /** Setter for the sos request xml content
     * @param sosRequest the sos request content to set
     */
    public void setSosRequest(String sosRequest) {
        this.sosRequest = sosRequest;
    }

    /** Duplicate of file name bean for primefaces messages,
     * Getter for the uploaded file name
     * @return the uploaded file name bean value
     */
    public String getFileNameRequired() {
        return fileNameRequired;
    }

    /** Duplicate of file name bean for primefaces messages,
     * Setter for the uploaded file name
     * @param fileNameRequired the uploaded file name bean value to set
     */
    public void setFileNameRequired(String fileNameRequired) {
        this.fileNameRequired = fileNameRequired;
    }

    /**Setter for snnanyObservation configuration state 
     * @return the configurable bean value <ul><li>true snnanyObservation is configured</li><li>false failed to configure snnanyObservation</li></ul>
     */
    public boolean isConfigurable() {
        return configurable;
    }

    /** Setter for snnanyObservation configuration state 
     * @param configurable the configurable bean value to set<ul><li>true snnanyObservation is configured</li><li>false failed to configure snnanyObservation</li></ul>
     */
    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    /** Getter for the associated sensorMl uuid
     * @return the sensorML uuid bean value
     */
    public String getSensoruuid() {
        return sensoruuid;
    }

    /** Setter for the associated sensorMl uuid
     * @param sensoruuid the sensorML uuid bean value to set
     */
    public void setSensoruuid(String sensoruuid) {
        this.sensoruuid = sensoruuid;
    }

    
    

    

    

    

    

    
}
