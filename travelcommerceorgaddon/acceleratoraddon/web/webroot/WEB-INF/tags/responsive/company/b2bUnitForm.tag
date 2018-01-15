<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="b2BUnitForm" required="true" type="de.hybris.platform.travelcommerceorgaddon.forms.B2BUnitForm"%>
<%@ attribute name="formUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="addEditLabel" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-primary panel-list">
	<div class="panel-heading">
		<h3 class="title">
			<spring:theme code="${addEditLabel}"></spring:theme>
		</h3>
	</div>
	<div class="panel-body">
		<form:form cssClass="y_b2bUnitform" action="${formUrl}" method="post" commandName="b2BUnitForm">
			<fieldset class="fieldset">
				<legend class="sr-only">Edit Unit details</legend>
				<form:input type="hidden" name="originalUid" path="originalUid" id="originalUid" />
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<formElement:formInputBox idKey="b2bunit.id" labelKey="b2bunit.id" path="uid" inputCSS="text" mandatory="true" />
					</div>
					<div class="col-xs-12 col-sm-6">
						<formElement:formInputBox idKey="b2bunit.name" labelKey="b2bunit.name" path="name" inputCSS="text" mandatory="true" />
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<formElement:formSelectBox idKey="b2bunit.parent" labelKey="b2bunit.parent" path="parentUnit" selectCSSClass="form-control" mandatory="false" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${branchSelectOptions}" />
					</div>
					<div class="col-xs-12 col-sm-6">
						<formElement:formSelectBox idKey="b2bunit.approvalProcess" labelKey="b2bunit.approvalProcess" selectCSSClass="form-control" path="approvalProcessCode" mandatory="false" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${businessProcesses}" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
						<ycommerce:testId code="unit_saveChange_button">
							<button type="submit" class="btn btn-primary btn-block bottom-align" id="SubmitBudget_button">
								<spring:theme code="b2bunit.save" />
							</button>
						</ycommerce:testId>
					</div>
					<div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
						<ycommerce:testId code="unit_cancel_button">
							<a href="${cancelUrl}" class="btn btn-primary btn-block bottom-align">
								<spring:theme code="b2bunit.cancel" />
							</a>
						</ycommerce:testId>
					</div>
				</div>
			</fieldset>
		</form:form>
	</div>
</div>