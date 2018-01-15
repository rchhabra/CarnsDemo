<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" %>
<%@ attribute name="searchPageData" required="true"
              type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="top" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showTopTotals" required="false" type="java.lang.Boolean" %>
<%@ attribute name="supportShowAll" required="true" type="java.lang.Boolean" %>
<%@ attribute name="supportShowPaged" required="true" type="java.lang.Boolean" %>
<%@ attribute name="additionalParams" required="false" type="java.util.HashMap" %>
<%@ attribute name="msgKey" required="false" %>
<%@ attribute name="showCurrentPageInfo" required="false" type="java.lang.Boolean" %>
<%@ attribute name="hideRefineButton" required="false" type="java.lang.Boolean" %>
<%@ attribute name="numberPagesShown" required="false" type="java.lang.Integer" %>
<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/nav/pagination" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}"/>
<c:set var="showCurrPage" value="${not empty showCurrentPageInfo ? showCurrentPageInfo : false}"/>
<c:set var="hideRefBtn" value="${hideRefineButton ? true : false}"/>
<c:set var="showTotals" value="${empty showTopTotals ? true : showTopTotals}"/>

<c:if test="${searchPageData.pagination.totalNumberOfResults == 0 && top && showTotals}">
    <div class="row">
        <div class="col-xs-12 col-sm-4">
            <ycommerce:testId code="searchResults_productsFound_label">
                <div class="pagination-bar-results"><spring:theme code="${themeMsgKey}.totalResults"
                                                                  arguments="${searchPageData.pagination.totalNumberOfResults}"/></div>
            </ycommerce:testId>
        </div>
    </div>
</c:if>
<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">
    <c:if test="${not empty searchPageData.sorts}">
        <div class="sortBy-pagination clearfix">
            <div class="col-xs-12 col-sm-4 sortBy">
                <div class="row">
                    <label for="sortForm${top ? '1' : '2'}">
                        <spring:theme code="${themeMsgKey}.sortTitle"/>
                    </label>
                    <form id="sortForm${top ? '1' : '2'}" name="sortForm${top ? '1' : '2'}" method="get"
                          action="#">
                        <select id="sortOptions${top ? '1' : '2'}" name="sort" class="form-control">
                            <option disabled><spring:theme code="${themeMsgKey}.sortTitle"/></option>
                            <c:forEach items="${searchPageData.sorts}" var="sort">
                                <option value="${sort.code}" ${sort.selected? 'selected="selected"' : ''}>
                                    <c:choose>
                                        <c:when test="${not empty sort.name}">
                                            ${sort.name}
                                        </c:when>
                                        <c:otherwise>
                                            <spring:theme code="${themeMsgKey}.sort.${sort.code}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                        <c:catch var="errorException">
                            <spring:eval expression="searchPageData.currentQuery.query"
                                         var="dummyVar"/><%-- This will throw an exception is it is not supported --%>
                            <input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}"/>
                        </c:catch>

                        <c:if test="${supportShowAll}">
                            <ycommerce:testId code="searchResults_showAll_link">
                                <input type="hidden" name="show" value="Page"/>
                            </ycommerce:testId>
                        </c:if>
                        <c:if test="${supportShowPaged}">
                            <ycommerce:testId code="searchResults_showPage_link">
                                <input type="hidden" name="show" value="All"/>
                            </ycommerce:testId>
                        </c:if>
                        <c:if test="${not empty additionalParams}">
                            <c:forEach items="${additionalParams}" var="entry">
                                <input type="hidden" name="${entry.key}" value="${entry.value}"/>
                            </c:forEach>
                        </c:if>
                    </form>
                </div>
            </div>
            <div class="col-xs-offset-8 col-xs-4">
                <pagination:pageSelectionPagination searchUrl="${searchUrl}" searchPageData="${searchPageData}"
                                                    numberPagesShown="${numberPagesShown}"
                                                    themeMsgKey="${themeMsgKey}"/>
            </div>
        </div>
    </c:if>
</c:if>
<c:if test="${top && showTotals}">
    <div class="col-xs-12 col-sm-4">
        <div class="pagination-bar-results">
            <ycommerce:testId code="searchResults_productsFound_label">
                <c:choose>
                    <c:when test="${showCurrPage}">
                        <c:choose>
                            <c:when test="${searchPageData.pagination.totalNumberOfResults == 1}">
                                <spring:theme code="${themeMsgKey}.totalResultsSingleOrder"/>
                            </c:when>
                            <c:when test="${searchPageData.pagination.numberOfPages <= 1}">
                                <spring:theme code="${themeMsgKey}.totalResultsSinglePag"
                                              arguments="${searchPageData.pagination.totalNumberOfResults}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="currentPageItems"
                                       value="${(searchPageData.pagination.currentPage + 1) * searchPageData.pagination.pageSize}"/>
                                <c:set var="upTo"
                                       value="${(currentPageItems > searchPageData.pagination.totalNumberOfResults ? searchPageData.pagination.totalNumberOfResults : currentPageItems)}"/>
                                <c:set var="currentPage"
                                       value="${searchPageData.pagination.currentPage * searchPageData.pagination.pageSize + 1} - ${upTo}"/>
                                <spring:theme code="${themeMsgKey}.totalResultsCurrPage"
                                              arguments="${currentPage},${searchPageData.pagination.totalNumberOfResults}"/>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="${themeMsgKey}.totalResults"
                                      arguments="${searchPageData.pagination.totalNumberOfResults}"/>
                    </c:otherwise>
                </c:choose>
            </ycommerce:testId>
        </div>
    </div>
</c:if>