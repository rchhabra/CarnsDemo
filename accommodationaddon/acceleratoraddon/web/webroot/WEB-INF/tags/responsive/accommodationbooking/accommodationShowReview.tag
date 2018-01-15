<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<%@ attribute name="reviewData" required="true" type="de.hybris.platform.commercefacades.product.data.ReviewData"%>

<section class="review">
	<div class="row">
		<div class="panel-heading">
			<h3 class="panel-title title-collapse">
			    <spring:theme code="accommodation.booking.details.page.review" />
			</h3>
		</div>
		<div id="review${fn:escapeXml(reviewData.roomStayRefNumber)}" class="panel-body collapse in">
			<div class="fieldset">
				<h3>${fn:escapeXml(reviewData.headline)}</h3>
				<p>${fn:escapeXml(reviewData.comment)}</p>
                <div class="row fixed-average-score">
                    <div class="average-score col-xs-12 col-sm-8">
                        <span aria-hidden="true">0</span>
                        <div class="score-value-wrapper">
                            <div class="score-value" style="width: ${fn:escapeXml(reviewData.rating*10)}%;">
                                <span class="sr-only">${fn:escapeXml(reviewData.rating)} average score</span>
                            </div>
                        </div>
                        <span aria-hidden="true"><span>${fn:escapeXml(reviewData.rating)}</span>/10</span>
                    </div>
                </div>
			</div>
		</div>
	</div>
</section>
