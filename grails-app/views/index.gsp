<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to Grails</title>
	</head>
	<body>
    <h1>CCAvenue</h1>
    <g:link controller="demo" action="iframe"> IFrame</g:link> <br/>
    <g:link controller="demo" action="nonSeamless"> Non Seamless</g:link> <br/>
    <g:link controller="demo" action="customCheckoutForm"> Custom Checkout</g:link> <br/>
    <hr/>

    <h1>Payu</h1>
    <g:link controller="payu" action="nonSeamless"> Non Seamless</g:link> <br/>
    <g:link controller="payu" action="seamless"> Seamless</g:link> <br/>
    <g:link controller="payu" action="makeApiCall"> Payu Api</g:link> <br/>
    </body>
</html>
