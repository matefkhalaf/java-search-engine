<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<style>
.center {
    text-align: center;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Engine Project</title>
<script src="http://code.jquery.com/jquery-latest.js">   
</script>
<style>
IMG.blockcenter {
    display: block;
    margin-left: auto;
    margin-right: auto;
}
div.background
{
background-color: yellow;
}
body {
    background-image: url("https://wallpaperbrowse.com/media/images/background-2.jpg");
}
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
					document.getElementById("Suggestions").style.width="49.5%";
					document.getElementById("Suggestions").style.margin="0px auto 0px auto";
					document.getElementById("Suggestions").style.padding="0px 9.1% 0px 0px";
					}  else{
						document.getElementById("Suggestions").style.border="0px";
					} 
                    });
					}
                });
            });
function validateForm() {
    var x = document.forms["SearchForm"]["SearchBox"].value;
    x.replace(/^\s+/g, '');
    if (isEmpty(x)) {
        alert("You didn't enter a value to search for");
        return false;
    }
}
function isEmpty(str){
    return !str.replace(/^\s+/g, '').length; // boolean (`true` if field is empty)
}
</script>
</head>

<body bgcolor="#f0f0f0">

<form action="SearchForm" id="SearchForm" onsubmit="return validateForm()" method="post">
	<br>	<br>	<br>	<br>	<br>	<br>	<br>	<br>		
	<br>	<br>	<br>	<br>	<br>	<br>	<br>	<br>	
 <IMG class="blockcenter" src="http://zeus.cooltext.com/images/f77/f77f058bb16ed49ddd5e83aaaaa975033e713f8f.png" alt="HTML5 Icon" >
	<div class="center" id="dSB"><input type="text" id="SearchBox" name="SearchBox" autocomplete=off size ="100" placeholder="Type a keyword to search for ..." />
	<input type="submit" value="Search" ></div>
	<div class="background" id="Suggestions"></div>
	<br>
</form>
</body>
</html>