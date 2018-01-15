<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<input type="hidden" id="y_noPackageAvailability" value="show" />
<div class="modal fade" id="y_noPackageAvailabilityModal" tabindex="-1" role="dialog" aria-labelledby="noPackageAvailabilityModal">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="noPackageAvailabilityModal">
                    <spring:theme code="text.package.details.no.availability.modal.title" text="No Package Available"/>
                </h4>
            </div>
            <div class="modal-body">
                <div><spring:theme code="text.package.details.no.availability.modal.message"
                                   text="There is no availability for your request please go back to modify your selection"/></div>
            </div>
            <div class="modal-footer">
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <a class="btn btn-secondary btn-block" href="javascript:history.back()">
                            <spring:theme code="text.package.details.no.availability.modal.button.back" text="Go back"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
