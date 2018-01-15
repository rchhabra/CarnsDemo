<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-users/resetpassword" var="resetpasswordUrl">
    <spring:param name="user" value="${customerResetPasswordForm.uid}" />
</spring:url>
<template:page pageTitle="${pageTitle}">
    <div class="my-company-section">
		<div class="container">
			<div class="col-xs-12 col-sm-3 side-to-top">
				<aside id="sidebar">
					<div class="promotions hidden-xs">
						<cms:pageSlot position="SideContent" var="feature" class="accountPageSideContent">
							<cms:component component="${feature}" element="section" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
			<div class="col-xs-12 col-sm-9">
				<div class="row heading-with-button">
					<div class="col-xs-7 col-sm-8">
						<h2 class="h2">
							<spring:theme code="text.account.profile.updatePasswordForm" />
						</h2>
					</div>
				</div>
				<form:form action="${resetpasswordUrl}" method="post" commandName="customerResetPasswordForm" autocomplete="off">
					<form:input type="hidden" name="uid" path="uid" id="uid" />
					<div class="row">
						<div class="col-xs-12 col-sm-6">
							<formElement:formPasswordBox idKey="profile-newPassword" labelKey="profile.newPassword" labelCSS="control-label" path="newPassword" inputCSS="form-control text password strength" mandatory="true" />
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12 col-sm-6">
							<formElement:formPasswordBox idKey="profile.checkNewPassword" labelKey="profile.checkNewPassword" labelCSS="control-label" path="checkNewPassword" inputCSS="form-control text password" mandatory="true" />
						</div>
					</div>
					<div class="row ">
						<div class="col-xs-12 col-sm-6">
							<button class="btn btn-primary btn-block">
								<spring:theme code="text.account.profile.updatePasswordForm" />
							</button>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</template:page>