<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<li class="col-xs-12">
	<div class="price-range row">
		<div class="col-xs-12 col-sm-6">
			<div class="row">
				<p>
					<label for="price" class="sr-only"><spring:theme code="text.cms.packagerefinement.pricerange.title" text="Package Price Range" /></label>
					<input type="text" class="y_priceRangeValue" data-minpricerange="${fn:escapeXml(minPackagePrice)}" data-maxpricerange="${fn:escapeXml(maxPackagePrice)}">
				</p>
			</div>
		</div>
		<div class="col-xs-12 col-sm-6">
			<div class="row">
				<div id="price-slide"></div>
			</div>
		</div>
	</div>
</li>
