//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.04 at 02:01:34 PM IST 
//


package de.hybris.platform.ndcfacades.ndc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;


/**
 * NDC Capability Model: FARE
 * 
 *  ============
 * Representation(s)
 *  ============
 * • Core Fare information
 * • Detail Fare information (Fare Component, Fare Rules)
 * 
 *  ============
 * Feature(s)
 *  ============
 * • Fare Indicators for Cat35 Fare, Reissue Pricing, Auto Exchange Pricing
 * • Fare Code (PADIS codeset element 9910 - Fare Qualifier)
 * 
 *  ============
 * Metadata
 *  ============
 * • FareMetadata
 * 
 * 
 * <p>Java class for FareType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FareType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}FareCode"/>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}FareDetail" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.iata.org/IATA/EDIST/2017.1}ObjAssociationAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FareType", propOrder = {
    "fareCode",
    "fareDetail"
})
public class FareType {

    @XmlElement(name = "FareCode", required = true)
    protected String fareCode;
    @XmlElement(name = "FareDetail")
    protected FareDetailType fareDetail;
    @XmlAttribute(name = "refs")
    @XmlIDREF
    protected List<Object> refs;

    /**
     * Gets the value of the fareCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFareCode() {
        return fareCode;
    }

    /**
     * Sets the value of the fareCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFareCode(String value) {
        this.fareCode = value;
    }

    /**
     * Gets the value of the fareDetail property.
     * 
     * @return
     *     possible object is
     *     {@link FareDetailType }
     *     
     */
    public FareDetailType getFareDetail() {
        return fareDetail;
    }

    /**
     * Sets the value of the fareDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FareDetailType }
     *     
     */
    public void setFareDetail(FareDetailType value) {
        this.fareDetail = value;
    }

    /**
     * Gets the value of the refs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getRefs() {
        if (refs == null) {
            refs = new ArrayList<Object>();
        }
        return this.refs;
    }

}
