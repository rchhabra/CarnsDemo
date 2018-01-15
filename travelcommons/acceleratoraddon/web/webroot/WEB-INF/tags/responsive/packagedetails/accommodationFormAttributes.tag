<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomStay" required="true" type="de.hybris.platform.commercefacades.accommodation.RoomStayData"%>
<%@ attribute name="roomType" required="true" type="de.hybris.platform.commercefacades.accommodation.RoomTypeData"%>

<fmt:formatDate value="${roomStay.checkInDate}" var="checkInDateFormatted" pattern="dd/MM/yyyy"/>
<fmt:formatDate value="${roomStay.checkOutDate}" var="checkOutDateFormatted" pattern="dd/MM/yyyy"/>
<input type="hidden" name="checkInDate" class="y_checkInDate" value="${checkInDateFormatted}"/>
<input type="hidden" name="checkOutDate" class="y_checkOutDate" value="${checkOutDateFormatted}"/>
<input type="hidden" name="accommodationCode" class="y_accommodationCode" value="${fn:escapeXml(roomType.code)}"/>
<input type="hidden" name="roomStayRefNumber" class="y_roomStayRefNumber" value="${fn:escapeXml(roomStay.roomStayRefNumber)}"/>

<c:set var="plan" value="${roomStay.ratePlans[0]}" />
<input type="hidden" name="ratePlanCode" value="${fn:escapeXml(plan.code)}" class="y_ratePlanCode"/>
<c:forEach var="roomRate" items="${plan.roomRates}" varStatus="roomRateIdx">
    <fmt:formatDate value="${roomRate.stayDateRange.startTime}" var="roomRateDateFormatted" pattern="dd/MM/yyyy"/>
    <input type="hidden" name="roomRate${roomRateIdx.index}" code="${fn:escapeXml(roomRate.code)}" value="${roomRateDateFormatted}" class="y_roomRate"/>
</c:forEach>
