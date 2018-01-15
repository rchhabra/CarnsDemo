<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<%@ attribute name="b2bTravelOrderDetails" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:choose>
	<c:when test="${b2bTravelOrderDetails}">
		<div class="panel-heading">
			<h3 class="panel-title">
				<spring:theme code="booking.price.summary.header.flight" text="Flight Price Summary" />
			</h3>
		</div>
	</c:when>
	<c:otherwise>
		<div class="panel-heading">
			<h3 class="panel-title">
				<spring:theme code="booking.price.summary.header" text="Price Summary" />
			</h3>
		</div>
	</c:otherwise>
</c:choose>
<div class="panel-body">
	<table class="col-xs-12" id="my-account-table">
		<thead>
			<tr>
				<th></th>
				<th><spring:theme code="booking.price.summary.base.fare.header" text="Base Fare" /></th>
				<th><spring:theme code="booking.price.summary.extras.header" text="Extras" /></th>
				<th><spring:theme code="booking.price.summary.taxes.header" text="Taxes" /></th>
				<th><spring:theme code="booking.price.summary.fees.header" text="Fees" /></th>
				<th><spring:theme code="booking.price.summary.discounts.header" text="Discounts" /></th>
				<th class="price-summary-totals"><spring:theme code="booking.price.summary.total.header" text="Total" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="reservationItem" items="${reservationData.reservationItems}">
				<c:set var="transportOfferings" value="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}"></c:set>
				<c:set var="originSector" value="${transportOfferings[0].sector}" />
				<c:set var="finalSector" value="${transportOfferings[fn:length(transportOfferings) - 1].sector}" />
				<tr>
					<td>${fn:escapeXml(originSector.origin.name)}(${fn:escapeXml(originSector.origin.code)})&nbsp;<span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>&nbsp;${fn:escapeXml(finalSector.destination.name)}(${fn:escapeXml(finalSector.destination.code)})
					</td>
					<td data-title="Fare"><format:price priceData="${reservationItem.reservationPricingInfo.totalFare.basePrice}" /></td>
					<td data-title="Extras"><c:choose>
							<c:when test="${empty reservationItem.reservationPricingInfo.totalFare.extrasPrice}"></c:when>
							<c:otherwise>
								<format:price priceData="${reservationItem.reservationPricingInfo.totalFare.extrasPrice}" />
							</c:otherwise>
						</c:choose></td>
					<td data-title="Taxes"><c:choose>
							<c:when test="${empty reservationItem.reservationPricingInfo.totalFare.taxes}"></c:when>
							<c:otherwise>
								<format:price priceData="${reservationItem.reservationPricingInfo.totalFare.taxPrice}" />
							</c:otherwise>
						</c:choose></td>
					<td data-title="Fees"><c:choose>
							<c:when test="${empty reservationItem.reservationPricingInfo.totalFare.fees}"></c:when>
							<c:otherwise>
								<format:price priceData="${reservationItem.reservationPricingInfo.totalFare.totalFees}" />
							</c:otherwise>
						</c:choose></td>
					<td data-title="Discounts"><c:choose>
							<c:when test="${empty reservationItem.reservationPricingInfo.totalFare.discounts}"></c:when>
							<c:otherwise>
								<c:if test="${reservationItem.reservationPricingInfo.totalFare.discountPrice.value > 0}">
								-
								</c:if>
								<format:price priceData="${reservationItem.reservationPricingInfo.totalFare.discountPrice}" />
							</c:otherwise>
						</c:choose></td>
					<td class="price-summary-totals" data-title="Total"><format:price priceData="${reservationItem.reservationPricingInfo.totalFare.totalPrice}" /></td>
				</tr>
			</c:forEach>
			<c:if test="${not empty reservationData.offerPricingInfos}">
				<tr>
					<td><spring:theme code="booking.price.summary.globalancillaries.header" text="Additional Products" /></td>
					<td data-title="Fare"></td>
					<td data-title="Extras"><format:price priceData="${reservationData.totalFare.extrasPrice}" /></td>
					<td data-title="Taxes"></td>
					<td data-title="Fees"></td>
					<td data-title="Discounts"></td>
					<td class="price-summary-totals" data-title="Total"><format:price priceData="${reservationData.totalFare.extrasPrice}" /></td>
				</tr>
			</c:if>
		</tbody>
		<tfoot>
			<c:forEach var="feeData" items="${reservationData.totalFare.fees}">
				<tr>
					<td>${fn:escapeXml(feeData.name)}</td>
					<td colspan="3" class="hidden-xs"></td>
					<td class="hidden-xs text-center"><format:price priceData="${feeData.price}" /></td>
					<td class="hidden-xs"></td>
					<td class="text-center price-summary-totals"><format:price priceData="${feeData.price}" /></td>
				</tr>
			</c:forEach>
			<tr>
				<td><spring:theme code="booking.price.summary.voucherspromotions.header" text="Discounts" /></td>
				<td colspan="4" class="hidden-xs"></td>
				<td class="hidden-xs text-center"><c:if test="${reservationData.totalFare.discountPrice.value > 0}">
					-
					</c:if> <format:price priceData="${reservationData.totalFare.discountPrice}" /></td>
				<td class="text-center price-summary-totals"><c:if test="${reservationData.totalFare.discountPrice.value > 0}">
					-
					</c:if> <format:price priceData="${reservationData.totalFare.discountPrice}" /></td>
			</tr>
			<tr class="price-summary-finaltotal">
				<td><spring:theme code="booking.price.summary.total.header" text="Total" /></td>
				<td colspan="5" class="hidden-xs"></td>
				<td class="text-center price-summary-totals"><format:price priceData="${reservationData.totalFare.totalPrice}" /></td>
			</tr>
		</tfoot>
	</table>
</div>
