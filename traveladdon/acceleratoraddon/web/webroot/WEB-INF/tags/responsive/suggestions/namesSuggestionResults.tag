<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty savedTravellers}">
	<div class="autocomplete-suggestions">
		<ul>
			<c:forEach var="traveller" items="${savedTravellers}" varStatus="i">
				<li class="child">
				<c:set var="specialRequests" />
				<c:forEach var="request" items="${traveller.specialRequestDetail.specialServiceRequests}">
					<c:set var="specialRequests" value="${specialRequests} ${fn:escapeXml(' ')} ${request.name}" />
				</c:forEach>
					<a href="" class="autocomplete-suggestion passengerName" 
							data-uid="${fn:escapeXml(traveller.uid)}"
							data-title="${fn:escapeXml(traveller.travellerInfo.title.code)}"
							data-firstname="${fn:escapeXml(traveller.travellerInfo.firstName)}"
							data-lastname="${fn:escapeXml(traveller.travellerInfo.surname)}"
							data-reasonfortravel="${fn:escapeXml(traveller.travellerInfo.reasonForTravel)}"
							data-passengertype="${fn:escapeXml(traveller.travellerInfo.passengerType.code)}"
							data-gender="${fn:escapeXml(traveller.travellerInfo.gender)}"
							data-membershipnumber="${fn:escapeXml(traveller.travellerInfo.membershipNumber)}"
							data-email="${fn:escapeXml(traveller.travellerInfo.email)}"
							data-specialrequestdetail="${specialRequests}">${fn:escapeXml(traveller.travellerInfo.title.name)} ${fn:escapeXml(' ')} ${fn:escapeXml(traveller.travellerInfo.firstName)} ${fn:escapeXml(' ')}
						${fn:escapeXml(traveller.travellerInfo.surname)}</a>
				</li>
			</c:forEach>
		</ul>
	</div>
</c:if>
