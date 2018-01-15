<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="modal fade" id="y_noUpgradeAvailableModal" tabindex="-1" role="dialog" aria-labelledby="noUpgradeAvailableModal" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="noUpgradeAvailableModal">
					<spring:theme code="text.ancillary.upgrade.bundle.unavailabe.title" text="" />
				</h4>
			</div>
			<div class="modal-body">
				<div>
					<spring:theme code="text.ancillary.upgrade.bundle.unavailabe.description" text="Unfortunately the offer you selected is no longer available. Please start the new booking journey." />
				</div>
			</div>
			<div class="modal-footer">
				<div class="row">
					<div class="col-xs-12 col-sm-6">
						<c:url var="homeUrl" value="/" />
						<a class="btn btn-secondary btn-block" href="${homeUrl}">
							<spring:theme code="text.ancillary.upgrade.bundle.unavailabe.button.back" text="Go back" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
