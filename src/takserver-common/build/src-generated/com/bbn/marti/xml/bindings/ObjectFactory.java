//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.bbn.marti.xml.bindings;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.bbn.marti.xml.bindings package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.bbn.marti.xml.bindings
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UserAuthenticationFile }
     * 
     * @return
     *     the new instance of {@link UserAuthenticationFile }
     */
    public UserAuthenticationFile createUserAuthenticationFile() {
        return new UserAuthenticationFile();
    }

    /**
     * Create an instance of {@link UserAuthenticationFile.User }
     * 
     * @return
     *     the new instance of {@link UserAuthenticationFile.User }
     */
    public UserAuthenticationFile.User createUserAuthenticationFileUser() {
        return new UserAuthenticationFile.User();
    }

}
