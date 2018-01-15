<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<div id="carousel-promotional-banners" class="carousel slide" data-ride="carousel">
	<div class="carousel-inner" role="listbox">
		<c:forEach items="${banners}" var="banner" varStatus="status">
			<c:if test="${ycommerce:evaluateRestrictions(banner)}">
				<c:set var="backgroundStyleCss">
					background-image: url('<c:out value="${banner.media.url}" />');
				</c:set>
				<div class="item ${status.index == 0 ? 'active' : ''}" style="${fn:escapeXml(backgroundStyleCss)}">
					<div class="carousel-caption">
						<div class="container">
							<div class="row">
								<a href="${encodedUrl}" <c:if test="${banner.external}"> target="_blank"</c:if>>
									<div class="col-xs-10 col-xs-offset-1 col-sm-4 col-sm-offset-8 main-banner-message">
										${banner.content}
									</div>
								</a>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
