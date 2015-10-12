<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
	<div class="container-fluid" id="main">
		<div class="row">
			<div id="results" class="col-xs-6" id="left">
				<br>
				

					<div class="">
								
				      <ul class="pagination pagination-sm " style="margin: 0px 0;">
				        <li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">«</span></a></li>
				        <li class="active"><a href="#">1 <span class="sr-only">(current)</span></a></li>
				        <li><a href="#">2</a></li>
				        <li><a href="#">3</a></li>
				        <li><a href="#">4</a></li>
				        <li><a href="#">5</a></li>
				        <li><a href="#" aria-label="Next"><span aria-hidden="true">»</span></a></li>
				     </ul>
				
					  <p id="newplace" class="newplace addbutton2" >+</p>
				</div>
		

				<!-- item list -->
				<c:forEach var="item" items="${ items }" varStatus="status">
				<div id="item${status.index}" class="item" >
				
					<div class="panel panel-default">
						<div class="panel-heading">
						
							<a href="/place?id=${item.id}" target="_blank">${item.title}</a>
							
							
						
							<div style="float: right;  margin: auto;">
												
							
								<span data-id="${item.id}" class="comment_item glyphicon glyphicon-pencil edit" aria-hidden="true" title="<spring:message code="home.result.item.edit"/>">
									<spring:message code="home.result.item.edit"/>
								</span> 
								
								<!-- 							
								<img  class="/img-circle button_item" alt="facebook" src="/img/like.jpg">
								<span class="comment_item"><spring:message code="home.result.item.like"/> (23)</span>
								<img  class="/img-circle button_item"  alt="facebook" src="/img/comment.png">
								<span class="comment_item"><spring:message code="home.result.item.comment"/> (123)</span>
								<img  class="/img-circle button_item" alt="facebook" src="/img/facebook.jpg">
								<span class="comment_item"><spring:message code="home.result.item.share"/> (3)</span>
								 -->
							</div>
							
						</div>
					</div>	
					<div class="item_content" id="${status.index}" data-lat="${item.latitude}" data-address="${item.address}"  
					data-id="${item.id}" data-type="${item.placeType}" data-icon="${item.iconPath}"
						data-lng="${item.longitude}" data-title="${item.title}"	data-img="${item.imagePath}">
						<article>
							<img class="photo_item" src="${item.imagePath}" alt="Photo">
							${item.information}
						</article>  
	
				        <table>
				            <tr>
				                <td><span><spring:message code="home.result.item.address"/>: </span></td>
				                <td><span>${item.address}</span>  <%--<img alt="" src="/img/flags/${item.country}.png">--%></td> 
				            </tr>
				            <c:if test="${ not empty item.openTime }">	
					            <tr>
					                <td><span><spring:message code="home.result.item.time"/>: </span></td>
					                <td><span>${item.openTime}</span></td>
					            </tr>
					        </c:if>
				            <c:if test="${ not empty item.telephone }">	
					            <tr>
					                <td><span><spring:message code="home.result.item.telephone"/>: </span></td>
					                <td><span>${item.telephone}</span></td>
					            </tr>	
					        </c:if>
				            <c:if test="${ not empty item.email }">		
					            <tr>
					                <td><span><spring:message code="home.result.item.email"/>: </span></td>
					                <td><span>${item.email}</span></td>
					            </tr>	
					        </c:if>
				            <c:if test="${ not empty item.referenceUrl }">		
					            <tr>
					                <td><span><spring:message code="home.result.item.homepage"/>: </span></td>
					                <td><span><a href="${item.referenceUrl}">${item.referenceUrl}</a></span></td>
					            </tr>	
					        </c:if>			            	            			            
				        </table>
					</div>
					<img  class="/img-circle button_item" alt="facebook" src="/img/user.png">
					<span class="comment_item"><spring:message code="home.result.item.postedby"/> ${item.createdFromIp} - ${item.updatedDate}</span>
					<!-- 
					<img  class="/img-circle button_item" alt="facebook" src="/img/thank.jpg">
					<span class="comment_item"><spring:message code="home.result.item.thank"/> (23)</span>
					 
					<img  class="/img-circle button_item" alt="facebook" src="/img/edit.png">
					<span class="comment_item edit"><spring:message code="home.result.item.edit"/></span>
					-->
					<hr>
				</div>				
				</c:forEach>
				<!-- end item list -->
				<%--
				<p id="scroll">
					<a href="/search?keywords=Việt Nam" target="_ext"
						class="center-block btn btn-primary"><spring:message code="home.result.more" /></a>
				</p>
				 --%>
				<nav class="text-center">
				      <ul class="pagination pagination-lg">
				        <li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">«</span></a></li>
				        <li class="active"><a href="#">1 <span class="sr-only">(current)</span></a></li>
				        <li><a href="#">2</a></li>
				        <li><a href="#">3</a></li>
				        <li><a href="#">4</a></li>
				        <li><a href="#">5</a></li>
				        <li><a href="#" aria-label="Next"><span aria-hidden="true">»</span></a></li>
				     </ul>
				 </nav>
				<hr>

			</div>
			
			<div id="map-canvas" class="col-xs-4">
				<!--map-canvas will be postioned here-->
			</div>
			<%--
				 <div class="col-xs-4 open" id="sidebar"  style="height:400px;border:2px solid blue">
				 	Hey There, I'm a 1/4 width Div
				 </div>
				  --%>
		</div>
	</div>