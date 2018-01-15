<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="accommodationCustomerReviews" required="true" type="java.util.List"%>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData"%>
<%@ attribute name="totalNumberOfReviews" required="true" type="java.lang.Long"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="maxFractionDigits" value="1" />
<c:forEach var="award" items="${property.awards}">
	<c:if test="${award.type eq 'USER_RATING'}">
		<c:set var="averageUserRating"><fmt:formatNumber value="${award.rating}" maxFractionDigits="${maxFractionDigits}" />
		</c:set>
	</c:if>
</c:forEach>

<c:if test="${not empty averageUserRating}">
	<div class="row fixed-average-score">
		<div class="col-xs-12">
			<p>
                <c:choose>
                    <c:when test="${totalNumberOfReviews eq 1}">
                        <spring:theme code="text.accommodation.details.customerreview.bar.header.review" arguments="${totalNumberOfReviews}" />
                    </c:when>
                    <c:otherwise>
			            <spring:theme code="text.accommodation.details.customerreview.bar.header.reviews" arguments="${totalNumberOfReviews}" />
                    </c:otherwise>
                </c:choose>
			 </p>
		</div>
		<div class="average-score col-xs-12 col-sm-8">
			<span aria-hidden="true">0</span>
			<div class="score-value-wrapper">
				<div class="score-value" style="width: ${fn:escapeXml(averageUserRating*10)}%;">
					<span class="sr-only">${fn:escapeXml(averageUserRating)} average score</span>
				</div>
			</div>
			<span aria-hidden="true"><span>${fn:escapeXml(averageUserRating)}</span>/10</span>
		</div>
	</div>
</c:if>
