<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="orderApprovalData" required="true" type="de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData"%>
<%@ attribute name="orderApprovalDecisionForm" required="true" type="de.hybris.platform.travelacceleratorstorefront.forms.OrderApprovalDecisionForm"%>
<%@ attribute name="orderApprovalDecisionURL" required="true" type="java.lang.String"%>
<%@ attribute name="decision" required="true" type="java.lang.String"%>
<%@ attribute name="actionButtonLabel" required="true" type="java.lang.String"%>
<%@ attribute name="commentLabel" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="commentMaxChars" value="255" />
<c:choose>
	<c:when test="${orderApprovalData.approvalDecisionRequired}">
		<div class="col-xs-12 col-sm-4 pull-right">
			<a href="#" class="btn btn-primary btn-block" data-toggle="modal" data-target="#action-confirmation-modal-${fn:escapeXml(renderViewFor)}-${fn:escapeXml(decision)}">
				<spring:theme code="${actionButtonLabel}" text="${fn:escapeXml(actionButtonLabel)}" />
			</a>
		</div>
		<div class="modal fade" tabindex="-1" role="dialog" id="action-confirmation-modal-${fn:escapeXml(renderViewFor)}-${fn:escapeXml(decision)}">
			<div class="modal-dialog">
				<div class="modal-content" id="action-confirmation-modal-content-${fn:escapeXml(renderViewFor)}-${fn:escapeXml(decision)}">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title">
							<spring:theme code="${commentLabel}" text="${fn:escapeXml(commentLabel)}" />
						</h4>
					</div>
					<div class="modal-body">
						<form:form method="post" commandName="orderApprovalDecisionForm" action="${orderApprovalDecisionURL}">
							<form:input type="hidden" name="workflowActionCode" path="workFlowActionCode" value="${fn:escapeXml(orderApprovalData.workflowActionModelCode)}" />
							<form:input type="hidden" name="approverSelectedDecision" path="approverSelectedDecision" value="${fn:escapeXml(decision)}" />
							<form:textarea path="comments" cssClass="form-control" maxlength="${fn:escapeXml(commentMaxChars)}"></form:textarea>
							<div class="help-block">
								<spring:theme code="responsive.checkout.summary.orderApproval.comment.maxchars" arguments="${commentMaxChars}" />
							</div>
							<div class="row">
								<div class="col-xs-12 col-md-6 button-block full-width">
									<div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-6">
										<button type="submit" class="btn btn-primary btn-block">
											<spring:theme code="${actionButtonLabel}" text="${fn:escapeXml(actionButtonLabel)}" />
										</button>
									</div>
									<div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-6 ">
										<a data-dismiss="modal">
											<button class="btn btn-secondary btn-block remove-btn">
												<spring:theme code="responsive.checkout.summary.orderApproval.cancel" />
											</button>
										</a>
									</div>
								</div>
							</div>
						</form:form>
					</div>
				</div>
			</div>
		</div>
	</c:when>
</c:choose>
