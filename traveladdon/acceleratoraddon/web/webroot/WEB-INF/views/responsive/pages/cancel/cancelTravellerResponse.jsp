<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="cancel" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/cancel"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<json:object escapeXml="false">
	<json:property name="isCancelPossible" value="${isCancelPossible}" />

	<c:choose>
		<c:when test="${isCancelPossible}">
			<json:property name="cancelTravellerModalHtml">
				<div class="modal fade" id="y_cancelTravellerModal" tabindex="-1" role="dialog" aria-labelledby="cancelTravellerModalLabel">
					<div class="modal-dialog" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<button id="y_modalCancelBtn" type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="cancelTravellerModalLabel">
									<spring:theme code="text.page.managemybooking.cancel.traveller.modal.title" text="Cancel Passenger" />
								</h4>
							</div>
							<div class="modal-body">
								<h3>
									<spring:theme code="text.page.managemybooking.cancel.traveller.modal.subtitle" />
								</h3>
								<p>
									<span id="y_cancelledTravellerDetails">${cancelledTraveller.travellerInfo.firstName}&nbsp;${cancelledTraveller.travellerInfo.surname}</span>
									<spring:theme code="text.page.managemybooking.cancel.traveller.message" />
								</p>
								<p>
									<span id="y_cancelledTravellerRefund">
										<format:price priceData="${totalToPay}" />
									</span>
									<spring:theme code="text.page.managemybooking.cancel.traveller.message.refund" />
								</p>
							</div>
							<div class="modal-footer">
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<a id="y_cancelTravellerUrl" class="btn btn-primary btn-block" href="">
											<spring:theme code="text.page.managemybooking.cancel.booking.confirmation" text="Confirm Cancellation" />
										</a>
									</div>
									<div class="col-xs-12 col-sm-6">
										<button class="btn btn-secondary btn-block" data-dismiss="modal" href="">
											<spring:theme code="text.page.managemybooking.cancel.booking.close" text="No  Thanks" />
										</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</json:property>
			<json:property name="cancelTravellerURL" value="${cancelTravellerURL}?orderCode=${orderCode}&travellerUid=${cancelledTraveller.uid}" />
		</c:when>
		<c:otherwise>
			<json:property name="errorMessage">
				<spring:theme code="text.page.managemybooking.cancel.traveller.not.possible" />
			</json:property>
		</c:otherwise>
	</c:choose>
</json:object>
