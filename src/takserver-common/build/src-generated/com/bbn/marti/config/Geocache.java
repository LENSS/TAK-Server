//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.bbn.marti.config;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="enable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       <attribute name="connectId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="maxTilesPerGoal" type="{http://www.w3.org/2001/XMLSchema}int" default="1000" />
 *       <attribute name="maxSizeOfCacheInMb" type="{http://www.w3.org/2001/XMLSchema}int" default="1000" />
 *       <attribute name="cacheDir" type="{http://www.w3.org/2001/XMLSchema}string" default="/tmp/geocache" />
 *       <attribute name="numThreads" type="{http://www.w3.org/2001/XMLSchema}int" default="10" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "geocache")
public class Geocache
    implements Serializable
{

    private static final long serialVersionUID = 6107951534079953L;
    @XmlAttribute(name = "enable")
    protected Boolean enable;
    @XmlAttribute(name = "connectId")
    protected String connectId;
    @XmlAttribute(name = "maxTilesPerGoal")
    protected Integer maxTilesPerGoal;
    @XmlAttribute(name = "maxSizeOfCacheInMb")
    protected Integer maxSizeOfCacheInMb;
    @XmlAttribute(name = "cacheDir")
    protected String cacheDir;
    @XmlAttribute(name = "numThreads")
    protected Integer numThreads;

    /**
     * Gets the value of the enable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnable() {
        if (enable == null) {
            return false;
        } else {
            return enable;
        }
    }

    /**
     * Sets the value of the enable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnable(Boolean value) {
        this.enable = value;
    }

    /**
     * Gets the value of the connectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectId() {
        return connectId;
    }

    /**
     * Sets the value of the connectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectId(String value) {
        this.connectId = value;
    }

    /**
     * Gets the value of the maxTilesPerGoal property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMaxTilesPerGoal() {
        if (maxTilesPerGoal == null) {
            return  1000;
        } else {
            return maxTilesPerGoal;
        }
    }

    /**
     * Sets the value of the maxTilesPerGoal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxTilesPerGoal(Integer value) {
        this.maxTilesPerGoal = value;
    }

    /**
     * Gets the value of the maxSizeOfCacheInMb property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMaxSizeOfCacheInMb() {
        if (maxSizeOfCacheInMb == null) {
            return  1000;
        } else {
            return maxSizeOfCacheInMb;
        }
    }

    /**
     * Sets the value of the maxSizeOfCacheInMb property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxSizeOfCacheInMb(Integer value) {
        this.maxSizeOfCacheInMb = value;
    }

    /**
     * Gets the value of the cacheDir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCacheDir() {
        if (cacheDir == null) {
            return "/tmp/geocache";
        } else {
            return cacheDir;
        }
    }

    /**
     * Sets the value of the cacheDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCacheDir(String value) {
        this.cacheDir = value;
    }

    /**
     * Gets the value of the numThreads property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getNumThreads() {
        if (numThreads == null) {
            return  10;
        } else {
            return numThreads;
        }
    }

    /**
     * Sets the value of the numThreads property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumThreads(Integer value) {
        this.numThreads = value;
    }

}
