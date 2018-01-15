<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ attribute name="accommodationCustomerReviews" required="true" type="java.util.List"%>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData"%>
<%@ attribute name="totalNumberOfReviews" required="true" type="java.lang.Long"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:choose>
	<c:when test="${not empty accommodationCustomerReviews}">
		<accommodationDetails:customerReviewListItem accommodationCustomerReviews="${accommodationCustomerReviews}" />
		<div class="y_customerReviewListItems"></div>
	</c:when>
	<c:otherwise>
		<spring:theme code="text.accommodation.details.customerReviews.NoResult" />
	</c:otherwise>
</c:choose>
