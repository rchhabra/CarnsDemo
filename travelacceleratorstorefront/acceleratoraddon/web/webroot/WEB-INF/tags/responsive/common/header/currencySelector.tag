<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="currencies" required="true" type="java.util.Collection"%>
<%@ attribute name="currentCurrency" required="true" type="de.hybris.platform.commercefacades.storesession.data.CurrencyData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${fn:length(currencies) > 1 && (empty amend || !amend) && (empty disableCurrencySelector || !disableCurrencySelector)}">
	<c:url value="/_s/currency" var="setCurrencyActionUrl" />
	<form:form action="${setCurrencyActionUrl}" method="post" id="y_currencyForm">
		<div class="form-group currency-select">
			<label class="control-label sr-only" for="currency-selector">
				<spring:theme code="text.currency" />
			</label>
			<ycommerce:testId code="header_currency_select">
				<select name="code" id="currency-selector" class="form-control y_currencySelector" style="display: none;">
					<c:forEach items="${currencies}" var="curr">
						<option value="${fn:escapeXml(curr.isocode)}" ${curr.isocode == currentCurrency.isocode ? 'selected="selected"' : ''}>
							<c:out value="${curr.symbol}" />
						</option>
					</c:forEach>
				</select>
			</ycommerce:testId>
		</div>
	</form:form>
</c:if>
