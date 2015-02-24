<#assign from = from?datetime>
<#assign to = to?datetime>
<#assign timePosition = .now>
<om:OM_Observation gml:id="top" xmlns:om="http://www.opengis.net/om/2.0"
    xmlns:gco="http://www.isotc211.org/2005/gco"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:swe="http://schemas.opengis.net/sweCommon/2.0/"
    xsi:schemaLocation="http://www.opengis.net/om/2.0 http://schemas.opengis.net/om/2.0/observation.xsd">

    <!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <!-- observation as in the following data file:                                                                             -->
    <!-- /home/coriolis_exp/spool/co01/co0134/co013407/co01340701/monthly/mooring/201410/MO_201410_TS_MO_61277.nc               -->
    <!-- from outside IFREMER (login myocean):                                                                                  -->
    <!--  ftp://vftp1.ifremer.fr/Core/INSITU_GLO_NRT_OBSERVATIONS_013_030/monthly/mooring/201410/MO_201410_TS_MO_61277.nc       -->
    <!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
    <!-- :site_code + :cdm_data_type + 'from' + :time_coverage_start + 'to' + :time_coverage_end -->        
    <gml:description>${siteCode} ${dataType} from ${from?iso_utc} to ${to?iso_utc}</gml:description>
    <gml:identifier codeSpace="uuid">${uuid}</gml:identifier>
    <!-- title or internal id, here id because title is empty -->
    <gml:name>${datasetName}</gml:name>

    <!-- location of the observation, for generic usage -->
    <!--  :geospatial_lat_min = ${latitude}f ;          --> 
    <!--  :geospatial_lat_max = ${latitude}f ;          -->
    <!--  :geospatial_lon_min = ${longitude}f ;         -->
    <!--  :geospatial_lon_max = ${longitude}f ;         -->
    <gml:boundedBy>
        <gml:Envelope>
            <!-- RES_LATMIN, RES_LONMIN -->
            <gml:lowerCorner>${latitude} ${longitude}</gml:lowerCorner>
            <!-- RES_LATMAX, RES_LONMAX -->
            <gml:upperCorner>${latitude} ${longitude}</gml:upperCorner>
        </gml:Envelope>
    </gml:boundedBy>

    <!-- always : hhttp://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement -->
    <om:type xlink:href="http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement"/>

    <!-- temporal extent which the observation relates to, for trajectory start and stop time value (begin<end) -->
    <!-- :time_coverage_start = "${from?iso_utc}" ;                                                             -->
    <!-- :time_coverage_end = "${to?iso_utc}" ;                                                                 -->
    <om:phenomenonTime>
        <gml:TimePeriod gml:id="temporalExtent">
            <!-- RES_DATEDEB -->
            <gml:beginPosition>${from?iso_utc}</gml:beginPosition>
            <!-- RES_DATEFIN -->
            <gml:endPosition>${to?iso_utc}</gml:endPosition>
        </gml:TimePeriod>
    </om:phenomenonTime>
    
    <!-- update time of the observation result       -->
    <!-- time of file in attribute                   -->
    <!--  :date_update = "${timePosition?iso_utc}" ; -->
    <om:resultTime>
        <gml:TimeInstant gml:id="updateDate">
                 
                 <gml:timePosition>${timePosition?iso_utc}</gml:timePosition>
        </gml:TimeInstant>
    </om:resultTime>
    <!-- URI to the sensorML description of the platform from which acquisition has been done -->
    <!-- the sensorML MSUT have been previously created (uploaded or editer)                  -->   
    <om:procedure
        xlink:href="${sensorMLrest}"/>

    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++-->
    <!-- can be described in a local resource shared by observations providing for example Temperature and Salinity -->
    <!-- see for example for weather : http://schemas.opengis.net/om/2.0/examples/swe_weather1.xml                  -->
    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++-->
    <om:observedProperty xlink:href="http://www.ifremer.fr/isi/seadatanet/swe/swe_oceanPhysics.xml"/>


    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++-->
    <!-- should be on sampling feature           	                    -->
    <!-- the sampling feature should have an "intention" attribute          -->
    <!-- here the featureOfInterest is the monitored site (Pylos, E1M3A)    -->
    <!-- referenced in a vocabulary service or better in a WFS              -->
    <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++-->
    <!-- when a site is described it should be edited in a shape file       -->
    <!-- published in WFS                                                   -->
    <om:featureOfInterest xlink:href="to be completed"> </om:featureOfInterest>


    <!-- measurements values                                                                    -->
    <!-- encoding details depend on the sampling feature                                        -->
    <!-- for each sampling feature different encoding option are available                      -->
    <!-- example out of http://schemas.opengis.net/om/2.0/examples/SWEArrayObservation1.xml     -->
    <om:result
        xlink:href="file://${downloadFile}"
        xlink:role="${fileFormat}" xsi:type="gml:ReferenceType"/>


</om:OM_Observation>
