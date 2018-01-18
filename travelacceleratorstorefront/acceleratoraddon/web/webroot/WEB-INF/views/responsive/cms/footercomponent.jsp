<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"%>
<div class="container">
	<nav>
		<ul class="flexbox col-sm-6">
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
		 <ul class="col-sm-3 list-unstyled flexbox pull-right"> 
              <li><a href="#"><i class="fa fa-twitter" aria-hidden="true"></i></a></li> 
              <li><a href="#"><i class="fa fa-facebook" aria-hidden="true"></i></a></li> 
              <li><a href="#"><i class="fa fa-instagram" aria-hidden="true"></i></a></li> 
              <li><a href="#"><i class="fa fa-google-plus" aria-hidden="true"></i></a></li> 
              <li><a href="#"><i class="fa fa-tripadvisor" aria-hidden="true"></i></a></li> 
            </ul> 
	</nav>
</div>
