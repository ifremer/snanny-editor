/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import javax.faces.context.FacesContext;

/** Build xml contents from freemarker templates
 * @see <a href="http://freemarker.org/">freemarker</a>
 * @author mlarour
 */
public class XmlBuilder {
    
    private static Configuration cfg = null;
    private static Template templateSensorML = null; 
    private static Template templateSosRequest = null; 
                 
    /** Build the freemarker templates to be ready to use
     * 
     * @param templateDir the freemarker template files directory
     * @throws IOException 
     */
    private static void configure(File templateDir) throws IOException
    {        
        if(cfg == null && templateSensorML == null)
        {
           System.out.println("**** XmlBuilder.configure()");
           cfg = new Configuration(Configuration.VERSION_2_3_21);
           cfg.setDirectoryForTemplateLoading(templateDir);
           cfg.setDefaultEncoding("UTF-8");
           cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
           templateSensorML = cfg.getTemplate("pylos_observation.ftl");
           templateSosRequest = cfg.getTemplate("post_observation.ftl");
        }
    }        
    
    /** Merge data-model with template for SensorML
     * 
     * @param observationModel data-model to merge to observation template
     * @return the SensorML xml content
     * @throws IOException IO error
     * @throws TemplateException merge failed
     */
    public static synchronized String buildSensorML(HashMap observationModel) throws IOException, TemplateException
    {                        
        XmlBuilder.configure(new File(FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/template").getPath()));
        StringWriter sw = new StringWriter();
        templateSensorML.process(observationModel, sw);        
        return(sw.toString());        
    }
    
    /** Merge data-model with template for Sos Request
     * 
     * @param observationModel data-model to merge to observation template
     * @return the xml sos request content 
     * @throws IOException IO error
     * @throws TemplateException Merge failed
     */
    public static synchronized String buildSosRequest(HashMap observationModel) throws IOException, TemplateException
    {                        
        XmlBuilder.configure(new File(FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/template").getPath()));
        StringWriter sw = new StringWriter();
        templateSosRequest.process(observationModel, sw);        
        return(sw.toString());        
    }
}
