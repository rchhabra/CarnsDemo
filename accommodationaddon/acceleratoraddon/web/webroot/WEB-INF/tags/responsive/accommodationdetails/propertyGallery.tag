<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData" %>

<div class="accommodation-image-gallery">
    <div class="owl-carousel main-image-carousel">
        <c:forEach var="image" items="${property.images}" varStatus='idx'>
            <c:set var="mainImageUrl" value="${image.url}"/>
            <c:if test="${image.format eq 'hotelGallery'}">
                <div class="item">
                    <img src="${mainImageUrl}" alt="hotel image ${idx.index+1}">
                </div>
            </c:if>
        </c:forEach>
    </div>
    <div class="thumbnail-carousel-wrapper">
        <div class="owl-carousel thumbnail-carousel">
            <c:forEach var="image" items="${property.images}" varStatus='idx'>
                <c:set var="mainImageUrl" value="${image.url}"/>
                <c:if test="${image.format eq 'thumbnail'}">
                    <div class="item">
                        <img src="${mainImageUrl}" alt="hotel thumbnail image ${idx.index+1}">
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>
</div>
