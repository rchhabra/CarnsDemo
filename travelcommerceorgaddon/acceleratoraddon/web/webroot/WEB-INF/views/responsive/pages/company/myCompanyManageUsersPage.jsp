<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-users/create" var="manageUsersUrl" htmlEscape="false" />
<c:set var="searchUrl" value="/my-company/organization-management/manage-users?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
<jsp:useBean id="additionalParams" class="java.util.HashMap" />
<c:set target="${additionalParams}" property="user" value="${param.user}" />
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
				<org-common:listHeadline url="${manageUsersUrl}" labelKey="text.company.manageusers.label" urlTestId="User_AddUser_button" />
				<c:choose>
					<c:when test="${not empty searchPageData.results}">
						<div class="account-section-content">
							<div class="account-orderhistory-pagination">
								<div class="account-orderhistory-pagination">
									<div class="row">
										<div class="account-orderhistory-pagination">
											<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.manageUser.pageAll" additionalParams="${additionalParams}" numberPagesShown="${numberPagesShown}" />
										</div>
									</div>
								</div>
							</div>
						</div>			
						<div class="account-overview-table">
							<div class="responsive-table">
								<table class="table">
									<tbody>
										<tr class="responsive-table-head hidden-xs">
											<th><spring:theme code="text.company.column.name.name" /></th>
											<th><spring:theme code="text.company.column.roles.name" /></th>
											<th><spring:theme code="text.company.column.parentUnit.name" /></th>
											<th><spring:theme code="text.company.manageUser.user.costCenter" /></th>
											<th><spring:theme code="text.company.status.title" /></th>
										</tr>
										<c:forEach items="${searchPageData.results}" var="user">
											<spring:url value="/my-company/organization-management/manage-users/details/" var="viewUserUrl" htmlEscape="false">
											<spring:param name="user" value="${user.uid}" />
											</spring:url>
											<spring:url value="/my-company/organization-management/manage-units/details/" var="viewUnitUrl" htmlEscape="false">
												<spring:param name="unit" value="${user.unit.uid}" />
											</spring:url>
											<tr class="responsive-table-item">
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.name.name" /></td>
												<td class="responsive-table-cell">
													<ycommerce:testId code="my-company_username_label">
														<a href="${viewUserUrl}" class="responsive-table-link">${fn:escapeXml(user.firstName)}&nbsp;${fn:escapeXml(user.lastName)}</a>
													</ycommerce:testId>
												</td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.roles.name" /></td>
												<td class="responsive-table-cell"><ycommerce:testId code="my-company_user_roles_label">
													<c:forEach items="${user.roles}" var="role">
														<spring:theme code="b2busergroup.${role}.name" />
														<br />
													</c:forEach>
														</ycommerce:testId></td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.parentUnit.name" /></td>
												<td class="responsive-table-cell"><ycommerce:testId code="my-company_user_unit_label">
													<a href="${viewUnitUrl}" class="responsive-table-link">${fn:escapeXml(user.unit.name)}</a>
													</ycommerce:testId></td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.manageUser.user.costCenter" /></td>
												<td class="responsive-table-cell">
													<ycommerce:testId code="my-company_user_costcenter_label">
														<c:forEach items="${user.unit.costCenters}" var="costCenter">
															<spring:url value="/my-company/organization-management/manage-costcenters/view/" var="viewCostCenterUrl" htmlEscape="false">
															<spring:param name="costCenterCode" value="${costCenter.code}" />
															</spring:url>
															<a href="${viewCostCenterUrl}" class="responsive-table-link">${fn:escapeXml(costCenter.code)}</a>
															<br />
														</c:forEach>
													</ycommerce:testId>
												</td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg">
													<spring:theme code="text.company.status.title" />
												</td>
												<td class="responsive-table-cell">
													<ycommerce:testId code="costCenter_status_label">
														<c:choose>
															<c:when test="${user.active}">
																<span>
																	<spring:theme code="text.company.status.active.true" />
																</span>
															</c:when>
															<c:otherwise>
																<span class="account-status-inactive">
																	<spring:theme code="text.company.status.active.false" />
																</span>
															</c:otherwise>
														</c:choose>
													</ycommerce:testId>
												</td>
											</tr>
										</c:forEach>
										<c:if test="${totalForQuery ne null}">
											<tr class="responsive-table-item">
												<td class="responsive-table-cell hidden-xs">&nbsp;</td>
												<td class="responsive-table-cell hidden-xs">&nbsp;</td>
												<td class="responsive-table-cell hidden-xs">&nbsp;</td>
												<td class="responsive-table-cell hidden-xs">&nbsp;</td>
												<td class="responsive-table-cell hidden-xs">&nbsp;</td>
											</tr>
										</c:if>
									</tbody>
								</table>
							</div>
						</div>
						<div class="row">
							<div class="account-orderhistory-pagination">
								<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" msgKey="text.company.manageUser.pageAll" hideRefineButton="true" numberPagesShown="${numberPagesShown}" />
								<c:if test="${empty searchPageData.results}">
									<p><spring:theme code="text.company.noentries" /></p>
								</c:if>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="account-section-content	col-md-6 col-md-push-3 content-empty">
							<spring:theme code="text.company.manageUser.noUser" />
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</template:page>
