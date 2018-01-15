<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty facetData.values}">
	<li role="presentation" class="dropdown  ${fn:length(facetData.values) gt 10 ? 'full-width-dropdown' : ''}">
		<a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
			<spring:theme code="package.listing.facet.${facetData.name}" />
			<span class="caret"></span>
		</a>
		<ul class="dropdown-menu">
			<c:forEach items="${facetData.values}" var="facetValue" varStatus="fcIdx">
				<li class="col-xs-12 ${fn:length(facetData.values) gt 10 ? 'col-sm-4' : ''}">
					<c:choose>
						<c:when test="${facetData.multiSelect}">
							<div class="checkbox">
								<label for="facet_${fn:escapeXml(facetData.code)}_${fcIdx.count}" class="col-xs-12">
									<input type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''} class="y_packageListingFacetCheckbox" id="facet_${fn:escapeXml(facetData.code)}_${fcIdx.count}" value="${fn:escapeXml(facetValue.query.query.value)}" />
									<spring:theme code="${facetValue.name}" text="${fn:escapeXml(facetValue.name)}" />
								</label>
							</div>
						</c:when>
						<c:otherwise>
							<label for="facet_${fn:escapeXml(facetData.code)}_${fcIdx.count}">
								<input name="facet_${fn:escapeXml(facetData.code)}_${fcIdx.count}" type="radio" ${facetValue.selected ? 'checked="checked"' : ''} class="y_packageListingFacetCheckbox" id="facet_${fn:escapeXml(facetData.code)}_${fcIdx.count}" value="${fn:escapeXml(facetValue.query.query.value)}">
								<spring:theme code="${facetValue.name}" text="${fn:escapeXml(facetValue.name)}" />
							</label>
						</c:otherwise>
					</c:choose>
				</li>
			</c:forEach>
		</ul>
	</li>
</c:if>
