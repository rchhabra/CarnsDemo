<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template" %>
<%@ taglib prefix="fareselection" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/fareselection" %>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation" %>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<template:page pageTitle="${pageTitle}">
    <progress:bookingProgressBar stage="flights" bookingJourney="${bookingJourney}"/>
    <c:url var="ancillaryPageUrl" value="/ancillary" />
    <div class="container y_fareSelectionPage">
        <h2 class="h2">
            <spring:theme code="fareselection.title" text="Please choose your flight"/>
        </h2>
        <div class="fare-selection-wrap y_fareSelectionSection clearfix">
            <div class="col-xs-12 col-sm-9 fare-selection-body y_nonItineraryContentArea">
                <%-- Fare Finder container --%>
                <div class="col-xs-12">
                    <div class="row">
                        <div class="modify-search panel panel-primary col-xs-12 col-sm-9">
                            <div class="row">
                                <cms:pageSlot position="LeftContent" var="feature" element="div">
                                    <cms:component component="${feature}"/>
                                </cms:pageSlot>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="fare-table-wrapper clearfix">
                    <div class="col-xs-12">
                        <div class="fare-table-inner-wrap">
                            <c:choose>
                                <c:when test="${not empty fareSelection && not empty fareSelection.pricedItineraries}">
                                    <input type="hidden" id="y_tripType" value="${fn:escapeXml(tripType)}" />
                                    <%-- Outbound offerings table --%>
                                    <div id="y_outbound_start" class="with-icon heading-booking-trip-blue">
                                        <h2 class="h4">
                                            <spring:theme code="fareselection.outbound"/>
                                        </h2>
                                        <h3 class="h5"><spring:theme code="fare.selection.inbound.outbound.label" arguments="${fn:escapeXml(origin)}, ${fn:escapeXml(destination)}"/></h3>
                                    </div>
                                    <div id="y_outbound" class="y_fareResultTabWrapper tab-wrapper clearfix">
                                        <fareselection:offeringList fareSelection="${fareSelection}" refNumber="${outboundRefNumber}"/>
                                    </div>
                                    <c:if test="${tripType == 'RETURN'}">
                                        <%-- Return offerings table --%>
                                        <div id="y_inbound_start" class="with-icon heading-booking-trip-blue">
                                            <h2 class="h4">
                                                <spring:theme code="fareselection.inbound"/>
                                            </h2>
                                            <h3 class="h5"><spring:theme code="fare.selection.inbound.outbound.label" arguments="${fn:escapeXml(destination)}, ${fn:escapeXml(origin)}"/></h3>
                                        </div>
                                        <div id="y_inbound" class="y_fareResultTabWrapper tab-wrapper clearfix">
                                            <fareselection:offeringList fareSelection="${fareSelection}" refNumber="${inboundRefNumber}"/>
                                        </div>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-danger" role="alert">
                                        <p>
                                            <spring:theme code="text.fareselection.noresults" text="No results found"/>
                                        </p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                                <%-- Save Customer Search --%>
                            <sec:authorize access="hasAnyRole('ROLE_CUSTOMERGROUP')">
                                <div class="alert alert-warning" role="alert" style="display: none;">
                                    <p id="y_messageSaveSearch"/>
                                </div>
                                <div class="row bottom-row">
                                    <div class="col-xs-5 col-sm-4 col-xs-offset-7 col-sm-offset-8">
                                        <button class="btn btn-primary btn-block" id="y_saveSearch">
                                            <spring:theme code="text.account.mysavedsearch.button.Save"/>
                                        </button>
                                    </div>
                                </div>
                            </sec:authorize>
                                <%-- Disclaimer for Prices displayed --%>
                            <c:if test="${not empty priceDisplayPassengerType and not empty fareSelection.pricedItineraries[0].itineraryPricingInfos[0]}">
                                <div class="alert alert-warning" role="alert">
                                    <p>
                                        <c:forEach var="ptcFareBreakdownData"
                                                   items="${fareSelection.pricedItineraries[0].itineraryPricingInfos[0].ptcFareBreakdownDatas}"
                                                   varStatus="ptcIdx">
                                            <c:if test="${priceDisplayPassengerType==ptcFareBreakdownData.passengerTypeQuantity.passengerType.code}">
                                                <spring:theme code="text.fare.selection.disclaimer"
                                                                arguments="${fn:escapeXml(ptcFareBreakdownData.passengerTypeQuantity.passengerType.name)}"/>
                                            </c:if>
                                        </c:forEach>
                                    </p>
                                </div>
                            </c:if>
                                <%-- About the fare --%>
                            <div class="panel panel-default about-fare">
                                <div >
                                    <h2 class="panel-title h4 panel-title title-collapse">
                                        <a role="button" data-toggle="collapse" aria-expanded="true" aria-controls="about-fare" data-target="#about-fare" class="panel-heading panel-header-link collapsable collapsed">
                                            <spring:theme code="fareselection.aboutfare.title" text="About Your Fare"/>
                                        </a>
                                    </h2>
                                </div>
                                <div class="panel-body collapse" id="about-fare">
                                    <cms:pageSlot position="BottomContent" var="feature">
                                        <cms:component component="${feature}"/>
                                    </cms:pageSlot>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-xs-12 col-sm-3">
                <aside id="sidebar" class="reservation y_reservationSideBar">
                    <div class="main-wrap">
                        <cms:pageSlot position="Reservation" var="feature" element="div">
                            <cms:component component="${feature}"/>
                        </cms:pageSlot>
                        <div class="row">
                            <div class="visible-xs-block col-xs-offset-1 col-xs-10">
                                <a href="${fn:escapeXml(ancillaryPageUrl)}" class="btn btn-secondary col-xs-12 y_fareSelectionContinueButton">
                                    <spring:theme code="reservation.btn.continue" text="Continue"/>
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="promotions hidden-xs">
                        <cms:pageSlot position="SideContent" var="feature" element="section">
                            <cms:component component="${feature}"/>
                        </cms:pageSlot>
                    </div>
                </aside>
            </div>
        </div>
    </div>
    <div class="y_continueBar continue-bar hidden-xs">
        <div class="container">
            <div class="row">
                <div class="col-xs-offset-9 col-xs-3">
                    <a href="${fn:escapeXml(ancillaryPageUrl)}" class="btn btn-secondary col-xs-12 y_fareSelectionContinueButton">
                        <spring:theme code="reservation.btn.continue" text="Continue"/>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <reservation:fullReservationOverlay/>
    <fareselection:addBundleToCartValidationModal/>
    <fareselection:fareSelectionValidationModal/>
</template:page>
