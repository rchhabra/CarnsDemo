<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="refinement" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagelisting/refinement"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="displayFilters" value="false" />
<c:if test="${not empty fn:escapeXml(filterPropertyName) or not empty fn:escapeXml(priceRange) or totalNumberOfResults gt 0}">
	<c:set var="displayFilters" value="true" />
</c:if>
<c:forEach items="${packageSearchResponse.criterion.facets}" var="facet">
	<c:if test="${not empty facet.values && not displayFilters}">
		<c:forEach items="${facet.values}" var="facetValue">
			<c:if test="${facetValue.selected}">
				<c:set var="displayFilters" value="true" />
			</c:if>
		</c:forEach>
	</c:if>
</c:forEach>
<c:if test="${displayFilters}">
	<c:if test="${not empty accommodationSearchParamsError}">
		<div class="col-xs-12">
			<div class="row">
				<div class="alert alert-danger alert-dismissable">
					<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>
					<spring:theme code="${accommodationSearchParamsError}" />
				</div>
			</div>
		</div>
	</c:if>
	<div class="col-xs-6 col-sm-12 sidebar-offcanvas" id="filter">
		<c:url value="/package-listing" var="packageListingUrl" />
		<div>
			<form action="${packageListingUrl}" method="GET" id="y_packageListingFacetForm">
				<c:forEach items="${accommodationSearchParams}" var="paramDetail">
					<input type="hidden" name="${fn:escapeXml(paramDetail.key)}" value="${fn:escapeXml(paramDetail.value)}" />
				</c:forEach>
				<c:forEach items="${packageSearchResponse.criterion.sorts}" var="sort">
					<c:if test="${sort.selected }">
						<input type="hidden" name="sort" value="${fn:escapeXml(sort.code)}" />
					</c:if>
				</c:forEach>
				<input type="hidden" name="q" value="${fn:escapeXml(packageSearchResponse.criterion.query)}" />
				<input type="hidden" name="priceRange" value="${fn:escapeXml(priceRange)}" />
				<input id="y_resultsViewTypeForFacetForm" type="hidden" name="resultsViewType" value="${fn:escapeXml(resultsViewType)}" />
			</form>
		</div>
		<div class="row">
			<div class="sidebar-nav clearfix">
				<button type="button" class="btn btn-primary visible-xs col-xs-offset-1 col-xs-10" data-toggle="offcanvas">
					<spring:theme code="text.cms.accommodationrefinement.close" text="Close" />
				</button>
				<div class="filter-form clearfix col-xs-12">
					<div class="filter-facets">
						<h3 class="hidden-xs">
							<spring:theme code="text.cms.accommodationrefinement.filterby" text="Filter by:" />
						</h3>
						<c:if test="${totalNumberOfResults gt 0 or not empty fn:escapeXml(priceRange)}">
							<ul class="nav nav-pills col-xs-12 col-sm-3">
								<refinement:packagePriceRangeFilter />
							</ul>
						</c:if>
						<ul class="nav nav-pills col-xs-12 col-sm-9">
							<c:if test="${totalNumberOfResults gt 0 or not empty fn:escapeXml(filterPropertyName)}">
								<refinement:propertyNameFilter filterPropertyName="${fn:escapeXml(filterPropertyName)}" />
							</c:if>
							<c:forEach items="${packageSearchResponse.criterion.facets}" var="facet">
								<refinement:facetFilter facetData="${facet}" />
							</c:forEach>
						</ul>
					</div>
					<div class="facets-search-container clearfix y_packageListingSelectedFiltersContainer">
						<refinement:selectedFacetFilter />
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>
