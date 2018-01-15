<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty packageSearchResponse.criterion.filteredFacets || not empty filterPropertyName}">
	<c:forEach items="${packageSearchResponse.criterion.filteredFacets}" var="facetData">
		<c:if test="${not empty facetData.values}">
			<ul class="col-xs-12 col-sm-3 col-lg-2">
				<li class="btn-close-facet">
					<a href="#" class="y_packageClearFacetFilter" data-facetcode="${fn:escapeXml(facetData.code)}" data-query="${fn:escapeXml(facetData.clearFacetQuery.value)}">
						<span class="glyphicon glyphicon-remove"></span>
						<span class="sr-only"><spring:theme code="package.listing.selected.facet.remove" text="Remove" /></span>
					</a>
				</li>
				<li class="search-category">
					<spring:theme code="package.listing.facet.${facetData.code}" />
					:
				</li>
				<c:forEach items="${facetData.values}" var="facetValue" varStatus="fcIdx">
					<li class="row facet-item">
						<span class="col-xs-10">
							<spring:theme code="${facetValue.name}" text="${fn:escapeXml(facetValue.name)}" />
						</span>
						<a href="#" class="y_packageClearFacetFilter pull-right" data-facetcode="${fn:escapeXml(facetData.code)}" data-query="${fn:escapeXml(facetValue.query.query.value)}">
							<span class="glyphicon glyphicon-remove"></span>
							<span class="sr-only"><spring:theme code="package.listing.selected.facet.remove" text="Remove" /></span>
						</a>
					</li>
				</c:forEach>
			</ul>
		</c:if>
	</c:forEach>
	<c:if test="${not empty filterPropertyName}">
		<ul class="col-xs-12 col-sm-3 col-lg-2">
			<li class="btn-close-facet">
				<a href="#" class="y_packageClearFacetFilter" data-facetcode="propertyName" data-query="">
					<span class="glyphicon glyphicon-remove"></span>
					<span class="sr-only"><spring:theme code="package.listing.selected.facet.remove" text="Remove" /></span>
				</a>
			</li>
			<li class="search-category">
				<spring:theme code="text.cms.accommodationrefinement.propertyname.placeholder" text="Property Name" />
				:
			</li>
			<li class="row facet-item">
				<span class="col-xs-10">
					<spring:theme code="${filterPropertyName}" text="${fn:escapeXml(filterPropertyName)}" />
				</span>
				<a href="#" class="y_packageClearFacetFilter pull-right" data-facetcode="propertyName" data-query="">
					<span class="glyphicon glyphicon-remove"></span>
					<span class="sr-only"><spring:theme code="package.listing.selected.facet.remove" text="Remove" /></span>
				</a>
			</li>
		</ul>
	</c:if>
	<div class="col-xs-12">
		<p>
			<a href="#" class="y_packageListing_clearAllFacetFilter">
				<spring:theme code="package.listing.facet.clearAll" text="Clear All" />
			</a>
		</p>
	</div>
</c:if>
