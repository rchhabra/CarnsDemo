<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ attribute name="accommodationAvailabilityResponse" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty accommodationAvailabilityResponse.roomStays}">
	<div class="row form-group">
		<div class="col-xs-12 col-sm-offset-8 col-sm-offset-8 col-sm-4">
			<button class="y_roomOptionsCollapse btn btn-primary col-xs-12 collapsed" type="button" data-toggle="collapse" data-target="#roomOptionsCollapse" aria-expanded="false" aria-controls="roomOptionsCollapse">
				<span class="show-text">
					<spring:theme code="text.package.details.button.show.room.options" text="Show room options" />
				</span>
				<span class="hide-text hidden">
					<spring:theme code="text.package.details.button.hide.room.options" text="Hide room options" />
				</span>
			</button>
		</div>
	</div>
	<packageDetails:changeAccommodationForm accommodationAvailabilityResponse="${accommodationAvailabilityResponse}" />
	<div id="roomOptionsCollapse" class="collapse">
		<c:forEach var="reservedRoomStay" items="${accommodationAvailabilityResponse.reservedRoomStays}" varStatus="idx">
			<c:if test="${!reservedRoomStay.nonModifiable}">
				<packageDetails:roomOptionDetails roomStays="${accommodationAvailabilityResponse.roomStays}" reservedRoomStayRefNumber="${reservedRoomStay.roomStayRefNumber}"/>
			</c:if>
		</c:forEach>
	</div>
</c:if>
