<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="urlTestId" required="false" type="java.lang.String"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<div class="row heading-with-button">
	<div class="col-xs-7 col-sm-8">
		<h2 class="h2">
			<spring:theme code="${labelKey}"/></h2>
	</div>
	<div class="col-xs-5 col-sm-4 button-inline">
		<ycommerce:testId code="${urlTestId}">
			<a href="${url}" class="btn btn-primary btn-block"><spring:theme code="text.company.addNew.button"/></a>
		</ycommerce:testId>
	</div>
</div>