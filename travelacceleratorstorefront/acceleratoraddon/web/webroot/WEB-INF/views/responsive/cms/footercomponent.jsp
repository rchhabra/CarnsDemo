<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"%>
<div class="container">
	<nav>
		<ul class="flexbox row">
			<li class="col-xs-6">
				<small class="copyright">${fn:escapeXml(notice)}</small>
			</li>
			<c:forEach items="${navigationNodes}" var="node">
				<c:if test="${node.visible}">
					<c:forEach items="${node.links}" step="${wrapAfter}" varStatus="i">
						<c:forEach items="${node.links}" var="childlink" begin="${i.index}" end="${i.index + wrapAfter - 1}">
							<cms:component component="${childlink}" evaluateRestriction="true" element="li" class="col-xs-6" />
						</c:forEach>
					</c:forEach>
				</c:if>
			</c:forEach>
		</ul>
	</nav>
</div>
