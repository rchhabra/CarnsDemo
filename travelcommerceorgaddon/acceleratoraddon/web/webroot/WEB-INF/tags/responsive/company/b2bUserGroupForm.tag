<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="b2BUserGroupForm" required="true" type="de.hybris.platform.travelcommerceorgaddon.forms.B2BUserGroupForm"%>
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
		<form:form action="${formUrl}" method="post" commandName="b2BUserGroupForm">
			<fieldset class="fieldset">
				<form:input type="hidden" name="originalUid" path="originalUid" id="originalUid" />
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<formElement:formInputBox idKey="b2busergroup.id" labelKey="b2busergroup.id" path="uid" inputCSS="text" mandatory="true" />
					</div>
					<div class="col-xs-12 col-sm-6">
						<formElement:formInputBox idKey="b2busergroup.name" labelKey="b2busergroup.name" path="name" inputCSS="text" mandatory="true" />
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<formElement:formSelectBox idKey="b2busergroup.parent" labelKey="b2busergroup.parent" path="parentUnit" mandatory="false" selectCSSClass="form-control" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${branchSelectOptions}" />
					</div>
				</div>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
						<ycommerce:testId code="UserGroup_SaveChange_button">
							<button type="submit" class="btn btn-primary btn-block bottom-align" id="SubmitBudget_button">
								<spring:theme code="text.company.save.button" />
							</button>
						</ycommerce:testId>
					</div>
					<div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
						<ycommerce:testId code="back_button">
							<a href="${cancelUrl}" class="btn btn-primary btn-block bottom-align">
								<spring:theme code="b2busergroup.cancel" />
							</a>
						</ycommerce:testId>
					</div>
				</div>
			</fieldset>
		</form:form>
	</div>
</div>