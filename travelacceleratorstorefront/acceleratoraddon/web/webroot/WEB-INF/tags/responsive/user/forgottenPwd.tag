<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<spring:htmlEscape defaultHtmlEscape="true" />


<div class="container">
<div class="forgotten-password">
	<div class="col-xs-12">
		<h2 class="h2"><spring:theme code="forgottenPwd.page.title" text="Reset Password"/></h2>
	</div>
	<div class="description col-xs-12"><spring:theme code="forgottenPwd.description"/></div>
	<form:form method="post" commandName="forgottenPwdForm">
		<div class="control-group col-xs-12 col-sm-6">
			<ycommerce:testId code="login_forgotPasswordEmail_input">
				<formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" mandatory="true"/>
			</ycommerce:testId>
			<ycommerce:testId code="login_forgotPasswordSubmit_button">
				<button class="btn btn-primary btn-block" type="submit">
					<spring:theme code="forgottenPwd.title"/>
				</button>
			</ycommerce:testId>
		</div>
	</form:form>
</div>
</div>
