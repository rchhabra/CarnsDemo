<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty facetData.values}">
	<c:choose>
		<c:when test="${fn:length(facetData.values) gt 10}">
			<li role="presentation" class="dropdown full-width-dropdown">
		</c:when>
		<c:otherwise>
			<li role="presentation" class="dropdown">
		</c:otherwise>
	</c:choose>
		<a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
			<spring:theme code="accommodation.search.facet.${facetData.name}" />
			<span class="caret"></span>
		</a>
		<ul class="dropdown-menu">
			<c:forEach items="${facetData.values}" var="facetValue" varStatus="fcIdx">
				<c:choose>
					<c:when test="${fn:length(facetData.values) gt 10}">
						<li class="col-xs-12 col-sm-4">
					</c:when>
					<c:otherwise>
						<li class="col-xs-12">
					</c:otherwise>
				</c:choose>
					<c:if test="${facetData.multiSelect}">
						<div class="checkbox">
							<label for="facet_${fn:escapeXml(facetData.code)}_${fn:escapeXml(fcIdx.count)}" class="col-xs-12">
								<input type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''} class="y_accommodationFacetCheckbox" id="facet_${fn:escapeXml(facetData.code)}_${fn:escapeXml(fcIdx.count)}" value="${fn:escapeXml(facetValue.query.query.value)}" />
								<spring:theme code="${facetValue.name}" text="${fn:escapeXml(facetValue.name)}" />
							</label>
						</div>
					</c:if>
					<c:if test="${not facetData.multiSelect}">
						<label for="facet_${fn:escapeXml(facetData.code)}_${fn:escapeXml(fcIdx.count)}">
							<input name="facet_${fn:escapeXml(facetData.code)}_${fn:escapeXml(fcIdx.count)}" type="radio" ${facetValue.selected ? 'checked="checked"' : ''} class="y_accommodationFacetCheckbox" id="facet_${fn:escapeXml(facetData.code)}_${fn:escapeXml(fcIdx.count)}" value="${fn:escapeXml(facetValue.query.query.value)}">
							<spring:theme code="${facetValue.name}" text="${fn:escapeXml(facetValue.name)}" />
						</label>
					</c:if>
				</li>
			</c:forEach>
		</ul>
	</li>
</c:if>
