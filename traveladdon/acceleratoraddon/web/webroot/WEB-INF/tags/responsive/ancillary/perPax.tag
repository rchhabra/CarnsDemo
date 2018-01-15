<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="offerGroup" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="itineraries" required="true" type="java.util.List"%>

<div role="tabpanel" class="tab-pane fade in active y_ancillaryFormQuantityWrapper" id="${fn:escapeXml(offerGroup.code)}tab-0">
	<ul class="list-group form-inline-wrapper">
		<c:forEach var="travellerBreakdown" items="${offerGroup.offerPricingInfos[0].travellerBreakdowns}" varStatus="loop">
				<li class="list-group-item y_ancillaryListElement">
					<c:set var="view">
						${fn:escapeXml(empty offerGroupsViewMap[offerGroup.code] ? offerGroupsViewMap['DEFAULTVIEW'] : offerGroupsViewMap[offerGroup.code])}
					</c:set>
					<c:set var="transportOfferings" value="" scope="request" />
					<c:set var="offerPricingInfos" value="${offerGroup.offerPricingInfos}" scope="request" />
					<c:set var="traveller" value="${travellerBreakdown.traveller}" scope="request" />
					<jsp:include page="${view}">
						<jsp:param name="offerGroupCode" value="${offerGroup.code}" />
						<jsp:param name="index" value="${loop.index}" />
						<jsp:param name="tabIndex" value="0" />
						<jsp:param name="travellerMinOfferGroupQty" value="${offerGroup.travelRestriction.travellerMinOfferQty}" />
						<jsp:param name="travellerMaxOfferGroupQty" value="${offerGroup.travelRestriction.travellerMaxOfferQty}" />
					</jsp:include>
				</li>
			</c:forEach>
		</ul>
</div>
