//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.07 at 04:46:04 PM GMT 
//


package de.hybris.platform.ndcfacades.ndc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST}PointOfSale" minOccurs="0"/>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Document"/>
 *         &lt;element ref="{http://www.iata.org/IATA/EDIST}Party"/>
 *         &lt;element name="Query">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}TicketDocQuantity"/>
 *                   &lt;element name="TicketDocInfo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.iata.org/IATA/EDIST}TravelerInfo"/>
 *                             &lt;element ref="{http://www.iata.org/IATA/EDIST}BookingReference" minOccurs="0"/>
 *                             &lt;element name="OrderReference" type="{http://www.iata.org/IATA/EDIST}CouponOrderKeyType" minOccurs="0"/>
 *                             &lt;element name="Promotions" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Promotion" type="{http://www.iata.org/IATA/EDIST}PromotionType" maxOccurs="unbounded"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Payments" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="Payment" type="{http://www.iata.org/IATA/EDIST}AcceptedPaymentFormType" maxOccurs="unbounded" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Commission" type="{http://www.iata.org/IATA/EDIST}CommissionType" minOccurs="0"/>
 *                             &lt;element ref="{http://www.iata.org/IATA/EDIST}FlightSegmentReferences" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="DataLists" type="{http://www.iata.org/IATA/EDIST}DataListType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.iata.org/IATA/EDIST}IATA_PayloadStdAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "pointOfSale",
    "document",
    "party",
    "query"
})
@XmlRootElement(name = "AirDocIssueRQ")
public class AirDocIssueRQ {

