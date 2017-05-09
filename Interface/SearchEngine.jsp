<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Engine Project</title>
<script src="http://code.jquery.com/jquery-latest.js">   
</script>
<style>
body {text-align: center; }

</style>
<script>
            $(document).ready(function() {                        
                $('#SearchBox').on("keyup input", function(event) {  
                    var searchbox=$('#SearchBox').val();
					if(searchbox == ""){
						 $('#Suggestions').html(""); 
						document.getElementById("Suggestions").style.border="0px";
					}else{
                 $.get('ActionServlet',{SearchBox:searchbox},function(responseText) { 
                        $('#Suggestions').html(responseText);   
					if(responseText != ""){
					document.getElementById("Suggestions").style.border="1px solid #A5ACB2";
					}  else{

						document.getElementById("Suggestions").style.border="0px";
					} 
                    });
					}
                });
            });
</script>
</head>

<body bgcolor="#f0f0f0">
<form action="SearchForm" id="SearchForm">
	<br>	<br>	<br>	<br>	<br>	<br>	<br>	<br>		
	<br>	<br>	<br>	<br>	<br>	<br>	<br>	<br>	
	<h1 align="center"> Search Engine </h1>
	<input type="text" id="SearchBox" name="SearchBox" autocomplete=off size ="100" placeholder="Type a keyword to search for ..." />
	<input type="submit" value="Search" />
	<div id="Suggestions"></div>
	<br>
</form>
</body>
</html>