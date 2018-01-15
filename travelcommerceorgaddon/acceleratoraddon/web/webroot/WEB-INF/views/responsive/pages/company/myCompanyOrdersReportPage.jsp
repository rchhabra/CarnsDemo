<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:url value="/manage-booking/booking-details" var="viewBookingUrl" />
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="searchUrl" value="/my-company/organization-management/report-orders/${unitId}?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}&fromDate=${param.fromDate}&toDate=${param.toDate}&email=${param.email}&currency=${param.currency}&costCenter=${param.costcenter}" />
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
					<div class="col-xs-12">
						<h2 class="h2">
							<spring:theme code="text.company.report.orders.title" text="Orders Report" />
						</h2>
					</div>
				</div>
				<div class="account-section-content">
					<div class="account-orderhistory-pagination">
						<form class="row order-search" action="#">
                            <div class="col-xs-12">
                                <h3 class="hidden-xs h4"><spring:theme code="text.company.orders.filterby"
                                                                       text="Filter by:"/></h3>
                            </div>
                            <div class="input-required-wrap col-xs-12 col-sm-2 form-group">
                                <label class="sr-only" for="from-date">
                                    <spring:theme code="text.company.orders.fromdate" text="From date"/></label>
                                <input type="text" name="fromDate"
                                       class="col-xs-12 datepicker input-grid form-control y_ordersFromDate" id="from-date"
                                       placeholder="From Date" value="${fn:escapeXml(param.fromDate)}">
                            </div>
                            <div class="input-required-wrap col-xs-12 col-sm-2 form-group">
                                <label class="sr-only" for="to-date">
                                    <spring:theme code="text.company.orders.todate" text="To date"/></label>
                                <input type="text" name="toDate"
                                       class="col-xs-12 datepicker input-grid form-control y_ordersToDate" id="to-date"
                                       placeholder="To Date" value="${fn:escapeXml(param.toDate)}">
                            </div>
                            <div class="input-required-wrap col-xs-12 col-sm-2 form-group">
                                <label class="sr-only" for="e-mail">
                                    <spring:theme code="text.company.orders.email" text="E Mail"/></label>
                                <input type="email" name="email" class="form-control" id="e-mail" placeholder="eMail"
                                       value="${fn:escapeXml(param.email)}">
                            </div>
                            <div>
                                <div class="input-required-wrap col-xs-12 col-sm-2 form-group">
                                    <label class="sr-only" for="currency"><spring:theme code="text.company.orders.currency"
                                                                                        text="Currency"/></label>
                                    <select name="currency" id="currency" class="form-control">
                                        <option value=""><spring:theme code="text.company.orders.currency"
                                                                       text="Currency"/></option>
                                        <c:forEach var="currency" items="${currencies}">
                                            <c:choose>
                                                <c:when test="${param.currency eq currency.isocode}">
                                                    <c:set var="selected" value="selected"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="selected" value=""/>
                                                </c:otherwise>
                                            </c:choose>
                                            <option value="${fn:escapeXml(currency.isocode)}" ${selected}>${fn:escapeXml(currency.symbol)}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div>
                                <div class="input-required-wrap col-xs-12 col-sm-2 form-group">
                                    <label class="sr-only" for="costCenters"><spring:theme code="text.company.orders.costcenter"
                                                                                           text="Cost Center"/></label>
                                    <select id="costCenters" class="form-control" name="costCenter">
                                        <option value=""><spring:theme code="text.company.orders.costcenter"
                                                                       text="Cost Center"/></option>
                                        <c:forEach var="costCenter" items="${costCenters}">
                                            <c:choose>
                                                <c:when test="${param.costCenter eq costCenter.code}">
                                                    <c:set var="selected" value="selected"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="selected" value=""/>
                                                </c:otherwise>
                                            </c:choose>
                                            <option value="${fn:escapeXml(costCenter.code)}" ${selected}>${fn:escapeXml(costCenter.name)}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="input-required-wrap col-xs-12">
                                <div class="row">
                                    <div class="col-xs-12 col-sm-3 form-group">
                                        <button type="submit" class="btn btn-default btn-block"><spring:theme
                                                code="text.company.orders.search" text="Search"/></button>
                                    </div>
                                    <div class="col-xs-12 col-sm-3 form-group">
                                        <spring:url value="/my-company/organization-management/report-orders/${unitId}"
                                                    var="resetUrl"/>
                                        <a class="btn btn-default btn-block " href="${resetUrl}"><spring:theme
                                                code="text.company.orders.reset" text="Reset"/></a>
                                    </div>
                                </div>
                            </div>
                        </form>
						<div class="account-orderhistory-pagination">
							<div class="row">
								<div class="sortBy-pagination">
										<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" msgKey="text.company.costCenter.page" hideRefineButton="true" numberPagesShown="${numberPagesShown}" />
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
									<th><spring:theme code="text.company.column.code" /></th>
									<th><spring:theme code="text.company.column.owner" /></th>
									<th><spring:theme code="text.company.column.unit" /></th>
									<th><spring:theme code="text.company.column.costCenter" /></th>
									<th><spring:theme code="text.company.column.currency" /></th>
									<th><spring:theme code="text.company.column.creationTime" /></th>
									<th><spring:theme code="text.company.column.totalAmount" /></th>
								</tr>
								<c:forEach items="${searchPageData.results}" var="reservation">
									<tr class="responsive-table-item">
										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.code" /></td>
										<td class="responsive-table-cell">
											<a href="${viewBookingUrl}/${reservation.code}">
												${fn:escapeXml(reservation.code)}
											</a>
										</td>
										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.owner" /></td>
										<td class="responsive-table-cell">${fn:escapeXml(reservation.owner)}</td>

										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.unit" /></td>
										<td class="responsive-table-cell">${fn:escapeXml(reservation.unit)}</td>

										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.costCenter" /></td>
										<td class="responsive-table-cell">${fn:escapeXml(reservation.costCenter)}</td>

										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.currency" /></td>
										<td class="responsive-table-cell">${fn:escapeXml(reservation.currencyCode)}</td>

										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.creationTime" /></td>
										<td class="responsive-table-cell"><fmt:formatDate value="${reservation.creationTime}" pattern="dd/MM/yyyy HH:mm" /></td>
										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.totalAmount" /></td>
										<td class="responsive-table-cell"><format:price priceData="${reservation.total}" /></td>
									</tr>
								</c:forEach>
								<c:if test="${totalForQuery ne null}">
									<tr class="responsive-table-item">
										<td class="responsive-table-cell hidden-xs">&nbsp;</td>
										<td class="responsive-table-cell hidden-xs">&nbsp;</td>
										<td class="responsive-table-cell hidden-xs">&nbsp;</td>
										<td class="responsive-table-cell hidden-xs">&nbsp;</td>
										<td class="responsive-table-cell hidden-xs">&nbsp;</td>
										<td class="responsive-table-cell hidden-sm hidden-md hidden-lg"><spring:theme code="text.company.column.total" /></td>
										<td class="responsive-table-cell hidden-xs"><spring:theme code="text.company.column.total" /></td>
										<td class="hidden-sm hidden-md hidden-lg hidden-xs"><format:price priceData="${totalForQuery}" /></td>
										<td class="responsive-table-cell"><format:price priceData="${totalForQuery}" /></td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row">
					<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" msgKey="text.company.costCenter.page" hideRefineButton="true" numberPagesShown="${numberPagesShown}" />
					<c:if test="${empty searchPageData.results}">
						<p><spring:theme code="text.company.noentries" /></p>
					</c:if>
				</div>
			</div>
		</div>
	</div>
</template:page>
