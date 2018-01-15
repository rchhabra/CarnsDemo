<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url var="addToCartUrl" value="/cart/accommodation/add"/>

<c:if test="${not empty accommodationAvailabilityResponse.roomStays}">
    <form:form id="accommodationAddToCartForm" name="accommodationAddToCartForm" class="y_accommodationAddToCartForm"
               action="${addToCartUrl}" method="post">
        <input type="hidden" name="accommodationOfferingCode"
               value="${fn:escapeXml(accommodationAvailabilityResponse.accommodationReference.accommodationOfferingCode)}"/>
        <input type="hidden" id="y_checkInDate" name="checkInDateTime" />
        <input type="hidden" id="y_checkOutDate" name="checkOutDateTime" />
        <input type="hidden" id="y_accommodationCode" name="accommodationCode"/>
        <input type="hidden" id="y_roomRateCodes" name="roomRateCodes"/>
        <input type="hidden" id="y_roomRateDates" name="roomRateDates"/>
        <input type="hidden" id="y_numberOfRooms" name="numberOfRooms"/>
        <input type="hidden" id="y_ratePlanCode" name="ratePlanCode"/>
    </form:form>
    <c:forEach var="roomStay" items="${accommodationAvailabilityResponse.roomStays}" varStatus="idx">
        <div class="y_roomStayContainer">
            <fmt:formatDate value="${roomStay.checkInDate}" var="checkInDateFormatted" pattern="dd/MM/yyyy"/>
            <fmt:formatDate value="${roomStay.checkOutDate}" var="checkOutDateFormatted" pattern="dd/MM/yyyy"/>
            <input type="hidden" name="checkInDate" class="y_checkInDate" value="${checkInDateFormatted}">
            <input type="hidden" name="checkOutDate" class="y_checkOutDate" value="${checkOutDateFormatted}">
            <c:forEach var="roomType" items="${roomStay.roomTypes}" varStatus="accommodationID">
                <input type="hidden" name="accommodationCode" class="y_accommodationCode" value="${fn:escapeXml(roomType.code)}"/>
                <accommodationDetails:roomTypeDetails roomType="${roomType}" index="${idx.index}" fromPrice="${roomStay.fromPrice}"/>
                <%-- Price Options --%>
                <accommodationDetails:roomPriceOptions roomStay="${roomStay}" index="${idx.index}" roomTypeCode="${roomType.code}"/>
                <%-- Accommodation Gallery --%>
                <accommodationDetails:accommodationGallery roomType="${roomType}" index="${idx.index}"/>
            </c:forEach>
        </div>
    </c:forEach>
</c:if>
