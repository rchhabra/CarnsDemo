<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="offerGroup" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData"%>
<%@ attribute name="itineraries" required="true" type="java.util.List"%>
<%@ attribute name="offerGroupIndex" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:forEach var="originDestinationOfferInfo" items="${offerGroup.originDestinationOfferInfos}" varStatus="originDestinationOfferInfoLoopTagStatus">
	<div role="tabpanel" class="tab-pane fade ${originDestinationOfferInfoLoopTagStatus.index eq 0 ? 'in active' : ''} y_ancillaryFormQuantityWrapper" id="${offerGroup.code}tab-${originDestinationOfferInfoLoopTagStatus.index}">
		<ul class="list-group form-inline-wrapper">
			<li class="list-group-item">
				<c:set var="view">
					${empty offerGroupsViewMap[offerGroup.code] ? offerGroupsViewMap['DEFAULTVIEW'] : offerGroupsViewMap[offerGroup.code]}
				</c:set>
				<c:set var="originDestinationOfferInfo" value="${originDestinationOfferInfo}" scope="request" />
				<c:set var="offerPricingInfos" value="${null}" scope="request" />
				<c:set var="travellers" value="${itineraries[originDestinationOfferInfo.originDestinationRefNumber].travellers}" scope="request" />
				<jsp:include page="${view}">
					<jsp:param name="offerGroupCode" value="${fn:toLowerCase(offerGroup.code)}" />
					<jsp:param name="offerGroupName" value="${offerGroup.name}" />
					<jsp:param name="offerGroupIndex" value="${offerGroupIndex}" />
					<jsp:param name="tabIndex" value="${originDestinationOfferInfoLoopTagStatus.count}" />
					<jsp:param name="travellerMinOfferGroupQty" value="${offerGroup.travelRestriction.travellerMinOfferQty}" />
					<jsp:param name="travellerMaxOfferGroupQty" value="${offerGroup.travelRestriction.travellerMaxOfferQty}" />
					<jsp:param name="offerGroupType" value="${offerGroup.travelRestriction.addToCartCriteria}" />
				</jsp:include>
			</li>
		</ul>
	</div>
</c:forEach>
