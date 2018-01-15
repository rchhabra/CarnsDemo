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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * A data type for Business Object Parameters.
 * 
 * <p>Java class for MessageParamsBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageParamsBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Languages" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}LanguageCode" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CurrCodes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="FiledInCurrency" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}CurrCode"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="Amount" type="{http://www.iata.org/IATA/EDIST/2017.1}NumericStringLength1to19" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "MessageParamsBaseType", propOrder = {
    "languages",
    "currCodes"
})
@XmlSeeAlso({
    ShopProductReqParamsType.class,
    BagChargeReqParamsType.class,
    BagChargeResParamsType.class,
    FareRulesReqParamsType.class,
    SrvListResParamsType.class,
    SrvListReqParamsType.class,
    BagAllowResParamsType.class,
    BagAllowReqParamsType.class,
    FltPriceReqParamsType.class,
    FltPriceResParamsType.class,
    SrvPriceResParamsType.class,
    SrvPriceReqParamsType.class,
    BagListResParamsType.class,
    BagListReqParamsType.class,
    AirShopReqParamsType.class,
    SeatAvailResParamsType.class,
    SeatAvailReqParamsType.class,
    ShopProductResParamsType.class,
    AirShopResParamsType.class
})
public class MessageParamsBaseType {

    @XmlElement(name = "Languages")
    protected MessageParamsBaseType.Languages languages;
    @XmlElement(name = "CurrCodes")
    protected MessageParamsBaseType.CurrCodes currCodes;
    @XmlAttribute(name = "refs")
    @XmlIDREF
    protected List<Object> refs;

    /**
     * Gets the value of the languages property.
     * 
     * @return
     *     possible object is
     *     {@link MessageParamsBaseType.Languages }
     *     
     */
    public MessageParamsBaseType.Languages getLanguages() {
        return languages;
    }

    /**
     * Sets the value of the languages property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageParamsBaseType.Languages }
     *     
     */
    public void setLanguages(MessageParamsBaseType.Languages value) {
        this.languages = value;
    }

    /**
     * Gets the value of the currCodes property.
     * 
     * @return
     *     possible object is
     *     {@link MessageParamsBaseType.CurrCodes }
     *     
     */
    public MessageParamsBaseType.CurrCodes getCurrCodes() {
        return currCodes;
    }

    /**
     * Sets the value of the currCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageParamsBaseType.CurrCodes }
     *     
     */
    public void setCurrCodes(MessageParamsBaseType.CurrCodes value) {
        this.currCodes = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="FiledInCurrency" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}CurrCode"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="Amount" type="{http://www.iata.org/IATA/EDIST/2017.1}NumericStringLength1to19" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "filedInCurrency"
    })
    public static class CurrCodes {

        @XmlElement(name = "FiledInCurrency")
        protected List<MessageParamsBaseType.CurrCodes.FiledInCurrency> filedInCurrency;

        /**
         * Gets the value of the filedInCurrency property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the filedInCurrency property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFiledInCurrency().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MessageParamsBaseType.CurrCodes.FiledInCurrency }
         * 
         * 
         */
        public List<MessageParamsBaseType.CurrCodes.FiledInCurrency> getFiledInCurrency() {
            if (filedInCurrency == null) {
                filedInCurrency = new ArrayList<MessageParamsBaseType.CurrCodes.FiledInCurrency>();
            }
            return this.filedInCurrency;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}CurrCode"/>
         *       &lt;/sequence>
         *       &lt;attribute name="Amount" type="{http://www.iata.org/IATA/EDIST/2017.1}NumericStringLength1to19" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "currCode"
        })
        public static class FiledInCurrency {

            @XmlElement(name = "CurrCode", required = true)
            protected CurrCode currCode;
            @XmlAttribute(name = "Amount")
            protected String amount;

            /**
             * Gets the value of the currCode property.
             * 
             * @return
             *     possible object is
             *     {@link CurrCode }
             *     
             */
            public CurrCode getCurrCode() {
                return currCode;
            }

            /**
             * Sets the value of the currCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link CurrCode }
             *     
             */
            public void setCurrCode(CurrCode value) {
                this.currCode = value;
            }

            /**
             * Gets the value of the amount property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAmount() {
                return amount;
            }

            /**
             * Sets the value of the amount property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAmount(String value) {
                this.amount = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST/2017.1}LanguageCode" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "languageCode"
    })
    public static class Languages {

        @XmlElement(name = "LanguageCode", required = true)
        protected List<LanguageCodeType> languageCode;

        /**
         * Gets the value of the languageCode property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the languageCode property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLanguageCode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link LanguageCodeType }
         * 
         * 
         */
        public List<LanguageCodeType> getLanguageCode() {
            if (languageCode == null) {
                languageCode = new ArrayList<LanguageCodeType>();
            }
            return this.languageCode;
        }

    }

}
