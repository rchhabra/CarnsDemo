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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * FLIGHT Order Offer Item.
 * 
 * <p>Java class for FlightItemOfferType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlightItemOfferType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pricing" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.iata.org/IATA/EDIST}FlightPriceType">
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Flight" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}SegmentKey" minOccurs="0"/>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}Departure"/>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}Arrival"/>
 *                   &lt;element name="MarketingCarrier" type="{http://www.iata.org/IATA/EDIST}MarketingCarrierFlightType"/>
 *                   &lt;element name="OperatingCarrier" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://www.iata.org/IATA/EDIST}OperatingCarrierFlightType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.iata.org/IATA/EDIST}Disclosures" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}Equipment" minOccurs="0"/>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}CabinType" minOccurs="0"/>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}ClassOfService" minOccurs="0"/>
 *                   &lt;element name="Details" type="{http://www.iata.org/IATA/EDIST}FlightDetailType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://www.iata.org/IATA/EDIST}ObjAssociationAttrGroup"/>
 *                 &lt;attribute name="PricingInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="ConnectionInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="E_TicketInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="TicketlessInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AircraftEquipment" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}AircraftCode"/>
 *                   &lt;element name="AirlineEquipCode" type="{http://www.iata.org/IATA/EDIST}ContextSimpleType" minOccurs="0"/>
 *                   &lt;element name="Name" type="{http://www.iata.org/IATA/EDIST}ProperNameSimpleType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="FareDetail" type="{http://www.iata.org/IATA/EDIST}FareDetailType" minOccurs="0"/>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST}TotalJourney" minOccurs="0"/>
 *         &lt;element name="Passengers" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}PassengerReference"/>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}GroupReference"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Details" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="InventoryGuarantee" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="InvGuaranteeID" type="{http://www.iata.org/IATA/EDIST}UniqueStringID_SimpleType"/>
 *                             &lt;element ref="{http://www.iata.org/IATA/EDIST}InventoryGuaranteeTimeLimits"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="WaitListInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.iata.org/IATA/EDIST}ObjAssociationAttrGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlightItemOfferType", propOrder = {
    "pricing",
    "flight",
    "aircraftEquipment",
    "fareDetail",
    "totalJourney",
    "passengers",
    "details"
})
public class FlightItemOfferType {

    @XmlElement(name = "Pricing")
    protected FlightItemOfferType.Pricing pricing;
    @XmlElement(name = "Flight", required = true)
    protected List<FlightItemOfferType.Flight> flight;
    @XmlElement(name = "AircraftEquipment")
    protected FlightItemOfferType.AircraftEquipment aircraftEquipment;
    @XmlElement(name = "FareDetail")
    protected FareDetailType fareDetail;
    @XmlElement(name = "TotalJourney")
    protected TotalJourneyType totalJourney;
    @XmlElement(name = "Passengers")
    protected FlightItemOfferType.Passengers passengers;
    @XmlElement(name = "Details")
    protected FlightItemOfferType.Details details;
    @XmlAttribute(name = "refs")
    @XmlIDREF
    protected List<Object> refs;

    /**
     * Gets the value of the pricing property.
     * 
     * @return
     *     possible object is
     *     {@link FlightItemOfferType.Pricing }
     *     
     */
    public FlightItemOfferType.Pricing getPricing() {
        return pricing;
    }

    /**
     * Sets the value of the pricing property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightItemOfferType.Pricing }
     *     
     */
    public void setPricing(FlightItemOfferType.Pricing value) {
        this.pricing = value;
    }

    /**
     * Gets the value of the flight property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flight property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FlightItemOfferType.Flight }
     * 
     * 
     */
    public List<FlightItemOfferType.Flight> getFlight() {
        if (flight == null) {
            flight = new ArrayList<FlightItemOfferType.Flight>();
        }
        return this.flight;
    }

