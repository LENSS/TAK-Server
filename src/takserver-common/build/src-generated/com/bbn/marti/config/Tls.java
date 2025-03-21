//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.bbn.marti.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *       <sequence maxOccurs="unbounded" minOccurs="0">
 *         <choice maxOccurs="unbounded" minOccurs="0">
 *           <element name="crl">
 *             <complexType>
 *               <complexContent>
 *                 <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   <attribute name="_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                   <attribute name="crlFile" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 </restriction>
 *               </complexContent>
 *             </complexType>
 *           </element>
 *         </choice>
 *       </sequence>
 *       <attribute name="keystore" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="keystoreFile" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="keystorePass" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="truststore" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="truststoreFile" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="truststorePass" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="context" type="{http://www.w3.org/2001/XMLSchema}string" default="TLSv1.2" />
 *       <attribute name="ciphers" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="keymanager" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="enableOCSP" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       <attribute name="responderUrl" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "crl"
})
@XmlRootElement(name = "tls")
public class Tls
    implements Serializable
{

    private static final long serialVersionUID = 6107951534079953L;
    protected List<Tls.Crl> crl;
    @XmlAttribute(name = "keystore", required = true)
    protected String keystore;
    @XmlAttribute(name = "keystoreFile", required = true)
    protected String keystoreFile;
    @XmlAttribute(name = "keystorePass", required = true)
    protected String keystorePass;
    @XmlAttribute(name = "truststore", required = true)
    protected String truststore;
    @XmlAttribute(name = "truststoreFile", required = true)
    protected String truststoreFile;
    @XmlAttribute(name = "truststorePass", required = true)
    protected String truststorePass;
    @XmlAttribute(name = "context")
    protected String context;
    @XmlAttribute(name = "ciphers")
    protected String ciphers;
    @XmlAttribute(name = "keymanager", required = true)
    protected String keymanager;
    @XmlAttribute(name = "enableOCSP")
    protected Boolean enableOCSP;
    @XmlAttribute(name = "responderUrl")
    protected String responderUrl;

    /**
     * Gets the value of the crl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the crl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tls.Crl }
     * 
     * 
     * @return
     *     The value of the crl property.
     */
    public List<Tls.Crl> getCrl() {
        if (crl == null) {
            crl = new ArrayList<>();
        }
        return this.crl;
    }

    /**
     * Gets the value of the keystore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeystore() {
        return keystore;
    }

    /**
     * Sets the value of the keystore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeystore(String value) {
        this.keystore = value;
    }

    /**
     * Gets the value of the keystoreFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeystoreFile() {
        return keystoreFile;
    }

    /**
     * Sets the value of the keystoreFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeystoreFile(String value) {
        this.keystoreFile = value;
    }

    /**
     * Gets the value of the keystorePass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeystorePass() {
        return keystorePass;
    }

    /**
     * Sets the value of the keystorePass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeystorePass(String value) {
        this.keystorePass = value;
    }

    /**
     * Gets the value of the truststore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTruststore() {
        return truststore;
    }

    /**
     * Sets the value of the truststore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTruststore(String value) {
        this.truststore = value;
    }

    /**
     * Gets the value of the truststoreFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTruststoreFile() {
        return truststoreFile;
    }

    /**
     * Sets the value of the truststoreFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTruststoreFile(String value) {
        this.truststoreFile = value;
    }

    /**
     * Gets the value of the truststorePass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTruststorePass() {
        return truststorePass;
    }

    /**
     * Sets the value of the truststorePass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTruststorePass(String value) {
        this.truststorePass = value;
    }

    /**
     * Gets the value of the context property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContext() {
        if (context == null) {
            return "TLSv1.2";
        } else {
            return context;
        }
    }

    /**
     * Sets the value of the context property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContext(String value) {
        this.context = value;
    }

    /**
     * Gets the value of the ciphers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiphers() {
        return ciphers;
    }

    /**
     * Sets the value of the ciphers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiphers(String value) {
        this.ciphers = value;
    }

    /**
     * Gets the value of the keymanager property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeymanager() {
        return keymanager;
    }

    /**
     * Sets the value of the keymanager property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeymanager(String value) {
        this.keymanager = value;
    }

    /**
     * Gets the value of the enableOCSP property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnableOCSP() {
        if (enableOCSP == null) {
            return false;
        } else {
            return enableOCSP;
        }
    }

    /**
     * Sets the value of the enableOCSP property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnableOCSP(Boolean value) {
        this.enableOCSP = value;
    }

    /**
     * Gets the value of the responderUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponderUrl() {
        return responderUrl;
    }

    /**
     * Sets the value of the responderUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponderUrl(String value) {
        this.responderUrl = value;
    }


    /**
     * Certificate Revocation Lists
     * 
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <attribute name="_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       <attribute name="crlFile" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Crl
        implements Serializable
    {

        private static final long serialVersionUID = 6107951534079953L;
        @XmlAttribute(name = "_name", required = true)
        protected String name;
        @XmlAttribute(name = "crlFile", required = true)
        protected String crlFile;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the crlFile property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCrlFile() {
            return crlFile;
        }

        /**
         * Sets the value of the crlFile property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCrlFile(String value) {
            this.crlFile = value;
        }

    }

}
