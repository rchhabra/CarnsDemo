<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="itineraryPricingInfo" required="true" type="de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData"%>
<%@ attribute name="pricedItinerary" required="true" type="de.hybris.platform.commercefacades.travel.PricedItineraryData"%>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="pricedItineraryDateTime" required="true" type="java.util.Date"%>
<%@ taglib prefix="fareselection" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/fareselection" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty itineraryPricingInfo}">
	<label class="${itineraryPricingInfo.promotional ? 'promotional-price' : ''} ${itineraryPricingInfo.selected ? 'selected' : ''}">
		<input type="radio" name="${fn:escapeXml(name)}" id="y_frSelect-${fn:escapeXml(pricedItinerary.id)}-${fn:escapeXml(itineraryPricingInfo.bundleType)}" ${itineraryPricingInfo.available ? '' : 'disabled'} ${itineraryPricingInfo.selected ? 'checked' : ''} value="${fn:escapeXml(pricedItineraryDateTime.time)}" class="y_fareResultSelect" />
		<c:if test="${not empty dealBundleTemplateId}">
			<span class="selected">
				<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
			</span>
			<span class="select">
				<spring:theme code="text.accommodation.details.accommodation.select.price" text="select" />
			</span>
		</c:if>
		<span class="class-desc">${fn:escapeXml(itineraryPricingInfo.bundleTypeName)}</span>
		<c:choose>
			<c:when test="${not empty priceDisplayPassengerType}">
				<c:choose>
					<c:when test="${itineraryPricingInfo.available}">
						<c:forEach var="ptcFareBreakdownData" items="${itineraryPricingInfo.ptcFareBreakdownDatas}" varStatus="ptcIdx">
							<c:if test="${ptcFareBreakdownData.passengerTypeQuantity.passengerType.code == priceDisplayPassengerType}">
								<span class="price-desc">${not empty ptcFareBreakdownData.passengerFare.perPax.formattedValue ? fn:escapeXml(ptcFareBreakdownData.passengerFare.perPax.formattedValue) : '&mdash;'}</span>
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<span class="price-desc">${'&mdash;'}</span>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="${not empty itineraryPricingInfo.totalFare.priceDifference}">
				<span class="price-desc">${fn:escapeXml(itineraryPricingInfo.totalFare.priceDifference.formattedValue)}</span>
			</c:when>
			<c:otherwise>
				<span class="price-desc">${itineraryPricingInfo.available ? fn:escapeXml(itineraryPricingInfo.totalFare.totalPrice.formattedValue) : '&mdash;'}</span>
			</c:otherwise>
		</c:choose>
		<cms:pageSlot var="feature" position="AsmContent">
			<span class="asm-remaining-seats">
				<c:if test="${itineraryPricingInfo.available}">
					<spring:theme code="fareselection.remaining.seats" arguments="${fn:escapeXml(remainingSeats[pricedItinerary.id][itineraryPricingInfo.bundleType])}" />
				</c:if>
			</span>
		</cms:pageSlot>

		<fareselection:addBundleToCartForm itineraryPricingInfo="${itineraryPricingInfo}" pricedItinerary="${pricedItinerary}" />

	</label>
</c:if>
