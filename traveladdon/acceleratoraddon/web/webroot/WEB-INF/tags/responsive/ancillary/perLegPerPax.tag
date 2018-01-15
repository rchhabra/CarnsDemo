<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="offerGroup" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData"%>
<%@ attribute name="itineraries" required="true" type="java.util.List"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:forEach var="originDestinationOfferInfo" items="${offerGroup.originDestinationOfferInfos}" varStatus="idx">
	<div role="tabpanel" class="tab-pane fade ${idx.index eq 0 ? 'in active' : ''} y_ancillaryFormQuantityWrapper" id="${fn:escapeXml(offerGroup.code)}tab-${fn:escapeXml(idx.index)}">
		<ul class="list-group form-inline-wrapper">
			<c:forEach var="traveller" items="${itineraries[originDestinationOfferInfo.originDestinationRefNumber].travellers}" varStatus="loop">
				<li class="list-group-item y_ancillaryListElement">
					<c:set var="view">
						${empty offerGroupsViewMap[offerGroup.code] ? offerGroupsViewMap['DEFAULTVIEW'] : offerGroupsViewMap[offerGroup.code]}
					</c:set>
					<c:set var="transportOfferings" value="${originDestinationOfferInfo.transportOfferings}" scope="request" />
					<c:set var="offerPricingInfos" value="${originDestinationOfferInfo.offerPricingInfos}" scope="request" />
					<c:set var="traveller" value="${traveller}" scope="request" />
					<jsp:include page="${view}">
						<jsp:param name="offerGroupCode" value="${offerGroup.code}" />
						<jsp:param name="index" value="${loop.index}" />
						<jsp:param name="tabIndex" value="${idx.index}" />
						<jsp:param name="travellerMinOfferGroupQty" value="${offerGroup.travelRestriction.travellerMinOfferQty}" />
						<jsp:param name="travellerMaxOfferGroupQty" value="${offerGroup.travelRestriction.travellerMaxOfferQty}" />
					</jsp:include>
				</li>
			</c:forEach>
		</ul>
		<c:forEach var="transportOffering" items="${originDestinationOfferInfo.transportOfferings}">
			<input type="hidden" name="transportOfferingCodes" value="${transportOffering.code}" />
		</c:forEach>
	</div>
</c:forEach>
