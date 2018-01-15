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
<spring:url value="/my-company/organization-management/manage-usergroups/create" var="createUserGroupUrl" htmlEscape="false" />
<c:set var="searchUrl" value="/my-company/organization-management/manage-usergroups?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
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
				<org-common:listHeadline url="${createUserGroupUrl}" labelKey="text.company.${action}.title" urlTestId="Usergroup_createNew_button" />
				<c:choose>
					<c:when test="${empty searchPageData.results}">
						<div class="row">
							<div class="col-xs-12">
								<div class="pagination-bar-results">
									<spring:theme code="text.company.noentries" text="No Entries" />
								</div>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="row">
							<div class="account-orderhistory-pagination">
								<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.${action}.page" additionalParams="${additionalParams}"
									numberPagesShown="${numberPagesShown}" />
							</div>
						</div>
						<div class="account-overview-table">
							<div class="responsive-table">
								<table class="table">
									<tbody>
										<tr class="responsive-table-head hidden-xs">
											<th><spring:theme code="text.company.column.id.name" /></th>
											<th><spring:theme code="text.company.column.name.name" /></th>
											<th><spring:theme code="text.company.column.parentUnit.name" /></th>
											<th><spring:theme code="text.company.status.title" /></th>
										</tr>
										<c:forEach items="${searchPageData.results}" var="group">
											<spring:url value="/my-company/organization-management/manage-usergroups/details/" var="viewUrl" htmlEscape="false">
												<spring:param name="usergroup" value="${group.uid}" />
											</spring:url>
											<spring:url value="/my-company/organization-management/manage-units/details/" var="viewUnitUrl" htmlEscape="false">
												<spring:param name="unit" value="${group.unit.uid}" />
											</spring:url>
											<tr class="responsive-table-item">
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.id.name" /></td>
												<td class="responsive-table-cell"><ycommerce:testId code="${action}_uid_link">
														<a href="${viewUrl}" class="responsive-table-link">${fn:escapeXml(group.uid)}</a>
													</ycommerce:testId></td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.name.name" /></td>
												<td class="responsive-table-cell"><ycommerce:testId code="${action}_name_label">
	                                            ${fn:escapeXml(group.name)}
	                                        </ycommerce:testId></td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.parentUnit.name" /></td>
												<td class="responsive-table-cell"><ycommerce:testId code="${action}_b2bunit_label">
														<a href="${viewUnitUrl}" class="responsive-table-link">${fn:escapeXml(group.unit.name)}</a>
													</ycommerce:testId></td>
												<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.status.title" /></td>
												<td class="responsive-table-cell"><ycommerce:testId code="${action}_status_label">
													<c:choose>
														<c:when test="${fn:length(group.members)>0}">
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
												</ycommerce:testId></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<div class="row">
							<div class="account-orderhistory-pagination">
								<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" hideRefineButton="true" searchUrl="${searchUrl}" msgKey="text.company.${action}.page" additionalParams="${additionalParams}"
									numberPagesShown="${numberPagesShown}" />
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</template:page>
