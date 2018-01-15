<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="offerGroup" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData"%>
<%@ attribute name="itineraries" required="true" type="java.util.List"%>
<%@ attribute name="offerGroupIndex" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div role="tabpanel" class="tab-pane fade in active y_ancillaryFormQuantityWrapper" id="${offerGroup.code}tab-0">
	<ul class="list-group form-inline-wrapper">
		<li class="list-group-item">
			<c:set var="view">
					${empty offerGroupsViewMap[offerGroup.code] ? offerGroupsViewMap['DEFAULTVIEW'] : offerGroupsViewMap[offerGroup.code]}
				</c:set>
			<c:set var="originDestinationOfferInfo" value="${null}" scope="request" />
			<c:set var="offerPricingInfos" value="${offerGroup.offerPricingInfos}" scope="request" />
			<c:set var="travellers" value="${null}" scope="request" />
			<jsp:include page="${view}">
				<jsp:param name="offerGroupCode" value="${fn:toLowerCase(offerGroup.code)}" />
				<jsp:param name="offerGroupName" value="${offerGroup.name}" />
				<jsp:param name="offerGroupIndex" value="${offerGroupIndex}" />
				<jsp:param name="tabIndex" value="0" />
				<jsp:param name="offerGroupType" value="${offerGroup.travelRestriction.addToCartCriteria}" />
			</jsp:include>
		</li>
	</ul>
</div>