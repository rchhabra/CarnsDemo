<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%-- Time Validation Modal --%>
<div class="modal fade" id="y_fareSelectionValidationModal" tabindex="-1" role="dialog" aria-labelledby="fareSelectionValidationModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="fareSelectionValidationModalLabel">
					<spring:theme code="fareselection.validation.incorrect" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:theme code="fareselection.validation.notenough" />
				</p>
			</div>
		</div>
	</div>
</div>
