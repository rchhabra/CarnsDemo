<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="y_reservationTotalsComponent reservationTotals">
    <input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_reservationTotalsComponentId" />
    <section class="hidden-xs">
        <h2 class="text-center h4"><spring:theme code="reservation.component.title" text="Your Reservation"/></h2>
    </section>
    <div class="visible-xs-block sidebar-mobile-content-section clearfix">
        <div class="text-center">
            <button data-toggle="collapse" data-target=".reservation.summary" class="glyphicon glyphicon-align-justify y_fareSlide"></button>
        </div>
    </div>
    <div>
        <c:if test="${not empty destinationLocationImage}">
            <img src="${destinationLocationImage.url}" class="img-responsive hidden-xs" alt='<spring:theme code="reservation.location.image.text" text="Your dream destination"/>'>
        </c:if>
    </div>
    <div class="y_spinner spinner reservation hidden"></div>
    <div id="grand-total-wrap">
        <div class="col-xs-12 hidden-xs">
        	<c:if test="${not empty reservationCode}">
	            <h3 class="h5"><spring:theme code="reservation.orderreference"/>&nbsp;${fn:escapeXml(reservationCode)}</h3>
        	</c:if>
        </div>
        <dl class="reservation-summary">
            <dt class="col-xs-6 col-sm-12 grand-total">
                <c:choose>
                    <c:when test="${reservationTotal.value < 0 }">
                        <spring:theme code="reservation.total.refund.label" text="Total to refund"/>
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="reservation.total.pay.label" text="Total"/>
                    </c:otherwise>
                </c:choose>
            </dt>
            <dd class="col-xs-6 col-sm-12 grand-price"><format:price priceData="${reservationTotal}"/></dd>
            <dd class="col-xs-12 hidden-xs">
                <spring:theme code="reservation.total.include.text" text="Include: discounts, taxes and fees"/>
            </dd>
        </dl>
    </div>
    <div class="form-group clearfix hidden-xs">
        <div class="col-xs-offset-2 col-xs-8">
            <div class="row">
                <button class="y_seeFullReservationBtn btn btn-primary col-xs-12" ${empty reservationCode ? 'disabled' : ''}><spring:theme code="reservation.full.link.title" text="See full reservation"/></button>
            </div>
        </div>
    </div>
</div>
