<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page trimDirectiveWhitespaces="true"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${reservation.bookingStatusCode ne 'CANCELLED'}">
	<div class="y_transportSummaryComponent inner-wrap y_reservationSideBarContent">
		<input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_transportSummaryComponentId" />
		<c:if test="${reservation ne null}">
			<section class="panel panel-primary">
				<div class="hidden-xs">
					<h2 class="h4 reservation-summary panel-title title-collapse">
						<a role="button" data-toggle="collapse" aria-expanded="true" aria-controls="summary" data-target="#summary" class="panel-heading panel-header-link collapsable collapsed">
							<spring:theme code="text.cms.transportsummary.title" text="Flight Summary" />
						</a>
					</h2>
				</div>
				<div class="panel-body reservation summary collapse" id="summary">
					<div class="sidebar-content-section">
						<c:forEach items="${reservation.reservationItems}" var="item" varStatus="itemIdx">
							<dl class="row">
								<dt class="col-xs-12">${fn:escapeXml(item.reservationItinerary.route.origin.code)} &gt; ${fn:escapeXml(item.reservationItinerary.route.destination.code)}</dt>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<spring:theme code="text.cms.transportsummary.depart" text="Departure:" />
								</dd>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<fmt:formatDate pattern="${dateFormat}" value="${item.reservationItinerary.originDestinationOptions[0].transportOfferings[0].departureTime}" />
								</dd>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<fmt:formatDate pattern="${timeFormat}" value="${item.reservationItinerary.originDestinationOptions[0].transportOfferings[0].departureTime}" />
								</dd>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<spring:theme code="text.cms.transportsummary.arrival" text="Arrival:" />
								</dd>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<c:set var="length" value="${fn:length(item.reservationItinerary.originDestinationOptions[0].transportOfferings)}" />
									<fmt:formatDate pattern="${dateFormat}" value="${item.reservationItinerary.originDestinationOptions[0].transportOfferings[length-1].arrivalTime}" />
								</dd>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<fmt:formatDate pattern="${timeFormat}" value="${item.reservationItinerary.originDestinationOptions[0].transportOfferings[length-1].arrivalTime}" />
								</dd>
								<dd class="col-xs-4 col-sm-12 col-md-4">
									<spring:theme code="text.cms.transportsummary.class" text="Class:" />
								</dd>
								<dd class="col-xs-8 col-sm-12 col-md-8">
										${fn:escapeXml(item.reservationPricingInfo.itineraryPricingInfo.bundleTypeName)}
								</dd>
								<c:if test="${!itemIdx.last}">
									<dd class="col-xs-12 col-sm-12 col-md-12">
										&nbsp;
									</dd>
								</c:if>
							</dl>
						</c:forEach>
					</div>
				</div>
			</section>
		</c:if>
	</div>
</c:if>
