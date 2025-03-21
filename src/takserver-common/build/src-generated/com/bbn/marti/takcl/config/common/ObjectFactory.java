//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.bbn.marti.takcl.config.common;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.bbn.marti.takcl.config.common package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _TemporaryDirectory_QNAME = new QName("http://bbn.com/marti/takcl/config/common", "TemporaryDirectory");
    private static final QName _FallbackTemporaryDirectory_QNAME = new QName("http://bbn.com/marti/takcl/config/common", "FallbackTemporaryDirectory");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.bbn.marti.takcl.config.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RunnableTAKServerConfig }
     * 
     * @return
     *     the new instance of {@link RunnableTAKServerConfig }
     */
    public RunnableTAKServerConfig createRunnableTAKServerConfig() {
        return new RunnableTAKServerConfig();
    }

    /**
     * Create an instance of {@link ConnectableTAKServerConfig }
     * 
     * @return
     *     the new instance of {@link ConnectableTAKServerConfig }
     */
    public ConnectableTAKServerConfig createConnectableTAKServerConfig() {
        return new ConnectableTAKServerConfig();
    }

    /**
     * Create an instance of {@link Input }
     * 
     * @return
     *     the new instance of {@link Input }
     */
    public Input createInput() {
        return new Input();
    }

    /**
     * Create an instance of {@link TAKCLTestSourceGenerationConfig }
     * 
     * @return
     *     the new instance of {@link TAKCLTestSourceGenerationConfig }
     */
    public TAKCLTestSourceGenerationConfig createTAKCLTestSourceGenerationConfig() {
        return new TAKCLTestSourceGenerationConfig();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://bbn.com/marti/takcl/config/common", name = "TemporaryDirectory")
    public JAXBElement<String> createTemporaryDirectory(String value) {
        return new JAXBElement<>(_TemporaryDirectory_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://bbn.com/marti/takcl/config/common", name = "FallbackTemporaryDirectory")
    public JAXBElement<String> createFallbackTemporaryDirectory(String value) {
        return new JAXBElement<>(_FallbackTemporaryDirectory_QNAME, String.class, null, value);
    }

}
