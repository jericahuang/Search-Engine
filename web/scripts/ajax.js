/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var request1;
var request2;

$(document).ready(function(){
    request1 = null;
    request2 = null;
    
    $("body").on("click","#searchButton",function(){
        if (request1){ request1.abort(); };
        if (request2){ request2.abort(); };
    
        var query = $("#searchInput").val();
        console.log(query);

        var dataToSend = {q: query};
        
        //Parallel AJAX requests
        $.when(
            
            //Search
            request1 = $.ajax({
                url:"performSearch",
                type: "post",
                data: dataToSend,
                dataType: "json"
            })
            .always(function(){
                jQuery("body").html(request1.responseText);
                request1 = null;
            })
            ,
          
            //Q and A
            request2 = $.ajax({
                url:"QandA",
                type: "post",
                data: dataToSend,
                dataType: "json"
            })
            .always(function(){
                var str = request2.responseText;
                if (str != null && str != "<error>"){
                    $("<style>").text("div#hooli { display:block; }").appendTo("body");        
                    document.getElementById("hooli").innerHTML = str;
                }
                else{
                    $("<style>").text("div#hooli { display:none; }").appendTo("body");        
                    document.getElementById("hooli").innerHTML = "";
                }
                request2 = null;
            })
        ).then(function() {
            console.log("done");
        });
        
    });

    $(document).keypress(function(e) {
    if(e.which == 13) {
        event.preventDefault();
        
        if (request1){ request1.abort(); };
        if (request2){ request2.abort(); };
    
        var query = $("#searchInput").val();
        console.log(query);

        var dataToSend = {q: query};
        
        //Parallel AJAX requests
        $.when(
            
            //Search
            request1 = $.ajax({
                url:"performSearch",
                type: "post",
                data: dataToSend,
                dataType: "json"
            })
            .always(function(){
                jQuery("body").html(request1.responseText);
                request1 = null;
            })
            ,
          
            //Q and A
            request2 = $.ajax({
                url:"QandA",
                type: "post",
                data: dataToSend,
                dataType: "json"
            })
            .always(function(){
                var str = request2.responseText;
                if (str != null && str != "<error>"){
                    $("<style>").text("div#hooli { display:block; }").appendTo("body");        
                    document.getElementById("hooli").innerHTML = str;
                }
                else{
                    $("<style>").text("div#hooli { display:none; }").appendTo("body");        
                    document.getElementById("hooli").innerHTML = "";
                }
                request2 = null;
            })
        ).then(function() {
            
        });
    }
});
});
