//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.google.earth.kml._2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemIconType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="ItemIconType">
 *   <complexContent>
 *     <extension base="{http://earth.google.com/kml/2.1}ObjectType">
 *       <sequence>
 *         <element name="state" type="{http://earth.google.com/kml/2.1}itemIconStateType" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="href" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemIconType", propOrder = {
    "state",
    "href"
})
public class ItemIconType
    extends ObjectType
    implements Serializable
{

    private static final long serialVersionUID = 6107951534079953L;
    @XmlElementRef(name = "state", namespace = "http://earth.google.com/kml/2.1", type = JAXBElement.class, required = false)
    protected List<JAXBElement<List<ItemIconStateEnum>>> state;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String href;

    /**
     * Gets the value of the state property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the state property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getState().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link ItemIconStateEnum }{@code >}{@code >}
     * 
     * 
     * @return
     *     The value of the state property.
     */
    public List<JAXBElement<List<ItemIconStateEnum>>> getState() {
        if (state == null) {
            state = new ArrayList<>();
        }
        return this.state;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

}
