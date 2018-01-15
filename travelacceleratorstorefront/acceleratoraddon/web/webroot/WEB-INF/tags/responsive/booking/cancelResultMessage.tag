<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ attribute name="refundResult" required="true" type="java.lang.String"%>
<%@ attribute name="cancellationResult" required="true" type="java.lang.String"%>
<%@ attribute name="cancellationParameter" required="true" type="java.lang.String"%>
<%@ attribute name="refundedAmount" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="success" value="${not empty refundResult}" />
<c:choose>
	<c:when test="${empty cancellationResult}">
		<div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible y_cancellationResult" role="alert" style="display: none;">
	</c:when>
	<c:otherwise>
		<div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible y_cancellationResult" role="alert">
	</c:otherwise>
</c:choose>
<button type="button" class="close" data-dismiss="alert" aria-label="Close">
	<span aria-hidden="true">&times;</span>
</button>
<p class="y_cancellationResultContent">
	<c:if test="${not empty cancellationResult}">
		<spring:theme code="${cancellationResult}" arguments="${cancellationParameter}" />
	</c:if>
</p>
<p class="y_cancellationRefundResultContent">
	<c:if test="${not empty refundResult}">
		<c:set var="cancelRefundedAmount">
			<format:price priceData="${refundedAmount}" />
		</c:set>
		<spring:theme code="${refundResult}" arguments="${cancelRefundedAmount}" argumentSeparator="-" />
	</c:if>
</p>
</div>
