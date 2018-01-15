<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="transportofferingstatus" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/transportofferingstatus"%>
<c:choose>
	<c:when test="${empty nextScheduledTransportOfferingData}">
		<%-- TRANSPORT OFFERING STATUS SEARCH COMPONENT --%>
		<c:url var="actionURL" value="/view/TransportOfferingStatusSearchComponentController/get-transport-offering-status-page" />
		<transportofferingstatus:statusForm formId="y_transportOfferingStatusSearchForm" action="${actionURL}" method="POST" />
	</c:when>
	<c:otherwise>
		<%-- NEXT TRANSPORT OFFERING STATUS INFORMATION --%>
		<transportofferingstatus:nextTransportOfferingStatus formId="y_nexttransportOfferingStatus"/>
	</c:otherwise>
</c:choose>
