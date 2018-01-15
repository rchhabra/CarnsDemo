<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="roomStay" required="true" type="de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="row room-wrap">
	<div class="col-xs-4 col-sm-2">
		<c:forEach items="${roomStay.roomTypes[0].images}" var="accommodationImage">
			<c:if test="${accommodationImage.format == 'list'}">
				<img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}" class="img-responsive">
			</c:if>
		</c:forEach>
	</div>
	<div class="col-xs-8 col-sm-10">
		<div class="row">
			<div class="col-xs-12">
				<h3>${fn:escapeXml(roomStay.roomTypes[0].name)}</h3>
			</div>
			<div class="col-xs-12">
				<p>${fn:escapeXml(roomStay.roomTypes[0].bedType)}</p>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<dl>
					<dt>
						<spring:theme code="text.guest.details.roomtype.dates" text="Dates" />
					</dt>
					<dd>
						<spring:theme code="text.guest.details.roomtype.check.in" text="Check In" />
						:&nbsp;
						<fmt:formatDate value="${roomStay.checkInDate}" pattern="dd/MM/yyyy" />
					</dd>
					<dd>
						<spring:theme code="text.guest.details.roomtype.check.out" text="Check Out" />
						:&nbsp;
						<fmt:formatDate value="${roomStay.checkOutDate}" pattern="dd/MM/yyyy" />
					</dd>
				</dl>
			</div>
		</div>
	</div>
</div>
