<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="cancel" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/cancel"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<json:object escapeXml="false">
	<json:property name="customerReviewsModalHtml">
		<div class="reviewsModal modal fade" id="y_reviewsModal" tabindex="-1" role="dialog" aria-labelledby="userReviews">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h3 class="modal-title" id="userReviews">
							<spring:theme code="text.accommodation.customer.review.modal.header" />
						</h3>
					</div>

					<div class="modal-body">
						<accommodationDetails:customerReviewProgressBar accommodationCustomerReviews="${customerReviewSearchPageData.results}" property="${property}" totalNumberOfReviews="${customerReviewSearchPageData.pagination.totalNumberOfResults}" />
						<div class="reviews-container">
							<div class="col-xs-12">
								<div class="row">
									<div class="xcol-xs-12">
										<accommodationDetails:propertyReview accommodationCustomerReviews="${customerReviewSearchPageData.results}" property="${property}" totalNumberOfReviews="${customerReviewSearchPageData.pagination.totalNumberOfResults}" />
									</div>
								</div>
							</div>
						</div>
						<span class="scroll-fade"></span>
					</div>
				</div>
			</div>
		</div>
	</json:property>
</json:object>
