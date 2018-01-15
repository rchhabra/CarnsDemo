<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="accommodationAvailabilityResponse" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData"%>
<%@ attribute name="addRoomToPackageUrl" required="true" type="java.lang.String"%>
<c:url var="addAccommodationToCartUrl" value="${addRoomToPackageUrl}" />
<form:form id="y_packageAccommodationAddToCartForm" name="accommodationAddToCartForm" class="y_accommodationAddToCartForm" action="${addAccommodationToCartUrl}" method="POST">
	<input type="hidden" name="accommodationOfferingCode" value="${fn:escapeXml(accommodationAvailabilityResponse.accommodationReference.accommodationOfferingCode)}" />
	<input type="hidden" name="numberOfRooms" value="1" />
	<input type="hidden" id="y_checkInDate" name="checkInDateTime" />
	<input type="hidden" id="y_checkOutDate" name="checkOutDateTime" />
	<input type="hidden" id="y_accommodationCode" name="accommodationCode" />
	<input type="hidden" id="y_roomRateCodes" name="roomRateCodes" />
	<input type="hidden" id="y_roomRateDates" name="roomRateDates" />
	<input type="hidden" id="y_ratePlanCode" name="ratePlanCode" />
	<input type="hidden" id="y_roomStayRefNumber" name="roomStayRefNumber" />
</form:form>
