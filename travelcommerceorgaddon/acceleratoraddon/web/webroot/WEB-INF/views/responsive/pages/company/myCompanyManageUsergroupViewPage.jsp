<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-usergroups/" var="backToUserGroupsUrl" htmlEscape="false" />
<spring:url value="/my-company/organization-management/manage-usergroups/permissions/" var="permissionsUrl" htmlEscape="false">
	<spring:param name="usergroup" value="${usergroup.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-usergroups/members/" var="membersUrl" htmlEscape="false">
	<spring:param name="usergroup" value="${usergroup.uid}" />
</spring:url>
<c:if test="${empty editUrl}">
	<spring:url value="/my-company/organization-management/manage-usergroups/edit/" var="editUrl" htmlEscape="false">
		<spring:param name="usergroup" value="${usergroup.uid}" />
	</spring:url>
</c:if>
<c:if test="${empty disableUrl}">
	<spring:url value="/my-company/organization-management/manage-usergroups/disable/" var="disableUrl" htmlEscape="false">
		<spring:param name="usergroup" value="${usergroup.uid}" />
	</spring:url>
</c:if>
<c:if test="${empty removeUrl}">
	<spring:url value="/my-company/organization-management/manage-usergroups/remove/" var="deleteUrl" htmlEscape="false">
		<spring:param name="usergroup" value="${usergroup.uid}" />
	</spring:url>
</c:if>
<c:set var="usersSelected" value="${(fn:length(usergroup.members) > 0) ? true : false}" />
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
				<org-common:headline url="${backToUserGroupsUrl}" labelKey="text.company.manageUsergroups.detailsUserGroup.title" backLabelKey="text.company.usergroup.back.button" />
				<div class="User-group-details col-xs-12">
					<div class="row">
						<div class="col-xs-12 well well-lg well-tertiary">
							<div class="col-sm-12 col-no-padding">
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.usergroup.id.title" />
										</span>
										<span class="item-value"> ${fn:escapeXml(usergroup.uid)} </span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.usergroup.name.title" />
										</span>
										<span class="item-value"> ${fn:escapeXml(usergroup.name)} </span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.usergroup.unit.title" />
										</span>
										<span class="item-value"> ${fn:escapeXml(usergroup.unit.name)} </span>
									</div>
								</div>
							</div>
							<div class="col-xs-12 col-sm-5  item-action">
								<a href="${editUrl}" class="button edit btn btn-block btn-primary">
									<spring:theme code="text.company.usergroup.edit.button" />
								</a>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="panel button-wrap">
						<c:if test="${usersSelected}">
							<div class="col-xs-12 col-sm-4 pull-right">
								<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-disable-usergroup">
									<spring:theme code="text.company.usergroup.disable.label" />
								</a>
							</div>
							<company:actionConfirmationModal id="disable-usergroup" targetUrl="${disableUrl}" messageKey="text.company.usergroup.disable.warning.message" messageArguments="${usergroup.uid}" />
						</c:if>
						<div class="col-xs-12 col-sm-4 pull-right">
							<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-delete-usergroup">
								<spring:theme code="text.company.usergroup.delete.label" />
							</a>
						</div>
						<company:actionConfirmationModal id="delete-usergroup" targetUrl="${deleteUrl}" messageKey="text.company.usergroup.delete.warning.message" actionButtonLabelKey="text.company.delete.button" />
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manageUser.permission.title" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<c:if test="${not empty usergroup.permissions}">
									<div class="row">
										<c:forEach items="${usergroup.permissions}" var="permission">
											<div class="form-group card col-xs-12 col-sm-6 col-md-4">
												<company:permissionCardDetails permission="${permission}" action="permission" />
											</div>
										</c:forEach>
									</div>
								</c:if>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${permissionsUrl}">
										<button class="btn btn-primary btn-block">
											<spring:theme code="text.company.addExisting.button" />
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.usergroup.members" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<c:if test="${not empty usergroup.members}">
									<div class="row">
										<c:forEach items="${usergroup.members}" var="user">
											<spring:url value="/my-company/organization-management/manage-users/details/" var="userDetailUrl" htmlEscape="false">
												<spring:param name="user" value="${user.uid}" />
											</spring:url>
											<div class="form-group card col-xs-12 col-sm-6 col-md-4">
												<ul>
													<li>
														<ycommerce:testId code="member_name_link">
															<a href="${userDetailUrl}">${fn:escapeXml(user.name)}</a>
														</ycommerce:testId>
													</li>
													<li>
														<ycommerce:testId code="member_email_label">${fn:escapeXml(user.uid)}</ycommerce:testId>
													</li>
												</ul>
											</div>
										</c:forEach>
									</div>
								</c:if>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${membersUrl}">
										<button class="btn btn-primary btn-block">
											<spring:theme code="text.company.addExisting.button" />
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4 col-sm-offset-8" id="backButton">
						<org-common:back cancelUrl="${backToUserGroupsUrl}" displayTextMsgKey="text.company.usergroup.back.button" />
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>