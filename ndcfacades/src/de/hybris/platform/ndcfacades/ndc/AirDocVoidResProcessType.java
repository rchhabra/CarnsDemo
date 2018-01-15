//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.07 at 04:46:04 PM GMT 
//


package de.hybris.platform.ndcfacades.ndc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Air Doc Void Response Message Processing.
 * 
 * <p>Java class for AirDocVoidResProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AirDocVoidResProcessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST}MarketingMessages" minOccurs="0"/>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Policies" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.iata.org/IATA/EDIST}ObjAssociationAttrGroup"/>
 *       &lt;attribute name="AlertInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="MarketingInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="WarningInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="NoticeInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AirDocVoidResProcessType", propOrder = {
    "marketingMessages",
    "policies"
})
@XmlSeeAlso({
    AirDocVoidProcessing.class
})
public class AirDocVoidResProcessType {

    @XmlElement(name = "MarketingMessages")
    protected MarketingMessages marketingMessages;
    @XmlElement(name = "Policies")
    protected Policies policies;
    @XmlAttribute(name = "AlertInd")
    protected Boolean alertInd;
    @XmlAttribute(name = "MarketingInd")
    protected Boolean marketingInd;
    @XmlAttribute(name = "WarningInd")
    protected Boolean warningInd;
    @XmlAttribute(name = "NoticeInd")
    protected Boolean noticeInd;
    @XmlAttribute(name = "refs")
    @XmlIDREF
    protected List<Object> refs;

    /**
     * Gets the value of the marketingMessages property.
     * 
     * @return
     *     possible object is
     *     {@link MarketingMessages }
     *     
     */
    public MarketingMessages getMarketingMessages() {
        return marketingMessages;
    }

    /**
     * Sets the value of the marketingMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link MarketingMessages }
     *     
     */
    public void setMarketingMessages(MarketingMessages value) {
        this.marketingMessages = value;
    }

    /**
     * Message Policy information.
     * 
     * @return
     *     possible object is
     *     {@link Policies }
     *     
     */
    public Policies getPolicies() {
        return policies;
    }

    /**
     * Sets the value of the policies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Policies }
     *     
     */
    public void setPolicies(Policies value) {
        this.policies = value;
    }

    /**
     * Gets the value of the alertInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlertInd() {
        return alertInd;
    }

    /**
     * Sets the value of the alertInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAlertInd(Boolean value) {
        this.alertInd = value;
    }

    /**
     * Gets the value of the marketingInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMarketingInd() {
        return marketingInd;
    }

    /**
     * Sets the value of the marketingInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMarketingInd(Boolean value) {
        this.marketingInd = value;
    }

    /**
     * Gets the value of the warningInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isWarningInd() {
        return warningInd;
    }

    /**
     * Sets the value of the warningInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWarningInd(Boolean value) {
        this.warningInd = value;
    }

    /**
     * Gets the value of the noticeInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNoticeInd() {
        return noticeInd;
    }

    /**
     * Sets the value of the noticeInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNoticeInd(Boolean value) {
        this.noticeInd = value;
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
