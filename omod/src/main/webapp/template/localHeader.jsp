<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short" /></a></li>

	<li <c:if test='<%= request.getRequestURI().contains("/synchronize") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/kenyamflsync/synchronize.form"><spring:message code="kenyamflsync.synchronize" /></a>
	</li>

</ul>
<h2>
	<spring:message code="kenyamflsync.title" />
</h2>
