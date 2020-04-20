function insert(num){
    document.form.textview.value = document.form.textview.value + num;
    try {
        exp = document.form.textview.value;
        eval(exp);
        document.getElementById("equal").disabled = false; 
     }
    catch (e) {
        console.log("Erro invalid expression:"+e); // passa o objeto de exceção para o manipulador de erro
        document.getElementById("equal").disabled = true; 
    }
    
}

function result(res){
    document.form.textview.value = res;
    console.log("EXECUTOU");
}

function equal(){
    exp = document.form.textview.value;
    try {
       x = eval(exp);
       //document.form.textview.value = x;
       document.getElementById("equal").disabled = false;
       msg = encodeURI("calculo?expr="+exp);
       
       httpGetAsync(msg, result, exp);
     }
    catch (e) {
        console.log("Erro invalid expression:"+e); // passa o objeto de exceção para o manipulador de erro
        document.getElementById("equal").disabled = true; 
    }
}
function c(){
    document.form.textview.value = "";
}
function back(){
    var exp = document.form.textview.value;
    document.form.textview.value = exp.substring(0, exp.length-1);
}

function httpGetAsync(theUrl, callback, exp)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.timeout = 16000;
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
        {
            var respo = xmlHttp.responseText;
            callback(respo);
            //document.getElementById("res").;
            var element = document.createElement("p");
            element.appendChild(document.createTextNode("expressão["+exp+"] -> resultado["+respo+"]"));
            document.getElementById('reg').appendChild(element);
        }
    };
    
    xmlHttp.ontimeout = function (e) {
    // XMLHttpRequest timed out. Do something here.
    var element = document.createElement("p");
    element.appendChild(document.createTextNode("timeout - expressão["+exp+"]"));
    document.getElementById('reg').appendChild(element);
    };

    xmlHttp.open("POST", theUrl, true); // true for asynchronous 
    xmlHttp.send(null);
}
