<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-permissions/edit" var="editPermissionDetailsUrl" htmlEscape="false">
	<spring:param name="permissionCode" value="${permissionData.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-permissions/enable" var="enableUrl" htmlEscape="false">
	<spring:param name="permissionCode" value="${permissionData.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details" var="unitDetailsUrl" htmlEscape="false">
	<spring:param name="unit" value="${permissionData.unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-permissions/disable" var="confirmDisableUrl" htmlEscape="false">
	<spring:param name="permissionCode" value="${permissionData.code}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-permissions" var="backUrl" htmlEscape="false" />
<template:page pageTitle="${pageTitle}">
	<div class="container my-company-section">
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
			<org-common:headline url="${backUrl}" labelKey="text.company.managePermissions.viewDetails.page.title" backLabelKey="text.company.managePermissions.viewDetails.back" />
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12 well well-lg well-tertiary">
						<div class="col-sm-12 col-no-padding">
							<div class="col-sm-4 item-wrapper">
								<div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.managePermissions.name.label" />
									</span>
									<span class="item-value"> ${fn:escapeXml(permissionData.code)} </span>
								</div>
								<div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.managePermissions.unit.label" />
									</span>
									<span class="item-value">
										<a href="${unitDetailsUrl}">${fn:escapeXml(permissionData.unit.name)}</a>
									</span>
								</div>
								<div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.permissions.type.label" />
									</span>
									<span class="item-value"> ${fn:escapeXml(permissionData.b2BPermissionTypeData.name)} </span>
								</div>
							</div>
							<div class="col-sm-4 item-wrapper">
								<div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.managePermissions.timespan.label" />
									</span>
									<span class="item-value">
										<company:permissionTimespan permission="${permissionData}" />
									</span>
								</div>
								<div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.managePermissions.value.label" />
									</span>
									<span class="item-value">
										<company:permissionAmount permission="${permissionData}" displayBracketsAroundCurrency="true" />
									</span>
								</div>
							</div>
							<div class="col-sm-4 item-wrapper">
								<div class="item-group">
									<span class="item-label">
										<spring:theme code="text.company.column.status.name" />
									</span>
									<span class="item-value">
										<c:choose>
											<c:when test="${permissionData.active}">
												<spring:theme code="text.company.managePermissions.status.enabled" />
											</c:when>
											<c:otherwise>
												<spring:theme code="text.company.managePermissions.status.disabled" />
											</c:otherwise>
										</c:choose>
									</span>
								</div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-5 item-action">
							<a href="${editPermissionDetailsUrl}" class="button edit btn btn-block btn-primary">
								<spring:theme code="text.company.costCenter.button.displayName" />
							</a>
						</div>
					</div>
				</div>
				<div class="row heading-with-button">
					<div class="col-sm-12">
						<c:choose>
							<c:when test="${permissionData.active}">
								<div class="accountActions-link  col-xs-12 col-sm-4 col-sm-offset-8">
									<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-disable">
										<spring:theme code="text.company.permission.disable.confirm" text="Disable Permission" />
									</a>
								</div>
								<company:actionConfirmationModal id="disable" targetUrl="${confirmDisableUrl}" modalTitle="text.company.permission.disable.confirm" messageKey="text.company.permission.disable.confirm.message" />
							</c:when>
							<c:otherwise>
								<div class="accountActions-link  col-xs-12 col-sm-4 col-sm-offset-8">
									<a href="${enableUrl}" class="btn btn-primary btn-block enable-link">
										<spring:theme code="text.company.permission.enable.confirm" />
									</a>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
