<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${reservation.bookingStatusCode ne 'CANCELLED'}">
<div class="y_accommodationSummaryComponent inner-wrap y_reservationSideBarContent">
	<input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_accommodationSummaryComponentId" />
	<c:if test="${reservation ne null}">
		<section class="panel panel-primary">
            <div class="hidden-xs">
                <h2 class="h4 reservation-summary panel-title title-collapse">
                    <a role="button" data-toggle="collapse" aria-expanded="true" aria-controls="accommodationsummary" data-target="#accommodationsummary" class="panel-heading panel-header-link collapsable collapsed">
                        <spring:theme code="text.cms.accommodationsummary.hotelsummary" text="Hotel Summary" />
                    </a>
                </h2>
			</div>
			<div class="panel-body summary collapse reservation" id="accommodationsummary">
				<div class="sidebar-content-section">
					<dl class="row">
						<dt class="col-xs-12">${fn:escapeXml(reservation.accommodationReference.accommodationOfferingName)}</dt>
						<dd class="col-xs-4 col-sm-12 col-md-4">
							<fmt:formatDate value="${reservation.roomStays[0].checkInDate}" pattern="dd/MM/yyyy" />
						</dd>
						<dd class="col-xs-4 col-sm-12 col-md-4">
							<fmt:formatDate value="${reservation.roomStays[0].checkOutDate}" pattern="dd/MM/yyyy" />
						</dd>
						<dt class="col-xs-12">
							<spring:theme code="text.cms.accommodationsummary.numberOfRooms" text="Number Of Rooms" />
						</dt>
						<dd class="col-xs-8">
							${fn:escapeXml(fn:length(reservation.roomStays))}&nbsp;
							<c:choose>
								<c:when test="${fn:length(reservation.roomStays) eq 1}">
									<spring:theme code="text.cms.accommodationsummary.room" text="Room" />
								</c:when>
								<c:otherwise>
									<spring:theme code="text.cms.accommodationsummary.rooms" text="Rooms" />
								</c:otherwise>
							</c:choose>
						</dd>
					</dl>
				</div>
			</div>
		</section>
	</c:if>
</div>
</c:if>
