<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>

<c:if test="${reservation.bookingStatusCode ne 'CANCELLED' && reservation != null}">
	<div class="panel panel-default my-account-secondary-panel">
		<div class="panel-price-summary">
			<booking:priceSummary reservationData="${reservation}" />
		</div>
	</div>
	<c:forEach var="reservationItem" items="${reservation.reservationItems}">
		<div class="journey-wrapper">
			<bookingDetails:bookingitem reservationData="${reservation}" reservationItem="${reservationItem}" cssClass="panel panel-default my-account-secondary-panel" />
		</div>
	</c:forEach>
	<bookingDetails:globalancillaries reservationData="${reservation}" />
</c:if>
