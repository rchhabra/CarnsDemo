<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<json:object escapeXml="false">
	
	<json:property name="isCancelPossible" value="${isCancelPossible}" />
	
	<c:choose>
		<c:when test="${isCancelPossible}">
			<json:property name="cancelBookingModalHtml">
				<div class="modal fade" id="y_cancelBookingModal" tabindex="-1" role="dialog" aria-labelledby="cancelBookingLabel">
					<div class="modal-dialog" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h3 class="modal-title" id="cancelBookingLabel">
									<spring:theme code="text.page.managemybooking.cancel.${cancelOrderType}booking.modal.title" text="Cancel Booking" />
								</h3>
							</div>
							<div class="modal-body">
								<h3>
									<spring:theme code="text.page.managemybooking.cancel.${cancelOrderType}booking.modal.subtitle" />
								</h3>
								<p>
									<spring:theme code="text.page.managemybooking.cancel.${cancelOrderType}booking.refund" arguments="${totalToRefund}" argumentSeparator="#"/>
								</p>
							</div>
							<div class="modal-footer">
								<div class="row">
									<div class="col-xs-12 col-sm-6">
										<a id="y_cancelBookingUrl" class="btn btn-primary btn-block" href="">
											<spring:theme code="text.page.managemybooking.cancel.booking.confirmation" text="Confirm Cancellation" />
										</a>
									</div>
									<div class="col-xs-12 col-sm-6">
										<button class="btn btn-secondary btn-block" data-dismiss="modal" href="">
											<spring:theme code="text.page.managemybooking.cancel.booking.close" text="No Thanks" />
										</button>
									</div>
								</div>
							</div>
						</div>
					</div>
			</div>
			</json:property>
		</c:when>	
		<c:otherwise>
			<json:property name="errorMessage">
				<spring:theme code="text.page.managemybooking.cancel.${cancelOrderType}order.not.possible" />
			</json:property>
		</c:otherwise>
	</c:choose>
	
</json:object>