    @XmlElement(name = "PointOfSale")
    protected PointOfSaleType pointOfSale;
    @XmlElement(name = "Document", required = true)
    protected MsgDocumentType document;
    @XmlElement(name = "Party", required = true)
    protected MsgPartiesType party;
    @XmlElement(name = "Query", required = true)
    protected AirDocIssueRQ.Query query;
    @XmlAttribute(name = "EchoToken")
    protected String echoToken;
    @XmlAttribute(name = "TimeStamp")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;
    @XmlAttribute(name = "Target")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String target;
    @XmlAttribute(name = "Version", required = true)
    protected String version;
    @XmlAttribute(name = "TransactionIdentifier")
    protected String transactionIdentifier;
    @XmlAttribute(name = "SequenceNmbr")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sequenceNmbr;
    @XmlAttribute(name = "TransactionStatusCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String transactionStatusCode;
    @XmlAttribute(name = "RetransmissionIndicator")
    protected Boolean retransmissionIndicator;
    @XmlAttribute(name = "CorrelationID")
    protected String correlationID;
    @XmlAttribute(name = "AsynchronousAllowedInd")
    protected Boolean asynchronousAllowedInd;
    @XmlAttribute(name = "AltLangID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String altLangID;
    @XmlAttribute(name = "PrimaryLangID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String primaryLangID;

    /**
     * Gets the value of the pointOfSale property.
     * 
     * @return
     *     possible object is
     *     {@link PointOfSaleType }
     *     
     */
    public PointOfSaleType getPointOfSale() {
        return pointOfSale;
    }

    /**
     * Sets the value of the pointOfSale property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointOfSaleType }
     *     
     */
    public void setPointOfSale(PointOfSaleType value) {
        this.pointOfSale = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link MsgDocumentType }
     *     
     */
    public MsgDocumentType getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsgDocumentType }
     *     
     */
    public void setDocument(MsgDocumentType value) {
        this.document = value;
    }

    /**
     * Gets the value of the party property.
     * 
     * @return
     *     possible object is
     *     {@link MsgPartiesType }
     *     
     */
    public MsgPartiesType getParty() {
        return party;
    }

    /**
     * Sets the value of the party property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsgPartiesType }
     *     
     */
    public void setParty(MsgPartiesType value) {
        this.party = value;
    }

    /**
     * Gets the value of the query property.
     * 
     * @return
     *     possible object is
     *     {@link AirDocIssueRQ.Query }
     *     
     */
    public AirDocIssueRQ.Query getQuery() {
        return query;
    }

    /**
     * Sets the value of the query property.
     * 
     * @param value
     *     allowed object is
     *     {@link AirDocIssueRQ.Query }
     *     
     */
    public void setQuery(AirDocIssueRQ.Query value) {
        this.query = value;
    }

    /**
     * Gets the value of the echoToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEchoToken() {
        return echoToken;
    }

    /**
     * Sets the value of the echoToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEchoToken(String value) {
        this.echoToken = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        if (target == null) {
            return "Production";
        } else {
            return target;
        }
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the transactionIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    /**
     * Sets the value of the transactionIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionIdentifier(String value) {
        this.transactionIdentifier = value;
    }

    /**
     * Gets the value of the sequenceNmbr property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNmbr() {
        return sequenceNmbr;
    }

    /**
     * Sets the value of the sequenceNmbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNmbr(BigInteger value) {
        this.sequenceNmbr = value;
    }

    /**
     * Gets the value of the transactionStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionStatusCode() {
        return transactionStatusCode;
    }

    /**
     * Sets the value of the transactionStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionStatusCode(String value) {
        this.transactionStatusCode = value;
    }

    /**
     * Gets the value of the retransmissionIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRetransmissionIndicator() {
        return retransmissionIndicator;
    }

    /**
     * Sets the value of the retransmissionIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRetransmissionIndicator(Boolean value) {
        this.retransmissionIndicator = value;
    }

    /**
     * Gets the value of the correlationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelationID() {
        return correlationID;
    }

    /**
     * Sets the value of the correlationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelationID(String value) {
        this.correlationID = value;
    }

    /**
     * Gets the value of the asynchronousAllowedInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAsynchronousAllowedInd() {
        return asynchronousAllowedInd;
    }

    /**
     * Sets the value of the asynchronousAllowedInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAsynchronousAllowedInd(Boolean value) {
        this.asynchronousAllowedInd = value;
    }

    /**
     * Gets the value of the altLangID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAltLangID() {
        return altLangID;
    }

    /**
     * Sets the value of the altLangID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAltLangID(String value) {
        this.altLangID = value;
    }

    /**
     * Gets the value of the primaryLangID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryLangID() {
        return primaryLangID;
    }

    /**
     * Sets the value of the primaryLangID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryLangID(String value) {
        this.primaryLangID = value;
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
     *         &lt;element ref="{http://www.iata.org/IATA/EDIST}TicketDocQuantity"/>
     *         &lt;element name="TicketDocInfo" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}TravelerInfo"/>
     *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}BookingReference" minOccurs="0"/>
     *                   &lt;element name="OrderReference" type="{http://www.iata.org/IATA/EDIST}CouponOrderKeyType" minOccurs="0"/>
     *                   &lt;element name="Promotions" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Promotion" type="{http://www.iata.org/IATA/EDIST}PromotionType" maxOccurs="unbounded"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Payments" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="Payment" type="{http://www.iata.org/IATA/EDIST}AcceptedPaymentFormType" maxOccurs="unbounded" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Commission" type="{http://www.iata.org/IATA/EDIST}CommissionType" minOccurs="0"/>
     *                   &lt;element ref="{http://www.iata.org/IATA/EDIST}FlightSegmentReferences" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="DataLists" type="{http://www.iata.org/IATA/EDIST}DataListType" minOccurs="0"/>
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
        "ticketDocQuantity",
        "ticketDocInfo",
        "dataLists"
    })
    public static class Query {

        @XmlElement(name = "TicketDocQuantity", required = true)
        protected BigInteger ticketDocQuantity;
        @XmlElement(name = "TicketDocInfo", required = true)
        protected List<AirDocIssueRQ.Query.TicketDocInfo> ticketDocInfo;
        @XmlElement(name = "DataLists")
        protected DataListType dataLists;

        /**
         * Gets the value of the ticketDocQuantity property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getTicketDocQuantity() {
            return ticketDocQuantity;
        }

        /**
         * Sets the value of the ticketDocQuantity property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setTicketDocQuantity(BigInteger value) {
            this.ticketDocQuantity = value;
        }

        /**
         * Gets the value of the ticketDocInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the ticketDocInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTicketDocInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AirDocIssueRQ.Query.TicketDocInfo }
         * 
         * 
         */
        public List<AirDocIssueRQ.Query.TicketDocInfo> getTicketDocInfo() {
            if (ticketDocInfo == null) {
                ticketDocInfo = new ArrayList<AirDocIssueRQ.Query.TicketDocInfo>();
            }
            return this.ticketDocInfo;
        }

        /**
         * Gets the value of the dataLists property.
         * 
         * @return
         *     possible object is
         *     {@link DataListType }
         *     
         */
        public DataListType getDataLists() {
            return dataLists;
        }

        /**
         * Sets the value of the dataLists property.
         * 
         * @param value
         *     allowed object is
         *     {@link DataListType }
         *     
         */
        public void setDataLists(DataListType value) {
            this.dataLists = value;
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
         *         &lt;element ref="{http://www.iata.org/IATA/EDIST}TravelerInfo"/>
         *         &lt;element ref="{http://www.iata.org/IATA/EDIST}BookingReference" minOccurs="0"/>
         *         &lt;element name="OrderReference" type="{http://www.iata.org/IATA/EDIST}CouponOrderKeyType" minOccurs="0"/>
         *         &lt;element name="Promotions" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Promotion" type="{http://www.iata.org/IATA/EDIST}PromotionType" maxOccurs="unbounded"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="Payments" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="Payment" type="{http://www.iata.org/IATA/EDIST}AcceptedPaymentFormType" maxOccurs="unbounded" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="Commission" type="{http://www.iata.org/IATA/EDIST}CommissionType" minOccurs="0"/>
         *         &lt;element ref="{http://www.iata.org/IATA/EDIST}FlightSegmentReferences" minOccurs="0"/>
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
            "travelerInfo",
            "bookingReference",
            "orderReference",
            "promotions",
            "payments",
            "commission",
            "flightSegmentReferences"
        })
        public static class TicketDocInfo {

            @XmlElement(name = "TravelerInfo", required = true)
            protected TravelerInfo travelerInfo;
            @XmlElement(name = "BookingReference")
            protected BookingReference bookingReference;
            @XmlElement(name = "OrderReference")
            protected CouponOrderKeyType orderReference;
            @XmlElement(name = "Promotions")
            protected AirDocIssueRQ.Query.TicketDocInfo.Promotions promotions;
            @XmlElement(name = "Payments")
            protected AirDocIssueRQ.Query.TicketDocInfo.Payments payments;
            @XmlElement(name = "Commission")
            protected CommissionType commission;
            @XmlElement(name = "FlightSegmentReferences")
            protected FlightSegmentReferences flightSegmentReferences;

            /**
             * Traveler information, including name, passenger type and contact.
             * 
             * 
             * @return
             *     possible object is
             *     {@link TravelerInfo }
             *     
             */
            public TravelerInfo getTravelerInfo() {
                return travelerInfo;
            }

            /**
             * Sets the value of the travelerInfo property.
             * 
             * @param value
             *     allowed object is
             *     {@link TravelerInfo }
             *     
             */
            public void setTravelerInfo(TravelerInfo value) {
                this.travelerInfo = value;
            }

            /**
             * Booking Reference information.
             * 
             * 
             * @return
             *     possible object is
             *     {@link BookingReference }
             *     
             */
            public BookingReference getBookingReference() {
                return bookingReference;
            }

            /**
             * Sets the value of the bookingReference property.
             * 
             * @param value
             *     allowed object is
             *     {@link BookingReference }
             *     
             */
            public void setBookingReference(BookingReference value) {
                this.bookingReference = value;
            }

            /**
             * Gets the value of the orderReference property.
             * 
             * @return
             *     possible object is
             *     {@link CouponOrderKeyType }
             *     
             */
            public CouponOrderKeyType getOrderReference() {
                return orderReference;
            }

            /**
             * Sets the value of the orderReference property.
             * 
             * @param value
             *     allowed object is
             *     {@link CouponOrderKeyType }
             *     
             */
            public void setOrderReference(CouponOrderKeyType value) {
                this.orderReference = value;
            }

            /**
             * Gets the value of the promotions property.
             * 
             * @return
             *     possible object is
             *     {@link AirDocIssueRQ.Query.TicketDocInfo.Promotions }
             *     
             */
            public AirDocIssueRQ.Query.TicketDocInfo.Promotions getPromotions() {
                return promotions;
            }

            /**
             * Sets the value of the promotions property.
             * 
             * @param value
             *     allowed object is
             *     {@link AirDocIssueRQ.Query.TicketDocInfo.Promotions }
             *     
             */
            public void setPromotions(AirDocIssueRQ.Query.TicketDocInfo.Promotions value) {
                this.promotions = value;
            }

            /**
             * Gets the value of the payments property.
             * 
             * @return
             *     possible object is
             *     {@link AirDocIssueRQ.Query.TicketDocInfo.Payments }
             *     
             */
            public AirDocIssueRQ.Query.TicketDocInfo.Payments getPayments() {
                return payments;
            }

            /**
             * Sets the value of the payments property.
             * 
             * @param value
             *     allowed object is
             *     {@link AirDocIssueRQ.Query.TicketDocInfo.Payments }
             *     
             */
            public void setPayments(AirDocIssueRQ.Query.TicketDocInfo.Payments value) {
                this.payments = value;
            }

            /**
             * Gets the value of the commission property.
             * 
             * @return
             *     possible object is
             *     {@link CommissionType }
             *     
             */
            public CommissionType getCommission() {
                return commission;
            }

            /**
             * Sets the value of the commission property.
             * 
             * @param value
             *     allowed object is
             *     {@link CommissionType }
             *     
             */
            public void setCommission(CommissionType value) {
                this.commission = value;
            }

            /**
             * Gets the value of the flightSegmentReferences property.
             * 
             * @return
             *     possible object is
             *     {@link FlightSegmentReferences }
             *     
             */
            public FlightSegmentReferences getFlightSegmentReferences() {
                return flightSegmentReferences;
            }

            /**
             * Sets the value of the flightSegmentReferences property.
             * 
             * @param value
             *     allowed object is
             *     {@link FlightSegmentReferences }
             *     
             */
            public void setFlightSegmentReferences(FlightSegmentReferences value) {
                this.flightSegmentReferences = value;
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
             *         &lt;element name="Payment" type="{http://www.iata.org/IATA/EDIST}AcceptedPaymentFormType" maxOccurs="unbounded" minOccurs="0"/>
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
                "payment"
            })
            public static class Payments {

                @XmlElement(name = "Payment")
                protected List<AcceptedPaymentFormType> payment;

                /**
                 * Gets the value of the payment property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the payment property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getPayment().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link AcceptedPaymentFormType }
                 * 
                 * 
                 */
                public List<AcceptedPaymentFormType> getPayment() {
                    if (payment == null) {
                        payment = new ArrayList<AcceptedPaymentFormType>();
                    }
                    return this.payment;
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
             *         &lt;element name="Promotion" type="{http://www.iata.org/IATA/EDIST}PromotionType" maxOccurs="unbounded"/>
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
                "promotion"
            })
            public static class Promotions {

                @XmlElement(name = "Promotion", required = true)
                protected List<PromotionType> promotion;

                /**
                 * Gets the value of the promotion property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the promotion property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getPromotion().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link PromotionType }
                 * 
                 * 
                 */
                public List<PromotionType> getPromotion() {
                    if (promotion == null) {
                        promotion = new ArrayList<PromotionType>();
                    }
                    return this.promotion;
                }

            }

        }

    }

}
