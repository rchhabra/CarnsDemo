<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="transportOfferings" required="true" type="java.util.List"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="isMultiSectorRoute" value="${fn:length(transportOfferings) gt 1}" />
<c:forEach var="transportOffering" items="${transportOfferings}">
    <div class="fieldset">
        <div class="row">
        	<c:if test="${isMultiSectorRoute}">
				<div class="col-xs-12">
					<dl>
						<dt>${fn:escapeXml(transportOffering.sector.origin.name)} (${fn:escapeXml(transportOffering.sector.origin.code)}) to ${fn:escapeXml(transportOffering.sector.destination.name)} (${fn:escapeXml(transportOffering.sector.destination.code)})</dt>
					</dl>
				</div>
			</c:if>
            <div class="col-xs-12">
                <div class="journey-details">
                    <div class="col-xs-12 col-sm-6 col-md-3">
                        <dl>
                            <dt>
                                <spring:theme code="text.page.managemybooking.reservationitem.flightnumber" text="Flight Number" />
                            </dt>
                            <dd>${fn:escapeXml(transportOffering.travelProvider.code)}${fn:escapeXml(transportOffering.number)}</dd>
                        </dl>
                    </div>
                    <div class="col-xs-12 col-sm-6 col-md-3">
                        <dl>
                            <dt>
                                <spring:theme code="text.page.managemybooking.reservationitem.departs" text="Departs" />
                            </dt>
                            <fmt:formatDate value="${transportOffering.departureTime}" var="formattedDepartureDate" type="both" pattern="HH:mm dd MMM yyyy" />
                            <dd>${fn:escapeXml(formattedDepartureDate)}</dd>
                        </dl>
                    </div>
                    <div class="col-xs-12 col-sm-6 col-md-3">
                        <dl>
                            <dt>
                                <spring:theme code="text.page.managemybooking.reservationitem.arrives" text="Arrives" />
                            </dt>
                            <fmt:formatDate value="${transportOffering.arrivalTime}" var="formattedArrivalDate" type="both" pattern="HH:mm dd MMM yyyy" />
                            <dd>${fn:escapeXml(formattedArrivalDate)}</dd>
                        </dl>
                    </div>
                    <div class="col-xs-12 col-sm-6 col-md-3">
                        <dl>
                            <dt>
                                <spring:theme code="text.page.managemybooking.reservationitem.terminal" text="Terminal" />
                            </dt>
                            <dd>${fn:escapeXml(transportOffering.originTerminal.name)}</dd>
                        </dl>
                    </div>
                </div>
                <div class="col-xs-12">
                    <dl class="status">
                        <dt>
                            <spring:theme code="text.page.managemybooking.reservationitem.flightStatus" text="Flight Status" />
                        </dt>
                        <dd>${fn:escapeXml(transportOffering.status)}</dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>
</c:forEach>
