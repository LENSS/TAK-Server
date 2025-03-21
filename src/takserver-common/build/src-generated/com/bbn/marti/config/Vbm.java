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
 *       <attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       <attribute name="disableSASharing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       <attribute name="disableChatSharing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       <attribute name="returnCopsWithPublicMissions" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       <attribute name="ismUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="ismConnectTimeoutSeconds" type="{http://www.w3.org/2001/XMLSchema}long" default="-1" />
 *       <attribute name="ismReadTimeoutSeconds" type="{http://www.w3.org/2001/XMLSchema}long" default="-1" />
 *       <attribute name="ismStrictEnforcing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       <attribute name="networkClassification" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "vbm")
public class Vbm
    implements Serializable
{

    private static final long serialVersionUID = 6107951534079953L;
    @XmlAttribute(name = "enabled")
    protected Boolean enabled;
    @XmlAttribute(name = "disableSASharing")
    protected Boolean disableSASharing;
    @XmlAttribute(name = "disableChatSharing")
    protected Boolean disableChatSharing;
    @XmlAttribute(name = "returnCopsWithPublicMissions")
    protected Boolean returnCopsWithPublicMissions;
    @XmlAttribute(name = "ismUrl")
    protected String ismUrl;
    @XmlAttribute(name = "ismConnectTimeoutSeconds")
    protected Long ismConnectTimeoutSeconds;
    @XmlAttribute(name = "ismReadTimeoutSeconds")
    protected Long ismReadTimeoutSeconds;
    @XmlAttribute(name = "ismStrictEnforcing")
    protected Boolean ismStrictEnforcing;
    @XmlAttribute(name = "networkClassification")
    protected String networkClassification;

    /**
     * Gets the value of the enabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnabled() {
        if (enabled == null) {
            return false;
        } else {
            return enabled;
        }
    }

    /**
     * Sets the value of the enabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the disableSASharing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDisableSASharing() {
        if (disableSASharing == null) {
            return true;
        } else {
            return disableSASharing;
        }
    }

    /**
     * Sets the value of the disableSASharing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableSASharing(Boolean value) {
        this.disableSASharing = value;
    }

    /**
     * Gets the value of the disableChatSharing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDisableChatSharing() {
        if (disableChatSharing == null) {
            return false;
        } else {
            return disableChatSharing;
        }
    }

    /**
     * Sets the value of the disableChatSharing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDisableChatSharing(Boolean value) {
        this.disableChatSharing = value;
    }

    /**
     * Gets the value of the returnCopsWithPublicMissions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReturnCopsWithPublicMissions() {
        if (returnCopsWithPublicMissions == null) {
            return true;
        } else {
            return returnCopsWithPublicMissions;
        }
    }

    /**
     * Sets the value of the returnCopsWithPublicMissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturnCopsWithPublicMissions(Boolean value) {
        this.returnCopsWithPublicMissions = value;
    }

    /**
     * Gets the value of the ismUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsmUrl() {
        return ismUrl;
    }

    /**
     * Sets the value of the ismUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsmUrl(String value) {
        this.ismUrl = value;
    }

    /**
     * Gets the value of the ismConnectTimeoutSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getIsmConnectTimeoutSeconds() {
        if (ismConnectTimeoutSeconds == null) {
            return -1L;
        } else {
            return ismConnectTimeoutSeconds;
        }
    }

    /**
     * Sets the value of the ismConnectTimeoutSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIsmConnectTimeoutSeconds(Long value) {
        this.ismConnectTimeoutSeconds = value;
    }

    /**
     * Gets the value of the ismReadTimeoutSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getIsmReadTimeoutSeconds() {
        if (ismReadTimeoutSeconds == null) {
            return -1L;
        } else {
            return ismReadTimeoutSeconds;
        }
    }

    /**
     * Sets the value of the ismReadTimeoutSeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIsmReadTimeoutSeconds(Long value) {
        this.ismReadTimeoutSeconds = value;
    }

    /**
     * Gets the value of the ismStrictEnforcing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsmStrictEnforcing() {
        if (ismStrictEnforcing == null) {
            return false;
        } else {
            return ismStrictEnforcing;
        }
    }

    /**
     * Sets the value of the ismStrictEnforcing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsmStrictEnforcing(Boolean value) {
        this.ismStrictEnforcing = value;
    }

    /**
     * Gets the value of the networkClassification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetworkClassification() {
        return networkClassification;
    }

    /**
     * Sets the value of the networkClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetworkClassification(String value) {
        this.networkClassification = value;
    }

}
