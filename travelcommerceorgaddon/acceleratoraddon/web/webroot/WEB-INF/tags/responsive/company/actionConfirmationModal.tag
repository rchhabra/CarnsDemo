<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="targetUrl" required="true" type="java.lang.String"%>
<%@ attribute name="messageKey" required="true" type="java.lang.String"%>
<%@ attribute name="messageArguments" required="false" type="java.lang.String"%>
<%@ attribute name="buttonsVisible" required="false" type="java.lang.Boolean"%>
<%@ attribute name="actionButtonLabelKey" required="false" type="java.lang.String"%>
<%@ attribute name="actionButtonAsLink" required="false" type="java.lang.Boolean"%>
<%@ attribute name="useSourceElementUrl" required="false" type="java.lang.Boolean"%>
<%@ attribute name="modalTitle" required="false" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="displayButtons" value="${(empty buttonsVisible) ? true : buttonsVisible}" />
<c:set var="mainButtonLabelKey" value="${(empty actionButtonLabelKey) ? 'text.company.disable.button' : actionButtonLabelKey}" />
<c:set var="dataUseSourceElementUrl" value="${(empty useSourceElementUrl) ? false : useSourceElementUrl}" />
<c:set var="useLinkForMainAction" value="${(empty actionButtonAsLink) ? false : actionButtonAsLink}" />
<c:set var="modalTitle" value="${(empty modalTitle) ? mainButtonLabelKey : modalTitle}" />
<div class="modal fade" tabindex="-1" role="dialog" id="action-confirmation-modal-${id}">
	<div class="modal-dialog">
		<div class="modal-content" id="action-confirmation-modal-content-${id}" data-use-source-element-url="${dataUseSourceElementUrl}">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<spring:theme code="${modalTitle}" text="${modalTitle}" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:theme code="${messageKey}" arguments="${messageArguments}" />
				</p>
			</div>
			<c:if test="${displayButtons}">
				<div class="modal-footer">
					<div class="row">
						<div class="col-xs-12 col-md-6 button-block full-width">
							<div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-6">
								<c:choose>
									<c:when test="${useLinkForMainAction}">
										<a href="#" url="${(empty targetUrl) ? '#' : targetUrl}">
											<button class="btn btn-primary btn-block">
												<spring:theme code="${mainButtonLabelKey}" />
											</button>
										</a>
									</c:when>
									<c:otherwise>
										<form:form action="${targetUrl}" class="url-holder">
											<button type="submit" class="btn btn-primary btn-block">
												<spring:theme code="${mainButtonLabelKey}" />
											</button>
										</form:form>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-6 ">
								<a data-dismiss="modal">
									<button class="btn btn-secondary btn-block remove-btn">
										<spring:theme code="text.company.cancel.button" />
									</button>
								</a>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>