    /**
     * Gets the value of the aircraftEquipment property.
     * 
     * @return
     *     possible object is
     *     {@link FlightItemOfferType.AircraftEquipment }
     *     
     */
    public FlightItemOfferType.AircraftEquipment getAircraftEquipment() {
        return aircraftEquipment;
    }

    /**
     * Sets the value of the aircraftEquipment property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightItemOfferType.AircraftEquipment }
     *     
     */
    public void setAircraftEquipment(FlightItemOfferType.AircraftEquipment value) {
        this.aircraftEquipment = value;
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
     * Gets the value of the totalJourney property.
     * 
     * @return
     *     possible object is
     *     {@link TotalJourneyType }
     *     
     */
    public TotalJourneyType getTotalJourney() {
        return totalJourney;
    }

    /**
     * Sets the value of the totalJourney property.
     * 
     * @param value
     *     allowed object is
     *     {@link TotalJourneyType }
     *     
     */
    public void setTotalJourney(TotalJourneyType value) {
        this.totalJourney = value;
    }

    /**
     * Gets the value of the passengers property.
     * 
     * @return
     *     possible object is
     *     {@link FlightItemOfferType.Passengers }
     *     
     */
    public FlightItemOfferType.Passengers getPassengers() {
        return passengers;
    }

    /**
     * Sets the value of the passengers property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightItemOfferType.Passengers }
     *     
     */
    public void setPassengers(FlightItemOfferType.Passengers value) {
        this.passengers = value;
    }

    /**
     * Gets the value of the details property.
     * 
     * @return
     *     possible object is
     *     {@link FlightItemOfferType.Details }
     *     
     */
    public FlightItemOfferType.Details getDetails() {
        return details;
    }

    /**
     * Sets the value of the details property.
     * 
     * @param value
     *     allowed object is
     *     {@link FlightItemOfferType.Details }
     *     
     */
    public void setDetails(FlightItemOfferType.Details value) {
        this.details = value;
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
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}AircraftCode"/>
     *         &lt;element name="AirlineEquipCode" type="{http://www.iata.org/IATA/EDIST}ContextSimpleType" minOccurs="0"/>
     *         &lt;element name="Name" type="{http://www.iata.org/IATA/EDIST}ProperNameSimpleType" minOccurs="0"/>
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
        "aircraftCode",
        "airlineEquipCode",
        "name"
    })
    public static class AircraftEquipment {

        @XmlElement(name = "AircraftCode", required = true)
        protected AircraftCode aircraftCode;
        @XmlElement(name = "AirlineEquipCode")
        protected String airlineEquipCode;
        @XmlElement(name = "Name")
        protected String name;

        /**
         * 
         *                       Aircraft Equipment Code. Example: 737 Encoding Scheme: IATA/ A4A Aircraft Equipment Code
         *                     
         * 
         * @return
         *     possible object is
         *     {@link AircraftCode }
         *     
         */
        public AircraftCode getAircraftCode() {
            return aircraftCode;
        }

        /**
         * Sets the value of the aircraftCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link AircraftCode }
         *     
         */
        public void setAircraftCode(AircraftCode value) {
            this.aircraftCode = value;
        }

        /**
         * Gets the value of the airlineEquipCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAirlineEquipCode() {
            return airlineEquipCode;
        }

        /**
         * Sets the value of the airlineEquipCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAirlineEquipCode(String value) {
            this.airlineEquipCode = value;
        }

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
     *         &lt;element name="InventoryGuarantee" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="InvGuaranteeID" type="{http://www.iata.org/IATA/EDIST}UniqueStringID_SimpleType"/>
     *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}InventoryGuaranteeTimeLimits"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="WaitListInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "inventoryGuarantee"
    })
    public static class Details {

        @XmlElement(name = "InventoryGuarantee")
        protected FlightItemOfferType.Details.InventoryGuarantee inventoryGuarantee;
        @XmlAttribute(name = "WaitListInd")
        protected Boolean waitListInd;

        /**
         * Gets the value of the inventoryGuarantee property.
         * 
         * @return
         *     possible object is
         *     {@link FlightItemOfferType.Details.InventoryGuarantee }
         *     
         */
        public FlightItemOfferType.Details.InventoryGuarantee getInventoryGuarantee() {
            return inventoryGuarantee;
        }

        /**
         * Sets the value of the inventoryGuarantee property.
         * 
         * @param value
         *     allowed object is
         *     {@link FlightItemOfferType.Details.InventoryGuarantee }
         *     
         */
        public void setInventoryGuarantee(FlightItemOfferType.Details.InventoryGuarantee value) {
            this.inventoryGuarantee = value;
        }

        /**
         * Gets the value of the waitListInd property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isWaitListInd() {
            return waitListInd;
        }

        /**
         * Sets the value of the waitListInd property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setWaitListInd(Boolean value) {
            this.waitListInd = value;
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
         *         &lt;element name="InvGuaranteeID" type="{http://www.iata.org/IATA/EDIST}UniqueStringID_SimpleType"/>
         *         &lt;element ref="{http://www.iata.org/IATA/EDIST}InventoryGuaranteeTimeLimits"/>
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
            "invGuaranteeID",
            "inventoryGuaranteeTimeLimits"
        })
        public static class InventoryGuarantee {

            @XmlElement(name = "InvGuaranteeID", required = true)
            protected String invGuaranteeID;
            @XmlElement(name = "InventoryGuaranteeTimeLimits", required = true)
            protected InventoryGuaranteeTimeLimits inventoryGuaranteeTimeLimits;

            /**
             * Gets the value of the invGuaranteeID property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInvGuaranteeID() {
                return invGuaranteeID;
            }

            /**
             * Sets the value of the invGuaranteeID property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInvGuaranteeID(String value) {
                this.invGuaranteeID = value;
            }

            /**
             * Gets the value of the inventoryGuaranteeTimeLimits property.
             * 
             * @return
             *     possible object is
             *     {@link InventoryGuaranteeTimeLimits }
             *     
             */
            public InventoryGuaranteeTimeLimits getInventoryGuaranteeTimeLimits() {
                return inventoryGuaranteeTimeLimits;
            }

            /**
             * Sets the value of the inventoryGuaranteeTimeLimits property.
             * 
             * @param value
             *     allowed object is
             *     {@link InventoryGuaranteeTimeLimits }
             *     
             */
            public void setInventoryGuaranteeTimeLimits(InventoryGuaranteeTimeLimits value) {
                this.inventoryGuaranteeTimeLimits = value;
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
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}SegmentKey" minOccurs="0"/>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Departure"/>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Arrival"/>
     *         &lt;element name="MarketingCarrier" type="{http://www.iata.org/IATA/EDIST}MarketingCarrierFlightType"/>
     *         &lt;element name="OperatingCarrier" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://www.iata.org/IATA/EDIST}OperatingCarrierFlightType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}Disclosures" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/extension>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Equipment" minOccurs="0"/>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}CabinType" minOccurs="0"/>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}ClassOfService" minOccurs="0"/>
     *         &lt;element name="Details" type="{http://www.iata.org/IATA/EDIST}FlightDetailType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attGroup ref="{http://www.iata.org/IATA/EDIST}ObjAssociationAttrGroup"/>
     *       &lt;attribute name="PricingInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="ConnectionInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="E_TicketInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="TicketlessInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "segmentKey",
        "departure",
        "arrival",
        "marketingCarrier",
        "operatingCarrier",
        "equipment",
        "cabinType",
        "classOfService",
        "details"
    })
    public static class Flight {

        @XmlElement(name = "SegmentKey")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String segmentKey;
        @XmlElement(name = "Departure", required = true)
        protected Departure departure;
        @XmlElement(name = "Arrival", required = true)
        protected FlightArrivalType arrival;
        @XmlElement(name = "MarketingCarrier", required = true)
        protected MarketingCarrierFlightType marketingCarrier;
        @XmlElement(name = "OperatingCarrier")
        protected FlightItemOfferType.Flight.OperatingCarrier operatingCarrier;
        @XmlElement(name = "Equipment")
        protected AircraftSummaryType equipment;
        @XmlElement(name = "CabinType")
        protected CabinType cabinType;
        @XmlElement(name = "ClassOfService")
        protected FlightCOSCoreType classOfService;
        @XmlElement(name = "Details")
        protected FlightDetailType details;
        @XmlAttribute(name = "PricingInd")
        protected Boolean pricingInd;
        @XmlAttribute(name = "ConnectionInd")
        protected Boolean connectionInd;
        @XmlAttribute(name = "E_TicketInd")
        protected Boolean eTicketInd;
        @XmlAttribute(name = "TicketlessInd")
        protected Boolean ticketlessInd;
        @XmlAttribute(name = "refs")
        @XmlIDREF
        protected List<Object> refs;

        /**
         * Gets the value of the segmentKey property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSegmentKey() {
            return segmentKey;
        }

        /**
         * Sets the value of the segmentKey property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSegmentKey(String value) {
            this.segmentKey = value;
        }

        /**
         * Gets the value of the departure property.
         * 
         * @return
         *     possible object is
         *     {@link Departure }
         *     
         */
        public Departure getDeparture() {
            return departure;
        }

        /**
         * Sets the value of the departure property.
         * 
         * @param value
         *     allowed object is
         *     {@link Departure }
         *     
         */
        public void setDeparture(Departure value) {
            this.departure = value;
        }

        /**
         * Gets the value of the arrival property.
         * 
         * @return
         *     possible object is
         *     {@link FlightArrivalType }
         *     
         */
        public FlightArrivalType getArrival() {
            return arrival;
        }

        /**
         * Sets the value of the arrival property.
         * 
         * @param value
         *     allowed object is
         *     {@link FlightArrivalType }
         *     
         */
        public void setArrival(FlightArrivalType value) {
            this.arrival = value;
        }

        /**
         * Gets the value of the marketingCarrier property.
         * 
         * @return
         *     possible object is
         *     {@link MarketingCarrierFlightType }
         *     
         */
        public MarketingCarrierFlightType getMarketingCarrier() {
            return marketingCarrier;
        }

        /**
         * Sets the value of the marketingCarrier property.
         * 
         * @param value
         *     allowed object is
         *     {@link MarketingCarrierFlightType }
         *     
         */
        public void setMarketingCarrier(MarketingCarrierFlightType value) {
            this.marketingCarrier = value;
        }

        /**
         * Gets the value of the operatingCarrier property.
         * 
         * @return
         *     possible object is
         *     {@link FlightItemOfferType.Flight.OperatingCarrier }
         *     
         */
        public FlightItemOfferType.Flight.OperatingCarrier getOperatingCarrier() {
            return operatingCarrier;
        }

        /**
         * Sets the value of the operatingCarrier property.
         * 
         * @param value
         *     allowed object is
         *     {@link FlightItemOfferType.Flight.OperatingCarrier }
         *     
         */
        public void setOperatingCarrier(FlightItemOfferType.Flight.OperatingCarrier value) {
            this.operatingCarrier = value;
        }

        /**
         * Gets the value of the equipment property.
         * 
         * @return
         *     possible object is
         *     {@link AircraftSummaryType }
         *     
         */
        public AircraftSummaryType getEquipment() {
            return equipment;
        }

        /**
         * Sets the value of the equipment property.
         * 
         * @param value
         *     allowed object is
         *     {@link AircraftSummaryType }
         *     
         */
        public void setEquipment(AircraftSummaryType value) {
            this.equipment = value;
        }

        /**
         * Gets the value of the cabinType property.
         * 
         * @return
         *     possible object is
         *     {@link CabinType }
         *     
         */
        public CabinType getCabinType() {
            return cabinType;
        }

        /**
         * Sets the value of the cabinType property.
         * 
         * @param value
         *     allowed object is
         *     {@link CabinType }
         *     
         */
        public void setCabinType(CabinType value) {
            this.cabinType = value;
        }

        /**
         * Gets the value of the classOfService property.
         * 
         * @return
         *     possible object is
         *     {@link FlightCOSCoreType }
         *     
         */
        public FlightCOSCoreType getClassOfService() {
            return classOfService;
        }

        /**
         * Sets the value of the classOfService property.
         * 
         * @param value
         *     allowed object is
         *     {@link FlightCOSCoreType }
         *     
         */
        public void setClassOfService(FlightCOSCoreType value) {
            this.classOfService = value;
        }

        /**
         * Gets the value of the details property.
         * 
         * @return
         *     possible object is
         *     {@link FlightDetailType }
         *     
         */
        public FlightDetailType getDetails() {
            return details;
        }

        /**
         * Sets the value of the details property.
         * 
         * @param value
         *     allowed object is
         *     {@link FlightDetailType }
         *     
         */
        public void setDetails(FlightDetailType value) {
            this.details = value;
        }

        /**
         * Gets the value of the pricingInd property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isPricingInd() {
            return pricingInd;
        }

        /**
         * Sets the value of the pricingInd property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setPricingInd(Boolean value) {
            this.pricingInd = value;
        }

        /**
         * Gets the value of the connectionInd property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isConnectionInd() {
            return connectionInd;
        }

        /**
         * Sets the value of the connectionInd property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setConnectionInd(Boolean value) {
            this.connectionInd = value;
        }

        /**
         * Gets the value of the eTicketInd property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isETicketInd() {
            return eTicketInd;
        }

        /**
         * Sets the value of the eTicketInd property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setETicketInd(Boolean value) {
            this.eTicketInd = value;
        }

        /**
         * Gets the value of the ticketlessInd property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isTicketlessInd() {
            return ticketlessInd;
        }

        /**
         * Sets the value of the ticketlessInd property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setTicketlessInd(Boolean value) {
            this.ticketlessInd = value;
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
         *     &lt;extension base="{http://www.iata.org/IATA/EDIST}OperatingCarrierFlightType">
         *       &lt;sequence>
         *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Disclosures" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "disclosures"
        })
        public static class OperatingCarrier
            extends OperatingCarrierFlightType
        {

            @XmlElement(name = "Disclosures")
            protected Disclosures disclosures;

            /**
             * Gets the value of the disclosures property.
             * 
             * @return
             *     possible object is
             *     {@link Disclosures }
             *     
             */
            public Disclosures getDisclosures() {
                return disclosures;
            }

            /**
             * Sets the value of the disclosures property.
             * 
             * @param value
             *     allowed object is
             *     {@link Disclosures }
             *     
             */
            public void setDisclosures(Disclosures value) {
                this.disclosures = value;
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
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}PassengerReference"/>
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}GroupReference"/>
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
        "passengerReference",
        "groupReference"
    })
    public static class Passengers {

        @XmlList
        @XmlElement(name = "PassengerReference", required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREFS")
        protected List<Object> passengerReference;
        @XmlElement(name = "GroupReference", required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object groupReference;

        /**
         * 
         *                       Association to multiple Passenger instance(s). Example: pax1 pax2
         *                     Gets the value of the passengerReference property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the passengerReference property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPassengerReference().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getPassengerReference() {
            if (passengerReference == null) {
                passengerReference = new ArrayList<Object>();
            }
            return this.passengerReference;
        }

        /**
         * Gets the value of the groupReference property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getGroupReference() {
            return groupReference;
        }

        /**
         * Sets the value of the groupReference property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setGroupReference(Object value) {
            this.groupReference = value;
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
     *     &lt;extension base="{http://www.iata.org/IATA/EDIST}FlightPriceType">
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Pricing
        extends FlightPriceType
    {


    }

}
