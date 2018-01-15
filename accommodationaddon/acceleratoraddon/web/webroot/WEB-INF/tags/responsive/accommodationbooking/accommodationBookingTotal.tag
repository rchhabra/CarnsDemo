<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="totalRateFormattedValue" required="true" type="java.lang.String"%>
<%@ attribute name="totalRateValue" required="false" type="java.math.BigDecimal"%>
<%@ attribute name="partialPaymentPaid" required="false" type="de.hybris.platform.commercefacades.product.data.PriceData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-default total">
	<div class="panel-body">
		<div class="col-xs-12">
			<dl class="text-right">
				<dt>
					<spring:theme code="text.cms.accommodationbreakdown.grandTotal" text="Booking Total" />
				</dt>
				<dd>${fn:escapeXml(totalRateFormattedValue)}</dd>
				<c:if test="${not empty partialPaymentPaid && partialPaymentPaid.value >= 0}">
					<dt>
						<spring:theme code="reservation.partial.pay.label" text="Paid" />
					</dt>
					<dd>
						<format:price priceData="${partialPaymentPaid}" />
					</dd>
				</c:if>
				<c:if test="${not empty partialPaymentDue && partialPaymentDue.value > 0}">
					<dt>
						<spring:theme code="reservation.partial.due.label" text="Due" />
					</dt>
					<dd>
						<format:price priceData="${partialPaymentDue}" />
					</dd>
				</c:if>
			</dl>
		</div>
	</div>
</div>
