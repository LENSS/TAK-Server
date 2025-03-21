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
 * Connection
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" default="jdbc:postgresql://127.0.0.1:5432/cot" />
 *       <attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" default="martiuser" />
 *       <attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       <attribute name="sslEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       <attribute name="sslMode" type="{http://www.w3.org/2001/XMLSchema}string" default="verify-ca" />
 *       <attribute name="sslCert" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       <attribute name="sslKey" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       <attribute name="sslRootCert" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *       <attribute name="queryBufferMaxMemoryPercentage" type="{http://www.w3.org/2001/XMLSchema}int" default="40" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "connection")
public class Connection
    implements Serializable
{

    private static final long serialVersionUID = 6107951534079953L;
    @XmlAttribute(name = "url")
    protected String url;
    @XmlAttribute(name = "username")
    protected String username;
    @XmlAttribute(name = "password")
    protected String password;
    @XmlAttribute(name = "sslEnabled")
    protected Boolean sslEnabled;
    @XmlAttribute(name = "sslMode")
    protected String sslMode;
    @XmlAttribute(name = "sslCert")
    protected String sslCert;
    @XmlAttribute(name = "sslKey")
    protected String sslKey;
    @XmlAttribute(name = "sslRootCert")
    protected String sslRootCert;
    @XmlAttribute(name = "queryBufferMaxMemoryPercentage")
    protected Integer queryBufferMaxMemoryPercentage;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        if (url == null) {
            return "jdbc:postgresql://127.0.0.1:5432/cot";
        } else {
            return url;
        }
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        if (username == null) {
            return "martiuser";
        } else {
            return username;
        }
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        if (password == null) {
            return "";
        } else {
            return password;
        }
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the sslEnabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSslEnabled() {
        if (sslEnabled == null) {
            return false;
        } else {
            return sslEnabled;
        }
    }

    /**
     * Sets the value of the sslEnabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSslEnabled(Boolean value) {
        this.sslEnabled = value;
    }

    /**
     * Gets the value of the sslMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSslMode() {
        if (sslMode == null) {
            return "verify-ca";
        } else {
            return sslMode;
        }
    }

    /**
     * Sets the value of the sslMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSslMode(String value) {
        this.sslMode = value;
    }

    /**
     * Gets the value of the sslCert property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSslCert() {
        if (sslCert == null) {
            return "";
        } else {
            return sslCert;
        }
    }

    /**
     * Sets the value of the sslCert property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSslCert(String value) {
        this.sslCert = value;
    }

    /**
     * Gets the value of the sslKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSslKey() {
        if (sslKey == null) {
            return "";
        } else {
            return sslKey;
        }
    }

    /**
     * Sets the value of the sslKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSslKey(String value) {
        this.sslKey = value;
    }

    /**
     * Gets the value of the sslRootCert property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSslRootCert() {
        if (sslRootCert == null) {
            return "";
        } else {
            return sslRootCert;
        }
    }

    /**
     * Sets the value of the sslRootCert property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSslRootCert(String value) {
        this.sslRootCert = value;
    }

    /**
     * Gets the value of the queryBufferMaxMemoryPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getQueryBufferMaxMemoryPercentage() {
        if (queryBufferMaxMemoryPercentage == null) {
            return  40;
        } else {
            return queryBufferMaxMemoryPercentage;
        }
    }

    /**
     * Sets the value of the queryBufferMaxMemoryPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQueryBufferMaxMemoryPercentage(Integer value) {
        this.queryBufferMaxMemoryPercentage = value;
    }

}
