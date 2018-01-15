<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="paymentInfo" required="true" type="de.hybris.platform.commercefacades.order.data.CCPaymentInfoData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="col-xs-12 col-md-6">
	<div class="col-xs-6 col-sm-6 col-md-6">
		<dl>
			<dt class="sr-only"><spring:theme code="sr.savedpayment.cardtype" text="Card Type" /></dt>
			<dd class="card-type">${fn:escapeXml(paymentInfo.cardType)}</dd>
			<dt class="sr-only"><spring:theme code="sr.savedpayment.name" text="Name" /></dt>
			<dd>${fn:escapeXml(paymentInfo.accountHolderName)}</dd>
			<dt class="sr-only"><spring:theme code="sr.savedpayment.cardnumber" text="Card Number" /></dt>
			<dd>
				<ycommerce:testId code="paymentDetails_item_cardNumber_text">${fn:escapeXml(paymentInfo.cardNumber)}</ycommerce:testId>
			</dd>
			<dt class="sr-only"><spring:theme code="sr.savedpayment.expiry" text="Expiry Date" /></dt>
			<dd>
				<c:if test="${paymentInfo.expiryMonth lt 10}">0</c:if>${fn:escapeXml(paymentInfo.expiryMonth)}&nbsp;/&nbsp;${fn:escapeXml(paymentInfo.expiryYear)}</dd>
		</dl>
	</div>
	<c:if test="${paymentInfo.billingAddress ne null}">
		<div class="col-xs-6 col-sm-6 col-md-6">
			<dl>
				<dt><spring:theme code="text.account.billingAddress" text="Billing Address" /></dt>
				<dd>${fn:escapeXml(paymentInfo.billingAddress.line1)}</dd>
				<dd>${fn:escapeXml(paymentInfo.billingAddress.town)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}</dd>
				<dd>${fn:escapeXml(paymentInfo.billingAddress.country.name)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.postalCode)}</dd>
			</dl>
		</div>
	</c:if>
</div>
