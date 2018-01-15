<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="customFormElement" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/customFormElement"%>
<%@ attribute name="b2BCustomerForm" required="true" type="de.hybris.platform.travelcommerceorgaddon.forms.B2BCustomerForm"%>
<%@ attribute name="saveUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="addEditLabel" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-primary panel-list">
	<div class="panel-heading">
		<h3 class="title">
			<spring:theme code="${addEditLabel}"></spring:theme>
		</h3>
	</div>
	<form:form action="${saveUrl}" method="post" commandName="b2BCustomerForm">
		<div class="panel-body">
			<fieldset class="fieldset">
				<div class="row">
					<div class="col-sm-6 col-xs-12">
						<formElement:formSelectBox idKey="user.title" labelKey="user.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" selectCSSClass="form-control" items="${titleData}" />
					</div>
					<form:input type="hidden" name="uid" path="uid" id="uid" />
					<div class="col-xs-12 col-sm-6">
						<formElement:formInputBox idKey="user.firstName" labelKey="user.firstName" path="firstName" inputCSS="text" mandatory="true" />
					</div>
				</div>
				<div class="row">
					<div class="col-sm-6 col-xs-12 nowrap">
						<formElement:formInputBox idKey="user.lastName" labelKey="user.lastName" path="lastName" inputCSS="text" mandatory="true" />
					</div>
					<div class="col-sm-6 col-xs-12 nowrap">
						<formElement:formInputBox idKey="user.email" labelKey="user.email" path="email" inputCSS="text" mandatory="true" />
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<formElement:formSelectBox idKey="text.company.user.unit.title" skipBlank="false" labelKey="text.company.user.unit.title" path="parentB2BUnit" selectCSSClass="form-control" mandatory="true" items="${b2bUnits}" disabled="${not empty param.unit and not empty param.role}"
							skipBlankMessageKey="form.select.empty" />
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<div class="form-group" id="roles">
							<customFormElement:formCheckboxes idKey="text.company.user.roles" labelKey="text.company.user.roles" path="roles" items="${roles}" disabled="${not empty param.unit and not empty param.role}" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
						<ycommerce:testId code="User_Save_button">
							<button type="submit" class="btn btn-primary btn-block bottom-align">
								<spring:theme code="text.company.save.button" />
							</button>
						</ycommerce:testId>
					</div>
					<div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
						<ycommerce:testId code="User_Cancel_button">
							<a href="${cancelUrl}" class="btn btn-primary btn-block bottom-align">
								<spring:theme code="text.company.cancel.button" />
							</a>
						</ycommerce:testId>
					</div>
				</div>
			</fieldset>
		</div>
	</form:form>
</div>