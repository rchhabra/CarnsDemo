//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.04 at 02:01:34 PM IST 
//


package de.hybris.platform.ndcfacades.ndc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * A data type for AGGREGATOR Intermediary Role, Core Representation.
 * 
 * Notes:
 *  1. This is a representation of common (role-agnostic) properties associated with this Actor definition.
 *  2. This type is used as (derived) base type for all Roles associated with this Actor.
 *  3. This type is derived from IntermediaryCoreRepType.
 * 
 * <p>Java class for AggregatorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AggregatorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iata.org/IATA/EDIST/2017.1}IntermediaryCoreRepType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}RoleOfIntermediaryReference" minOccurs="0"/>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}AggregatorID"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ConnectMethod" type="{http://www.iata.org/IATA/EDIST/2017.1}ConnectMethodSimpleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AggregatorType", propOrder = {
    "roleOfIntermediaryReference",
    "aggregatorID"
})
@XmlSeeAlso({
    AggregatorMsgPartyCoreType.class
})
public class AggregatorType
    extends IntermediaryCoreRepType
{

    @XmlElement(name = "RoleOfIntermediaryReference")
    protected RoleOfIntermediaryReferenceType roleOfIntermediaryReference;
    @XmlElement(name = "AggregatorID", required = true)
    protected AggregatorIDType aggregatorID;
    @XmlAttribute(name = "ConnectMethod")
    protected String connectMethod;

    /**
     * Gets the value of the roleOfIntermediaryReference property.
     * 
     * @return
     *     possible object is
     *     {@link RoleOfIntermediaryReferenceType }
     *     
     */
    public RoleOfIntermediaryReferenceType getRoleOfIntermediaryReference() {
        return roleOfIntermediaryReference;
    }

    /**
     * Sets the value of the roleOfIntermediaryReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoleOfIntermediaryReferenceType }
     *     
     */
    public void setRoleOfIntermediaryReference(RoleOfIntermediaryReferenceType value) {
        this.roleOfIntermediaryReference = value;
    }

    /**
     * Gets the value of the aggregatorID property.
     * 
     * @return
     *     possible object is
     *     {@link AggregatorIDType }
     *     
     */
    public AggregatorIDType getAggregatorID() {
        return aggregatorID;
    }

    /**
     * Sets the value of the aggregatorID property.
     * 
     * @param value
     *     allowed object is
     *     {@link AggregatorIDType }
     *     
     */
    public void setAggregatorID(AggregatorIDType value) {
        this.aggregatorID = value;
    }

    /**
     * Gets the value of the connectMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectMethod() {
        return connectMethod;
    }

    /**
     * Sets the value of the connectMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectMethod(String value) {
        this.connectMethod = value;
    }

}
