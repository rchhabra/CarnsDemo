<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<%@ attribute name="travellerCode" required="true" type="java.lang.String"%>
<%@ attribute name="routeOriginDestinationCode" required="true" type="java.lang.String"%>

		
<c:forEach var="originDestinationOfferInfo" items="${reservationItem.reservationPricingInfo.originDestinationOfferInfos}">
  <c:set var="originDestinationCode" value=""/>
    <c:if test="${fn:length(originDestinationOfferInfo.transportOfferings) gt 1}">
       <c:forEach items="${originDestinationOfferInfo.transportOfferings}" var="transportOffering" varStatus="idx">
			<c:if test="${idx.first}">
			<c:set var="originCode" value="${transportOffering.sector.origin.code}" />
			</c:if>
			<c:if test="${idx.last}">
			<c:set var="originDestinationCode" value="${originCode} - ${transportOffering.sector.destination.code}" />
			</c:if>
	   </c:forEach>
    </c:if>
          
   <c:if test="${routeOriginDestinationCode eq originDestinationCode}">
	   	<c:set var="showRouteCode" value="true" />
	    <ul class="list-unstyled ${fn:length(reservationItem.reservationItinerary.travellers) gt 9 ? 'col-xs-6' : ''}">
			<c:forEach var="offerPricingInfo" items="${originDestinationOfferInfo.offerPricingInfos}">
				<c:forEach var="travellerBreakdown" items="${offerPricingInfo.travellerBreakdowns}">
					<c:if test="${travellerBreakdown.traveller.label eq travellerCode}">
						<c:if test="${showRouteCode}">
							<li><strong>${fn:escapeXml(routeOriginDestinationCode)}</strong></li>
							<c:set var="showRouteCode" value="false" />
						</c:if>
						<c:set var="includedProductText">
							<spring:theme code="text.page.managemybooking.bookingdetails.included.product" text="This product is included in your bundle." />
						</c:set>
						<c:choose>
							<c:when test="${offerPricingInfo.bundleIndicator == 0}">
								<li>${fn:escapeXml(travellerBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerPricingInfo.product.name)}</li>
							</c:when>
							<c:otherwise>
								<li tabindex="0" class="included-product" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(includedProductText)}">
									${fn:escapeXml(travellerBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerPricingInfo.product.name)}
									&nbsp;<i class="glyphicon glyphicon-info-sign"></i>
								</li>
							</c:otherwise>
						</c:choose>
					</c:if>
				</c:forEach>
			</c:forEach>
		</ul>
  </c:if>
</c:forEach>
