<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-units/details/" var="cancelUrl" htmlEscape="false">
	<spring:param name="unit" value="${uid}" />
</spring:url>
<c:choose>
	<c:when test="${not empty addressData.id}">
		<spring:url value="/my-company/organization-management/manage-units/edit-address/" var="actionUrl" htmlEscape="false">
			<spring:param name="unit" value="${uid}" />
			<spring:param name="addressId" value="${addressData.id}" />
		</spring:url>
	</c:when>
	<c:otherwise>
		<spring:url value="/my-company/organization-management/manage-units/add-address/" var="actionUrl" htmlEscape="false">
			<spring:param name="unit" value="${uid}" />
		</spring:url>
	</c:otherwise>
</c:choose>
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
				<org-common:headline url="${cancelUrl}" backLabelKey="text.company.unit.edit.back" labelKey="text.company.manage.units.addressForm.${empty addressData.id?'create':'edit'}.title" />
				<div class="panel panel-primary panel-list">
					<div class="panel-heading">
						<h3 class="title">
							<spring:theme code="text.company.manage.unit.${empty addressData.id?'addAddress':'editAddress'}"></spring:theme>
						</h3>
					</div>
					<div class="panel-body">
						<form:form cssClass="y_b2bUnitAddAddressform" action="${actionUrl}" method="post" commandName="addressForm">
							<fieldset class="fieldset">
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<formElement:formSelectBox idKey="address.country" labelKey="address.country" path="countryIso" mandatory="true" skipBlank="false" selectCSSClass="form-control" skipBlankMessageKey="address.selectCountry" items="${countryData}" itemValue="isocode" selectedValue="${addressForm.countryIso}" />
									</div>
								</div>
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" selectCSSClass="form-control" skipBlankMessageKey="address.title.pleaseSelect" items="${titleData}" selectedValue="${addressForm.titleCode}" />
									</div>
									<div class="col-xs-12 col-sm-6">
										<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text" mandatory="true" />
									</div>
								</div>
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true" />
									</div>
									<div class="col-xs-12 col-sm-6">
										<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text" mandatory="true" />
									</div>
								</div>
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="text" mandatory="false" />
									</div>
									<div class="col-xs-12 col-sm-6">
										<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text" mandatory="true" />
									</div>
								</div>
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="text" mandatory="true" />
									</div>
								</div>
								<div class="row">
									<div class="form-group col-xs-12 col-sm-6 bottom-align-parent btn-margin">
										<ycommerce:testId code="unitAddress_saveAddress_button">
											<button type="submit" class="btn btn-primary btn-block bottom-align">
												<spring:theme code="text.company.save.button" />
											</button>
										</ycommerce:testId>
									</div>
									<div class="form-group col-xs-12 col-sm-6  bottom-align-parent">
										<ycommerce:testId code="unitAddress_cancelAddress_button">
											<a href="${cancelUrl}" class="btn btn-primary btn-block bottom-align">
												<spring:theme code="text.company.manage.unit.address.cancelButton" />
											</a>
										</ycommerce:testId>
									</div>
								</div>
							</fieldset>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
