<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-users/" var="backToManageUsersUrl" htmlEscape="false" />
<spring:url value="/my-company/organization-management/manage-users/edit" var="editUserUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/disable" var="disableUserUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/enable" var="enableUserUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/resetpassword" var="resetPasswordUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/approvers" var="approversUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/permissions" var="permissionsUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-users/usergroups" var="usergroupsUrl" htmlEscape="false">
	<spring:param name="user" value="${customerData.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/details" var="unitDetailsUrl" htmlEscape="false">
	<spring:param name="unit" value="${customerData.unit.uid}" />
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
				<org-common:headline url="${backToManageUsersUrl}" backLabelKey="text.company.manageUsers.back.button" labelKey="text.company.manageUser.userDetails" />
				<div class="User-details col-xs-12">
					<div class="row">
						<div class="col-xs-12 well well-lg well-tertiary">
							<div class="col-sm-12 col-no-padding">
								<div class="col-sm-6 item-wrapper">
									<div class="item-group">
										<span class="item-label  ">
											<spring:theme code="text.company.user.email" />
										</span>
										<span class="item-value  "> ${fn:escapeXml(customerData.displayUid)} </span>
									</div>
									<div class="item-group">
										<span class="item-label  ">
											<spring:theme code="text.company.manage.units.user.name" />
										</span>
										<span class="item-value  ">
											<spring:theme code="text.company.user.${customerData.titleCode}.name" />
											&nbsp;${fn:escapeXml(customerData.firstName)}&nbsp;${fn:escapeXml(customerData.lastName)}
										</span>
									</div>
									<div class="item-group">
										<span class="item-label  ">
											<spring:theme code="text.company.user.parentBusinessUnit" />
										</span>
										<span class="item-value  ">
											<a href="${unitDetailsUrl}">${fn:escapeXml(customerData.unit.name)}</a>
										</span>
									</div>
								</div>
								<div class="col-sm-6 item-wrapper">
									<div class="item-group">
										<span class="item-label  ">
											<spring:theme code="text.company.manageUser.roles" />
										</span>
										<span class="item-value  ">
											<c:forEach items="${customerData.roles}" var="group">
												<spring:theme code="b2busergroup.${group}.name" />
												<br />
											</c:forEach>
										</span>
									</div>
									<div class="item-group">
										<span class="item-label  ">
											<spring:theme code="text.company.user.userEnabledStatus" />
										</span>
										<span class="item-value  ">
											<c:choose>
												<c:when test="${customerData.active}">
													<spring:theme code="text.company.manage.unit.user.enable" />
												</c:when>
												<c:otherwise>
													<spring:theme code="text.company.manage.unit.user.disable" />
												</c:otherwise>
											</c:choose>
										</span>
									</div>
								</div>
								<div class="col-sm-6 item-wrapper">
									<c:if test="${not empty customerData.contactNumber}">
										<div class="item-group">
											<span class="item-label  ">
												<spring:theme code="text.company.unit.contactNumber" />
											</span>
											<span class="item-value  "> ${fn:escapeXml(customerData.contactNumber)} </span>
										</div>
									</c:if>
								</div>
							</div>
							<div class="col-xs-12 col-sm-5  item-action">
								<a href="${editUserUrl}" class="button edit btn btn-block btn-primary">
									<spring:theme code="text.company.manageUser.button.edit" />
								</a>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="panel button-wrap">
						<div class="col-xs-12 col-sm-4 pull-right">
							<button href="${resetPasswordUrl}" class="btn btn-primary btn-block">
								<spring:theme code="text.company.user.resetPassword" />
							</button>
						</div>
						<c:choose>
							<c:when test="${customerData.active}">
								<div class="col-xs-12 col-sm-4 pull-right">
									<button href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-disable">
										<spring:theme code="text.company.manageusers.button.disableuser" text="Disable User" />
									</button>
								</div>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${customerData.unit.active}">
										<div class="col-xs-12 col-sm-4 pull-right">
											<form:form action="${enableUserUrl}">
												<button type="submit" class="btn btn-primary btn-block">
													<spring:theme code="text.company.manageusers.button.enableuser" />
												</button>
											</form:form>
										</div>
									</c:when>
									<c:otherwise>
										<div class="col-xs-12 col-sm-4 pull-right">
											<button type="button" class="btn btn-primary btn-block" disabled>
												<spring:theme code="text.company.manageusers.button.enableuser" />
											</button>
										</div>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<company:actionConfirmationModal id="disable" targetUrl="${disableUserUrl}" modalTitle="text.company.manageusers.button.disableuser" messageKey="text.company.manageuser.disableuser.confirmation" messageArguments="${customerData.uid}" />
				<%-- Approvers --%>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manage.units.header.approvers" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:if test="${not empty customerData.approvers}">
										<div class="account-cards clearfix">
											<c:forEach items="${customerData.approvers}" var="user">
												<spring:url value="/my-company/organization-management/manage-users/details" var="approverUrl" htmlEscape="false">
													<spring:param name="user" value="${user.email}" />
												</spring:url>
												<div class="card col-xs-12 col-sm-6 col-md-4">
													<ul>
														<li>
															<ycommerce:testId code="user_name_link_details">
																<a href="${approverUrl}">${fn:escapeXml(user.name)}</a>
															</ycommerce:testId>
														</li>
														<li>
															<ycommerce:testId code="user_email">
                                            ${fn:escapeXml(user.email)}
                                        </ycommerce:testId>
														</li>
													</ul>
													<div class="form-group account-cards-actions col-xs-12">
														<button href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-removeApprover-${ycommerce:normalizedCode(user.uid)}">
															<spring:theme code="text.company.users.remove.button" text="Remove" />
														</button>
														<spring:url value="/my-company/organization-management/manage-users/approvers/remove/" var="removeApproverUrl" htmlEscape="false">
															<spring:param name="user" value="${customerData.uid}" />
															<spring:param name="approver" value="${user.uid}" />
														</spring:url>
														<company:actionConfirmationModal id="removeApprover-${ycommerce:normalizedCode(user.uid)}" targetUrl="${removeApproverUrl}" messageKey="text.company.users.remove.confirmation.b2bapprovergroup" messageArguments="${user.uid}, ${customerData.uid}"
															actionButtonLabelKey="text.company.users.remove.button" />
													</div>
												</div>
											</c:forEach>
										</div>
									</c:if>
								</div>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${approversUrl}">
										<button class="btn btn-primary btn-block">
											<span class="visible-xs">
												<spring:theme code="text.company.add.button" />
											</span>
											<span class="hidden-xs">
												<c:choose>
													<c:when test="${addNew}">
														<spring:theme code="text.company.addNew.button" />
													</c:when>
													<c:otherwise>
														<spring:theme code="text.company.addExisting.button" />
													</c:otherwise>
												</c:choose>
											</span>
										</button>
									</a>
								</div>
							</fieldset>
						</div>
						<div></div>
					</div>
				</div>
				<%-- Permissions --%>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manageUser.permission.title" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:if test="${not empty customerData.permissions}">
										<div class="account-cards clearfix">
											<c:forEach items="${customerData.permissions}" var="permission">
												<div class="card col-xs-12 col-sm-6 col-md-4">
													<company:permissionCardDetails permission="${permission}" action="permission" />
													<div class="form-group account-cards-actions col-xs-12">
														<button href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-removePermission-${ycommerce:normalizedCode(permission.code)}">
															<spring:theme code="text.company.users.remove.button" text="Remove" />
														</button>
														<spring:url value="/my-company/organization-management/manage-users/permissions/remove/" var="removePermissionUrl" htmlEscape="false">
															<spring:param name="user" value="${customerData.uid}" />
															<spring:param name="permission" value="${permission.code}" />
														</spring:url>
														<company:actionConfirmationModal id="removePermission-${ycommerce:normalizedCode(permission.code)}" targetUrl="${removePermissionUrl}" messageKey="text.company.users.remove.confirmation.permission" messageArguments="${permission.code}, ${customerData.uid}"
															actionButtonLabelKey="text.company.users.remove.button" />
													</div>
												</div>
											</c:forEach>
										</div>
									</c:if>
								</div>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${permissionsUrl}">
										<button class="btn btn-primary btn-block">
											<span class="visible-xs">
												<spring:theme code="text.company.add.button" />
											</span>
											<span class="hidden-xs">
												<c:choose>
													<c:when test="${addNew}">
														<spring:theme code="text.company.addNew.button" />
													</c:when>
													<c:otherwise>
														<spring:theme code="text.company.addExisting.button" />
													</c:otherwise>
												</c:choose>
											</span>
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<%-- User Groups --%>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manageUser.usergroups.title" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:if test="${not empty customerData.permissionGroups}">
										<div class="account-cards clearfix">
											<c:forEach items="${customerData.permissionGroups}" var="group">
												<div class="card col-xs-12 col-sm-6 col-md-4">
													<ul>
														<li>
															<ycommerce:testId code="permissiongroup_id_link">
																<spring:url value="/my-company/organization-management/manage-usergroups/details/" var="permissionGroupUrl" htmlEscape="false">
																	<spring:param name="usergroup" value="${group.uid}" />
																</spring:url>
																<c:choose>
																	<c:when test="${group.editable}">
																		<a href="${permissionGroupUrl}">${fn:escapeXml(group.uid)}</a>
																	</c:when>
																	<c:otherwise>
                                                    ${fn:escapeXml(group.uid)}
                                                </c:otherwise>
																</c:choose>
															</ycommerce:testId>
														</li>
														<li>
															<ycommerce:testId code="permissiongroup_name_link">
                                            ${fn:escapeXml(group.name)}
                                        </ycommerce:testId>
														</li>
														<li>
															<ycommerce:testId code="permissiongroup_parentunit_link">
																<spring:url value="/my-company/organization-management/manage-units/details" var="parentUnitUrl" htmlEscape="false">
																	<spring:param name="unit" value="${group.unit.uid}" />
																</spring:url>
																<c:choose>
																	<c:when test="${group.editable}">
																		<a href="${parentUnitUrl}">${fn:escapeXml(group.unit.name)}</a>
																	</c:when>
																	<c:otherwise>
                                                    ${fn:escapeXml(group.unit.name)}
                                                </c:otherwise>
																</c:choose>
															</ycommerce:testId>
														</li>
													</ul>
													<div class="form-group account-cards-actions col-xs-12">
														<button href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-removeUserGroup-${ycommerce:normalizedCode(group.uid)}">
															<spring:theme code="text.company.users.remove.button" text="Remove" />
														</button>
														<spring:url value="/my-company/organization-management/manage-users/usergroups/remove/" var="removeUserGroupUrl" htmlEscape="false">
															<spring:param name="user" value="${customerData.uid}" />
															<spring:param name="usergroup" value="${group.uid}" />
														</spring:url>
														<company:actionConfirmationModal id="removeUserGroup-${ycommerce:normalizedCode(group.uid)}" targetUrl="${removeUserGroupUrl}" messageKey="text.company.users.remove.confirmation.usergroup" messageArguments="${group.uid}, ${customerData.uid}"
															actionButtonLabelKey="text.company.users.remove.button" />
													</div>
												</div>
											</c:forEach>
										</div>
									</c:if>
								</div>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${usergroupsUrl}">
										<button class="btn btn-primary btn-block">
											<span class="visible-xs">
												<spring:theme code="text.company.add.button" />
											</span>
											<span class="hidden-xs">
												<c:choose>
													<c:when test="${addNew}">
														<spring:theme code="text.company.addNew.button" />
													</c:when>
													<c:otherwise>
														<spring:theme code="text.company.addExisting.button" />
													</c:otherwise>
												</c:choose>
											</span>
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12 col-sm-4 col-sm-offset-8" id="backButton">
						<org-common:back cancelUrl="${backToManageUsersUrl}" displayTextMsgKey="text.company.manageUsers.back.button" />
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
