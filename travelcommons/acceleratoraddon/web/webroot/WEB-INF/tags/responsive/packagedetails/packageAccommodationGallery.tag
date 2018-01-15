<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomType" required="true" type="de.hybris.platform.commercefacades.accommodation.RoomTypeData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>

<div class="modal fade" id="selected-accommodation-gallery-modal-${index}" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header clearfix">
                <div class="col-xs-10"><span>${fn:escapeXml(roomType.name)}</span></div>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="accommodation-image-gallery">
                    <div class="owl-carousel main-image-carousel">
                        <c:forEach items="${roomType.images}" var="accommodationImage">
                            <c:if test="${accommodationImage.format == 'accommodationDetail'}">
                                <div class="item">
                                    <img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                    <div class="thumbnail-carousel-wrapper">
                        <div class="owl-carousel thumbnail-carousel">
                            <c:forEach items="${roomType.images}" var="accommodationImage">
                                <c:if test="${accommodationImage.format == 'thumbnail'}">
                                    <div class="item">
                                        <img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
