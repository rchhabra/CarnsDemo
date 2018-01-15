<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<%@ attribute name="cssClass" required="true" type="java.lang.String"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>

<booking:itineraryitem reservationItem="${reservationItem}" cssClass="${cssClass}"/>
<booking:passengersummary reservationData="${reservationData}" reservationItem="${reservationItem}" />
<booking:ancillarySharedSummary reservationItem="${reservationItem}" />
