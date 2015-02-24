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

/**
 *
 * @author mlarour
 */
@ManagedBean
@ViewScoped
public class ObservationBean implements Serializable {

    private static final String insertObservationEndUrlString = "sos?service=SOS&version=2.0&request=insertObservation";
    private static final String restEndUrlBeforeUuidString = "record/";

    /**
     * @return the insertObservationUrlString
     */
    public String getInsertObservationUrlString() {
        return insertObservationUrlString;
    }

    /**
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
    
    public ObservationBean() {
    }
                
    /**  
     * 
     * @param event
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
           
    /* sample code for data model
      sensorMLmodel.put("siteCode", "Big Joe");
      Map latest = new HashMap();
      sensorMLmodel.put("latestProduct", latest);
      latest.put("url", "products/greenmouse.html");
      latest.put("name", "green mouse");
    */
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
    /* sample code for data model
      sensorMLmodel.put("siteCode", "Big Joe");
      Map latest = new HashMap();
      sensorMLmodel.put("latestProduct", latest);
      latest.put("url", "products/greenmouse.html");
      latest.put("name", "green mouse");
    */
    /** getter for the observation data model
     * 
     * @return the observation data model
     */
    private HashMap getSosRequestModel(String sensorML)
    {
        HashMap sosRequestModel = new HashMap();                       
        sosRequestModel.put("sensorML",sensorML);                
        return(sosRequestModel);
    }
    
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
    
    private void buildXmls() throws IOException, TemplateException
    {
        oem = XmlBuilder.buildSensorML(getSensorMLmodel());
        sosRequest = XmlBuilder.buildSosRequest(getSosRequestModel(oem));
    }
    
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
    
    /**
     * @return the oem
     */
    public String getOem() {
        
        return oem;
    }

    /**
     * @param oem the oem to set
     */
    public void setOem(String oem) {
        this.oem = oem;
    }
    

    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the siteCode
     */
    public String getSiteCode() {
        return siteCode;
    }

    /**
     * @param siteCode the siteCode to set
     */
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * @return the fileFormat
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * @param fileFormat the fileFormat to set
     */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the datasetName
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     * @param datasetName the datasetName to set
     */
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

     

    /**
     * @return the fileName
     */
    public String getFileName() {        
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {        
        this.fileName = fileName;
    }

    /**
     * @return the sosRequest
     */
    public String getSosRequest() {
        return sosRequest;
    }

    /**
     * @param sosRequest the sosRequest to set
     */
    public void setSosRequest(String sosRequest) {
        this.sosRequest = sosRequest;
    }

    /**
     * @return the fileNameRequired
     */
    public String getFileNameRequired() {
        return fileNameRequired;
    }

    /**
     * @param fileNameRequired the fileNameRequired to set
     */
    public void setFileNameRequired(String fileNameRequired) {
        this.fileNameRequired = fileNameRequired;
    }

    /**
     * @return the configurable
     */
    public boolean isConfigurable() {
        return configurable;
    }

    /**
     * @param configurable the configurable to set
     */
    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    /**
     * @return the sensorMLuuid
     */
    public String getSensoruuid() {
        return sensoruuid;
    }

    /**
     * @param sensoruuid the sensorMLuuid to set
     */
    public void setSensoruuid(String sensoruuid) {
        this.sensoruuid = sensoruuid;
    }

    
    

    

    

    

    

    
}
