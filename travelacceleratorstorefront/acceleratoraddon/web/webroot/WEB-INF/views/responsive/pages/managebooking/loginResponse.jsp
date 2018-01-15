<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking"%>
<%@ taglib prefix="error" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/error"%>
<c:url var="manageMyBookingUrl" value="/manage-booking/login" />

<json:object escapeXml="false">

    <json:property name="loginStatus" value="${loginStatus}"/>
    <json:property name="bookingReference" value="${bookingReference}"/>

    <c:if test="${loginStatus eq 'ADDITIONAL_SECURITY'}">
        <json:property name="modal">
            <booking:additionalSecurity manageMyBookingUrl="${manageMyBookingUrl}" bookingReference="${bookingReference}" passengerReference="${passengerReference}" lastName="${lastName}" errorMessage="${errorMessage}"/>
        </json:property>
    </c:if>

    <c:if test="${loginStatus eq 'ERROR'}">
        <json:property name="modal">
            <error:error errorMessage="${errorMessage}"/>
        </json:property>
    </c:if>

</json:object>
