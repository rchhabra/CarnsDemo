<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/bookingdetails"%>
<json:object escapeXml="false">
	<json:property name="updatedBookingPagedHtml">
		<bookingDetails:updateAccommodationBookingDates accommodationAvailabilityResponse="${accommodationAvailabilityResponse}" orderCode="${orderCode}" />
	</json:property>
</json:object>