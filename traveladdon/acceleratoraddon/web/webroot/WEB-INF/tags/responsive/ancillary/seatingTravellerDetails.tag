<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="passengerNum" required="false" type="java.lang.String"%>
<%@ attribute name="tabId" required="true" type="java.lang.String"%>
<%@ attribute name="tabName" required="true" type="java.lang.String"%>
<%@ attribute name="passengerType" required="true" type="java.lang.String"%>
<%@ attribute name="travellerData" required="true" type="de.hybris.platform.commercefacades.travel.TravellerData"%>
<%@ attribute name="transportOfferingCode" required="true" type="java.lang.String"%>
<%@ attribute name="originDestinationRefNumber" required="true" type="java.lang.String"%>
<%@ attribute name="travelRoute" required="true" type="java.lang.String"%>
<%@ attribute name="checked" required="true" type="java.lang.Boolean"%>
<%@ attribute name="disabled" required="true" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<li class="row inv-seat col-xs-12 y_seatInfoData" data-accommodationuid="" data-previousselectedaccommodationuid=""
	data-addremoveaction="" data-transportofferingcode="${transportOfferingCode}" data-travellercode="${travellerData.label}"
	data-travelleruid="${travellerData.simpleUID}" data-origindestinationrefno="${originDestinationRefNumber}"
	data-travelroute="${travelRoute}">
	<div id="${fn:escapeXml(tabId)}" class="${fn:escapeXml(tabName)}" data-transportofferingnumber="${transportOfferingCode}" data-passengernum="${fn:escapeXml(passengerNum)}" data-passengertype="${fn:escapeXml(travellerData.label)}">
		<div class="y_seatIcon seat-icon ${checked ? 'active' : ''} ${disabled ? 'disabled' : '' }">
			<span>${fn:escapeXml(passengerType)}</span>
			<span class="y_seatIconSeatNumber y_seatIconIsEmpty y_selectedSeatNumber hidden-sm hidden-md hidden-lg">--</span>
		</div>
		<div class="passenger-group hidden-xs">
			<span class="passenger-name">${fn:escapeXml(passengerType)}</span>
			<span class="passenger-seat-info">
				<span class="passenger-seat-number">
					<spring:theme code="text.ancillary.seatmap.seat" text="Seat" />
				</span>
				<span class="y_seatIconSeatNumber">
					<spring:theme code="text.ancillary.seatmap.passenger.seat.number.placeholder" text="--" />
				</span>
			</span>
			<span class="y_seatIconSeatPrice">
				<spring:theme code="text.ancillary.seatmap.passenger.seat.price.placeholder" text="--" />
			</span>
		</div>
		<a class="btn-delete-seat btn y_removeSeatSelection hidden-xs ${disabled ? 'disabled' : '' }">
			<span class="glyphicon glyphicon-remove"></span>
			<span class="sr-only">
				<spring:theme code="text.ancillary.seatmap.seat.remove" text="Remove" />
			</span>
		</a>
	</div>
</li>
