<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="company" tagdir="/WEB-INF/tags/addons/travelcommerceorgaddon/responsive/company"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="node" required="true" type="de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData"%>
<%@ attribute name="loopIndex" required="false" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/organization-management/manage-units/details" var="unitDetailUrl" htmlEscape="false">
	<spring:param name="unit" value="${node.id}" />
</spring:url>
<c:choose>
	<c:when test="${(fn:length(node.children) > 0)}">
		<div class="panel-heading" id="heading${loopIndex}">
			<button type="button" class="pull-right btn panel-header-link collapsable" data-toggle="collapse" data-parent="#accordion${loopIndex}" data-target="#collapse${loopIndex}" aria-expanded="true" aria-controls="collapse${loopIndex}"></button>
			<a href="${unitDetailUrl}" class="link">
				<h2 class="panel-title">${fn:escapeXml(node.name)}</h2>
				<c:if test="${!node.active}"> (<spring:theme code="text.company.manage.units.disabled.label" />)</c:if>
			</a>
		</div>
	</c:when>
	<c:otherwise>
		<div class="panel-heading" id="heading${loopIndex}">
			<button type="button" class="pull-right btn panel-header-link collapsable collapsed" data-toggle="collapse" data-parent="#accordion${loopIndex}" data-target="#collapse${loopIndex}" aria-expanded="true" aria-controls="collapse${loopIndex}"></button>
			<a href="${unitDetailUrl}" class="link">
				<h2 class="panel-title">${fn:escapeXml(node.name)}</h2>
				<c:if test="${!node.active}"> (<spring:theme code="text.company.manage.units.disabled.label" />)</c:if>
			</a>
		</div>
	</c:otherwise>
</c:choose>
<c:if test="${fn:length(node.children) > 0}">
	<div id="collapse${loopIndex}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading${loopIndex}">
		<div class="panel-body">
			<c:forEach var="node" items="${node.children}" varStatus="loop">
				<c:choose>
					<c:when test="${(fn:length(node.children) > 0)}">
						<div id="accordion${loopIndex}_${loop.count}" class="panel-group accordion">
							<div class="sub-panel">
								<company:unitTree node="${node}" loopIndex="${loopIndex}_${loop.count}" />
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div id="accordion${loopIndex}_${loop.count}" class="panel-group accordion no-subs">
							<div class="sub-panel">
								<company:unitTree node="${node}" loopIndex="${loopIndex}_${loop.count}" />
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
	</div>
</c:if>