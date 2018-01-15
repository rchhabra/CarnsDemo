<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div id="y_accommodationListingShowMore" class="hidden"></div>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${hasMoreResults}">
    <div class="row">
        <div class="col-xs-12">
            <c:if test="${totalNumberOfResults gt 0}">
                <div class="col-md-8 col-xs-12">
                    <div class="row">
                        <h3 class="h4 y_shownResultId">
                            <spring:theme code="text.accommodation.listing.shown.hotels.results" arguments="${totalshownResults}, ${totalNumberOfResults}" />
                        </h3>
                    </div>
                </div>
            </c:if>
            <div class="col-md-4 col-xs-12 pull-right">
                <div class="row bottom-row">
                    <button class="btn btn-primary col-xs-12 col-sm-12 pull-right y_accommodationListingShowMore" data-pagenumber="${fn:escapeXml(pageNum)}">
                        <spring:theme code="text.accommodation.listing.button.show.more" />
                    </button>
                </div>
            </div>
        </div>
    </div>
</c:if>
