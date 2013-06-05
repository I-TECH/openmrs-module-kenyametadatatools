<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<b class="boxHeader">Synchronize with remote spreadsheet</b>
<form method="post" class="box">
	<p>Spreadsheet URL:
		<input name="spreadsheetUrl" value="${spreadsheetUrl}" style="width: 400px" />
		<c:if test="${spreasheetUrlError}">
			<span class="error"><c:out value="${spreasheetUrlError}" /></span>
		</c:if>
	</p>

	<input type="submit" value="Start" />
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>