<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ attribute name="pricedItineraries" required="true" type="java.util.List"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${fn:length(pricedItineraries) > 0}">
	<div class="tab-wrapper left-tabs">
		<ul class="nav nav-tabs" role="tablist">
			<c:set var="firstTransportOfferingTab" value="true"/>
			<c:forEach var="upgradableOptionsPerLeg" items="${pricedItineraries}">
				<c:set var="tabId" value="${upgradableOptionsPerLeg.originDestinationRefNumber +1}" />
				<c:set var="transportOfferings" value="${upgradableOptionsPerLeg.itinerary.originDestinationOptions[0].transportOfferings}" />
				<li role="presentation" <c:if test="${fn:escapeXml(firstTransportOfferingTab)}">
					class="active"
				</c:if>>
					<a href="#upgradeBundle-tab-${fn:escapeXml(tabId)}" aria-controls="upgradeBundle-tab-${fn:escapeXml(tabId)}" role="tab" data-toggle="tab">
						<c:choose>
							<c:when test="${fn:length(transportOfferings) eq 1}">
								<span class="tab-line-1">
									<c:out value="${transportOfferings[0].sector.origin.code}" />
									-
									<c:out value="${transportOfferings[0].sector.destination.code}" />
								</span>
								<span class="tab-line-1">
									<c:out value="${transportOfferings[0].travelProvider.code}${fn:escapeXml(transportOfferings[0].number)}" />
								</span>
							</c:when>
							<c:otherwise>
								<c:set var="numberOfTransportOfferings" value="${fn:length(transportOfferings)}" />
								<span class="tab-line-1">
									<c:out value="${transportOfferings[0].sector.origin.code}" />
									-
									<c:out value="${transportOfferings[numberOfTransportOfferings-1].sector.destination.code}" />
								</span>
								<c:forEach begin="0" end="${numberOfTransportOfferings - 1}" varStatus="loop">
									<span class="tab-line-1">
										<c:out value="${transportOfferings[loop.index].travelProvider.code}${fn:escapeXml(transportOfferings[loop.index].number)}" />
									</span>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</a>
				</li>
				<c:set var="firstTransportOfferingTab" value="false"/>
			</c:forEach>
		</ul>
		<div class="tab-content">
			<c:set var="firstItinerary" value="true"/>
			<c:forEach var="upgradableOptionsPerLeg" items="${pricedItineraries}" varStatus="index">
				<c:set var="tabId" value="${upgradableOptionsPerLeg.originDestinationRefNumber +1}"/>
				<div role="tabpanel" class="tab-pane fade clearfix ${firstItinerary ? ' active in ' : ''}" id="upgradeBundle-tab-${fn:escapeXml(tabId)}">
					<c:choose>
						<c:when test="${fn:length(upgradableOptionsPerLeg.itineraryPricingInfos) gt 1}">
							<ul class="list-group no-padding col-xs-12">
								<ancillary:bundleDetails tabId="${tabId}" upgradableOptionsPerLeg="${upgradableOptionsPerLeg}" />
							</ul>
						</c:when>
						<c:otherwise>
							<p class="tab-pane-message">
								<spring:message code="text.cms.upgradebundle.upgrade.available.false" text="Upgrade is not available." />
							</p>
						</c:otherwise>
					</c:choose>
				</div>
				<c:set var="firstItinerary" value="false"/>
			</c:forEach>
		</div>
	</div>
</c:if>
