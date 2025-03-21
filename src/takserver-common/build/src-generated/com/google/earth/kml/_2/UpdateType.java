//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package com.google.earth.kml._2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType name="UpdateType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="targetHref" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         <choice maxOccurs="unbounded">
 *           <element name="Create" type="{http://earth.google.com/kml/2.1}CreateType" minOccurs="0"/>
 *           <element name="Delete" type="{http://earth.google.com/kml/2.1}DeleteType" minOccurs="0"/>
 *           <element name="Change" type="{http://earth.google.com/kml/2.1}ChangeType" minOccurs="0"/>
 *           <element name="Replace" type="{http://earth.google.com/kml/2.1}ReplaceType" minOccurs="0"/>
 *         </choice>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateType", propOrder = {
    "targetHref",
    "createOrDeleteOrChange"
})
public class UpdateType
    implements Serializable
{

    private static final long serialVersionUID = 6107951534079953L;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetHref;
    @XmlElements({
        @XmlElement(name = "Create", type = CreateType.class),
        @XmlElement(name = "Delete", type = DeleteType.class),
        @XmlElement(name = "Change", type = ChangeType.class),
        @XmlElement(name = "Replace", type = ReplaceType.class)
    })
    protected List<Serializable> createOrDeleteOrChange;

    /**
     * Gets the value of the targetHref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetHref() {
        return targetHref;
    }

    /**
     * Sets the value of the targetHref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetHref(String value) {
        this.targetHref = value;
    }

    /**
     * Gets the value of the createOrDeleteOrChange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the createOrDeleteOrChange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCreateOrDeleteOrChange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChangeType }
     * {@link CreateType }
     * {@link DeleteType }
     * {@link ReplaceType }
     * 
     * 
     * @return
     *     The value of the createOrDeleteOrChange property.
     */
    public List<Serializable> getCreateOrDeleteOrChange() {
        if (createOrDeleteOrChange == null) {
            createOrDeleteOrChange = new ArrayList<>();
        }
        return this.createOrDeleteOrChange;
    }

}
