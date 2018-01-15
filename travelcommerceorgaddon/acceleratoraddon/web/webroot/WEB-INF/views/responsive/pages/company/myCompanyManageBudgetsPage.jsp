<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-budgets/add" var="addBudgetUrl" htmlEscape="false" />
<c:set var="searchUrl" value="/my-company/organization-management/manage-budgets?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />
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
							<spring:theme code="text.company.manage.budget.title" text="Budgets" />
						</h2>
					</div>
					<div class="col-xs-5 col-sm-4 button-inline">
						<a href="${addBudgetUrl}" class="btn btn-primary btn-block">
							<spring:theme code="text.company.budget.createButton.displayName" text="Add New" />
						</a>
					</div>
				</div>
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
						<%-- to be changed --%>
						<div class="row">
							<div class="account-orderhistory-pagination">
								<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" msgKey="text.company.budget.page" numberPagesShown="${numberPagesShown}" hideRefineButton="true" />
							</div>
						</div>
						<%-- to be changed --%>
						<div class="account-overview-table">
							<div class="responsive-table">
								<table class="table">
									<tr class="responsive-table-head hidden-xs">
										<th><spring:theme code="text.company.column.id.name" text="ID" /></th>
										<th><spring:theme code="text.company.column.name.name" text="Name" /></th>
										<th><spring:theme code="text.company.column.parentUnit.name" text="Parent Unit" /></th>
										<th><spring:theme code="text.company.manage.units.costCenters" text="Cost Centers" /></th>
										<th><spring:theme code="text.company.budget.amount" text="Budget Amount" /></th>
										<th><spring:theme code="text.company.budget.start" text="Start" /></th>
										<th><spring:theme code="text.company.budget.end" text="End" /></th>
										<th><spring:theme code="text.company.column.status.name" text="Status" /></th>
									</tr>
									<c:forEach items="${searchPageData.results}" var="b2bBudget">
										<spring:url value="/my-company/organization-management/manage-budgets/view/" var="viewBudgetDetailsUrl" htmlEscape="false">
											<spring:param name="budgetCode" value="${b2bBudget.code}" />
										</spring:url>
										<spring:url value="/my-company/organization-management/manage-units/details/" var="unitDetailsUrl" htmlEscape="false">
											<spring:param name="unit" value="${b2bBudget.unit.name}" />
										</spring:url>
										<tr class="responsive-table-item">
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.id.name" text="ID" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_id_link">
													<a href="${viewBudgetDetailsUrl}" class="responsive-table-link">${fn:escapeXml(b2bBudget.code)}</a>
												</ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.name.name" text="Name" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_name_link">
                                        ${fn:escapeXml(b2bBudget.name)}
                                    </ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.parentUnit.name" text="Parent Unit" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_unit_label">
													<a href="${unitDetailsUrl}" class="responsive-table-link">${fn:escapeXml(b2bBudget.unit.name)}</a>
												</ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.manage.units.costCenters" text="Cost Centers" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_unit_label">
													<c:forEach items="${b2bBudget.costCenterNames}" var="costCenter">
                                            ${fn:escapeXml(costCenter)}<br />
													</c:forEach>
												</ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.budget.amount" text="Budget Amount" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_amount_label">
													<fmt:formatNumber value="${b2bBudget.budget}" maxFractionDigits="2" minFractionDigits="2" /> (${fn:escapeXml(b2bBudget.currency.isocode)})
                                    </ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.budget.start" text="Start" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_startDate_label">
													<fmt:formatDate value="${b2bBudget.startDate}" />
												</ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.budget.end" text="End" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_endDate_label">
													<fmt:formatDate value="${b2bBudget.endDate}" />
												</ycommerce:testId></td>
											<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.status.name" text="Status" /></td>
											<td class="responsive-table-cell"><ycommerce:testId code="budget_status_label">
													<c:choose>
														<c:when test="${b2bBudget.active}">
															<span>
																<spring:theme code="text.company.budget.status.active" text="Active" />
															</span>
														</c:when>
														<c:otherwise>
															<span class="account-status-inactive">
																<spring:theme code="text.company.budget.status.disabled" text="Disabled" />
															</span>
														</c:otherwise>
													</c:choose>
												</ycommerce:testId></td>
										</tr>
									</c:forEach>
								</table>
							</div>
						</div>
						<div class="row">
							<div class="account-orderhistory-pagination">
								<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" msgKey="text.company.budget.page" numberPagesShown="${numberPagesShown}" hideRefineButton="true" />
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</template:page>
