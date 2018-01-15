<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<json:object escapeXml="false">
	<c:choose>
		<c:when test="${isUpgradeOptionAvailable}">
			<json:property name="htmlContent">
				<ancillary:upgradeBundleDetails pricedItineraries="${pricedItineraries}" />
			</json:property>
		</c:when>
	</c:choose>
	<json:property name="isUpgradeOptionAvailable" value="${isUpgradeOptionAvailable}" />
</json:object>
