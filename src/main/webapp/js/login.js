function log_in()
{
	var username = document.getElementById("username").value;
	var password = document.getElementById("pwd").value;

	if (!username || !password)
	{
		alert("Please fill out all fields.")
	}
	
	let baseURL = window.location.origin + "/Project201/";
    var url = new URL("LoginServlet", baseURL);
    
    fetch(url,
	{
		method: 'POST',
		header: {'Content-Type': 'application/json'},
		body: JSON.stringify
		({
			username: username,
			password: password,
		})
	})
	.then(response => response.json()) .then((data) => 
	{
	
		if (data == "Bad login")
		{
			alert("Account doesn't exist.");
		}
		else
		{
			alert("Successfully logged in");
			window.location.replace("http://localhost:8080/Project201/main.html")
			localStorage.setItem("username", username);
		}
		
		document.getElementById("username2").value = "";
		document.getElementById("email").value = "";
		document.getElementById("pwd2").value = "";
		
		document.getElementById("username").value = "";
		document.getElementById("pwd").value = "";
	})
	return false;
}

function sign_up()
{
	var username = document.getElementById("username2").value;
	var email = document.getElementById("email").value;
	var password = document.getElementById("pwd2").value;
	
	if (!username || !email || !password)
	{
		alert("Please fill out all fields.")
		return false;
	}
	
	let baseURL = window.location.origin + "/Project201/";
    var url = new URL("RegisterServlet", baseURL);
	fetch(url,
	{
		method: 'POST',
		header: {'Content-Type': 'application/json'},
		body: JSON.stringify
		({
			username: username,
			email: email,
			password: password
		})
	})
	.then(response => response.json()) .then((data) => 
	{
		if (data == "Email taken")
		{
			alert("Email is already taken.");
		}
		else if (data == "Username taken")
		{
			alert("Username is already taken");
		}
		else
		{
			alert("Successfully signed up.");	
			window.location.replace("http://localhost:8080/Project201/main.html")
			localStorage.setItem("username", username);
		}
		
		document.getElementById("username2").value = "";
		document.getElementById("email").value = "";
		document.getElementById("pwd2").value = "";
		
		document.getElementById("username").value = "";
		document.getElementById("pwd").value = "";
	})
	return false;
}
