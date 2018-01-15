<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-units/" var="cancelUrl" htmlEscape="false" />
<spring:url value="/my-company/organization-management/manage-units/edit" var="editUnitUrl" htmlEscape="false">
	<spring:param name="unit" value="${unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/create" var="createUnitUrl" htmlEscape="false">
	<spring:param name="unit" value="${unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/disable" var="disableUnitUrl" htmlEscape="false">
	<spring:param name="unit" value="${unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/enable" var="enableUnitUrl" htmlEscape="false">
	<spring:param name="unit" value="${unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/add-address" var="addUnitAddressUrl" htmlEscape="false">
	<spring:param name="unit" value="${unit.uid}" />
</spring:url>
<spring:url value="/my-company/organization-management/manage-units/addcostcenter" var="addCostcenterUrl" htmlEscape="false">
	<spring:param name="unit" value="${unit.uid}" />
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
				<org-common:headline url="${cancelUrl}" labelKey="text.company.manage.units.unitDetails" backLabelKey="text.company.unit.details.back" />
				<div class="Unit-details col-xs-12">
					<div class="row">
						<div class="col-xs-12 well well-lg well-tertiary">
							<div class="col-sm-12 col-no-padding">
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.unit.id" />
										</span>
										<span class="item-value"> ${fn:escapeXml(unit.uid)}</span>
									</div>
								</div>
								<div class="col-sm-4 item-wrapper">
									<div class="item-group">
										<span class="item-label">
											<spring:theme code="text.company.unit.name" />
										</span>
										<span class="item-value"> ${fn:escapeXml(unit.name)}</span>
									</div>
								</div>
								<c:if test="${not empty unit.unit.uid}">
									<div class="col-sm-4 item-wrapper">
										<div class="item-group">
											<span class="item-label">
												<spring:theme code="text.company.unit.parent" />
											</span>
											<span class="item-value"> ${fn:escapeXml(unit.unit.uid)} </span>
										</div>
									</div>
								</c:if>
								<c:if test="${not empty unit.approvalProcessName}">
									<div class="col-sm-4 item-wrapper">
										<div class="item-group">
											<span class="item-label">
												<spring:theme code="text.company.unit.approvalProcess" />
											</span>
											<span class="item-value"> ${fn:escapeXml(unit.approvalProcessName)} </span>
										</div>
									</div>
								</c:if>
								<div class="col-xs-12 col-sm-5  item-action">
									<a href="${editUnitUrl}" class="button edit btn btn-block btn-primary">
										<spring:theme code="text.company.manage.units.button.editUnit" />
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="panel button-wrap">
						<div class="col-xs-12 col-sm-4 pull-right">
							<c:choose>
								<c:when test="${unit.active}">
									<c:if test="${unit.uid != user.unit.uid}">
										<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-disable-unit">
											<spring:theme code="text.company.manage.units.button.disableUnit" />
										</a>
										<company:actionConfirmationModal id="disable-unit" targetUrl="${disableUnitUrl}" modalTitle="text.company.manage.units.unit.disable" messageKey="text.company.manage.units.disableUnit.confirmation" />
									</c:if>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${unit.unit.active}">
											<a href="${enableUnitUrl}" class="btn btn-primary btn-block">
												<spring:theme code="text.company.manage.units.button.enableUnit" />
											</a>
										</c:when>
										<c:otherwise>
											<div class="pull-right">
												<spring:theme code="text.company.manage.units.parentUnit.disabled" />
											</div>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manage.units.addresses" count="${fn:length(unit.addresses)}" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:forEach items="${unit.addresses}" var="address">
										<div class="form-group card col-xs-12 col-sm-6 col-md-4">
											<spring:url value="/my-company/organization-management/manage-units/edit-address/" var="editUnitAddressUrl" htmlEscape="false">
												<spring:param name="unit" value="${unit.uid}" />
												<spring:param name="addressId" value="${address.id}" />
											</spring:url>
											<spring:url value="/my-company/organization-management/manage-units/remove-address/" var="removeUnitAddressUrl" htmlEscape="false">
												<spring:param name="unit" value="${unit.uid}" />
												<spring:param name="addressId" value="${address.id}" />
											</spring:url>
											<ul>
												<li>${fn:escapeXml(address.line1)}</li>
												<li>${fn:escapeXml(address.line2)}</li>
												<li>${fn:escapeXml(address.town)}</li>
												<li>${fn:escapeXml(address.postalCode)}</li>
												<li>${fn:escapeXml(address.country.name)}</li>
											</ul>
											<div class="form-group account-cards-actions col-xs-12">
												<a href="#" data-toggle="modal" data-target="#action-confirmation-modal-remove-address">
													<button class="btn btn-primary btn-block">
														<spring:theme code="text.company.manage.unit.address.removeButton" />
													</button>
												</a>
											</div>
											<div class="form-group account-cards-actions col-xs-12">
												<a href="${editUnitAddressUrl}">
													<button class="btn btn-primary btn-block">
														<spring:theme code='text.company.manage.units.edit' />
													</button>
												</a>
											</div>
										</div>
									</c:forEach>
								</div>
								<company:actionConfirmationModal id="remove-address" targetUrl="${removeUnitAddressUrl}" modalTitle="text.company.manage.units.delete.address" messageKey="text.company.manage.units.delete.address.message" actionButtonLabelKey="text.company.manage.units.delete.address" />
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${addUnitAddressUrl}">
										<button class="btn btn-primary btn-block">
											<spring:theme code="text.company.addNew.button" />
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manage.units.accountManagers" count="${fn:length(unit.accountManagers)}" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:forEach items="${unit.accountManagers}" var="accountManager">
										<div class="form-group card col-xs-12 col-sm-6 col-md-4">
											<ul>
												<li>${fn:escapeXml(accountManager.name)}</li>
											</ul>
										</div>
									</c:forEach>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manage.units.costCenters" count="${fn:length(unit.costCenters)}" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:forEach items="${unit.costCenters}" var="b2bCostCenter">
										<div class="form-group card col-xs-12 col-sm-6 col-md-4">
											<spring:url value="/my-company/organization-management/manage-units/costcenter/" var="viewCostcenterUrl" htmlEscape="false">
												<spring:param name="unit" value="${unit.uid}" />
												<spring:param name="costCenterCode" value="${b2bCostCenter.code}" />
											</spring:url>
											<ul>
												<li>
													<a href="${viewCostcenterUrl}">${fn:escapeXml(b2bCostCenter.code)}</a>
												</li>
												<li>${fn:escapeXml(b2bCostCenter.name)}</li>
												<li>${fn:escapeXml(b2bCostCenter.currency.name)}</li>
											</ul>
										</div>
									</c:forEach>
								</div>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${addCostcenterUrl}">
										<button class="btn btn-primary btn-block">
											<spring:theme code="text.company.addNew.button" />
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<div class="panel panel-primary panel-list account-list">
					<org-common:selectEntityHeadline labelKey="text.company.manage.units.childUnits" count="${fn:length(unit.children)}" />
					<div class="panel-body collapse in">
						<div class="form account-cards">
							<fieldset class="fieldset">
								<div class="row">
									<c:forEach items="${unit.children}" var="unit">
										<div class="form-group card col-xs-12 col-sm-6 col-md-4">
											<spring:url value="/my-company/organization-management/manage-units/details" var="unitUrl" htmlEscape="false">
												<spring:param name="unit" value="${unit.uid}" />
											</spring:url>
											<ul>
												<li>
													<a href="${unitUrl}">${unit.uid}</a>
												</li>
												<li>${unit.name}</li>
											</ul>
										</div>
									</c:forEach>
								</div>
								<div class="accountActions-link  col-xs-12 col-sm-5 col-sm-offset-7">
									<a href="${createUnitUrl}">
										<button class="btn btn-primary btn-block">
											<spring:theme code="text.company.addNew.button" />
										</button>
									</a>
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<spring:url value="/my-company/organization-management/manage-units/createuser" var="createUserUrl" htmlEscape="false">
					<spring:param name="unit" value="${unit.uid}" />
				</spring:url>
				<spring:url value="/my-company/organization-management/manage-units/administrators" var="editAdministratorUrl" htmlEscape="false">
					<spring:param name="unit" value="${unit.uid}" />
					<spring:param name="role" value="b2badmingroup" />
				</spring:url>
				<spring:url value="/my-company/organization-management/manage-units/managers" var="editManagersUrl" htmlEscape="false">
					<spring:param name="unit" value="${unit.uid}" />
					<spring:param name="role" value="b2bmanagergroup" />
				</spring:url>
				<spring:url value="/my-company/organization-management/manage-units/approvers" var="editApproversUrl" htmlEscape="false">
					<spring:param name="unit" value="${unit.uid}" />
				</spring:url>
				<spring:url value="/my-company/organization-management/manage-units/customers" var="editCustomersUrl" htmlEscape="false">
					<spring:param name="unit" value="${unit.uid}" />
					<spring:param name="role" value="b2bcustomergroup" />
				</spring:url>
				<company:userList users="${unit.approvers}" action="approvers" role="b2bapprovergroup" editUrl="${editApproversUrl}" createUrl="${createUserUrl}" />
				<company:userList users="${unit.administrators}" action="administrators" role="b2badmingroup" editUrl="${editAdministratorUrl}" createUrl="${createUserUrl}" />
				<company:userList users="${unit.managers}" action="managers" role="b2bmanagergroup" editUrl="${editManagersUrl}" createUrl="${createUserUrl}" />
				<company:userList users="${unit.customers}" action="customers" role="b2bcustomergroup" editUrl="${editCustomersUrl}" createUrl="${createUserUrl}" />
				<div class="row">
					<div class="col-xs-12 col-sm-4 col-sm-offset-8" id="backButton">
						<a href="${cancelUrl}" class="btn btn-primary btn-block">
							<spring:theme code="text.company.unit.details.back" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
