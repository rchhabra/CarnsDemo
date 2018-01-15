<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="saveUrl" required="true" type="java.lang.String" %>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String" %>
<%@ attribute name="b2BCostCenterForm" required="true" type="de.hybris.platform.travelcommerceorgaddon.forms.B2BCostCenterForm" %>
<%@ attribute name="addEditLabel" type="java.lang.String" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-primary panel-list">
	<div class="panel-heading">
		<h3 class="title">
			<spring:theme code="${addEditLabel}"></spring:theme>
		</h3>
	</div>
	<div class="panel-body">
		<fieldset class="fieldset">
			<form:form action="${saveUrl}" id="b2BCostCenterform" commandName="b2BCostCenterForm" method="POST">
				<form:input type="hidden" name="originalCode" path="originalCode" id="originalCode" />
				<div class="row">
					<div class="col-sm-6 col-xs-12">
						<formElement:formInputBox idKey="text.company.costCenter.id.title" labelKey="text.company.costCenter.id.title" path="code" inputCSS="text" mandatory="true" />
					</div>
					<div class="col-xs-12 col-sm-6">
						<formElement:formInputBox idKey="text.company.costCenter.name.title" labelKey="text.company.costCenter.name.title" path="name" inputCSS="text" mandatory="true" />
					</div>
				</div>
				<div class="row">
					<div class="col-sm-6 col-xs-12 nowrap">
						<formElement:formSelectBox idKey="text.company.costCenter.unit.title" skipBlank="true" labelKey="text.company.costCenter.unit.title" path="parentB2BUnit" mandatory="true" selectCSSClass="form-control" items="${b2bUnits}" />
					</div>
					<div class="col-xs-12 col-sm-6">
						<formElement:formSelectBox idKey="text.company.costCenter.currency.title" skipBlank="true" labelKey="text.company.costCenter.currency.title" path="currency" mandatory="true" selectCSSClass="form-control" items="${b2bCostCenterCurrencies}" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
						<button type="submit" class="btn btn-primary btn-block bottom-align">
							<spring:theme code="text.company.costCenter.saveButton.displayName" />
						</button>
					</div>
					<div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
						<a href="${cancelUrl}" class="btn btn-primary btn-block bottom-align">
							<spring:theme code="text.company.costCenter.cancelButton.displayName" />
						</a>
					</div>
				</div>
			</form:form>
		</fieldset>
	</div>
</div>