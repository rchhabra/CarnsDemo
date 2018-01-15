<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div id="y_transportOfferingStatusResultTable">
<c:choose>
	<c:when test="${not empty transportOfferingDataList}">
		<c:forEach var="entry" items="${transportOfferingDataList}">
			<c:set var="origin">${entry.sector.origin.location.name} ${entry.sector.origin.name} (${entry.sector.origin.code})</c:set>
			<c:set var="destination">${entry.sector.destination.location.name} ${entry.sector.destination.name} (${entry.sector.destination.code})</c:set>
			<div class="panel panel-primary flight-status-table">
				<div class="panel-heading">
					<h2 class="panel-title">${fn:escapeXml(origin)}>${fn:escapeXml(destination)}</h2>
				</div>
				<div class="panel-body">
					<div class="row">
						<div id="departing" class="col-md-6">
							<fmt:formatDate value="${entry.departureTime}" pattern="dd/MM/yyyy" var="departureDate" />
							<p>
								<spring:theme code="transport.offering.status.result.departing" />
								${fn:escapeXml(departureDate)}
							</p>
						</div>
						<div id="updated" class="col-md-6">
							<%-- <p>Last Updated: 07:32 (GMT)</p> --%>
						</div>
					</div>
				</div>
				<div id="statusTable" class="account-overview-table">
		        	<div class="responsive-table">
		            	<table class="table">
		                	<tbody><tr class="responsive-table-head hidden-xs">
								<th><spring:theme code="transport.offering.status.result.flight.number" /></th>
								<th><spring:theme code="transport.offering.status.result.scheduled.departure" /></th>
								<th><spring:theme code="transport.offering.status.result.updated.departure" /></th>
								<th><spring:theme code="transport.offering.status.result.flight.duration" /></th>
								<th><spring:theme code="transport.offering.status.result.terminal" /></th>
								<th><spring:theme code="transport.offering.status.result.status" /></th>
							</tr>
		                    <tr class="responsive-table-item">
		                    	<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="transport.offering.status.result.flight.number" /></td>
		                    	<td class="responsive-table-cell">${fn:escapeXml(entry.travelProvider.code)}${fn:escapeXml(entry.number)}</td>
		                                
		                    	<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="transport.offering.status.result.scheduled.departure" /></td>
		                        <fmt:formatDate value="${entry.departureTime}" pattern="HH:mm dd/MM/yyyy" var="departureTime" />
		                        <td class="responsive-table-cell">${fn:escapeXml(departureTime)}</td>
		                                
		                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="transport.offering.status.result.updated.departure" /></td>
		                        <fmt:formatDate value="${entry.updatedDepartureTime}" pattern="HH:mm dd/MM/yyyy" var="updatedDepartureTime" />
		                        <td class="responsive-table-cell">${fn:escapeXml(not empty entry.updatedDepartureTime ? updatedDepartureTime : "-" )}</td>
		                                
		                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="transport.offering.status.result.flight.duration" /></td>
		                        <td class="responsive-table-cell"><c:forEach var="mapEntry" items="${entry.duration}" varStatus="idx" ><c:if test="${idx.index != 0 }" >&nbsp;</c:if>${fn:escapeXml(mapEntry.value)}<spring:theme code="${fn:escapeXml(mapEntry.key)}" /></c:forEach></td>
		                                
		                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="transport.offering.status.result.terminal" /></td>
		                        <td class="responsive-table-cell">${fn:escapeXml(entry.originTerminal.name)}</td>
		                                
		                        <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="transport.offering.status.result.status" /></td>
		                        <td class="responsive-table-cell">${fn:escapeXml(entry.status)}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</c:forEach>
	</c:when>
	<c:otherwise>
		<c:if test="${not hasErrorFlag}">
			<div class="alert alert-danger" role="alert">
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
				<spring:theme code="transport.offering.status.no.result" />
			</div>
		</c:if>
	</c:otherwise>
</c:choose>
</div>
